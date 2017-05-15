#version 330

uniform sampler2D diffuseTexture;

uniform vec4 lightColor;

in vec3 pass_Position;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = vec4(lightColor.rgb, 1.0) * texture2D(diffuseTexture, pass_TextureCoord) * lightColor.a;
}