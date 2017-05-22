package library.models;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class Model {
	
	private ArrayList<Mesh> meshes = new ArrayList<>();
	
	private Joint rootJoint;
	
	private int jointCount;
	
	public Model(Mesh mesh) {
		meshes.add(mesh);
	}
	
	public Model() {
		
	}
	
	public Model(ArrayList<Mesh> meshes) {
		this.meshes.addAll(meshes);
	}
	
	public void addMesh(Mesh mesh) {
		meshes.add(mesh);
	}
	
	public void setSkeleton(Joint rootJoint, int jointCount) {
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		rootJoint.calcInverseBindTransform(new Matrix4f());
	}
	
	public Joint getSkeleton() {
		return rootJoint;
	}
	
	public ArrayList<Mesh> getMeshes() {
		return meshes;
	}

	public int getJointCount() {
		return jointCount;
	}

}
