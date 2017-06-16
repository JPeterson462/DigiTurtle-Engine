#version 330

#define MAX_SHININESS {{maxShininess}}

uniform sampler2D diffuseTexture;

uniform float shininess;
uniform float specularFactor;

in vec2 pass_TextureCoord;
in vec3 pass_Normal;
in vec3 pass_ViewSpacePos;
in mat3 pass_NormalMatrix;

out vec4 out_Color0;
out vec4 out_Color1;
out vec4 out_Color2;

#file "engine/scene/deferred/normalMap.glsl"

void main(void) {
	out_Color0 = texture2D(diffuseTexture, pass_TextureCoord);
	out_Color1 = vec4(normalize(pass_NormalMatrix * pass_Normal) * 0.5 + 0.5, 1.0);
	out_Color2 = vec4(shininess / MAX_SHININESS, specularFactor, 0.0, 1.0);
}
