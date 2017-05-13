package engine.rendering.opengl;

import java.io.InputStream;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import engine.rendering.Shader;
import library.opengl.GLProgram;

public class GLShader implements Shader {
	
	private GLProgram program;
	
	private library.opengl.GLShader vertexShader, fragmentShader;

	public GLShader(InputStream vertexStream, InputStream fragmentStream) {
		
	}
	
	@Override
	public void bind() {
		program.bind();
	}

	@Override
	public int getUniformLocation(String name) {
		return program.getUniformLocation(name);
	}
	
	@Override
	public void uploadInteger(int location, int value) {
		program.upload(location, value);
	}

	@Override
	public void uploadVector(int location, Vector2f vector) {
		program.upload(location, vector);
	}

	@Override
	public void uploadVector(int location, Vector3f vector) {
		program.upload(location, vector);
	}

	@Override
	public void uploadVector(int location, Vector4f vector) {
		program.upload(location, vector);
		
	}

	@Override
	public void uploadMatrix(int location, Matrix4f matrix) {
		program.upload(location, matrix);
		
	}

	@Override
	public void unbind() {
		program.unbind();
	}

	@Override
	public void delete() {
		program.delete();
	}

}
