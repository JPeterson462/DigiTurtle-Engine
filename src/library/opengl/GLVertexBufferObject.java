package library.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL15;

public class GLVertexBufferObject implements GLResource {
	
	private int id, type;
	
	public GLVertexBufferObject(int type) {
		this.type = type;
		id = GL15.glGenBuffers();
	}
	
	public int getID() {
		return id;
	}
	
	public void bufferData(Buffer data, int usage) {
		if (data instanceof FloatBuffer) {
			GL15.glBufferData(type, (FloatBuffer) data, usage);
		}
		else if (data instanceof IntBuffer) {
			GL15.glBufferData(type, (IntBuffer) data, usage);
		}
		else if (data instanceof ShortBuffer) {
			GL15.glBufferData(type, (ShortBuffer) data, usage);
		}
		else if (data instanceof ByteBuffer) {
			GL15.glBufferData(type, (ByteBuffer) data, usage);
		}
	}
	
	public void bufferSubData(Buffer data, int offset) {
		if (data instanceof FloatBuffer) {
			GL15.glBufferSubData(type, offset, (FloatBuffer) data);
		}
		else if (data instanceof IntBuffer) {
			GL15.glBufferSubData(type, offset, (IntBuffer) data);
		}
		else if (data instanceof ShortBuffer) {
			GL15.glBufferSubData(type, offset, (ShortBuffer) data);
		}
		else if (data instanceof ByteBuffer) {
			GL15.glBufferSubData(type, offset, (ByteBuffer) data);
		}
	}
	
	public void bind() {
		GL15.glBindBuffer(type, id);
	}
	
	public void unbind() {
		GL15.glBindBuffer(type, 0);
	}
	
	public void delete() {
		GL15.glDeleteBuffers(id);
	}

	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLVertexBufferObject && ((GLVertexBufferObject) object).id == id;
	}

}
