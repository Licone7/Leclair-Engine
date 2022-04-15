package Leclair.audio.renderer;

import Leclair.audio.sound.Sound;

/**
 * @since v1
 * @author Brett Burnett
 */
public interface AudioRenderer {

    public void init();

    public void printCapabilities();

    public void addSound(Sound sound);

    public void deleteSound(Sound sound);

    public void addEffect(Sound sound, byte effect);

    public void deleteEffect(Sound sound, byte effect);

    public void loop();

    public void cleanup();
    
}
