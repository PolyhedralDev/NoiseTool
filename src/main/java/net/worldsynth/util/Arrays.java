/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.util;

public class Arrays {

    public static float[][] fill(float[][] array2d, float d) {
        for(int i = 0, width = array2d.length; i < width; i++) {
            for(int j = 0, length = array2d[0].length; j < length; j++) {
                array2d[i][j] = d;
            }
        }
        return array2d;
    }

    public static double[][] fill(double[][] array2d, double d) {
        for(int i = 0, width = array2d.length; i < width; i++) {
            for(int j = 0, length = array2d[0].length; j < length; j++) {
                array2d[i][j] = d;
            }
        }
        return array2d;
    }

    public static double[][] cast2d(float[][] array2f) {
        double[][] array2d = new double[array2f.length][array2f[0].length];
        for(int i = 0, width = array2f.length; i < width; i++) {
            for(int j = 0, length = array2f[0].length; j < length; j++) {
                array2d[i][j] = array2f[i][j];
            }
        }
        return array2d;
    }

    public static float[][] cast2f(double[][] array2d) {
        float[][] array2f = new float[array2d.length][array2d[0].length];
        for(int i = 0, width = array2d.length; i < width; i++) {
            for(int j = 0, length = array2d[0].length; j < length; j++) {
                array2f[i][j] = (float) array2d[i][j];
            }
        }
        return array2f;
    }
}
