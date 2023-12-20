/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview;

public class TriFace extends Primitive {
    public float[] vertices = new float[9];
    public float[] normal = new float[3];
    public float[] color = new float[9];

    public void setVertex(int i, float x, float y, float z) {
        vertices[i * 3] = x;
        vertices[i * 3 + 1] = y;
        vertices[i * 3 + 2] = z;
    }

    public void setColor(int i, float r, float g, float b) {
        color[i * 3] = r;
        color[i * 3 + 1] = g;
        color[i * 3 + 2] = b;
    }

    public void setColor(float r, float g, float b) {
        for(int i = 0; i < 3; i++) {
            setColor(i, r, g, b);
        }
    }

    public void setNormal(float x, float y, float z) {
        normal[0] = x;
        normal[1] = y;
        normal[2] = z;
    }
}
