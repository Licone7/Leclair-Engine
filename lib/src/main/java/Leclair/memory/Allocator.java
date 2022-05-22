package Leclair.memory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

/**
 * The buffers in this class are for convenience and are used throughout the
 * engine to avoid expensive buffer allocations for trivially small data. The
 * Garbage Collector automatically cleans up these buffers so they never have
 * to be manually de-allocated.
 * 
 * @since v1.0
 * @author Brett Burnett
 */
public class Allocator {

    /**
     * A utility ByteBuffer with a capacity of 1
     */
    public static final ByteBuffer UtilityBB1 = BufferUtils.createByteBuffer(1);

    /**
     * A utility ByteBuffer with a capacity of 128
     */
    public static final ByteBuffer UtilityBB2 = BufferUtils.createByteBuffer(128);

    /**
     * A utility ByteBuffer with a capacity of 256
     */
    public static final ByteBuffer UtilityBB3 = BufferUtils.createByteBuffer(256);

    /**
     * A utility IntBuffer with a capacity of 1
     */
    public static final IntBuffer UtilityIB1 = BufferUtils.createIntBuffer(1);

    /**
     * A utility IntBuffer with a capacity of 128
     */
    public static final IntBuffer UtilityIB2 = BufferUtils.createIntBuffer(128);

    /**
     * A utility FloatBuffer with a capacity of 1
     */
    public static final FloatBuffer UtilityFB1 = BufferUtils.createFloatBuffer(1);

    /**
     * A utility FloatBuffer with a capacity of 128
     */
    public static final FloatBuffer UtilityFB2 = BufferUtils.createFloatBuffer(128);

    /**
     * A utility PointerBuffer with a capacity of 1
     */
    public static final PointerBuffer UtilityPB1 = BufferUtils.createPointerBuffer(1);

    /**
     * A utility PointerBuffer with a capacity of 128
     */
    public static final PointerBuffer UtilityPB2 = BufferUtils.createPointerBuffer(128); 

    public static void deallocate() {

    }
}