/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview;

public class Point extends Primitive {
    public float[] vertices = new float[3];
    public float[] color = new float[3];

    public void setVertex(float x, float y, float z) {
        vertices[0] = x;
        vertices[1] = y;
        vertices[2] = z;
    }

    public void setColor(float r, float g, float b) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
    }
}
