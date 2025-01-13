#version 430

in vec3 interpolated_normal;
in vec3 interpolated_light_direction;
in vec3 interpolated_vertex_position;
in vec3 interpolated_half_vector;
in vec4 shadow_coordinate;
in vec2 texture_coordinate;
in vec3 original_vertex;
in vec3 eye_space_vertex;

out vec4 fragment_color;
 
struct PositionalLight {
	vec4 ambient, diffuse, specular;
	vec3 position;
};

struct Material {
	vec4 ambient, diffuse, specular;
	float shininess;
};

uniform vec4 global_ambient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix; 
uniform mat4 p_matrix;
uniform mat4 normals_matrix;
uniform mat4 shadow_mvp;
uniform float bump_mapped;
uniform float light_enabled;
uniform float alpha;
layout (binding = 0) uniform sampler2DShadow shadow_sampler;
layout (binding = 1) uniform sampler2D texture_sampler;
layout (binding = 2) uniform sampler2D normals_sampler;


float lookup(float x, float y) {
	x *= 0.001 * shadow_coordinate.w;
	y *= 0.001 * shadow_coordinate.w;
	return textureProj(shadow_sampler, shadow_coordinate + vec4(x, y, -0.01, 0.0));
}

void main(void) {
	float shadow_factor = 0.0;
	vec3 L = normalize(interpolated_light_direction);
	vec3 N = normalize(interpolated_normal);
	vec3 V = normalize(-v_matrix[3].xyz - interpolated_vertex_position);
	vec3 H = normalize(interpolated_half_vector);

	if (bump_mapped == 1.0) {
		float a = 0.3;
		float b = 400.0;
		float x = original_vertex.x;
		float y = original_vertex.y;
		float z = original_vertex.z;
		N.x = interpolated_normal.x + a*sin(b*x);
		N.y = interpolated_normal.y + a*sin(b*y);
		N.z = interpolated_normal.z + a*sin(b*z);
		N = normalize(N);
	}

	float swidth = 2.5;
	vec2 o = mod(floor(gl_FragCoord.xy), 2.0) * swidth;
	shadow_factor += lookup(-1.5 * swidth + o.x,  1.5 * swidth - o.y);
	shadow_factor += lookup(-1.5 * swidth + o.x, -0.5 * swidth - o.y);
	shadow_factor += lookup(0.5 * swidth + o.x,  1.5 * swidth - o.y);
	shadow_factor += lookup(0.5 * swidth + o.x, -0.5 * swidth - o.y);
	shadow_factor = shadow_factor / 4.0;

	// Smooth shadows.
	float width = 2.5;
	float endp = width * 3.0 + width / 2.0;
	for (float m = -endp; m <= endp; m = m + width) {
		for (float n = -endp; n <= endp; n = n + width) {
			shadow_factor += lookup(m, n);
		}
	}
	shadow_factor = shadow_factor / 64.0;
	shadow_factor = (light_enabled == 1.0) ? shadow_factor : 0.0;

	// Apply shadows.
	vec4 shadow_color = global_ambient * material.ambient + light.ambient * material.ambient;
	vec4 lighted_color = light.diffuse * material.diffuse * max(dot(L, N), 0.0)
				+ light.specular * material.specular
				* pow(max(dot(H, N), 0.0), material.shininess * 3.0);

	lighted_color *= vec4(texture(texture_sampler, texture_coordinate).xyz, 1.0);
	shadow_color *= vec4(texture(texture_sampler, texture_coordinate).xyz, 1.0);
	fragment_color = vec4((shadow_color.xyz + shadow_factor * (lighted_color.xyz)), alpha);

	// Apply fog.
	vec4 fog_color = vec4(1.0, 1.0, 1.0, 1.0);
	float fog_start_distance = 1.0;
	float fog_end_distance = 100.0;
	float distance = length(eye_space_vertex.xyz);
	float fog_factor = clamp(((fog_end_distance - distance) / (fog_end_distance - fog_start_distance)), 0.0, 1.0);
	fragment_color = mix(fog_color, fragment_color, fog_factor);
}
