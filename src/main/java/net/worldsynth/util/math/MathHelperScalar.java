/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.util.math;

public class MathHelperScalar {

    public static double clamp(double a, double min, double max) {
        return Math.min(Math.max(a, min), max);
    }

    public static float clamp(float a, float min, float max) {
        return Math.min(Math.max(a, min), max);
    }

    public static boolean isPositivePowerOfTwo(int number) {
        return number > 0 && ((number & (number - 1)) == 0);
    }
}
