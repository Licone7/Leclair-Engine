package Leclair.graphics.renderer;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.WindowsLibrary;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.KHRWin32Surface;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkWin32SurfaceCreateInfoKHR;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import Leclair.window.WindowInfo;

public class VKRenderer implements Renderer {

    static VkInstance instance;

    public VKRenderer(final ViewPort viewPort) {

    }

    @Override
    public void init() {
        GLFW.glfwInit();
        GLFWVulkan.glfwVulkanSupported();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer extensionList= stack.mallocPointer(64);
            PointerBuffer layerList = null;
            extensionList.put(MemoryUtil.memASCII(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME));
            if (Platform.get() == Platform.WINDOWS) {
                extensionList.put(MemoryUtil.memASCII(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME));
            }
            extensionList.flip();
            VkApplicationInfo vkApplicationInfo = VkApplicationInfo.calloc(stack);
            vkApplicationInfo.sType$Default();
            vkApplicationInfo.pNext(0);
            vkApplicationInfo.pApplicationName(stack.UTF8(WindowInfo.getTitle()));
            vkApplicationInfo.pEngineName(stack.UTF8("Leclair Engine"));
            vkApplicationInfo.engineVersion(1);
            vkApplicationInfo.apiVersion(VK.getInstanceVersionSupported());
            VkInstanceCreateInfo vkInstanceCreateInfo = VkInstanceCreateInfo.calloc(stack);
            vkInstanceCreateInfo.sType$Default();
            vkInstanceCreateInfo.pNext(0);
            vkInstanceCreateInfo.flags(0);
            vkInstanceCreateInfo.pApplicationInfo(vkApplicationInfo);
            vkInstanceCreateInfo.ppEnabledLayerNames(layerList);
            vkInstanceCreateInfo.ppEnabledExtensionNames(extensionList);
            PointerBuffer pInstance = stack.mallocPointer(1);
            VK11.vkCreateInstance(vkInstanceCreateInfo, null, pInstance);
            instance = new VkInstance(pInstance.get(0), vkInstanceCreateInfo);
            if (Platform.get() == Platform.WINDOWS) {
            VkWin32SurfaceCreateInfoKHR vkWin32SurfaceCreateInfoKHR = VkWin32SurfaceCreateInfoKHR.calloc(stack);
            vkWin32SurfaceCreateInfoKHR.sType$Default();
            vkWin32SurfaceCreateInfoKHR.hinstance(WindowsLibrary.HINSTANCE);
            vkWin32SurfaceCreateInfoKHR.hwnd(WindowInfo.window().getWHandle());
            LongBuffer pSurface = stack.mallocLong(1);
            KHRWin32Surface.vkCreateWin32SurfaceKHR(instance, vkWin32SurfaceCreateInfoKHR, null, pSurface);
            }
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
