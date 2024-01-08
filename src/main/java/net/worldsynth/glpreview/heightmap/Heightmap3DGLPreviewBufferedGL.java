/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.heightmap;

import com.dfsek.noise.swing.ColorScale;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.worldsynth.glpreview.buffered.BufferedGLPanel;


public class Heightmap3DGLPreviewBufferedGL extends BufferedGLPanel {
    private static final long serialVersionUID = -1895542307473079689L;

    /**
     * Color scale.
     */
    private ColorScale colorscale = ColorScale.GRAYSCALE_0_1;

    private HeightmapModel heightmapModel;

    public Heightmap3DGLPreviewBufferedGL() {
        GLProfile glprofile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        setRequestedGLCapabilities(glcapabilities);
    }

    public void clearHeightmap() {
        startNewModel();
        endNewModel();
        display();
    }

    public void setHeightmap(float[][] heightmap) {
        //Create colormap from heightmap according to the colorscale
        float[][][] colormap = colormapFromHeightmap(heightmap);

        heightmapModel = new HeightmapModel(heightmap, colormap, Math.max(heightmap.length, heightmap[0].length));

        minYLookatHeight = -64;
        maxYLookatHeight = 320;

        startNewModel();
        loadModel(heightmapModel);
        endNewModel();
        display();
    }

    public void setHeightmap(double[][] heightmap) {
        setHeightmap(cast2f(heightmap));
    }

    private float[][] cast2f(double[][] array2d) {
        float[][] array2f = new float[array2d.length][array2d[0].length];
        for(int i = 0, width = array2d.length; i < width; i++) {
            for(int j = 0, length = array2d[0].length; j < length; j++) {
                array2f[i][j] = (float) array2d[i][j];
            }
        }
        return array2f;
    }

    public void setColorScale(ColorScale colorscale) {
        this.colorscale = colorscale;
    }

    public void setLookAtHeightSpan(float minLookAtHeight, float maxLookAtHeight) {
        minYLookatHeight = minLookAtHeight;
        maxYLookatHeight = maxLookAtHeight;
    }

    private float[][][] colormapFromHeightmap(float[][] heightmap) {
        int width = heightmap.length;
        int length = heightmap[0].length;
        float[][][] coloromap = new float[width][length][3];

        float max = Float.NEGATIVE_INFINITY;
        float min = Float.POSITIVE_INFINITY;
        for(int x = 0; x < width; x++) {
            for(int z = 0; z < length; z++) {
                max = Math.max(heightmap[x][z], max);
                min = Math.min(heightmap[x][z], min);
            }
        }

        for(int x = 0; x < width; x++) {
            for(int z = 0; z < length; z++) {
                coloromap[x][z] = colorscale.valueToFRgb(heightmap[x][z], min, max);
            }
        }

        return coloromap;
    }
}