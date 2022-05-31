package Leclair.audio.renderer;

/**
 * @since v1
 * @author Kane Burnett
 */
public interface AudioRenderers {
    
    /**
     * Commands the renderer to disable all audio operations
     */
    public static final byte BLACK_HOLE = 0;

    /**
     * Commands the renderer to use the OpenAL API for all audio operations
     */
    public static final byte OPENAL = 1;
}