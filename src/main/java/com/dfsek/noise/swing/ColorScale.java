package com.dfsek.noise.swing;

public class ColorScale {
    private final String name;
    private final float[][] scale;


    public static final ColorScale SOLID = new ColorScale("Solid", new float[][]{
        { 0.0f, 0.5f, 0.5f, 0.5f },
        { 1.0f, 0.5f, 0.5f, 0.5f }
    });

    public static final ColorScale GRAYSCALE_0_1 = new ColorScale("Grayscale 0 - 1", new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 1.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale GRAYSCALE_0_256 = new ColorScale("Grayscale 0 - 256", new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 100.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale GRAYSCALE_N64_320 = new ColorScale("Grayscale (-64) - 320", new float[][]{
        { 0.0f, 0.0f, 0.0f, 0.0f },
        { 320.0f, 1.0f, 1.0f, 1.0f }
    });

    public static final ColorScale COLORED_0_320 = new ColorScale("Colored 0 - 320", new float[][]{
        { 0.0f, 0.0f, 0.0f, 1.0f },
        { 64.0f, 1.0f, 1.0f, 0.0f },
        { 128.0f, 0.0f, 1.0f, 0.0f },
        { 192.0f, 0.75f, 0.56f, 0.2f },
        { 256.0f, 0.5f, 0.5f, 0.5f },
        { 320.0f, 1.0f, 1.0f, 1.0f }
    });

    public ColorScale(String name, float[][] scale) {
        this.name = name;
        this.scale = scale;
    }

    public ColorScale(String name, String scaleAsText) {
        this.name = name;

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

    @Override
    public String toString() {
        return getName();
    }
}
