package engine.text.opengl;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

import engine.text.TextBuffer;
import engine.text.TextRenderer;
import library.opengl.GLProgram;
import library.opengl.GLShader;
import utils.IOUtils;

public class GLTextRenderer implements TextRenderer {

	private GLProgram shader;
	
	private GLShader vertexShader, fragmentShader;
	
	private Matrix4f projectionMatrix;
	
	public GLTextRenderer(float width, float height) {
		projectionMatrix = new Matrix4f().ortho2D(0, width, height, 0);
		shader = new GLProgram();
		vertexShader = new GLShader(GL20.GL_VERTEX_SHADER);
		vertexShader.source(IOUtils.readStringQuietly(getClass().getResourceAsStream("textVertex.glsl")));
		if (!vertexShader.compile()) {
			throw new IllegalStateException("Invalid Vertex Shader: " + vertexShader.getLog());
		}
		fragmentShader = new GLShader(GL20.GL_FRAGMENT_SHADER);
		fragmentShader.source(IOUtils.readStringQuietly(getClass().getResourceAsStream("textFragment.glsl")));
		if (!fragmentShader.compile()) {
			throw new IllegalStateException("Invalid Fragment Shader: " + fragmentShader.getLog());
		}
		shader.attach(vertexShader.getID());
		shader.attach(fragmentShader.getID());
		shader.bindAttribute(0, "in_Position");
		shader.bindAttribute(1, "in_TexCoord");
		if (!shader.compile()) {
			throw new IllegalStateException("Invalid Text Shader: " + vertexShader.getLog() + "\n" + fragmentShader.getLog() + "\n" + shader.getLog());
		}
	}
	
	@Override
	public TextBuffer createBuffer() {
		return new GLTextBuffer(shader, projectionMatrix);
	}

	@Override
	public void destroy() {
		fragmentShader.delete();
		vertexShader.delete();
		shader.delete();
	}

}
