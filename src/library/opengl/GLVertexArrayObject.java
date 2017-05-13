package library.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

public class GLVertexArrayObject implements GLResource {
	
	private int id;
	
	public GLVertexArrayObject() {
		id = GL30.glGenVertexArrays();
	}
	
	public int getID() {
		return id;
	}
	
	public void instanceAttribute(int index, int instanceCount) {
		GL33.glVertexAttribDivisor(index, instanceCount);
	}
	
	public void vertexAttributeFloat(int index, int size, int stride, int offset) {
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, stride, offset);
	}
	
	public void vertexAttributeShort(int index, int size, int stride, int offset) {
		GL20.glVertexAttribPointer(index, size, GL11.GL_UNSIGNED_SHORT, false, stride, offset);
	}

	public void vertexAttributeByte(int index, int size, int stride, int offset) {
		GL20.glVertexAttribPointer(index, size, GL11.GL_UNSIGNED_BYTE, false, stride, offset);
	}
	
	public void vertexAttributeInt(int index, int size, int stride, int offset) {
		GL30.glVertexAttribIPointer(index, size, GL11.GL_INT, stride, offset);
	}
	
	public void enableAttributes(int... attributes) {
		for (int i = 0; i < attributes.length; i++) {
			GL20.glEnableVertexAttribArray(attributes[i]);
		}
	}

	public void disableAttributes(int... attributes) {
		for (int i = 0; i < attributes.length; i++) {
			GL20.glDisableVertexAttribArray(attributes[i]);
		}
	}
	
	public void bind() {
		GL30.glBindVertexArray(id);
	}
	
	public void unbind() {
		GL30.glBindVertexArray(0);
	}
	
	public void delete() {
		GL30.glDeleteVertexArrays(id);
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLVertexArrayObject && ((GLVertexArrayObject) object).id == id;
	}

}
