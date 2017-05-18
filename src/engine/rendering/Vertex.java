package engine.rendering;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Vertex {
	
	public static final int POSITION_BIT = (1 << 0);
	
	public static final int TEXTURE_COORD_BIT = (1 << 1);
	
	public static final int NORMAL_BIT = (1 << 2);
	
	public static final int JOINTID_BIT = (1 << 3);
	
	public static final int WEIGHT_BIT = (1 << 4);
	
	public static final int POSITION2D_BIT = (1 << 5);
	
	private Vector3f position = new Vector3f();
	
	private Vector2f textureCoord = new Vector2f();
	
	private Vector3f normal = new Vector3f();
	
	private Vector3i jointIds = new Vector3i();
	
	private Vector3f weights = new Vector3f();
	
	public Vertex() {
		
	}
	
	public Vertex(library.models.Vertex vertex) {
		position.set(vertex.getPosition());
		textureCoord.set(vertex.getTextureCoord());
		normal.set(vertex.getNormal());
	}
	
	public Vector3f position() {
		return position;
	}

	public Vertex position(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}
	
	public Vertex position(Vector3f position) {
		this.position.set(position);
		return this;
	}
	
	public Vector2f textureCoord() {
		return textureCoord;
	}
	
	public Vertex textureCoord(float s, float t) {
		textureCoord.set(s, t);
		return this;
	}
	
	public Vertex textureCoord(Vector2f textureCoord) {
		this.textureCoord.set(textureCoord);
		return this;
	}
	
	public Vector3f normal() {
		return normal;
	}
	
	public Vertex normal(float x, float y, float z) {
		normal.set(x, y, z);
		return this;
	}
	
	public Vertex normal(Vector3f normal) {
		this.normal.set(normal);
		return this;
	}
	
	public Vector3i jointIDs() {
		return jointIds;
	}
	
	public Vertex jointIDs(int i0, int i1, int i2) {
		jointIds.set(i0, i1, i2);
		return this;
	}
	
	public Vertex jointIDs(Vector3i jointIds) {
		this.jointIds.set(jointIds);
		return this;
	}
	
	public Vector3f weights() {
		return weights;
	}
	
	public Vertex weights(float w0, float w1, float w2) {
		weights.set(w0, w1, w2);
		return this;
	}
	
	public Vertex weights(Vector3f weights) {
		this.weights.set(weights);
		return this;
	}

}
