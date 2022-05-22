package Leclair.graphics.renderer;

import static Leclair.memory.Allocator.ib1;
import static Leclair.memory.Allocator.lb1;
import static Leclair.memory.Allocator.pb1;
import static org.lwjgl.vulkan.VK10.VK_ERROR_INCOMPATIBLE_DRIVER;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.WindowsLibrary;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRWin32Surface;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkWin32SurfaceCreateInfoKHR;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import Leclair.window.WindowInfo;

public class VKRenderer implements Renderer {

    static int vkResult = VK_SUCCESS;
    static VkInstance instance;
    static long surface;
    static VkPhysicalDevice physicalDevice;

    public VKRenderer(final ViewPort viewPort) {

    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            // EXTENSIONS

            PointerBuffer extensionList = MemoryUtil.memAllocPointer(64);
            PointerBuffer layerList = null;
            extensionList.put(MemoryUtil.memASCII(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME));
            if (Platform.get() == Platform.WINDOWS) {
                extensionList.put(MemoryUtil.memASCII(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME));
            }
            extensionList.flip();

            // CREATE VULKAN INSTANCE

            VkApplicationInfo vkApplicationInfo = VkApplicationInfo.calloc(stack);
            vkApplicationInfo.sType$Default();
            vkApplicationInfo.pNext(0);
            vkApplicationInfo.apiVersion(VK.getInstanceVersionSupported());
            VkInstanceCreateInfo vkInstanceCreateInfo = VkInstanceCreateInfo.calloc(stack);
            vkInstanceCreateInfo.sType$Default();
            vkInstanceCreateInfo.pNext(0);
            vkInstanceCreateInfo.flags(0);
            vkInstanceCreateInfo.pApplicationInfo(vkApplicationInfo);
            vkInstanceCreateInfo.ppEnabledLayerNames(layerList);
            vkInstanceCreateInfo.ppEnabledExtensionNames(extensionList);
            vkResult = vkCreateInstance(vkInstanceCreateInfo, null, pb1);
            switch (vkResult) {
                case VK_SUCCESS:
                    // Continue
                    break;
                case VK_ERROR_INCOMPATIBLE_DRIVER:
                    throw new IllegalStateException("A compatible Vulkan ICD was not found!");
            }
            instance = new VkInstance(pb1.get(0), vkInstanceCreateInfo);
            extensionList.free();

            // CREATE WINDOW SURFACE

            if (Platform.get() == Platform.WINDOWS) {
                VkWin32SurfaceCreateInfoKHR vkWin32SurfaceCreateInfoKHR = VkWin32SurfaceCreateInfoKHR.calloc(stack);
                vkWin32SurfaceCreateInfoKHR.sType$Default();
                vkWin32SurfaceCreateInfoKHR.hinstance(WindowsLibrary.HINSTANCE);
                vkWin32SurfaceCreateInfoKHR.hwnd(WindowInfo.window().getWHandle());
                vkResult = KHRWin32Surface.vkCreateWin32SurfaceKHR(instance, vkWin32SurfaceCreateInfoKHR, null,
                        lb1);
                switch (vkResult) {
                    case VK_SUCCESS:
                        break;
                    default:
                        throw new IllegalStateException();
                }
                surface = lb1.get(0);
            }

            // FIND VULKANS GPUS

            vkEnumeratePhysicalDevices(instance, ib1, null);
            if (ib1.get(0) > 0) {
                PointerBuffer physicalDevices = stack.mallocPointer(ib1.get(0));
                vkEnumeratePhysicalDevices(instance, ib1, physicalDevices);
                physicalDevice = new VkPhysicalDevice(physicalDevices.get(0), instance);
            } else {
                throw new IllegalStateException("No Vulkan compatible GPUs were found for use");
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
