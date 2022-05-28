package Leclair.graphics.renderer;

public interface GraphicsRenderers {
    
    /**
     * Commands the renderer to disable all graphics and compute operations
     */
    public static final byte DISABLED = 0;

    /**
     * Commands the renderer to use the Vulkan API for all graphics and compute
     * operations
     */
    public static final byte VULKAN = 1;

    /**
     * Commands the renderer to use the OpenGL API for all graphics and compute
     * operations
     */
    public static final byte OPENGL = 2;
}