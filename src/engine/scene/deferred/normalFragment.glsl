#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

in vec2 pass_TextureCoord;
in vec3 pass_Normal;

out vec4 out_Color0;
out vec4 out_Color1;

vec3 computeNormal() {
	vec3 vertexNormal = normalize(pass_Normal);
	vec3 fragmentNormal = normalize(texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0);
	return (vertexNormal + fragmentNormal) / 2.0;
}

void main(void) {
	out_Color0 = texture2D(diffuseTexture, pass_TextureCoord);
	out_Color1 = vec4(computeNormal() * 0.5 + 0.5, 1.0);
}
