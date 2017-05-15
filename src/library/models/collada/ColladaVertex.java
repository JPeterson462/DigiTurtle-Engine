package library.models.collada;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class ColladaVertex {
	
	private Vector3f position;
	
	private Vector2f textureCoord;
	
	private Vector3f normal;

	private Vector3i jointIds;
	
	private Vector3f vertexWeights;

	public ColladaVertex(Vector3f position, Vector2f textureCoord, Vector3f normal, Vector3i jointIds, Vector3f vertexWeights) {
		this.position = position;
		this.textureCoord = textureCoord;
		this.normal = normal;
		this.jointIds = jointIds;
		this.vertexWeights = vertexWeights;
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

	public Vector3i getJointIds() {
		return jointIds;
	}

	public Vector3f getVertexWeights() {
		return vertexWeights;
	}
	
}
