package Leclair.math;

public class Vector4 {

    float x = 0f;
    float y = 0f;
    float z = 0f;
    float w = 0f;

    public Vector4() {

    }

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void add(float x, float y, float z, float w) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
        this.w = this.w + w;
    }

    public void add(Vector4 vector) {
        this.x = this.x + vector.x;
        this.y = this.y + vector.y;
        this.z = this.z + vector.z;
        this.w = this.w + vector.w;
    }

    public void subtract(float x, float y, float z, float w) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
        this.w = this.w - w;
    }

    public void subtract(Vector4 vector) {
        this.x = this.x - vector.x;
        this.y = this.y - vector.y;
        this.z = this.z - vector.z;
        this.w = this.w - vector.w;
    }

    public void multiply(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        this.w = this.w * scalar;
    }

    public void multiply(float x, float y, float z, float w) {
        this.x = this.x * x;
        this.y = this.y * y;
        this.z = this.z * z;
        this.w = this.w * w;
    }

    public void multiply(Vector4 vector) {
        this.x = this.x * vector.x;
        this.y = this.y * vector.y;
        this.z = this.z * vector.z;
        this.w = this.w * vector.w;
    }

    public void divide(float scalar) {
        scalar = 1f / scalar;
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
        this.w = this.w * scalar;
    }

    public void divide(float x, float y, float z, float w) {
        this.x = this.x / x;
        this.y = this.y / y;
        this.z = this.z / z;
        this.w = this.w / w;
    }

    public void divide(Vector4 vector) {
        this.x = this.x / vector.x;
        this.y = this.y / vector.y;
        this.z = this.z / vector.z;
        this.w = this.w / vector.w;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public void normalize() {

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
