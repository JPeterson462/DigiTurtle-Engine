package library.font;

import java.io.InputStream;

import utils.RelativeStreamGenerator;

public interface FontImporter {

	public String[] getExtensions();
	
	public Font importFont(InputStream stream, float fontSize, RelativeStreamGenerator generator);

}
