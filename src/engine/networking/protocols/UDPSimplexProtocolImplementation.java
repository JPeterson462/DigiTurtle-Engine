package engine.networking.protocols;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import engine.networking.Encoder;
import engine.networking.Endpoint;
import engine.networking.NetworkSettings;
import engine.networking.ProtocolImplementation;

public class UDPSimplexProtocolImplementation implements ProtocolImplementation {
	
	private Endpoint endpoint;

	private DatagramChannel datagramChannel;
	
	private Selector selector;

	private Iterator<SelectionKey> selectionKeyIterator;

	@Override
	public void connect(String ip, int port, Endpoint endpoint) throws IOException {
		this.endpoint = endpoint;
		datagramChannel = DatagramChannel.open();
		datagramChannel.configureBlocking(false);
		datagramChannel.socket().connect(new InetSocketAddress(ip, port));
		selector = Selector.open();
		datagramChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void write(T object) throws IOException {
		ByteBuffer buffer = ((Encoder<T>) endpoint.getEncoder(object.getClass())).encode(object);
		ByteBuffer udpBuffer = ByteBuffer.allocate(NetworkSettings.UDP_PACKET_SIZE);
		udpBuffer.putInt(endpoint.getRegistry().lookup(object.getClass()));
		udpBuffer.put(buffer);
		udpBuffer.limit(udpBuffer.capacity());
		udpBuffer.position(0);
		datagramChannel.write(udpBuffer);
	}

	@Override
	public Object read() throws IOException {
		if (selectionKeyIterator == null || !selectionKeyIterator.hasNext()) {
			selector.select();
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			selectionKeyIterator = selectionKeys.iterator();
		}
		SelectionKey selectionKey = selectionKeyIterator.next();
		if (selectionKey == null) {
			return null;
		}
		if (selectionKey.isReadable()) {
			ByteBuffer buffer = ByteBuffer.allocate(NetworkSettings.UDP_PACKET_SIZE);
			datagramChannel.read(buffer);
			buffer.flip();
			return endpoint.getDecoder(buffer.getInt()).decode(buffer);
		}
		return null;
	}

	@Override
	public void disconnect() throws IOException {
		datagramChannel.disconnect();
	}

}
