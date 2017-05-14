package engine.scene;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Camera;
import engine.rendering.Framebuffer;
import engine.rendering.Geometry;
import engine.rendering.Renderer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;

public class DeferredRenderingPipeline implements RenderingPipeline {
	
	private Shader defaultGeometryShader, lightingShader;
	
	private Framebuffer geometryPass, lightingPass;
	
	private Geometry lightingFullscreenPass;
	
	private int defaultGeometryShader_viewMatrix, defaultGeometryShader_modelMatrix,
		defaultGeometryShader_projectionMatrix;
	private int lightingShader_viewMatrix, lightingShader_projectionMatrix;
	
	public DeferredRenderingPipeline(Renderer renderer) {
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
		defaultGeometryShader.unbind();
		attributes = new HashMap<>();
		attributes.put(0, "in_Position");
		attributes.put(1, "in_TextureCoord");
		lightingShader = renderer.createShader(getShader("lightingVertex"), getShader("lightingFragment"), attributes);
		lightingShader.bind();
		lightingShader_viewMatrix = lightingShader.getUniformLocation("viewMatrix");
		lightingShader_projectionMatrix = lightingShader.getUniformLocation("projectionMatrix");
		lightingShader.unbind();
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
		renderGeometry(modelMatrix, defaultGeometryShader, camera, defaultEntities, false);
		geometryPass.unbind();
	}
	
	private void renderGeometry(Matrix4f modelMatrix, Shader shader, Camera camera, 
			HashMap<Geometry, HashMap<Material, ArrayList<Entity>>> entities, boolean normalMapped) {
		shader.bind();
		shader.uploadMatrix(defaultGeometryShader_projectionMatrix, camera.getProjectionMatrix());
		shader.uploadMatrix(defaultGeometryShader_viewMatrix, camera.getViewMatrix());
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
					modelMatrix.identity().translationRotateScale(entity.getPosition(), entity.getOrientation(), entity.getScale());
					shader.uploadMatrix(defaultGeometryShader_modelMatrix, modelMatrix);
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
	public void doLightingPass(Camera camera, ArrayList<Light> lights, Vector3f cameraPosition) {
//		lightingPass.bind();
		lightingShader.bind();
		lightingShader.uploadMatrix(lightingShader_projectionMatrix, camera.getProjectionMatrix());
		lightingShader.uploadMatrix(lightingShader_viewMatrix, camera.getViewMatrix());
		int lightColorUniform = lightingShader.getUniformLocation("lightColor");
		int lightPosUniform = lightingShader.getUniformLocation("lightPos");
		int viewPosUniform = lightingShader.getUniformLocation("viewPos");
		int diffuseTextureUniform = lightingShader.getUniformLocation("diffuseTexture");
		int normalTextureUniform = lightingShader.getUniformLocation("normalTexture");
		lightingShader.uploadVector(lightColorUniform, new Vector3f(1, 0, 0));
		lightingShader.uploadVector(lightPosUniform, new Vector3f(0, 0, 0));
		lightingShader.uploadVector(viewPosUniform, cameraPosition);
		lightingShader.uploadInteger(diffuseTextureUniform, 0);
		lightingShader.uploadInteger(normalTextureUniform, 1);
		lightingFullscreenPass.bind();
		geometryPass.getColorTexture(0).bind(0);
		geometryPass.getColorTexture(1).bind(1);
		lightingFullscreenPass.render();
		geometryPass.getColorTexture(0).unbind();
		lightingFullscreenPass.unbind();
		lightingShader.unbind();
//		lightingPass.unbind();
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
