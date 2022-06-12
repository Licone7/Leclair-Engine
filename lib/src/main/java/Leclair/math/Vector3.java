package Leclair.math;

/**
 * This class represents a standard three component vector using floats
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Vector3 {

    float x;
    float y;
    float z;

    public Vector3() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector3 setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector3 add(float x, float y, float z) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
        return this;
    }

    public Vector3 add(Vector3 vector) {
        this.x = this.x + vector.x;
        this.y = this.y + vector.y;
        this.z = this.z + vector.z;
        return this;
    }

    public Vector3 subtract(float x, float y, float z) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
        return this;
    }

    public Vector3 subtract(Vector3 vector) {
        this.x = this.x - vector.x;
        this.y = this.y - vector.y;
        this.z = this.z - vector.z;
        return this;
    }

    public Vector3 multiply(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        return this;
    }

    public Vector3 multiply(float x, float y, float z) {
        this.x = this.x * x;
        this.y = this.y * y;
        this.z = this.z * z;
        return this;
    }

    public Vector3 multiply(Vector3 vector) {
        this.x = this.x * vector.x;
        this.y = this.y * vector.y;
        this.z = this.z * vector.z;
        return this;
    }

    public Vector3 divide(float scalar) {
        scalar = 1f / scalar;
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        return this;
    }

    public Vector3 divide(float x, float y, float z) {
        this.x = this.x / x;
        this.y = this.y / y;
        this.z = this.z / z;
        return this;
    }

    public Vector3 divide(Vector3 vector) {
        this.x = this.x / vector.x;
        this.y = this.y / vector.y;
        this.z = this.z / vector.z;
        return this;
    }

    public Vector3 negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Vector3 normalize() {
        return this;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

}
