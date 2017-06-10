#version 330

uniform sampler2D diffuseTexture;

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = texture2D(diffuseTexture, pass_TextureCoord);
}
