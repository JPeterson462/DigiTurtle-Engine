#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

in vec2 in_Position;
in vec2 in_TexCoord;

out vec2 pass_TexCoord;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * vec4(in_Position, 0.0, 1.0);
	pass_TexCoord = in_TexCoord;
}
