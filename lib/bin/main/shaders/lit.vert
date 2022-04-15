#version 420 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) out vec2 texCoordsVarying;

layout (location = 4) in vec4 ambientIn;
layout (location = 5) out vec4 ambient;

layout (location = 6) in vec4 diffuseIn;
layout (location = 7) out vec4 diffuse;

layout (location = 8) in vec4 specularIn;
layout (location = 9) out vec4 specular;

layout (binding = 0) uniform Camera
{ 
  mat4 viewProjMatrix;
} camera;

layout (binding = 1) uniform Scene
{ 
  mat4 transformationMatrix;
} scene;

void main() {
  texCoordsVarying = texCoords;
  ambient = ambientIn;
  diffuse = diffuseIn;
  specular = specularIn;
  gl_Position = camera.viewProjMatrix * scene.transformationMatrix * vec4(position);
}