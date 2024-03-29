package engine.world;

import org.joml.Vector3f;

public class SpotLight implements Light {

	private Vector3f position, direction, color;
	
	private float range, angle;
	
	public SpotLight(float r, float g, float b) {
		position = new Vector3f();
		direction = new Vector3f();
		color = new Vector3f(r, g, b);
		range = 1;
	}
	
	public float getRange() {
		return range;
	}
	
	public void setRange(float range) {
		this.range = range;
	}

	public float getAngle() {
		return angle;
	}
	
	public void setAngle(float radians) {
		angle = (float) Math.cos(radians);
	}
	
	public void setDirection(float x, float y, float z) {
		direction.set(x, y, z);
	}
	
	public void setDirection(Vector3f direction) {
		this.direction.set(direction);
	}
	
	public Vector3f getDirection() {
		return direction;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}
	
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	
	@Override
	public Vector3f getColor() {
		return color;
	}
	
	public void setColor(float r, float g, float b) {
		color.set(r, g, b);
	}
	
	public void setColor(Vector3f color) {
		this.color.set(color);
	}

}
