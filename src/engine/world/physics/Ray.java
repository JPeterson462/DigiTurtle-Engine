package engine.world.physics;

import org.joml.Vector3f;

public class Ray {
	
	private Vector3f origin, direction;
	
	public Ray(Vector3f origin, Vector3f direction) {
		this.origin = origin;
		this.direction = direction.normalize();
	}
	
	public Vector3f getOrigin() {
		return origin;
	}
	
	public Vector3f getDirection() {
		return direction;
	}

}
