package Leclair.math;

/**
 * This class contains basic math functions.
 * 
 * @since 1.0
 * @author Brett Burnett
 */
public class MathUtilities {

    private MathUtilities() {
        throw new IllegalAccessError();
    }

    public static final float PI = 3.141592f;

    public static final float E = 2.718281f;

    public static final float ROOT_TWO = 1.414213f;

    public static final float ROOT_THREE = 1.732050f;

    public static float power(float number, int exponent) {
        float result = 1;
        for (int i = 0; i < exponent; i++) {
            result = result * number;
        }
        return result;
    }

    public static int factorial(int number) {
        int factorial = 1;
        for (int i = 1; i <= number; i++) {
            factorial = factorial * i;
        }
        return factorial;
    }

    // TRIGONOMETRIC FUNCTIONS

    public static float sine(float number) {
        // Use the Taylor Series
        number = number - ((power(number, 3) / factorial(3)) - (power(number, 5) / factorial(5)));
        return number;
    }

    public static float cosine(float number) {
        // Use the Taylor Series
        number = 1 - ((power(number, 2) / factorial(2)) + (power(number, 4) / factorial(4)));
        return number;
    }

    // ANGLE CONVERSION

    public static float asRadians(float degrees) {
        return degrees * (PI / 180);
    }

    public static float asDegrees(float radians) {
        return radians * (180 / PI);
    }

    // GENERATE A RANDOM NUMBER

    public static float generateRandom() {
        return System.nanoTime() / System.currentTimeMillis();
    }
}