
package Leclair.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.lwjgl.BufferUtils;

/**
 * @since v1
 * @author Kane Burnett
 */
public class AssetLoader {

    public static String pathOfResourcesFolder = null;

    // TODO: MULTITHREAD THIS
    public static void setup() {
        String tempPath = System.getProperty("user.dir") + "/src/main/resources/";
        if (tempPath.contains("\\")) {
            tempPath = tempPath.replaceAll("\\\\", "/");
        }
        pathOfResourcesFolder = tempPath;
    }

    public static String importAsString(final String path) {
        return null;
    }

    public static ByteBuffer importAsBinary(final String path) throws IOException {
        return importAsBinary(path, 64 * 1024);
    }

    public static ByteBuffer importAsBinary(final String resource, final int bufferSize) {
        ByteBuffer buffer = null;
        try {
            // Path path = Paths.get(resource);
            // if (Files.isReadable(path)) {
            // try (SeekableByteChannel fc = Files.newByteChannel(path)) {
            // buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
            // while (fc.read(buffer) != -1) {

            // }
            // }
            // } else {
            // try (InputStream source =
            // AssetLoader.class.getClassLoader().getResourceAsStream(resource);
            // ReadableByteChannel rbc = Channels.newChannel(source)) {
            // buffer = BufferUtils.createByteBuffer(bufferSize);
            // // buffer = MemoryUtil.memAlloc(bufferSize);
            // // buffer = LibCStdlib.malloc(bufferSize);
            // while (true) {
            // int bytes = rbc.read(buffer);
            // if (bytes == -1) {
            // break;
            // }
            // if (buffer.remaining() == 0) {
            // ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 3 /
            // 2);
            // buffer.flip();
            // newBuffer.put(buffer);
            // buffer = newBuffer;
            // }
            // }
            // }
            // }
            // buffer.flip();
            // MemoryUtil.memSlice(buffer);

            final URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
            if (url == null)
                throw new IOException("Classpath resource not found: " + resource);
            final File file = new File(url.getFile());
            if (file.isFile()) {
                final FileInputStream fis = new FileInputStream(file);
                final FileChannel fc = fis.getChannel();
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                fc.close();
                fis.close();
            } else {
                buffer = BufferUtils.createByteBuffer(bufferSize);
                final InputStream source = url.openStream();
                if (source == null)
                    throw new FileNotFoundException(resource);
                try {
                    final byte[] buf = new byte[8192];
                    while (true) {
                        final int bytes = source.read(buf, 0, buf.length);
                        if (bytes == -1)
                            break;
                        if (buffer.remaining() < bytes) {
                            buffer = resizeBuffer(buffer,
                                    Math.max(buffer.capacity() * 2, buffer.capacity() - buffer.remaining() + bytes));
                        }
                        buffer.put(buf, 0, bytes);
                    }
                    buffer.flip();
                } finally {
                    source.close();
                }
            }
        } catch (final IOException e) {

        }
        return buffer;
    }

    private static ByteBuffer resizeBuffer(final ByteBuffer buffer, final int newCapacity) {
        System.out.println("NEW CAPACIY" + newCapacity);
        final ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
