package Leclair.audio.renderer;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.EXTEfx.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.system.MemoryStack;

import Leclair.audio.effect.Effect;
import Leclair.audio.effect.Effects;
import Leclair.audio.sound.Sound;

/**
 * @since v1
 * @author Brett Burnett
 */
public class ALRenderer implements AudioRenderer {

    static boolean effectsSupported = true;
    static long device;
    static ALCCapabilities deviceCaps;
    static long context;
    static boolean useTLC;
    static ALCapabilities caps;
    static List<Integer> buffers = new ArrayList<Integer>();
    static List<Integer> sources = new ArrayList<Integer>();

    public ALRenderer() {

    }

    @Override
    public void init() {
        device = alcOpenDevice((ByteBuffer) null);
        deviceCaps = ALC.createCapabilities(device);
        if (!deviceCaps.ALC_EXT_EFX) {
            effectsSupported = false;
        }
        if (!deviceCaps.OpenALC11) {
            effectsSupported = false;
        }
        context = alcCreateContext(device, (IntBuffer) null);
        useTLC = deviceCaps.ALC_EXT_thread_local_context && EXTThreadLocalContext.alcSetThreadContext(context);
        if (!useTLC) {
            if (!alcMakeContextCurrent(context)) {
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

    }

    @Override
    public void cleanup() {
        alcMakeContextCurrent(0L);
        if (useTLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    @Override
    public void addEffect(final Sound sound, final Effect effect) {
        int effectSlot = alGenAuxiliaryEffectSlots();
        int reverbEffect = alGenEffects();
        switch (effect.getType()) {
            case Effects.CHORUS_EFFECT:
                alEffecti(reverbEffect, AL_EFFECT_TYPE, AL_EFFECT_CHORUS);
                break;
            case Effects.DISTORTION_EFFECT:
                alEffecti(reverbEffect, AL_EFFECT_TYPE, AL_EFFECT_DISTORTION);
                break;
            case Effects.ECHO_EFFECT:
                alEffecti(reverbEffect, AL_EFFECT_TYPE, AL_EFFECT_ECHO);
                break;
            case Effects.REVERB_EFFECT:
                alEffecti(reverbEffect, AL_EFFECT_TYPE, AL_EFFECT_REVERB);
                break;
        }
       // alEffecti(reverbEffect, AL_EFFECT_TYPE, AL_EFFECT_CHORUS);
        alEffectf(reverbEffect, AL_REVERB_DECAY_TIME, 5.0f);
        alAuxiliaryEffectSloti(effectSlot, AL_EFFECTSLOT_EFFECT, reverbEffect);
        alSource3i(sources.get(sound.index), AL_AUXILIARY_SEND_FILTER, effectSlot, 0, AL_FILTER_NULL);
    }

    @Override
    public void deleteEffect(final Sound sound, final Effect effect) {

    }

    @Override
    public void processSound(final Sound sound) {
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
    public void playSound(Sound sound) {
        alSource3f(sources.get(sound.index), AL_POSITION, 0f, 0.0f, 0.0f);
        alSourcePlay(sources.get(sound.index));
    }

    @Override
    public void pauseSound(Sound sound) {
        alSourcePause(sources.get(sound.index));
    }

    @Override
    public void stopSound(Sound sound) {
        alSourceStop(sources.get(sound.index));
    }

    @Override
    public void deleteSound(final Sound sound) {
        alDeleteSources(sources.get(sound.index));
        alDeleteBuffers(buffers.get(sound.index));
    }
}