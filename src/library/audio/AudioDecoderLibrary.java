package library.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioDecoderLibrary {
	
	private static HashMap<String[], AudioDecoder> decoders = new HashMap<>();
	
	public static void registerDecoder(AudioDecoder decoder) {
		decoders.put(decoder.getExtensions(), decoder);
	}
	
	public static AudioDecoder findDecoder(String extension) {
		for (Map.Entry<String[], AudioDecoder> decoder : decoders.entrySet()) {
			if (contains(decoder.getKey(), extension)) {
				return decoder.getValue();
			}
		}
		return null;
	}

	private static boolean contains(String[] set, String value) {
		for (int i = 0; i < set.length; i++) {
			if (set[i].equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}

}
