/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.heightmap;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.worldsynth.glpreview.buffered.BufferedGLPanel;


public class Heightmap3DGLPreviewBufferedGL extends BufferedGLPanel {
    private static final long serialVersionUID = -1895542307473079689L;

    /**
     * Color scale. Entries are {elevation, r, g, b}.
     */
    private float[][] colorscale = {
        { 0.0f, 0.0f, 0.0f, 1.0f },
        { 64.0f, 1.0f, 1.0f, 0.0f },
        { 128.0f, 0.0f, 1.0f, 0.0f },
        { 192.0f, 0.75f, 0.56f, 0.2f },
        { 256.0f, 0.5f, 0.5f, 0.5f },
        { 320.0f, 1.0f, 1.0f, 1.0f }
    };

    private HeightmapModel heightmapModel;

    public Heightmap3DGLPreviewBufferedGL() {
        GLProfile glprofile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        setRequestedGLCapabilities(glcapabilities);
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

    public void setColorscale(float[][] colorscale) {
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

        for(int x = 0; x < width; x++) {
            for(int z = 0; z < length; z++) {
                coloromap[x][z] = heightToColor(heightmap[x][z]);
            }
        }

        return coloromap;
    }

    private float[] heightToColor(float height) {
        float colorscaleMin = colorscale[0][0];
        float colorscaleMax = colorscale[colorscale.length-1][0];
        height = Math.min(Math.max(height, colorscaleMin), colorscaleMax);

        float[] lowRange = colorscale[0];
        float[] highRange = colorscale[1];
        for(int i = 1; i < colorscale.length; i++) {
            if(height <= colorscale[i][0] && height >= colorscale[i - 1][0]) {
                lowRange = colorscale[i - 1];
                highRange = colorscale[i];
            }
        }
        float[] colorspace = new float[3];
        float a = (height - lowRange[0]) / (highRange[0] - lowRange[0]);
        for(int j = 0; j < 3; j++) {
            colorspace[j] = lowRange[j + 1] * (1 - a) + highRange[j + 1] * a;
        }
        float[] color = { colorspace[0], colorspace[1], colorspace[2] };
        return color;
    }
}