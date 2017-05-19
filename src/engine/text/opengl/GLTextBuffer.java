package engine.text.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import engine.text.CharacterStream;
import engine.text.TextAlign;
import engine.text.TextBuffer;
import engine.text.TextEffect;
import engine.text.TextEffects;
import library.font.Font;
import library.font.Page;
import library.opengl.GLProgram;
import library.opengl.GLVertexArrayObject;
import library.opengl.GLVertexBufferObject;

public class GLTextBuffer implements TextBuffer {

	private int projectionMatrixUniform, viewMatrixUniform;

	private int textColorUniform, textWidthUniform, textEdgeUniform;

	private int outlineOffsetUniform, outlineColorUniform, outlineWidthUniform, outlineEdgeUniform;

	private Font font;

	private TextEffect effect = TextEffects.newNullEffect(new Vector3f(1, 1, 1), 0.5f);

	private Matrix4f viewMatrix = new Matrix4f();

	private String text;

	private boolean needToUpdate = true;

	private GLProgram shader;

	private GLVertexArrayObject vao;

	private GLVertexBufferObject dataVbo;

	private GLVertexBufferObject elementVbo;

	private int vertexCount;

	private float boxWidth = Float.MAX_VALUE, boxHeight;

	public GLTextBuffer(GLProgram shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.bind();
		projectionMatrixUniform = shader.getUniformLocation("projectionMatrix");
		viewMatrixUniform = shader.getUniformLocation("viewMatrix");
		textColorUniform = shader.getUniformLocation("textColor");
		textWidthUniform = shader.getUniformLocation("textWidth");
		textEdgeUniform = shader.getUniformLocation("textEdge");
		outlineOffsetUniform = shader.getUniformLocation("outlineOffset");
		outlineColorUniform = shader.getUniformLocation("outlineColor");
		outlineWidthUniform = shader.getUniformLocation("outlineWidth");
		outlineEdgeUniform = shader.getUniformLocation("outlineEdge");
		shader.upload(projectionMatrixUniform, projectionMatrix);
		shader.upload(shader.getUniformLocation("fontPage"), 0);
		shader.unbind();
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
		needToUpdate = true;
	}

	@Override
	public void setEffect(TextEffect effect) {
		this.effect = effect;
	}

	@Override
	public void setPosition(Vector2f position) {
		viewMatrix.identity().translate(position.x, position.y, 0).scale(font.getScaleFactor(), font.getScaleFactor(), 1);
	}

	@Override
	public void setBounds(float boxWidth, float boxHeight) {
		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;
		needToUpdate = true;
	}

	@Override
	public void setText(String text) {
		this.text = text;
		needToUpdate = true;
	}

	@Override
	public void render() {
		if (needToUpdate) {
			update();
			needToUpdate = false;
		}
		shader.bind();
		shader.upload(viewMatrixUniform, viewMatrix);
		shader.upload(outlineColorUniform, effect.getBorderColor());
		shader.upload(outlineEdgeUniform, effect.getBorderSharpness());
		shader.upload(outlineOffsetUniform, effect.getOutlineOffset());
		shader.upload(outlineWidthUniform, effect.getBorderWidth());
		shader.upload(textColorUniform, effect.getLineColor());
		shader.upload(textEdgeUniform, effect.getLineSharpness());
		shader.upload(textWidthUniform, effect.getLineWidth());
		vao.bind();
		vao.enableAttributes(0, 1);
		elementVbo.bind();
		Page page = font.getPages()[0];
		page.getTexture().activeTexture(0);
		page.getTexture().bind();
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
		page.getTexture().unbind();
		elementVbo.unbind();
		vao.disableAttributes(0, 1);
		vao.unbind();
		shader.unbind();
	}

	private void update() {
		CharacterStream stream = new CharacterStream(text, boxWidth, boxHeight, font, TextAlign.ALIGN_LEFT | TextAlign.ALIGN_TOP);
		int vertexSize = 4 * 4;
		int bufferSize = stream.size() * vertexSize;
		vertexCount = stream.size() * 6;
		IntBuffer indices = BufferUtils.createIntBuffer(vertexCount);
		indices.limit(vertexCount);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(bufferSize);
		buffer.limit(bufferSize);
		for (int i = 0; i < stream.size(); i++) {
			Vector4f offsetAndSize = stream.getOffsetAndSize(i);
			Vector4f textureOffsetAndSize = stream.getTextureOffsetAndSize(i);
			// Vertex 0
			buffer.put(i * vertexSize + 0, offsetAndSize.x);
			buffer.put(i * vertexSize + 1, offsetAndSize.y);
			buffer.put(i * vertexSize + 2, textureOffsetAndSize.x);
			buffer.put(i * vertexSize + 3, textureOffsetAndSize.y);
			// Vertex 1
			buffer.put(i * vertexSize + 4, offsetAndSize.x + offsetAndSize.z);
			buffer.put(i * vertexSize + 5, offsetAndSize.y);
			buffer.put(i * vertexSize + 6, textureOffsetAndSize.z);
			buffer.put(i * vertexSize + 7, textureOffsetAndSize.y);
			// Vertex 2
			buffer.put(i * vertexSize + 8, offsetAndSize.x + offsetAndSize.z);
			buffer.put(i * vertexSize + 9, offsetAndSize.y + offsetAndSize.w);
			buffer.put(i * vertexSize + 10, textureOffsetAndSize.z);
			buffer.put(i * vertexSize + 11, textureOffsetAndSize.w);
			// Vertex 3
			buffer.put(i * vertexSize + 12, offsetAndSize.x);
			buffer.put(i * vertexSize + 13, offsetAndSize.y + offsetAndSize.w);
			buffer.put(i * vertexSize + 14, textureOffsetAndSize.x);
			buffer.put(i * vertexSize + 15, textureOffsetAndSize.w);
			// Indices
			indices.put(i * 6 + 0, i * 4 + 0);
			indices.put(i * 6 + 1, i * 4 + 1);
			indices.put(i * 6 + 2, i * 4 + 2);
			indices.put(i * 6 + 3, i * 4 + 2);
			indices.put(i * 6 + 4, i * 4 + 3);
			indices.put(i * 6 + 5, i * 4 + 0);
		}
		if (vao == null) {
			vao = new GLVertexArrayObject();
			dataVbo = new GLVertexBufferObject(GL15.GL_ARRAY_BUFFER);
			elementVbo = new GLVertexBufferObject(GL15.GL_ELEMENT_ARRAY_BUFFER);
		}
		vao.bind();
		dataVbo.bind();
		dataVbo.bufferData(buffer, GL15.GL_DYNAMIC_DRAW);
		vao.vertexAttributeFloat(0, 2, 4 << 2, 0 << 2);
		vao.vertexAttributeFloat(1, 2, 4 << 2, 2 << 2);
		dataVbo.unbind();
		vao.unbind();
		elementVbo.bind();
		elementVbo.bufferData(indices, GL15.GL_DYNAMIC_DRAW);
		elementVbo.unbind();
	}

	@Override
	public void delete() {
		elementVbo.delete();
		dataVbo.delete();
		vao.delete();
	}

}
