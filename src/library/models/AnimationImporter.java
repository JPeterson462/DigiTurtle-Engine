package library.models;

import java.io.InputStream;

public interface AnimationImporter {

	public Animation importAnimation(InputStream stream, String animation);
	
	public String[] getExtensions();

}
