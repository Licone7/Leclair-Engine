package Leclair.audio.sound;

import static Leclair.audio.AudioInfo.getRenderer;

import java.nio.ShortBuffer;
import java.util.List;

import Leclair.audio.effect.Effect;
import Leclair.audio.processor.OggProcessor;
import Leclair.audio.renderer.AudioRenderer;

/**
 * @since v1
 * @author Brett Burnett
 */
public class Sound {

    public String path = null;
    public ShortBuffer pcm;
    public int channels = 0;
    public int sampleRate = 0;
    public int index;
    float volume = 1;
    byte state = PlayStates.STATE_UNINITIALIZED;

    public Sound(final String path, boolean process) {
        this.path = path;
        AudioRenderer.sounds.add(this);
        this.index = AudioRenderer.sounds.indexOf(this);
        if (process) {
            process();
        } 
    }

    public void process() {
        List<Object> information = OggProcessor.processOggFile(path);
        this.channels = (int) information.get(0);
        this.sampleRate = (int) information.get(1);
        this.pcm = (ShortBuffer) information.get(2);
        getRenderer().processSound(this);
    }

    public void play() {
        setState(PlayStates.STATE_PLAYING);
        getRenderer().playSound(this);
    }

    public void pause() {
        setState(PlayStates.STATE_PAUSED);
        getRenderer().pauseSound(this);
    }

    public void stop() {
        setState(PlayStates.STATE_STOPPED);
        getRenderer().stopSound(this);
    }

    public void delete() {
        setState(PlayStates.STATE_DELETE);
        getRenderer().deleteSound(this);
    }

    public void addEffect(Effect effect) {
        getRenderer().addEffect(this, effect);
    }

    public void deleteEffect(Effect effect) {
        getRenderer().deleteEffect(this, effect);
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return this.state;
    }
}