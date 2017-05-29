package engine.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client extends Endpoint {
	
	private ProtocolImplementation protocolImplementation;
	
	private String ip;
	
	private int port;
	
	private Thread thread;
	
	private AtomicBoolean listening = new AtomicBoolean();
	
	private ArrayList<EndpointListener> listeners = new ArrayList<>();
	
	public Client(Protocol protocol, String ip, int port) {
		this.ip = ip;
		this.port = port;
		switch (protocol) {
			case TCP:
				protocolImplementation = new TCPSimplexProtocolImplementation();
				break;
			case UDP:
				protocolImplementation = new UDPSimplexProtocolImplementation();
				break;
		}
	}
	
	public Client listen(EndpointListener listener) {
		listeners.add(listener);
		return this;
	}
	
	public void start() {
		thread = new Thread(() -> {
			try {
				protocolImplementation.connect(ip, port, Client.this);System.out.println("c-connected");
				listening.set(true);
				while (listening.get()) {
					Object object = protocolImplementation.read();System.out.println("c-read");
					if (object == null) {
						continue;
					}
					for (int i = 0; i < listeners.size(); i++) {
						listeners.get(i).onPacketReceived(object, this, (o) -> {
							try {
								protocolImplementation.write(o);System.out.println("c-write");
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
		while (!listening.get()) {
			// WAIT
		}
		try {
			protocolImplementation.write(object);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
