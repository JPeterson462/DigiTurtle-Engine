package library.models;

import java.util.ArrayList;

import engine.world.Material;

public class Model {
	
	private ArrayList<Vertex> vertices;
	
	private ArrayList<Integer> indices;
	
	private Material material;
	
	private Joint rootJoint;
	
	private int jointCount;
	
	public Model(ModelVertexQueue queue) {
		vertices = new ArrayList<>();
		for (int i = 0; i < queue.getVertices().size(); i++) {
			vertices.add(queue.transform(queue.getVertices().get(i)));
		}
		indices = queue.getIndices();
	}
	
	public Model(ArrayList<Vertex> vertices, ArrayList<Integer> indices) {
		this.vertices = vertices;
		this.indices = indices;
	}
	
	public void setSkeleton(Joint rootJoint, int jointCount) {
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
	}
	
	public Joint getSkeleton() {
		return rootJoint;
	}
	
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}

	public int getJointCount() {
		return jointCount;
	}

}
