plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.1"))

	implementation("org.lwjgl", "lwjgl")
	implementation("org.lwjgl", "lwjgl-assimp")
	implementation("org.lwjgl", "lwjgl-glfw")
	implementation("org.lwjgl", "lwjgl-openal")
	implementation("org.lwjgl", "lwjgl-opengl")
	implementation("org.lwjgl", "lwjgl-shaderc")
	implementation("org.lwjgl", "lwjgl-stb")
	implementation("org.lwjgl", "lwjgl-vulkan")
    // LINUX X64
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-linux")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-linux")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-linux")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-linux")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-linux")
    // LINUX ARM64
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-linux-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-linux-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-linux-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-linux-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-linux-arm64")
    // LINUX ARM32
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-linux-arm32")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-linux-arm32")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-linux-arm32")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-linux-arm32")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-linux-arm32")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-linux-arm32")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-linux-arm32")
    // MACOS X64
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-macos")
	runtimeOnly("org.lwjgl", "lwjgl-vulkan", classifier = "natives-macos")
    // MACOS ARM64
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-macos-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-vulkan", classifier = "natives-macos-arm64")
    // WINDOWS X64
	runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-windows")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-windows")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-windows")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-windows")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-windows")
    // WINDOWS X86
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-windows-x86")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows-x86")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-windows-x86")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-windows-x86")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows-x86")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-windows-x86")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-windows-x86")
    // WINDOWS ARM64
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-windows-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-windows-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-windows-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = "natives-windows-arm64")
	runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-windows-arm64")
    // JOML
	implementation("org.joml", "joml", "1.10.4")
}
