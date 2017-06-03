package engine.world.physics;

import java.util.ArrayList;

import org.joml.Vector3f;

import engine.rendering.Vertex;
import engine.world.TerrainChunk;

public class NavigationMesh {
	
	private ArrayList<Triangle> faces;
	
	/** maxAngle is the angle from the up vector <br> 0 <= angle <= PI */
	public NavigationMesh(TerrainChunk chunk, float maxAngle, Vector3f up) {
		faces = new ArrayList<>();
		// start with full graph, remove faces that are too steep
		ArrayList<Vertex> vertices = chunk.getVertices();
		ArrayList<Integer> indices = chunk.getIndices();
		for (int i = 0; i < indices.size(); i += 3) {
			faces.add(new Triangle(new Vector3f(vertices.get(indices.get(i)).position()), 
					new Vector3f(vertices.get(indices.get(i + 1)).position()), new Vector3f(vertices.get(indices.get(i + 2)).position())));
		}
		maxAngle = -((float) (maxAngle / Math.PI) * 2 - 1); // 1 is straight up
		Vector3f normal = new Vector3f();
		for (int i = faces.size() - 1; i >= 0; i--) {
			normal.set(faces.get(i).getNormal()).normalize();
			float angle = up.dot(normal);
			if (angle > maxAngle) {
				faces.remove(i);
			}
		}
	}
	
	public Triangle getTriangle(float x, float z) {
		for (int i = 0; i < faces.size(); i++) {
			Triangle face = faces.get(i);
			if (face.contains(x, z)) {
				return face;
			}
		}
		return null;
	}

}
