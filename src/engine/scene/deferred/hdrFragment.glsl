#version 330

uniform sampler2D diffuseTexture;

uniform float exposure;

in vec2 pass_TextureCoord;

out vec4 out_Color;

// https://learnopengl.com/#!Advanced-Lighting/HDR

void main(void) {
	const float gamma = 2.2;
    vec3 hdrColor = texture(diffuseTexture, pass_TextureCoord).rgb;
  
    // Exposure tone mapping
    vec3 mapped = vec3(1.0) - exp(-hdrColor * exposure);
    // Gamma correction 
    mapped = pow(mapped, vec3(1.0 / gamma));
  
    out_Color = vec4(mapped, 1.0);
}
