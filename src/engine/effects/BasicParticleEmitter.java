package engine.effects;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.BlendMode;
import engine.rendering.InstancedGeometry;
import engine.rendering.Renderer;
import engine.rendering.Texture;
import engine.rendering.Vertex;

public class BasicParticleEmitter extends ParticleEmitter {
	
	private InstancedGeometry<ParticleTemplate> instancedGeometry;

	private final int maxInstances = 10_000;
	
	public BasicParticleEmitter(Camera camera, Texture texture, Vector3f position, float emissionRate, float speed,
			float gravityEffect, float size, float lifeLength, float speedVariance, float scaleVariance,
			float rotationVariance, float lifeVariance, int[] frames) {
		super(camera, texture, position, emissionRate, speed, gravityEffect, size, lifeLength, speedVariance, scaleVariance,
				rotationVariance, lifeVariance, frames);
	}

	@Override
	public void create(Renderer renderer) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex().position(-0.5f, -0.5f, 0));
		vertices.add(new Vertex().position(0.5f, -0.5f, 0));
		vertices.add(new Vertex().position(0.5f, 0.5f, 0));
		vertices.add(new Vertex().position(-0.5f, 0.5f, 0));
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);
		indices.add(1);
		indices.add(2);
		indices.add(2);
		indices.add(3);
		indices.add(0);		
		instancedGeometry = renderer.createInstancedGeometry(vertices, indices, Vertex.POSITION2D_BIT, ParticleTemplate.class, maxInstances);
	}

	@Override
	public InstancedGeometry<ParticleTemplate> getInstancedGeometry() {
		return instancedGeometry;
	}

	@Override
	public BlendMode getBlendMode() {
		return BlendMode.ADDITIVE;
	}

	@Override
	public Particle newParticle(Camera camera, Random random, float speed, float gravityEffect, float size, float lifeLength,
			float speedVariance, float scaleVariance, float rotationVariance, float lifeVariance, Vector3f position,
			int[] frames) {
		Vector3f velocity = new Vector3f(randomize(random, -1, 2), randomize(random, 0, 1), randomize(random, -1, 2));
		velocity.normalize();
		velocity.mul(randomize(random, speed, speedVariance));
		return new Particle(camera, position, velocity, gravityEffect, randomize(random, lifeLength, lifeVariance), 
				randomize(random, 0, rotationVariance), randomize(random, size, scaleVariance), frames);
	}
	
	private float randomize(Random random, float baseline, float variance) {
		return baseline + random.nextFloat() * variance;
	}

	@Override
	public int getMaxParticles() {
		return maxInstances;
	}

}
