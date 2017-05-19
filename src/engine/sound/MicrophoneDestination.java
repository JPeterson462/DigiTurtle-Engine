package engine.sound;

import java.nio.ShortBuffer;

@FunctionalInterface
public interface MicrophoneDestination {

	public void submit(ShortBuffer pcm);
	
}
