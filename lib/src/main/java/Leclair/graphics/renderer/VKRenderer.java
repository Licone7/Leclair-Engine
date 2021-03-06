package Leclair.graphics.renderer;

import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.WindowsLibrary;
import org.lwjgl.vulkan.KHRWin32Surface;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkWin32SurfaceCreateInfoKHR;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import Leclair.window.Window;

/**
 * @since v1
 * @author Kane Burnett
 */
public class VKRenderer implements GraphicsRenderer {

    // Engine Variables
    static Window window;
    static ViewPort viewPort;
    // VARIABLES INITIALIZED
    static boolean validate = false; // This should be true only for testing, otherwise it'll always be false
    static boolean multiDrawIndirectSupported = false;
    static final ByteBuffer EXT_debug_report = MemoryUtil.memASCII(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
    static final ByteBuffer KHR_Surface = MemoryUtil.memASCII(VK_KHR_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_Win32_Surface = MemoryUtil
            .memASCII(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_swapchain = MemoryUtil.memASCII(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    // VARIABLES NOT INITIALIZED
    static VkInstance instance;
    static long surface;
    static VkPhysicalDevice physicalDevice;
    static VkDevice device;
    static int graphicsQueueFamilyIndex;
    static int presentQueueFamilyIndex;
    static VkQueue graphicsQueue;
    static VkQueue presentQueue;
    static long imageAcquiredSemaphore;
    static long renderingFinishedSemaphore;
    static long swapchain;
    static int imageFormat;
    static long images[];
    static long imageViews[];
    static long renderPass;
    static long framebuffers[];
    static long commandPool;
    static VkCommandBuffer[] commandBuffers;
    static long[] fences;

    public VKRenderer(final Window window, final ViewPort viewPort) {

    }

    @Override
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            // EXTENSIONS

            PointerBuffer layerList = null;
            PointerBuffer ppEnabledExtensionNames = stack.mallocPointer(5); // Shouldn't be many required
            ppEnabledExtensionNames.put(KHR_Surface);
            if (Platform.get() == Platform.WINDOWS) {
                ppEnabledExtensionNames.put(KHR_Win32_Surface);
            }
            ppEnabledExtensionNames.flip();

            // CREATE VULKAN INSTANCE

            VkApplicationInfo vkApplicationInfo = VkApplicationInfo.calloc(stack); // Convert to malloc later
            vkApplicationInfo.sType$Default();
            vkApplicationInfo.pNext(0);
            vkApplicationInfo.apiVersion(VK_API_VERSION_1_1);
            VkInstanceCreateInfo pInstanceCreateInfo = VkInstanceCreateInfo.malloc(stack);
            pInstanceCreateInfo.sType$Default();
            pInstanceCreateInfo.pNext(0);
            pInstanceCreateInfo.flags(0);
            pInstanceCreateInfo.pApplicationInfo(vkApplicationInfo);
            pInstanceCreateInfo.ppEnabledLayerNames(layerList);
            pInstanceCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            PointerBuffer pInstance = stack.mallocPointer(1);
            if (vkCreateInstance(pInstanceCreateInfo, null, pInstance) != VK_SUCCESS) {
                throw new IllegalStateException(
                        "Vulkan instance creation failed; ensure you have a Vulkan ICD installed!");
            }
            instance = new VkInstance(pInstance.get(0), pInstanceCreateInfo);
            ppEnabledExtensionNames.clear();

            // CREATE WINDOW SURFACE

            switch (Platform.get()) {
                case WINDOWS:
                    VkWin32SurfaceCreateInfoKHR pWin32SurfaceCreateInfoKHR = VkWin32SurfaceCreateInfoKHR.malloc(stack);
                    pWin32SurfaceCreateInfoKHR.sType$Default();
                    pWin32SurfaceCreateInfoKHR.pNext(0);
                    pWin32SurfaceCreateInfoKHR.flags(0);
                    pWin32SurfaceCreateInfoKHR.hinstance(WindowsLibrary.HINSTANCE);
                    pWin32SurfaceCreateInfoKHR.hwnd(window.getNativeWindowHandle());
                    LongBuffer pSurface = stack.mallocLong(1);
                    if (KHRWin32Surface.vkCreateWin32SurfaceKHR(instance, pWin32SurfaceCreateInfoKHR, null,
                            pSurface) != VK_SUCCESS) {
                        throw new IllegalStateException("Window surface creation failed!");
                    }
                    surface = pSurface.get(0);
                    break;
                case MACOSX:
                    // TODO
                    break;
                case LINUX:
                    // TODO
                    break;
            }

            // FIND VULKANS GPUS

            // TODO: We need a better way of finding suitable physical devices
            IntBuffer pPhysicalDeviceCount = stack.mallocInt(1);
            vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, null);
            PointerBuffer pPhysicalDevices = stack.mallocPointer(pPhysicalDeviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
            for (int i = 0; i < pPhysicalDeviceCount.get(0); ++i) {
                long handle = pPhysicalDevices.get(i);
                VkPhysicalDevice checkPhysicalDevice = new VkPhysicalDevice(handle, instance);
                VkPhysicalDeviceProperties pProperties = VkPhysicalDeviceProperties.malloc(stack);
                vkGetPhysicalDeviceProperties(checkPhysicalDevice, pProperties);
                if (VK_API_VERSION_MINOR(pProperties.apiVersion()) < 1) { // Vulkan 1.1 needs to be minimum
                    continue; // Keeping 1.1 minimum might not be needed, needs research
                }
                physicalDevice = checkPhysicalDevice;
            }
            if (physicalDevice == null) {
                throw new IllegalStateException("No GPUs compatible with Vulkan were found!");
            }

            // CHECK FOR MULTIPLE DRAW INDIRECT SUPPORT

            VkPhysicalDeviceFeatures pFeatures = VkPhysicalDeviceFeatures.malloc(stack);
            vkGetPhysicalDeviceFeatures(physicalDevice, pFeatures);
            if (pFeatures.multiDrawIndirect() == true) {
                // If multiple draw indirect is supported...AWESOME
                multiDrawIndirectSupported = true;
            }

            // FIND GRAPHICS AND PRESENT QUEUES

            IntBuffer pQueueFamilyPropertyCount = stack.mallocInt(1);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
            VkQueueFamilyProperties.Buffer pQueueFamilyProperties = VkQueueFamilyProperties
                    .malloc(pQueueFamilyPropertyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, pQueueFamilyProperties);
            int checkGraphicsQueueFamilyIndex = Integer.MAX_VALUE;
            int checkPresentQueueFamilyIndex = Integer.MAX_VALUE;
            IntBuffer supportsPresent = stack.mallocInt(pQueueFamilyProperties.capacity());
            for (int i = 0; i < supportsPresent.capacity(); i++) {
                supportsPresent.position(i);
                vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, i, surface, supportsPresent);
            }
            for (int i = 0; i < supportsPresent.capacity(); i++) {
                if ((pQueueFamilyProperties.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                    if (checkGraphicsQueueFamilyIndex == Integer.MAX_VALUE) {
                        checkGraphicsQueueFamilyIndex = i;
                    }
                    // Try to find a queue supporting both graphics and present operations
                    if (supportsPresent.get(i) == VK_TRUE) {
                        checkGraphicsQueueFamilyIndex = i;
                        checkPresentQueueFamilyIndex = i;
                        break;
                    }
                }
            }
            // If we didn't find a queue supporting both graphics and present operations
            if (checkPresentQueueFamilyIndex == Integer.MAX_VALUE) {
                for (int i = 0; i < supportsPresent.capacity(); ++i) {
                    if (supportsPresent.get(i) == VK_TRUE) {
                        checkPresentQueueFamilyIndex = i;
                        break;
                    }
                }
            }
            if (checkGraphicsQueueFamilyIndex == Integer.MAX_VALUE
                    || checkPresentQueueFamilyIndex == Integer.MAX_VALUE) {
                throw new IllegalStateException("Could not find a graphics and a present queue");
            }
            graphicsQueueFamilyIndex = checkGraphicsQueueFamilyIndex;
            presentQueueFamilyIndex = checkGraphicsQueueFamilyIndex;
            VkDeviceQueueCreateInfo.Buffer vkDeviceQueueCreateInfo = VkDeviceQueueCreateInfo.malloc(1, stack);
            vkDeviceQueueCreateInfo.get(0).sType$Default();
            vkDeviceQueueCreateInfo.get(0).pNext(0);
            vkDeviceQueueCreateInfo.get(0).flags(0);
            vkDeviceQueueCreateInfo.get(0).queueFamilyIndex(graphicsQueueFamilyIndex);
            vkDeviceQueueCreateInfo.get(0).pQueuePriorities(stack.floats(1.0f));
            if (graphicsQueueFamilyIndex != presentQueueFamilyIndex) {
                vkDeviceQueueCreateInfo.get(1).sType$Default();
                vkDeviceQueueCreateInfo.get(1).pNext(0);
                vkDeviceQueueCreateInfo.get(1).flags(0);
                vkDeviceQueueCreateInfo.get(1).queueFamilyIndex(presentQueueFamilyIndex);
                vkDeviceQueueCreateInfo.get(1).pQueuePriorities(stack.floats(1.0f));
            }

            // CREATE NEW DEVICE

            ppEnabledExtensionNames.put(KHR_swapchain); // Here we're assuming the extension is available
            ppEnabledExtensionNames.flip(); // and although it probably is, it'd be good to check
            VkDeviceCreateInfo pDeviceCreateInfo = VkDeviceCreateInfo.malloc(stack);
            pDeviceCreateInfo.sType$Default();
            pDeviceCreateInfo.pNext(0);
            pDeviceCreateInfo.flags(0);
            pDeviceCreateInfo.pQueueCreateInfos(vkDeviceQueueCreateInfo);
            pDeviceCreateInfo.ppEnabledLayerNames(null);
            pDeviceCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            PointerBuffer pDevice = stack.mallocPointer(1);
            if (vkCreateDevice(physicalDevice, pDeviceCreateInfo, null, pDevice) != VK_SUCCESS) {
                throw new IllegalStateException();
            }
            device = new VkDevice(pDevice.get(0), physicalDevice, pDeviceCreateInfo);

            // CREATE QUEUES

            PointerBuffer pGraphicsQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device, graphicsQueueFamilyIndex, 0, pGraphicsQueue);
            graphicsQueue = new VkQueue(pGraphicsQueue.get(0), device);
            PointerBuffer pPresentQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device, presentQueueFamilyIndex, 0, pPresentQueue);
            presentQueue = new VkQueue(pPresentQueue.get(0), device);

            // CREATE SEMAPHORES

            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.malloc(stack);
            semaphoreCreateInfo.sType$Default();
            semaphoreCreateInfo.pNext(0);
            semaphoreCreateInfo.flags(0);
            LongBuffer pImageAcquiredSemaphore = stack.mallocLong(1);
            vkCreateSemaphore(device, semaphoreCreateInfo, null, pImageAcquiredSemaphore);
            imageAcquiredSemaphore = pImageAcquiredSemaphore.get(0);
            LongBuffer pRenderingFinishedSemaphore = stack.mallocLong(1);
            vkCreateSemaphore(device, semaphoreCreateInfo, null, pRenderingFinishedSemaphore);
            renderingFinishedSemaphore = pRenderingFinishedSemaphore.get(0);
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

    int idx = 0;

    @Override
    public void loop() {

    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {

    }

    @Override
    public void processMesh(final Mesh mesh) {

    }

    @Override
    public void renderMesh(final Mesh mesh) {

    }

    @Override
    public void removeMesh(final Mesh mesh) {

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
        // for (long framebuffer : framebuffers) {
        // vkDestroyFramebuffer(device, framebuffer, null);
        // }
        // for (VkCommandBuffer commandBuffer : commandBuffers) {
        // vkFreeCommandBuffers(device, commandPool, commandBuffer);
        // }
        // vkDestroyRenderPass(device, renderPass, null);
        // vkDestroyCommandPool(device, commandPool, null);
        // for (int i = 0; i < imageViews.length; i++) {
        // vkDestroyImageView(device, imageViews[i], null);
        // }
        vkDestroySwapchainKHR(device, swapchain, null);
        vkDestroySemaphore(device, imageAcquiredSemaphore, null);
        vkDestroySemaphore(device, renderingFinishedSemaphore, null);
        vkDestroySurfaceKHR(instance, surface, null);
        vkDeviceWaitIdle(device);
        vkDestroyDevice(device, null);
        vkDestroyInstance(instance, null);
        MemoryUtil.memFree(EXT_debug_report);
        MemoryUtil.memFree(KHR_Surface);
        MemoryUtil.memFree(KHR_Win32_Surface);
        MemoryUtil.memFree(KHR_swapchain);
    }
}