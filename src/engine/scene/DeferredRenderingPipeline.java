package engine.scene;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import engine.skeleton.SkeletonComponent;
import engine.world.AmbientLight;
import engine.world.Entity;
import engine.world.Light;
import engine.world.Material;
import engine.world.PointLight;

public class DeferredRenderingPipeline implements RenderingPipeline {
	
	private Shader defaultGeometryShader, normalMappedGeometryShader, defaultSkeletalGeometryShader, pointLightShader, ambientLightShader;
	
	private Framebuffer geometryPass, lightingPass;
	
	private Geometry lightingFullscreenPass;
	
	private CoreSettings coreSettings;
	
	private GraphicsSettings graphicsSettings;
	
	private int defaultGeometryShader_viewMatrix, defaultGeometryShader_modelMatrix,
		defaultGeometryShader_projectionMatrix;
	private int defaultSkeletalGeometryShader_viewMatrix, defaultSkeletalGeometryShader_modelMatrix,
		defaultSkeletalGeometryShader_projectionMatrix;
	private int normalMappedGeometryShader_viewMatrix, normalMappedGeometryShader_modelMatrix,
		normalMappedGeometryShader_projectionMatrix;
	private int pointLightShader_viewMatrix, pointLightShader_projectionMatrix,
		pointLightShader_invViewMatrix, pointLightShader_invProjectionMatrix,
		pointLightShader_near, pointLightShader_far;
	private int ambientLightShader_viewMatrix, ambientLightShader_projectionMatrix;
	
	public DeferredRenderingPipeline(Renderer renderer, CoreSettings coreSettings, GraphicsSettings graphicsSettings) {
		this.coreSettings = coreSettings;
		this.graphicsSettings = graphicsSettings;
		geometryPass = renderer.createFramebuffer(2);
		lightingPass = renderer.createFramebuffer(1);
		HashMap<Integer, String> attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		defaultGeometryShader = renderer.createShader(getShader("defaultVertex"), getShader("defaultFragment"), attributes);
		defaultGeometryShader.bind();
		defaultGeometryShader_viewMatrix = defaultGeometryShader.getUniformLocation("viewMatrix");
		defaultGeometryShader_modelMatrix = defaultGeometryShader.getUniformLocation("modelMatrix");
		defaultGeometryShader_projectionMatrix = defaultGeometryShader.getUniformLocation("projectionMatrix");
		defaultGeometryShader.uploadInteger(defaultGeometryShader.getUniformLocation("diffuseTexture"), 0);
		defaultGeometryShader.unbind();
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		normalMappedGeometryShader = renderer.createShader(getShader("defaultVertex"), getShader("normalFragment"), attributes);
		normalMappedGeometryShader.bind();
		normalMappedGeometryShader_viewMatrix = normalMappedGeometryShader.getUniformLocation("viewMatrix");
		normalMappedGeometryShader_modelMatrix = normalMappedGeometryShader.getUniformLocation("modelMatrix");
		normalMappedGeometryShader_projectionMatrix = normalMappedGeometryShader.getUniformLocation("projectionMatrix");
		normalMappedGeometryShader.uploadInteger(normalMappedGeometryShader.getUniformLocation("diffuseTexture"), 0);
		normalMappedGeometryShader.uploadInteger(normalMappedGeometryShader.getUniformLocation("normalTexture"), 1);
		normalMappedGeometryShader.unbind();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		attributes.put(2, "in_Normal");
		attributes.put(3, "in_Joints");
		attributes.put(4, "in_Weights");
		defaultSkeletalGeometryShader = renderer.createShader(getShader("skeletalVertex"), getShader("defaultFragment"), attributes);
		defaultSkeletalGeometryShader.bind();
		defaultSkeletalGeometryShader_viewMatrix = defaultSkeletalGeometryShader.getUniformLocation("viewMatrix");
		defaultSkeletalGeometryShader_modelMatrix = defaultSkeletalGeometryShader.getUniformLocation("modelMatrix");
		defaultSkeletalGeometryShader_projectionMatrix = defaultSkeletalGeometryShader.getUniformLocation("projectionMatrix");
		defaultSkeletalGeometryShader.uploadInteger(defaultSkeletalGeometryShader.getUniformLocation("diffuseTexture"), 0);
		defaultSkeletalGeometryShader.unbind();
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		pointLightShader = renderer.createShader(getShader("lightingVertex"), getShader("lightingPointFragment"), attributes);
		pointLightShader.bind();
		pointLightShader_viewMatrix = pointLightShader.getUniformLocation("viewMatrix");
		pointLightShader_projectionMatrix = pointLightShader.getUniformLocation("projectionMatrix");
		pointLightShader_invViewMatrix = pointLightShader.getUniformLocation("invViewMatrix");
		pointLightShader_invProjectionMatrix = pointLightShader.getUniformLocation("invProjectionMatrix");
		pointLightShader_near = pointLightShader.getUniformLocation("near");
		pointLightShader_far = pointLightShader.getUniformLocation("far");
		pointLightShader.unbind();
		ambientLightShader = renderer.createShader(getShader("lightingVertex"), getShader("lightingAmbientFragment"), attributes);
		ambientLightShader.bind();
		ambientLightShader_viewMatrix = ambientLightShader.getUniformLocation("viewMatrix");
		ambientLightShader_projectionMatrix = ambientLightShader.getUniformLocation("projectionMatrix");
		ambientLightShader.unbind();
		lightingFullscreenPass = createFullscreenQuad(renderer);
	}
	
	private Geometry createFullscreenQuad(Renderer renderer) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.add(new Vertex().position(-1, -1, 0).textureCoord(0, 0));
		vertices.add(new Vertex().position(1, -1, 0).textureCoord(1, 0));
		vertices.add(new Vertex().position(1, 1, 0).textureCoord(1, 1));
		vertices.add(new Vertex().position(-1, 1, 0).textureCoord(0, 1));
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(0);
		indices.add(1);
		indices.add(2);
		indices.add(2);
		indices.add(3);
		indices.add(0);
		return renderer.createGeometry(vertices, indices, Vertex.POSITION_BIT | Vertex.TEXTURE_COORD_BIT);
	}
	
	private InputStream getShader(String name) {
		return getClass().getClassLoader().getResourceAsStream("engine/scene/deferred/" + name + ".glsl");
	}

	@Override
	public void doGeometryPass(Camera camera, HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> defaultSkeletalEntities,
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> normalMappedSkeletalEntities) {
		Matrix4f modelMatrix = new Matrix4f();
		geometryPass.bind();
		renderGeometry(modelMatrix, defaultGeometryShader, camera, defaultEntities, false, false,
				defaultGeometryShader_projectionMatrix, defaultGeometryShader_viewMatrix, defaultGeometryShader_modelMatrix);
		renderGeometry(modelMatrix, normalMappedGeometryShader, camera, normalMappedEntities, true, false, 
				normalMappedGeometryShader_projectionMatrix, normalMappedGeometryShader_viewMatrix, normalMappedGeometryShader_modelMatrix);
		renderGeometry(modelMatrix, defaultSkeletalGeometryShader, camera, defaultSkeletalEntities, false, true,
				defaultSkeletalGeometryShader_projectionMatrix, defaultSkeletalGeometryShader_viewMatrix, defaultSkeletalGeometryShader_modelMatrix);
		geometryPass.unbind();
	}
	
	private void renderGeometry(Matrix4f modelMatrix, Shader shader, Camera camera, 
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> entities, boolean normalMapped, boolean skeletal,
			int projectionMatrixLocation, int viewMatrixLocation, int modelMatrixLocation) {
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
	public void doLightingPass(float lightLevel, Camera camera, ArrayList<Light> lights, Vector3f cameraPosition) {
//		lightingPass.bind();
		renderAmbientLights(lightLevel, ambientLightShader, camera, lights);
		renderPointLights(pointLightShader, camera, lights, cameraPosition);
//		lightingPass.unbind();
	}
	
	private Matrix4f inverseProjectionMatrix = new Matrix4f(), inverseViewMatrix = new Matrix4f();
	private int lightColorUniform, lightPosUniform, viewPosUniform, lightRadiusUniform, diffuseTextureUniform, 
		normalTextureUniform, depthTextureUniform;
	private boolean pointLightUniformsSet = false;
	
	private void renderPointLights(Shader lightingShader, Camera camera, ArrayList<Light> lights, Vector3f cameraPosition) {
		camera.getProjectionMatrix().invert(inverseProjectionMatrix);
		camera.getViewMatrix().invert(inverseViewMatrix);
		lightingShader.bind();
		lightingShader.uploadMatrix(pointLightShader_projectionMatrix, camera.getProjectionMatrix());
		lightingShader.uploadMatrix(pointLightShader_viewMatrix, camera.getViewMatrix());
		lightingShader.uploadMatrix(pointLightShader_invProjectionMatrix, inverseProjectionMatrix);
		lightingShader.uploadMatrix(pointLightShader_invViewMatrix, inverseViewMatrix);
		lightingShader.uploadFloat(pointLightShader_near, graphicsSettings.near);
		lightingShader.uploadFloat(pointLightShader_far, graphicsSettings.far);
		if (!pointLightUniformsSet) {
			pointLightUniformsSet = true;
			lightColorUniform = lightingShader.getUniformLocation("lightColor");
			lightPosUniform = lightingShader.getUniformLocation("lightPos");
			viewPosUniform = lightingShader.getUniformLocation("viewPos");
			lightRadiusUniform = lightingShader.getUniformLocation("lightRadius");
			diffuseTextureUniform = lightingShader.getUniformLocation("diffuseTexture");
			normalTextureUniform = lightingShader.getUniformLocation("normalTexture");
			depthTextureUniform = lightingShader.getUniformLocation("depthTexture");
			lightingShader.uploadInteger(diffuseTextureUniform, 0);
			lightingShader.uploadInteger(normalTextureUniform, 1);
			lightingShader.uploadInteger(depthTextureUniform, 2);
		}
		lightingShader.uploadVector(viewPosUniform, cameraPosition);
		lightingFullscreenPass.bind();
		geometryPass.getColorTexture(0).bind(0);
		geometryPass.getColorTexture(1).bind(1);
		geometryPass.getDepthTexture().bind(2);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof PointLight) {
				PointLight pointLight = (PointLight) light;
				lightingShader.uploadVector(lightColorUniform, pointLight.getColor());
				lightingShader.uploadVector(lightPosUniform, pointLight.getPosition());
				lightingShader.uploadFloat(lightRadiusUniform, pointLight.getRange());
				lightingFullscreenPass.render();
			}
		}
		geometryPass.getColorTexture(0).unbind();
		lightingFullscreenPass.unbind();
		lightingShader.unbind();
	}
	
	private Vector4f ambientLightColor = new Vector4f();
	private void renderAmbientLights(float lightLevel, Shader lightingShader, Camera camera, ArrayList<Light> lights) {
		lightingShader.bind();
		lightingShader.uploadMatrix(ambientLightShader_projectionMatrix, camera.getProjectionMatrix());
		lightingShader.uploadMatrix(ambientLightShader_viewMatrix, camera.getViewMatrix());
		int lightColorUniform = lightingShader.getUniformLocation("lightColor");
		int diffuseTextureUniform = lightingShader.getUniformLocation("diffuseTexture");
		lightingShader.uploadInteger(diffuseTextureUniform, 0);
		lightingFullscreenPass.bind();
		geometryPass.getColorTexture(0).bind(0);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof AmbientLight) {
				AmbientLight ambientLight = (AmbientLight) light;
				ambientLightColor.set(ambientLight.getColor(), lightLevel);
				lightingShader.uploadVector(lightColorUniform, ambientLightColor);
				lightingFullscreenPass.render();
			}
		}
		geometryPass.getColorTexture(0).unbind();		
		lightingFullscreenPass.unbind();
		lightingShader.unbind();
	}

	@Override
	public void doFinalRender() {
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, geometryPass.getDepthTexture().getID());
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(-1, -1);
//		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(1, -1);
//		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(1, 1);
//		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(-1, 1);
//		GL11.glEnd();
	}

}
