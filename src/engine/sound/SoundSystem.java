package engine.sound;

import java.nio.ShortBuffer;

import library.audio.AudioData;
import library.audio.AudioStream;

public interface SoundSystem {
	
	public void createContext();
	
	public SoundEffect createSoundEffect(ShortBuffer pcm, AudioData data);
	
	public Music createMusic(AudioStream stream, AudioData data);
	
	public Listener getListener();
	
	public void destroyContext();
	
	public void updateListener();
	
	public void checkError();
	
	public float getVolume();
	
	public void setVolume(float volume);
	
	public Microphone createMicrophone();
	
	public int getMicrophoneFrequency();

}
