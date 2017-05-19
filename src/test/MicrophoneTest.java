package test;

import java.io.IOException;

import org.lwjgl.openal.AL10;

import engine.sound.Microphone;
import engine.sound.SoundSystem;
import engine.sound.openal.ALSoundSystem;
import library.openal.ALBuffer;
import library.openal.ALCaptureDevice;
import library.openal.ALSource;

public class MicrophoneTest {
	
	public static void main(String[] args) throws IOException {
		SoundSystem system = new ALSoundSystem();
		system.createContext();
		
		// Writing
		ALSource source = new ALSource();
		ALBuffer buffer = new ALBuffer();

		// Reading
		Microphone mic = system.createMicrophone();
		mic.setDestination((pcm) -> {
			System.out.println("Polling data");
			buffer.bufferData(AL10.AL_FORMAT_STEREO16, pcm, ALCaptureDevice.DEFAULT_FREQUENCY);
			source.attachBuffer(buffer);
			source.play();
		});
		
//		boolean loop = true;
//		while (loop) {
		while (true) {
			mic.poll();
			
			int error = AL10.alGetError();
			while (error != AL10.AL_NO_ERROR) {
				System.out.println("OpenAL Error: " + error);
				error = AL10.alGetError();
			}
			
		}
//		system.destroyContext();
	}

}
