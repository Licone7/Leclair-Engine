package Leclair.input;

public interface Input {

    public static final byte KEY_A = 1;

    public static final byte KEY_B = 2;

    public static final byte KEY_C = 3;

    public static final byte KEY_D = 4;

    public static final byte KEY_W = 23;

    public static final byte KEY_S = 19;

    public static final byte KEY_Q = 17;

    public static final byte KEY_Z = 26;

    public void init();

    public boolean nativeKeyDown(byte key);

    public void loop();

    public void clenaup();
    
}
