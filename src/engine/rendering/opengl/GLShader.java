package engine.rendering.opengl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;

import com.esotericsoftware.minlog.Log;

import engine.rendering.Shader;
import library.opengl.GLProgram;
import utils.IOUtils;

public class GLShader implements Shader {
	
	private GLProgram program;
	
	private library.opengl.GLShader vertexShader, fragmentShader;

	public GLShader(InputStream vertexStream, InputStream fragmentStream, HashMap<Integer, String> attributes) {
		program = new GLProgram();
		vertexShader = new library.opengl.GLShader(GL20.GL_VERTEX_SHADER);
		vertexShader.source(IOUtils.readStringQuietly(vertexStream));
		if (!vertexShader.compile()) {
			Log.error("Vertex Shader did not compile: " + vertexShader.getLog());
			throw new IllegalStateException();
		}
		fragmentShader = new library.opengl.GLShader(GL20.GL_FRAGMENT_SHADER);
		fragmentShader.source(IOUtils.readStringQuietly(fragmentStream));
		if (!fragmentShader.compile()) {
			Log.error("Fragment Shader did not compile: " + fragmentShader.getLog());
			throw new IllegalStateException();
		}
		program.attach(vertexShader.getID());
		program.attach(fragmentShader.getID());
		for (Map.Entry<Integer, String> attribute : attributes.entrySet()) {
			program.bindAttribute(attribute.getKey(), attribute.getValue());
		}
		if (!program.link()) {
			Log.error("Program did not compile: " + program.getLog());
			throw new IllegalStateException();
		}
	}

	public GLShader(InputStream vertexStream, InputStream fragmentStream, HashMap<Integer, String> attributes, HashMap<String, String> replacements) {
		program = new GLProgram();
		vertexShader = new library.opengl.GLShader(GL20.GL_VERTEX_SHADER);
		String vertexSource = IOUtils.readStringQuietly(vertexStream);
		vertexSource = substitute(vertexSource, replacements);
		vertexShader.source(vertexSource);
		if (!vertexShader.compile()) {
			Log.error("Vertex Shader did not compile: " + vertexShader.getLog());
			throw new IllegalStateException();
		}
		fragmentShader = new library.opengl.GLShader(GL20.GL_FRAGMENT_SHADER);
		String fragmentSource = IOUtils.readStringQuietly(fragmentStream);
		fragmentSource = substitute(fragmentSource, replacements);
		fragmentShader.source(fragmentSource);
		if (!fragmentShader.compile()) {
			Log.error("Fragment Shader did not compile: " + fragmentShader.getLog());
			throw new IllegalStateException();
		}
		program.attach(vertexShader.getID());
		program.attach(fragmentShader.getID());
		for (Map.Entry<Integer, String> attribute : attributes.entrySet()) {
			program.bindAttribute(attribute.getKey(), attribute.getValue());
		}
		if (!program.link()) {
			Log.error("Program did not compile: " + program.getLog());
			throw new IllegalStateException();
		}
	}
	
	private String substitute(String initial, HashMap<String, String> replacements) {
		for (Map.Entry<String, String> replacement : replacements.entrySet()) {
			initial = initial.replaceAll("\\{\\{" + replacement.getKey() + "\\}\\}", replacement.getValue());
		}
		return initial;
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
	public void uploadFloat(int location, float value) {
		program.upload(location, value);
	}

	@Override
	public void unbind() {
		program.unbind();
	}

	@Override
	public void delete() {
		program.delete();
	}

	@Override
	public void bindAttribute(int index, String name) {
		program.bindAttribute(index, name);
	}

}
