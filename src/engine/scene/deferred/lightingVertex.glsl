#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

in vec3 in_Position;
in vec2 in_TextureCoord;

out vec3 pass_Position;
out vec2 pass_TextureCoord;

void main(void) {
	gl_Position = vec4(in_Position, 1.0);
	pass_Position = in_Position;
	pass_TextureCoord = in_TextureCoord;
}
