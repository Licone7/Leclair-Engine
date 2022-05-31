package Leclair.graphics.image;

import java.nio.IntBuffer;

import Leclair.asset.AssetLoader;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

/**
 * @since v1
 * @author Kane Burnett
 */
public class ImageLoader {

    public static void loadImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer components = stack.mallocInt(1);
            STBImage.stbi_load_from_memory(AssetLoader.importAsBinary("path", 1024), width,
                    height, components, 4);
            width.get();
            height.get();
            components.get();
        }
    }
}
