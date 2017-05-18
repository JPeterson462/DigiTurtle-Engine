package engine.sound;

public interface Music {
	
	public boolean isPlaying();
	
	public boolean play();
	
	public boolean update();
	
	public void stop();
	
	public void delete();
	
	public void setLooping(boolean looping);
	
	public float getTime();
	
	public float getLength();

}
