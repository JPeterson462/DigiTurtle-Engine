package library.models;

import java.util.ArrayList;

import org.joml.Matrix4f;

public class Joint {

	private final int index;
	
	private final String name;
	
	private final Matrix4f bindLocalTransform;
	
	private final ArrayList<Joint> children = new ArrayList<>();

	private Matrix4f localBindTransform, inverseBindTransform, bindTransform;

	public Joint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;
		this.bindLocalTransform = bindLocalTransform;
		localBindTransform = new Matrix4f();
		inverseBindTransform = new Matrix4f();
		bindTransform = new Matrix4f();
	}

	protected void calcInverseBindTransform(Matrix4f parentBindTransform) {
		parentBindTransform.mul(localBindTransform, bindTransform);
		bindTransform.invert(inverseBindTransform);
		for (Joint child : children) {
			child.calcInverseBindTransform(bindTransform);
		}
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

	public Matrix4f getBindTransform() {
		return bindTransform;
	}
	
}
