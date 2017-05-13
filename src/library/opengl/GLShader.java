package library.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GLShader implements GLResource {

	private int id;
	
	public GLShader(int type) {
		id = GL20.glCreateShader(type);
	}
	
	public int getID() {
		return id;
	}
	
	public void source(String source) {
		GL20.glShaderSource(id, source);
	}
	
	public boolean compile() {
		GL20.glCompileShader(id);
		return GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_TRUE;
	}
	
	public String getLog() {
		return GL20.glGetShaderInfoLog(id);
	}
	
	public void delete() {
		GL20.glDeleteShader(id);
	}
	
	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	public boolean equals(Object object) {
		return object instanceof GLShader && ((GLShader) object).id == id;
	}

}
