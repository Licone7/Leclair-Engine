package Leclair.graphics.renderer;

import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK11.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.WindowsLibrary;
import org.lwjgl.vulkan.KHRWin32Surface;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSubpassDescription;
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
    static int imageFormat;
    static long images[];
    static long imageViews[];
    static long renderPass;
    static long framebuffers[];
    static long commandPool;
    static VkCommandBuffer[] commandBuffers;
    static long[] fences;

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
                vkWin32SurfaceCreateInfoKHR.hwnd(GLFWNativeWin32.glfwGetWin32Window(WindowInfo.window().getWHandle()));
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
        prepare();
    }

    void prepare() {
        createSwapchain();
        createRenderPass();
        createFramebuffers();
        createCommandBuffers();
    }

    void createSwapchain() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long oldSwapchain = swapchain;

            // FIND SURFACE CAPABILITIES

            VkSurfaceCapabilitiesKHR pSurfaceCapabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, pSurfaceCapabilities);
            IntBuffer pSurfaceFormatCount = stack.mallocInt(1); // Surface Formats
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pSurfaceFormatCount, null);
            VkSurfaceFormatKHR.Buffer pSurfaceFormats = VkSurfaceFormatKHR.malloc(pSurfaceFormatCount.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pSurfaceFormatCount,
                    pSurfaceFormats);
            if (pSurfaceFormatCount.get(0) == 1 && pSurfaceFormats.get(0).format() == VK_FORMAT_UNDEFINED) {
                imageFormat = VK_FORMAT_B8G8R8A8_UNORM;
            } else {
                assert pSurfaceFormatCount.get(0) >= 1;
                imageFormat = pSurfaceFormats.get(0).format();
            }
            IntBuffer pPresentModeCount = stack.mallocInt(1); // Present Mode
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
            VkExtent2D imageExtent = VkExtent2D.malloc(stack); // Swapchain Extents
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
            int preTransform; // Pretransform
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
            IntBuffer pSwapchainImageCount = stack.mallocInt(1);
            vkGetSwapchainImagesKHR(device, swapchain, pSwapchainImageCount, null);
            int swapchainImageCount = pSwapchainImageCount.get(0);
            LongBuffer pSwapchainImages = stack.mallocLong(swapchainImageCount);
            vkGetSwapchainImagesKHR(device, swapchain, pSwapchainImageCount, pSwapchainImages);
            images = new long[swapchainImageCount];
            pSwapchainImages.get(images, 0, images.length);
            imageViews = new long[swapchainImageCount];
            LongBuffer pImageView = stack.mallocLong(1);
            for (int i = 0; i < swapchainImageCount; i++) {
                VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.malloc(stack);
                subresourceRange.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                subresourceRange.baseMipLevel(0);
                subresourceRange.levelCount(1);
                subresourceRange.baseArrayLayer(0);
                subresourceRange.layerCount(1);
                VkImageViewCreateInfo pImageViewCreateInfo = VkImageViewCreateInfo.malloc(stack);
                pImageViewCreateInfo.sType$Default();
                pImageViewCreateInfo.pNext(0);
                pImageViewCreateInfo.image(pSwapchainImages.get(i));
                pImageViewCreateInfo.viewType(VK_IMAGE_TYPE_2D);
                pImageViewCreateInfo.format(imageFormat);
                pImageViewCreateInfo.subresourceRange(subresourceRange);
                vkCreateImageView(device, pImageViewCreateInfo, null, pImageView);
                imageViews[i] = pImageView.get(0);
            }
        }
    }

    void createRenderPass() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkAttachmentDescription.Buffer pAttachments = VkAttachmentDescription.malloc(1, stack);
            pAttachments.flags(0);
            pAttachments.format(imageFormat);
            pAttachments.samples(VK_SAMPLE_COUNT_1_BIT);
            pAttachments.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            pAttachments.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            pAttachments.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            pAttachments.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            pAttachments.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            pAttachments.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
           // pAttachments.flip();
            VkAttachmentReference.Buffer pColorAttachments = VkAttachmentReference.malloc(1, stack);
            pColorAttachments.attachment(0);
            pColorAttachments.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
            //pColorAttachments.flip();
            VkSubpassDescription.Buffer pSubpasses = VkSubpassDescription.malloc(1, stack);
            pSubpasses.flags(0);
            pSubpasses.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            pSubpasses.pInputAttachments(null);
            pSubpasses.colorAttachmentCount(1);
            pSubpasses.pColorAttachments(pColorAttachments);
            pSubpasses.pResolveAttachments(null);
            pSubpasses.pDepthStencilAttachment(null);
            pSubpasses.pPreserveAttachments(null);
            //pSubpasses.flip();
            VkRenderPassCreateInfo pRenderPassCreateInfo = VkRenderPassCreateInfo.malloc(stack);
            pRenderPassCreateInfo.sType$Default();
            pRenderPassCreateInfo.pNext(0);
            pRenderPassCreateInfo.flags(0);
            pRenderPassCreateInfo.pAttachments(pAttachments);
            pRenderPassCreateInfo.pSubpasses(pSubpasses);
            pRenderPassCreateInfo.pDependencies(null);
            LongBuffer pRenderPass = stack.mallocLong(1);
            if (vkCreateRenderPass(device, pRenderPassCreateInfo, null, pRenderPass) != VK_SUCCESS) {
                throw new IllegalStateException();
            }
            renderPass = pRenderPass.get(0);
        }
    }

    void createFramebuffers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (framebuffers != null) {
                for (long framebuffer : framebuffers)
                    vkDestroyFramebuffer(device, framebuffer, null);
            }
            LongBuffer pAttachments = stack.mallocLong(1);
            VkFramebufferCreateInfo pFramebufferCreateInfo = VkFramebufferCreateInfo.malloc(stack);
            pFramebufferCreateInfo.sType$Default();
            pFramebufferCreateInfo.pAttachments(pAttachments);
            pFramebufferCreateInfo.height(WindowInfo.getHeight());
            pFramebufferCreateInfo.width(WindowInfo.getWidth());
            pFramebufferCreateInfo.layers(1);
            pFramebufferCreateInfo.renderPass(renderPass);
            framebuffers = new long[images.length];
            LongBuffer pFramebuffer = stack.mallocLong(1);
            for (int i = 0; i < images.length; i++) {
                pAttachments.put(0, imageViews[i]);
                System.out.println(vkCreateFramebuffer(device, pFramebufferCreateInfo, null, pFramebuffer));
                framebuffers[i] = pFramebuffer.get(0);
            }
        }
    }

    void createCommandBuffers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo pCommandPoolCreateInfo = VkCommandPoolCreateInfo.malloc(stack);
            pCommandPoolCreateInfo.sType$Default();
            pCommandPoolCreateInfo.pNext(0);
            pCommandPoolCreateInfo.flags(0);
            pCommandPoolCreateInfo.queueFamilyIndex(presentQueueFamilyIndex);
            LongBuffer pCommandPool = stack.mallocLong(1);
            vkCreateCommandPool(device, pCommandPoolCreateInfo, null, pCommandPool);
            commandPool = pCommandPool.get(0);
            VkClearValue.Buffer pClearValues = VkClearValue.malloc(1, stack);
            pClearValues.color().float32(0, 1.0f);
            pClearValues.color().float32(1, 0.4f);
            pClearValues.color().float32(2, 1.8f);
            pClearValues.color().float32(3, 0.5f);
            pClearValues.flip();
            VkRenderPassBeginInfo pRenderPassBegin = VkRenderPassBeginInfo.malloc(stack);
            pRenderPassBegin.sType$Default();
            pRenderPassBegin.renderPass(renderPass);
            pRenderPassBegin.pClearValues(pClearValues);
            pRenderPassBegin.renderArea(a -> a.extent().set(WindowInfo.getWidth(), WindowInfo.getHeight())); // TODO:
            int count = imageViews.length;
            commandBuffers = new VkCommandBuffer[count];
            for (int i = 0; i < images.length; i++) {

                PointerBuffer pCommandBuffer = stack.mallocPointer(1);
                vkAllocateCommandBuffers(device,
                        VkCommandBufferAllocateInfo
                                .calloc(stack)
                                .sType$Default()
                                .commandPool(commandPool)
                                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                                .commandBufferCount(1),
                        pCommandBuffer);
                VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), device);
                VkCommandBufferBeginInfo pCommandBufferBeginInfo = VkCommandBufferBeginInfo.malloc(stack);
                pCommandBufferBeginInfo.sType$Default();
                pCommandBufferBeginInfo.pNext(0);
                pCommandBufferBeginInfo.flags(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
                pCommandBufferBeginInfo.pInheritanceInfo(null);
                vkBeginCommandBuffer(commandBuffer, pCommandBufferBeginInfo);
                commandBuffers[i] = commandBuffer;
                pRenderPassBegin.framebuffer(framebuffers[i]);
                vkCmdBeginRenderPass(commandBuffer, pRenderPassBegin, VK_SUBPASS_CONTENTS_INLINE);
                vkCmdEndRenderPass(commandBuffer);
                vkEndCommandBuffer(commandBuffer);
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

    int idx = 0;

    @Override
    public void loop() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pImageIndex = stack.mallocInt(1);
            vkAcquireNextImageKHR(device, swapchain, -1L, imageAcquiredSemaphore, VK_NULL_HANDLE, pImageIndex);
            // if (!acquireSwapchainImage(pImageIndex, idx)) {
            // needRecreate = true;
            // continue;
            // }
            // needRecreate = !submitAndPresent(pImageIndex.get(0), idx);

            vkQueueSubmit(presentQueue, VkSubmitInfo
                    .calloc(stack)
                    .sType$Default()
                    .pWaitSemaphores(stack.longs(imageAcquiredSemaphore))
                    // must wait before COLOR_ATTACHMENT_OUTPUT to output color values
                    .pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                    .pCommandBuffers(stack.pointers(commandBuffers[idx]))
                    .waitSemaphoreCount(1)
                    .pSignalSemaphores(stack.longs(renderingFinishedSemaphore)),
                    VK_NULL_HANDLE);
            int result = vkQueuePresentKHR(presentQueue, VkPresentInfoKHR
                    .calloc(stack)
                    .sType$Default()
                    .pWaitSemaphores(stack.longs(renderingFinishedSemaphore))
                    .swapchainCount(1)
                    .pSwapchains(stack.longs(swapchain))
                    .pImageIndices(stack.ints(pImageIndex.get(0))));
System.out.println(result);
            idx = (idx + 1) % imageViews.length;
        }
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