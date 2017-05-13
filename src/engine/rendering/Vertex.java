package engine.rendering;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
	
	public static final int POSITION_BIT = (1 << 0);
	
	public static final int TEXTURE_COORD_BIT = (1 << 1);
	
	public static final int NORMAL_BIT = (1 << 2);
	
	private Vector3f position = new Vector3f();
	
	private Vector2f textureCoord = new Vector2f();
	
	private Vector3f normal = new Vector3f();
	
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

}
