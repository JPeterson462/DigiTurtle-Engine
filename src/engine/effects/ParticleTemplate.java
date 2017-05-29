package engine.effects;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.Camera;
import engine.rendering.InstanceTemplate;
import library.opengl.GLVertexArrayObject;

public class ParticleTemplate implements InstanceTemplate {

	private Camera camera;

	private Matrix4f modelViewMatrix = new Matrix4f();

	private Vector4f textureAtlasOffset = new Vector4f();

	private float blendFactor;

	public ParticleTemplate() {

	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void setModelMatrix(Matrix4f modelMatrix, Vector3f position, float zRotation, float scale) {
		modelViewMatrix.identity();
		Matrix4f viewMatrix = camera.getViewMatrix();
		modelMatrix.identity();
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.translate(position);
		modelMatrix.rotate(zRotation, new Vector3f(0, 0, 1));
		modelMatrix.scale(scale);
		viewMatrix.mul(modelMatrix, modelViewMatrix);
	}

	public void setTextureAtlasOffset(Vector4f textureAtlasOffset) {
		this.textureAtlasOffset = textureAtlasOffset;
	}

	public void setBlendFactor(float blendFactor) {
		this.blendFactor = blendFactor;
	}

	@Override
	public int getAttributeCount() {
		return 6;
	}

	@Override
	public void getAttributes(int[] attributes, int offset, int attributeOffset) {
		attributes[offset + 0] = attributeOffset + 0;
		attributes[offset + 1] = attributeOffset + 1;
		attributes[offset + 2] = attributeOffset + 2;
		attributes[offset + 3] = attributeOffset + 3;
		attributes[offset + 4] = attributeOffset + 4;
		attributes[offset + 5] = attributeOffset + 5;
	}

	@Override
	public int getInstanceSize() {
		return 21;
	}

	@Override
	public void bindAttributes(GLVertexArrayObject vao, int attributeOffset) {
		vao.vertexAttributeFloat(attributeOffset + 0, 4, getInstanceSize() << 2, 0 << 2);
		vao.instanceAttribute(attributeOffset + 0, 1);
		vao.vertexAttributeFloat(attributeOffset + 1, 4, getInstanceSize() << 2, 4 << 2);
		vao.instanceAttribute(attributeOffset + 1, 1);
		vao.vertexAttributeFloat(attributeOffset + 2, 4, getInstanceSize() << 2, 8 << 2);
		vao.instanceAttribute(attributeOffset + 2, 1);
		vao.vertexAttributeFloat(attributeOffset + 3, 4, getInstanceSize() << 2, 12 << 2);
		vao.instanceAttribute(attributeOffset + 3, 1);
		vao.vertexAttributeFloat(attributeOffset + 4, 4, getInstanceSize() << 2, 16 << 2);
		vao.instanceAttribute(attributeOffset + 4, 1);
		vao.vertexAttributeFloat(attributeOffset + 5, 1, getInstanceSize() << 2, 20 << 2);
		vao.instanceAttribute(attributeOffset + 5, 1);
	}

	@Override
	public void uploadInstance(FloatBuffer buffer, int position) {
		modelViewMatrix.get(position, buffer);
		textureAtlasOffset.get(position + 16, buffer);
		buffer.put(position + 20, blendFactor);
	}

}
