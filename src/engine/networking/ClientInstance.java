package engine.networking;

import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

public class ClientInstance {
	
	public static int getUID(SocketChannel channel) {
		Socket socket = channel.socket();
		return socket.getInetAddress().getHostAddress().hashCode() * 100_000 + socket.getPort();		
	}

	public static int getUID(DatagramChannel channel) {
		DatagramSocket socket = channel.socket();
		return socket.getInetAddress().getHostAddress().hashCode() * 100_000 + socket.getPort();
	}

	private int uid;
	
	public ClientInstance(int uid) {
		this.uid = uid;
	}
	
	public int getUID() {
		return uid;
	}

}
