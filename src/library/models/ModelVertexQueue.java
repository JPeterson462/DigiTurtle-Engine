package library.models;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class ModelVertexQueue {

	private ArrayList<ModelVertex> vertices = new ArrayList<>();

	private ArrayList<Vector2f> textureCoords = new ArrayList<>();

	private ArrayList<Vector3f> normals = new ArrayList<>();

	private ArrayList<Integer> indices = new ArrayList<>();

	public void position(float x, float y, float z) {
		vertices.add(new ModelVertex(vertices.size(), new Vector3f(x, y, z)));
	}

	public void textureCoord(float s, float t) {
		textureCoords.add(new Vector2f(s, t));
	}

	public void normal(float x, float y, float z) {
		normals.add(new Vector3f(x, y, z));
	}

	public void submit(int[] vertex0, int[] vertex1, int[] vertex2) {
		ModelVertex v0 = submit(vertex0[0], vertex0[1], vertex0[2]);
		ModelVertex v1 = submit(vertex1[0], vertex1[1], vertex1[2]);
		ModelVertex v2 = submit(vertex2[0], vertex2[1], vertex2[2]);
		Vector3f deltaPos1 = new Vector3f();
		v1.getPosition().sub(v0.getPosition(), deltaPos1);
		Vector3f deltaPos2 = new Vector3f();
		v2.getPosition().sub(v0.getPosition(), deltaPos1);
		Vector2f uv0 = textureCoords.get(v0.getTextureIndex());
		Vector2f uv1 = textureCoords.get(v1.getTextureIndex());
		Vector2f uv2 = textureCoords.get(v2.getTextureIndex());
		Vector2f deltaUv1 = new Vector2f();
		uv1.sub(uv0, deltaUv1);
		Vector2f deltaUv2 = new Vector2f();
		uv2.sub(uv0, deltaUv1);
		float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		deltaPos1.mul(deltaUv2.y);
		deltaPos2.mul(deltaUv1.y);
		Vector3f tangent = deltaPos1.sub(deltaPos2);
		tangent.mul(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}

	private ModelVertex submit(int vertexPointer, int textureIndex, int normalIndex) {
		vertexPointer--;
		textureIndex--;
		normalIndex--;
		ModelVertex vertex = vertices.get(vertexPointer);
		if (!vertex.isSet()) {
			vertex.setTextureIndex(textureIndex);
			vertex.setNormalIndex(normalIndex);
			indices.add(vertexPointer);
			return vertex;
		} else {
			return submitDuplicate(vertex, textureIndex, normalIndex);
		}
	}
	
	private ModelVertex submitDuplicate(ModelVertex vertex, int textureIndex, int normalIndex) {
		if (vertex.hasSameTextureAndNormal(textureIndex, normalIndex)) {
			indices.add(vertex.getIndex());
			return vertex;
		} else {
			ModelVertex anotherVertex = vertex.getDuplicateVertex();
			if  (anotherVertex != null) {
				return submitDuplicate(anotherVertex, textureIndex, normalIndex);
			} else {
				ModelVertex duplicateVertex = new ModelVertex(vertices.size(), vertex.getPosition());
				duplicateVertex.setTextureIndex(textureIndex);
				duplicateVertex.setNormalIndex(normalIndex);
				vertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}
		}
	}
	
	public ArrayList<ModelVertex> getVertices() {
		return vertices;
	}
	
	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public Vertex transform(ModelVertex modelVertex) {
		return new Vertex(modelVertex.getPosition(), textureCoords.get(modelVertex.getTextureIndex()), 
				normals.get(modelVertex.getNormalIndex()), modelVertex.getTangent());
	}

}
