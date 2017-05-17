package engine.sound;

import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Listener {
	
	private Vector3f position = new Vector3f(), velocity = new Vector3f(), orientation = new Vector3f(0, 0, -1);
	
	private FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(3), 
			velocityBuffer = BufferUtils.createFloatBuffer(3),
			orientationBuffer = BufferUtils.createFloatBuffer(6);
	
	public Listener() {
		positionBuffer.limit(positionBuffer.capacity());
		velocityBuffer.limit(velocityBuffer.capacity());
		orientationBuffer.limit(orientationBuffer.capacity());
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public void setVelocity(float x, float y, float z) {
		velocity.set(x, y, z);
	}

	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}

	public Vector3f getOrientation() {
		return orientation;
	}
	
	public void setOrientation(float x, float y, float z) {
		orientation.set(x, y, z);
		orientation.normalize();
	}

	public void setOrientation(Vector3f orientation) {
		this.orientation = orientation;
		this.orientation.normalize();
	}

	public FloatBuffer getPositionBuffer() {
		positionBuffer.put(0, position.x);
		positionBuffer.put(1, position.y);
		positionBuffer.put(2, position.z);
		return positionBuffer;
	}

	public FloatBuffer getVelocityBuffer() {
		velocityBuffer.put(0, velocity.x);
		velocityBuffer.put(1, velocity.y);
		velocityBuffer.put(2, velocity.z);
		return velocityBuffer;
	}

	public FloatBuffer getOrientationBuffer() {
		orientationBuffer.put(0, orientation.x);
		orientationBuffer.put(1, orientation.y);
		orientationBuffer.put(2, orientation.z);
		orientationBuffer.put(3, 0);
		orientationBuffer.put(4, 1);
		orientationBuffer.put(5, 0);
		return orientationBuffer;
	}

}
