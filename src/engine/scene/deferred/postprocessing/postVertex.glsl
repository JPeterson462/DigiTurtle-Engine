#version 330

in vec2 in_Position;

out vec2 pass_TextureCoord;

void main(void) {
	gl_Position = vec4(in_Position, 0.0, 1.0);
	pass_TextureCoord = in_Position * 0.5 + 0.5;
}
