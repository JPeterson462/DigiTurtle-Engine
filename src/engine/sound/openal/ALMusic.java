package engine.sound.openal;

import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import engine.sound.Music;
import library.audio.AudioData;
import library.audio.AudioStream;
import library.openal.ALBuffer;
import library.openal.ALSource;

public class ALMusic implements Music {
	
	private AudioStream stream;
	
	private AudioData data;

	private ALSource source;
	
	private ALBuffer[] buffers;
	
	private int samplesLeft;
	
	private ShortBuffer pcm = BufferUtils.createShortBuffer(16 * 1024);
	
	private boolean looping = false, playing = false;
	
	public ALMusic(AudioStream stream, AudioData data) {
		this.stream = stream;
		this.data = data;
		source = new ALSource();
		buffers = new ALBuffer[3];
		for (int i = 0; i < buffers.length; i++) {
			buffers[i] = new ALBuffer();
		}
	}
	
	private boolean stream(ALBuffer buffer) {
		int samples = stream.readSamples(pcm, data.channels);
		buffer.bufferData(data.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, pcm, data.sampleRate);
		if (samples == 0) {
			return false;
		}
		samplesLeft -= samples / data.channels;
		return true;
	}
	
	@Override
	public boolean isPlaying() {
		return samplesLeft == 0 || !playing;
	}

	@Override
	public boolean play() {
		playing = true;
		for (int i = 0; i < buffers.length; i++) {
			if (!stream(buffers[i])) {
				return false;
			}
		}
		samplesLeft = data.audioLengthSamples;
		source.queueBuffers(buffers);
		source.play();
		return true;		
	}

	@Override
	public boolean update() {
		if (!playing) {
			return false;
		}
		int processed = source.getBuffersProcessed();
		for (int i = 0; i < processed; i++) {
			ALBuffer buffer = source.unqueueBuffer();
			if (!stream(buffer)) {
				boolean shouldExit = true;
				if (looping) {
					stream.reset();
					shouldExit = !stream(buffer);
				}
				if (shouldExit) {
					return false;
				}
			}
			source.queueBuffer(buffer);
		}
		if (processed == buffers.length) {
			source.play();
		}
		return true;
	}

	@Override
	public void stop() {
		source.stop();
		playing = false;
	}

	@Override
	public void delete() {
		for (int i = 0; i < buffers.length; i++) {
			buffers[i].delete();
		}
		source.delete();
	}

	@Override
	public void setLooping(boolean looping) {
		this.looping = looping;
	}

}
