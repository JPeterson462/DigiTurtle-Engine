package engine.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.Renderer;
import engine.rendering.Texture;
import engine.world.World;
import utils.profiling.GPUProfiler;
import utils.profiling.GPUTaskProfile;

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
		// TODO
		GPUProfiler.startFrame();
		// TODO
		pipeline.doGeometryPass(camera, world.getDefaultEntities(), world.getNormalMappedEntities(),
				world.getDefaultSkeletalEntities(), world.getNormalMappedSkeletalEntities(), 
				world.getTerrain(), world.getSkybox());
		pipeline.doLightingPass(lightLevel, camera, world.getLights(), cameraPosition);
		pipeline.doFXAAPass();
		pipeline.doDOFPass();
		pipeline.doFogPass(world.getSkybox());
		pipeline.doFinalRender();
		// TODO
		GPUProfiler.endFrame();

		GPUTaskProfile tp;
		while((tp = GPUProfiler.getFrameResults()) != null){

			tp.dump(); //Dumps the frame to System.out.
			//or use the functions of GPUTaskProfile to extract information about the frame:
			//getName(), getStartTime(), getEndTime(), getTimeTaken(), getChildren()
			//Then you can draw the result as fancy graphs or something.

			GPUProfiler.recycle(tp); //Recycles GPUTaskProfile instances and their OpenGL query objects.
		}
		// TODO
	}

	public Texture getDepthTexture() {
		return pipeline.getSceneDepthTexture();
	}

}
