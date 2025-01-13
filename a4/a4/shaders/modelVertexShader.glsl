#version 430

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec2 vertex_texture_coordinate;
layout (location = 2) in vec3 vertex_normal;

out vec3 interpolated_normal;
out vec3 interpolated_light_direction;
out vec3 interpolated_vertex_position;
out vec3 interpolated_half_vector;
out vec4 shadow_coordinate;
out vec2 texture_coordinate;
out vec3 original_vertex;
out vec3 eye_space_vertex;

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


void main(void) {
	interpolated_vertex_position = (m_matrix * vec4(vertex_position,1.0)).xyz;
	interpolated_light_direction = light.position - interpolated_vertex_position;
	interpolated_normal = (normals_matrix * vec4(vertex_normal, 1.0)).xyz;
	interpolated_half_vector = (interpolated_light_direction - interpolated_vertex_position).xyz;
	shadow_coordinate = shadow_mvp * vec4(vertex_position, 1.0);
	gl_Position = p_matrix * v_matrix * m_matrix * vec4(vertex_position, 1.0);
	texture_coordinate = vertex_texture_coordinate;
	original_vertex = vertex_position;
	eye_space_vertex = (v_matrix * m_matrix * vec4(vertex_position, 1.0)).xyz;
}
