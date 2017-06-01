package engine.world.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.world.Component;
import engine.world.Entity;

public class PhysicalComponent implements Component {
	
	private Vector3f position, velocity, acceleration, angularVelocity, angularAcceleration;
	
	private Quaternionf orientation;
	
	private float mass;

	private Bounds bounds;
	
	public PhysicalComponent(Vector3f position, Quaternionf orientation, float mass, Bounds bounds) {
		this.position = new Vector3f(position);
		this.orientation = new Quaternionf(orientation);
		velocity = new Vector3f();
		acceleration = new Vector3f();
		angularVelocity = new Vector3f();
		angularAcceleration = new Vector3f();
		this.mass = mass;
		this.bounds = bounds;
		bounds.setAccessors(() -> position, () -> orientation);
	}
	
	public void setVelocity(Vector3f velocity, Vector3f angularVelocity) {
		this.velocity.set(velocity);
		this.angularVelocity.set(angularVelocity);
	}
	
	public void setAcceleration(Vector3f acceleration, Vector3f angularAcceleration) {
		this.acceleration.set(acceleration);
		this.angularAcceleration.set(angularAcceleration);
	}
	
	public void addAcceleration(Vector3f acceleration, Vector3f angularAcceleration) {
		this.acceleration.add(acceleration);
		this.angularAcceleration.add(angularAcceleration);
	}

	@Override
	public void update(Entity entity, float delta) {
		// collision detection TODO
		velocity.fma(delta, acceleration);
		position.fma(delta, velocity);
		angularVelocity.fma(delta, angularAcceleration);
		orientation.integrate(delta, angularVelocity.x, angularVelocity.y, angularVelocity.z);		
		entity.setPosition(position);
		entity.setOrientation(orientation);
	}

}
