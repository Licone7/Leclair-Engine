package Leclair.audio.sound;

import static Leclair.application.ApplicationStructure.audioRenderer;

import java.nio.ShortBuffer;
import java.util.List;

import Leclair.audio.effect.Effect;
import Leclair.audio.filter.Filter;
import Leclair.audio.processor.OggProcessor;
import Leclair.audio.renderer.AudioRenderer;
import Leclair.math.Vector3;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Sound {

    public String path = null;
    public ShortBuffer pcm;
    public int channels = 0;
    public int sampleRate = 0;
    public int index;
    float volume = 1;
    Vector3 position;
    byte state = PlayStates.STATE_UNINITIALIZED;

    public Sound(final String path, boolean process) {
        this.path = path;
        AudioRenderer.sounds.add(this);
        this.index = AudioRenderer.sounds.indexOf(this);
        this.position = new Vector3(0, 0, 0);
        if (process) {
            process();
        }
    }

    public Sound(final String path, boolean process, Vector3 position) {
        this.path = path;
        AudioRenderer.sounds.add(this);
        this.index = AudioRenderer.sounds.indexOf(this);
        this.position = position;
        if (process) {
            process();
        }
    }

    public void process() {
        List<Object> information = OggProcessor.processOggFile(path);
        this.channels = (int) information.get(0);
        this.sampleRate = (int) information.get(1);
        this.pcm = (ShortBuffer) information.get(2);
        audioRenderer.processSound(this);
    }

    public void play() {
        setState(PlayStates.STATE_PLAYING);
        audioRenderer.playSound(this);
    }

    public void pause() {
        setState(PlayStates.STATE_PAUSED);
        audioRenderer.pauseSound(this);
    }

    public void stop() {
        setState(PlayStates.STATE_STOPPED);
        audioRenderer.stopSound(this);
    }

    /**
     * Sets the 3D position of the given sound
     * 
     * @param position
     * @apiNote Stereo sounds cannot be given a position!
     */
    public void setPosition(Vector3 position) {
        this.position = position;
        audioRenderer.setPosition(this);
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public void delete() {
        setState(PlayStates.STATE_DELETE);
        audioRenderer.deleteSound(this);
    }

    public void addEffect(Effect effect) {
        audioRenderer.addEffect(this, effect);
    }

    public void deleteEffect(Effect effect) {
        audioRenderer.deleteEffect(this, effect);
    }

    public void addFilter(Filter filter) {
        audioRenderer.addFilter(this, filter);
    }

    public void deleteFilter(Filter filter) {
        audioRenderer.deleteFilter(this, filter);
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return this.state;
    }

}
