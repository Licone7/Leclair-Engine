package Leclair.graphics.renderer;

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
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.lwjgl.vulkan.VkWin32SurfaceCreateInfoKHR;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.scene.ViewPort;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;
import Leclair.window.WindowInfo;

public class VKRenderer implements Renderer {

    static boolean multiDrawIndirectSupported = false;
    static final ByteBuffer KHR_Surface = MemoryUtil.memASCII(VK_KHR_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_Win32_Surface = MemoryUtil
            .memASCII(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME);
    static final ByteBuffer KHR_swapchain = MemoryUtil.memASCII(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
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
    static long oldSwapchain = VK_NULL_HANDLE;

    public VKRenderer(final ViewPort viewPort) {

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
            VkInstanceCreateInfo vkInstanceCreateInfo = VkInstanceCreateInfo.malloc(stack);
            vkInstanceCreateInfo.sType$Default();
            vkInstanceCreateInfo.pNext(0);
            vkInstanceCreateInfo.flags(0);
            vkInstanceCreateInfo.pApplicationInfo(vkApplicationInfo);
            vkInstanceCreateInfo.ppEnabledLayerNames(layerList);
            vkInstanceCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            PointerBuffer pInstance = stack.mallocPointer(1);
            if (vkCreateInstance(vkInstanceCreateInfo, null, pInstance) != VK_SUCCESS) {
                throw new IllegalStateException(
                        "Vulkan instance creation failed; ensure you have a Vulkan ICD installed!");
            }
            instance = new VkInstance(pInstance.get(0), vkInstanceCreateInfo);
            ppEnabledExtensionNames.clear();

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
            vkDeviceQueueCreateInfo.sType$Default();
            vkDeviceQueueCreateInfo.pNext(0);
            vkDeviceQueueCreateInfo.flags(0);
            vkDeviceQueueCreateInfo.queueFamilyIndex(graphicsQueueFamilyIndex);
            vkDeviceQueueCreateInfo.pQueuePriorities(stack.floats(1f));
            // vkDeviceQueueCreateInfo.get(0).sType$Default();
            // vkDeviceQueueCreateInfo.get(0).pNext(0);
            // vkDeviceQueueCreateInfo.get(0).flags(0);
            // vkDeviceQueueCreateInfo.get(0).queueFamilyIndex(graphicsQueueFamilyIndex);
            // vkDeviceQueueCreateInfo.get(0).pQueuePriorities(stack.floats(1.0f));
            // if (graphicsQueueFamilyIndex != presentQueueFamilyIndex) {
            // vkDeviceQueueCreateInfo.get(1).sType$Default();
            // vkDeviceQueueCreateInfo.get(1).pNext(0);
            // vkDeviceQueueCreateInfo.get(1).flags(0);
            // vkDeviceQueueCreateInfo.get(1).queueFamilyIndex(presentQueueFamilyIndex);
            // vkDeviceQueueCreateInfo.get(1).pQueuePriorities(stack.floats(1.0f));
            // }
            // vkDeviceQueueCreateInfo.flip(); // TODO: Is it needed to call flip here?

            // CREATE A NEW VULKAN DEVICE

            ppEnabledExtensionNames.put(KHR_swapchain); // Here we're assuming the extension is available
            ppEnabledExtensionNames.flip(); // and although it probably is, it'd be good to check
            VkDeviceCreateInfo pCreateInfo = VkDeviceCreateInfo.malloc(stack);
            pCreateInfo.sType$Default();
            pCreateInfo.pNext(0);
            pCreateInfo.flags(0);
            pCreateInfo.pQueueCreateInfos(vkDeviceQueueCreateInfo);
            pCreateInfo.ppEnabledLayerNames(null);
            pCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            PointerBuffer pDevice = stack.mallocPointer(1);
            if (vkCreateDevice(physicalDevice, pCreateInfo, null, pDevice) != VK_SUCCESS) {
                throw new IllegalStateException();
            }
            device = new VkDevice(pDevice.get(0), physicalDevice, pCreateInfo);

            // CREATE VULKAN QUEUES

            PointerBuffer pGraphicsQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device, graphicsQueueFamilyIndex, 0, pGraphicsQueue);
            graphicsQueue = new VkQueue(pGraphicsQueue.get(0), device);
            PointerBuffer pPresentQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device, presentQueueFamilyIndex, 0, pPresentQueue);
            presentQueue = new VkQueue(pPresentQueue.get(0), device);

            // CREATE TWO SEMAPHORES

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

            // CREATE SWAPCHAIN

            VkSurfaceCapabilitiesKHR pSurfaceCapabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, pSurfaceCapabilities);
            IntBuffer pSurfaceFormatCount = stack.mallocInt(1);
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pSurfaceFormatCount, null);
            int imageFormat;
            VkSurfaceFormatKHR.Buffer pSurfaceFormats = VkSurfaceFormatKHR.malloc(pSurfaceFormatCount.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pSurfaceFormatCount,
                    pSurfaceFormats);
            if (pSurfaceFormatCount.get(0) == 1 && pSurfaceFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
                imageFormat = VK_FORMAT_B8G8R8A8_UNORM;
            } else {
                assert pSurfaceFormatCount.get(0) >= 1;
                imageFormat = pSurfaceFormats.get(0).format();
            }
            IntBuffer pPresentModeCount = stack.mallocInt(1);
            vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null);
            IntBuffer pPresentModes = stack.mallocInt(pPresentModeCount.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount,
                    pPresentModes);
                    int presentMode = VK_PRESENT_MODE_FIFO_KHR; // FIFO is always supported
            for (int i = 0; i < pPresentModeCount.get(0); i++) {
                int checkPresentMode = pPresentModes.get(i);
                if (checkPresentMode == VK_PRESENT_MODE_MAILBOX_KHR) {
                    presentMode = VK_PRESENT_MODE_MAILBOX_KHR; // Use Mailbox for low latency V-Sync
                    break;
                }
            }
            int minImageCount = pSurfaceCapabilities.minImageCount() + 1;
            if (pSurfaceCapabilities.maxImageCount() > 0 &&
                    (minImageCount > pSurfaceCapabilities.maxImageCount())) {
                minImageCount = pSurfaceCapabilities.maxImageCount();
            }
            VkExtent2D imageExtent = VkExtent2D.malloc(stack);
            if (pSurfaceCapabilities.currentExtent().width() == 0xFFFFFFFF) {
                imageExtent.width(WindowInfo.getWidth());
                imageExtent.height(WindowInfo.getHeight());
                if (imageExtent.width() < pSurfaceCapabilities.minImageExtent().width()) {
                    imageExtent.width(pSurfaceCapabilities.minImageExtent().width());
                } else if (imageExtent.width() > pSurfaceCapabilities.maxImageExtent().width()) {
                    imageExtent.width(pSurfaceCapabilities.maxImageExtent().width());
                }
                if (imageExtent.height() < pSurfaceCapabilities.minImageExtent().height()) {
                    imageExtent.height(pSurfaceCapabilities.minImageExtent().height());
                } else if (imageExtent.height() > pSurfaceCapabilities.maxImageExtent().height()) {
                    imageExtent.height(pSurfaceCapabilities.maxImageExtent().height());
                }
            } else {
                imageExtent.set(pSurfaceCapabilities.currentExtent());
                WindowInfo.setWidth(pSurfaceCapabilities.currentExtent().width());
                WindowInfo.setHeight(pSurfaceCapabilities.currentExtent().height());
            }
            int preTransform;
            if ((pSurfaceCapabilities.supportedTransforms() & VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
                preTransform = VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
            } else {
                preTransform = pSurfaceCapabilities.currentTransform();
            }
            VkSwapchainCreateInfoKHR pSwapchainCreateInfo = VkSwapchainCreateInfoKHR.malloc(stack);
            pSwapchainCreateInfo.sType$Default();
            pSwapchainCreateInfo.pNext(0);
            pSwapchainCreateInfo.flags(0);
            pSwapchainCreateInfo.surface(surface);
            pSwapchainCreateInfo.minImageCount(minImageCount);
            pSwapchainCreateInfo.imageFormat(imageFormat);
            pSwapchainCreateInfo.imageColorSpace(pSurfaceFormats.get(0).colorSpace());
            pSwapchainCreateInfo.imageExtent(imageExtent);
            pSwapchainCreateInfo.imageArrayLayers(1);
            pSwapchainCreateInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);
            pSwapchainCreateInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            pSwapchainCreateInfo.queueFamilyIndexCount(0);
            pSwapchainCreateInfo.pQueueFamilyIndices(null);
            pSwapchainCreateInfo.preTransform(preTransform);
            pSwapchainCreateInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            pSwapchainCreateInfo.presentMode(presentMode);
            pSwapchainCreateInfo.clipped(true);
            pSwapchainCreateInfo.oldSwapchain(oldSwapchain);
            LongBuffer pSwapchain = stack.mallocLong(1);
            if (vkCreateSwapchainKHR(device, pSwapchainCreateInfo, null, pSwapchain) != VK_SUCCESS) {
                throw new IllegalStateException();
            }
            swapchain = pSwapchain.get(0);
            if (oldSwapchain != VK_NULL_HANDLE) {
                vkDestroySwapchainKHR(device, oldSwapchain, null);
            }
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
        vkDestroySurfaceKHR(instance, surface, null);
        vkDeviceWaitIdle(device);
        vkDestroyDevice(device, null);
        vkDestroyInstance(instance, null);
        MemoryUtil.memFree(KHR_Surface);
        MemoryUtil.memFree(KHR_Win32_Surface);
        MemoryUtil.memFree(KHR_swapchain);
    }
}