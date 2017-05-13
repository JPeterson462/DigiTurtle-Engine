package engine.rendering.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import engine.rendering.Geometry;
import engine.rendering.Vertex;
import library.opengl.GLVertexArrayObject;
import library.opengl.GLVertexBufferObject;

public class GLGeometry implements Geometry {
	
	private GLVertexArrayObject vao;
	
	private GLVertexBufferObject dataVbo, elementVbo;
	
	private int[] attributes;
	
	private int vertexCount;
	
	private int drawCall;
	
	private FloatBuffer dataBuffer;
	
	private IntBuffer indexBuffer;
	
	public GLGeometry(ArrayList<Vertex> vertices, ArrayList<Integer> indices, int flags) {
		drawCall = GL11.GL_TRIANGLES;
		vao = new GLVertexArrayObject();
		vao.bind();
		int vertexSize = getVertexSize(flags);
		attributes = getAttributes(flags);
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
		}
		dataVbo = new GLVertexBufferObject(GL15.GL_ARRAY_BUFFER);
		dataVbo.bind();
		dataVbo.bufferData(dataBuffer, GL15.GL_STATIC_DRAW);
		int offset = 0, index = 0;
		if ((flags & Vertex.POSITION_BIT) != 0) {
			vao.vertexAttributeFloat(index, 3, vertexSize << 2, offset << 2);
			index++;
			offset += 3;
		}
		if ((flags & Vertex.TEXTURE_COORD_BIT) != 0) {
			vao.vertexAttributeFloat(index, 2, vertexSize << 2, offset << 2);
			index++;
			offset += 2;
		}
		if ((flags & Vertex.NORMAL_BIT) != 0) {
			vao.vertexAttributeFloat(index, 3, vertexSize << 2, offset << 2);
			index++;
			offset += 3;
		}
		dataVbo.unbind();
		vao.unbind();	
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
	
	private int[] getAttributes(int flags) {
		int attributeCount = 0;
		
		int[] attributes = new int[attributeCount];
		
		return attributes;
	}
	
	private int getVertexSize(int flags) {
		int size = 0;
		if ((flags & Vertex.POSITION_BIT) != 0) {
			size += 3;
		}
		if ((flags & Vertex.TEXTURE_COORD_BIT) != 0) {
			size += 2;
		}
		if ((flags & Vertex.NORMAL_BIT) != 0) {
			size += 3;
		}
		return size;
	}

	@Override
	public void bind() {
		vao.bind();
		vao.enableAttributes(attributes);
		if (elementVbo != null) {
			elementVbo.bind();
		}
	}

	@Override
	public void render() {
		if (elementVbo != null) {
			GL11.glDrawElements(drawCall, vertexCount, GL11.GL_UNSIGNED_INT, 0);
		} else {
			GL11.glDrawArrays(drawCall, 0, vertexCount);
		}
	}

	@Override
	public void unbind() {
		if (elementVbo != null) {
			elementVbo.unbind();
		}
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

}
