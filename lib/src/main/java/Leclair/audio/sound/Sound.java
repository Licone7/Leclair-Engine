package Leclair.audio.sound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.asset.AssetLoader;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

/**
 * @since v1
 * @author Brett Burnett
 */
public class Sound {

    static List<Sound> sounds = new ArrayList<Sound>();

    public String path = null;
    public ShortBuffer pcm;
    public int channels = 0;
    public int sampleRate = 0;
    public int index;
    float volume = 1;
    public boolean destroy = false;
    public boolean UPDATED_STATE = false;
    public boolean initialized = false;
    public boolean stopped = false;
    public boolean paused = false;
    public boolean playing = false;

    public Sound(final String path) {
        this.path = path;
        sounds.add(this);
        this.index = sounds.indexOf(this);
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
        this.stopped = false;
        this.paused = false;
        this.playing = true;
        this.UPDATED_STATE = true;
    }

    public void pause() {
        this.stopped = false;
        this.paused = true;
        this.playing = false;
        this.UPDATED_STATE = true;
    }

    public void stop() {
        this.stopped = true;
        this.paused = false;
        this.playing = false;
        this.UPDATED_STATE = true;
    }

    public void destroy() {
        this.stopped = false;
        this.paused = false;
        this.playing = false;
        this.destroy = true;
        this.UPDATED_STATE = true;
    }

    public static List<Sound> getSounds() {
        return sounds;
    }
}