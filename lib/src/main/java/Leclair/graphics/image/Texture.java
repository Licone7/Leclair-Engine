package Leclair.graphics.image;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import Leclair.asset.AssetLoader;
import Leclair.graphics.shader.Shader;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Texture {

    String path;
    int width;
    int height;
    int components;
    ByteBuffer data = null;
    Shader textShader;
    boolean initialized = false;

    public Texture(final String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final IntBuffer width = stack.mallocInt(1);
            final IntBuffer height = stack.mallocInt(1);
            final IntBuffer components = stack.mallocInt(1);
            this.data = STBImage.stbi_load_from_memory(AssetLoader.importAsBinary(path, 1024), width, height,
                    components, 4);
            this.width = width.get();
            this.height = height.get();
            this.components = components.get();
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getComponents() {
        return this.components;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public void free() {
        STBImage.stbi_image_free(this.data);
    }
}