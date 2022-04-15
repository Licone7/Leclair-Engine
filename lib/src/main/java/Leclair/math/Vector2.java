package Leclair.math;

public class Vector2 {
    
    float x = 0f;
    float y = 0f;

    public Vector2() {

    }

    public Vector2 (float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void add(float x, float y) {
        this.x = this.x + x;
        this.y = this.y + y;
    }

    public void add(Vector2 vector) {
        this.x = this.x + vector.x;
        this.y = this.y + vector.y;
    }

    public void subtract(float x, float y) {
        this.x = this.x - x;
        this.y = this.y - y;
    }

    public void subtract(Vector2 vector) {
        this.x = this.x - vector.x;
        this.y = this.y - vector.y;
    }

    public void multiply(float scalar) {
        this.x = this.x * scalar;
        this.y = this.y * scalar;
    }

    public void multiply(float x, float y) {
        this.x = this.x * x;
        this.y = this.y * y;
    }

    public void multiply(Vector2 vector) {
        this.x = this.x * vector.x;
        this.y = this.y * vector.y;
    }

    public void divide(float scalar) {
        scalar = 1f / scalar;
        this.x = this.x * scalar;
        this.y = this.y * scalar;
    }

    public void divide(float x, float y) {
        this.x = this.x / x;
        this.y = this.y / y;
    }

    public void divide(Vector2 vector) {
        this.x = this.x / vector.x;
        this.y = this.y / vector.y;
    }

    public void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }
 
    public void normalize() {
        
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

}
