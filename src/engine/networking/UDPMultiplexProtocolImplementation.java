package engine.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class UDPMultiplexProtocolImplementation implements ProtocolImplementation, Multiplex {

	private Endpoint endpoint;

	private DatagramChannel serverDatagramChannel;
	
	private Selector selector;
	
	private Iterator<SelectionKey> selectionKeyIterator;
	
	private DatagramChannel clientChannel;

	private ByteBuffer intW = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN), intR = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
	
	private HashMap<Integer, ClientInstance> connectedInstances = new HashMap<>();
	
	private ClientInstance currentInstance;
	
	@Override
	public void connect(String ip, int port, Endpoint endpoint) throws IOException {
		this.endpoint = endpoint;
		selector = Selector.open();
		serverDatagramChannel = selector.provider().openDatagramChannel();
		serverDatagramChannel.socket().bind(new InetSocketAddress(port));
		serverDatagramChannel.configureBlocking(false);
		int ops = serverDatagramChannel.validOps();
		serverDatagramChannel.register(selector, ops);
		intW.limit(intW.capacity());
		intR.limit(intR.capacity());
		while (!serverDatagramChannel.isConnected()) {
			// WAIT
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void write(T object) throws IOException {
		ByteBuffer buffer = ((Encoder<T>) endpoint.getEncoder(object.getClass())).encode(object);
		intW.position(0);
		intW.putInt(0, buffer.limit());
		while (intW.hasRemaining()) {
			clientChannel.write(intW);
		}
		intW.position(0);
		intW.putInt(0, endpoint.getRegistry().lookup(object.getClass()));
		while (intW.hasRemaining()) {
			clientChannel.write(intW);
		}
		while (buffer.hasRemaining()) {
			clientChannel.write(buffer);
		}
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
		}System.out.println("selection_key");
		if (selectionKey.isReadable()) {
			clientChannel = (DatagramChannel) selectionKey.channel();
			int length = readBlocking();
			int id = readBlocking();
			ByteBuffer buffer = ByteBuffer.allocate(length);
			buffer.limit(length);
			clientChannel.receive(buffer);
			buffer.flip();
			if (!connectedInstances.containsKey(ClientInstance.getUID(clientChannel))) {
				connectedInstances.put(ClientInstance.getUID(clientChannel), new ClientInstance(ClientInstance.getUID(clientChannel)));
			}
			currentInstance = connectedInstances.get(ClientInstance.getUID(clientChannel));
			selectionKey.interestOps(SelectionKey.OP_WRITE);
			return endpoint.getDecoder(id).decode(buffer);
		}
		selectionKeyIterator.remove();
		return null;
	}

	private int readBlocking() throws IOException {
		intR.position(0);
		clientChannel.receive(intR);
		return intR.getInt(0);
	}

	@Override
	public void disconnect() throws IOException {
		serverDatagramChannel.close();
	}
	
	@Override
	public ClientInstance getCurrentInstance() {
		return currentInstance;
	}

}
