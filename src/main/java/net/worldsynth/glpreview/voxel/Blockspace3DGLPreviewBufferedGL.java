/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.voxel;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.worldsynth.glpreview.buffered.BufferedGLPanel;


public class Blockspace3DGLPreviewBufferedGL extends BufferedGLPanel {
    private static final long serialVersionUID = -1895542307473079689L;

    private VoxelModel voxelModel;

    public Blockspace3DGLPreviewBufferedGL() {
        GLProfile glprofile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        setRequestedGLCapabilities(glcapabilities);
    }

    public void setBlockspace(boolean[][][] blockspace, float normalizedHeight) {
        voxelModel = new VoxelModel(blockspace);

        minYLookatHeight = -normalizedHeight;
        maxYLookatHeight = normalizedHeight;

        startNewModel();
        if(voxelModel.getPrimitivesCount() > 0) {
            loadModel(voxelModel);
        }
        endNewModel();
        display();
    }
}
