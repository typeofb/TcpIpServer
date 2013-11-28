import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;


public class DcVO {
	private int dcId;
	private InetAddress inetAddress;
	private int port;
	private DataInputStream dis;
	private DataOutputStream dos;
	private int ackCheck;
	
	public int getDcId() {
		return dcId;
	}
	public void setDcId(int dcId) {
		this.dcId = dcId;
	}
	public InetAddress getInetAddress() {
		return inetAddress;
	}
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public DataInputStream getDis() {
		return dis;
	}
	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}
	public DataOutputStream getDos() {
		return dos;
	}
	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	public int getAckCheck() {
		return ackCheck;
	}
	public void setAckCheck(int ackCheck) {
		this.ackCheck = ackCheck;
	}
}
