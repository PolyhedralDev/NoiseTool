/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview;

public class QuadFace extends Primitive {
    public float[] vertices = new float[12];
    public float[] normal = new float[3];
    public float[] color = new float[12];

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
        for(int i = 0; i < 4; i++) {
            setColor(i, r, g, b);
        }
    }

    public void setNormal(float x, float y, float z) {
        normal[0] = x;
        normal[1] = y;
        normal[2] = z;
    }

    public TriFace[] getTriFaces() {
        TriFace[] triangleFaces = new TriFace[2];

        float[] fColors = color;
        float[] fNormal = normal;
        float[] fVertex = vertices;

        TriFace f1 = new TriFace();
        f1.setColor(0, fColors[0], fColors[1], fColors[2]);
        f1.setColor(1, fColors[3], fColors[4], fColors[5]);
        f1.setColor(2, fColors[6], fColors[7], fColors[8]);
        f1.setNormal(fNormal[0], fNormal[1], fNormal[2]);
        f1.setVertex(0, fVertex[0], fVertex[1], fVertex[2]);
        f1.setVertex(1, fVertex[3], fVertex[4], fVertex[5]);
        f1.setVertex(2, fVertex[6], fVertex[7], fVertex[8]);

        TriFace f2 = new TriFace();
        f2.setColor(0, fColors[6], fColors[7], fColors[8]);
        f2.setColor(1, fColors[9], fColors[10], fColors[11]);
        f2.setColor(2, fColors[0], fColors[1], fColors[2]);
        f2.setNormal(fNormal[0], fNormal[1], fNormal[2]);
        f2.setVertex(0, fVertex[6], fVertex[7], fVertex[8]);
        f2.setVertex(1, fVertex[9], fVertex[10], fVertex[11]);
        f2.setVertex(2, fVertex[0], fVertex[1], fVertex[2]);

        triangleFaces[0] = f1;
        triangleFaces[1] = f2;

        return triangleFaces;
    }

    public void translate(float x, float y, float z) {
        for(int i = 0; i < 4; i++) {
            vertices[i * 3] += x;
            vertices[i * 3 + 1] += y;
            vertices[i * 3 + 2] += z;
        }
    }
}
