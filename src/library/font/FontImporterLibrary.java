package library.font;

import java.util.HashMap;
import java.util.Map;

public class FontImporterLibrary {

	private static HashMap<String[], FontImporter> importers = new HashMap<>();
	
	public static void registerImporter(FontImporter importer) {
		importers.put(importer.getExtensions(), importer);
	}
	
	public static FontImporter findImporter(String extension) {
		for (Map.Entry<String[], FontImporter> importer : importers.entrySet()) {
			if (contains(importer.getKey(), extension)) {
				return importer.getValue();
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
