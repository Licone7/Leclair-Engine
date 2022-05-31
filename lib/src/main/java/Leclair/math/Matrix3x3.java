package Leclair.math;

/**
 * This class represents a standard three-by-three matrix using floats
 * 
 * @since v1
 * @author Kane Burnett
 */
public class Matrix3x3 {

    float r0c0 = 0f; // Row 0 Column 0
    float r0c1 = 0f; // Row 0 Column 1
    float r0c2 = 0f; // Row 0 Column 2
    float r1c0 = 0f; // Row 1 Column 0
    float r1c1 = 0f; // Row 1 Column 1
    float r1c2 = 0f; // Row 1 Column 2
    float r2c0 = 0f; // Row 2 Column 0
    float r2c1 = 0f; // Row 2 Column 1
    float r2c2 = 0f; // Row 2 Column 2

    public Matrix3x3() {

    }

    public Matrix3x3(float r0c0, float r0c1, float r0c2, float r1c0, float r1c1, float r1c2, float r2c0, float r2c1,
            float r2c2) {
        this.r0c0 = r0c0;
        this.r0c1 = r0c1;
        this.r0c2 = r0c2;
        this.r1c0 = r1c0;
        this.r1c1 = r1c1;
        this.r1c2 = r1c2;
        this.r2c0 = r2c0;
        this.r2c1 = r2c1;
        this.r2c2 = r2c2;
    }

    public Vector3 getRow(int row) {
        switch (row) {
            case 0:
                return new Vector3(r0c0, r0c1, r0c2);
            case 1:
                return new Vector3(r1c0, r1c1, r1c2);
            case 2:
                return new Vector3(r2c0, r2c1, r2c2);
            default:
                return null;
        }
    }

    public Vector3 getColumn(int column) {
        switch (column) {
            case 0:
                return new Vector3(r0c0, r1c0, r2c0);
            case 1:
                return new Vector3(r0c1, r1c1, r2c1);
            case 2:
                return new Vector3(r0c2, r1c2, r2c2);
            default:
                return null;
        }
    }
}
