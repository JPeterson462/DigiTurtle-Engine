package library.audio;

import java.nio.ShortBuffer;

public interface AudioStream {
	
	public void reset();
	
	public int getSampleOffset();
	
	public void seek(int sampleNumber);
	
	public int readSamples(ShortBuffer pcm, int channels);

}
