package Leclair.graphics.renderer;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import static org.lwjgl.vulkan.VK10.*;

public class VKRenderer implements Renderer {

    static VkInstance instance;

    public VKRenderer(final ViewPort viewPort) {
        
    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer requiredLayers = null;
            final PointerBuffer windowExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
            final PointerBuffer ppEnabledExtensionNames = stack.mallocPointer(windowExtensions.capacity() + 1);
            ppEnabledExtensionNames.put(windowExtensions);
            ppEnabledExtensionNames.flip();
            final VkApplicationInfo value = VkApplicationInfo.malloc(stack);
            value.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            value.pNext(0L);
            value.pApplicationName(stack.UTF8("Game"));
            value.applicationVersion(1);
            value.pEngineName(stack.UTF8("Leclair Engine"));
            value.engineVersion(VK_MAKE_VERSION(1, 0, 0));
            value.apiVersion(VK.getInstanceVersionSupported());
            final VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.malloc(stack);
            pCreateInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            pCreateInfo.flags(0);
            pCreateInfo.pApplicationInfo(value);
            pCreateInfo.ppEnabledLayerNames(requiredLayers);
            pCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            final PointerBuffer pInstance = stack.mallocPointer(1);
            if (vkCreateInstance(pCreateInfo, null, pInstance) != VK_SUCCESS) {
                throw new RuntimeException();
            }
            instance = new VkInstance(pInstance.get(0), pCreateInfo);
        }
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
