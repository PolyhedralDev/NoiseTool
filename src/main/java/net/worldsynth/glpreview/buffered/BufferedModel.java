/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.buffered;

import com.jogamp.opengl.GL2;


public class BufferedModel {
    private final Program shaderProgram;
    private final int GLDrawMode;
    private VAO vao;
    private int vertexCount;

    public BufferedModel(VAO vao, int GLDrawMode, Program shaderProgram, int vertexCount) {
        this.shaderProgram = shaderProgram;
        this.GLDrawMode = GLDrawMode;
        this.vao = vao;
        this.vertexCount = vertexCount;
    }

    public int getVaoId() {
        return vao.getVaoId();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Program getShaderProgram() {
        return shaderProgram;
    }

    public void drawModel(GL2 gl) {
        if(vao == null) return;

        gl.glBindVertexArray(getVaoId());
        vao.enableVertexAttribArrays(gl);
        gl.glDrawArrays(GLDrawMode, 0, getVertexCount());
        vao.disableVertexAttribArrays(gl);
        gl.glBindVertexArray(0);
    }

    public void decommission(GL2 gl) {
        ////Remove the associated VAO from buffer and remove the stored information and reference to it
        vao.decommission(gl);
        vao = null;
        vertexCount = 0;
    }
}
