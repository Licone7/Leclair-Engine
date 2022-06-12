package Leclair.math;

/**
 * This class represents a color with a four component vector
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Color {

    float r;
    float g;
    float b;
    float a;

    public Color() {
        this.r = 0f;
        this.g = 0f;
        this.b = 0f;
        this.a = 0f;
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

    public Color set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public Color setR(float r) {
        this.r = r;
        return this;
    }

    public Color setG(float g) {
        this.g = g;
        return this;
    }

    public Color setB(float b) {
        this.b = b;
        return this;
    }

    public Color setA(float a) {
        this.a = a;
        return this;
    }

    public Color add(float r, float g, float b, float a) {
        this.r = this.r + r;
        this.g = this.g + g;
        this.b = this.b + b;
        this.a = this.a + a;
        return this;
    }

    public Color add(Color color) {
        this.r = this.r + color.r;
        this.g = this.g + color.g;
        this.b = this.b + color.b;
        this.a = this.a + color.a;
        return this;
    }

    public Color subtract(float r, float g, float b, float a) {
        this.r = this.r - r;
        this.g = this.g - g;
        this.b = this.b - b;
        this.a = this.a - a;
        return this;
    }

    public Color subtract(Color color) {
        this.r = this.r - color.r;
        this.g = this.g - color.g;
        this.b = this.b - color.b;
        this.a = this.a - color.a;
        return this;
    }

    public Color multiply(float scalar) {
        this.r = this.r * scalar;
        this.g = this.g * scalar;
        this.b = this.b * scalar;
        this.a = this.a * scalar;
        return this;
    }

    public Color multiply(float r, float g, float b, float a) {
        this.r = this.r * r;
        this.g = this.g * g;
        this.b = this.b * b;
        this.a = this.a * a;
        return this;
    }

    public Color multiply(Color color) {
        this.r = this.r * color.r;
        this.g = this.g * color.g;
        this.b = this.b * color.b;
        this.a = this.a * color.a;
        return this;
    }

    public Color divide(float scalar) {
        scalar = 1f / scalar;
        this.r = this.r * scalar;
        this.g = this.g * scalar;
        this.b = this.b * scalar;
        this.a = this.a * scalar;
        return this;
    }

    public Color divide(float r, float g, float b, float a) {
        this.r = this.r / r;
        this.g = this.g / g;
        this.b = this.b / b;
        this.a = this.a / a;
        return this;
    }

    public Color divide(Color color) {
        this.r = this.r / color.r;
        this.g = this.g / color.g;
        this.b = this.b / color.b;
        this.a = this.a / color.a;
        return this;
    }

    public Vector4 AsVector() {
        return new Vector4(this.r, this.g, this.b, this.a);
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
