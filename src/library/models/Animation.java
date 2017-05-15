package library.models;

public class Animation {

	private final float lengthSeconds;
	
	private final KeyFrameData[] keyFrames;

	public Animation(float lengthSeconds, KeyFrameData[] keyFrames) {
		this.lengthSeconds = lengthSeconds;
		this.keyFrames = keyFrames;
	}

	public float getLengthSeconds() {
		return lengthSeconds;
	}

	public KeyFrameData[] getKeyFrames() {
		return keyFrames;
	}
	
}
