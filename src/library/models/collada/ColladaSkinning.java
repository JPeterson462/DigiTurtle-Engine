package library.models.collada;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class ColladaSkinning {

	private final ArrayList<String> jointOrder;

	private final ArrayList<VertexSkinData> verticesSkinData;

	public ColladaSkinning(ArrayList<String> jointOrder, ArrayList<VertexSkinData> verticesSkinData) {
		this.jointOrder = jointOrder;
		this.verticesSkinData = verticesSkinData;
	}

	public ArrayList<String> getJointOrder() {
		return jointOrder;
	}

	public ArrayList<VertexSkinData> getVerticesSkinData() {
		return verticesSkinData;
	}

	public static class VertexSkinData {

		private final ArrayList<Integer> jointIds = new ArrayList<>();

		private final ArrayList<Float> weights = new ArrayList<>();
		
		public Vector3i getJointIDs() {
			return new Vector3i(jointIds.get(0), jointIds.get(1), jointIds.get(2));
		}

		public Vector3f getJointWeights() {
			return new Vector3f(weights.get(0), weights.get(1), weights.get(2));
		}

		public void addJointWeight(int jointId, float weight) {
			for (int i = 0; i < weights.size(); i++) {
				if (weight > weights.get(i)) {
					jointIds.add(i, jointId);
					weights.add(i, weight);
					return;
				}
			}
			jointIds.add(jointId);
			weights.add(weight);
		}

		public void limitJointNumber(int max) {
			if (jointIds.size() > max) {
				float[] topWeights = new float[max];
				float total = saveTopWeights(topWeights);
				refillWeightList(topWeights, total);
				removeExcessJointIds(max);
			}
			else if (jointIds.size() < max) {
				fillEmptyWeights(max);
			}
		}

		private void fillEmptyWeights(int max) {
			while (jointIds.size() < max) {
				jointIds.add(0);
				weights.add(0f);
			}
		}

		private float saveTopWeights(float[] topWeightsArray) {
			float total = 0;
			for (int i = 0; i < topWeightsArray.length; i++) {
				topWeightsArray[i] = weights.get(i);
				total += topWeightsArray[i];
			}
			return total;
		}

		private void refillWeightList(float[] topWeights, float total) {
			weights.clear();
			for (int i = 0; i < topWeights.length; i++) {
				weights.add(Math.min(topWeights[i]/total, 1));
			}
		}

		private void removeExcessJointIds(int max) {
			while (jointIds.size() > max) {
				jointIds.remove(jointIds.size() - 1);
			}
		}

	}
}
