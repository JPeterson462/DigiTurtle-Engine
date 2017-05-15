package library.models.collada;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class ColladaJoint {
	
	private final int index;
	
	private final String name;
	
	private final Matrix4f bindLocalTransform;
	
	private final ArrayList<ColladaJoint> children = new ArrayList<>();

	public ColladaJoint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;
		this.bindLocalTransform = bindLocalTransform;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public Matrix4f getBindLocalTransform() {
		return bindLocalTransform;
	}

	public ArrayList<ColladaJoint> getChildren() {
		return children;
	}
	
	public void addChild(ColladaJoint joint) {
		children.add(joint);
	}

}
