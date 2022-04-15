package Leclair.math;

/**
 * This class contains basic math functions
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

    public static float Power(float number, int exponent) {
        float result = 1;
        for (int i = 0; i < exponent; i++) {
            result = result * number;
        }
        return result;
    }

    public static int Factorial(int number) {
        int factorial = 1;
        for (int i = 1; i <= number; i++) {
            factorial = factorial * i;
        }
        return factorial;
    }

    // TRIGONOMETRIC FUNCTIONS

    public static float Sine(float number) {
        // Use the Taylor Series
        number = number - ((Power(number, 3) / Factorial(3)) - (Power(number, 5) / Factorial(5)));
        return number;
    }

    public static float Cosine(float number) {
        // Use the Taylor Series
        number = 1 - ((Power(number, 2) / Factorial(2)) + (Power(number, 4) / Factorial(4)));
        return number;
    }

    // ANGLE CONVERSION

    public static float AsRadians(float degrees) {
        return degrees * (PI / 180);
    }

    public static float AsDegrees(float radians) {
        return radians * (180 / PI);
    }

    // GENERATE A RANDOM NUMBER
    
    public static float GenerateRandom() {
        return System.nanoTime() / System.currentTimeMillis();
    }
}