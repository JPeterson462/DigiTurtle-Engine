package library.models;

import org.joml.Vector3f;

public class ModelVertex {
	
	private static final int NO_INDEX = -1;
	
	private Vector3f position;
	
	private int textureIndex = NO_INDEX;
	
	private int normalIndex = NO_INDEX;
	
	private ModelVertex duplicateVertex = null;
	
	private int index;
	
	private float length;
	
	private Vector3f tangent = new Vector3f();
	
	private Vector3f tangentSum = new Vector3f();
	
	private float tangents = 0;
	
	private boolean needToUpdate;
	
	public ModelVertex(int index, Vector3f position) {
		this.index = index;
		this.position = position;
		length = position.length();
	}
	
	public boolean hasSameTextureAndNormal(int otherTextureIndex, int otherNormalIndex) {
		return textureIndex == otherTextureIndex && normalIndex == otherNormalIndex;
	}
	
	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public ModelVertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(ModelVertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

	public Vector3f getTangent() {
		if (needToUpdate) {
			needToUpdate = false;
			if (tangents > 0) {
				tangentSum.mul(1f / tangents, tangent);
			} else {
				tangent.zero();
			}
		}
		return tangent;
	}

	public void addTangent(Vector3f tangent) {
		tangentSum.fma(1f / tangent.length(), tangent);
		tangents++;
		needToUpdate = true;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return length;
	}

}
