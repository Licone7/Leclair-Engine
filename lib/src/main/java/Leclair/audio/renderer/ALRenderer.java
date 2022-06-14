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
import org.lwjgl.system.MemoryUtil;

import Leclair.audio.effect.Effect;
import Leclair.audio.effect.Effects;
import Leclair.audio.filter.Filter;
import Leclair.audio.filter.Filters;
import Leclair.audio.sound.Sound;
import Leclair.logger.LogTypes;
import Leclair.logger.Logger;

/**
 * @since v1
 * @author Kane Burnett
 */
public class ALRenderer implements AudioRenderer {

    // Capabilities Info
    static boolean effectsSupported = true;
    // OpenAL variables
    static long device;
    static ALCCapabilities deviceCaps;
    static long context;
    static boolean useTLC;
    static ALCapabilities caps;
    // Lists
    static List<Integer> buffers;
    static List<Integer> sources;
    static List<Integer> effects;
    static List<Integer> effectSlots;
    static List<Integer> filters;

    public ALRenderer() {
        buffers = new ArrayList<Integer>();
        sources = new ArrayList<Integer>();
        effects = new ArrayList<Integer>();
        effectSlots = new ArrayList<Integer>();
        filters = new ArrayList<Integer>();
    }

    @Override
    public void init() {
        device = alcOpenDevice((ByteBuffer) null);
        deviceCaps = ALC.createCapabilities(device);
        if (!deviceCaps.ALC_EXT_EFX) {
            effectsSupported = false;
        }
        if (!deviceCaps.OpenALC11) { // It seems effects aren't supported on ALC10
            effectsSupported = false;
        }
        context = alcCreateContext(device, (IntBuffer) null);
        useTLC = deviceCaps.ALC_EXT_thread_local_context && EXTThreadLocalContext.alcSetThreadContext(context);
        if (!useTLC) {
            if (!alcMakeContextCurrent(context)) {
                throw new IllegalStateException();
            }
        }
        caps = AL.createCapabilities(deviceCaps, MemoryUtil::memCallocPointer);
    }

    @Override
    public void printCapabilities() {
        final String api = "API: OpenAL " + alGetString(AL_VERSION);
        final String renderer = "Renderer: " + alGetString(AL_RENDERER);
        Logger.getLogger().log("Audio Capabilities:", LogTypes.TYPE_INFO);
        Logger.getLogger().log(api, LogTypes.TYPE_INFO);
        Logger.getLogger().log(renderer, LogTypes.TYPE_INFO);
        Logger.getLogger().log("_____", LogTypes.TYPE_INFO);
    }

    @Override
    public void loop() {

    }

    @Override
    public void cleanup() {
        for (int effect : effects) {
            alDeleteEffects(effect);
        }
        effects.clear();
        effectSlots.clear();
        for (int filter : filters) {
            alDeleteFilters(filter);
        }
        for (int source : sources) {
            alDeleteSources(source);
        }
        sources.clear();
        for (int buffer : buffers) {
            alDeleteBuffers(buffer);
        }
        buffers.clear();
        alcMakeContextCurrent(0L);
        if (useTLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }
        alcDestroyContext(context);
        alcCloseDevice(device);
        MemoryUtil.memFree(caps.getAddressBuffer());
    }

    @Override
    public void addEffect(final Sound sound, final Effect effect) {
        int effectSlot = alGenAuxiliaryEffectSlots();
        int iEffect = alGenEffects();
        switch (effect.getType()) {
            case Effects.CHORUS_EFFECT:
                alEffecti(iEffect, AL_EFFECT_TYPE, AL_EFFECT_CHORUS);
                break;
            case Effects.DISTORTION_EFFECT:
                alEffecti(iEffect, AL_EFFECT_TYPE, AL_EFFECT_DISTORTION);
                break;
            case Effects.ECHO_EFFECT:
                alEffecti(iEffect, AL_EFFECT_TYPE, AL_EFFECT_ECHO);
                break;
            case Effects.REVERB_EFFECT:
                alEffecti(iEffect, AL_EFFECT_TYPE, AL_EFFECT_REVERB);
                alEffectf(iEffect, AL_REVERB_DECAY_TIME, 5.0f);
                break;
            case Effects.FLANGER_EFFECT:
                alEffecti(iEffect, AL_EFFECT_TYPE, AL_EFFECT_FLANGER);
                break;
            case Effects.COMPRESSOR_EFFECT:
                alEffecti(iEffect, AL_EFFECT_TYPE, AL_EFFECT_COMPRESSOR);
                break;
        }
        alAuxiliaryEffectSloti(effectSlot, AL_EFFECTSLOT_EFFECT, iEffect);
        alSource3i(sources.get(sound.index), AL_AUXILIARY_SEND_FILTER, effectSlot, 0, AL_FILTER_NULL);
        effects.add(sound.index, iEffect);
        effectSlots.add(sound.index, effectSlot);
        System.out.println(effectSlots.get(sound.index));
    }

    @Override
    public void deleteEffect(final Sound sound, final Effect effect) {
        alSource3i(sources.get(sound.index), AL_AUXILIARY_SEND_FILTER, AL_EFFECTSLOT_NULL, 0, AL_FILTER_NULL);
        alAuxiliaryEffectSloti(effectSlots.get(sound.index), AL_EFFECTSLOT_EFFECT, AL_EFFECT_NULL);
        alDeleteEffects(effects.get(sound.index));
        effects.remove(sound.index);
        effectSlots.remove(sound.index);
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

    @Override
    public void setPosition(final Sound sound) {
        alSourcei(sources.get(sound.index), AL_SOURCE_RELATIVE, AL_TRUE);
        alSource3f(sources.get(sound.index), AL_POSITION, sound.getPosition().getX(), sound.getPosition().getY(),
                sound.getPosition().getZ());
    }

    @Override
    public void addFilter(Sound sound, Filter filter) {
        int iFilter = alGenFilters();
        switch (filter.getType()) {
            case Filters.HIGHPASS_FILTER:
                alFilteri(iFilter, AL_FILTER_TYPE, AL_FILTER_HIGHPASS);
                break;
            case Filters.LOWPASS_FILTER:
                alFilteri(iFilter, AL_FILTER_TYPE, AL_FILTER_LOWPASS);
                alFilterf(iFilter, AL_LOWPASS_GAIN, 0.5f);
                alFilterf(iFilter, AL_LOWPASS_GAINHF, 0.5f);
                break;
            case Filters.BANDPASS_FILTER:
                alFilteri(iFilter, AL_FILTER_TYPE, AL_FILTER_BANDPASS);
                break;
        }
        alSourcei(sources.get(sound.index), AL_DIRECT_FILTER, iFilter);
        filters.add(sound.index, iFilter);
    }

    @Override
    public void deleteFilter(Sound sound, Filter filter) {
        alSourcei(sources.get(sound.index), AL_DIRECT_FILTER, AL_FILTER_NULL);
        alDeleteFilters(filters.get(sound.index));
    }

}
