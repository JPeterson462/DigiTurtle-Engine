package engine.rendering;

import java.nio.FloatBuffer;

import library.opengl.GLVertexArrayObject;

public interface InstanceTemplate {
	
	public int getAttributeCount();
	
	public void getAttributes(int[] attributes, int offset, int attributeOffset);
	
	public int getInstanceSize();
	
	public void bindAttributes(GLVertexArrayObject vao, int attributeOffset);
	
	public void uploadInstance(FloatBuffer buffer, int position);

}
