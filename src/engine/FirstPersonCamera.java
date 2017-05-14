package engine;

import org.joml.Matrix4f;

public class FirstPersonCamera implements Camera {
	
	private Matrix4f projectionMatrix, viewMatrix;
	
	private float pitch, yaw;
	
	public FirstPersonCamera(CoreSettings coreSettings, GraphicsSettings graphicsSettings) {
		projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(graphicsSettings.fov), (float) coreSettings.width / (float) coreSettings.height, 
				graphicsSettings.near, graphicsSettings.far);
		viewMatrix = new Matrix4f();
		pitch = 0;
		yaw = 0;
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	@Override
	public void update() {
		viewMatrix.identity().rotateXYZ((float) Math.toRadians(pitch), (float) Math.toRadians(yaw), 0);
	}

}
