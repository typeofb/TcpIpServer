import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DemandControl implements Runnable {

	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	TcpIpServer tcpIpServer;

	public DemandControl(TcpIpServer tcpIpServer) {
		this.tcpIpServer = tcpIpServer;
	}

	@Override
	public void run() {
		try {
			socket = tcpIpServer.socket;
			System.out.println(socket.getInetAddress() + ":" + socket.getPort() + "로부터 연결요청이 들어왔습니다.");
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			byte[] buffer = new byte[1024];
			while (dis.read(buffer, 0, buffer.length) != -1) {
				byte[] byteArray = Common.checkBCC(buffer);

				switch ((char) byteArray[0]) {
				case 'V':
					TcpIpServer.dcMap.put(Common.twoByteArrayToInt(byteArray, 1), dos);
					System.out.println(TcpIpServer.dcMap);
					presentMonitor(byteArray);
					break;
				case 'B':
					ackConfirm(byteArray, dos);
					break;
				case 'T':
					timeSynchronize(byteArray, dos);
					break;
				case 'A':
					System.out.println("예약제어");
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// DC -> Server
	private void presentMonitor(byte[] byteArray) {

		System.out.println("수용가 DC ID : " + Common.twoByteArrayToInt(byteArray, 1));
		System.out.println("현재전력 : " + Common.fourByteArrayToInt(byteArray, 3));
		System.out.println("기준전력 : " + Common.fourByteArrayToInt(byteArray, 7));
		System.out.println("예측전력 : " + Common.fourByteArrayToInt(byteArray, 11));
		System.out.println("목표전력 : " + Common.fourByteArrayToInt(byteArray, 15));
		System.out.println("수요시간 : " + Common.twoByteArrayToInt(byteArray, 19));
		System.out.println("경보상태 : " + Common.toHexString(byteArray[21]));
		System.out.println("Restart Flag : " + Common.toHexString(byteArray[22]));

		System.out.println("년월일 : " + Common.twoByteArrayToInt(byteArray, 23) + String.format("%02d", byteArray[25]) + String.format("%02d", byteArray[26]));
		System.out.println("시분초 : " + String.format("%02d", byteArray[27]) + String.format("%02d", byteArray[28]) + String.format("%02d", byteArray[29]));

		System.out.println("RCU부하수 : " + Common.oneByteArrayToInt(byteArray, 30));
		System.out.println("RCU부하상태 : " + Common.toHexString(byteArray[31]) + Common.toHexString(byteArray[32]) + Common.toHexString(byteArray[33]) + Common.toHexString(byteArray[34]));
		System.out.println("RCU통신상태 : " + Common.toHexString(byteArray[35]) + Common.toHexString(byteArray[36]) + Common.toHexString(byteArray[37]) + Common.toHexString(byteArray[38]));

		System.out.println("15분 수요전력 : " + Common.fourByteArrayToInt(byteArray, 39));
		System.out.println("현재사용전력 : " + Common.fourByteArrayToInt(byteArray, 43));
		System.out.println("누적사용전력량 : " + Common.fourByteArrayToInt(byteArray, 47));
		System.out.println("------------------------------");
	}

	// DC(Ack 신호 전송) -> Server -> DC
	private void ackConfirm(byte[] byteArray, DataOutputStream dos2) throws IOException {

		byte[] sendResultArr = new byte[8];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);

		out.write(byteArray[0]); // Command
		out.write(byteArray[1]); // DC ID
		out.write(byteArray[2]); // DC ID

		sendResultArr = Common.makeBCC(baos.toByteArray());
		dos2.write(sendResultArr);
		dos2.flush();

		out.close();
		baos.close();
	}

	// DC(시간동기) -> Server -> DC
	private void timeSynchronize(byte[] byteArray, DataOutputStream dos3) throws IOException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = format.format(cal.getTime());

		int year = Integer.parseInt(today.substring(0, 4));
		int month = Integer.parseInt(today.substring(4, 6));
		int day = Integer.parseInt(today.substring(6, 8));
		int hour = Integer.parseInt(today.substring(8, 10));
		int min = Integer.parseInt(today.substring(10, 12));
		int sec = Integer.parseInt(today.substring(12, 14));

		byte[] sendResultArr = new byte[20];

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);

		out.write(byteArray[0]); // Command
		out.write(byteArray[1]); // DC ID
		out.write(byteArray[2]); // DC ID
		out.write(Common.intToTwoByteArray(year));
		out.write(Common.intToTwoByteArray(month));
		out.write(Common.intToTwoByteArray(day));
		out.write(Common.intToTwoByteArray(hour));
		out.write(Common.intToTwoByteArray(min));
		out.write(Common.intToTwoByteArray(sec));

		sendResultArr = Common.makeBCC(baos.toByteArray());
		dos3.write(sendResultArr);
		dos3.flush();

		out.close();
		baos.close();
	}
}
