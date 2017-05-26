#version 330

#define GAMMA_CORRECTION

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D depthTexture;

uniform vec3 lightColor;
uniform vec4 lightPos;
uniform vec3 viewPos;
uniform vec4 lightDirPacked;
uniform float lightRadius;
uniform int directional;

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

float computeSpecular(vec3 normal, vec3 viewDir, vec3 lightDir, float shininess) {
	vec3 halfwayDir = (viewDir + lightDir) * vec3(0.5);
	return pow(max(dot(halfwayDir, normal), 0.0), shininess);
}

float computeDiffuse(vec3 normal, vec3 viewDir, vec3 lightDir) {
	return max(0.0, dot(normal, lightDir));
}

vec2 computeLighting(vec3 position, vec3 normal, vec3 viewDir, vec4 lightDir, float shininess) {
	float diffuseFactor = computeDiffuse(normal, viewDir, lightDir.xyz);
	float specularFactor = computeSpecular(normal, viewDir, lightDir.xyz, shininess);
	return vec2(diffuseFactor, specularFactor) * vec2(lightDir.w);
}

float computeSpotFalloff(vec4 lightDir, vec3 lightVec) {
	vec3 L = normalize(lightVec);
	vec3 spotDir = normalize(lightDir.xyz);
	float curAngleCos = dot(-L, spotDir);
	float innerAngleCos = floor(lightDir.w) * 0.0001;
	float outerAngleCos = fract(lightDir.w);
	float angle = (curAngleCos - outerAngleCos) / (innerAngleCos - outerAngleCos);
	float falloff = clamp(angle, step(lightDir.w, 0.001), 1.0);
	return pow(clamp(angle, 0.0, 1.0), 4.0);
}

vec4 lightComputeDir(vec3 worldPos, vec4 color, vec4 position, vec4 spotDir) {
	if (directional == 0) {
		return vec4(-position.xyz, 1.0);
	}
	vec3 lightVec = position.xyz - worldPos.xyz;
	vec4 lightDir = vec4(0.0);
	lightDir.xyz = lightVec;
	float dist = length(lightDir.xyz);
	lightDir.w = clamp(1.0 - position.w * dist, 0.0, 1.0);
	lightDir.xyz /= dist;
	if (directional == 2) {
		lightDir.w = computeSpotFalloff(spotDir, lightVec) * lightDir.w;
	}
	return lightDir;
}

float computeOcclusion(vec3 worldPos, vec3 lightPos, vec3 cameraPos) {
	//float distanceToLight = length(lightPos - cameraPos);
	//float distanceToFragment = length(worldPos - cameraPos);
	//return distanceToLight <= distanceToFragment ? 1.0 : 0.0;
	return 1.0;
}

void main(void) {
	vec4 diffuseColor = texture2D(diffuseTexture, pass_TextureCoord);
	if (diffuseColor.a == 0.0) {
		discard;
	}

	vec3 normal = normalize(texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0);
	vec3 position = reconstructPosition();

	vec3 viewDir = normalize(viewPos - position);
	vec4 lightDir = lightComputeDir(position, vec4(lightColor, 1.0), lightPos, lightDirPacked);
	vec2 light = computeLighting(position, normal, viewDir, lightDir, 32.0);

#ifdef GAMMA_CORRECTION
	vec4 gammaExponent = vec4(vec3(1.0 / 2.2), 1.0);
#else
	vec4 gammaExponent = vec4(1.0);
#endif

	vec4 color = vec4(light.x * diffuseColor.xyz + light.y * vec3(1.0), 1.0);
	//out_Color = vec4(pow(color, gammaExponent), 1.0);
	out_Color = pow(color, gammaExponent) * vec4(lightColor, 1.0) * computeOcclusion(position, lightPos.xyz, viewPos);
}
