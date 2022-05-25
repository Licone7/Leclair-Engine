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
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkWin32SurfaceCreateInfoKHR;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import Leclair.window.WindowInfo;

public class VKRenderer implements Renderer {

    static boolean multiDrawIndirectSupported = false;
    static PointerBuffer extensionList = MemoryUtil.memAllocPointer(64);
    static final ByteBuffer KHR_Surface = MemoryUtil.memASCII(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_Win32_Surface = MemoryUtil
            .memASCII(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_swapchain = MemoryUtil.memASCII(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    static VkInstance instance;
    static long surface;
    static VkPhysicalDevice physicalDevice;
    static VkDevice device;
    static VkQueue queue;

    static int queueFamilyIndex;

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
            if (vkCreateInstance(vkInstanceCreateInfo, null, pInstance) != VK_SUCCESS) {
                throw new IllegalStateException(
                        "Vulkan instance creation failed; ensure you have a Vulkan ICD installed!");
            }
            instance = new VkInstance(pInstance.get(0), vkInstanceCreateInfo);

            // CREATE WINDOW SURFACE

            if (Platform.get() == Platform.WINDOWS) {
                VkWin32SurfaceCreateInfoKHR vkWin32SurfaceCreateInfoKHR = VkWin32SurfaceCreateInfoKHR.malloc(stack);
                vkWin32SurfaceCreateInfoKHR.sType$Default();
                vkWin32SurfaceCreateInfoKHR.hinstance(WindowsLibrary.HINSTANCE);
                vkWin32SurfaceCreateInfoKHR.hwnd(WindowInfo.window().getWHandle());
                LongBuffer pSurface = stack.mallocLong(1);
                if (KHRWin32Surface.vkCreateWin32SurfaceKHR(instance, vkWin32SurfaceCreateInfoKHR, null,
                        pSurface) != VK_SUCCESS) {
                    throw new IllegalStateException("Window surface creation failed!");
                }

                surface = pSurface.get(0);
            }

            // FIND VULKANS GPUS

            // TODO: We need a better way of finding suitable physical devices
            IntBuffer pPhysicalDeviceCount = stack.mallocInt(1);
            vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, null);
            PointerBuffer pPhysicalDevices = stack.mallocPointer(pPhysicalDeviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
            if (pPhysicalDeviceCount.get(0) > 0) {
                // int selectedQueueFamilyIndex = Integer.MAX_VALUE;
                for (int i = 0; i < pPhysicalDeviceCount.get(0); ++i) {
                    long handle = pPhysicalDevices.get(i);
                    VkPhysicalDevice checkPhysicalDevice = new VkPhysicalDevice(handle, instance);
                    VkPhysicalDeviceProperties pProperties = VkPhysicalDeviceProperties.malloc(stack);
                    VkPhysicalDeviceFeatures pFeatures = VkPhysicalDeviceFeatures.malloc(stack);
                    vkGetPhysicalDeviceProperties(checkPhysicalDevice, pProperties);
                    vkGetPhysicalDeviceFeatures(checkPhysicalDevice, pFeatures);
                    if (VK_API_VERSION_MINOR(pProperties.apiVersion()) < 1) { // Vulkan 1.1 needs to be minimum
                        continue; // Keeping 1.1 minimum might not be needed, needs research
                    }
                    if (pFeatures.multiDrawIndirect() == true) {
                        // If multiple draw indirect is supported...AWESOME
                        multiDrawIndirectSupported = true;
                    }

                    IntBuffer pQueueFamilyPropertyCount = stack.mallocInt(1);
                    vkGetPhysicalDeviceQueueFamilyProperties(checkPhysicalDevice, pQueueFamilyPropertyCount, null);
                    if (pQueueFamilyPropertyCount.get(0) == 0) {
                        continue;
                    }
                    VkQueueFamilyProperties.Buffer pQueueFamilyProperties = VkQueueFamilyProperties
                            .calloc(pQueueFamilyPropertyCount.get(0), stack);
                    vkGetPhysicalDeviceQueueFamilyProperties(checkPhysicalDevice, pQueueFamilyPropertyCount,
                            pQueueFamilyProperties);
                    for (int j = 0; j < pQueueFamilyPropertyCount.get(0); ++j) {
                        if ((pQueueFamilyProperties.get(j).queueCount() > 0)
                                && (pQueueFamilyProperties.get(j).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                            queueFamilyIndex = j;
                            break;
                        }
                    }
                    physicalDevice = checkPhysicalDevice;
                }
            }
            if (physicalDevice == null) {
                throw new IllegalStateException("No GPUs compatible with Vulkan were found!");
            }
            VkDeviceQueueCreateInfo.Buffer vkDeviceQueueCreateInfo = VkDeviceQueueCreateInfo.malloc(1, stack);
            vkDeviceQueueCreateInfo.sType$Default();
            vkDeviceQueueCreateInfo.pNext(0);
            vkDeviceQueueCreateInfo.flags(0);
            vkDeviceQueueCreateInfo.queueFamilyIndex(queueFamilyIndex);
            vkDeviceQueueCreateInfo.pQueuePriorities(stack.floats(1.0f));
            VkDeviceCreateInfo pCreateInfo = VkDeviceCreateInfo.malloc(stack);
            pCreateInfo.sType$Default();
            pCreateInfo.pNext(0);
            pCreateInfo.flags(0);
            pCreateInfo.pQueueCreateInfos(vkDeviceQueueCreateInfo);
            pCreateInfo.ppEnabledLayerNames(null);
            pCreateInfo.ppEnabledExtensionNames(null);
            PointerBuffer pDevice = stack.mallocPointer(1);
            if (vkCreateDevice(physicalDevice, pCreateInfo, null, pDevice) != VK_SUCCESS) {
                throw new IllegalStateException();
            }
            device = new VkDevice(pDevice.get(0), physicalDevice, pCreateInfo);
            PointerBuffer pQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device, queueFamilyIndex, 0, pQueue);
            queue = new VkQueue(pQueue.get(0), device);
            
            // boolean foundSwapchainExtension = false;
            // IntBuffer pPropertyCount = stack.mallocInt(1);
            // vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null,
            //         pPropertyCount, null);
            // if (pPropertyCount.get(0) > 0) {
            //     VkExtensionProperties.Buffer deviceExtensions = VkExtensionProperties.malloc(pPropertyCount.get(0),
            //             stack);
            //     vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null,
            //             pPropertyCount, deviceExtensions);
            //     for (int i = 0; i < pPropertyCount.get(0); i++) {
            //         deviceExtensions.position(i);
            //         if (VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(deviceExtensions.extensionNameString())) {
            //             foundSwapchainExtension = true;
            //             extensionList.put(KHR_swapchain);
            //         }
            //     }
            // }
            // if (foundSwapchainExtension == false) {
            //     throw new IllegalStateException("The VK_KHR_swapchain extension could not be found");
            // }

        }
    }

    @Override
    public void printCapabilities() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPhysicalDeviceProperties pProperties = VkPhysicalDeviceProperties.malloc(stack);
            vkGetPhysicalDeviceProperties(physicalDevice, pProperties);
            final String api = "API: Vulkan " + VK_API_VERSION_MAJOR(pProperties.apiVersion()) + "."
                    + VK_API_VERSION_MINOR(pProperties.apiVersion());
            final String renderer = "Renderer: " + pProperties.deviceNameString();
            System.out.println("Graphics Info:");
            System.out.println(api);
            System.out.println(renderer);
            System.out.println("_____");
        }
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
        vkDeviceWaitIdle(device);
        vkDestroyDevice(device, null);
        vkDestroyInstance(instance, null);
        MemoryUtil.memFree(KHR_Surface);
        MemoryUtil.memFree(KHR_Win32_Surface);
        MemoryUtil.memFree(KHR_swapchain);
        // extensionList.free();
    }
}