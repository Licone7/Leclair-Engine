package Leclair.math;

/**
 * This class represents a standard four component vector using floats
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Vector4 {

    float x;
    float y;
    float z;
    float w;

    public Vector4() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.w = 0f;
    }

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4 set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4 setX(float x) {
        this.x = x;
        return this;
    }

    public Vector4 setY(float y) {
        this.y = y;
        return this;
    }

    public Vector4 setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector4 setW(float w) {
        this.w = w;
        return this;
    }

    public Vector4 add(float x, float y, float z, float w) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
        this.w = this.w + w;
        return this;
    }

    public Vector4 add(Vector4 vector) {
        this.x = this.x + vector.x;
        this.y = this.y + vector.y;
        this.z = this.z + vector.z;
        this.w = this.w + vector.w;
        return this;
    }

    public Vector4 subtract(float x, float y, float z, float w) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
        this.w = this.w - w;
        return this;
    }

    public Vector4 subtract(Vector4 vector) {
        this.x = this.x - vector.x;
        this.y = this.y - vector.y;
        this.z = this.z - vector.z;
        this.w = this.w - vector.w;
        return this;
    }

    public Vector4 multiply(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        this.w = this.w * scalar;
        return this;
    }

    public Vector4 multiply(float x, float y, float z, float w) {
        this.x = this.x * x;
        this.y = this.y * y;
        this.z = this.z * z;
        this.w = this.w * w;
        return this;
    }

    public Vector4 multiply(Vector4 vector) {
        this.x = this.x * vector.x;
        this.y = this.y * vector.y;
        this.z = this.z * vector.z;
        this.w = this.w * vector.w;
        return this;
    }

    public Vector4 divide(float scalar) {
        scalar = 1f / scalar;
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        this.w = this.w * scalar;
        return this;
    }

    public Vector4 divide(float x, float y, float z, float w) {
        this.x = this.x / x;
        this.y = this.y / y;
        this.z = this.z / z;
        this.w = this.w / w;
        return this;
    }

    public Vector4 divide(Vector4 vector) {
        this.x = this.x / vector.x;
        this.y = this.y / vector.y;
        this.z = this.z / vector.z;
        this.w = this.w / vector.w;
        return this;
    }

    public Vector4 negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    public Vector4 normalize() {
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

    public float getW() {
        return this.w;
    }
    
}
