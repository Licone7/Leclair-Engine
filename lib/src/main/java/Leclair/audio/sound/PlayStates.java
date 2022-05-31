package Leclair.audio.sound;

public interface PlayStates {
    public static byte STATE_DELETE = 0;
    public static byte STATE_INITIALIZED = 1;
    public static byte STATE_UNINITIALIZED = 2;
    public static byte STATE_STOPPED = 3;
    public static byte STATE_PLAYING = 4;
    public static byte STATE_PAUSED = 5;
}