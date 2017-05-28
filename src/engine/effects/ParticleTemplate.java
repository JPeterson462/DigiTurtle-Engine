package engine.effects;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
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
	
	public void setModelMatrix(Matrix4f modelMatrix, float zRotation) {
		modelViewMatrix.set(camera.getViewMatrix());
		modelViewMatrix.mul(modelMatrix, modelViewMatrix);
		modelViewMatrix.m00(1); modelViewMatrix.m10(0); modelViewMatrix.m20(0);
		modelViewMatrix.m01(0); modelViewMatrix.m11(1); modelViewMatrix.m21(0);
		modelViewMatrix.m02(0); modelViewMatrix.m12(0); modelViewMatrix.m22(1);
		modelViewMatrix.rotateZ((float) Math.toRadians(zRotation));
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
		vao.vertexAttributeFloat(attributeOffset + 1, 4, getInstanceSize() << 2, 1 << 2);
		vao.instanceAttribute(attributeOffset + 1, 1);
		vao.vertexAttributeFloat(attributeOffset + 2, 4, getInstanceSize() << 2, 2 << 2);
		vao.instanceAttribute(attributeOffset + 2, 1);
		vao.vertexAttributeFloat(attributeOffset + 3, 4, getInstanceSize() << 2, 3 << 2);
		vao.instanceAttribute(attributeOffset + 3, 1);
		vao.vertexAttributeFloat(attributeOffset + 4, 4, getInstanceSize() << 2, 4 << 2);
		vao.instanceAttribute(attributeOffset + 4, 1);
		vao.vertexAttributeFloat(attributeOffset + 5, 1, getInstanceSize() << 2, 5 << 2);
		vao.instanceAttribute(attributeOffset + 5, 1);
	}

	@Override
	public void uploadInstance(FloatBuffer buffer, int position) {
		int oldPosition = buffer.position();
		buffer.position(position);
		modelViewMatrix.get(buffer);
		buffer.position(buffer.position() + 16);
		textureAtlasOffset.get(buffer);
		buffer.position(buffer.position() + 4);
		buffer.put(buffer.position(), blendFactor);
		buffer.position(oldPosition);
	}

}
