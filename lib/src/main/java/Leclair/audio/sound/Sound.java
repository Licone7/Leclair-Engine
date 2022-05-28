package Leclair.audio.sound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.asset.AssetLoader;
import Leclair.audio.effect.Effect;
import Leclair.audio.renderer.AudioRenderer;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import static Leclair.audio.AudioInfo.getRenderer;

/**
 * @since v1
 * @author Brett Burnett
 */
public class Sound {

    List<Effect> effects = new ArrayList<Effect>();

    public String path = null;
    public ShortBuffer pcm;
    public int channels = 0;
    public int sampleRate = 0;
    public int index;
    float volume = 1;
    byte state = PlayStates.STATE_UNINITIALIZED;
    // public boolean destroy = false;
    // public boolean UPDATED_STATE = false;

    public Sound(final String path) {
        this.path = path;
        AudioRenderer.sounds.add(this);
        this.index = AudioRenderer.sounds.indexOf(this);
        compile();
    }

    public void compile() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final STBVorbisInfo info = STBVorbisInfo.malloc(stack);
            ByteBuffer vorbis;
            vorbis = AssetLoader.importAsBinary(path, 64 * 1024);
            final IntBuffer error = stack.mallocInt(1);
            final long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == 0L) {
                throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }
            STBVorbis.stb_vorbis_get_info(decoder, info);
            final int channels = info.channels();
            final ShortBuffer pcm = ByteBuffer
                    .allocateDirect(STBVorbis.stb_vorbis_stream_length_in_samples(decoder) * channels).asShortBuffer();
            STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
            STBVorbis.stb_vorbis_close(decoder);
            this.channels = info.channels();
            this.sampleRate = info.sample_rate();
            this.pcm = pcm;
        }

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
        this.effects.add(effect);  
        getRenderer().addEffect(this, effect); 
    }

    public void deleteEffect(Effect effect) {
        this.effects.remove(effect);
        getRenderer().deleteEffect(this, effect);
    }

    public List<Effect> getEffects() {
        return this.effects;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public byte getState() {
        return this.state;
    }
}