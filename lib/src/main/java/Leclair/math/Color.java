package Leclair.math;

/**
 * This class represents a color with a four component vector
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Color {

    public static final Color RED = new Color(1f, 0f, 0f, 0f);

    public static final Color GREEN = new Color(0f, 1f, 0f, 0f);

    public static final Color BLUE = new Color(0f, 0f, 1f, 0f);

    public static final Color BLACK = new Color(0f, 0f, 0f, 0f);

    public static final Color WHITE = new Color(1, 1, 1, 0);

    float r = 0f;
    float g = 0f;
    float b = 0f;
    float a = 0f;

    public Color() {
        
    }

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(Vector4 vector) {
        this.r = vector.x;
        this.g = vector.y;
        this.b = vector.z;
        this.a = vector.w;
    }

    public void set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void setR(float r) {
        this.r = r;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setB(float b) {
        this.b = b;
    }

    public void setA(float a) {
        this.a = a;
    }

    public void add(float r, float g, float b, float a) {
        this.r = this.r + r;
        this.g = this.g + g;
        this.b = this.b + b;
        this.a = this.a + a;
    }

    public void add(Color color) {
        this.r = this.r + color.r;
        this.g = this.g + color.g;
        this.b = this.b + color.b;
        this.a = this.a + color.a;
    }

    public void subtract(float r, float g, float b, float a) {
        this.r = this.r - r;
        this.g = this.g - g;
        this.b = this.b - b;
        this.a = this.a - a;
    }

    public void subtract(Color color) {
        this.r = this.r - color.r;
        this.g = this.g - color.g;
        this.b = this.b - color.b;
        this.a = this.a - color.a;
    }

    public void multiply(float scalar) {
        this.r = this.r * scalar;
        this.g = this.g * scalar;
        this.b = this.b * scalar;
        this.a = this.a * scalar;
    }

    public void multiply(float r, float g, float b, float a) {
        this.r = this.r * r;
        this.g = this.g * g;
        this.b = this.b * b;
        this.a = this.a * a;
    }

    public void multiply(Color color) {
        this.r = this.r * color.r;
        this.g = this.g * color.g;
        this.b = this.b * color.b;
        this.a = this.a * color.a;
    }

    public void divide(float scalar) {
        scalar = 1f / scalar;
        this.r = this.r * scalar;
        this.g = this.g * scalar;
        this.b = this.b * scalar;
        this.a = this.a * scalar;
    }

    public void divide(float r, float g, float b, float a) {
        this.r = this.r / r;
        this.g = this.g / g;
        this.b = this.b / b;
        this.a = this.a / a;
    }

    public void divide(Color color) {
        this.r = this.r / color.r;
        this.g = this.g / color.g;
        this.b = this.b / color.b;
        this.a = this.a / color.a;
    }

    // public void GenerateRandom() {
    //     this.r = 255f / Constants.GenerateRandom();
    //     this.g = 255f / Constants.GenerateRandom();
    //     this.b = 255f / Constants.GenerateRandom();
    //     this.a = 255f / Constants.GenerateRandom();
    // }

    public Vector4 AsVector() {
        return new Vector4(r, g, b, a);
    }

    public float getR() {
        return this.r;
    }

    public float getG() {
        return this.g;
    }

    public float getB() {
        return this.b;
    }

    public float getA() {
        return this.a;
    }
}