package engine;

public class GraphicsSettings {
	
	public float fov, near, far;
	
	public boolean anisotropicFiltering;
	
	public float dofDistance, dofRange;
	
	public float hdrExposure;
	
	public GraphicsSettings() {
		fov = 70;
		near = 0.1f;
		far = 1000f;
		anisotropicFiltering = false;
		dofDistance = 0;
		dofRange = 32;
		hdrExposure = 1.0f;
	}

}
