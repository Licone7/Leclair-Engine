plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // LWJGL IMPLEMENTATION
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.1"))

	implementation("org.lwjgl", "lwjgl")
	implementation("org.lwjgl", "lwjgl-assimp")
	implementation("org.lwjgl", "lwjgl-openal")
	implementation("org.lwjgl", "lwjgl-opengl")
	implementation("org.lwjgl", "lwjgl-shaderc")
	implementation("org.lwjgl", "lwjgl-stb")
	implementation("org.lwjgl", "lwjgl-vulkan")
    // LWJGL NATIVES LINUX X64
    implementation("org.lwjgl", "lwjgl", classifier = "natives-linux")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-linux")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-linux")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-linux")
    // LWJGL NATIVES LINUX ARM64
    implementation("org.lwjgl", "lwjgl", classifier = "natives-linux-arm64")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux-arm64")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-linux-arm64")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux-arm64")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-linux-arm64")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-linux-arm64")
    // LWJGL NATIVES LINUX ARM32
    implementation("org.lwjgl", "lwjgl", classifier = "natives-linux-arm32")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux-arm32")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-linux-arm32")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux-arm32")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-linux-arm32")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-linux-arm32")
    // LWJGL NATIVES MACOS X64
    implementation("org.lwjgl", "lwjgl", classifier = "natives-macos")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-macos")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-macos")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-macos")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-macos")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-macos")
	implementation("org.lwjgl", "lwjgl-vulkan", classifier = "natives-macos")
    // LWJGL NATIVES MACOS ARM64
    implementation("org.lwjgl", "lwjgl", classifier = "natives-macos-arm64")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-macos-arm64")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-macos-arm64")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-macos-arm64")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-macos-arm64")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-macos-arm64")
	implementation("org.lwjgl", "lwjgl-vulkan", classifier = "natives-macos-arm64")
    // LWJGL NATIVES WINDOWS X64
	implementation("org.lwjgl", "lwjgl", classifier = "natives-windows")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-windows")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-windows")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-windows")
    // LWJGL NATIVES WINDOWS X86
    implementation("org.lwjgl", "lwjgl", classifier = "natives-windows-x86")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows-x86")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-windows-x86")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows-x86")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-windows-x86")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-windows-x86")
    // LWJGL NATIVES WINDOWS ARM64
    implementation("org.lwjgl", "lwjgl", classifier = "natives-windows-arm64")
	implementation("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows-arm64")
	implementation("org.lwjgl", "lwjgl-openal", classifier = "natives-windows-arm64")
	implementation("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows-arm64")
	implementation("org.lwjgl", "lwjgl-shaderc", classifier = "natives-windows-arm64")
	implementation("org.lwjgl", "lwjgl-stb", classifier = "natives-windows-arm64")
    // JOML
	implementation("org.joml", "joml", "1.10.4")
}
