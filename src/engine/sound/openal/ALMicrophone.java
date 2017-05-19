package engine.sound.openal;

import java.io.IOException;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL10;

import engine.sound.Microphone;
import engine.sound.MicrophoneDestination;
import library.openal.ALCaptureDevice;

public class ALMicrophone implements Microphone {
	
	private MicrophoneDestination destination = (pcm) -> {};
	
	private ALCaptureDevice captureDevice;
	
	public ALMicrophone() {
		captureDevice = new ALCaptureDevice(AL10.AL_FORMAT_STEREO16);
	}

	@Override
	public void setDestination(MicrophoneDestination stream) {
		destination = stream;
	}

	@Override
	public void poll() throws IOException {
		ShortBuffer buffer = captureDevice.sampleDevice();
		if (buffer != null) {
			destination.submit(buffer);
		}
	}

	@Override
	public void close() {
		captureDevice.close();
	}

}
