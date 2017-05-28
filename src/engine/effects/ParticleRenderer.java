package engine.effects;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.BlendMode;
import engine.rendering.Renderer;
import engine.rendering.Shader;

public class ParticleRenderer {
	
	private Renderer renderer;
	
	private Shader shader;
	
	private ArrayList<ParticleEmitter> emitters = new ArrayList<>();
	
	public ParticleRenderer(Renderer renderer, Camera camera) {
		this.renderer = renderer;
		HashMap<Integer, String> attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		shader = renderer.createShader(getClass().getClassLoader().getResourceAsStream("engine/scene/deferred/particleVertex.glsl"), getClass().getClassLoader().getResourceAsStream("engine/scene/deferred/particleFragment.glsl"), attributes);
		shader.bind();
		shader.uploadMatrix(shader.getUniformLocation("projectionMatrix"), camera.getProjectionMatrix());
		shader.uploadInteger(shader.getUniformLocation("texture"), 0);
	}
	
	public void update(float delta, Vector3f cameraPosition) {
		for (int i = 0; i < emitters.size(); i++) {
			emitters.get(i).update(delta, cameraPosition);
		}
	}
	
	public void render() {
		shader.bind();
		for (int i = 0; i < emitters.size(); i++) {
			ParticleEmitter emitter = emitters.get(i);
			shader.uploadVector(shader.getUniformLocation("textureAtlasSize"), emitter.getAtlasSize());
			emitter.getTexture().activeTexture(0);
			emitter.getTexture().bind();
			renderer.setBlendMode(emitter.getBlendMode());
			emitter.getInstancedGeometry().bind();
			emitter.store(emitter.getInstancedGeometry(), emitter.getMaxParticles());
			emitter.getInstancedGeometry().render(emitter.getParticleCount());
			emitter.getInstancedGeometry().unbind();			
			emitter.getTexture().unbind();			
		}
		renderer.setBlendMode(BlendMode.DEFAULT);
		shader.unbind();
	}
	
	public void addEmitter(ParticleEmitter emitter) {
		emitter.create(renderer);
		emitters.add(emitter);
	}

}
