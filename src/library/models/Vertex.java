package library.models;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Vertex {
	
	private Vector3f position;
	
	private Vector2f textureCoord;
	
	private Vector3f normal;
	
	private Vector3f tangent;
	
	private Vector3i jointIds;

	private Vector3f weights;
	
	public Vertex(Vector3f position, Vector2f textureCoord, Vector3f normal, Vector3f tangent) {
		this.position = position;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.tangent = tangent;
	}
	
	public Vertex(Vector3f position, Vector2f textureCoord, Vector3f normal, Vector3i jointIds, Vector3f weights) {
		this.position = position;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.tangent = null;
		this.jointIds = jointIds;
		this.weights = weights;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector2f getTextureCoord() {
		return textureCoord;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public Vector3i getJointIds() {
		return jointIds;
	}

	public Vector3f getWeights() {
		return weights;
	}

}
