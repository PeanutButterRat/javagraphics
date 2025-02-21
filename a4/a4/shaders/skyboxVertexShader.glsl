#version 430

layout (location = 0) in vec3 position;

out vec3 tc;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
layout (binding = 0) uniform samplerCube samp;

void main(void)
{
	tc = position;
	gl_Position = p_matrix * mat4(mat3(v_matrix)) * vec4(position,1.0);
}
