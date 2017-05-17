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

	private Vector3f previousOffset = new Vector3f(), nextOffset = new Vector3f(), position = new Vector3f();
	
	private Quaternionf previousRotation = new Quaternionf(), nextRotation = new Quaternionf(), rotation = new Quaternionf();
	
	private Matrix4f interpolate(Matrix4f previousTransform, Matrix4f nextTransform, float progression) {
		fromMatrix(previousTransform, previousRotation);
		fromMatrix(nextTransform, nextRotation);
		previousOffset.set(previousTransform.m30(), previousTransform.m31(), previousTransform.m32());
		nextOffset.set(nextTransform.m30(), nextTransform.m31(), nextTransform.m32());
		interpolate(previousOffset, nextOffset, progression, position);
		interpolate(previousRotation, nextRotation, progression, rotation);
		Matrix4f matrix = new Matrix4f();
		matrix.setTranslation(position);
		matrix.mul(toRotationMatrix(rotation), matrix);
		return matrix;
	}

	private void interpolate(Vector3f start, Vector3f end, float progression, Vector3f target) {
		float x = start.x + (end.x - start.x) * progression;
		float y = start.y + (end.y - start.y) * progression;
		float z = start.z + (end.z - start.z) * progression;
		target.set(x, y, z);
	}

	private void interpolate(Quaternionf a, Quaternionf b, float blend, Quaternionf result) {
		float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
		float blendI = 1f - blend;
		if (dot < 0) {
			result.w = blendI * a.w + blend * -b.w;
			result.x = blendI * a.x + blend * -b.x;
			result.y = blendI * a.y + blend * -b.y;
			result.z = blendI * a.z + blend * -b.z;
		} else {
			result.w = blendI * a.w + blend * b.w;
			result.x = blendI * a.x + blend * b.x;
			result.y = blendI * a.y + blend * b.y;
			result.z = blendI * a.z + blend * b.z;
		}
		result.normalize();
	}

	private Matrix4f toRotationMatrix(Quaternionf rotation) {
		Matrix4f matrix = new Matrix4f();
		final float xy = rotation.x * rotation.y;
		final float xz = rotation.x * rotation.z;
		final float xw = rotation.x * rotation.w;
		final float yz = rotation.y * rotation.z;
		final float yw = rotation.y * rotation.w;
		final float zw = rotation.z * rotation.w;
		final float xSquared = rotation.x * rotation.x;
		final float ySquared = rotation.y * rotation.y;
		final float zSquared = rotation.z * rotation.z;
		matrix.m00(1 - 2 * (ySquared + zSquared));
		matrix.m01(2 * (xy - zw));
		matrix.m02(2 * (xz + yw));
		matrix.m03(0);
		matrix.m10(2 * (xy + zw));
		matrix.m11(1 - 2 * (xSquared + zSquared));
		matrix.m12(2 * (yz - xw));
		matrix.m13(0);
		matrix.m20(2 * (xz - yw));
		matrix.m21(2 * (yz + xw));
		matrix.m22(1 - 2 * (xSquared + ySquared));
		matrix.m23(0);
		matrix.m30(0);
		matrix.m31(0);
		matrix.m32(0);
		matrix.m33(1);
		return matrix;
	}

	private void fromMatrix(Matrix4f matrix, Quaternionf rotation) {
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
		rotation.set(x, y, z, w);
	}

}
