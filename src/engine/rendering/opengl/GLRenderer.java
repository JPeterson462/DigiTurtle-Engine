package engine.rendering.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import com.esotericsoftware.minlog.Log;

import engine.AssetInputStream;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.IOUtils;
import engine.rendering.BlendMode;
import engine.rendering.Framebuffer;
import engine.rendering.FreeFunction;
import engine.rendering.Geometry;
import engine.rendering.InstanceTemplate;
import engine.rendering.InstancedGeometry;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import library.glfw.GLFWIcon;
import library.glfw.GLFWMonitor;
import library.glfw.GLFWWindow;

public class GLRenderer implements Renderer {

	private GLFWWindow window;
	
	private int width, height;
	
	private long time;
	
	private float frames, fps;
	
	private CoreSettings coreSettings;
	
	private GraphicsSettings graphicsSettings;

	@Override
	public void createContext(CoreSettings coreSettings, GraphicsSettings graphicsSettings, FreeFunction initFunction) {
		this.coreSettings = coreSettings;
		this.graphicsSettings = graphicsSettings;
		GLFWWindow.initialize();
		window = new GLFWWindow();
		if (coreSettings.fullscreen) {
			GLFWMonitor monitor = GLFWMonitor.getPrimaryMonitor();
			window.createFullscreen(monitor, coreSettings.title, false);
			width = monitor.getWidth();
			height = monitor.getHeight();
		} else {
			window.createWindowed(new Vector2i(coreSettings.width, coreSettings.height), coreSettings.title, false, false);
			width = coreSettings.width;
			height = coreSettings.height;
		}
		window.setIcon(getIcons(coreSettings.windowIconPaths));
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
//		GL11.glEnable(GL13.GL_MULTISAMPLE);
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(coreSettings.backgroundColor.x, coreSettings.backgroundColor.y, coreSettings.backgroundColor.z, 0);
		initFunction.call();
		time = System.currentTimeMillis();
		frames = 0;
		fps = 0;
		window.showWindow();
	}
	
	private GLFWIcon[] getIcons(String[] paths) {
		GLFWIcon[] icons = new GLFWIcon[paths.length];
		for (int i = 0; i < icons.length; i++) {
			icons[i] = new GLFWIcon();
			ByteBuffer data = IOUtils.readBufferQuietly(new AssetInputStream(paths[i]));
			int[] width = {0}, height = {0}, components = {0};
			ByteBuffer pixels = STBImage.stbi_load_from_memory(data, width, height, components, 0);
			icons[i].image(pixels, width[0], height[0]);
		}
		return icons;
	}

	@Override
	public Geometry createGeometry(ArrayList<Vertex> vertices, int flags) {
		return new GLGeometry(vertices, null, flags);
	}

	@Override
	public Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags) {
		return new GLGeometry(vertices, indices, flags);
	}

	public <T extends InstanceTemplate> InstancedGeometry<T> createInstancedGeometry(ArrayList<Vertex> vertices, int flags, Class<T> type, int maxInstances) {
		return new GLInstancedGeometry<T>(vertices, null, flags, type, maxInstances);
	}

	public <T extends InstanceTemplate> InstancedGeometry<T> createInstancedGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags, Class<T> type, int maxInstances) {
		return new GLInstancedGeometry<T>(vertices, indices, flags, type, maxInstances);
	}

	@Override
	public Texture createTexture(InputStream stream, boolean repeat) {
		return new GLTexture(stream, repeat, graphicsSettings.anisotropicFiltering);
	}

	@Override
	public Texture createCubemap(InputStream right, InputStream left, InputStream top, InputStream bottom, InputStream back, InputStream front) {
		return new GLTexture(right, left, top, bottom, back, front);
	}
	
	@Override
	public Shader createShader(InputStream vertexStream, InputStream fragmentStream, HashMap<Integer, String> attributes) {
		return new GLShader(vertexStream, fragmentStream, attributes);
	}

	@Override
	public Framebuffer createFramebuffer(int colorAttachments) {
		return createFramebuffer(width, height, colorAttachments);
	}

	@Override
	public Framebuffer createFloatingPointFramebuffer(int colorAttachments) {
		return new GLFramebuffer(width, height, colorAttachments, this.width, this.height, GL11.GL_FLOAT);
	}

	@Override
	public Framebuffer createFramebuffer(int width, int height, int colorAttachments) {
		return new GLFramebuffer(width, height, colorAttachments, this.width, this.height, GL11.GL_UNSIGNED_BYTE);
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
		checkError(true);
		if (System.currentTimeMillis() - time > 1000) {
			if (coreSettings.showFPS) {
				window.setTitle(coreSettings.title + " (FPS: " + (int) frames + ")");
			}
			fps = frames;
			frames = 0;
			time = System.currentTimeMillis();
		}
		frames++;
	}
	
	public void checkError(boolean failOnErrors) {
		int error = GL11.glGetError();
		int errors = 0;
		while (error != GL11.GL_NO_ERROR) {
			Log.warn("OpenGL Error: " + error);
			errors++;
			error = GL11.glGetError();
		}
		if (errors > 0 && failOnErrors) {
			throw new IllegalStateException("Invalid OpenGL Context.");
		}
	}

	@Override
	public int getFPS() {
		return (int) fps;
	}

	@Override
	public float getDeltaTime() {
		if (fps == 0) {
			float millis = System.currentTimeMillis() - time; // 0-1000
			float millisPerFrame = millis / frames; // 0-1000
			float dt = millisPerFrame / 1000f; // 0-1
			if (Float.isFinite(dt)) {
				return dt;
			} else {
				return 0;
			}
		}
		return 1f / fps;
	}
	
	private BlendMode current = null;

	@Override
	public void setBlendMode(BlendMode mode) {
		if (current != null && current.equals(mode)) {
			return; // Ignore this call
		}
		switch (mode) {
			case ADDITIVE:
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				break;
			case DEFAULT:
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case OVERWRITE:
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
				break;
		}
		current = mode;
	}
	
}
