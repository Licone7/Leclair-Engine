package Leclair.graphics.shader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Shader {

    public boolean compiled = false;
    public String filePath = null;
    public ByteBuffer binaryCode;
    public byte shaderType;

    public static List<Shader> shaders = new ArrayList<Shader>();

    public Shader(String path, byte type) {
        this.filePath = path;
        this.shaderType = type;
        shaders.add(this);
    }

}
