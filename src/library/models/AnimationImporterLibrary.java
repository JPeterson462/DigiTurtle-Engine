package library.models;

import java.util.HashMap;
import java.util.Map;

public class AnimationImporterLibrary {

	private static HashMap<String[], AnimationImporter> importers = new HashMap<>();
	
	public static void registerImporter(AnimationImporter importer) {
		importers.put(importer.getExtensions(), importer);
	}
	
	public static AnimationImporter findImporter(String extension) {
		for (Map.Entry<String[], AnimationImporter> importer : importers.entrySet()) {
			if (contains(importer.getKey(), extension)) {
				return importer.getValue();
			}
		}
		for (Map.Entry<String[], AnimationImporter> importer : importers.entrySet()) {
			if (importer.getKey().length == 0) {
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
