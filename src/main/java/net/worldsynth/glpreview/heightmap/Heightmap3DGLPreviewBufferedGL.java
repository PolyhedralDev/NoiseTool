/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.heightmap;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import net.worldsynth.glpreview.buffered.BufferedGLPanel;
import net.worldsynth.util.Arrays;
import net.worldsynth.util.math.MathHelperScalar;


public class Heightmap3DGLPreviewBufferedGL extends BufferedGLPanel {
    private static final long serialVersionUID = -1895542307473079689L;

    private float[][] colorscale = {
        { 0, 0, 0, 1 },
        { 0.2f, 1.0f, 1.0f, 0.0f },
        { 0.5f, 0.0f, 1.0f, 0.0f },
        { 0.7f, 0.75f, 0.56f, 0.2f },
        { 0.8f, 0.5f, 0.5f, 0.5f },
        { 1, 1, 1, 1 }
    };

    private HeightmapModel heightmapModel;

    public Heightmap3DGLPreviewBufferedGL() {
        GLProfile glprofile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        setRequestedGLCapabilities(glcapabilities);
    }

    public void setHeightmap(float[][] heightmap, double size, float normalizedHeight) {
        //Create colormap from heightmap according to the colorscale
        float[][][] colormap = colormapFromHeightmap(heightmap, normalizedHeight);

        heightmapModel = new HeightmapModel(heightmap, colormap, size);

        minYLookatHeight = -normalizedHeight;
        maxYLookatHeight = normalizedHeight;

        startNewModel();
        loadModel(heightmapModel);
        endNewModel();
        display();
    }

    public void setHeightmap(double[][] heightmap, double size, float normalizedHeight) {
        setHeightmap(Arrays.cast2f(heightmap), size, normalizedHeight);
    }

    public void setColorscale(float[][] colorscale) {
        this.colorscale = colorscale;
    }

    private float[][][] colormapFromHeightmap(float[][] heightmap, float normalizedHeight) {
        int width = heightmap.length;
        int length = heightmap[0].length;
        float[][][] coloromap = new float[width][length][3];

        for(int x = 0; x < width; x++) {
            for(int z = 0; z < length; z++) {
                coloromap[x][z] = heightToColor(heightmap[x][z], normalizedHeight);
            }
        }

        return coloromap;
    }

    private float[] heightToColor(float height, float normalizedHeight) {
        height = MathHelperScalar.clamp(height, 0.0f, normalizedHeight) / normalizedHeight;

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