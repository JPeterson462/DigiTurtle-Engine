package engine.scene;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.EXTDepthBoundsTest;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import engine.Camera;
import engine.CoreSettings;
import engine.GraphicsSettings;
import engine.rendering.BlendMode;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Vertex;
import engine.scene.DeferredRenderingPipeline.PassState;
import engine.world.DirectionalLight;
import engine.world.Light;
import engine.world.PointLight;
import engine.world.SpotLight;
import utils.SphereGenerator;
import utils.profiling.GPUProfiler;

public class DeferredLightingPipeline {

	private Shader pointLightShader, spotLightShader, directionalLightShader, hdrShader, bloomShader;

	private Geometry lightingFullscreenPass, postProcessingPass, pointLightPass;

	private Framebuffer lightingPass, bloomPass, hdrPass;

	private int pointLightShader_viewMatrix, pointLightShader_projectionMatrix, pointLightShader_modelMatrix,
		pointLightShader_invProjectionMatrix, pointLightShader_radius;
	private int spotLightShader_viewMatrix, spotLightShader_projectionMatrix, spotLightShader_modelMatrix,
		spotLightShader_invProjectionMatrix, spotLightShader_radius;
	private int directionalLightShader_viewMatrix, directionalLightShader_projectionMatrix, directionalLightShader_modelMatrix,
		directionalLightShader_invProjectionMatrix;
	private int hdrShader_ambientLight;

	private Renderer renderer;

	private PassState state;

	private GraphicsSettings graphicsSettings;

	public DeferredLightingPipeline(Renderer renderer, CoreSettings coreSettings, GraphicsSettings graphicsSettings, PassState state, Geometry postProcessingPass) {
		this.state = state;
		this.renderer = renderer;
		this.graphicsSettings = graphicsSettings;
		this.postProcessingPass = postProcessingPass;
		lightingPass = renderer.createFloatingPointFramebuffer(1);
		lightingFullscreenPass = createFullscreenQuad(renderer);
		hdrPass = renderer.createFramebuffer(1);
		bloomPass = renderer.createFloatingPointFramebuffer(coreSettings.width / 2, coreSettings.height / 2, 1);
		HashMap<Integer, String> attributes;
		HashMap<String, String> lightDefines = new HashMap<>();
		// HDR Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		hdrShader = renderer.createShader(getShader("postprocessing/postVertex"), getShader("postprocessing/hdrFragment"), attributes, coreSettings.shaderFinder);
		hdrShader.bind();
		hdrShader.uploadInteger(hdrShader.getUniformLocation("diffuseTexture"), 0);
		hdrShader.uploadInteger(hdrShader.getUniformLocation("bloomTexture"), 1);
		hdrShader.uploadInteger(hdrShader.getUniformLocation("sceneTexture"), 2);
		hdrShader.uploadFloat(hdrShader.getUniformLocation("exposure"), graphicsSettings.hdrExposure);
		hdrShader_ambientLight = hdrShader.getUniformLocation("ambientLight");
		hdrShader.unbind();
		// Bloom Shaders
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		bloomShader = renderer.createShader(getShader("postprocessing/postVertex"), getShader("postprocessing/bloomExtractFragment"), attributes, coreSettings.shaderFinder);
		bloomShader.bind();
		bloomShader.uploadInteger(bloomShader.getUniformLocation("diffuseTexture"), 0);
		bloomShader.unbind();
		// Light Volumes
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		SphereGenerator.generateSphere(vertices, indices);
		pointLightPass = renderer.createGeometry(vertices, indices, Vertex.POSITION_BIT);
		// General Light Shader
		lightDefines.put("maxShininess", coreSettings.maxShininess);
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		lightDefines.put("type", "1");
		pointLightShader = renderer.createShader(getShader("lighting/pointLightVertex"), getShader("lighting/pointLightFragment"), attributes, lightDefines, coreSettings.shaderFinder);
		pointLightShader.bind();
		pointLightShader.uploadVector(pointLightShader.getUniformLocation("screenSize"), new Vector2f(1f / (float) coreSettings.width, 1f / (float) coreSettings.height));
		pointLightShader_radius = pointLightShader.getUniformLocation("radius");
		pointLightShader_modelMatrix = pointLightShader.getUniformLocation("modelMatrix");
		pointLightShader_viewMatrix = pointLightShader.getUniformLocation("viewMatrix");
		pointLightShader_projectionMatrix = pointLightShader.getUniformLocation("projectionMatrix");
		pointLightShader_invProjectionMatrix = pointLightShader.getUniformLocation("invProjectionMatrix");
		pointLightShader.unbind();
		lightDefines.put("type", "2");
		spotLightShader = renderer.createShader(getShader("lighting/spotLightVertex"), getShader("lighting/spotLightFragment"), attributes, lightDefines, coreSettings.shaderFinder);
		spotLightShader.bind();
		spotLightShader.uploadVector(spotLightShader.getUniformLocation("screenSize"), new Vector2f(1f / (float) coreSettings.width, 1f / (float) coreSettings.height));
		spotLightShader_radius = spotLightShader.getUniformLocation("radius");
		spotLightShader_modelMatrix = spotLightShader.getUniformLocation("modelMatrix");
		spotLightShader_viewMatrix = spotLightShader.getUniformLocation("viewMatrix");
		spotLightShader_projectionMatrix = spotLightShader.getUniformLocation("projectionMatrix");
		spotLightShader_invProjectionMatrix = spotLightShader.getUniformLocation("invProjectionMatrix");
		spotLightShader.unbind();
		lightDefines.put("type", "0");
		directionalLightShader = renderer.createShader(getShader("lighting/directionalLightVertex"), getShader("lighting/directionalLightFragment"), attributes, lightDefines, coreSettings.shaderFinder);
		directionalLightShader.bind();
		directionalLightShader_modelMatrix = directionalLightShader.getUniformLocation("modelMatrix");
		directionalLightShader_viewMatrix = directionalLightShader.getUniformLocation("viewMatrix");
		directionalLightShader_projectionMatrix = directionalLightShader.getUniformLocation("projectionMatrix");
		directionalLightShader_invProjectionMatrix = directionalLightShader.getUniformLocation("invProjectionMatrix");
		directionalLightShader.unbind();
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

	public void doLightingPass(float lightLevel, Camera camera, ArrayList<Light> lights, Vector3f cameraPosition, Vector4f ambientLight, Framebuffer geometryPass) {
		// TODO
		GPUProfiler.start("Lighting");
		// TODO
		// Render
		lightingPass.bind();
		renderer.setBlendMode(BlendMode.ADDITIVE);
		renderLights(camera, lights, cameraPosition);
		renderer.setBlendMode(BlendMode.DEFAULT);
		lightingPass.unbind();
		state.lastPass = lightingPass;
		// TODO
		GPUProfiler.end();
		// TODO
		// TODO
		GPUProfiler.start("Bloom and HDR");
		//TODO
		// Bloom
		bloomPass.bind();
		postProcessingPass.bind();
		bloomShader.bind();
		lightingPass.getColorTexture(0).activeTexture(0);
		lightingPass.getColorTexture(0).bind();
		postProcessingPass.render();
		bloomShader.unbind();
		postProcessingPass.unbind();
		bloomPass.unbind();
		state.lastPass = bloomPass;
		//		postProcessingPass.bind();
		//		blurShader.bind();
		//		Texture texture = bloomPass.getColorTexture(0);
		//		blurPassH.bind();
		//		texture.activeTexture(0);
		//		texture.bind();
		//		blurShader.uploadInteger(blurShader_horizontal, 1);
		//		postProcessingPass.render();
		//		blurPassH.unbind();
		//		blurPassV.bind();
		//		blurPassH.getColorTexture(0).activeTexture(0);
		//		blurPassH.getColorTexture(0).bind();
		//		blurShader.uploadInteger(blurShader_horizontal, 0);
		//		postProcessingPass.render();
		//		blurPassV.unbind();
		//		blurShader.unbind();
		//		postProcessingPass.unbind();
		//		lastPass = blurPassV;
		// HDR
		hdrPass.bind();
		postProcessingPass.bind();
		hdrShader.bind();
		hdrShader.uploadVector(hdrShader_ambientLight, ambientLight);
		lightingPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(0).bind(1);
		geometryPass.getColorTexture(0).bind(2);
		postProcessingPass.render();
		hdrShader.unbind();
		postProcessingPass.unbind();
		hdrPass.unbind();
		state.lastPass = hdrPass;
		// TODO
		GPUProfiler.end();
		// TODO
	}

	private Matrix4f inverseProjectionMatrix = new Matrix4f(), inverseViewMatrix = new Matrix4f();
	
	private void renderUnshadowedLight() {
		boolean depthBoundsSupported = GL.getCapabilities().GL_EXT_depth_bounds_test;
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDepthMask(false);
		GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		if (depthBoundsSupported) {
			GL11.glEnable(EXTDepthBoundsTest.GL_DEPTH_BOUNDS_TEST_EXT);
			EXTDepthBoundsTest.glDepthBoundsEXT(graphicsSettings.near, graphicsSettings.far);
			pointLightPass.render();
			GL11.glDisable(EXTDepthBoundsTest.GL_DEPTH_BOUNDS_TEST_EXT);
		} else {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glCullFace(GL11.GL_BACK);
			GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
			GL11.glColorMask(false, false, false, false);
			pointLightPass.render();
			GL11.glCullFace(GL11.GL_FRONT);
			GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);
			GL11.glColorMask(true, true, true, true);
			pointLightPass.render();
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDepthMask(true);
		GL11.glDisable(GL32.GL_DEPTH_CLAMP);
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	private void renderLights(Camera camera, ArrayList<Light> lights, Vector3f cameraPosition) {
		camera.getProjectionMatrix().invert(inverseProjectionMatrix);
		camera.getViewMatrix().invert(inverseViewMatrix);
		getLightUniforms();

		Matrix4f pointLightMatrix = new Matrix4f();
		Matrix4f spotLightMatrix = new Matrix4f();
		Matrix4f directionalLightMatrix = new Matrix4f();
		Quaternionf lightQuaternion = new Quaternionf();

		pointLightPass.bind();
		
		Vector4f pointLightPosition4 = new Vector4f();	
		Vector3f pointLightPosition3 = new Vector3f();
		pointLightShader.bind();
		pointLightShader.uploadMatrix(pointLightShader_projectionMatrix, camera.getProjectionMatrix());
		pointLightShader.uploadMatrix(pointLightShader_viewMatrix, camera.getViewMatrix());
		pointLightShader.uploadMatrix(pointLightShader_invProjectionMatrix, inverseProjectionMatrix);
		pointLightShader.uploadVector(pointLight_viewPosUniform, cameraPosition);
		state.lastPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(1).bind(1);
		state.lastPass.getDepthTexture().bind(2);
		state.lastPass.getColorTexture(2).bind(3);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof PointLight) {
				PointLight pointLight = (PointLight) light;
				pointLightPosition4.set(pointLight.getPosition(), 1);
				camera.getViewMatrix().transform(pointLightPosition4);
				pointLightPosition3.set(pointLightPosition4.x, pointLightPosition4.y, pointLightPosition4.z);
				pointLightMatrix.identity().translationRotateScale(pointLight.getPosition(), lightQuaternion, pointLight.getRange());
				pointLightShader.uploadMatrix(pointLightShader_modelMatrix, pointLightMatrix);
				pointLightShader.uploadFloat(pointLightShader_radius, pointLight.getRange());
				pointLightShader.uploadVector(pointLight_lightColorUniform, pointLight.getColor());
				pointLightShader.uploadVector(pointLight_lightPosUniform, pointLightPosition3);
				renderUnshadowedLight();
			}
		}
		pointLightShader.unbind();

		Vector4f spotLightPosition4 = new Vector4f();	
		Vector3f spotLightPosition3 = new Vector3f();
		spotLightShader.bind();
		spotLightShader.uploadMatrix(spotLightShader_projectionMatrix, camera.getProjectionMatrix());
		spotLightShader.uploadMatrix(spotLightShader_viewMatrix, camera.getViewMatrix());
		spotLightShader.uploadMatrix(spotLightShader_invProjectionMatrix, inverseProjectionMatrix);
		spotLightShader.uploadVector(spotLight_viewPosUniform, cameraPosition);
		state.lastPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(1).bind(1);
		state.lastPass.getDepthTexture().bind(2);
		state.lastPass.getColorTexture(2).bind(3);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof SpotLight) {
				SpotLight spotLight = (SpotLight) light;
				spotLightPosition4.set(spotLight.getPosition(), 1);
				camera.getViewMatrix().transform(spotLightPosition4);
				spotLightPosition3.set(spotLightPosition4.x, spotLightPosition4.y, spotLightPosition4.z);
				spotLightMatrix.identity().translationRotateScale(spotLight.getPosition(), lightQuaternion, spotLight.getRange());
				spotLightShader.uploadMatrix(spotLightShader_modelMatrix, spotLightMatrix);
				spotLightShader.uploadFloat(spotLightShader_radius, spotLight.getRange());
				spotLightShader.uploadFloat(spotLight_rangeUniform, spotLight.getAngle());
				spotLightShader.uploadVector(spotLight_lightColorUniform, spotLight.getColor());
				spotLightShader.uploadVector(spotLight_lightPosUniform, spotLightPosition3);
				spotLightShader.uploadVector(spotLight_spotDirUniform, spotLight.getDirection());
				renderUnshadowedLight();
			}
		}
		spotLightShader.unbind();

		pointLightPass.unbind();
		
		lightingFullscreenPass.bind();
		
		directionalLightShader.bind();
		directionalLightShader.uploadMatrix(directionalLightShader_projectionMatrix, camera.getProjectionMatrix());
		directionalLightShader.uploadMatrix(directionalLightShader_viewMatrix, camera.getViewMatrix());
		directionalLightShader.uploadMatrix(directionalLightShader_invProjectionMatrix, inverseProjectionMatrix);
		directionalLightShader.uploadVector(directionalLight_viewPosUniform, cameraPosition);
		state.lastPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(1).bind(1);
		state.lastPass.getDepthTexture().bind(2);
		state.lastPass.getColorTexture(2).bind(3);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof DirectionalLight) {
				DirectionalLight directionalLight = (DirectionalLight) light;
				directionalLightMatrix.identity();
				directionalLightShader.uploadMatrix(directionalLightShader_modelMatrix, directionalLightMatrix);
				directionalLightShader.uploadVector(directionalLight_lightColorUniform, directionalLight.getColor());
				directionalLightShader.uploadVector(directionalLight_lightDirectionUniform, directionalLight.getDirection());
				lightingFullscreenPass.render();
			}
		}
		directionalLightShader.unbind();

		state.lastPass.getColorTexture(0).unbind();
		lightingFullscreenPass.unbind();
	}

	private int pointLight_lightColorUniform, pointLight_lightPosUniform, pointLight_viewPosUniform;
	private int spotLight_lightColorUniform, spotLight_lightPosUniform, spotLight_viewPosUniform, spotLight_spotDirUniform, spotLight_rangeUniform;
	private int directionalLight_lightColorUniform, directionalLight_lightDirectionUniform, directionalLight_viewPosUniform;
	private boolean lightUniformsSet = false;

	private void getLightUniforms() {
		if (!lightUniformsSet) {
			lightUniformsSet = true;
			pointLightShader.bind();
			pointLight_lightColorUniform = pointLightShader.getUniformLocation("lightColor");
			pointLight_lightPosUniform = pointLightShader.getUniformLocation("lightPosition");
			pointLight_viewPosUniform = pointLightShader.getUniformLocation("eyePosition");
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("diffuseTexture"), 0);
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("normalTexture"), 1);
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("depthTexture"), 2);
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("materialTexture"), 3);
			pointLightShader.unbind();
			spotLightShader.bind();
			spotLight_lightColorUniform = spotLightShader.getUniformLocation("lightColor");
			spotLight_lightPosUniform = spotLightShader.getUniformLocation("lightPosition");
			spotLight_viewPosUniform = spotLightShader.getUniformLocation("eyePosition");
			spotLight_spotDirUniform = spotLightShader.getUniformLocation("lightDirection");
			spotLight_rangeUniform = spotLightShader.getUniformLocation("lightRange");
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("diffuseTexture"), 0);
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("normalTexture"), 1);
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("depthTexture"), 2);
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("materialTexture"), 3);
			spotLightShader.unbind();
			directionalLightShader.bind();
			directionalLight_lightColorUniform = directionalLightShader.getUniformLocation("lightColor");
			directionalLight_lightDirectionUniform = directionalLightShader.getUniformLocation("lightDirection");
			directionalLight_viewPosUniform = directionalLightShader.getUniformLocation("eyePosition");
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("diffuseTexture"), 0);
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("normalTexture"), 1);
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("depthTexture"), 2);
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("materialTexture"), 3);
			directionalLightShader.unbind();
		}
	}

}
