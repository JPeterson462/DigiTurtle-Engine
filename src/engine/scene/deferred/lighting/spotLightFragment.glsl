#version 330

#define MAX_SHININESS {{maxShininess}}

uniform mat4 invProjectionMatrix;

uniform sampler2D diffuseTexture;
uniform sampler2D depthTexture;
uniform sampler2D normalTexture;
uniform sampler2D materialTexture;

uniform vec2 screenSize;

uniform vec3 lightColor;
uniform vec3 eyePosition;
uniform float radius;

uniform vec3 lightPosition;
uniform vec3 lightDirection;
uniform float lightRange;

out vec4 out_Color;

void main(void) {
	vec2 textureCoord = gl_FragCoord.xy * screenSize;
	vec4 clipPos = vec4(vec3(textureCoord, texture2D(depthTexture, textureCoord).r) * 2.0 - 1.0, 1.0);
	vec4 eyeSpace = invProjectionMatrix * clipPos;
	eyeSpace.xyz /= eyeSpace.w;
	
	vec4 material = texture2D(materialTexture, textureCoord);
	float shininess = material.r * MAX_SHININESS;
	vec3 specularColor = vec3(material.g);
	
	vec3 distanceToLight = lightPosition - eyeSpace.xyz;
	float distance = length(distanceToLight);
	if (distance > radius) {
		discard;
	}
	vec3 lightDir = normalize(distanceToLight);
	vec3 normal = texture2D(normalTexture, textureCoord).rgb * 2.0 - 1.0;
	vec3 albedo = texture2D(diffuseTexture, textureCoord).rgb;
	float attenuation = 1.0 - clamp(distance / radius, 0.0, 1.0);
	float coneFactor = dot(normalize(lightDirection), lightDir);
	if (coneFactor < lightRange) {
		discard;
	}
	
	float diffuseFactor = dot(normal, lightDir);
	if (diffuseFactor <= 0.0) {
		discard;
	}
	vec3 diffuse = diffuseFactor * albedo * lightColor * attenuation;
	
	vec3 viewDir = normalize(0 - eyeSpace.xyz);
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float specularCoefficient = max(dot(halfwayDir, normal), 0.0);
	float specularFactor = pow(specularCoefficient, shininess);
	if (shininess <= 1.0) {
		specularFactor = 0.0;
	}
	specularFactor *= diffuseFactor;
	vec3 specular = specularFactor * specularColor * lightColor * attenuation;
	
	out_Color = vec4(diffuse + specular, 1.0);
}
