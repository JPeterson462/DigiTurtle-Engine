package library.models;

import java.io.InputStream;

import engine.rendering.Renderer;
import utils.RelativeStreamGenerator;

public interface ModelImporter {
	
	public Model importModel(InputStream stream, String animation, Renderer renderer, RelativeStreamGenerator streamGenerator);
	
	public String[] getExtensions();

}
