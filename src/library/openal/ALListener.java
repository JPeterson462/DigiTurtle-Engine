package library.openal;

import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class ALListener {

	private FloatBuffer vec3 = BufferUtils.createFloatBuffer(3), 
						vec3Pair = BufferUtils.createFloatBuffer(6);
	
	public ALListener() {
		vec3.limit(vec3.capacity());
		vec3Pair.limit(vec3Pair.capacity());
	}

	public void setPosition(float x, float y, float z) {
		vec3.put(0, x);
		vec3.put(1, y);
		vec3.put(2, z);
		AL10.alListenerfv(AL10.AL_POSITION, vec3);
	}
	
	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	
	public void setVelocity(float vx, float vy, float vz) {
		vec3.put(0, vx);
		vec3.put(1, vy);
		vec3.put(2, vz);
		AL10.alListenerfv(AL10.AL_VELOCITY, vec3);
	}
	
	public void setVelocity(Vector3f velocity) {
		setVelocity(velocity.x, velocity.y, velocity.z);
	}
	
	public void setOrientation(float dx, float dy, float dz, float ux, float uy, float uz) {
		vec3Pair.put(0, dx);
		vec3Pair.put(1, dy);
		vec3Pair.put(2, dz);
		vec3Pair.put(3, ux);
		vec3Pair.put(4, uy);
		vec3Pair.put(5, uz);
		AL10.alListenerfv(AL10.AL_ORIENTATION, vec3Pair);
	}
	
	public void setOrientation(float dx, float dy, float dz) {
		vec3Pair.put(0, dx);
		vec3Pair.put(1, dy);
		vec3Pair.put(2, dz);
		vec3Pair.put(3, 0);
		vec3Pair.put(4, 1);
		vec3Pair.put(5, 0);
		AL10.alListenerfv(AL10.AL_ORIENTATION, vec3Pair);
	}
	
	public void setOrientation(Vector3f direction) {
		if (direction.lengthSquared() > 1) {
			direction.normalize();
		}
		setOrientation(direction.x, direction.y, direction.z);
	}
	
	public void setOrientation(Vector3f direction, Vector3f up) {
		if (direction.lengthSquared() > 1) {
			direction.normalize();
		}
		if (up.lengthSquared() > 1) {
			up.normalize();
		}
		setOrientation(direction.x, direction.y, direction.z, up.x, up.y, up.z);
	}

}
