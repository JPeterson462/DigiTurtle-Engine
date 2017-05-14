package engine;

import org.joml.Matrix4f;

public interface Camera {
	
	public Matrix4f getProjectionMatrix();
	
	public Matrix4f getViewMatrix();
	
	public void update();
	
}
