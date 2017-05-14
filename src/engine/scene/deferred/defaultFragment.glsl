#version 330

uniform sampler2D diffuseTexture;

in vec2 pass_TextureCoord;
in vec3 pass_Normal;

out vec4 out_Color0;
out vec4 out_Color1;

void main(void) {
	out_Color0 = texture2D(diffuseTexture, pass_TextureCoord);
	out_Color1 = vec4(normalize(pass_Normal) * 0.5 + 0.5, 1.0);
}
