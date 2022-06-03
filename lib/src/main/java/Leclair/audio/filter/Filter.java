package Leclair.audio.filter;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Filter {

    byte type = Filters.HIGHPASS_FILTER;

    public Filter(byte type) {
        this.type = type;
    }

    public byte getType() {
        return this.type;
    }
    
}
