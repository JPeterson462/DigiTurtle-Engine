package library.models;

import java.io.InputStream;

public interface ModelImporter {
	
	public Model importModel(InputStream stream, String animation);
	
	public String[] getExtensions();

}
