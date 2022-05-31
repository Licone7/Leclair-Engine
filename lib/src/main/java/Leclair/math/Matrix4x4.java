package Leclair.math;

/**
 * This class represents a standard four-by-four matrix using floats
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Matrix4x4 {

    float r0c0 = 0f;
    float r1c0 = 0f;
    float r2c0 = 0f;
    float r3c0 = 0f;
    float r0c1 = 0f;
    float r1c1 = 0f;
    float r2c1 = 0f;
    float r3c1 = 0f;
    float r0c2 = 0f;
    float r1c2 = 0f;
    float r2c2 = 0f;
    float r3c2 = 0f;
    float r0c3 = 0f;
    float r1c3 = 0f;
    float r2c3 = 0f;
    float r3c3 = 0f;

    public Matrix4x4() {

    }

    public Matrix4x4(float r0c0, float r1c0, float r2c0, float r3c0, /* */ float r0c1, float r1c1, float r2c1,
            float r3c1, /* */ float r0c2, float r1c2, float r2c2, float r3c2, /* */ float r0c3, float r1c3, float r2c3,
            float r3c3) {
        this.r0c0 = r0c0;
        this.r1c0 = r1c0;
        this.r2c0 = r2c0;
        this.r3c0 = r3c0;
        this.r0c1 = r0c1;
        this.r1c1 = r1c1;
        this.r2c1 = r2c1;
        this.r3c1 = r3c1;
        this.r0c2 = r0c2;
        this.r1c2 = r1c2;
        this.r2c2 = r2c2;
        this.r3c2 = r3c2;
        this.r0c3 = r0c3;
        this.r1c3 = r1c3;
        this.r2c3 = r2c3;
        this.r3c3 = r3c3;
    }

    public Vector4 getRow(int row) {
        switch (row) {
            case 0:
                return new Vector4(r0c0, r0c1, r0c2, r0c3);
            case 1:
                return new Vector4(r1c0, r1c1, r1c2, r1c3);
            case 2:
                return new Vector4(r2c0, r2c1, r2c2, r2c3);
            case 3:
                return new Vector4(r3c0, r3c1, r3c2, r3c3);
            default:
                return null;
        }
    }

    public Vector4 getColumn(int column) {
        switch (column) {
            case 0:
                return new Vector4(r0c0, r1c0, r2c0, r3c0);
            case 1:
                return new Vector4(r0c1, r1c1, r2c1, r3c1);
            case 2:
                return new Vector4(r0c2, r1c2, r2c2, r3c2);
            case 3:
                return new Vector4(r0c3, r1c3, r2c3, r3c3);
            default:
                return null;
        }
    }
}
