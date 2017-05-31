package engine.effects;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.BlendMode;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Texture;

public class ParticleRenderer {
	
	private Renderer renderer;
	
	private Shader shader;
	
	private ArrayList<ParticleEmitter> emitters = new ArrayList<>();
	
	public ParticleRenderer(Renderer renderer, Camera camera, Vector2f windowSize) {
		this.renderer = renderer;
		HashMap<Integer, String> attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "modelViewMatrix");
		attributes.put(5, "textureAtlasOffset");
		attributes.put(6, "blendFactor");
		shader = renderer.createShader(getClass().getClassLoader().getResourceAsStream("engine/scene/deferred/particleVertex.glsl"), getClass().getClassLoader().getResourceAsStream("engine/scene/deferred/particleFragment.glsl"), attributes);
		shader.bind();
		shader.uploadMatrix(shader.getUniformLocation("projectionMatrix"), camera.getProjectionMatrix());
		shader.uploadInteger(shader.getUniformLocation("texture"), 0);
		shader.uploadInteger(shader.getUniformLocation("sceneDepthTexture"), 1);
		shader.uploadVector(shader.getUniformLocation("windowSize"), windowSize);
	}
	
	public void update(float delta, Vector3f cameraPosition) {
		for (int i = 0; i < emitters.size(); i++) {
			emitters.get(i).update(delta, cameraPosition);
		}
	}
	
	public void render(Texture sceneDepth) {
		shader.bind();
		sceneDepth.activeTexture(1);
		sceneDepth.bind();
		for (int i = 0; i < emitters.size(); i++) {
			ParticleEmitter emitter = emitters.get(i);
			shader.uploadVector(shader.getUniformLocation("textureAtlasSize"), emitter.getAtlasSize());
			renderer.setBlendMode(emitter.getBlendMode());
			emitter.store(emitter.getInstancedGeometry(), emitter.getMaxParticles());
			emitter.getInstancedGeometry().bind();
			emitter.getInstancedGeometry().update(emitter.getParticleCount());
			emitter.getTexture().activeTexture(0);
			emitter.getTexture().bind();
			emitter.getInstancedGeometry().render();
			emitter.getTexture().unbind();			
			emitter.getInstancedGeometry().unbind();			
		}
		renderer.setBlendMode(BlendMode.DEFAULT);
		shader.unbind();
	}
	
	public void addEmitter(ParticleEmitter emitter) {
		emitter.create(renderer);
		emitters.add(emitter);
	}

}
