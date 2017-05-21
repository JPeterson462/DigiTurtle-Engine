package engine.skeleton;

import org.joml.Matrix4f;

import library.models.Model;

public class Skeleton {

	private Joint rootJoint;

	private int jointCount;

	public Skeleton(Model model) {
		rootJoint = convertJoint(model.getSkeleton());
		jointCount = model.getJointCount();
		rootJoint.calcInverseBindTransform(new Matrix4f());
	}

	public Joint getRootJoint() {
		return rootJoint;
	}

	private Joint convertJoint(library.models.Joint oldJoint) {
		Joint joint = new Joint(oldJoint.getIndex(), oldJoint.getName(), oldJoint.getBindLocalTransform());
		Joint[] children = new Joint[oldJoint.getChildren().size()];
		for (int i = 0; i < children.length; i++) {
			children[i] = convertJoint(oldJoint.getChildren().get(i));
		}
		joint.setChildren(children);
		return joint;
	}

	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		if (headJoint.getAnimatedTransform() == null) {
			jointMatrices[headJoint.index] = new Matrix4f();
		}
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

	public static class Joint {

		private int index;

		private String name;

		private Joint[] children;

		private Matrix4f animatedTransform;

		private Matrix4f localBindTransform;

		private Matrix4f inverseBindTransform = new Matrix4f();

		public Joint(int index, String name, Matrix4f bindLocalTransform) {
			this.index = index;
			this.name = name;
			localBindTransform = bindLocalTransform;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public void setChildren(Joint[] children) {
			this.children = children;
		}

		public Joint[] getChildren() {
			return children;
		}

		public Matrix4f getAnimatedTransform() {
			return animatedTransform;
		}

		public void setAnimationTransform(Matrix4f animationTransform) {
			this.animatedTransform = animationTransform;
		}

		public Matrix4f getInverseBindTransform() {
			return inverseBindTransform;
		}

		protected void calcInverseBindTransform(Matrix4f parentBindTransform) {
			Matrix4f bindTransform = new Matrix4f();
			parentBindTransform.mul(localBindTransform, bindTransform);
			bindTransform.invert(inverseBindTransform);
			for (Joint child : children) {
				child.calcInverseBindTransform(bindTransform);
			}
		}

	}

}
