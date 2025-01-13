#version 430

out vec4 vcolor;

uniform vec4 a_pos;
uniform vec4 b_pos;
uniform vec4 c_pos;

uniform vec4 a_color;
uniform vec4 b_color;
uniform vec4 c_color;


void main(void)
{
    if (gl_VertexID == 0) {
        gl_Position = a_pos;
        vcolor = a_color;
    }
    else if (gl_VertexID == 1) {
        gl_Position = b_pos;
        vcolor = b_color;
    }
    else {
        gl_Position = c_pos;
        vcolor = c_color;
    }
}