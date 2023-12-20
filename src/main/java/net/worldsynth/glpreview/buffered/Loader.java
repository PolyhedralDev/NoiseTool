/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.buffered;

import com.jogamp.opengl.GL2;
import net.worldsynth.glpreview.PrimitiveType;
import net.worldsynth.glpreview.model.AbstractModel;

import java.util.ArrayList;


public class Loader {

    private ArrayList<BufferedModel> loadedModels = new ArrayList<BufferedModel>();

    public BufferedModel loadModelToVAO(GL2 gl, AbstractModel<?> model, Program pointShaderProgram, Program lineShaderProgram,
                                        Program surfaceShaderProgram) {
        if(model.getPrimitivesType() == PrimitiveType.POINTS) {
            return loadPointModelToVAO(gl, model, pointShaderProgram);
        } else if(model.getPrimitivesType() == PrimitiveType.LINES) {
            return loadLineModelToVAO(gl, model, lineShaderProgram);
        } else if(model.getPrimitivesType() == PrimitiveType.TRIFACES) {
            return loadTrifaceModelToVAO(gl, model, surfaceShaderProgram);
        } else if(model.getPrimitivesType() == PrimitiveType.QUADFACES) {
            return loadQuadfaceModelToVAO(gl, model, surfaceShaderProgram);
        } else {
            return null;
        }
    }

    private BufferedModel loadPointModelToVAO(GL2 gl, AbstractModel<?> model, Program pointShaderProgram) {
        //Get the attribute locations for vertex positions and colors
        int vertexLoc = pointShaderProgram.glGetAttribLocation(gl, "in_position");
        int colorLoc = pointShaderProgram.glGetAttribLocation(gl, "in_color");

        float[] vertexPositions = model.getVertexPositionArray();
        float[] vertexColors = model.getVertexColorArray();

        VAO vao = new VAO(gl);
        vao.storeDataInAttributeList(gl, vertexLoc, 4, vertexPositions);
        vao.storeDataInAttributeList(gl, colorLoc, 4, vertexColors);
        BufferedModel newLoadedModel = new BufferedModel(vao, GL2.GL_POINTS, pointShaderProgram, vertexPositions.length / 4);
        loadedModels.add(newLoadedModel);
        return newLoadedModel;
    }

    private BufferedModel loadLineModelToVAO(GL2 gl, AbstractModel<?> model, Program lineShaderProgram) {
        //Get the attribute locations for vertex positions, colors and normals
        int vertexLoc = lineShaderProgram.glGetAttribLocation(gl, "in_position");
        int colorLoc = lineShaderProgram.glGetAttribLocation(gl, "in_color");

        float[] vertexPositions = model.getVertexPositionArray();
        float[] vertexColors = model.getVertexColorArray();

        VAO vao = new VAO(gl);
        vao.storeDataInAttributeList(gl, vertexLoc, 4, vertexPositions);
        vao.storeDataInAttributeList(gl, colorLoc, 4, vertexColors);
        BufferedModel newLoadedModel = new BufferedModel(vao, GL2.GL_LINES, lineShaderProgram, vertexPositions.length / 4);
        loadedModels.add(newLoadedModel);
        return newLoadedModel;
    }

    private BufferedModel loadTrifaceModelToVAO(GL2 gl, AbstractModel<?> model, Program surfaceShaderProgram) {
        //Get the attribute locations for vertex positions, colors and normals
        int vertexLoc = surfaceShaderProgram.glGetAttribLocation(gl, "in_position");
        int colorLoc = surfaceShaderProgram.glGetAttribLocation(gl, "in_color");
        int normalLoc = surfaceShaderProgram.glGetAttribLocation(gl, "in_normal");

        float[] vertexPositions = model.getVertexPositionArray();
        float[] vertexColors = model.getVertexColorArray();
        float[] vertexNormals = model.getVertexNormalArray();

        VAO vao = new VAO(gl);
        vao.storeDataInAttributeList(gl, vertexLoc, 4, vertexPositions);
        vao.storeDataInAttributeList(gl, colorLoc, 4, vertexColors);
        vao.storeDataInAttributeList(gl, normalLoc, 4, vertexNormals);
        BufferedModel newLoadedModel = new BufferedModel(vao, GL2.GL_TRIANGLES, surfaceShaderProgram, vertexPositions.length / 4);
        loadedModels.add(newLoadedModel);
        return newLoadedModel;
    }

    private BufferedModel loadQuadfaceModelToVAO(GL2 gl, AbstractModel<?> model, Program surfaceShaderProgram) {
        //Get the attribute locations for vertex positions, colors and normals
        int vertexLoc = surfaceShaderProgram.glGetAttribLocation(gl, "in_position");
        int colorLoc = surfaceShaderProgram.glGetAttribLocation(gl, "in_color");
        int normalLoc = surfaceShaderProgram.glGetAttribLocation(gl, "in_normal");

        float[] vertexPositions = model.getVertexPositionArray();
        float[] vertexColors = model.getVertexColorArray();
        float[] vertexNormals = model.getVertexNormalArray();

        VAO vao = new VAO(gl);
        vao.storeDataInAttributeList(gl, vertexLoc, 4, vertexPositions);
        vao.storeDataInAttributeList(gl, colorLoc, 4, vertexColors);
        vao.storeDataInAttributeList(gl, normalLoc, 4, vertexNormals);
        BufferedModel newLoadedModel = new BufferedModel(vao, GL2.GL_QUADS, surfaceShaderProgram, vertexPositions.length / 4);
        loadedModels.add(newLoadedModel);
        return newLoadedModel;
    }

    public void cleanUpAll(GL2 gl) {
        //Remove all loaded models from buffer
        for(BufferedModel lModel : loadedModels) {
            lModel.decommission(gl);
        }
        //Cleanup registry of loaded models
        loadedModels = new ArrayList<BufferedModel>();
    }

    public void cleanUpModels(GL2 gl, ArrayList<BufferedModel> models) {
        //Remove all loaded models from buffer
        for(BufferedModel model : models) {
            model.decommission(gl);
        }
        //Cleanup registry removing from loader register
        for(BufferedModel model : models) {
            loadedModels.remove(model);
        }
    }
}
