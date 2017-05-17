package library.audio;

import java.io.InputStream;
import java.nio.ShortBuffer;

public interface AudioDecoder {
	
	public String[] getExtensions();
	
	public ShortBuffer decodeFully(InputStream stream, AudioData data);
	
	public AudioStream openStream(InputStream stream, AudioData data);

}
