package engine.world;

import org.joml.Vector3f;

public class AmbientLight implements Light {
	
	private Vector3f color;
	
	public AmbientLight(float r, float g, float b) {
		color = new Vector3f(r, g, b);
	}

	@Override
	public Vector3f getColor() {
		return color;
	}

}
