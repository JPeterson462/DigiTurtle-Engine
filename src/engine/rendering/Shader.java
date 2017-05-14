package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface Shader {
	
	public void bind();
	
	public int getUniformLocation(String name);
	
	public void uploadInteger(int location, int value);
	
	public void uploadVector(int location, Vector2f vector);

	public void uploadVector(int location, Vector3f vector);

	public void uploadVector(int location, Vector4f vector);
	
	public void uploadMatrix(int location, Matrix4f matrix);
	
	public void unbind();
	
	public void delete();
	
	public void bindAttribute(int index, String name);

}
