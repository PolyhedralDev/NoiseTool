/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.heightmap;

import net.worldsynth.glpreview.QuadFace;
import net.worldsynth.glpreview.TriFace;
import net.worldsynth.glpreview.Vec3f;
import net.worldsynth.glpreview.model.AbstractTrifaceModel;


public class HeightmapModel extends AbstractTrifaceModel {

    public float[][] heightmap;
    public float[][][] colormap;

    private int facesCount = 0;

    private final int modelWidth;
    //	private int modelHeight;
    private final int modelLength;

    public HeightmapModel(float[][] heightmap, float[][][] colormap, double size) {
        this.heightmap = heightmap;
        this.colormap = colormap;

        modelWidth = heightmap.length;
        modelLength = heightmap[0].length;

        generateFaces(heightmap, colormap, size);
    }

    @Override
    public int getPrimitivesCount() {
        return facesCount;
    }

    private void generateFaces(float[][] heightmap, float[][][] colormap, double size) {
        int zx = heightmap.length;
        int zz = heightmap[0].length;

        facesCount = (zx - 1) * (zz - 1) + (zx - 1) * 2 + (zz - 1) * 2;
        facesCount *= 2;
        initVertexArray(facesCount);

        float heightRatio = (float) (Math.max(zz, zx) / size);

        int index = 0;
        QuadFace tempFace = new QuadFace();

        for(int x = 0; x < zx - 1; x++) {
            for(int z = 0; z < zz - 1; z++) {
                Vec3f v1 = new Vec3f(1, (heightmap[x + 1][z] - heightmap[x][z]), 0);
                Vec3f v2 = new Vec3f(0, (heightmap[x][z + 1] - heightmap[x][z]), 1);
                Vec3f v3 = new Vec3f();
                v3.cross(v2, v1);
                v3.normalize();

                float[] color;

                tempFace.setNormal(v3.x, v3.y, v3.z);

                color = colormap[x][z];
                tempFace.setVertex(0, x, heightmap[x][z] * heightRatio, z);
                tempFace.setColor(0, color[0], color[1], color[2]);

                color = colormap[x + 1][z];
                tempFace.setVertex(1, x + 1, heightmap[x + 1][z] * heightRatio, z);
                tempFace.setColor(1, color[0], color[1], color[2]);

                color = colormap[x + 1][z + 1];
                tempFace.setVertex(2, x + 1, heightmap[x + 1][z + 1] * heightRatio, z + 1);
                tempFace.setColor(2, color[0], color[1], color[2]);

                color = colormap[x][z + 1];
                tempFace.setVertex(3, x, heightmap[x][z + 1] * heightRatio, z + 1);
                tempFace.setColor(3, color[0], color[1], color[2]);

                tempFace.translate(-modelWidth / 2, 0, -modelLength / 2);

                TriFace[] f = tempFace.getTriFaces();
                insertVertexArray(f[0], index++);
                insertVertexArray(f[1], index++);
            }
        }

        //Generate sides along x-axis
        for(int z = 0; z < zz - 1; z++) {

            float[] color;

            tempFace.setNormal(-1.0f, 0.0f, 0.0f);

            color = colormap[0][z];
            tempFace.setVertex(0, 0.0f, 0.0f, z);
            tempFace.setColor(0, color[0], color[1], color[2]);

            color = colormap[0][z];
            tempFace.setVertex(1, 0.0f, heightmap[0][z] * heightRatio, z);
            tempFace.setColor(1, color[0], color[1], color[2]);

            color = colormap[0][z + 1];
            tempFace.setVertex(2, 0.0f, heightmap[0][z + 1] * heightRatio, z + 1);
            tempFace.setColor(2, color[0], color[1], color[2]);

            color = colormap[0][z + 1];
            tempFace.setVertex(3, 0.0f, 0.0f, z + 1);
            tempFace.setColor(3, color[0], color[1], color[2]);

            tempFace.translate(-modelWidth / 2, 0, -modelLength / 2);

            TriFace[] f = tempFace.getTriFaces();
            insertVertexArray(f[0], index++);
            insertVertexArray(f[1], index++);

            tempFace.setNormal(1.0f, 0.0f, 0.0f);

            color = colormap[zx - 1][z];
            tempFace.setVertex(0, zx - 1, 0.0f, z);
            tempFace.setColor(0, color[0], color[1], color[2]);

            color = colormap[zx - 1][z];
            tempFace.setVertex(1, zx - 1, heightmap[zx - 1][z] * heightRatio, z);
            tempFace.setColor(1, color[0], color[1], color[2]);

            color = colormap[zx - 1][z + 1];
            tempFace.setVertex(2, zx - 1, heightmap[zx - 1][z + 1] * heightRatio, z + 1);
            tempFace.setColor(2, color[0], color[1], color[2]);

            color = colormap[zx - 1][z + 1];
            tempFace.setVertex(3, zx - 1, 0.0f, z + 1);
            tempFace.setColor(3, color[0], color[1], color[2]);

            tempFace.translate(-modelWidth / 2, 0, -modelLength / 2);

            f = tempFace.getTriFaces();
            insertVertexArray(f[0], index++);
            insertVertexArray(f[1], index++);
        }

        //Generate sides along z-axis
        for(int x = 0; x < zx - 1; x++) {

            float[] color;

            tempFace.setNormal(0.0f, 0.0f, -1.0f);

            color = colormap[x][0];
            tempFace.setVertex(0, x, 0.0f, 0.0f);
            tempFace.setColor(0, color[0], color[1], color[2]);

            color = colormap[x][0];
            tempFace.setVertex(1, x, heightmap[x][0] * heightRatio, 0.0f);
            tempFace.setColor(1, color[0], color[1], color[2]);

            color = colormap[x + 1][0];
            tempFace.setVertex(2, x + 1, heightmap[x + 1][0] * heightRatio, 0.0f);
            tempFace.setColor(2, color[0], color[1], color[2]);

            color = colormap[x + 1][0];
            tempFace.setVertex(3, x + 1, 0.0f, 0.0f);
            tempFace.setColor(3, color[0], color[1], color[2]);

            tempFace.translate(-modelWidth / 2, 0, -modelLength / 2);

            TriFace[] f = tempFace.getTriFaces();
            insertVertexArray(f[0], index++);
            insertVertexArray(f[1], index++);

            tempFace.setNormal(0.0f, 0.0f, 1.0f);

            color = colormap[x][zz - 1];
            tempFace.setVertex(0, x, 0.0f, zz - 1);
            tempFace.setColor(0, color[0], color[1], color[2]);

            color = colormap[x][zz - 1];
            tempFace.setVertex(1, x, heightmap[x][zz - 1] * heightRatio, zz - 1);
            tempFace.setColor(1, color[0], color[1], color[2]);

            color = colormap[x + 1][zz - 1];
            tempFace.setVertex(2, x + 1, heightmap[x + 1][zz - 1] * heightRatio, zz - 1);
            tempFace.setColor(2, color[0], color[1], color[2]);

            color = colormap[x + 1][zz - 1];
            tempFace.setVertex(3, x + 1, 0.0f, zz - 1);
            tempFace.setColor(3, color[0], color[1], color[2]);

            tempFace.translate(-modelWidth / 2, 0, -modelLength / 2);

            f = tempFace.getTriFaces();
            insertVertexArray(f[0], index++);
            insertVertexArray(f[1], index++);
        }
    }
}
