#version 330

#define MAX_SHININESS {{maxShininess}}

uniform mat4 invProjectionMatrix;

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D normalTexture;
uniform sampler2D materialTexture;

uniform vec3 lightColor;
uniform vec3 eyePosition;
uniform float radius;

uniform vec3 lightPosition;
uniform vec3 lightDirection;
uniform float lightRange;

in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	vec4 clipPos = vec4(vec3(pass_TextureCoord, texture2D(depthTexture, pass_TextureCoord).r) * 2.0 - 1.0, 1.0);
	vec4 eyeSpace = invProjectionMatrix * clipPos;
	eyeSpace.xyz /= eyeSpace.w;
	
	vec4 material = texture2D(materialTexture, pass_TextureCoord);
	float shininess = material.r * MAX_SHININESS;
	vec3 specularColor = vec3(material.g);
	
	vec3 distanceToLight = lightPosition - eyeSpace.xyz;
	float distance = length(distanceToLight);
	if (distance > radius) {
		discard;
	}
	vec3 lightDir = normalize(distanceToLight);
	vec3 normal = texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0;
	vec3 albedo = texture2D(diffuseTexture, pass_TextureCoord).rgb;
	float attenuation = 1.0 - clamp(distance / radius, 0.0, 1.0);
	float coneFactor = dot(lightDir, lightDirection);
	if (coneFactor < lightRange) {
		discard;
	}
	float coneAttenuation = 1.0;
	
	float diffuseFactor = dot(normal, lightDir);
	if (diffuseFactor <= 0.0) {
		discard;
	}
	vec3 diffuse = diffuseFactor * albedo * lightColor * attenuation * coneAttenuation;
	
	vec3 viewDir = normalize(0 - eyeSpace.xyz);
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float specularCoefficient = max(dot(halfwayDir, normal), 0.0);
	float specularFactor = pow(specularCoefficient, shininess);
	vec3 specular = specularFactor * specularColor * lightColor * attenuation * coneAttenuation;
	
	out_Color = vec4(diffuse + specular, 1.0);

}
