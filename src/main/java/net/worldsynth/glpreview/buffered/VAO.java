/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.buffered;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;

import java.nio.FloatBuffer;
import java.util.ArrayList;


final class VAO {

    private int vaoId;
    private ArrayList<Integer> vbos = new ArrayList<Integer>();
    private final ArrayList<Integer> attributes = new ArrayList<Integer>();

    VAO(GL2 gl) {
        int[] idArray = new int[1];
        gl.glGenVertexArrays(1, idArray, 0);
        vaoId = idArray[0];
    }

    public int getVaoId() {
        return vaoId;
    }

    public void bind(GL2 gl) {
        gl.glBindVertexArray(vaoId);
    }

    public void unbind(GL2 gl) {
        gl.glBindVertexArray(0);
    }

    public void storeDataInAttributeList(GL2 gl, int attributeNumber, int size, float[] data) {
        if(vaoId == 0) return;
        //Bind this vao
        bind(gl);
        //Create a new vertex buffer object
        int vboId = createBuffer(gl);
        //Bind the new vertex buffer object
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId);
        //Buffer the vertex array
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, (long) data.length * (long) (Float.SIZE / 8), createBufferFromData(data), GL2.GL_STATIC_DRAW);
        //Setup attribute pointer and store attribute for later use
        gl.glVertexAttribPointer(attributeNumber, size, GL2.GL_FLOAT, false, 0, 0);
        attributes.add(attributeNumber);
        //Unbind the vertex buffer object
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        //Unbind this vao
        unbind(gl);
    }

    public void enableVertexAttribArrays(GL2 gl) {
        for(int attribute : attributes) {
            gl.glEnableVertexAttribArray(attribute);
        }
    }

    public void disableVertexAttribArrays(GL2 gl) {
        for(int attribute : attributes) {
            gl.glDisableVertexAttribArray(attribute);
        }
    }

    public void decommission(GL2 gl) {
        //Delete all vbos associated with this vao
        for(int vbo : vbos) {
            int[] idArray = { vbo };
            gl.glDeleteBuffers(1, idArray, 0);
        }
        vbos = new ArrayList<Integer>();

        //Delete this vao
        int[] idArray = { vaoId };
        gl.glDeleteVertexArrays(1, idArray, 0);
        vaoId = 0;
    }

    private int createBuffer(GL2 gl) {
        int[] idArray = new int[1];
        gl.glGenBuffers(1, idArray, 0);
        vbos.add(idArray[0]);
        return idArray[0];
    }

    private FloatBuffer createBufferFromData(float[] data) {
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(data);
        return buffer;
    }
}
