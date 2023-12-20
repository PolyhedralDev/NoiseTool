/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.model;

import net.worldsynth.glpreview.Point;
import net.worldsynth.glpreview.PrimitiveType;


public abstract class AbstractPointModel extends AbstractModel<Point> {

    @Override
    public final PrimitiveType getPrimitivesType() {
        return PrimitiveType.POINTS;
    }

    @Override
    protected void initVertexArray(int primitivesCount) {
        vertexPositionArray = new float[primitivesCount * 4];
        vertexColorArray = new float[primitivesCount * 4];
    }

    @Override
    protected void insertVertexArray(Point primitive, int index) {
        insertVertexPositionArray(vertexPositionArray, primitive, index);
        insertVertexColorArray(vertexColorArray, primitive, index);
    }

    private void insertVertexPositionArray(float[] vertexArray, Point point, int index) {
        //Iterate vector components
        vertexPositionArray[index * 4] = point.vertices[0];
        vertexPositionArray[index * 4 + 1] = point.vertices[1];
        vertexPositionArray[index * 4 + 2] = point.vertices[2];
        vertexPositionArray[index * 4 + 3] = 1.0f;
    }

    private void insertVertexColorArray(float[] vertexArray, Point point, int index) {
        //Iterate color components RGBA
        vertexColorArray[index * 4] = point.color[0];
        vertexColorArray[index * 4 + 1] = point.color[1];
        vertexColorArray[index * 4 + 2] = point.color[2];
        vertexColorArray[index * 4 + 3] = 1.0f;
    }
}
