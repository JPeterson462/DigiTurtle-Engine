package engine.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

public class UDPSimplexProtocolImplementation implements ProtocolImplementation {

	private Endpoint endpoint;

	private DatagramChannel datagramChannel;

	private ByteBuffer intW = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN), intR = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
	
	private SocketAddress remote;
	
	@Override
	public void connect(String ip, int port, Endpoint endpoint) throws IOException {
		this.endpoint = endpoint;
		datagramChannel = DatagramChannel.open();
		datagramChannel.connect(remote = new InetSocketAddress(ip, port));
		datagramChannel.configureBlocking(false);
		intW.limit(intW.capacity());
		intR.limit(intR.capacity());
		while (!datagramChannel.isConnected()) {
			// WAIT
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void write(T object) throws IOException {
		Encoder<T> encoder = (Encoder<T>) endpoint.getEncoder(object.getClass());
		ByteBuffer buffer = encoder.encode(object);
		intW.position(0);
		intW.putInt(0, buffer.limit());
		datagramChannel.send(intW, remote);
		intW.position(0);
		intW.putInt(0, endpoint.getRegistry().lookup(object.getClass()));
		datagramChannel.send(intW, remote);
		datagramChannel.send(buffer, remote);
	}

	@Override
	public Object read() throws IOException {System.out.println("start");
		int length = readBlocking();System.out.println("length");
		int id = readBlocking();System.out.println("id");
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.limit(length);
		datagramChannel.read(buffer);System.out.println("read");
		buffer.limit(buffer.position());
		return endpoint.getDecoder(id).decode(buffer);
	}
	
	private int readBlocking() throws IOException {
		intR.position(0);
		datagramChannel.receive(intR);System.out.println(intR);
		return intR.getInt(0);
	}

	@Override
	public void disconnect() throws IOException {
		datagramChannel.close();
	}

}
