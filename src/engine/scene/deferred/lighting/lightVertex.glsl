#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec2 in_Position;
in vec2 in_TextureCoord;

out vec2 pass_TextureCoord;

void main(void) {
	gl_Position = vec4(in_Position, 0.0, 1.0);
	pass_TextureCoord = in_TextureCoord;
}
