#version 330

#define MAX_SHININESS {{maxShininess}}

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

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
	vec3 vertexNormal = normalize(pass_Normal);
	vec3 fragmentNormal = normalize(texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0);
	vec3 normal = normalMap(fragmentNormal, vertexNormal, pass_ViewSpacePos, pass_TextureCoord);
	out_Color0 = texture2D(diffuseTexture, pass_TextureCoord);
	out_Color1 = vec4(normal * 0.5 + 0.5, 1.0);
	out_Color2 = vec4(shininess / MAX_SHININESS, specularFactor, 0.0, 1.0); 
}
