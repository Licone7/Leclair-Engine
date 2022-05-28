package Leclair.audio.renderer;

public interface AudioRenderers {
    
    /**
     * Commands the renderer to disable all audio operations
     */
    public static final byte DISABLED = 0;

    /**
     * Commands the renderer to use the OpenAL API for all audio operations
     */
    public static final byte OPENAL = 1;
}