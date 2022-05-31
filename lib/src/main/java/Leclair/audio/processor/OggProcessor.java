package Leclair.audio.processor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;

import Leclair.asset.AssetLoader;

/**
 * @since v1
 * @author Kane Burnett
 */
public class OggProcessor {

    public static List<Object> processOggFile(String path) {
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
            List<Object> information = new ArrayList<Object>();
            information.add(0, info.channels());
            information.add(1, info.sample_rate());
            information.add(2, pcm);
            return information;
        }
    }
}
