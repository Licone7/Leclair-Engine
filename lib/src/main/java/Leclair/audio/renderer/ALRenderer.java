package Leclair.audio.renderer;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_FORMAT_MONO16;
import static org.lwjgl.openal.AL10.AL_FORMAT_STEREO16;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_RENDERER;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alBufferData;
import static org.lwjgl.openal.AL10.alDeleteBuffers;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenBuffers;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.openal.AL10.alSource3f;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcei;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import Leclair.audio.sound.Sound;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.system.MemoryStack;

/**
 * @since v1
 * @author Brett Burnett
 */
public class ALRenderer implements AudioRenderer {

    long device;
    ALCCapabilities deviceCaps;
    long context;
    boolean useTLC;
    ALCapabilities caps;
    List<Integer> buffers = new ArrayList<Integer>();
    List<Integer> sources = new ArrayList<Integer>();

    public ALRenderer() {

    }

    @Override
    public void init() {
        device = ALC10.alcOpenDevice((ByteBuffer) null);
        deviceCaps = ALC.createCapabilities(device);
        context = ALC10.alcCreateContext(device, (IntBuffer) null);
        useTLC = deviceCaps.ALC_EXT_thread_local_context && EXTThreadLocalContext.alcSetThreadContext(context);
        if (!useTLC) {
            if (!ALC10.alcMakeContextCurrent(context)) {
                throw new IllegalStateException();
            }
        }
        caps = AL.createCapabilities(deviceCaps, null);
    }

    @Override
    public void printCapabilities() {
        final String api = "API: OpenAL " + alGetString(AL_VERSION);
        final String renderer = "Renderer: " + alGetString(AL_RENDERER);
        System.out.println("Audio Info:");
        System.out.println(api);
        System.out.println(renderer);
        System.out.println("_____");
    }

    @Override
    public void loop() {
        for (final Sound sound : Sound.getSounds()) { // Very inefficient, need a better way
            if (sound.UPDATED_STATE == true) {
                if (sound.playing == true) {
                    alSource3f(sources.get(sound.index), AL_POSITION, 0f, 0.0f, 0.0f);
                    alSourcePlay(sources.get(sound.index));
                } else if (sound.paused == true) {
                    alSourcePause(sources.get(sound.index));
                } else if (sound.stopped == true) {
                    alSourceStop(sources.get(sound.index));
                } else if (sound.destroy == true) {
                    alSourceStop(sources.get(sound.index));
                    deleteSound(sound);
                }
            }
            sound.UPDATED_STATE = false;
        }
    }

    @Override
    public void cleanup() {
        ALC10.alcMakeContextCurrent(0L);
        if (useTLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }

    @Override
    public void addEffect(final Sound sound, final byte effect) {

    }

    @Override
    public void deleteEffect(final Sound sound, final byte effect) {

    }

    @Override
    public void addSound(final Sound sound) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer bufferNames = stack.mallocInt(1);
            alGenBuffers(bufferNames);
            final int buffer = bufferNames.get(0);
            IntBuffer srcNames = stack.mallocInt(1);
            alGenSources(srcNames);
            final int source = srcNames.get(0);
            alBufferData(buffer, sound.channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, sound.pcm,
                    sound.sampleRate);
            alSourcei(source, AL_LOOPING, 1);
            alSourcei(source, AL_BUFFER, buffer);
            buffers.add(sound.index, buffer);
            sources.add(sound.index, source);
        }
    }

    @Override
    public void deleteSound(final Sound sound) {
        alDeleteSources(sources.get(sound.index));
        alDeleteBuffers(buffers.get(sound.index));
    }

}
