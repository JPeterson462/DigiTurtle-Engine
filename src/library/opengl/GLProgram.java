package library.opengl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GLProgram implements GLResource {
	
	private int id;
	
	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public GLProgram() {
		id = GL20.glCreateProgram();
	}
	
	public int getID() {
		return id;
	}

	public void attach(int shader) {
		GL20.glAttachShader(id, shader);
	}
	
	public void link() {
		GL20.glLinkProgram(id);
	}
	
	public void validate() {
		GL20.glValidateProgram(id);
	}
	
	public boolean compile() {
		link();
		validate();
		return GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_TRUE &&
				GL20.glGetProgrami(id, GL20.GL_VALIDATE_STATUS) == GL11.GL_TRUE;
	}
	
	public String getLog() {
		return GL20.glGetProgramInfoLog(id);
	}
	
	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(id, name);
	}
	
	public void bind() {
		GL20.glUseProgram(id);
	}
	
	public void unbind() {
		GL20.glUseProgram(0);
	}
	
	public void delete() {
		GL20.glDeleteProgram(id);
	}
	
	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLProgram && ((GLProgram) object).id == id;
	}
	
	public void bindAttribute(int index, String name) {
		GL20.glBindAttribLocation(id, index, name);
	}

	public void upload(int location, int i) {
		GL20.glUniform1i(location, i);
	}
	
	public void upload(int location, int x, int y) {
		GL20.glUniform2i(location, x, y);
	}
	
	public void upload(int location, Vector2i vector) {
		GL20.glUniform2i(location, vector.x, vector.y);
	}
	
	public void upload(int location, int x, int y, int z) {
		GL20.glUniform3i(location, x, y, z);
	}
	
	public void upload(int location, Vector3i vector) {
		GL20.glUniform3i(location, vector.x, vector.y, vector.z);
	}

	public void upload(int location, int x, int y, int z, int w) {
		GL20.glUniform4i(location, x, y, z, w);
	}
	
	public void upload(int location, Vector4i vector) {
		GL20.glUniform4i(location, vector.x, vector.y, vector.z, vector.w);
	}
	
	public void upload(int location, float i) {
		GL20.glUniform1f(location, i);
	}

	public void upload(int location, float x, float y) {
		GL20.glUniform2f(location, x, y);
	}
	
	public void upload(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	public void upload(int location, float x, float y, float z) {
		GL20.glUniform3f(location, x, y, z);
	}
	
	public void upload(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	public void upload(int location, float x, float y, float z, float w) {
		GL20.glUniform4f(location, x, y, z, w);
	}
	
	public void upload(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}
	
	public void upload(int location, Matrix4f matrix) {
		matrix.get(matrixBuffer);
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
	public void upload(int location, FloatBuffer matrixBuffer) {
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}
	
}
