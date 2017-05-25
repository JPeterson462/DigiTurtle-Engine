package engine.scene;

import org.joml.Vector3f;

import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.Renderer;
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
		pipeline.doGeometryPass(camera, world.getDefaultEntities(), world.getNormalMappedEntities(),
				world.getDefaultSkeletalEntities(), world.getNormalMappedSkeletalEntities(), world.getTerrain());
		pipeline.doLightingPass(lightLevel, camera, world.getLights(), cameraPosition);
		pipeline.doFXAAPass();
		pipeline.doFinalRender();
	}

}
