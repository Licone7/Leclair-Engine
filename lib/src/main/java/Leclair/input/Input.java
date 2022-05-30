package Leclair.input;

public interface Input {

    public void init();

    public boolean nativeKeyDown(byte key);

    public void loop();

    public void cleanup();
    
}