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
	
	private Matrix4f projectionMatrix;
	
	public GLTextRenderer() {
		shader = new GLProgram();
		GLShader vertexShader = new GLShader(GL20.GL_VERTEX_SHADER);
		vertexShader.source(IOUtils.readStringQuietly(getClass().getResourceAsStream("textVertex.glsl")));
		if (!vertexShader.compile()) {
			throw new IllegalStateException("Invlaid Vertex Shader: " + vertexShader.getLog());
		}
		GLShader fragmentShader = new GLShader(GL20.GL_VERTEX_SHADER);
		fragmentShader.source(IOUtils.readStringQuietly(getClass().getResourceAsStream("textFragment.glsl")));
		if (!fragmentShader.compile()) {
			throw new IllegalStateException("Invlaid Fragment Shader: " + fragmentShader.getLog());
		}
		shader.attach(vertexShader.getID());
		shader.attach(fragmentShader.getID());
		shader.bindAttribute(0, "in_Position");
		shader.bindAttribute(1, "in_TexCoord");
		if (!shader.compile()) {
			throw new IllegalStateException("Invalid Text Shader: " + shader.getLog());
		}
	}
	
	@Override
	public TextBuffer createBuffer() {
		return new GLTextBuffer(shader, projectionMatrix);
	}

	@Override
	public void destroy() {
		shader.delete();
	}

}
