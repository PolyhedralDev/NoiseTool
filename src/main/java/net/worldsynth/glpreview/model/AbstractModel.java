/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.model;

import net.worldsynth.glpreview.Primitive;
import net.worldsynth.glpreview.PrimitiveType;


public abstract class AbstractModel<T extends Primitive> {

    protected float[] vertexPositionArray;
    protected float[] vertexColorArray;
    protected float[] vertexNormalArray;

    public abstract PrimitiveType getPrimitivesType();

    public abstract int getPrimitivesCount();

    protected abstract void initVertexArray(int primitivesCount);

    protected abstract void insertVertexArray(T primitive, int index);

    public float[] getVertexPositionArray() {
        return vertexPositionArray;
    }

    public float[] getVertexColorArray() {
        return vertexColorArray;
    }

    public float[] getVertexNormalArray() {
        return vertexNormalArray;
    }
}
