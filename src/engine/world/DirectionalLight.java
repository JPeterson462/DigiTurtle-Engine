package engine.world;

import org.joml.Vector3f;

public class DirectionalLight implements Light {

	private Vector3f direction, color;
	
	public DirectionalLight(float r, float g, float b) {
		color = new Vector3f(r, g, b);
		direction = new Vector3f(0, -1, 0);
	}
	
	public void setColor(float r, float g, float b) {
		color.set(r, g, b);
	}
	
	public void setColor(Vector3f color) {
		this.color.set(color);
	}
	
	@Override
	public Vector3f getColor() {
		return color;
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

}
