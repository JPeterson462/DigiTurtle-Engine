package library.models.importers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import com.esotericsoftware.minlog.Log;

import engine.rendering.Renderer;
import library.models.Mesh;
import library.models.Model;
import library.models.ModelImporter;
import library.models.ModelVertexQueue;

public class OBJModelImporter implements ModelImporter {

	private String[] EXTENSIONS = { "obj" };

	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

	@Override
	public Model importModel(InputStream stream, String animation, Renderer renderer) {
		if (animation != null) {
			Log.warn("OBJ Importer ignoring animation '" + animation + "'");
		}
		AtomicBoolean parsedFaces = new AtomicBoolean(false);
		ModelVertexQueue queue = new ModelVertexQueue();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			reader.lines().forEach(line -> {
				String[] parts = line.split(" ");
				if (!parsedFaces.get()) {
					if (line.startsWith("v ")) {
						float[] data = parseFloatData(parts, 1);
						queue.position(data[0], data[1], data[2]);
					}
					else if (line.startsWith("vt ")) {
						float[] data = parseFloatData(parts, 1);
						queue.textureCoord(data[0], data[1]);
					}
					else if (line.startsWith("vn ")) {
						float[] data = parseFloatData(parts, 1);
						queue.normal(data[0], data[1], data[2]);
					}
					else if (line.startsWith("f ")) {
						parsedFaces.set(true);
						int[] v0 = parseIntData(parts[1].split("/"), 0);
						int[] v1 = parseIntData(parts[2].split("/"), 0);
						int[] v2 = parseIntData(parts[3].split("/"), 0);
						queue.submit(v0, v1, v2);
					}
				} else {
					if (line.startsWith("f ")) {
						int[] v0 = parseIntData(parts[1].split("/"), 0);
						int[] v1 = parseIntData(parts[2].split("/"), 0);
						int[] v2 = parseIntData(parts[3].split("/"), 0);
						queue.submit(v0, v1, v2);
					}
				}
			});
		} catch (IOException e) {
			Log.error("Failed to import OBJ file. " + stream, e);
			return null;
		}
		return new Model(new Mesh(queue));
	}
	
	private float[] parseFloatData(String[] parts, int offset) {
		float[] data = new float[parts.length - offset];
		for (int i = offset; i < parts.length; i++) {
			data[i - offset] = Float.parseFloat(parts[i]);
		}
		return data;
	}
	
	private int[] parseIntData(String[] parts, int offset) {
		int[] data = new int[parts.length - offset];
		for (int i = offset; i < parts.length; i++) {
			data[i - offset] = Integer.parseInt(parts[i]);
		}
		return data;
	}

}
