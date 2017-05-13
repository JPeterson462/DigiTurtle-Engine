package engine.rendering.opengl;

import java.io.InputStream;
import java.util.ArrayList;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.FreeFunction;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import library.glfw.GLFWMonitor;
import library.glfw.GLFWWindow;

public class GLRenderer implements Renderer {
	//TODO instanced geometry
	//TODO asset I/O

	private GLFWWindow window;
	
	private int width, height;

	@Override
	public void createContext(CoreSettings coreSettings, GraphicsSettings graphicsSettings, FreeFunction initFunction) {
		window = new GLFWWindow();
		if (coreSettings.fullscreen) {
			GLFWMonitor monitor = GLFWMonitor.getPrimaryMonitor();
			window.createFullscreen(monitor, coreSettings.title);
			width = monitor.getWidth();
			height = monitor.getHeight();
		} else {
			window.createWindowed(new Vector2i(coreSettings.width, coreSettings.height), coreSettings.title);
			width = coreSettings.width;
			height = coreSettings.height;
		}
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(coreSettings.backgroundColor.x, coreSettings.backgroundColor.y, coreSettings.backgroundColor.z, 0);
		initFunction.call();
		window.showWindow();
	}

	@Override
	public Geometry createGeometry(ArrayList<Vertex> vertices, int flags) {
		return new GLGeometry(vertices, null, flags);
	}

	@Override
	public Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags) {
		return new GLGeometry(vertices, indices, flags);
	}

	@Override
	public Texture createTexture(InputStream stream, boolean repeat) {
		return new GLTexture(stream, repeat);
	}

	@Override
	public Shader createShader(InputStream vertexStream, InputStream fragmentStream) {
		return new GLShader(vertexStream, fragmentStream);
	}
	
	@Override
	public void destroyContext(FreeFunction deinitFunction) {
		deinitFunction.call();
		window.destroy();
	}

	@Override
	public boolean validContext() {
		return window.valid();
	}

	@Override
	public void prepareContext() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void updateContext() {
		window.update();
	}

}
