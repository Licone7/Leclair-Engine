#version 330 core

in vec4 position;
in vec2 inTexCoords;
out vec2 outTexCoords;

uniform mat4 viewProjectionMatrix; // Camera
uniform mat4 transformationMatrix; // Scene

void main() {
  outTexCoords = inTexCoords;
  gl_Position = viewProjectionMatrix * transformationMatrix * vec4(position);
} 