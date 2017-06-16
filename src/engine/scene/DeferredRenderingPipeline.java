package engine.scene;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.BlendMode;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.skeleton.SkeletonComponent;
import engine.world.Entity;
import engine.world.Light;
import engine.world.Material;
import engine.world.Skybox;
import engine.world.TerrainChunk;
import engine.world.TerrainTexturePack;
import utils.CubeGenerator;
import utils.profiling.GPUProfiler;

public class DeferredRenderingPipeline implements RenderingPipeline {
	
	private Shader defaultGeometryShader, normalMappedGeometryShader, 
		defaultSkeletalGeometryShader, normalMappedSkeletalGeometryShader, 
		fxaaShader, terrainShader, blurShader, finalShader, environmentShader, skyShader;
	
	private Framebuffer geometryPass, fxaaPass, environmentPass, skyPass;
	
	private Geometry postProcessingPass;
	
	private int defaultGeometryShader_viewMatrix, defaultGeometryShader_modelMatrix,
		defaultGeometryShader_projectionMatrix, defaultGeometryShader_shininess, defaultGeometryShader_specularFactor;
	private int defaultSkeletalGeometryShader_viewMatrix, defaultSkeletalGeometryShader_modelMatrix,
		defaultSkeletalGeometryShader_projectionMatrix, defaultSkeletalGeometryShader_shininess, defaultSkeletalGeometryShader_specularFactor;
	private int normalMappedGeometryShader_viewMatrix, normalMappedGeometryShader_modelMatrix,
		normalMappedGeometryShader_projectionMatrix, normalMappedGeometryShader_shininess, normalMappedGeometryShader_specularFactor;
	private int normalMappedSkeletalGeometryShader_viewMatrix, normalMappedSkeletalGeometryShader_modelMatrix,
		normalMappedSkeletalGeometryShader_projectionMatrix, normalMappedSkeletalGeometryShader_shininess, normalMappedSkeletalGeometryShader_specularFactor;
	private int terrainShader_mvpMatrix, terrainShader_shininess, terrainShader_specularFactor;
	private int environmentShader_fogDensity, environmentShader_fogDistance, environmentShader_fogColor;
	private int skyShader_blendFactor, skyShader_projectionMatrix, skyShader_viewMatrix, 
		skyShader_modelMatrix;
	
	private DeferredLightingPipeline lightingPipeline;
	
	private Renderer renderer;
	
	private Geometry skyCube;
	
	private CubeGenerator cubeGenerator = new CubeGenerator();
	
	private PassState state = new PassState();
	
	class PassState {
		public Framebuffer lastPass;
	}
	
	public DeferredRenderingPipeline(Renderer renderer, CoreSettings coreSettings, GraphicsSettings graphicsSettings) {
		this.renderer = renderer;
		// Fullscreen Pass
		postProcessingPass = createPostProcessingPass(renderer);
		// Framebuffers and Pipelines
		lightingPipeline = new DeferredLightingPipeline(renderer, coreSettings, graphicsSettings, state, postProcessingPass);
		geometryPass = renderer.createFramebuffer(3);
		fxaaPass = renderer.createFramebuffer(1);
		environmentPass = renderer.createFramebuffer(1);
		skyPass = renderer.createFramebuffer(1);
		// Miscellaneous
		HashMap<String, String> geometryDefines = new HashMap<>();
		geometryDefines.put("maxShininess", coreSettings.maxShininess);
		// Default Geometry
		HashMap<Integer, String> attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		defaultGeometryShader = renderer.createShader(getShader("defaultVertex"), getShader("defaultFragment"), attributes, geometryDefines, coreSettings.shaderFinder);
		defaultGeometryShader.bind();
		defaultGeometryShader_viewMatrix = defaultGeometryShader.getUniformLocation("viewMatrix");
		defaultGeometryShader_modelMatrix = defaultGeometryShader.getUniformLocation("modelMatrix");
		defaultGeometryShader_projectionMatrix = defaultGeometryShader.getUniformLocation("projectionMatrix");
		defaultGeometryShader_shininess = defaultGeometryShader.getUniformLocation("shininess");
		defaultGeometryShader_specularFactor = defaultGeometryShader.getUniformLocation("specularFactor");
		defaultGeometryShader.uploadInteger(defaultGeometryShader.getUniformLocation("diffuseTexture"), 0);
		defaultGeometryShader.unbind();
		// Normal Mapped Geometry
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		normalMappedGeometryShader = renderer.createShader(getShader("defaultVertex"), getShader("normalFragment"), attributes, geometryDefines, coreSettings.shaderFinder);
		normalMappedGeometryShader.bind();
		normalMappedGeometryShader_viewMatrix = normalMappedGeometryShader.getUniformLocation("viewMatrix");
		normalMappedGeometryShader_modelMatrix = normalMappedGeometryShader.getUniformLocation("modelMatrix");
		normalMappedGeometryShader_projectionMatrix = normalMappedGeometryShader.getUniformLocation("projectionMatrix");
		normalMappedGeometryShader_shininess = normalMappedGeometryShader.getUniformLocation("shininess");
		normalMappedGeometryShader_specularFactor = normalMappedGeometryShader.getUniformLocation("specularFactor");
		normalMappedGeometryShader.uploadInteger(normalMappedGeometryShader.getUniformLocation("diffuseTexture"), 0);
		normalMappedGeometryShader.uploadInteger(normalMappedGeometryShader.getUniformLocation("normalTexture"), 1);
		normalMappedGeometryShader.unbind();
		// Skeletal Geometry
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		attributes.put(3, "in_Joints");
		attributes.put(4, "in_Weights");
		defaultSkeletalGeometryShader = renderer.createShader(getShader("skeletalVertex"), getShader("defaultFragment"), attributes, geometryDefines, coreSettings.shaderFinder);
		defaultSkeletalGeometryShader.bind();
		defaultSkeletalGeometryShader_viewMatrix = defaultSkeletalGeometryShader.getUniformLocation("viewMatrix");
		defaultSkeletalGeometryShader_modelMatrix = defaultSkeletalGeometryShader.getUniformLocation("modelMatrix");
		defaultSkeletalGeometryShader_projectionMatrix = defaultSkeletalGeometryShader.getUniformLocation("projectionMatrix");
		defaultSkeletalGeometryShader_shininess = defaultSkeletalGeometryShader.getUniformLocation("shininess");
		defaultSkeletalGeometryShader_specularFactor = defaultSkeletalGeometryShader.getUniformLocation("specularFactor");
		defaultSkeletalGeometryShader.uploadInteger(defaultSkeletalGeometryShader.getUniformLocation("diffuseTexture"), 0);
		defaultSkeletalGeometryShader.unbind();
		// Normal Mapped Skeletal
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		attributes.put(3, "in_Joints");
		attributes.put(4, "in_Weights");
		normalMappedSkeletalGeometryShader = renderer.createShader(getShader("skeletalVertex"), getShader("normalFragment"), attributes, geometryDefines, coreSettings.shaderFinder);
		normalMappedSkeletalGeometryShader.bind();
		normalMappedSkeletalGeometryShader_viewMatrix = normalMappedSkeletalGeometryShader.getUniformLocation("viewMatrix");
		normalMappedSkeletalGeometryShader_modelMatrix = normalMappedSkeletalGeometryShader.getUniformLocation("modelMatrix");
		normalMappedSkeletalGeometryShader_projectionMatrix = normalMappedSkeletalGeometryShader.getUniformLocation("projectionMatrix");
		normalMappedSkeletalGeometryShader_shininess = normalMappedSkeletalGeometryShader.getUniformLocation("shininess");
		normalMappedSkeletalGeometryShader_specularFactor = normalMappedSkeletalGeometryShader.getUniformLocation("specularFactor");
		normalMappedSkeletalGeometryShader.uploadInteger(normalMappedSkeletalGeometryShader.getUniformLocation("diffuseTexture"), 0);
		normalMappedSkeletalGeometryShader.unbind();
		// FXAA Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		fxaaShader = renderer.createShader(getShader("postprocessing/postVertex"), getShader("postprocessing/fxaaFragment"), attributes, coreSettings.shaderFinder);
		fxaaShader.bind();
		fxaaShader.uploadInteger(fxaaShader.getUniformLocation("diffuseTexture"), 0);
		fxaaShader.uploadVector(fxaaShader.getUniformLocation("resolution"), new Vector2f(coreSettings.width, coreSettings.height));
		fxaaShader.unbind();
		// Terrain Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		terrainShader = renderer.createShader(getShader("terrainVertex"), getShader("terrainFragment"), attributes, geometryDefines, coreSettings.shaderFinder);
		terrainShader.bind();
		terrainShader_mvpMatrix = terrainShader.getUniformLocation("mvpMatrix");
		terrainShader_shininess = terrainShader.getUniformLocation("shininess");
		terrainShader_specularFactor = terrainShader.getUniformLocation("specularFactor");
		TerrainTexturePack texturePack = new TerrainTexturePack(null, null, null, null, null);
		texturePack.connect(terrainShader);
		terrainShader.unbind();
		skyCube = renderer.createGeometry(cubeGenerator.generateCube(1), Vertex.POSITION_BIT);
		// Blur Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		blurShader = renderer.createShader(getShader("postprocessing/postVertex"), getShader("postprocessing/blurFragment"), attributes, coreSettings.shaderFinder);
		blurShader.bind();
//		blurShader_horizontal = blurShader.getUniformLocation("horizontal");
		blurShader.uploadInteger(blurShader.getUniformLocation("diffuseTexture"), 0);
		blurShader.uploadVector(blurShader.getUniformLocation("resolution"), new Vector2f(coreSettings.width, coreSettings.height));
		blurShader.unbind();
		// Final Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		finalShader = renderer.createShader(getShader("postprocessing/postVertex"), getShader("basicFragment"), attributes, coreSettings.shaderFinder);
		finalShader.bind();
		finalShader.uploadInteger(finalShader.getUniformLocation("diffuseTexture"), 0);
		finalShader.unbind();
		// Environment Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		environmentShader = renderer.createShader(getShader("postprocessing/postVertex"), getShader("postprocessing/environmentFragment"), attributes, coreSettings.shaderFinder);
		environmentShader.bind();
		environmentShader.uploadInteger(environmentShader.getUniformLocation("diffuseTexture"), 0);
		environmentShader.uploadInteger(environmentShader.getUniformLocation("depthTexture"), 1);
		environmentShader.uploadInteger(environmentShader.getUniformLocation("skyTexture"), 2);
		environmentShader.uploadVector(environmentShader.getUniformLocation("nearFar"), new Vector2f(graphicsSettings.near, graphicsSettings.far));
		environmentShader.uploadVector(environmentShader.getUniformLocation("resolution"), new Vector2f(coreSettings.width, coreSettings.height));
		environmentShader.uploadFloat(environmentShader.getUniformLocation("focusDistance"), graphicsSettings.dofDistance);
		environmentShader.uploadFloat(environmentShader.getUniformLocation("focusRange"), graphicsSettings.dofRange);
		environmentShader_fogDensity = environmentShader.getUniformLocation("fogDensity");
		environmentShader_fogDistance = environmentShader.getUniformLocation("fogDistance");
		environmentShader_fogColor = environmentShader.getUniformLocation("fogColor");
		environmentShader.unbind();
		// Sky Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		skyShader = renderer.createShader(getShader("skyVertex"), getShader("skyFragment"), attributes, coreSettings.shaderFinder);
		skyShader.bind();
		skyShader.uploadInteger(skyShader.getUniformLocation("cubeMap1"), 0);
		skyShader.uploadInteger(skyShader.getUniformLocation("cubeMap2"), 1);
		skyShader_blendFactor = skyShader.getUniformLocation("blendFactor");
		skyShader_projectionMatrix = skyShader.getUniformLocation("projectionMatrix");
		skyShader_viewMatrix = skyShader.getUniformLocation("viewMatrix");
		skyShader_modelMatrix = skyShader.getUniformLocation("modelMatrix");
		skyShader.unbind();
	}
	
	private Geometry createPostProcessingPass(Renderer renderer) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex().position(-1, -1, 0));
		vertices.add(new Vertex().position(1, -1, 0));
		vertices.add(new Vertex().position(1, 1, 0));
		vertices.add(new Vertex().position(-1, 1, 0));
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);
		indices.add(1);
		indices.add(2);
		indices.add(2);
		indices.add(3);
		indices.add(0);
		return renderer.createGeometry(vertices, indices, Vertex.POSITION2D_BIT);
	}
	
	private InputStream getShader(String name) {
		return getClass().getClassLoader().getResourceAsStream("engine/scene/deferred/" + name + ".glsl");
	}

	@Override
	public void doGeometryPass(Camera camera, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities, 
			TerrainChunk[][] terrain, Skybox skybox) {
		//TODO
		GPUProfiler.start("Geometry");
		//TODO
		renderer.setBlendMode(BlendMode.OVERWRITE);
		Matrix4f modelMatrix = new Matrix4f();
		GPUProfiler.start("Skybox");
		modelMatrix.rotateY(skybox.getRotation()).scale(500);
		skyPass.bind();
		skyShader.bind();
		skyShader.uploadMatrix(skyShader_projectionMatrix, camera.getProjectionMatrix());
		skyShader.uploadMatrix(skyShader_viewMatrix, camera.getViewMatrix());
		skyShader.uploadMatrix(skyShader_modelMatrix, modelMatrix);
		skyShader.uploadFloat(skyShader_blendFactor, skybox.getBlendFactor());
		skyCube.bind();
		skybox.getTexture1().bind(0);
		skybox.getTexture2().bind(1);
		skyCube.render();
		skyCube.unbind();
		skyShader.unbind();
		skyPass.unbind();
		GPUProfiler.end();
		// skybox
		modelMatrix.identity();
		geometryPass.bind();
		renderTerrain(modelMatrix, terrainShader, camera, terrain, terrainShader_mvpMatrix);
		renderGeometry(modelMatrix, defaultGeometryShader, camera, defaultEntities, false, false,
				defaultGeometryShader_projectionMatrix, defaultGeometryShader_viewMatrix, defaultGeometryShader_modelMatrix,
				defaultGeometryShader_shininess, defaultGeometryShader_specularFactor);
		renderGeometry(modelMatrix, normalMappedGeometryShader, camera, normalMappedEntities, true, false, 
				normalMappedGeometryShader_projectionMatrix, normalMappedGeometryShader_viewMatrix, normalMappedGeometryShader_modelMatrix,
				normalMappedGeometryShader_shininess, normalMappedGeometryShader_specularFactor);
		renderGeometry(modelMatrix, defaultSkeletalGeometryShader, camera, defaultSkeletalEntities, false, true,
				defaultSkeletalGeometryShader_projectionMatrix, defaultSkeletalGeometryShader_viewMatrix, defaultSkeletalGeometryShader_modelMatrix,
				defaultSkeletalGeometryShader_shininess, defaultSkeletalGeometryShader_specularFactor);
		renderGeometry(modelMatrix, normalMappedSkeletalGeometryShader, camera, normalMappedSkeletalEntities, true, true,
				normalMappedSkeletalGeometryShader_projectionMatrix, normalMappedSkeletalGeometryShader_viewMatrix, normalMappedSkeletalGeometryShader_modelMatrix,
				normalMappedSkeletalGeometryShader_shininess, normalMappedSkeletalGeometryShader_specularFactor);
		geometryPass.unbind();
		state.lastPass = geometryPass;
		renderer.setBlendMode(BlendMode.DEFAULT);
		// TODO
		GPUProfiler.end();
		// TODO
	}

	private Matrix4f nullMatrix = new Matrix4f();
	
	private void renderTerrain(Matrix4f modelMatrix, Shader shader, Camera camera, TerrainChunk[][] terrain, int mvpMatrixLocation) {
		//TODO
		GPUProfiler.start("Terrain");
		//TODO
		shader.bind();
		Matrix4f mvp = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(nullMatrix);
		shader.uploadMatrix(mvpMatrixLocation, mvp);
		TerrainTexturePack texturePack = null;
		Geometry geometry = null;
		for (int i = 0; i < terrain.length; i++) {
			for (int j = 0; j < terrain[0].length; j++) {
				GPUProfiler.start("Terrain Chunk " + (i * terrain[0].length + j));
				TerrainChunk chunk = terrain[i][j];
				shader.uploadFloat(terrainShader_shininess, chunk.getShininess());
				shader.uploadFloat(terrainShader_specularFactor, chunk.getSpecularFactor());
				geometry = chunk.getGeometry(renderer);
				geometry.bind();
				TerrainTexturePack oldTexturePack = texturePack;
				texturePack = chunk.getTexturePack();
				if (oldTexturePack == null || oldTexturePack.hashCode() != texturePack.hashCode()) {
					GPUProfiler.start("Texture Pack");
					texturePack.bind();
					GPUProfiler.end();
				}
				geometry.render();
				GPUProfiler.end();
			}
		}
		if (geometry != null) {
			geometry.unbind();
		}
		if (texturePack != null) {
			texturePack.unbind();
		}
		shader.unbind();
		// TODO
		GPUProfiler.end();
		// TODO
	}
	
	private void renderGeometry(Matrix4f modelMatrix, Shader shader, Camera camera, 
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> entities, boolean normalMapped, boolean skeletal,
			int projectionMatrixLocation, int viewMatrixLocation, int modelMatrixLocation, int shininessLocation, int specularFactorLocation) {
		shader.bind();
		shader.uploadMatrix(projectionMatrixLocation, camera.getProjectionMatrix());
		shader.uploadMatrix(viewMatrixLocation, camera.getViewMatrix());
		for (Map.Entry<Geometry, HashMap<Material, ArrayList<Entity>>> entitiesGeometry : entities.entrySet()) {
			Geometry geometry = entitiesGeometry.getKey();
			geometry.bind();
			for (Map.Entry<Material, ArrayList<Entity>> entitiesMaterial : entitiesGeometry.getValue().entrySet()) {
				Texture diffuseTexture = entitiesMaterial.getKey().getDiffuseTexture();
				diffuseTexture.activeTexture(0);
				diffuseTexture.bind();
				Texture normalTexture = null;
				if (normalMapped) {
					normalTexture = entitiesMaterial.getKey().getNormalTexture();
					normalTexture.activeTexture(1);
					normalTexture.bind();
				}
				shader.uploadFloat(shininessLocation, entitiesMaterial.getKey().getShininess());
				shader.uploadFloat(specularFactorLocation, entitiesMaterial.getKey().getSpecularFactor());
				//TODO upload lighting constants
				for (Entity entity : entitiesMaterial.getValue()) {
					if (skeletal) {
						Matrix4f[] jointTransforms = entity.getComponent(SkeletonComponent.class).getSkeleton().getJointTransforms();
						for (int i = 0; i < jointTransforms.length; i++) {
							shader.uploadMatrix(shader.getUniformLocation("jointTransforms[" + i + "]"), jointTransforms[i]);
						}
					}
					modelMatrix.identity().translationRotateScale(entity.getPosition(), entity.getOrientation(), entity.getScale());
					shader.uploadMatrix(modelMatrixLocation, modelMatrix);
					geometry.render();
				}
				diffuseTexture.unbind();
				if (normalTexture != null) {
					normalTexture.unbind();
				}
			}
			geometry.unbind();
		}
		shader.unbind();
	}
	
	@Override
	public void doLightingPass(float lightLevel, Camera camera, ArrayList<Light> lights, Vector3f cameraPosition, Vector4f ambientLight) {
		lightingPipeline.doLightingPass(lightLevel, camera, lights, cameraPosition, ambientLight, geometryPass);
	}

	@Override
	public void doEnvironmentPass(Skybox skybox) {
		// TODO rotate skybox
		GPUProfiler.start("Environment");
		environmentPass.bind();
		postProcessingPass.bind();
		environmentShader.bind();
		state.lastPass.getColorTexture(0).bind(0);
		getSceneDepthTexture().bind(1);
		skyPass.getColorTexture(0).bind(2);
		environmentShader.uploadVector(environmentShader_fogColor, skybox.getFogColor());
		environmentShader.uploadFloat(environmentShader_fogDensity, skybox.getFogDensity());
		environmentShader.uploadFloat(environmentShader_fogDistance, skybox.getFogDistance());
		postProcessingPass.render();
		environmentShader.unbind();
		postProcessingPass.unbind();
		environmentPass.unbind();
		state.lastPass = environmentPass;
		GPUProfiler.end();
	}
	
	@Override
	public void doFXAAPass() {
		//TODO
		GPUProfiler.start("FXAA");
		//TODO
		fxaaPass.bind();
		postProcessingPass.bind();
		fxaaShader.bind();
		state.lastPass.getColorTexture(0).activeTexture(0);
		state.lastPass.getColorTexture(0).bind();
		postProcessingPass.render();
		fxaaShader.unbind();
		postProcessingPass.unbind();
		fxaaPass.unbind();
		state.lastPass = fxaaPass;
		// TODO
		GPUProfiler.end();
		// TODO
	}

	@Override
	public void doFinalRender() {
		renderer.setBlendMode(BlendMode.DEFAULT);

		GPUProfiler.start("Final Render");
		
		finalShader.bind();
		postProcessingPass.bind();
		state.lastPass.getColorTexture(0).activeTexture(0);;
		state.lastPass.getColorTexture(0).bind();
		postProcessingPass.render();
		postProcessingPass.unbind();
		finalShader.unbind();
		

		GPUProfiler.end();
	}

	@Override
	public Texture getSceneDepthTexture() {
		return geometryPass.getDepthTexture();
	}

}
