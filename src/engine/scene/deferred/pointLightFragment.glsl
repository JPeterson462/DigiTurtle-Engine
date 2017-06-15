#version 330

uniform mat4 invProjectionMatrix;

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D normalTexture;

uniform vec3 lightColor;
uniform vec3 eyePosition;
uniform float radius;

in vec3 pass_LightPos;
in vec2 pass_TextureCoord;
in mat3 pass_NormalMatrix;

out vec4 out_Color;

void main(void) {
	vec4 clipPos = vec4(vec3(pass_TextureCoord, texture2D(depthTexture, pass_TextureCoord).r) * 2.0 - 1.0, 1.0);
	vec4 eyeSpace = invProjectionMatrix * clipPos;
	eyeSpace.xyz /= eyeSpace.w;
	
	vec3 distanceToLight = pass_LightPos - eyeSpace.xyz;
	float distance = length(distanceToLight);
	vec3 lightDir = normalize(distanceToLight);
	vec3 normal = texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0;
	vec3 albedo = texture2D(diffuseTexture, pass_TextureCoord).rgb;
	vec3 specularColor = vec3(1.0);
	float attenuation = 1.0 - clamp(distance / radius, 0.0, 1.0);
	float shininess = 32.0;
	
	float diffuseFactor = max(dot(normal, lightDir), 0.0);
	if (diffuseFactor == 0) {
		discard;
	}
	vec3 diffuse = diffuseFactor * albedo * lightColor * attenuation;
	
	vec3 halfwayDir = (lightDir - eyeSpace.xyz) * vec3(0.5);
	float specularCoefficient = max(dot(halfwayDir, normal), 0.0);
	float specularFactor = pow(specularCoefficient, shininess);
	vec3 specular = specularFactor * specularColor * lightColor * attenuation;
	
	out_Color = vec4(diffuse + specular, 1.0);
}
