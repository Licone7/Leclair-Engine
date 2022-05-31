package Leclair.graphics.shader;

public interface Shaders {

    /**
     * Handle to a vertex shader <br>
     * <br>
     * Used to specify the shader type when creating a new Shader();
     */
    public static final byte VERTEX_SHADER = 10;

    /**
     * Handle to a fragment shader <br>
     * <br>
     * Used to specify the shader type when creating a new Shader();
     */
    public static final byte FRAGMENT_SHADER = 11;

    /**
     * Handle to a compute shader <br>
     * <br>
     * Used to specify the shader type when creating a new Shader();
     */
    public static final byte COMPUTE_SHADER = 12;   
}