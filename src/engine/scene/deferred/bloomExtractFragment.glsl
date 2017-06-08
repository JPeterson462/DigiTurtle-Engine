#version 330

uniform sampler2D diffuseTexture;

in vec2 pass_TextureCoord;

out vec4 out_Color;

const vec3 BRIGHTNESS_BASE = vec3(0.2126, 0.7152, 0.0722);

// https://learnopengl.com/#!Advanced-Lighting/Bloom

void main(void) {
	vec4 color = texture2D(diffuseTexture, pass_TextureCoord);
	float brightness = dot(color.rgb, BRIGHTNESS_BASE);
	if (brightness > 0.9) {
		out_Color = color;
	} else {
		discard;
	}
}
