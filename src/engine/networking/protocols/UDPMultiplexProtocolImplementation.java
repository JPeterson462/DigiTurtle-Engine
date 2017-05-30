package engine.networking.protocols;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import engine.networking.ClientInstance;
import engine.networking.Decoder;
import engine.networking.Encoder;
import engine.networking.Endpoint;
import engine.networking.Multiplex;
import engine.networking.NetworkSettings;
import engine.networking.ProtocolImplementation;

public class UDPMultiplexProtocolImplementation implements ProtocolImplementation, Multiplex {

	private Endpoint endpoint;
	
	private DatagramChannel datagramChannel;

	private Selector selector;
	
	private Iterator<SelectionKey> selectionKeyIterator;

	private DatagramChannel clientChannel;

	private HashMap<Integer, ClientInstance> connectedInstances = new HashMap<>();
	
	private ClientInstance currentInstance;
	
	private SocketAddress clientAddress;
	
	private ByteBuffer response;
	
	@Override
	public ClientInstance getCurrentInstance() {
		return currentInstance;
	}

	@Override
	public void connect(String ip, int port, Endpoint endpoint) throws IOException {
		this.endpoint = endpoint;
		selector = Selector.open();
		datagramChannel = DatagramChannel.open();
		datagramChannel.configureBlocking(false);
		datagramChannel.socket().bind(new InetSocketAddress(port));
		datagramChannel.register(selector, SelectionKey.OP_READ);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void write(T object) throws IOException {
		ByteBuffer buffer = ((Encoder<T>) endpoint.getEncoder(object.getClass())).encode(object);
		response.putInt(endpoint.getRegistry().lookup(object.getClass()));
		response.put(buffer);
		response.limit(response.capacity());
		response.position(0);
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
			clientChannel = (DatagramChannel) selectionKey.channel();
			ByteBuffer buffer = ByteBuffer.allocate(NetworkSettings.UDP_PACKET_SIZE);
			clientAddress = clientChannel.receive(buffer);
			buffer.flip();
			int uid = ClientInstance.getUID((InetSocketAddress) clientAddress);
			if (!connectedInstances.containsKey(uid)) {
				connectedInstances.put(uid, new ClientInstance(uid));
			}
			currentInstance = connectedInstances.get(uid);
			if (clientAddress != null) {
				selectionKey.interestOps(SelectionKey.OP_WRITE);
				response = ByteBuffer.allocate(NetworkSettings.UDP_PACKET_SIZE);
			}
			Decoder<? extends Object> decoder = (Decoder<? extends Object>) endpoint.getDecoder(buffer.getInt());
			return decoder.decode(buffer);
		}
		else if (selectionKey.isValid() && selectionKey.isWritable()) {
			int bytesSent = clientChannel.send(response, clientAddress);
			if (bytesSent != 0) {
				selectionKey.interestOps(SelectionKey.OP_READ);
			}
		}
		selectionKeyIterator.remove();
		return null;
	}

	@Override
	public void disconnect() throws IOException {
		datagramChannel.disconnect();
	}

}
