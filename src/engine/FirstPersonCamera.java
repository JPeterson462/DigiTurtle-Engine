package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class FirstPersonCamera implements Camera {

	private Matrix4f projectionMatrix, viewMatrix;

	private float pitch, yaw;

	private Vector3f position;

	public FirstPersonCamera(CoreSettings coreSettings, GraphicsSettings graphicsSettings) {
		projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(graphicsSettings.fov), (float) coreSettings.width / (float) coreSettings.height, 
				graphicsSettings.near, graphicsSettings.far);
		viewMatrix = new Matrix4f();
		position = new Vector3f();
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

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}
	
	public void lookAt(Vector3f target) {
		Vector3f direction = new Vector3f(target).sub(position).normalize();
		pitch = (float) Math.asin(direction.y);
		yaw = (float) Math.atan2(direction.x, direction.z);
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = (float) Math.toRadians(pitch);
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = (float) Math.toRadians(yaw);
	}

	@Override
	public void update() {
		viewMatrix.identity();
		viewMatrix.rotateXYZ(pitch, yaw, 0).translate(-position.x, -position.y, -position.z);
	}

}
