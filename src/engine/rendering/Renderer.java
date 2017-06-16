package engine.rendering;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import engine.CoreSettings;
import engine.GraphicsSettings;
import utils.RelativeStreamGenerator;

public interface Renderer {
	
	public void createContext(CoreSettings coreSettings, GraphicsSettings graphicsSettings, FreeFunction initFunction);
	
	public Geometry createGeometry(ArrayList<Vertex> vertices, int flags);

	public Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags);
	
	public <T extends InstanceTemplate> InstancedGeometry<T> createInstancedGeometry(ArrayList<Vertex> vertices, int flags, Class<T> type, int maxInstances);

	public <T extends InstanceTemplate> InstancedGeometry<T> createInstancedGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags, Class<T> type, int maxInstances);

	public Texture createTexture(InputStream stream, boolean repeat);
	
	public Texture createCubemap(InputStream right, InputStream left, InputStream top, InputStream bottom, InputStream back, InputStream front);
	
	public Shader createShader(InputStream vertexStream, InputStream fragmentStream, HashMap<Integer, String> attributes, RelativeStreamGenerator generator);

	public Shader createShader(InputStream vertexStream, InputStream fragmentStream, HashMap<Integer, String> attributes, HashMap<String, String> replacements, RelativeStreamGenerator generator);
	
	public Framebuffer createFramebuffer(int colorAttachments);
	
	public Framebuffer createFloatingPointFramebuffer(int colorAttachments);

	public Framebuffer createFramebuffer(int width, int height, int colorAttachments);

	public Framebuffer createFloatingPointFramebuffer(int width, int height, int colorAttachments);

	public void destroyContext(FreeFunction deinitFunction);
	
	public boolean validContext();
	
	public void prepareContext();
	
	public void updateContext();
	
	public int getFPS();

	public float getDeltaTime();

	public void setBlendMode(BlendMode mode);
	
}
