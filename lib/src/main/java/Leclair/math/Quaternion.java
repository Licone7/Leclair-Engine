package Leclair.math;

/**
 * This class represents a standard quaternion using floats, composed of three
 * imaginary parts (X, Y, Z) and one real part (W)
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Quaternion {

    float x;
    float y;
    float z;
    float w;

    public Quaternion() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(float[] array) {
        this.x = array[0];
        this.y = array[1];
        this.z = array[2];
        this.w = array[3];
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void add(float x, float y, float z, float w) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
        this.w = this.w + w;
    }

    public void add(Quaternion quaternion) {
        this.x = this.x + quaternion.x;
        this.y = this.y + quaternion.y;
        this.z = this.z + quaternion.z;
        this.w = this.w + quaternion.w;
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
