#version 330 core

in vec2 outTexCoords; // "Out" from vertex shader, not fragment shader
out vec4 finalColor;
uniform sampler2D tex;

void main() {
  vec4 color = texture(tex, outTexCoords);
  finalColor = color * vec4(2, 1, 1, 1);
} 