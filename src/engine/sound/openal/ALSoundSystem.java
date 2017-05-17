package engine.sound.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.system.MemoryUtil;

import com.esotericsoftware.minlog.Log;

import engine.sound.Listener;
import engine.sound.Music;
import engine.sound.SoundEffect;
import engine.sound.SoundSystem;
import library.audio.AudioData;
import library.audio.AudioStream;

public class ALSoundSystem implements SoundSystem {

	private Listener listener = new Listener();
	
	private long device, context;
	
	private float volume;
	
	@Override
	public void createContext() {
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Could not open audio device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Could not create audio context.");
		}
		EXTThreadLocalContext.alcSetThreadContext(context);
		AL.createCapabilities(deviceCaps);
		setVolume(0.6f);
		updateListener();
	}

	@Override
	public SoundEffect createSoundEffect(ShortBuffer pcm, AudioData data) {
		return new ALSoundEffect(pcm, data, this);
	}

	@Override
	public Music createMusic(AudioStream stream, AudioData data) {
		return new ALMusic(stream, data);
	}

	@Override
	public Listener getListener() {
		return listener;
	}

	@Override
	public void destroyContext() {
		EXTThreadLocalContext.alcSetThreadContext(MemoryUtil.NULL);
		ALC10.alcDestroyContext(context);
		ALC10.alcCloseDevice(device);
	}

	@Override
	public void updateListener() {
		AL10.alListenerfv(AL10.AL_POSITION, listener.getPositionBuffer());
		AL10.alListenerfv(AL10.AL_VELOCITY, listener.getVelocityBuffer());
		AL10.alListenerfv(AL10.AL_ORIENTATION, listener.getOrientationBuffer());
	}

	@Override
	public void checkError() {
		int error = AL10.alGetError();
		int errors = 0;
		while (error != AL10.AL_NO_ERROR) {
			Log.info("OpenAL Error: " + error);
			error = AL10.alGetError();
			errors++;
		}
		if (errors > 0) {
			throw new IllegalStateException();
		}
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public void setVolume(float volume) {
		volume = Math.min(1f, Math.max(0f, volume)); // clamp the volume
		this.volume = volume;
		AL10.alListenerf(AL10.AL_GAIN, volume);
	}
	
}
