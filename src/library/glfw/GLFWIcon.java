package library.glfw;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWImage;

public class GLFWIcon {
	
	private GLFWImage image;
	
	public GLFWIcon() {
		image = GLFWImage.create();
	}
	
	public void image(ByteBuffer data, int width, int height) {
		image.set(width, height, data);
	}
	
	public GLFWImage getImage() {
		return image;
	}

}
