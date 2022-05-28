package Leclair.audio.effect;

/**
 * @since v1
 * @author Brett Burnett
 */
public class Effect {

    byte type = Effects.REVERB_EFFECT;

    public Effect(byte type) {
        this.type = type;
    }

    public byte getType() {
        return this.type;
    }
}