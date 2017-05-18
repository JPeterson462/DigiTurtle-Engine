package utils;

import java.io.InputStream;

@FunctionalInterface
public interface RelativeStreamGenerator {

	public InputStream getRelativeStream(String path);
	
}
