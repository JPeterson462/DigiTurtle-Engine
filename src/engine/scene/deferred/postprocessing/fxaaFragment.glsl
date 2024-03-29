#version 330

#define FXAA_REDUCE_MIN (1.0/128.0)
#define FXAA_REDUCE_MUL (1.0/8.0)
#define FXAA_SPAN_MAX (8.0)
#define FXAA_RESOLUTION (0.1)

uniform sampler2D diffuseTexture;

uniform vec2 resolution;

in vec2 pass_TextureCoord;

out vec4 out_Color;

// https://github.com/mattdesl/glsl-fxaa/blob/master/demo/optimized.frag

const vec3 luma = vec3(0.299, 0.587, 0.114);

vec4 fxaa(vec2 fragCoord) {
	vec2 invResolution = vec2(1.0 / resolution.x, 1.0 / resolution.y);
	vec3 rgbNW = texture2D(diffuseTexture, (fragCoord + vec2(-FXAA_RESOLUTION, -FXAA_RESOLUTION)) * invResolution).rgb;
	vec3 rgbNE = texture2D(diffuseTexture, (fragCoord + vec2( FXAA_RESOLUTION, -FXAA_RESOLUTION)) * invResolution).rgb;
	vec3 rgbSW = texture2D(diffuseTexture, (fragCoord + vec2(-FXAA_RESOLUTION,  FXAA_RESOLUTION)) * invResolution).rgb;
	vec3 rgbSE = texture2D(diffuseTexture, (fragCoord + vec2( FXAA_RESOLUTION,  FXAA_RESOLUTION)) * invResolution).rgb;
	vec4 texColor = texture2D(diffuseTexture, fragCoord * invResolution);
	vec3 rgbM = texColor.rgb;
	float lumaNW = dot(rgbNW, luma);
	float lumaNE = dot(rgbNE, luma);
	float lumaSW = dot(rgbSW, luma);
	float lumaSE = dot(rgbSE, luma);
	float lumaM = dot(rgbM, luma);
    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
	vec2 dir;
	dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
	dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
	float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 
		(0.25 * FXAA_REDUCE_MUL), FXAA_REDUCE_MIN);
	float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);
	dir = min(vec2(FXAA_SPAN_MAX, FXAA_SPAN_MAX),
    			max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX),
				dir * rcpDirMin)) * invResolution;
	 vec3 rgbA = 0.5 * (
        texture2D(diffuseTexture, fragCoord * invResolution + dir * (1.0 / 3.0 - 0.5)).xyz +
        texture2D(diffuseTexture, fragCoord * invResolution + dir * (2.0 / 3.0 - 0.5)).xyz);
    vec3 rgbB = rgbA * 0.5 + 0.25 * (
        texture2D(diffuseTexture, fragCoord * invResolution + dir * -0.5).xyz +
		texture2D(diffuseTexture, fragCoord * invResolution + dir * 0.5).xyz);
	float lumaB = dot(rgbB, luma);
	if ((lumaB < lumaMin) || (lumaB > lumaMax)) {
		return vec4(rgbA, texColor.a);
	} else {
		return vec4(rgbB, texColor.a);
	}
}

void main(void) {
	out_Color = fxaa(pass_TextureCoord * resolution);
}
