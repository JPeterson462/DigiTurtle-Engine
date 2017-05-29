package engine.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Endpoint {

	private ProtocolImplementation protocolImplementation;
	
	private String ip;
	
	private int port;
	
	private Thread thread;
	
	private AtomicBoolean listening = new AtomicBoolean();
	
	private ArrayList<EndpointListener> listeners = new ArrayList<>();
	
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
	
	public Server listen(EndpointListener listener) {
		listeners.add(listener);
		return this;
	}
	
	public ClientInstance getCurrentInstance() {
		return ((Multiplex) protocolImplementation).getCurrentInstance();
	}
	
	public void start() {
		thread = new Thread(() -> {
			try {
				protocolImplementation.connect(ip, port, Server.this);System.out.println("s-connected");
				while (listening.get()) {
					Object object = protocolImplementation.read();System.out.println("s-read");
					if (object == null) {
						continue;
					}
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onPacketReceived(object, this, (o) -> {
							try {
								protocolImplementation.write(o);System.out.println("s-write");
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						});
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
