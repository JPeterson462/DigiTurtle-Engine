package library.models.collada;

public class ColladaAnimation {
	
	private final float lengthSeconds;
	
	private final ColladaKeyFrameData[] keyFrames;

	public ColladaAnimation(float lengthSeconds, ColladaKeyFrameData[] keyFrames) {
		this.lengthSeconds = lengthSeconds;
		this.keyFrames = keyFrames;
	}

	public float getLengthSeconds() {
		return lengthSeconds;
	}

	public ColladaKeyFrameData[] getKeyFrames() {
		return keyFrames;
	}

}
