package engine.sound;

import java.io.IOException;

public interface Microphone {
	
	public void setDestination(MicrophoneDestination stream);
	
	public void poll() throws IOException;
	
	public void close();

}
