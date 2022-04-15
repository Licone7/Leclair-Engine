package Leclair.graphics.renderer;

import Leclair.graphics.scene.Mesh;
import Leclair.graphics.shader.Shader;
import Leclair.math.Color;

public interface Renderer {

    public void init();

    public void ReadInfoFromGPU();

    public void loop();

    public void setBackgroundColor(Color backgroundColor);

    public void addMesh(Mesh mesh);

    public void deleteMesh(Mesh mesh);

    public void setWireframe(boolean enabled);

    public void addShader(Shader shader, int program, int index);

    public void deleteShader(Shader shader);

    public void cleanup();
    
}
