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

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(3000);
			while (true) {
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "로부터 연결요청이 들어왔습니다.");
				dis = new DataInputStream(socket.getInputStream());

				// Web으로부터 예약제어
				byte[] sendResultArr = new byte[32];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);

				dos.writeByte(0x41);						// Command A
				dos.write(Common.intToTwoByteArray(3064));	// DC ID
				
				dos.write(Common.intToTwoByteArray(2013));	// 제어시작일시
				dos.write(Common.intToOneByteArray(11));
				dos.write(Common.intToOneByteArray(18));
				dos.write(Common.intToOneByteArray(17));
				dos.write(Common.intToOneByteArray(03));
				dos.write(Common.intToOneByteArray(24));
				
				dos.write(Common.intToTwoByteArray(2013));	// 제어종료일시
				dos.write(Common.intToOneByteArray(11));
				dos.write(Common.intToOneByteArray(18));
				dos.write(Common.intToOneByteArray(17));
				dos.write(Common.intToOneByteArray(33));
				dos.write(Common.intToOneByteArray(24));
				
				dos.writeByte(0x20);						// 제어종류코드
				dos.writeByte(10);							// 제어주기
				dos.writeByte(10);							// 제어복귀주기

				sendResultArr = baos.toByteArray();
				baos.close();
				dos.close();

				TcpIpServer.dcMap.get(3064).write(Common.makeBCC(sendResultArr));
				TcpIpServer.dcMap.get(3064).flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}