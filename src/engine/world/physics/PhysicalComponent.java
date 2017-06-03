package engine.world.physics;

import java.util.ArrayList;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.world.Component;
import engine.world.Entity;

public abstract class PhysicalComponent implements Component {
	
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
	
	public Bounds getBounds() {
		return bounds;
	}
	
	public float getMass() {
		return mass;
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
		orientation.set(entity.getOrientation());
		position.set(entity.getPosition());
		velocity.fma(delta, acceleration);
		position.fma(delta, velocity);
		angularVelocity.fma(delta, angularAcceleration);
		orientation.integrate(delta, angularVelocity.x, angularVelocity.y, angularVelocity.z);
		entity.setPosition(position);
		entity.setOrientation(orientation);
		ArrayList<Entity> collided = new ArrayList<>();
		entity.getWorld().forEachEntity((other) -> {
			PhysicalComponent otherPhysical = other.getComponent(PhysicalComponent.class);
			if (otherPhysical == null) {
				return;
			}
			if (other != entity) {
				if (getBounds().overlaps(otherPhysical.getBounds())) {
					collided.add(other);
				}
			}
		});
		for (int i = 0; i < collided.size(); i++) {
			collided.get(i).getComponent(PhysicalComponent.class).onCollision(entity);
			onCollision(collided.get(i));
		}
		Vector3f tmpVector = new Vector3f();
		for (int i = 0; i < collided.size(); i++) {
			Entity other = collided.get(i);
			PhysicalComponent physicalComponent = other.getComponent(PhysicalComponent.class);
			Vector3f overlap = getBounds().getOverlap(physicalComponent.getBounds()).negate(); // Overlap of A to B
			if (overlap != null) {
				if (mass == Float.MAX_VALUE) {
					if (physicalComponent.mass != Float.MAX_VALUE) {
						tmpVector.set(overlap).negate().add(other.getPosition());
						other.setPosition(tmpVector);
					}
				} else {
					if (physicalComponent.mass == Float.MAX_VALUE) {
						tmpVector.set(overlap).add(entity.getPosition());
						entity.setPosition(tmpVector);
					} else {
						float massRatio = mass / (mass + physicalComponent.mass);
						tmpVector.set(overlap).mul(massRatio).add(entity.getPosition());
						entity.setPosition(tmpVector);
						tmpVector.set(overlap).negate().mul(1f - massRatio).add(other.getPosition());
						other.setPosition(tmpVector);						
					}
				}
			}
		}
	}
	
	public abstract void onCollision(Entity other);

}
