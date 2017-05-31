package engine.rendering.opengl;

import engine.rendering.Vertex;
import library.opengl.GLVertexArrayObject;

public class GLGeometryUtils {
	
	public static int[] getAttributes(int flags) {
		int attributeCount = 0;
		if ((flags & Vertex.POSITION_BIT) != 0) {
			attributeCount++;
		}
		if ((flags & Vertex.POSITION2D_BIT) != 0) {
			attributeCount++;
		}
		if ((flags & Vertex.TEXTURE_COORD_BIT) != 0) {
			attributeCount++;
		}
		if ((flags & Vertex.NORMAL_BIT) != 0) {
			attributeCount++;
		}
		if ((flags & Vertex.JOINTID_BIT) != 0) {
			attributeCount++;
		}
		if ((flags & Vertex.WEIGHT_BIT) != 0) {
			attributeCount++;
		}
		int[] attributes = new int[attributeCount];
		for (int i = 0; i < attributes.length; i++) {
			attributes[i] = i;
		}
		return attributes;
	}
	
	public static int getVertexSize(int flags) {
		int size = 0;
		if ((flags & Vertex.POSITION_BIT) != 0) {
			size += 3;
		}
		if ((flags & Vertex.POSITION2D_BIT) != 0) {
			size += 2;
		}
		if ((flags & Vertex.TEXTURE_COORD_BIT) != 0) {
			size += 2;
		}
		if ((flags & Vertex.NORMAL_BIT) != 0) {
			size += 3;
		}
		if ((flags & Vertex.JOINTID_BIT) != 0) {
			size += 3;
		}
		if ((flags & Vertex.WEIGHT_BIT) != 0) {
			size += 3;
		}
		return size;
	}
	
	public static void bindAttributes(int flags, GLVertexArrayObject vao, int vertexSize) {
		int offset = 0, index = 0;
		if ((flags & Vertex.POSITION_BIT) != 0) {
			vao.vertexAttributeFloat(index, 3, vertexSize << 2, offset << 2);
			index++;
			offset += 3;
		}
		if ((flags & Vertex.POSITION2D_BIT) != 0) {
			vao.vertexAttributeFloat(index, 2, vertexSize << 2, offset << 2);
			index++;
			offset += 2;
		}
		if ((flags & Vertex.TEXTURE_COORD_BIT) != 0) {
			vao.vertexAttributeFloat(index, 2, vertexSize << 2, offset << 2);
			index++;
			offset += 2;
		}
		if ((flags & Vertex.NORMAL_BIT) != 0) {
			vao.vertexAttributeFloat(index, 3, vertexSize << 2, offset << 2);
			index++;
			offset += 3;
		}
		if ((flags & Vertex.JOINTID_BIT) != 0) {
			vao.vertexAttributeFloat(index, 3, vertexSize << 2, offset << 2);
			index++;
			offset += 3;
		}
		if ((flags & Vertex.WEIGHT_BIT) != 0) {
			vao.vertexAttributeFloat(index, 3, vertexSize << 2, offset << 2);
			index++;
			offset += 3;
		}
	}

}
