package Leclair.graphics.material;

import Leclair.graphics.image.Texture;
import Leclair.graphics.shader.Shader;
import Leclair.graphics.shader.Shaders;
import Leclair.math.Color;
import Leclair.math.Colors;

/**
 * @since v1
 * @author Kane Burnett
 */
public class Material {

    Color ambientColor = Colors.BLACK;
    Color diffuseColor = Colors.WHITE;
    Color specularColor = Colors.BLACK;
    float reflectance = 0;
    Texture texture = null;
    Shader vertexShader = null;
    Shader fragmentShader = null;

    public Material(Color color, float reflectance, byte type) {
        this(color, color, color, reflectance, null, type);
    }

    public Material(Color color, float reflectance, Texture texture, byte type) {
        this(color, color, color, reflectance, texture, type);
    }

    public Material(Texture texture, byte type) {
        this(Colors.BLACK, Colors.WHITE, Colors.BLACK, 0f, texture, type);
    }

    public Material(Color ambientColor, Color diffuseColor, Color specularColor, float reflectance, Texture texture,
            byte type) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
        switch (type) {
            case Materials.UNLIT_MATERIAL:
            vertexShader = new Shader("shaders/light.vert", Shaders.VERTEX_SHADER);
            fragmentShader = new Shader("shaders/light.frag", Shaders.FRAGMENT_SHADER);
            // vertexShader.compile();
            // fragmentShader.compile();
                break;
            case Materials.LIT_MATERIAL: // TODO
            vertexShader = new Shader("shaders/light.vert", Shaders.VERTEX_SHADER);
            fragmentShader = new Shader("shaders/light.frag", Shaders.FRAGMENT_SHADER);
            // vertexShader.compile();
            // fragmentShader.compile();
                break;
            default:
                throw new IllegalArgumentException("Unknown material type");
        }
    }

    public Color getAmbientColor() {
        return this.ambientColor;
    }

    public Color getDiffuseColor() {
        return this.diffuseColor;
    }

    public Color getSpecularColor() {
        return this.specularColor;
    }

    public float getReflectance() {
        return this.reflectance;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public Shader getVertexShader() {
        return this.vertexShader;
    }

    public Shader getFragmentShader() {
        return this.fragmentShader;
    }
}