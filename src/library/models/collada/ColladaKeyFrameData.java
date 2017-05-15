package library.models.collada;

import java.util.HashMap;

import org.joml.Matrix4f;

public class ColladaKeyFrameData {
	
	private final float time;
	
	private final HashMap<String, Matrix4f> jointTransforms = new HashMap<>();
	
	public ColladaKeyFrameData(float time) {
		this.time = time;
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
