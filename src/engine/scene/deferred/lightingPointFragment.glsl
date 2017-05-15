#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform float lightRadius;

uniform mat4 invProjectionMatrix;
uniform mat4 invViewMatrix;
uniform float near;
uniform float far;

in vec3 pass_Position;
in vec2 pass_TextureCoord;

out vec4 out_Color;

const float kPi = 3.14159265;
const float kShininess = 16.0;
const float kEnergyConservation = (8.0 + kShininess) / (8.0 * kPi);

float getAttenuation(float distance) {
	if (distance > lightRadius) {
		return 0;
	}
	float x = distance / lightRadius;
	return 1 / (1 + x * x);
}

vec3 reconstructPosition() {
	vec4 clipSpaceLocation;
	clipSpaceLocation.xy = pass_TextureCoord * 2.0 - 1.0;
	clipSpaceLocation.z = texture2D(depthTexture, pass_TextureCoord).r * 2.0 - 1.0;
	clipSpaceLocation.w = 1.0;
	vec4 homogenousLocation = invViewMatrix * invProjectionMatrix * clipSpaceLocation;
	return homogenousLocation.xyz / homogenousLocation.w;
}

void main(void) {
	vec3 normal = normalize(texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0);
	vec3 position = reconstructPosition();

	vec3 lightVector = lightPos - position;
	float distanceToLight = length(lightVector);
	float attenuation = getAttenuation(distanceToLight);
	vec3 lightDir = normalize(lightVector);

	float diff = max(dot(lightDir, normal), 0.0);
	vec3 diffuse = diff * lightColor;

	vec3 viewDir = normalize(viewPos - position);
	vec3 reflectDir = reflect(-lightDir, normal);
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float spec = kEnergyConservation * pow(max(dot(normal, halfwayDir), 0.0), 32.0);
	vec3 specular = vec3(0.3) * spec;

	vec3 color = (diffuse + specular) * attenuation;

	out_Color = vec4(pow(color, vec3(1.0/2.2)), attenuation * attenuation) * texture2D(diffuseTexture, pass_TextureCoord);
}
