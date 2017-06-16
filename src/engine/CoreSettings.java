package engine;

import org.joml.Vector3f;

import utils.RelativeStreamGenerator;

public class CoreSettings {

	public boolean fullscreen;
	
	public String title;
	
	public int width, height;
	
	public Vector3f backgroundColor;
	
	public boolean showFPS;
	
	public String[] windowIconPaths;
	
	public RelativeStreamGenerator shaderFinder = (path) -> getClass().getClassLoader().getResourceAsStream(path);
	
	public String maxShininess;
	
	public CoreSettings() {
		fullscreen = false;
		title = "Application";
		width = 1280;
		height = 720;
		backgroundColor = new Vector3f(0, 0, 0);
		showFPS = true;
		windowIconPaths = new String[] {
			"crate.png"
		};
		maxShininess = "255.0";
	}
	
}
