package engine.networking.protocols;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import engine.networking.ClientInstance;
import engine.networking.Encoder;
import engine.networking.Endpoint;
import engine.networking.Multiplex;
import engine.networking.ProtocolImplementation;

public class TCPMultiplexProtocolImplementation implements ProtocolImplementation, Multiplex {

	private Endpoint endpoint;

	private ServerSocketChannel serverSocketChannel;
	
	private Selector selector;
	
	private Iterator<SelectionKey> selectionKeyIterator;
	
	private SocketChannel clientChannel;

	private ByteBuffer intW = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN), intR = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
	
	private HashMap<Integer, ClientInstance> connectedInstances = new HashMap<>();
	
	private ClientInstance currentInstance;
	
	@Override
	public void connect(String ip, int port, Endpoint endpoint) throws IOException {
		this.endpoint = endpoint;
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		selector = Selector.open();
		int ops = serverSocketChannel.validOps();
		serverSocketChannel.register(selector, ops);
		intW.limit(intW.capacity());
		intR.limit(intR.capacity());
		while (!serverSocketChannel.isOpen()) {
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
		}
		if (selectionKey.isAcceptable()) {
			SocketChannel clientChannel = serverSocketChannel.accept();
			if (clientChannel == null) {
				return null;
			}
			clientChannel.configureBlocking(false);
			clientChannel.register(selector, SelectionKey.OP_READ);
			int uid = ClientInstance.getUID(clientChannel);
			connectedInstances.put(uid, new ClientInstance(uid));
			return null;
		}
		else if (selectionKey.isReadable()) {
			clientChannel = (SocketChannel) selectionKey.channel();
			int length = readBlocking();
			int id = readBlocking();
			ByteBuffer buffer = ByteBuffer.allocate(length);
			int read = 0;
			buffer.limit(length);
			while (buffer.hasRemaining()) {
				buffer.position(read);
				int perCall = clientChannel.read(buffer);
				if (perCall < 0) {
					break;
				}
				read += perCall;
			}
			buffer.flip();
			currentInstance = connectedInstances.get(ClientInstance.getUID(clientChannel));
			return endpoint.getDecoder(id).decode(buffer);
		}
		selectionKeyIterator.remove();
		return null;
	}

	private int readBlocking() throws IOException {
		intR.position(0);
		int bytesRead = clientChannel.read(intR);
		while (bytesRead < 4) {
			intR.position(bytesRead);
			bytesRead += clientChannel.read(intR);
		}
		intR.flip();
		int value = intR.getInt(0);
		return value;
	}

	@Override
	public void disconnect() throws IOException {
		serverSocketChannel.close();
	}
	
	@Override
	public ClientInstance getCurrentInstance() {
		return currentInstance;
	}

}
