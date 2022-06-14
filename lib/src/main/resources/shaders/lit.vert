#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 texCoords;
layout (location = 2) out vec2 texCoordsVarying;

uniform mat4 viewProjectionMatrix; // Camera
uniform mat4 transformationMatrix; // Scene

void main() {
  texCoordsVarying = texCoords;
  gl_Position = viewProjectionMatrix * transformationMatrix * vec4(position);
} 