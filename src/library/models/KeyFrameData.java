package library.models;

import java.util.HashMap;

import org.joml.Matrix4f;

public class KeyFrameData {

	private final float time;
	
	private final HashMap<String, Matrix4f> jointTransforms;
	
	public KeyFrameData(float time) {
		this.time = time;
		jointTransforms = new HashMap<>();
	}
	
	public KeyFrameData(float time, HashMap<String, Matrix4f> jointTransforms) {
		this.time = time;
		this.jointTransforms = jointTransforms;
	}
	
	public float getTime() {
		return time;
	}
	
	public HashMap<String, Matrix4f> getJointTransforms() {
		return jointTransforms;
	}
	
	public void addTransform(String name, Matrix4f transform) {
		jointTransforms.put(name, transform);
	}

}
