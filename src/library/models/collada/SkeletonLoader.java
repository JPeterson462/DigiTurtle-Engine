package library.models.collada;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import utils.XMLNode;

public class SkeletonLoader {

	private XMLNode armatureData;

	private ArrayList<String> boneOrder;

	private int jointCount = 0;

	public SkeletonLoader(XMLNode visualSceneNode, ArrayList<String> boneOrder, String animation) {
		this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", animation);
		this.boneOrder = boneOrder;
	}

	public void extractBoneData(ColladaModel model) {
		XMLNode headNode = armatureData.getChild("node");
		ColladaJoint headJoint = loadJointData(headNode, true);
		model.setSkeletonData(jointCount, headJoint);
	}

	private ColladaJoint loadJointData(XMLNode jointNode, boolean isRoot) {
		ColladaJoint joint = extractMainJointData(jointNode, isRoot);
		for (XMLNode childNode : jointNode.getChildren("node")) {
			joint.addChild(loadJointData(childNode, false));
		}
		return joint;
	}

	private ColladaJoint extractMainJointData(XMLNode jointNode, boolean isRoot) {
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
		Matrix4f matrix = new Matrix4f(convertData(matrixData));
		matrix.transpose();
		if (isRoot) {
			// because in Blender z is up
			ColladaUtils.correct(matrix);
		}
		jointCount++;
		return new ColladaJoint(index, nameId, matrix);
	}

	private FloatBuffer convertData(String[] rawData) {
		float[] matrixData = new float[16];
		for (int i = 0; i < matrixData.length; i++) {
			matrixData[i] = Float.parseFloat(rawData[i]);
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(matrixData);
		buffer.flip();
		return buffer;
	}

}
