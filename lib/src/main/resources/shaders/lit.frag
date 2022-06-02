#version 420 core

layout (location = 2) in vec2 texCoordsVarying;

layout (location = 0) out vec4 finalColor;

layout (binding = 0) uniform sampler2D tex;

vec4 test;

layout (std140, row_major, binding = 2) uniform Material
{ 
  vec4 ambientColor;
  vec4 diffuseColor;
  vec4 specularColor;
  float isTextured;
  float reflectance;
} material;

vec3 ambientLight;

layout (std140, row_major, binding = 3) uniform DirectionalLight
{
  vec3 lightColor;
  vec3 direction;
  vec3 power;
} directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void main() {
  ambientLight = vec3(1, 1, 1);
  if (material.isTextured == 1) {
    ambientC = texture(tex, texCoordsVarying);
    diffuseC = ambientC;
    specularC = specularC;
  } else {
    ambientC = material.ambientColor;
    diffuseC = material.diffuseColor;
    specularC = material.specularColor;
  }
  finalColor = ambientC * vec4(ambientLight, 1);
}