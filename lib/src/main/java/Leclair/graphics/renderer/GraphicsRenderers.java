package Leclair.graphics.renderer;

/**
 * @since v1
 * @author Kane Burnett
 */
public interface GraphicsRenderers {
    
    /**
     * Commands the renderer to disable all graphics and compute operations
     */
    public static final byte BLACK_HOLE = 0;

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