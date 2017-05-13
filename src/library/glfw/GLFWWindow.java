package library.glfw;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import com.esotericsoftware.minlog.Log;

public class GLFWWindow {
	
	private long window;

	public void createFullscreen(GLFWMonitor monitor, String title) {
		createFullscreen(monitor, title, new Vector2i(monitor.getWidth(), monitor.getHeight()));
	}
	
	public void createFullscreen(GLFWMonitor monitor, String title, Vector2i size) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit())
			Log.error("Failed to create display", new IllegalStateException("Invalid GLFW Context"));
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		window = GLFW.glfwCreateWindow(size.x, size.y, title, monitor.getPointer(), MemoryUtil.NULL);
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		if (!GL.getCapabilities().OpenGL33) {
			throw new IllegalStateException("This application requires OpenGL 3.3 or newer!");
		}
		GL11.glViewport(0, 0, size.x, size.y);
	}

	public void createWindowed(Vector2i size, String title) {
		createWindowed(size, title, false);
	}

	public void createWindowed(Vector2i size, String title, boolean resizable) {
		GLFWMonitor monitor = new GLFWMonitor(GLFW.glfwGetPrimaryMonitor());
		createWindowed(size, title, 
				new Vector2i(monitor.getWidth() / 2, monitor.getHeight() / 2).sub(size.x / 2, size.y / 2), resizable, true);
	}

	public void createWindowed(Vector2i size, String title, boolean resizable, boolean vSync) {
		GLFWMonitor monitor = new GLFWMonitor(GLFW.glfwGetPrimaryMonitor());
		createWindowed(size, title, 
				new Vector2i(monitor.getWidth() / 2, monitor.getHeight() / 2).sub(size.x / 2, size.y / 2), resizable, vSync);
	}

	public void createWindowed(Vector2i size, String title, Vector2i position, boolean resizable, boolean vSync) {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit())
			Log.error("Failed to create display", new IllegalStateException("Invalid GLFW Context"));
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		window = GLFW.glfwCreateWindow(size.x, size.y, title, MemoryUtil.NULL, MemoryUtil.NULL);
		GLFW.glfwSetWindowPos(window, position.x, position.y);
		GLFW.glfwSwapInterval(vSync ? 1 : 0);
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		if (!GL.getCapabilities().OpenGL33) {
			throw new IllegalStateException("This application requires OpenGL 3.3 or newer!");
		}
		GL11.glViewport(0, 0, size.x, size.y);
	}
	
	public void setIcon(GLFWIcon... icons) {
		if (icons.length > 0) {
			GLFWImage.Buffer buffer = GLFWImage.calloc(icons.length);
			for (int i = 0; i < icons.length; i++) {
				buffer.put(i, icons[i].getImage());
			}
			GLFW.glfwSetWindowIcon(window, buffer);
		}
	}
	
	public void setTitle(String title) {
		GLFW.glfwSetWindowTitle(window, title);
	}
	
	public void showWindow() {
		GLFW.glfwShowWindow(window);
	}
	
	public boolean valid() {
		return !GLFW.glfwWindowShouldClose(window);
	}
	
	public void update() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
	}

	public void destroy() {
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
	}
	
	public long getPointer() {
		return window;
	}

}
