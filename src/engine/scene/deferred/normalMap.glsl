mat3 cotangentFrame(vec3 normal, vec3 position, vec2 uv) {
	vec3 dp1 = dFdx(position);
	vec3 dp2 = dFdy(position);
	vec2 duv1 = dFdx(uv);
	vec2 duv2 = dFdy(uv);
	
	vec3 dp2perp = cross(dp2, normal);
	vec3 dp1perp = cross(normal, dp1);
	vec3 tangent = dp2perp * duv1.x + dp1perp * duv2.x;
	vec3 bitangent = dp2perp * duv1.y + dp1perp * duv2.y;
	float invMax = inversesqrt(max(dot(tangent, tangent), dot(bitangent, bitangent)));
	tangent *= invMax;
	bitangent *= invMax;
	return mat3(tangent, bitangent, normal);
}

vec3 perturbNormal(vec3 normal, vec3 position, vec2 textureCoord, vec3 normalSample) {
	vec3 map = vec3(normalSample.x, normalSample.y, 1.0);
	map = map * (255.0 / 127.0) - (128.0 / 127.0);
	map.z = sqrt(1.0 - dot(map.xx, map.yy));
	map.y = -map.y;
	mat3 tangentBitangentNormal = cotangentFrame(normal, position, textureCoord);
	return normalize(tangentBitangentNormal * map);
}

vec3 normalMap(vec3 normalSample, vec3 vertexNormal, vec3 viewSpacePos, vec2 textureCoord) {
	return perturbNormal(normalize(vertexNormal), normalize(viewSpacePos), textureCoord, normalSample);
}