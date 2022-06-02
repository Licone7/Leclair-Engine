package Leclair.audio.renderer;

import java.util.ArrayList;
import java.util.List;

import Leclair.audio.effect.Effect;
import Leclair.audio.filter.Filter;
import Leclair.audio.sound.Sound;

/**
 * @since v1
 * @author Kane Burnett
 */
public interface AudioRenderer {

    public static List<Sound> sounds = new ArrayList<Sound>();

    /**
     * Initializes the renderer by setting up APIs and any other needed resources
     * This method must be called before using any other methods in
     * {@link #AudioRenderer}!
     * 
     * @apiNote For internal usage <b>only</b>!
     */
    public void init();

    /**
     * Reads and prints the capabilities of the renderer to the console
     * 
     * @apiNote For internal usage <b>only</b>!
     */
    public void printCapabilities();

    /**
     * @apiNote For internal usage <b>only</b>!
     */
    public void loop();

    /**
     * Shuts down the renderer by cleaning up APIs and other resources created by
     * the {@link #init()} method
     * 
     * @apiNote For internal usage <b>only</b>!
     */
    public void cleanup();

    /**
     * Commands the renderering API to process the given sound and add it to memory
     * 
     * @param sound
     * @apiNote For internal usage <b>only</b>!
     */
    public void processSound(Sound sound);

    /**
     * Plays the given sound
     * 
     * @param sound
     */
    public void playSound(Sound sound);

    /**
     * Pauses the given sound
     * 
     * @param sound
     */
    public void pauseSound(Sound sound);

    /**
     * Stops the given sound
     * 
     * @param sound
     */
    public void stopSound(Sound sound);

    /**
     * Removes the given sound from the rendering API's memory
     * 
     * @param sound
     * @apiNote For internal usage <b>only</b>!
     */
    public void deleteSound(Sound sound);

    /**
     * 
     * @param sound
     * @param effect
     * @apiNote For internal usage <b>only</b>!
     */
    public void addEffect(Sound sound, Effect effect);

    /**
     * 
     * @param sound
     * @param effect
     * @apiNote For internal usage <b>only</b>!
     */
    public void deleteEffect(Sound sound, Effect effect);

    /**
     * 
     * @param sound
     * @param filter
     * @apiNote For internal usage <b>only</b>!
     */
    public void addFilter(Sound sound, Filter filter);

    /**
     * 
     * @param sound
     * @param filter
     * @apiNote For internal usage <b>only</b>!
     */
    public void deleteFilter(Sound sound, Effect filter);

}
