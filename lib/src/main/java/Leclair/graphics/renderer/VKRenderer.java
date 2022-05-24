package Leclair.graphics.renderer;

import static org.lwjgl.vulkan.VK11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.KHRSwapchain.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.WindowsLibrary;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRWin32Surface;
import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
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
    static PointerBuffer extensionList = MemoryUtil.memAllocPointer(64);
    static final ByteBuffer KHR_Surface = MemoryUtil.memASCII(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_Win32_Surface = MemoryUtil
            .memASCII(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_swapchain = MemoryUtil.memASCII(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    static VkInstance instance;
    static long surface;
    static VkPhysicalDevice physicalDevice;

    public VKRenderer(final ViewPort viewPort) {

    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            // EXTENSIONS

            PointerBuffer layerList = null;
            extensionList.put(KHR_Surface);
            if (Platform.get() == Platform.WINDOWS) {
                extensionList.put(KHR_Win32_Surface);
            }
            extensionList.flip();

            // CREATE VULKAN INSTANCE

            VkApplicationInfo vkApplicationInfo = VkApplicationInfo.calloc(stack); // Convert to malloc later
            vkApplicationInfo.sType$Default();
            vkApplicationInfo.pNext(0);
            vkApplicationInfo.apiVersion(VK.getInstanceVersionSupported());
            VkInstanceCreateInfo vkInstanceCreateInfo = VkInstanceCreateInfo.malloc(stack);
            vkInstanceCreateInfo.sType$Default();
            vkInstanceCreateInfo.pNext(0);
            vkInstanceCreateInfo.flags(0);
            vkInstanceCreateInfo.pApplicationInfo(vkApplicationInfo);
            vkInstanceCreateInfo.ppEnabledLayerNames(layerList);
            vkInstanceCreateInfo.ppEnabledExtensionNames(extensionList);
            PointerBuffer pInstance = stack.mallocPointer(1);
            vkResult = vkCreateInstance(vkInstanceCreateInfo, null, pInstance);
            switch (vkResult) {
                case VK_SUCCESS:
                    // Continue
                    break;
                case VK_ERROR_INCOMPATIBLE_DRIVER:
                    throw new IllegalStateException("A compatible Vulkan ICD was not found!");
            }
            instance = new VkInstance(pInstance.get(0), vkInstanceCreateInfo);

            // CREATE WINDOW SURFACE

            if (Platform.get() == Platform.WINDOWS) {
                VkWin32SurfaceCreateInfoKHR vkWin32SurfaceCreateInfoKHR = VkWin32SurfaceCreateInfoKHR.malloc(stack);
                vkWin32SurfaceCreateInfoKHR.sType$Default();
                vkWin32SurfaceCreateInfoKHR.hinstance(WindowsLibrary.HINSTANCE);
                vkWin32SurfaceCreateInfoKHR.hwnd(WindowInfo.window().getWHandle());
                LongBuffer pSurface = stack.mallocLong(1);
                vkResult = KHRWin32Surface.vkCreateWin32SurfaceKHR(instance, vkWin32SurfaceCreateInfoKHR, null,
                        pSurface);
                switch (vkResult) {
                    case VK_SUCCESS:
                        break;
                    default:
                        throw new IllegalStateException();
                }
                surface = pSurface.get(0);
            }

            // FIND VULKANS GPUS

            IntBuffer pPhysicalDeviceCount = stack.mallocInt(1);
            vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, null);
            if (pPhysicalDeviceCount.get(0) > 0) {
                PointerBuffer pPhysicalDevices = stack.mallocPointer(pPhysicalDeviceCount.get(0));
                vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
                physicalDevice = new VkPhysicalDevice(pPhysicalDevices.get(0), instance);
            } else {
                throw new IllegalStateException("No Vulkan compatible GPUs were found for use");
            }
            boolean foundSwapchainExtension = false;
            IntBuffer pPropertyCount = stack.mallocInt(1);
            vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null, pPropertyCount, null);
            if (pPropertyCount.get(0) > 0) {
                VkExtensionProperties.Buffer deviceExtensions = VkExtensionProperties.malloc(pPropertyCount.get(0), stack);
                vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null, pPropertyCount, deviceExtensions);
                for (int i = 0; i < pPropertyCount.get(0); i++) {
                    deviceExtensions.position(i);
                    if (VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(deviceExtensions.extensionNameString())) {
                        foundSwapchainExtension = true;
                        extensionList.put(KHR_swapchain);
                    }
                }
            }
            if (foundSwapchainExtension == false) {
                throw new IllegalStateException("The VK_KHR_swapchain extension could not be found");
            }
            
        }
    }

    @Override
    public void printCapabilities() {

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
        KHRSurface.vkDestroySurfaceKHR(instance, surface, null);
        vkDestroyInstance(instance, null);
        MemoryUtil.memFree(KHR_Surface);
        MemoryUtil.memFree(KHR_Win32_Surface);
        MemoryUtil.memFree(KHR_swapchain);
        // extensionList.free();
    }
}