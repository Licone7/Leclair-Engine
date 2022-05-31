package Leclair.input;

/**
 * @since v1
 * @author Kane Burnett
 */
public interface Input {

    public void init();

    public boolean nativeKeyDown(byte key);

    public void loop();

    public void cleanup();
    
}