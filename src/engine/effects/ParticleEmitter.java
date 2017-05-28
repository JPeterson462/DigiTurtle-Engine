package engine.effects;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.BlendMode;
import engine.rendering.InstancedGeometry;
import engine.rendering.Renderer;
import engine.rendering.Texture;

public abstract class ParticleEmitter {

	private ArrayList<Particle> particles;

	private Texture texture;

	private Vector3f position;

	private float emissionRate, speed, gravityEffect, size, lifeLength;

	private float speedVariance, scaleVariance, rotationVariance, lifeVariance;

	private float time;

	private int[] frames;

	private Random random;
	
	private Camera camera;

	public ParticleEmitter(Camera camera, Texture texture, Vector3f position, float emissionRate, float speed, float gravityEffect,
			float size, float lifeLength, float speedVariance, float scaleVariance, float rotationVariance,
			float lifeVariance, int[] frames) {
		this.camera = camera;
		particles = new ArrayList<>();
		this.texture = texture;
		this.position = position;
		this.emissionRate = emissionRate;
		this.speed = speed;
		this.gravityEffect = gravityEffect;
		this.size = size;
		this.lifeLength = lifeLength;
		this.speedVariance = speedVariance;
		this.scaleVariance = scaleVariance;
		this.rotationVariance = rotationVariance;
		this.lifeVariance = lifeVariance;
		time = 0;
		this.frames = frames;
		random = new Random();
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void update(float delta, Vector3f cameraPosition) {
		ArrayList<Particle> particleList = particles;
		for (int i = particleList.size() - 1; i >= 0; i--) {
			Particle particle = particleList.get(i);
			if (!particle.update(delta, cameraPosition)) {
				particleList.remove(i);
			}
		}
		particleList.sort((a, b) -> a.compareTo(b));
		time += delta;
		while (time > emissionRate) {
			particles.add(newParticle(camera, random, speed, gravityEffect, size, lifeLength, speedVariance, 
					scaleVariance, rotationVariance, lifeVariance, position, frames));
			time -= emissionRate;
		}
		System.out.println(particles.size() + " " + delta);
	}

	public int getParticleCount() {
		return particles.size();
	}

	public void store(InstancedGeometry<ParticleTemplate> instancedGeometry, int maxParticles) {
		Matrix4f modelMatrix = new Matrix4f();
		Quaternionf orientation = new Quaternionf();
		ArrayList<Particle> particleList = particles;
		for (int instanceId = particleList.size() - 1; instanceId >= 0; instanceId--) {
			Particle particle = particleList.get(instanceId);
			particle.store(particle.getParticleTemplate(), modelMatrix, orientation);
			instancedGeometry.updateInstance(instanceId, particle.getParticleTemplate());
		}
	}
	
	private Vector2f atlasSize = new Vector2f();
	
	public Vector2f getAtlasSize() {
		atlasSize.set(frames[0], frames[1]);
		return atlasSize;
	}
	
	public abstract void create(Renderer renderer);
	
	public abstract InstancedGeometry<ParticleTemplate> getInstancedGeometry();

	public abstract BlendMode getBlendMode();

	public abstract Particle newParticle(Camera camera, Random random, float speed, float gravityEffect, 
			float size, float lifeLength, float speedVariance, float scaleVariance, float rotationVariance, 
			float lifeVariance, Vector3f position, int[] frames);
	
	public abstract int getMaxParticles();

}
