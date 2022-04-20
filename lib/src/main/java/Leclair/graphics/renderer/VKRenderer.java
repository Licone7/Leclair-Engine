package Leclair.graphics.renderer;

import org.lwjgl.vulkan.VkInstance;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;

public class VKRenderer implements Renderer {

    static VkInstance instance;

    public VKRenderer(final ViewPort viewPort) {
        
    }

    @Override
    public void init() {
        
    }

    @Override
    public void ReadInfoFromGPU() {

    }

    @Override
    public void loop() {

    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {

    }

    @Override
    public void addMesh(final Mesh mesh) {

    }

    @Override
    public void deleteMesh(final Mesh mesh) {

    }

    @Override
    public void setWireframe(final boolean enabled) {

    }

    @Override
    public void addShader(final Shader shader, final int program, final int index) {

    }

    @Override
    public void deleteShader(final Shader shader) {

    }

    @Override
    public void cleanup() {

    }

}
