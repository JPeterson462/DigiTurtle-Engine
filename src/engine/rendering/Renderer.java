package engine.rendering;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import engine.CoreSettings;
import engine.GraphicsSettings;

public interface Renderer {
	
	public void createContext(CoreSettings coreSettings, GraphicsSettings graphicsSettings, FreeFunction initFunction);
	
	public Geometry createGeometry(ArrayList<Vertex> vertices, int flags);

	public Geometry createGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags);

	public Texture createTexture(InputStream stream, boolean repeat);
	
	public Shader createShader(InputStream vertexStream, InputStream fragmentStream, HashMap<Integer, String> attributes);
	
	public Framebuffer createFramebuffer(int colorAttachments);

	public Framebuffer createFramebuffer(int width, int height, int colorAttachments);
	
	public void destroyContext(FreeFunction deinitFunction);
	
	public boolean validContext();
	
	public void prepareContext();
	
	public void updateContext();
	
	public int getFPS();

	public float getDeltaTime();
	
	public void enableAdditiveRendering();
	
	public void disableAdditiveRendering();
	
}
