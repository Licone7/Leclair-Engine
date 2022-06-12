package Leclair.math;

/**
 * This class contains basic math functions
 * 
 * @since 1.0
 * @author Kane Burnett
 */
public class MathUtilities {

    private MathUtilities() {
        throw new IllegalAccessError();
    }

    public static float power(final float number, final int exponent) {
        float result = 1;
        for (int i = 0; i < exponent; i++) {
            result = result * number;
        }
        return result;
    }

    /** 
     * Calculates the factorial of the given number
     */
    public static int factorial(final int number) {
        int factorial = 1;
        for (int i = 1; i <= number; i++) {
            factorial = factorial * i;
        }
        return factorial;
    }

    public static float inverseSquareRoot(final float number) {
        return 1.0f / (float) Math.sqrt(number);
    }

    // TRIGONOMETRIC FUNCTIONS

    /**
     * Calculates the sine of the given number via the Taylor Series
     */
    public static float sine(float number) {
        // Use the Taylor Series
        number = number - ((power(number, 3) / factorial(3)) - (power(number, 5) / factorial(5)));
        return number;
    }

    /**
     * Calculates the cosine of the given number via the Taylor Series
     */
    public static float cosine(float number) {
        // Use the Taylor Series
        number = 1 - ((power(number, 2) / factorial(2)) + (power(number, 4) / factorial(4)));
        return number;
    }

    // ANGLE CONVERSION

    /**
     * Converts degrees to radians
     */
    public static float asRadians(final float degrees) {
        return degrees * (Constants.PI / 180);
    }

    /**
     * Converts radians to degrees
     */
    public static float asDegrees(final float radians) {
        return radians * (180 / Constants.PI);
    }

    // RANDOM NUMBERS

    /**
     * Generates a random number
     */
    public static float generateRandom() {
        return System.nanoTime() / System.currentTimeMillis();
    }
}