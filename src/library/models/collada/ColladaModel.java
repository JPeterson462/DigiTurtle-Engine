package library.models.collada;

import java.util.ArrayList;

public class ColladaModel {
	
	private ArrayList<ColladaVertex> vertices = new ArrayList<>();
	
	private ArrayList<Integer> indices = new ArrayList<>();
	
	private int jointCount;
	
	private ColladaJoint rootJoint;
	
	public void setMeshData(ArrayList<ColladaVertex> vertices, ArrayList<Integer> indices) {
		this.vertices = vertices;
		this.indices = indices;
	}
	
	public void setSkeletonData(int jointCount, ColladaJoint rootJoint) {
		this.jointCount = jointCount;
		this.rootJoint = rootJoint;
	}

	public ArrayList<ColladaVertex> getVertices() {
		return vertices;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public int getJointCount() {
		return jointCount;
	}

	public ColladaJoint getRootJoint() {
		return rootJoint;
	}
	
}
