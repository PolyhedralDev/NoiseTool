/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview;

public class Commons {

    // ------------------
    // VECTOR STUFF
    //

    // res = a cross b;
    public static void crossProduct(float[] a, float[] b, float[] res) {
        res[0] = a[1] * b[2] - b[1] * a[2];
        res[1] = a[2] * b[0] - b[2] * a[0];
        res[2] = a[0] * b[1] - b[0] * a[1];
    }

    // Normalize a vec3
    public static void normalize(float[] a) {

        float mag = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);

        a[0] /= mag;
        a[1] /= mag;
        a[2] /= mag;
    }

    public static void addition(float[] a, float[] b, float[] res) {
        res[0] = a[0] + b[0];
        res[0] = a[1] + b[1];
        res[0] = a[2] + b[2];
    }

    public static void subtraction(float[] a, float[] b, float[] res) {
        res[0] = a[0] - b[0];
        res[0] = a[1] - b[1];
        res[0] = a[2] - b[2];
    }

    // ----------------
    // MATRIX STUFF
    //

    // sets the square matrix mat to the identity matrix,
    // size refers to the number of rows (or columns)
    public static void setIdentityMatrix(float[] mat, int size) {

        // fill matrix with 0s
        for(int i = 0; i < size * size; ++i)
            mat[i] = 0.0f;

        // fill diagonal with 1s
        for(int i = 0; i < size; ++i)
            mat[i + i * size] = 1.0f;
    }

    //
    // a = a * b;
    //
    public static void multMatrix(float[] a, float[] b) {

        float[] res = new float[16];

        for(int i = 0; i < 4; ++i) {
            for(int j = 0; j < 4; ++j) {
                res[j * 4 + i] = 0.0f;
                for(int k = 0; k < 4; ++k) {
                    res[j * 4 + i] += a[k * 4 + i] * b[j * 4 + k];
                }
            }
        }
        System.arraycopy(res, 0, a, 0, 16);
    }

    // Defines a transformation matrix mat with a translation
    public static void setTranslationMatrix(float[] mat, float x, float y, float z) {

        setIdentityMatrix(mat, 4);
        mat[12] = x;
        mat[13] = y;
        mat[14] = z;
    }

    // ------------------
    // Projection Matrix
    //

    public static float[] buildProjectionMatrix(float fov, float ratio, float nearP, float farP, float[] projMatrix) {

        float f = 1.0f / (float) Math.tan(fov * (Math.PI / 360.0));

        setIdentityMatrix(projMatrix, 4);

        projMatrix[0] = f / ratio;
        projMatrix[4 + 1] = f;
        projMatrix[2 * 4 + 2] = (farP + nearP) / (nearP - farP);
        projMatrix[3 * 4 + 2] = (2.0f * farP * nearP) / (nearP - farP);
        projMatrix[2 * 4 + 3] = -1.0f;
        projMatrix[3 * 4 + 3] = 0.0f;

        return projMatrix;
    }

    // ------------------
    // View Matrix
    //
    // note: it assumes the camera is not tilted,
    // i.e. a vertical up vector (remmeber gluLookAt?)
    //

    public static float[] setCamera(float posX, float posY, float posZ, float lookAtX, float lookAtY, float lookAtZ, float[] viewMatrix) {

        float[] dir = new float[3];
        float[] right = new float[3];
        float[] up = new float[3];

        up[0] = 0.0f;
        up[1] = 1.0f;
        up[2] = 0.0f;

        dir[0] = (lookAtX - posX);
        dir[1] = (lookAtY - posY);
        dir[2] = (lookAtZ - posZ);
        normalize(dir);

        crossProduct(dir, up, right);
        normalize(right);

        crossProduct(right, dir, up);
        normalize(up);

        float[] aux = new float[16];

        viewMatrix[0] = right[0];
        viewMatrix[4] = right[1];
        viewMatrix[8] = right[2];
        viewMatrix[12] = 0.0f;

        viewMatrix[1] = up[0];
        viewMatrix[5] = up[1];
        viewMatrix[9] = up[2];
        viewMatrix[13] = 0.0f;

        viewMatrix[2] = -dir[0];
        viewMatrix[6] = -dir[1];
        viewMatrix[10] = -dir[2];
        viewMatrix[14] = 0.0f;

        viewMatrix[3] = 0.0f;
        viewMatrix[7] = 0.0f;
        viewMatrix[11] = 0.0f;
        viewMatrix[15] = 1.0f;

        setTranslationMatrix(aux, -posX, -posY, -posZ);

        multMatrix(viewMatrix, aux);

        return viewMatrix;
    }

    public static float[] buildRotationMatrixX(float theta) {
        float[] mat = new float[16];

        setIdentityMatrix(mat, 4);
        mat[5] = (float) Math.cos(theta);
        mat[6] = (float) -Math.sin(theta);
        mat[9] = (float) Math.sin(theta);
        mat[10] = (float) Math.cos(theta);

        return mat;
    }

    public static float[] buildRotationMatrixY(float theta) {
        float[] mat = new float[16];

        setIdentityMatrix(mat, 4);
        mat[0] = (float) Math.cos(theta);
        mat[2] = (float) Math.sin(theta);
        mat[8] = (float) -Math.sin(theta);
        mat[10] = (float) Math.cos(theta);

        return mat;
    }

    public static float[] buildRotationMatrixZ(float theta) {
        float[] mat = new float[16];

        setIdentityMatrix(mat, 4);
        mat[0] = (float) Math.cos(theta);
        mat[1] = (float) -Math.sin(theta);
        mat[4] = (float) Math.sin(theta);
        mat[5] = (float) Math.cos(theta);

        return mat;
    }
}
