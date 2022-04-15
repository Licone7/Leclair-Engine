package Leclair.audio;

import Leclair.audio.renderer.ALRenderer;
import Leclair.audio.renderer.AudioRenderer;
import Leclair.audio.sound.Sound;

/**
 * @since v1
 * @author Brett Burnett
 */
public class AudioInfo {

    /**
     * Commands the renderer to disable all audio operations
     */
    public static final byte DISABLED = 0;

    /**
     * Commands the renderer to use the OpenAL API for all audio operations
     */
    public static final byte OPENAL = 1;

    static byte audioApi = OPENAL;
    static AudioRenderer renderer;

    /**
     * Commands the engine to use the specified renderer <br>
     * <br>
     * Can be either {@link #DISABLED} or {@link #OPENAL}, any other value will
     * result in an IllegalArgumentException
     */
    public static void setRenderer(final byte api) {
        audioApi = api;
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void setup() {
        switch (audioApi) {
        case DISABLED:
            break;
        case OPENAL:
            renderer = new ALRenderer();
            break;
        default:
            throw new IllegalArgumentException("The requested renderer is invalid");
        }
        renderer.init();
        renderer.printCapabilities();
        for (final Sound sound : Sound.getSounds()) {
            if (sound.initialized == false) {
                renderer.addSound(sound);
                sound.initialized = true;
            }
        }
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void loop() {
        for (final Sound sound : Sound.getSounds()) {
            if (sound.initialized == false) {
                renderer.addSound(sound);
                sound.initialized = true;
            }
        }
        renderer.loop();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void cleanup() {
        for (final Sound sound : Sound.getSounds()) {
            renderer.deleteSound(sound);
        }
        renderer.cleanup();
    }

}
