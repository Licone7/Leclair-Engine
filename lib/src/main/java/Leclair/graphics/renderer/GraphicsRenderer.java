package Leclair.graphics.renderer;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;

/**
 * @since v1
 * @author Kane Burnett
 */
public interface GraphicsRenderer {

    public void init();

    public void printCapabilities();

    public void loop();

    public void setBackgroundColor(Color backgroundColor);

    public void processMesh(Mesh mesh);

    public void renderMesh(Mesh mesh);

    public void removeMesh(Mesh mesh);

    public void deleteMesh(Mesh mesh);

    public void setWireframe(boolean enabled);

    public void addShader(Shader shader, int program, int index);

    public void deleteShader(Shader shader);

    public void cleanup();
    
}
