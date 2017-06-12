package engine.scene;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
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
import engine.rendering.Vertex;
import engine.scene.DeferredRenderingPipeline.PassState;
import engine.world.DirectionalLight;
import engine.world.Light;
import engine.world.PointLight;
import engine.world.SpotLight;
import utils.profiling.GPUProfiler;

public class DeferredLightingPipeline {

	private Shader pointLightShader, spotLightShader, directionalLightShader, hdrShader, bloomShader;

	private Geometry lightingFullscreenPass, postProcessingPass;

	private Framebuffer lightingPass, bloomPass, hdrPass;

	private int pointLightShader_viewMatrix, pointLightShader_projectionMatrix,
	pointLightShader_invViewProjectionMatrix, pointLightShader_near, pointLightShader_far;
	private int spotLightShader_viewMatrix, spotLightShader_projectionMatrix,
	spotLightShader_invViewProjectionMatrix, spotLightShader_near, spotLightShader_far;
	private int directionalLightShader_viewMatrix, directionalLightShader_projectionMatrix,
	directionalLightShader_invViewProjectionMatrix, directionalLightShader_near, directionalLightShader_far;
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
		hdrShader = renderer.createShader(getShader("postVertex"), getShader("hdrFragment"), attributes);
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
		bloomShader = renderer.createShader(getShader("postVertex"), getShader("bloomExtractFragment"), attributes);
		bloomShader.bind();
		bloomShader.uploadInteger(bloomShader.getUniformLocation("diffuseTexture"), 0);
		bloomShader.unbind();
		// General Light Shader
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		lightDefines.put("type", "1");
		pointLightShader = renderer.createShader(getShader("lightingVertex"), getShader("lightingFragment"), attributes, lightDefines);
		pointLightShader.bind();
		pointLightShader_viewMatrix = pointLightShader.getUniformLocation("viewMatrix");
		pointLightShader_projectionMatrix = pointLightShader.getUniformLocation("projectionMatrix");
		pointLightShader_invViewProjectionMatrix = pointLightShader.getUniformLocation("invViewProjectionMatrix");
		pointLightShader_near = pointLightShader.getUniformLocation("near");
		pointLightShader_far = pointLightShader.getUniformLocation("far");
		pointLightShader.unbind();
		lightDefines.put("type", "2");
		spotLightShader = renderer.createShader(getShader("lightingVertex"), getShader("lightingFragment"), attributes, lightDefines);
		spotLightShader.bind();
		spotLightShader_viewMatrix = spotLightShader.getUniformLocation("viewMatrix");
		spotLightShader_projectionMatrix = spotLightShader.getUniformLocation("projectionMatrix");
		spotLightShader_invViewProjectionMatrix = spotLightShader.getUniformLocation("invViewProjectionMatrix");
		spotLightShader_near = spotLightShader.getUniformLocation("near");
		spotLightShader_far = spotLightShader.getUniformLocation("far");
		spotLightShader.unbind();
		lightDefines.put("type", "0");
		directionalLightShader = renderer.createShader(getShader("lightingVertex"), getShader("lightingFragment"), attributes, lightDefines);
		directionalLightShader.bind();
		directionalLightShader_viewMatrix = directionalLightShader.getUniformLocation("viewMatrix");
		directionalLightShader_projectionMatrix = directionalLightShader.getUniformLocation("projectionMatrix");
		directionalLightShader_invViewProjectionMatrix = directionalLightShader.getUniformLocation("invViewProjectionMatrix");
		directionalLightShader_near = directionalLightShader.getUniformLocation("near");
		directionalLightShader_far = directionalLightShader.getUniformLocation("far");
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

	private Vector4f lightPos = new Vector4f(), packedDir = new Vector4f();

	private void renderLights(Camera camera, ArrayList<Light> lights, Vector3f cameraPosition) {
		camera.getProjectionMatrix().invert(inverseProjectionMatrix);
		camera.getViewMatrix().invert(inverseViewMatrix);
		Matrix4f inverseViewProjection = new Matrix4f(inverseViewMatrix).mul(inverseProjectionMatrix);
		getLightUniforms();
		lightingFullscreenPass.bind();

		pointLightShader.bind();
		pointLightShader.uploadMatrix(pointLightShader_projectionMatrix, camera.getProjectionMatrix());
		pointLightShader.uploadMatrix(pointLightShader_viewMatrix, camera.getViewMatrix());
		pointLightShader.uploadMatrix(pointLightShader_invViewProjectionMatrix, inverseViewProjection);
		pointLightShader.uploadFloat(pointLightShader_near, graphicsSettings.near);
		pointLightShader.uploadFloat(pointLightShader_far, graphicsSettings.far);
		pointLightShader.uploadVector(pointLight_viewPosUniform, cameraPosition);
		state.lastPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(1).bind(1);
		state.lastPass.getDepthTexture().bind(2);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof PointLight) {
				PointLight pointLight = (PointLight) light;
				lightPos.set(pointLight.getPosition(), 1f / pointLight.getRange());
				pointLightShader.uploadVector(pointLight_lightColorUniform, pointLight.getColor());
				pointLightShader.uploadVector(pointLight_lightPosUniform, lightPos);
				lightingFullscreenPass.render();
			}
		}
		pointLightShader.unbind();

		spotLightShader.bind();
		spotLightShader.uploadMatrix(spotLightShader_projectionMatrix, camera.getProjectionMatrix());
		spotLightShader.uploadMatrix(spotLightShader_viewMatrix, camera.getViewMatrix());
		spotLightShader.uploadMatrix(spotLightShader_invViewProjectionMatrix, inverseViewProjection);
		spotLightShader.uploadFloat(spotLightShader_near, graphicsSettings.near);
		spotLightShader.uploadFloat(spotLightShader_far, graphicsSettings.far);
		spotLightShader.uploadVector(spotLight_viewPosUniform, cameraPosition);
		state.lastPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(1).bind(1);
		state.lastPass.getDepthTexture().bind(2);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof SpotLight) {
				SpotLight spotLight = (SpotLight) light;
				lightPos.set(spotLight.getPosition(), 1f / spotLight.getRange());
				packedDir.set(spotLight.getDirection(), spotLight.getPackedAngle());
				spotLightShader.uploadVector(spotLight_lightColorUniform, spotLight.getColor());
				spotLightShader.uploadVector(spotLight_lightPosUniform, lightPos);
				spotLightShader.uploadVector(spotLight_spotDirUniform, packedDir);
				lightingFullscreenPass.render();
			}
		}
		spotLightShader.unbind();

		directionalLightShader.bind();
		directionalLightShader.uploadMatrix(directionalLightShader_projectionMatrix, camera.getProjectionMatrix());
		directionalLightShader.uploadMatrix(directionalLightShader_viewMatrix, camera.getViewMatrix());
		directionalLightShader.uploadMatrix(directionalLightShader_invViewProjectionMatrix, inverseViewProjection);
		directionalLightShader.uploadFloat(directionalLightShader_near, graphicsSettings.near);
		directionalLightShader.uploadFloat(directionalLightShader_far, graphicsSettings.far);
		directionalLightShader.uploadVector(directionalLight_viewPosUniform, cameraPosition);
		state.lastPass.getColorTexture(0).bind(0);
		state.lastPass.getColorTexture(1).bind(1);
		state.lastPass.getDepthTexture().bind(2);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			if (light instanceof DirectionalLight) {
				DirectionalLight directionalLight = (DirectionalLight) light;
				lightPos.set(directionalLight.getDirection(), 0);
				directionalLightShader.uploadVector(directionalLight_lightColorUniform, directionalLight.getColor());
				directionalLightShader.uploadVector(directionalLight_lightPosUniform, lightPos);
				lightingFullscreenPass.render();
			}
		}
		directionalLightShader.unbind();

		state.lastPass.getColorTexture(0).unbind();
		lightingFullscreenPass.unbind();
	}

	private int pointLight_lightColorUniform, pointLight_lightPosUniform, pointLight_viewPosUniform;
	private int spotLight_lightColorUniform, spotLight_lightPosUniform, spotLight_viewPosUniform, spotLight_spotDirUniform;
	private int directionalLight_lightColorUniform, directionalLight_lightPosUniform, directionalLight_viewPosUniform;
	private boolean lightUniformsSet = false;

	private void getLightUniforms() {
		if (!lightUniformsSet) {
			lightUniformsSet = true;
			pointLightShader.bind();
			pointLight_lightColorUniform = pointLightShader.getUniformLocation("lightColor");
			pointLight_lightPosUniform = pointLightShader.getUniformLocation("lightPos");
			pointLight_viewPosUniform = pointLightShader.getUniformLocation("viewPos");
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("diffuseTexture"), 0);
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("normalTexture"), 1);
			pointLightShader.uploadInteger(pointLightShader.getUniformLocation("depthTexture"), 2);
			pointLightShader.unbind();
			spotLightShader.bind();
			spotLight_lightColorUniform = spotLightShader.getUniformLocation("lightColor");
			spotLight_lightPosUniform = spotLightShader.getUniformLocation("lightPos");
			spotLight_viewPosUniform = spotLightShader.getUniformLocation("viewPos");
			spotLight_spotDirUniform = spotLightShader.getUniformLocation("lightDirPacked");
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("diffuseTexture"), 0);
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("normalTexture"), 1);
			spotLightShader.uploadInteger(spotLightShader.getUniformLocation("depthTexture"), 2);
			spotLightShader.unbind();
			directionalLightShader.bind();
			directionalLight_lightColorUniform = directionalLightShader.getUniformLocation("lightColor");
			directionalLight_lightPosUniform = directionalLightShader.getUniformLocation("lightPos");
			directionalLight_viewPosUniform = directionalLightShader.getUniformLocation("viewPos");
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("diffuseTexture"), 0);
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("normalTexture"), 1);
			directionalLightShader.uploadInteger(directionalLightShader.getUniformLocation("depthTexture"), 2);
			directionalLightShader.unbind();
		}
	}

}
