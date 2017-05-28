package engine.effects;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;

public class Particle implements Comparable<Particle> {

	private Vector3f position, velocity;
	
	private float gravityEffect, lifeLength, rotation, scale, elapsedTime;
	
	public static float GRAVITY = -50;
	
	private Vector2i atlasSize;
	
	private Vector2f atlasOffset = new Vector2f(), nextAtlasOffset = new Vector2f();
	
	private float blendFactor, distanceSquared;
	
	private Vector3f distanceVector = new Vector3f();
	
	private int stageCount;
	
	private ParticleTemplate particleTemplate;

	public Particle(Camera camera, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale, int[] frames) {
		particleTemplate = new ParticleTemplate();
		particleTemplate.setCamera(camera);
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		elapsedTime = 0;
		atlasSize = new Vector2i(frames[0], frames[1]);
		stageCount = atlasSize.x * atlasSize.y;
		computeAtlasOffset();
	}
	
	private void computeAtlasOffset() {
		float lifeFactor = elapsedTime / lifeLength;
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		float invAtlasSizeY = 1f / (float) atlasSize.y;
		if (index1 < stageCount - 1) {
			int index2 = index1 + 1;
			blendFactor = atlasProgression - (int) atlasProgression;
			int row = index1 / atlasSize.y;
			int column = index1 - row * atlasSize.y;
			atlasOffset.x = (float) column * invAtlasSizeY;
			atlasOffset.y = (float) row * invAtlasSizeY;
			row = index2 / atlasSize.y;
			column = index2 - row * atlasSize.y;
			nextAtlasOffset.x = (float) column * invAtlasSizeY;
			nextAtlasOffset.y = (float) row * invAtlasSizeY;
		} else {
			blendFactor = 1;
			int row = index1 / atlasSize.y;
			int column = index1 - row * atlasSize.y;
			atlasOffset.x = (float) column * invAtlasSizeY;
			atlasOffset.y = (float) row * invAtlasSizeY;
			nextAtlasOffset.set(atlasOffset);
		}
	}
	
	public String toString() {
		return position + " " + velocity + " " + gravityEffect + " " + lifeLength + " " + rotation + " " + scale + " " + elapsedTime + " " + GRAVITY; 
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getGravityEffect() {
		return gravityEffect;
	}

	public float getLifeLength() {
		return lifeLength;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public float getElapsedTime() {
		return elapsedTime;
	}
	
	public Vector2i getAtlasSize() {
		return atlasSize;
	}
	
	public Vector2f getAtlasOffset() {
		return atlasOffset;
	}

	public Vector2f getNextAtlasOffset() {
		return nextAtlasOffset;
	}
	
	public float getBlendFactor() {
		return blendFactor;
	}
	
	public float getDistanceSquared() {
		return distanceSquared;
	}
	
	public ParticleTemplate getParticleTemplate() {
		return particleTemplate;
	}
	
	public boolean update(float delta, Vector3f cameraPosition) {System.out.println(position);
		velocity.y += GRAVITY * gravityEffect * delta;
		position.fma(delta, velocity);
		elapsedTime += delta;
		computeAtlasOffset();
		distanceVector.set(cameraPosition).sub(position);
		distanceSquared = distanceVector.lengthSquared();
		return elapsedTime < lifeLength;
	}
	
	public void store(ParticleTemplate instance, Matrix4f modelMatrix, Quaternionf orientation) {
		modelMatrix.identity().translationRotateScale(position, orientation, scale);
		instance.setModelMatrix(modelMatrix, rotation);
		instance.setTextureAtlasOffset(new Vector4f(atlasOffset.x, atlasOffset.y, nextAtlasOffset.x, nextAtlasOffset.y));
		instance.setBlendFactor(blendFactor);
	}

	@Override
	public int compareTo(Particle o) {
		return Float.compare(getDistanceSquared(), o.getDistanceSquared());
	}
	
}
