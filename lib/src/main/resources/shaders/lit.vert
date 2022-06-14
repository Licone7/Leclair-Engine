#version 420 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) out vec2 texCoordsVarying;

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
  gl_Position = camera.viewProjMatrix * scene.transformationMatrix * vec4(position);
} 