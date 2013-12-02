import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Web implements Runnable {

	ServerSocket serverSocket;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	DcVO dcVO = new DcVO();

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(3000);
			while (true) {
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "웹으로부터 연결요청이 들어왔습니다.");
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				
				dcVO.setDcId(0);
				dcVO.setInetAddress(socket.getInetAddress());
				dcVO.setPort(socket.getPort());
				dcVO.setDis(dis);
				dcVO.setDos(dos);
				
				// key:0 Web DataOutputStream
				TcpIpServer.dcMap.put(dcVO.getDcId(), dcVO);

				byte[] buffer = new byte[1024];
				byte[] result = null;
				int leftBufferSize = 0;
				if (dis.available() > 0) {
					while ((leftBufferSize = dis.read(buffer, 0, buffer.length)) != -1) {
						result = new byte[leftBufferSize];
						for (int i = 0; i < result.length; i++) {
							result[i] = buffer[i];
						}
						
						switch ((char) result[0]) {
						case 'A':
							reserveControl(result); // 예약제어
							break;
						default:
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reserveControl(byte[] byteArray) throws IOException {
		
		// ② Server -> DC
		int mtrCtrlNo = Common.oneByteArrayToInt(byteArray, 1);	// 회차 번호
		int manyType = Common.oneByteArrayToInt(byteArray, 2);	// 1: 단일 전송, 2: 다수 전송
		int cpeDcId = Common.twoByteArrayToInt(byteArray, 3);	// DC ID
		System.out.println(mtrCtrlNo + manyType + cpeDcId);
		
		byte[] sendResultArr = new byte[32];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		dos.writeByte(0x41);									// Command A
		dos.write(Common.intToTwoByteArray(cpeDcId));			// DC ID
		
		dos.write(Common.intToTwoByteArray(2013));				// 제어시작일시
		dos.write(Common.intToOneByteArray(11));
		dos.write(Common.intToOneByteArray(25));
		dos.write(Common.intToOneByteArray(17));
		dos.write(Common.intToOneByteArray(03));
		dos.write(Common.intToOneByteArray(24));
		
		dos.write(Common.intToTwoByteArray(2013));				// 제어종료일시
		dos.write(Common.intToOneByteArray(11));
		dos.write(Common.intToOneByteArray(25));
		dos.write(Common.intToOneByteArray(17));
		dos.write(Common.intToOneByteArray(33));
		dos.write(Common.intToOneByteArray(24));
		
		dos.writeByte(0x20);									// 제어종류코드(0x20:전체제어)
		dos.writeByte(10);										// 제어주기(분)
		dos.writeByte(10);										// 제어복귀주기(분)

		sendResultArr = baos.toByteArray();
		baos.close();
		dos.close();
		
		DataOutputStream out = TcpIpServer.dcMap.get(cpeDcId).getDos();
		out.write(Common.makeBCC(sendResultArr));
		out.flush();
	}
}
