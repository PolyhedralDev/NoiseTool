package com.dfsek.noise.swing;

public class ColorScale {
    private final String name;
    private final boolean normalized;
    private final float[][] scale;


    public static final ColorScale SOLID = new ColorScale("Solid", false, new float[][]{
        { 0.0f, 0.5f, 0.5f, 0.5f },
        { 1.0f, 0.5f, 0.5f, 0.5f }
    });

    public static final ColorScale GRAYSCALE_NORMALIZED = new ColorScale("Grayscale normalized", true, new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 1.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale GRAYSCALE_0_1 = new ColorScale("Grayscale 0 - 1", false, new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 1.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale GRAYSCALE_0_256 = new ColorScale("Grayscale 0 - 256", false, new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 100.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale GRAYSCALE_N64_320 = new ColorScale("Grayscale (-64) - 320", false, new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 320.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale COLORED_0_320 = new ColorScale("Colored 0 - 320", false, new float[][]{
        { 0.0f, 0.0f, 0.0f, 1.0f },
        { 64.0f, 1.0f, 1.0f, 0.0f },
        { 128.0f, 0.0f, 1.0f, 0.0f },
        { 192.0f, 0.75f, 0.56f, 0.2f },
        { 256.0f, 0.5f, 0.5f, 0.5f },
        { 320.0f, 1.0f, 1.0f, 1.0f }
    });

    public ColorScale(String name, boolean normalized, float[][] scale) {
        this.name = name;
        this.normalized = normalized;
        this.scale = scale;
    }

    public ColorScale(String name, boolean normalized, String scaleAsText) {
        this.name = name;
        this.normalized = normalized;

        String[] split = scaleAsText.split("\\n\\r*|,\\s*");
        scale = new float[split.length / 4][4];
        for(int i = 0; i < split.length / 4; i++) {
            scale[i][0] = Float.parseFloat(split[i * 4]);
            scale[i][1] = Float.parseFloat(split[i * 4 + 1]);
            scale[i][2] = Float.parseFloat(split[i * 4 + 2]);
            scale[i][3] = Float.parseFloat(split[i * 4 + 3]);
        }
    }

    public String getName() {
        return name;
    }

    public boolean getNormalized() {
        return normalized;
    }

    public float[][] getScale() {
        return scale;
    }

    public String getScaleAsText() {
        StringBuilder text = new StringBuilder();
        for(float[] stop : scale) {
            text.append(stop[0]).append(", ");
            text.append(stop[1]).append(", ");
            text.append(stop[2]).append(", ");
            text.append(stop[3]).append("\n\r");
        }
        text.setLength(text.length() - 2);

        return text.toString();
    }

    private static float normal(float in, float out, float min, float max) {
        double range = max - min;
        return (float) ((in - min) * out / range);
    }

    public float[] valueToFRgb(float value, float min, float max) {
        float colorScaleMin = scale[0][0];
        float colorScaleMax = scale[scale.length - 1][0];

        if(normalized) {
            value = normal(value, 1.0f, min, max);
        } else {
            value = Math.min(Math.max(value, colorScaleMin), colorScaleMax);
        }

        float[] lowRange = scale[0];
        float[] highRange = scale[1];
        for(int i = 1; i < scale.length; i++) {
            if(value <= scale[i][0] && value >= scale[i - 1][0]) {
                lowRange = scale[i - 1];
                highRange = scale[i];
            }
        }
        float[] color = new float[3];
        float a = (value - lowRange[0]) / (highRange[0] - lowRange[0]);
        for(int j = 0; j < 3; j++) {
            color[j] = lowRange[j + 1] * (1 - a) + highRange[j + 1] * a;
        }
        return color;
    }

    public int valueToIRgb(float value, float min, float max) {
        float[] fRgb = valueToFRgb(value, min, max);
        int r = (int) (fRgb[0] * 255);
        int g = (int) (fRgb[1] * 255);
        int b = (int) (fRgb[2] * 255);
        return -16777216 + (r << 16) + (g << 8) + b;
    }

    public int valueToIRgb(double value, double min, double max) {
        return valueToIRgb((float) value, (float) min, (float) max);
    }

    @Override
    public String toString() {
        return getName();
    }
}
