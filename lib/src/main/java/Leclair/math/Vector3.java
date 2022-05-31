package Leclair.math;

/**
 * This class represents a standard three component vector using floats
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Vector3 {

    float x = 0f;
    float y = 0f;
    float z = 0f;

    public Vector3() {

    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public void add(float x, float y, float z) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
    }

    public void add(Vector3 vector) {
        this.x = this.x + vector.x;
        this.y = this.y + vector.y;
        this.z = this.z + vector.z;
    }

    public void subtract(float x, float y, float z) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
    }

    public void subtract(Vector3 vector) {
        this.x = this.x - vector.x;
        this.y = this.y - vector.y;
        this.z = this.z - vector.z;
    }

    public void multiply(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
    }

    public void multiply(float x, float y, float z) {
        this.x = this.x * x;
        this.y = this.y * y;
        this.z = this.z * z;
    }

    public void multiply(Vector3 vector) {
        this.x = this.x * vector.x;
        this.y = this.y * vector.y;
        this.z = this.z * vector.z;
    }

    public void divide(float scalar) {
        scalar = 1f / scalar;
        this.x = this.x * scalar;
        this.y = this.y * scalar;
        this.z = this.z * scalar;
    }

    public void divide(float x, float y, float z) {
        this.x = this.x / x;
        this.y = this.y / y;
        this.z = this.z / z;
    }

    public void divide(Vector3 vector) {
        this.x = this.x / vector.x;
        this.y = this.y / vector.y;
        this.z = this.z / vector.z;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
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

}
