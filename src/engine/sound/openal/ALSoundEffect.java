package engine.sound.openal;

import java.nio.ShortBuffer;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

import engine.sound.SoundEffect;
import engine.sound.SoundSystem;
import library.audio.AudioData;
import library.openal.ALBuffer;
import library.openal.ALSource;

public class ALSoundEffect implements SoundEffect {
	
	private ALSource source;
	
	private ALBuffer buffer;
	
	private SoundSystem system;
	
	public ALSoundEffect(ShortBuffer pcm, AudioData data, SoundSystem system) {
		source = new ALSource();
		buffer = new ALBuffer();
		buffer.bufferData(data.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, pcm, data.sampleRate);
		source.attachBuffer(buffer);
		this.system = system;
	}

	@Override
	public void play(Vector3f position, Vector3f velocity) {
		source.setPosition(position);
		source.setVelocity(velocity);
		source.play();
	}

	@Override
	public void play() {
		source.setPosition(system.getListener().getPosition());
		source.setVelocity(system.getListener().getVelocity());
		source.play();
	}

	@Override
	public void delete() {
		buffer.delete();
		source.delete();
	}

}
