package library.models.collada;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import utils.XMLNode;

public class AnimationLoader {

	private XMLNode animationData;
	private XMLNode jointHierarchy;

	private String animation;

	public AnimationLoader(XMLNode animationData, XMLNode jointHierarchy, String animation) {
		this.animationData = animationData;
		this.jointHierarchy = jointHierarchy;
		this.animation = animation;
	}

	public ColladaAnimation extractAnimation() {
		String rootNode = findRootJointName();
		float[] times = getKeyTimes();
		float duration = times[times.length-1];
		ColladaKeyFrameData[] keyFrames = initKeyFrames(times);
		List<XMLNode> animationNodes = animationData.getChildren("animation");
		for (XMLNode jointNode : animationNodes) {
			loadJointTransforms(keyFrames, jointNode, rootNode);
		}
		return new ColladaAnimation(duration, keyFrames);
	}

	private float[] getKeyTimes() {
		XMLNode timeData = animationData.getChild("animation").getChild("source").getChild("float_array");
		String[] rawTimes = timeData.getData().split(" ");
		float[] times = new float[rawTimes.length];
		for (int i = 0; i < times.length; i++){
			times[i] = Float.parseFloat(rawTimes[i]);
		}
		return times;
	}

	private ColladaKeyFrameData[] initKeyFrames(float[] times) {
		ColladaKeyFrameData[] frames = new ColladaKeyFrameData[times.length];
		for (int i = 0; i < frames.length; i++){
			frames[i] = new ColladaKeyFrameData(times[i]);
		}
		return frames;
	}

	private void loadJointTransforms(ColladaKeyFrameData[] frames, XMLNode jointData, String rootNodeId) {
		String jointNameId = getJointName(jointData);
		String dataId = getDataId(jointData);
		XMLNode transformData = jointData.getChildWithAttribute("source", "id", dataId);
		String[] rawData = transformData.getChild("float_array").getData().split(" ");
		processTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
	}

	private String getDataId(XMLNode jointData) {
		XMLNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT");
		return node.getAttribute("source").substring(1);
	}

	private String getJointName(XMLNode jointData) {
		XMLNode channelNode = jointData.getChild("channel");
		String data = channelNode.getAttribute("target");
		return data.split("/")[0];
	}

	private void processTransforms(String jointName, String[] rawData, ColladaKeyFrameData[] keyFrames, boolean root) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		float[] matrixData = new float[16];
		for (int i = 0; i < keyFrames.length; i++) {
			for (int j = 0; j < 16; j++) {
				matrixData[j] = Float.parseFloat(rawData[i * 16 + j]);
			}
			buffer.clear();
			buffer.put(matrixData);
			buffer.flip();
			Matrix4f transform = new Matrix4f(buffer);
			transform.transpose();
			if (root) {
				// blender uses z up axis
				ColladaUtils.correct(transform);
			}
			keyFrames[i].addTransform(jointName, transform);
		}
	}

	private String findRootJointName() {
		XMLNode skeleton = jointHierarchy.getChild("visual_scene").getChildWithAttribute("node", "id", animation);
		return skeleton.getChild("node").getAttribute("id");
	}

}
