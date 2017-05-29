package engine.networking;

import java.io.IOException;

public interface ProtocolImplementation {
	
	public void connect(String ip, int port, Endpoint endpoint) throws IOException;
	
	public <T> void write(T object) throws IOException;
	
	public Object read() throws IOException;
	
	public void disconnect() throws IOException;

}
