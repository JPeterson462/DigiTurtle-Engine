package engine.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import engine.networking.protocols.TCPMultiplexProtocolImplementation;
import engine.networking.protocols.UDPMultiplexProtocolImplementation;

public class Server extends Endpoint {

	private ProtocolImplementation protocolImplementation;
	
	private String ip;
	
	private int port;
	
	private Thread thread;
	
	private AtomicBoolean listening = new AtomicBoolean();
	
	private ArrayList<EndpointListener<?>> listeners = new ArrayList<>();
	private ArrayList<Class<?>> listenerFilters = new ArrayList<>();
	
	public Server(Protocol protocol, String ip, int port) {
		this.ip = ip;
		this.port = port;
		switch (protocol) {
			case TCP:
				protocolImplementation = new TCPMultiplexProtocolImplementation();
				break;
			case UDP:
				protocolImplementation = new UDPMultiplexProtocolImplementation();
				break;
		}
	}
	
	public <T> Server listen(Class<T> type, EndpointListener<T> listener) {
		listeners.add(listener);
		listenerFilters.add(type);
		return this;
	}
	
	public ClientInstance getCurrentInstance() {
		return ((Multiplex) protocolImplementation).getCurrentInstance();
	}

	@SuppressWarnings("unchecked")
	private <T> void sendObject(EndpointListener<?> listener, Class<T> type, Object object) {
		((EndpointListener<T>) listener).onPacketReceived(type.cast(object), this, (o) -> {
			try {
				protocolImplementation.write(o);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public void start() {
		thread = new Thread(() -> {
			try {
				protocolImplementation.connect(ip, port, Server.this);
				while (listening.get()) {
					Object object = protocolImplementation.read();
					if (object == null) {
						continue;
					}
					for (int i = 0; i < listeners.size(); i++) {
						Class<?> type = listenerFilters.get(i);
						if (type.isAssignableFrom(object.getClass())) {
							sendObject(listeners.get(i), type, object);
						}
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		listening.set(true);
		thread.start();
	}
	
	public void disconnect() throws IOException {
		listening.set(false);
		while (thread.isAlive()) {
			// Wait for the thread
		}
		protocolImplementation.disconnect();
	}

	@Override
	public void write(Object object) {
		while (thread == null) {
			// WAIT
		}
		try {
			protocolImplementation.write(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
