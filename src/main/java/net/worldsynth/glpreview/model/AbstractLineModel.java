/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.model;

import net.worldsynth.glpreview.Line;
import net.worldsynth.glpreview.PrimitiveType;


public abstract class AbstractLineModel extends AbstractModel<Line> {

    @Override
    public final PrimitiveType getPrimitivesType() {
        return PrimitiveType.LINES;
    }

    @Override
    protected void initVertexArray(int primitivesCount) {
        vertexPositionArray = new float[primitivesCount * 8];
        vertexColorArray = new float[primitivesCount * 8];
    }

    @Override
    protected void insertVertexArray(Line primitive, int index) {
        insertVertexPositionArray(vertexPositionArray, primitive, index);
        insertVertexColorArray(vertexColorArray, primitive, index);
    }

    private void insertVertexPositionArray(float[] vertexArray, Line line, int index) {
        //Iterate vertices
        for(int j = 0; j < 2; j++) {
            //Iterate vector components
            vertexPositionArray[index * 8 + j * 4] = line.vertices[j * 3];
            vertexPositionArray[index * 8 + j * 4 + 1] = line.vertices[j * 3 + 1];
            vertexPositionArray[index * 8 + j * 4 + 2] = line.vertices[j * 3 + 2];
            vertexPositionArray[index * 8 + j * 4 + 3] = 1.0f;
        }
    }

    private void insertVertexColorArray(float[] vertexArray, Line line, int index) {
        //Iterate vertices
        for(int j = 0; j < 2; j++) {
            //Iterate color components RGBA
            vertexColorArray[index * 8 + j * 4] = line.color[j * 3];
            vertexColorArray[index * 8 + j * 4 + 1] = line.color[j * 3 + 1];
            vertexColorArray[index * 8 + j * 4 + 2] = line.color[j * 3 + 2];
            vertexColorArray[index * 8 + j * 4 + 3] = 1.0f;
        }
    }
}
