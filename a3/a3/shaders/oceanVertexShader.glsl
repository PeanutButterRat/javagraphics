// Noise functions come from The Book of Shaders by Patricio Gonzalez Vivo & Jen Lowe
// https://thebookofshaders.com/edit.php#11/2d-snoise-clear.frag

#version 430

layout (location = 0) in vec3 vertex;

out vec3 varyingNormal;
out vec3 varyingLightDir;
out vec3 varyingVertPos;
out vec3 oceanColor;
out vec3 eyeSpacePos;

struct PositionalLight {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec3 position;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
uniform vec2 offset;
uniform vec2 resolution;

// ***** From The Book of Shaders by Patricio Gonzalez Vivo & Jen Lowe *****
vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec3 permute(vec3 x) { return mod289(((x * 34.0) + 1.0) * x); }

float snoise(vec2 v) {
    const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);

    vec2 i  = floor(v + dot(v, C.yy));
    vec2 x0 = v - i + dot(i, C.xx);

    vec2 i1 = vec2(0.0);
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec2 x1 = x0.xy + C.xx - i1;
    vec2 x2 = x0.xy + C.zz;

    i = mod289(i);
    vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0)) + i.x + vec3(0.0, i1.x, 1.0 ));

    vec3 m = max(0.5 - vec3(dot(x0, x0), dot(x1, x1), dot(x2, x2)), 0.0);
    m = m * m;
    m = m * m;

    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;

    m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);

    vec3 g = vec3(0.0);
    g.x  = a0.x * x0.x + h.x * x0.y;
    g.yz = a0.yz * vec2(x1.x, x2.x) + h.yz * vec2(x1.y, x2.y);
    return 130.0 * dot(m, g);
}
// ***** End Book of Shaders code *****

void main() {
    // This little bit is also from The Book of Shaders.
    vec2 st = vertex.xz / resolution + offset;
    st.x *= resolution.x / resolution.y;
    st *= 10.0;
    // End.

    vec3 a = vec3(vertex.x, snoise(st) * 0.25 + 0.5, vertex.z);
    vec3 b = vec3(vertex.x, snoise(st + vec2(0.001, 0.0)) * 0.25 + 0.5, vertex.z);
    vec3 c = vec3(vertex.x, snoise(st + vec2(0.0, 0.001)) * 0.25 + 0.5, vertex.z);
    vec3 normal = normalize(cross(a - b, a - c));

    varyingVertPos = (m_matrix * vec4(vertex, 1.0)).xyz;
    varyingLightDir = light.position - varyingVertPos;
    varyingNormal = (norm_matrix * vec4(normal, 1.0)).xyz;
    gl_Position = p_matrix * v_matrix * m_matrix * vec4(a, 1.0);
    eyeSpacePos = (v_matrix * m_matrix * vec4(a, 1.0)).xyz;

    vec3 darkColor = vec3(0.0, 0.294, 0.451);
    vec3 lightColor = vec3(0.047, 0.655, 0.98);
    oceanColor = darkColor + (lightColor - darkColor) * a.y;
}
