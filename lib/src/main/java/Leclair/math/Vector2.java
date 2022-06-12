package Leclair.math;

/**
 * This class represents a standard two component vector using floats
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Vector2 {

    float x = 0f;
    float y = 0f;

    public Vector2() {

    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector2 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x = this.x + x;
        this.y = this.y + y;
        return this;
    }

    public Vector2 add(Vector2 vector) {
        this.x = this.x + vector.x;
        this.y = this.y + vector.y;
        return this;
    }

    public Vector2 subtract(float x, float y) {
        this.x = this.x - x;
        this.y = this.y - y;
        return this;
    }

    public Vector2 subtract(Vector2 vector) {
        this.x = this.x - vector.x;
        this.y = this.y - vector.y;
        return this;
    }

    public Vector2 multiply(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        return this;
    }

    public Vector2 multiply(float x, float y) {
        this.x = this.x * x;
        this.y = this.y * y;
        return this;
    }

    public Vector2 multiply(Vector2 vector) {
        this.x = this.x * vector.x;
        this.y = this.y * vector.y;
        return this;
    }

    public Vector2 divide(float scalar) {
        scalar = 1f / scalar;
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        return this;
    }

    public Vector2 divide(float x, float y) {
        this.x = this.x / x;
        this.y = this.y / y;
        return this;
    }

    public Vector2 divide(Vector2 vector) {
        this.x = this.x / vector.x;
        this.y = this.y / vector.y;
        return this;
    }

    public Vector2 negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2 normalize() {
        float inverse = MathUtilities.inverseSquareRoot(x * x + y * y);
        this.x = x * inverse;
        this.y = y * inverse;
        return this;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

}
