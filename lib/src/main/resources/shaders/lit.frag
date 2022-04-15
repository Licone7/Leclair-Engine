#version 420 core

layout (location = 2) in vec2 texCoordsVarying;

layout (location = 5) in vec4 ambient;
layout (location = 7) in vec4 diffuse;
layout (location = 9) in vec4 specular;

layout (location = 0) out vec4 color;

layout (binding = 0) uniform sampler2D tex;

void main() {
  vec4 multiplier = ambient + diffuse + specular;
  color = texture(tex, texCoordsVarying);
}