package com.alientome.core.util;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class MathUtilsTest {

    // METHOD : MathUtils.decrease(double, double)

    @RunWith(Parameterized.class)
    public static class MethodDecreaseValues {

        @Parameter(0)
        public double toDecrease;
        @Parameter(1)
        public double decreaseBy;
        @Parameter(2)
        public double result;

        @Parameters(name = "{index}: decrease({0}, {1})={2}")
        public static Collection<Object[]> data() {
            Object[][] data = {
                    {5.2, 2.1, 3.1}, // Test positive number decreased positively :
                                        // 5.2 - 2.1 = 3.1
                    {-5.3, 2.3, -3.0}, // Test negative number decreased positively :
                                        // -5.3 + 2.3 = -3.0
                    {6.9, -1.7, 8.6}, // Test positive number 'decreased' negatively :
                                        // 6.9 - (-1.7) = 8.6
                    {-7.4, -1.5, -8.9}, // Test negative number 'decreased' negatively :
                                        // -7.4 + (-1.5) = -8.9
                    {0.5, 5, 0}, // Test positive number close enough to zero :
                                        // |0.5| < 5 ==> result = 0
                    {-0.7, 3, 0}, // Test negative number close enough to zero :
                                        // |-0.7| < 3 ==> result = 0
                    {42.3, 42.3, 0}, // Test positive number equal to what it's decreased by :
                                        // |42.3| = 42.3 ==> result = 0
                    {-37.1, 37.1, 0} // Test negative number opposite to what it's decreased by :
                                        // |-37.1| = 37.1 ==> result = 0
            };
            return Arrays.asList(data);
        }

        @Test
        public void testDecrease() {
            assertEquals(result, MathUtils.decrease(toDecrease, decreaseBy), 0.1);
            // Delta is 0.1 because it is the precision of the passed data.
        }
    }

    // METHOD : MathUtils.decrease(Vec2, double)
    // NOTE : Depends on MathUtils.decrease(double, double). Therefore, only test new behaviour.

    public static class MethodDecreaseVec2 {

        @Test(expected = NullPointerException.class)
        public void testVecNullThrows() {
            MathUtils.decrease(null, 0);
        }

        @Test
        public void testDecreaseVec2() {
            double vX = 5.7; // Keep both positive and greater than or equal to decreaseBy !
            double vY = 4.2; // (We are not testing the decreasing function, but only assignment)
            double decreaseBy = 3.1; // Keep this positive

            double expectedVX = vX - decreaseBy; // Avoid using MathUtils.decrease(double, double)
            double expectedVY = vY - decreaseBy;

            Vec2 vec = new Vec2(vX, vY);
            MathUtils.decrease(vec, decreaseBy);

            assertEquals(expectedVX, vec.x, 0.1);
            assertEquals(expectedVY, vec.y, 0.1);
        }
    }

    // METHOD : MathUtils.clamp(double, double, double)

    @RunWith(Parameterized.class)
    public static class MethodClampDoubleValues {

        @Parameter(0)
        public double value;
        @Parameter(1)
        public double minVal;
        @Parameter(2)
        public double maxVal;
        @Parameter(3)
        public double result;

        @Parameters(name = "{index}: clamp({0}, {1}, {2})={3}")
        public static Collection<Object[]> data() {
            Object[][] data = {
                    {-45.0, 2.1, 7.4, 2.1}, // Test a number that's less than the lower bound :
                                        // -45.0 < 2.1 ==> result = 2.1
                    {204.2, 3.7, 7.9, 7.9}, // Test a number that's greater than the upper bound :
                                        // 204.2 > 7.9 ==> result = 7.9
                    {5.3, 1.4, 9.5, 5.3}, // Test a number that's in the range :
                                        // 1.4 < 5.3 < 9.5 ==> result = 5.3
                    {1.7, 1.7, 23.0, 1.7}, // Test a number equal to the lower bound :
                                        // 1.7 = 1.7 ==> result = 1.7
                    {4.2, 1.3, 4.2, 4.2}, // Test a number equal to the upper bound :
                                        // 4.2 = 4.2 ==> result = 4.2
                                                // Test when lower bound and upper bound are equal :
                    {0.5, 1.7, 1.7, 1.7}, // A number that's less than the lower bound
                    {2.3, 1.2, 1.2, 1.2}, // A number that's greater than the upper bound
                    {13.7, 13.7, 13.7, 13.7} // A number equal to both bounds
            };
            return Arrays.asList(data);
        }

        @Test
        public void testClampDouble() {
            assertEquals(result, MathUtils.clamp(value, minVal, maxVal), 0.1);
        }
    }

    public static class MethodClampDoubleMisc {

        @Test(expected = IllegalArgumentException.class)
        public void testWrongBoundsThrows() {
            MathUtils.clamp(0.0, 5.3, 3.7);
        }
    }

    // METHOD : MathUtils.clamp(int, int, int)

    @RunWith(Parameterized.class)
    public static class MethodClampIntValues {

        @Parameter(0)
        public int value;
        @Parameter(1)
        public int minVal;
        @Parameter(2)
        public int maxVal;
        @Parameter(3)
        public int result;

        @Parameters(name = "{index}: clamp({0}, {1}, {2})={3}")
        public static Collection<Object[]> data() {
            Object[][] data = { // See MethodClampDoubleValues.data() for precisions on test cases
                    {-45, 2, 7, 2},
                    {204, 3, 8, 8},
                    {5, 1, 9, 5},
                    {2, 2, 23, 2},
                    {4, 1, 4, 4},
                    {0, 2, 2, 2},
                    {5, 3, 3, 3},
                    {12, 12, 12, 12}
            };
            return Arrays.asList(data);
        }

        @Test
        public void testClampInt() {
            assertEquals(result, MathUtils.clamp(value, minVal, maxVal));
        }
    }

    public static class MethodClampIntMisc {

        @Test(expected = IllegalArgumentException.class)
        public void testWrongBoundsThrows() {
            MathUtils.clamp(0, 5, 3);
        }
    }

    // METHOD : MathUtils.lerp(double, double, double)

    @RunWith(Parameterized.class)
    public static class MethodLERPValues {

        @Parameter(0)
        public double start;
        @Parameter(1)
        public double end;
        @Parameter(2)
        public double t;
        @Parameter(3)
        public double result;

        @Parameters(name = "{index}: lerp({0}, {1}, {2})={3}")
        public static Collection<Object[]> data() {
            Object[][] data = {
                    {4.2, 8.7, 0.5, 6.45},
                    {3.1, 8.2, 0, 3.1},
                    {7.2, 10.5, 1, 10.5},
                    {13.7, 13.7, 0.314, 13.7}
            };
            return Arrays.asList(data);
        }

        @Test
        public void testLERP() {
            assertEquals(result, MathUtils.lerp(start, end, t), 0.01);
        }
    }

    // METHOD : MathUtils.lerpVec2(Vec2, Vec2, double)
    // NOTE : Depends on MathUtils.decrease(double, double). Therefore, only test new behaviour.

    public static class MethodLERPVec2 {

        @Test(expected = NullPointerException.class)
        public void testStartNullThrows() {
            MathUtils.lerpVec2(null, new Vec2(), 0);
        }

        @Test(expected = NullPointerException.class)
        public void testEndNullThrows() {
            MathUtils.lerpVec2(new Vec2(), null, 0);
        }

        @Test(expected = NullPointerException.class)
        public void testBothNullThrows() {
            MathUtils.lerpVec2(null, null, 0);
        }

        private final double sX = 42.3, sY = 51.2;
        private final double eX = 67.4, eY = 89.0;
        private final double t = 0.141;

        @Test
        public void testNoModifyParameterVec2s() {

            Vec2 start = new Vec2(sX, sY);
            Vec2 end = new Vec2(eX, eY);

            Vec2 startCopy = new Vec2(start);
            Vec2 endCopy = new Vec2(end);

            MathUtils.lerpVec2(start, end, t);

            assertEquals(startCopy, start);
            assertEquals(endCopy, end);
        }

        @Test
        public void testLERPVec2() {

            Vec2 start = new Vec2(sX, sY);
            Vec2 end = new Vec2(eX, eY);

            double expectedX = sX + t * (eX - sX);
            double expectedY = sY + t * (eY - sY);

            Vec2 result = MathUtils.lerpVec2(start, end, t);

            assertEquals(expectedX, result.x, 0.01);
            assertEquals(expectedY, result.y, 0.01);
        }
    }

    // METHOD : MathUtils.diagonalDistance(Vec2, Vec2)

    @RunWith(Parameterized.class)
    public static class MethodDiagonalDistanceValues {

        @Parameter(0)
        public double pos0x;
        @Parameter(1)
        public double pos0y;
        @Parameter(2)
        public double pos1x;
        @Parameter(3)
        public double pos1y;
        @Parameter(4)
        public double result;

        @Parameters(name = "{index}: diagonalDistance([{0}, {1}], [{2}, {3}])={4}")
        public static Collection<Object[]> data() {
            Object[][] data = { // Maybe we should test the 27 possible cases (< or = or > for each relation)
                                // But for now let's do just four
                    {25.5, 196, 13, 52.5, 143.5}, // Test for dX < 0, dY < 0, |dX| < |dY|
                    {25.5, 52.5, 13, 196, 143.5}, // Test for dX < 0, dY > 0, |dX| < |dY|
                    {412.5, 42, 137, 12.5, 275.5}, // Test for dX < 0, dY < 0, |dX| > |dY|
                    {137, 42, 412.5, 12.5, 275.5}, // Test for dX > 0, dY < 0, |dX| > |dY|
            };
            return Arrays.asList(data);
        }

        @Test
        public void testDiagonalDistance() {
            assertEquals(result, MathUtils.diagonalDistance(new Vec2(pos0x, pos0y), new Vec2(pos1x, pos1y)), 0.1);
        }
    }

    public static class MethodDiagonalDistance {

        @Test(expected = NullPointerException.class)
        public void testPos0NullThrows() {
            MathUtils.diagonalDistance(null, new Vec2());
        }

        @Test(expected = NullPointerException.class)
        public void testPos1NullThrows() {
            MathUtils.diagonalDistance(new Vec2(), null);
        }

        @Test(expected = NullPointerException.class)
        public void testBothNullThrows() {
            MathUtils.diagonalDistance(null, null);
        }
    }

    // METHOD MathUtils.roundClosest(double, double)

    @RunWith(Parameterized.class)
    public static class MethodRoundClosestValues {

        @Parameter(0)
        public double toRound;
        @Parameter(1)
        public double step;
        @Parameter(2)
        public double result;

        @Parameters(name = "{index}: roundClosest({0}, {1})={2}")
        public static Collection<Object[]> data() {
            Object[][] data = {
                    {1.33, 0.25, 1.25}, // Test a number getting rounded down
                    {1.55, 0.33, 1.65}, // Test a number getting rounded up
                    {1.125, 0.25, 1.25}, // Test a number to equal distance from either; has to be rounded up.
                    {1.40, 0.70, 1.40} // Test a number already rounded
            };
            return Arrays.asList(data);
        }

        @Test
        public void testRoundClosest() {
            assertEquals(result, MathUtils.roundClosest(toRound, step), 0.01);
        }
    }

    public static class MethodRoundClosest {

        @Test(expected = IllegalArgumentException.class)
        public void testStep0Throws() {
            MathUtils.roundClosest(1.0, 0.0);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testStepNegativeThrows() {
            MathUtils.roundClosest(1.0, -0.25);
        }
    }
}
