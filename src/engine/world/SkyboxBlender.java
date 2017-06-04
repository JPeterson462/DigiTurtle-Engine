package engine.world;

public class SkyboxBlender {
	
	private float[] milestones;
	
	private float cycleTime, blendFactor, time, perMilestone;
	
	public SkyboxBlender(float cycleTime, float... milestones) {
		this.cycleTime = cycleTime;
		blendFactor = 0;
		this.milestones = new float[milestones.length + 2];
		System.arraycopy(milestones, 0, this.milestones, 1, milestones.length);
		this.milestones[0] = 0;
		this.milestones[milestones.length] = cycleTime;
		time = 0;
		perMilestone = 1f / (milestones.length + 1);
	}
	
	public void update(float dt) {
		blendFactor = 0;
		time += dt;
		time %= cycleTime;
		for (int i = 0; i < milestones.length - 1; i++) {
			if (time >= milestones[i] && time < milestones[i + 1]) {
				blendFactor = (time - milestones[i]) / (milestones[i + 1] - milestones[i]) + (i * perMilestone);
				break;
			}
		}
		if (time >= milestones[milestones.length - 1]) {
			blendFactor = 1;
		}//TODO invert blending
	}
	
	public float getBlendFactor() {
		return blendFactor;
	}

	public float getRotation() {
		return 2 * (float) Math.PI * (time / cycleTime);
	}

}
