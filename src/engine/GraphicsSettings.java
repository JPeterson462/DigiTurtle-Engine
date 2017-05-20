package engine;

public class GraphicsSettings {
	
	public float fov, near, far;
	
	public boolean anisotropicFiltering;
	
	public GraphicsSettings() {
		fov = 70;
		near = 0.1f;
		far = 1000f;
		anisotropicFiltering = false;
	}

}
