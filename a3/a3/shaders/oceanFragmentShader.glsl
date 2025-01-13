#version 430

in vec3 varyingNormal;
in vec3 varyingLightDir;
in vec3 varyingVertPos;
in vec3 oceanColor;
in vec3 eyeSpacePos;

out vec4 fragColor;

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
uniform sampler2D colorBuffer; // Color buffer texture


void main(void) {
    vec3 L = normalize(varyingLightDir);
    vec3 N = normalize(varyingNormal);
    vec3 V = normalize(-v_matrix[3].xyz - varyingVertPos);
    vec3 R = normalize(reflect(-L, N));
    float cosTheta = dot(L, N);
    float cosPhi = dot(V, R);

    vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * max(cosTheta, 0.0);
    vec3 specular = light.specular.xyz * material.specular.xyz * pow(max(cosPhi, 0.0), material.shininess);

    fragColor = clamp(vec4(oceanColor + specular, 1.0), 0.0, 1.0);

    vec4 fogColor = vec4(185.0 / 255, 207.0 / 255, 218.0 / 255, 1.0);
    float fogStart = 1;
    float fogEnd = 500;

    float distance = length(eyeSpacePos.xyz);
    float fogFactor = clamp(((fogEnd - distance) / (fogEnd - fogStart)), 0.0, 1.0);
    fragColor = mix(fogColor, fragColor, fogFactor);
}