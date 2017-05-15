package library.models;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class Joint {

	private final int index;
	
	private final String name;
	
	private final Matrix4f bindLocalTransform;
	
	private final ArrayList<Joint> children = new ArrayList<>();

	public Joint(int index, String name, Matrix4f bindLocalTransform) {
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

	public ArrayList<Joint> getChildren() {
		return children;
	}
	
	public void addChild(Joint joint) {
		children.add(joint);
	}
	
}
