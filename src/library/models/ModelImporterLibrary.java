package library.models;

import java.util.HashMap;
import java.util.Map;

public class ModelImporterLibrary {
	
	private static HashMap<String[], ModelImporter> importers = new HashMap<>();
	
	public static void registerImporter(ModelImporter importer) {
		importers.put(importer.getExtensions(), importer);
	}
	
	public static ModelImporter findImporter(String extension) {
		if (extension != null) {
			for (Map.Entry<String[], ModelImporter> importer : importers.entrySet()) {
				if (contains(importer.getKey(), extension)) {
					return importer.getValue();
				}
			}
		}
		for (Map.Entry<String[], ModelImporter> importer : importers.entrySet()) {
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
