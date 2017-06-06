package engine.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.Renderer;
import engine.rendering.Texture;
import engine.world.World;

public class SceneRenderer {
	
	private RenderingPipeline pipeline;
	
	private float lightLevel = 0.5f;
	
//	private Renderer renderer;
	
	public SceneRenderer(Renderer renderer, RenderingStrategy strategy, CoreSettings coreSettings, GraphicsSettings graphicsSettings) {
//		this.renderer = renderer;
		switch (strategy) {
			case DEFERRED:
				pipeline = new DeferredRenderingPipeline(renderer, coreSettings, graphicsSettings);
				break;
		}
	}
	
	public float getLightLevel() {
		return lightLevel;
	}
	
	public void setLightLevel(float lightLevel) {
		this.lightLevel = lightLevel;
	}
	
	public void render(Camera camera, Vector3f cameraPosition, World world) {
		Matrix4f invViewMatrix = new Matrix4f(), invProjectionMatrix = new Matrix4f();
		camera.getViewMatrix().invert(invViewMatrix);
		camera.getProjectionMatrix().invert(invProjectionMatrix);
		pipeline.doGeometryPass(camera, world.getDefaultEntities(), world.getNormalMappedEntities(),
				world.getDefaultSkeletalEntities(), world.getNormalMappedSkeletalEntities(), 
				world.getTerrain(), world.getSkybox());
		pipeline.doLightingPass(lightLevel, camera, world.getLights(), cameraPosition);
		pipeline.doFXAAPass();
		pipeline.doDOFPass();
		pipeline.doFogPass(world.getSkybox(), invViewMatrix, invProjectionMatrix, cameraPosition);
		pipeline.doFinalRender();
	}

	public Texture getDepthTexture() {
		return pipeline.getSceneDepthTexture();
	}

}
