package engine.world;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Vertex;

public class TerrainChunk {

	private TerrainGenerator generator; // use the generator so that instead of interpolating the heights you just do a lookup

	private ArrayList<Vertex> vertices = new ArrayList<>();

	private ArrayList<Integer> indices = new ArrayList<>();

	private Geometry geometry;
	
	private TerrainTexturePack texturePack;

	public TerrainChunk(TerrainGenerator generator, int width, int height, float resolution, float x, float z, TerrainTexturePack texturePack) {
		this.texturePack = texturePack;
		this.generator = generator;
		int iwidth = width + 1, iheight = height + 1;
		for (int i = 0; i < iwidth; i++) {
			for (int j = 0; j < iheight; j++) {
				Vector3f position = new Vector3f(i * resolution + x, 0, j * resolution + z);
				position.y = generator.getHeightAt(position.x, position.z);
				Vector2f textureCoord = new Vector2f((float) i / (float) width, (float) j / (float) height);
				Vector3f normal = calculateNormal(resolution, position);
				vertices.add(new Vertex().position(position).textureCoord(textureCoord).normal(normal));
			}
		}
		for (int i = 0; i < iwidth - 1; i++) {
			for (int j = 0; j < iheight - 1; j++) {
				int topLeft = (i * iwidth) + j, topRight = topLeft + 1;
				int bottomLeft = ((i + 1) * iwidth) + j, bottomRight = bottomLeft + 1;
				indices.add(topLeft);
				indices.add(bottomLeft);
				indices.add(topRight);
				indices.add(topRight);
				indices.add(bottomLeft);
				indices.add(bottomRight);
			}
		}
	}

	private Vector3f calculateNormal(float resolution, Vector3f position) {
		float heightL = getHeightAt(position.x - resolution, position.z);
		float heightR = getHeightAt(position.x + resolution, position.z);
		float heightD = getHeightAt(position.x, position.z - resolution);
		float heightU = getHeightAt(position.x, position.z + resolution);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}

	public void create(Renderer renderer) {
		geometry = renderer.createGeometry(vertices, indices, Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT | Vertex.NORMAL_BIT);
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public Geometry getGeometry(Renderer renderer) {
		if (geometry == null) {
			create(renderer);
		}
		return getGeometry();
	}

	public float getHeightAt(float x, float z) {
		return generator.getHeightAt(x, z);
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

}
