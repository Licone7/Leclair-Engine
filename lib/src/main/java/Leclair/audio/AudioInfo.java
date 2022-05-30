package Leclair.audio;

import Leclair.audio.renderer.ALRenderer;
import Leclair.audio.renderer.AudioRenderer;
import Leclair.audio.renderer.AudioRenderers;
import Leclair.audio.renderer.BlackHoleRenderer;
import Leclair.audio.sound.Sound;

/**
 * @since v1
 * @author Brett Burnett
 */
public class AudioInfo {

    static byte audioApi = AudioRenderers.OPENAL;
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
            case AudioRenderers.BLACK_HOLE:
                renderer = new BlackHoleRenderer();
                break;
            case AudioRenderers.OPENAL:
                renderer = new ALRenderer();
                break;
            default:
                throw new IllegalArgumentException("The requested renderer is invalid");
        }
        renderer.init();
        renderer.printCapabilities();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void loop() {
        renderer.loop();
    }

    /**
     * @apiNote For internal use only, <b>never</b> explicitly invoke!
     */
    public static void cleanup() {
        for (final Sound sound : AudioRenderer.sounds) {
            renderer.deleteSound(sound);
        }
        renderer.cleanup();
    }

    public static AudioRenderer getRenderer() {
        return renderer;
    }

}