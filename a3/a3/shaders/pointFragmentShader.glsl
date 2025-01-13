#version 430

out vec4 fragColor;

uniform vec3 position;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;

void main(void) {
    fragColor = vec4(1.0, 1.0, 0.0, 1.0);
}