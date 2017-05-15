package engine.skeleton;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.skeleton.Skeleton.Joint;
import engine.world.Component;
import engine.world.Entity;
import library.models.Animation;
import library.models.KeyFrameData;

public class AnimationComponent implements Component {

	private Animation currentAnimation;

	private float animationTime = 0;

	public void doAnimation(Animation animation) {
		animationTime = 0;
		currentAnimation = animation;
	}

	@Override
	public void update(Entity entity, float delta) {
		if (currentAnimation == null) {
			return;
		}
		increaseAnimationTime(delta);
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, entity.getComponent(SkeletonComponent.class).getSkeleton().getRootJoint(), new Matrix4f());
	}

	private void increaseAnimationTime(float delta) {
		animationTime += delta;
		if (animationTime > currentAnimation.getLengthSeconds()) {
			this.animationTime %= currentAnimation.getLengthSeconds();
		}
	}

	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrameData[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}

	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.getName());
		Matrix4f currentTransform = new Matrix4f();
		parentTransform.mul(currentLocalTransform, currentTransform);
		for (Joint childJoint : joint.getChildren()) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		currentTransform.mul(joint.getInverseBindTransform(), currentTransform);
		joint.setAnimationTransform(currentTransform);
	}

	private KeyFrameData[] getPreviousAndNextFrames() {
		KeyFrameData[] allFrames = currentAnimation.getKeyFrames();
		KeyFrameData previousFrame = allFrames[0];
		KeyFrameData nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTime() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrameData[] { previousFrame, nextFrame };
	}

	private float calculateProgression(KeyFrameData previousFrame, KeyFrameData nextFrame) {
		float totalTime = nextFrame.getTime() - previousFrame.getTime();
		float currentTime = animationTime - previousFrame.getTime();
		return currentTime / totalTime;
	}

	private Map<String, Matrix4f> interpolatePoses(KeyFrameData previousFrame, KeyFrameData nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getJointTransforms().keySet()) {
			Matrix4f previousTransform = previousFrame.getJointTransforms().get(jointName);
			Matrix4f nextTransform = nextFrame.getJointTransforms().get(jointName);
			Matrix4f currentTransform = interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform);
		}
		return currentPose;
	}
	
	private Matrix4f interpolate(Matrix4f previousTransform, Matrix4f nextTransform, float progression) {
		Quaternionf previousRotation = fromMatrix(previousTransform),
				nextRotation = fromMatrix(nextTransform), 
				interpolatedRotation = new Quaternionf();
		Vector3f previousOffset = new Vector3f(previousTransform.m30(), previousTransform.m31(), previousTransform.m32()), 
				nextOffset = new Vector3f(nextTransform.m30(), nextTransform.m31(), nextTransform.m32()), 
				interpolatedOffset = new Vector3f();
		previousOffset.lerp(nextOffset, progression, interpolatedOffset);
		previousRotation.slerp(nextRotation, progression, interpolatedRotation);
		return new Matrix4f().translationRotate(interpolatedOffset.x, interpolatedOffset.y, interpolatedOffset.z, interpolatedRotation);
	}
	
	private Quaternionf fromMatrix(Matrix4f matrix) {
		float w, x, y, z;
		float diagonal = matrix.m00() + matrix.m11() + matrix.m22();
		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (matrix.m21() - matrix.m12()) / w4;
			y = (matrix.m02() - matrix.m20()) / w4;
			z = (matrix.m10() - matrix.m01()) / w4;
		} else if ((matrix.m00() > matrix.m11()) && (matrix.m00() > matrix.m22())) {
			float x4 = (float) (Math.sqrt(1f + matrix.m00() - matrix.m11() - matrix.m22()) * 2f);
			w = (matrix.m21() - matrix.m12()) / x4;
			x = x4 / 4f;
			y = (matrix.m01() + matrix.m10()) / x4;
			z = (matrix.m02() + matrix.m20()) / x4;
		} else if (matrix.m11() > matrix.m22()) {
			float y4 = (float) (Math.sqrt(1f + matrix.m11() - matrix.m00() - matrix.m22()) * 2f);
			w = (matrix.m02() - matrix.m20()) / y4;
			x = (matrix.m01() + matrix.m10()) / y4;
			y = y4 / 4f;
			z = (matrix.m12() + matrix.m21()) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1f + matrix.m22() - matrix.m00() - matrix.m11()) * 2f);
			w = (matrix.m10() - matrix.m01()) / z4;
			x = (matrix.m02() + matrix.m20()) / z4;
			y = (matrix.m12() + matrix.m21()) / z4;
			z = z4 / 4f;
		}
		return new Quaternionf(x, y, z, w);
}

}
