package Leclair.memory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

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
    public static final ByteBuffer bb1 = BufferUtils.createByteBuffer(1);

    /**
     * A utility ByteBuffer with a capacity of 64
     */
    public static final ByteBuffer bb64 = BufferUtils.createByteBuffer(64);

    /**
     * A utility ByteBuffer with a capacity of 128
     */
    public static final ByteBuffer bb128 = BufferUtils.createByteBuffer(128);

    /**
     * A utility IntBuffer with a capacity of 1
     */
    public static final IntBuffer ib1 = BufferUtils.createIntBuffer(1);

    /**
     * A utility IntBuffer with a capacity of 128
     */
    public static final IntBuffer ib128 = BufferUtils.createIntBuffer(128);

    /**
     * A utility LongBuffer with a capacity of 1
     */
    public static final LongBuffer lb1 = BufferUtils.createLongBuffer(1);

    /**
     * A utility LongBuffer with a capacity of 128
     */
    public static final LongBuffer lb128 = BufferUtils.createLongBuffer(128);

    /**
     * A utility PointerBuffer with a capacity of 1
     */
    public static final PointerBuffer pb1 = BufferUtils.createPointerBuffer(1);

    /**
     * A utility PointerBuffer with a capacity of 64
     */
    public static final PointerBuffer pb64 = BufferUtils.createPointerBuffer(64); 

    /**
     * A utility PointerBuffer with a capacity of 128
     */
    public static final PointerBuffer pb128 = BufferUtils.createPointerBuffer(128); 

    public static void deallocate() {

    }
}