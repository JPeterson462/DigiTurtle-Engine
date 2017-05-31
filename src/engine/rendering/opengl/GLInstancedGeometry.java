package engine.rendering.opengl;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

import engine.rendering.InstanceTemplate;
import engine.rendering.InstancedGeometry;
import engine.rendering.Vertex;
import library.opengl.GLVertexArrayObject;
import library.opengl.GLVertexBufferObject;

public class GLInstancedGeometry<T extends InstanceTemplate> implements InstancedGeometry<T> {

	private GLVertexArrayObject vao;
	
	private GLVertexBufferObject dataVbo, elementVbo, instanceVbo;
	
	private int[] attributes, instanceAttributes;
	
	private int vertexCount;
	
	private int drawCall;
	
	private int flags, vertexSize;
	
	private T[] instances;
	
	private FloatBuffer dataBuffer, instanceBuffer;
	
	private IntBuffer indexBuffer;
	
	private boolean updateInstances = true;
	
	private Class<T> type;
	
	private InstanceTemplate template;
	
	private int instanceCount;
	
	@SuppressWarnings("unchecked")
	public GLInstancedGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags, Class<T> type, int maxInstances) {
		this.type = type;
		this.flags = flags;
		drawCall = GL11.GL_TRIANGLES;
		vao = new GLVertexArrayObject();
		vertexSize = GLGeometryUtils.getVertexSize(flags);
		attributes = GLGeometryUtils.getAttributes(flags);
		dataBuffer = BufferUtils.createFloatBuffer(vertices.size() * vertexSize);
		dataBuffer.limit(dataBuffer.capacity());
		for (int i = 0; i < vertices.size(); i++) {
			int position = i * vertexSize;
			Vertex vertex = vertices.get(i);
			if ((flags & Vertex.POSITION_BIT) != 0) {
				dataBuffer.put(position + 0, vertex.position().x);
				dataBuffer.put(position + 1, vertex.position().y);
				dataBuffer.put(position + 2, vertex.position().z);
				position += 3;
			}
			if ((flags & Vertex.POSITION2D_BIT) != 0) {
				dataBuffer.put(position + 0, vertex.position().x);
				dataBuffer.put(position + 1, vertex.position().y);
				position += 2;				
			}
			if ((flags & Vertex.TEXTURE_COORD_BIT) != 0) {
				dataBuffer.put(position + 0, vertex.textureCoord().x);
				dataBuffer.put(position + 1, vertex.textureCoord().y);
				position += 2;
			}
			if ((flags & Vertex.NORMAL_BIT) != 0) {
				dataBuffer.put(position + 0, vertex.normal().x);
				dataBuffer.put(position + 1, vertex.normal().y);
				dataBuffer.put(position + 2, vertex.normal().z);
				position += 3;
			}
			if ((flags & Vertex.JOINTID_BIT) != 0) {
				dataBuffer.put(position + 0, vertex.jointIDs().x);
				dataBuffer.put(position + 1, vertex.jointIDs().y);
				dataBuffer.put(position + 2, vertex.jointIDs().z);
				position += 3;
			}
			if ((flags & Vertex.WEIGHT_BIT) != 0) {
				dataBuffer.put(position + 0, vertex.weights().x);
				dataBuffer.put(position + 1, vertex.weights().y);
				dataBuffer.put(position + 2, vertex.weights().z);
				position += 3;
			}
		}
		dataVbo = new GLVertexBufferObject(GL15.GL_ARRAY_BUFFER);
		dataVbo.bind();
		dataVbo.bufferData(dataBuffer, GL15.GL_DYNAMIC_DRAW);
		dataVbo.unbind();
		try {
			template = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		instanceBuffer = BufferUtils.createFloatBuffer(template.getInstanceSize() * maxInstances);
		instanceBuffer.limit(instanceBuffer.capacity());
		instances = (T[]) Array.newInstance(type, maxInstances);
		instanceVbo = new GLVertexBufferObject(GL15.GL_ARRAY_BUFFER);
		instanceVbo.bind();
		instanceVbo.orphanBuffer(template.getInstanceSize() << 2, GL15.GL_STREAM_DRAW);
		instanceVbo.unbind();
		vao.bind();
		dataVbo.bind();
		GLGeometryUtils.bindAttributes(flags, vao, vertexSize);
		instanceVbo.bind();
		template.bindAttributes(vao, attributes.length);
		vao.unbind();	
		instanceAttributes = new int[template.getAttributeCount()];
		template.getAttributes(instanceAttributes, 0, attributes.length);
		vertexCount = vertices.size();
		if (indices != null && indices.size() > 0) {
			vertexCount = indices.size();
			indexBuffer = BufferUtils.createIntBuffer(indices.size());
			indexBuffer.limit(indexBuffer.capacity());
			for (int i = 0; i < indices.size(); i++) {
				indexBuffer.put(i, indices.get(i));
			}
			elementVbo = new GLVertexBufferObject(GL15.GL_ELEMENT_ARRAY_BUFFER);
			elementVbo.bind();
			elementVbo.bufferData(indexBuffer, GL15.GL_STATIC_DRAW);
			elementVbo.unbind();
		}
	}
	
	@Override
	public void updateInstance(int instance, T newValue) {
		instances[instance] = newValue;
		updateInstances = true;
	}

	@Override
	public void bind() {
		vao.bind();
		vao.enableAttributes(attributes);
		vao.enableAttributes(instanceAttributes);
		if (elementVbo != null) {
			elementVbo.bind();
		}
	}
	
	@Override
	public void update(int instanceCount) {
		if (updateInstances) {
			this.instanceCount = instanceCount;
			instanceBuffer.limit(instanceBuffer.capacity());
			instanceBuffer.position(0);
			int size = 0;
			for (int i = 0; i < instanceCount; i++) {
				instances[i].uploadInstance(instanceBuffer, instances[i].getInstanceSize() * i);
				size += instances[i].getInstanceSize();
			}
			instanceBuffer.limit(size);
			instanceBuffer.position(0);
			if (instanceCount == 0) {
				return;
			}
			instanceVbo.bind();
			instanceVbo.orphanBuffer(instanceBuffer.limit() << 2, GL15.GL_STREAM_DRAW);
			instanceVbo.bufferSubData(instanceBuffer, 0);
			instanceVbo.unbind();
			updateInstances = false;
		}
	}

	@Override
	public void render() {
		if (instanceCount == 0) {
			return;
		}
		if (elementVbo != null) {
			GL31.glDrawElementsInstanced(drawCall, vertexCount, GL11.GL_UNSIGNED_INT, 0, instanceCount);
		} else {
			GL31.glDrawArraysInstanced(drawCall, 0, vertexCount, instanceCount);
		}
	}

	@Override
	public void unbind() {
		if (elementVbo != null) {
			elementVbo.unbind();
		}
		vao.disableAttributes(instanceAttributes);
		vao.disableAttributes(attributes);
		vao.unbind();
	}

	@Override
	public void delete() {
		dataVbo.delete();
		if (elementVbo != null) {
			elementVbo.delete();
		}
		vao.delete();
	}
	
	public int hashCode() {
		int hashCode = 0;
		hashCode = 31 * hashCode + vao.hashCode();
		hashCode = 31 * hashCode + dataVbo.hashCode();
		if (elementVbo != null) {
			hashCode = 31 * hashCode + elementVbo.hashCode();
		}
		return hashCode;
	}

}
