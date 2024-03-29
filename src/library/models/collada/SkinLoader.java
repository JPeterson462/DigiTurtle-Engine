package library.models.collada;

import utils.XMLNode;

import java.util.ArrayList;

import library.models.collada.ColladaSkinning.VertexSkinData;;

public class SkinLoader {

	private final XMLNode skinningData;
	
	private final int maxWeights;

	public SkinLoader(XMLNode controllersNode, int maxWeights) {
		this.skinningData = controllersNode.getChild("controller").getChild("skin");
		this.maxWeights = maxWeights;
	}

	public ColladaSkinning extractSkinData() {
		ArrayList<String> jointsList = loadJointsList();
		float[] weights = loadWeights();
		XMLNode weightsDataNode = skinningData.getChild("vertex_weights");
		int[] effectorJointCounts = getEffectiveJointsCounts(weightsDataNode);
		ArrayList<VertexSkinData> vertexWeights = getSkinData(weightsDataNode, effectorJointCounts, weights);
		return new ColladaSkinning(jointsList, vertexWeights);
	}

	private ArrayList<String> loadJointsList() {
		XMLNode inputNode = skinningData.getChild("vertex_weights");
		String jointDataId = inputNode.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("source")
				.substring(1);
		XMLNode jointsNode = skinningData.getChildWithAttribute("source", "id", jointDataId).getChild("Name_array");
		String[] names = jointsNode.getData().split(" ");
		ArrayList<String> jointsList = new ArrayList<String>();
		for (String name : names) {
			jointsList.add(name);
		}
		return jointsList;
	}

	private float[] loadWeights() {
		XMLNode inputNode = skinningData.getChild("vertex_weights");
		String weightsDataId = inputNode.getChildWithAttribute("input", "semantic", "WEIGHT").getAttribute("source")
				.substring(1);
		XMLNode weightsNode = skinningData.getChildWithAttribute("source", "id", weightsDataId).getChild("float_array");
		String[] rawData = weightsNode.getData().split(" ");
		float[] weights = new float[rawData.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = Float.parseFloat(rawData[i]);
		}
		return weights;
	}

	private int[] getEffectiveJointsCounts(XMLNode weightsDataNode) {
		String[] rawData = weightsDataNode.getChild("vcount").getData().split(" ");
		int[] counts = new int[rawData.length];
		for (int i = 0; i < rawData.length; i++) {
			counts[i] = Integer.parseInt(rawData[i]);
		}
		return counts;
	}

	private ArrayList<VertexSkinData> getSkinData(XMLNode weightsDataNode, int[] counts, float[] weights) {
		String[] rawData = weightsDataNode.getChild("v").getData().split(" ");
		ArrayList<VertexSkinData> skinningData = new ArrayList<VertexSkinData>();
		int pointer = 0;
		for (int count : counts) {
			VertexSkinData skinData = new VertexSkinData();
			for (int i = 0; i < count; i++) {
				int jointId = Integer.parseInt(rawData[pointer++]);
				int weightId = Integer.parseInt(rawData[pointer++]);
				skinData.addJointWeight(jointId, weights[weightId]);
			}
			skinData.limitJointNumber(maxWeights);
			skinningData.add(skinData);
		}
		return skinningData;
	}
	
}
