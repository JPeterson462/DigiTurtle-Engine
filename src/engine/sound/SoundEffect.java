package engine.sound;

import org.joml.Vector3f;

public interface SoundEffect {
	
	public void play(Vector3f position, Vector3f velocity);
	
	public void play();
	
	public void delete();

}
