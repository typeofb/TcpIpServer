import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class TcpIpServer {

	private ServerSocket serverSocket;
	public Socket socket;
	public static Hashtable<Integer, DataOutputStream> dcMap;

	public TcpIpServer() {
		dcMap = new Hashtable<Integer, DataOutputStream>();
		try {
			serverSocket = new ServerSocket(5078);
			while (true) {
				socket = serverSocket.accept();
				Thread demandControl = new Thread(new DemandControl(this));
				demandControl.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Thread mainThread = new Thread(new MainThread());
		Thread webThread = new Thread(new Web());

		mainThread.start();
		webThread.start();
	}
}

class MainThread implements Runnable {

	@Override
	public void run() {
		new TcpIpServer();
	}
}