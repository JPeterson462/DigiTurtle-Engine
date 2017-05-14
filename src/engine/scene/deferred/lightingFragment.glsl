#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;

uniform vec3 lightColor;
uniform vec3 lightPos;
uniform vec3 viewPos;

in vec3 pass_Position;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	vec3 ambient = 0.05 * lightColor;

	vec3 lightDir = normalize(lightPos - pass_Position);
	vec3 normal = normalize(texture2D(normalTexture, pass_TextureCoord).rgb * 2.0 - 1.0);
	float diff = max(dot(lightDir, normal), 0.0);
	vec3 diffuse = diff * lightColor;

	vec3 viewDir = normalize(viewPos - pass_Position);
	vec3 reflectDir = reflect(-lightDir, normal);
	vec3 halfwayDir = normalize(lightDir + viewDir);
	float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);
	vec3 specular = vec3(0.3) * spec;

	vec4 color = vec4(ambient + diffuse + specular, 1.0);

	out_Color = color * texture2D(diffuseTexture, pass_TextureCoord);
}
