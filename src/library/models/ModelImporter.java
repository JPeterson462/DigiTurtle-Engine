package library.models;

import java.io.InputStream;

import engine.rendering.Renderer;

public interface ModelImporter {
	
	public Model importModel(InputStream stream, String animation, Renderer renderer);
	
	public String[] getExtensions();

}
