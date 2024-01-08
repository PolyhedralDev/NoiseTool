/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.voxel;

import net.worldsynth.glpreview.QuadFace;
import net.worldsynth.glpreview.model.AbstractQuadfaceModel;


public class VoxelModel extends AbstractQuadfaceModel {

    public boolean[][][] blockspace;

    private int facesCount = 0;

    private final int modelWidth;
    private int modelHeight;
    private int modelLength;

    private float offsetX = 0;
    private float offsetY = 0;
    private float offsetZ = 0;

    public VoxelModel(boolean[][][] blockspace) {
        this(blockspace, 0, 0, 0);
    }

    public VoxelModel(boolean[][][] blockspace, float offsetX, float offsetY, float offsetZ) {
        this.blockspace = blockspace;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;

        modelWidth = blockspace.length;
        if(modelWidth == 0) {
            return;
        }
        modelHeight = blockspace[0].length;
        if(modelHeight == 0) {
            return;
        }
        modelLength = blockspace[0][0].length;
        if(modelLength == 0) {
            return;
        }

        generateFaces(blockspace);
    }

    @Override
    public int getPrimitivesCount() {
        return facesCount;
    }

    private int faceCount(boolean[][][] blockspace) {
        int zx = blockspace.length;
        int zy = blockspace[0].length;
        int zz = blockspace[0][0].length;

        int facesCount = 0;
        for(int x = 0; x < zx; x++) {
            for(int y = 0; y < zy; y++) {
                for(int z = 0; z < zz; z++) {
                    if(!blockspace[x][y][z]) continue;
                    //Face EAST
                    if(isNeighbourAir(blockspace, x, y, z, Direction.EAST)) {
                        facesCount++;
                    }

                    //Face WEST
                    if(isNeighbourAir(blockspace, x, y, z, Direction.WEST)) {
                        facesCount++;
                    }

                    //Face NORTH
                    if(isNeighbourAir(blockspace, x, y, z, Direction.NORTH)) {
                        facesCount++;
                    }

                    //Face SOUTH
                    if(isNeighbourAir(blockspace, x, y, z, Direction.SOUTH)) {
                        facesCount++;
                    }

                    //Face UP
                    if(isNeighbourAir(blockspace, x, y, z, Direction.UP)) {
                        facesCount++;
                    }

                    //Face DOWN
                    if(isNeighbourAir(blockspace, x, y, z, Direction.DOWN)) {
                        facesCount++;
                    }
                }
            }
        }

        return facesCount;
    }

    /**
     * Generate voxel mesh with culling
     */
    private void generateFaces(boolean[][][] blockspace) {
        int zx = blockspace.length;
        int zy = blockspace[0].length;
        int zz = blockspace[0][0].length;

        facesCount = faceCount(blockspace);
        initVertexArray(facesCount);

        int index = 0;
        QuadFace tempFace = new QuadFace();

        for(int x = 0; x < zx; x++) {
            for(int y = 0; y < zy; y++) {
                for(int z = 0; z < zz; z++) {
                    if(!blockspace[x][y][z]) {
                        continue;
                    }

                    float[] color = { 0.5f, 0.5f, 0.5f };

                    //Face EAST
                    if(isNeighbourAir(blockspace, x, y, z, Direction.EAST)) {
                        tempFace.setColor(color[0], color[1], color[2]);
                        tempFace.setNormal(1.0f, 0.0f, 0.0f);

                        tempFace.setVertex(0, x + 1, y + 1, z + 1);
                        tempFace.setVertex(1, x + 1, y, z + 1);
                        tempFace.setVertex(2, x + 1, y, z);
                        tempFace.setVertex(3, x + 1, y + 1, z);

                        tempFace.translate(-modelWidth / 2 - offsetX, -modelHeight / 2 - offsetY, -modelLength / 2 - offsetZ);

                        insertVertexArray(tempFace, index++);
                    }

                    //Face WEST
                    if(isNeighbourAir(blockspace, x, y, z, Direction.WEST)) {
                        tempFace.setColor(color[0], color[1], color[2]);
                        tempFace.setNormal(-1.0f, 0.0f, 0.0f);

                        tempFace.setVertex(0, x, y, z + 1);
                        tempFace.setVertex(1, x, y + 1, z + 1);
                        tempFace.setVertex(2, x, y + 1, z);
                        tempFace.setVertex(3, x, y, z);

                        tempFace.translate(-modelWidth / 2 - offsetX, -modelHeight / 2 - offsetY, -modelLength / 2 - offsetZ);

                        insertVertexArray(tempFace, index++);
                    }

                    //Face NORTH
                    if(isNeighbourAir(blockspace, x, y, z, Direction.NORTH)) {
                        tempFace.setColor(color[0], color[1], color[2]);
                        tempFace.setNormal(0.0f, 0.0f, -1.0f);

                        tempFace.setVertex(0, x, y + 1, z);
                        tempFace.setVertex(1, x + 1, y + 1, z);
                        tempFace.setVertex(2, x + 1, y, z);
                        tempFace.setVertex(3, x, y, z);

                        tempFace.translate(-modelWidth / 2 - offsetX, -modelHeight / 2 - offsetY, -modelLength / 2 - offsetZ);

                        insertVertexArray(tempFace, index++);
                    }

                    //Face SOUTH
                    if(isNeighbourAir(blockspace, x, y, z, Direction.SOUTH)) {
                        tempFace.setColor(color[0], color[1], color[2]);
                        tempFace.setNormal(0.0f, 0.0f, 1.0f);

                        tempFace.setVertex(0, x, y, z + 1);
                        tempFace.setVertex(1, x + 1, y, z + 1);
                        tempFace.setVertex(2, x + 1, y + 1, z + 1);
                        tempFace.setVertex(3, x, y + 1, z + 1);

                        tempFace.translate(-modelWidth / 2 - offsetX, -modelHeight / 2 - offsetY, -modelLength / 2 - offsetZ);

                        insertVertexArray(tempFace, index++);
                    }

                    //Face UP
                    if(isNeighbourAir(blockspace, x, y, z, Direction.UP)) {
                        tempFace.setColor(color[0], color[1], color[2]);
                        tempFace.setNormal(0.0f, 1.0f, 0.0f);

                        tempFace.setVertex(0, x, y + 1, z + 1);
                        tempFace.setVertex(1, x + 1, y + 1, z + 1);
                        tempFace.setVertex(2, x + 1, y + 1, z);
                        tempFace.setVertex(3, x, y + 1, z);

                        tempFace.translate(-modelWidth / 2 - offsetX, -modelHeight / 2 - offsetY, -modelLength / 2 - offsetZ);

                        insertVertexArray(tempFace, index++);
                    }

                    //Face DOWN
                    if(isNeighbourAir(blockspace, x, y, z, Direction.DOWN)) {
                        tempFace.setColor(color[0], color[1], color[2]);
                        tempFace.setNormal(0.0f, -1.0f, 0.0f);

                        tempFace.setVertex(0, x, y, z);
                        tempFace.setVertex(1, x + 1, y, z);
                        tempFace.setVertex(2, x + 1, y, z + 1);
                        tempFace.setVertex(3, x, y, z + 1);

                        tempFace.translate(-modelWidth / 2 - offsetX, -modelHeight / 2 - offsetY, -modelLength / 2 - offsetZ);

                        insertVertexArray(tempFace, index++);
                    }
                }
            }
        }
    }

    private boolean isNeighbourAir(boolean[][][] blockspace, int x, int y, int z, Direction dir) {
        int xSize = blockspace.length;
        int ySize = blockspace[0].length;
        int zSize = blockspace[0][0].length;

        x += dir.getxOffset();
        y += dir.getyOffset();
        z += dir.getzOffset();

        if(0 > x || x >= xSize) return true;
        if(0 > y || y >= ySize) return true;
        if(0 > z || z >= zSize) return true;

        return !blockspace[x][y][z];
    }

    private enum Direction {
        EAST(1, 0, 0),
        WEST(-1, 0, 0),
        NORTH(0, 0, -1),
        SOUTH(0, 0, 1),
        UP(0, 1, 0),
        DOWN(0, -1, 0);

        private final int xOffset, yOffset, zOffset;

        Direction(int xOffset, int yOffset, int zOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
        }

        public int getxOffset() {
            return xOffset;
        }

        public int getyOffset() {
            return yOffset;
        }

        public int getzOffset() {
            return zOffset;
        }
    }
}
