package engine.world;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Vertex;

public class Terrain {
	
	private TerrainGenerator generator; // use the generator so that instead of interpolating the heights you just do a lookup
	
	private ArrayList<Vertex> vertices = new ArrayList<>();
	
	private ArrayList<Integer> indices = new ArrayList<>();
	
	private ArrayList<Vector3f> normals = new ArrayList<>();
	
	private Geometry geometry;
	
	private int width;
	
	public Terrain(TerrainGenerator generator, int width, int height, float resolution, float x, float z) {
		this.width = width;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Vector3f position = new Vector3f(i * resolution + x, 0, j * resolution + z);
				position.y = generator.getHeightAt(position.x, position.z);
				Vector2f textureCoord = new Vector2f((float) i / (float) width, (float) j / (float) height);
				Vector3f normal = new Vector3f();
				vertices.add(new Vertex().position(position).textureCoord(textureCoord).normal(normal));
			}
		}
		for (int i = 0; i < width * height; i++) {
			normals.add(new Vector3f(0, 0, 0));
		}
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {
				addFaces(i, j);
			}
		}
		for (int i = 0 ; i < vertices.size(); i++) {
			vertices.get(i).normal(normals.get(i).normalize());
		}
	}
	
	private void addFaces(int i, int j) {
		Vector3f face0 = computeFaceNormal(i + 0, j + 0, i + 1, j + 0, i + 1, j + 1);
		Vector3f face1 = computeFaceNormal(i + 1, j + 1, i + 0, j + 1, i + 0, j + 0);
		indices.add(i + 0, j + 0);
		indices.add(i + 1, j + 0);
		indices.add(i + 1, j + 1);
		indices.add(i + 1, j + 1);
		indices.add(i + 0, j + 1);
		indices.add(i + 0, j + 0);
		normals.get((i + 0) * width + (j + 0)).add(face0);
		normals.get((i + 1) * width + (j + 0)).add(face0);
		normals.get((i + 1) * width + (j + 1)).add(face0);
		normals.get((i + 1) * width + (j + 1)).add(face1);
		normals.get((i + 0) * width + (j + 1)).add(face1);
		normals.get((i + 0) * width + (j + 0)).add(face1);
	}
	
	private Vertex getVertex(int x, int z) {
		return vertices.get(x * width + z);
	}
	
	private Vector3f computeFaceNormal(int x0, int z0, int x1, int z1, int x2, int z2) {
		Vector3f a = getVertex(x0, z0).position();
		Vector3f b = getVertex(x1, z1).position();
		Vector3f c = getVertex(x2, x2).position();
		Vector3f v0 = new Vector3f(), v1 = new Vector3f(), v2 = new Vector3f();
		b.sub(a, v0);
		c.sub(a, v1);
		v0.cross(v1, v2);
		v2.normalize();
		return v2;
	}
	
	public void create(Renderer renderer) {
		geometry = renderer.createGeometry(vertices, indices, Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT);
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public float getHeightAt(float x, float z) {
		return generator.getHeightAt(x, z);
	}

}
