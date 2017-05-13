package library.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class GLFWMonitor {
	
	private String name;
	
	private int width, height;
	
	private int refreshRate;
	
	private int redBits, greenBits, blueBits;
	
	private long pointer;
	
	public static GLFWMonitor getPrimaryMonitor() {
		return new GLFWMonitor(GLFW.glfwGetPrimaryMonitor());
	}
	
	public GLFWMonitor(long pointer) {
		this.pointer = pointer;
		name = GLFW.glfwGetMonitorName(pointer);
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(pointer);
		width = videoMode.width();
		height = videoMode.height();
		refreshRate = videoMode.refreshRate();
		redBits = videoMode.redBits();
		greenBits = videoMode.greenBits();
		blueBits = videoMode.blueBits();
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public int getRedBits() {
		return redBits;
	}

	public int getGreenBits() {
		return greenBits;
	}

	public int getBlueBits() {
		return blueBits;
	}

	public long getPointer() {
		return pointer;
	}
	
}
