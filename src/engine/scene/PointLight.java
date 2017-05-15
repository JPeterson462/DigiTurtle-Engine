package engine.scene;

import org.joml.Vector3f;

public class PointLight implements Light {

	private Vector3f position, color;
	
	private float range;
	
	public PointLight(float r, float g, float b) {
		position = new Vector3f();
		color = new Vector3f(r, g, b);
		range = 1;
	}
	
	public float getRange() {
		return range;
	}
	
	public void setRange(float range) {
		this.range = range;
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
