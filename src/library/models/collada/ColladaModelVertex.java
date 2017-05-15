package library.models.collada;

import org.joml.Vector3f;

public class ColladaModelVertex {
	
	private static final int NO_INDEX = -1;
	
	private Vector3f position;
	
	private int textureIndex = NO_INDEX;
	
	private int normalIndex = NO_INDEX;
	
	private ColladaModelVertex duplicateVertex = null;
	
	private int index;
	
	private float length;
	
	private Vector3f tangent = new Vector3f();
	
	private Vector3f tangentSum = new Vector3f();
	
	private float tangents = 0;
	
	private boolean needToUpdate;
	
	private ColladaSkinning.VertexSkinData skinData;
	
	public ColladaModelVertex(int index, Vector3f position, ColladaSkinning.VertexSkinData skinData) {
		this.index = index;
		this.position = position;
		length = position.length();
		this.skinData = skinData;
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

	public ColladaModelVertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(ColladaModelVertex duplicateVertex) {
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

	public ColladaSkinning.VertexSkinData getSkinData() {
		return skinData;
	}

}
