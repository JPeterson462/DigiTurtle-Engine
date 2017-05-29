package engine.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

public class TCPSimplexProtocolImplementation implements ProtocolImplementation {
	
	private Endpoint endpoint;

	private SocketChannel socketChannel;

	private ByteBuffer intW = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN), intR = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
	
	@Override
	public void connect(String ip, int port, Endpoint endpoint) throws IOException {
		this.endpoint = endpoint;
		socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(ip, port));
		intW.limit(intW.capacity());
		intR.limit(intR.capacity());
		while (!socketChannel.isConnected()) {
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
		while (intW.hasRemaining()) {
			socketChannel.write(intW);
		}
		intW.position(0);
		intW.putInt(0, endpoint.getRegistry().lookup(object.getClass()));
		while (intW.hasRemaining()) {
			socketChannel.write(intW);
		}
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
	}

	@Override
	public Object read() throws IOException {
		int length = readBlocking();
		int id = readBlocking();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		int read = 0;
		buffer.limit(length);
		while (read < length) {
			buffer.position(read);
			int perCall = socketChannel.read(buffer);
			if (perCall < 0) {
				break;
			}
			read += perCall;
		}
		buffer.limit(buffer.position());
		return endpoint.getDecoder(id).decode(buffer);
	}
	
	private int readBlocking() throws IOException {
		intR.position(0);
		int bytesRead = socketChannel.read(intR);
		while (bytesRead < 4) {
			intR.position(bytesRead);
			bytesRead += socketChannel.read(intR);
		}
		return intR.getInt(0);
	}

	@Override
	public void disconnect() throws IOException {
		socketChannel.close();
	}

}
