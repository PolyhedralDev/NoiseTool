package com.dfsek.noise.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NoiseDistributionPanel extends JPanel {
    private final JLabel image;

    public NoiseDistributionPanel() {
        this.image = new JLabel();
        add(image);
    }

    public void update(int[] buckets) {
        try {
            image.setIcon(new ImageIcon(getImage(buckets)));
        } catch (Exception e) {
            e.printStackTrace();
            image.setIcon(new TextIcon(this, "An error occurred. "));
        }
    }

    private BufferedImage getImage(int[] buckets) {
        int sizeX = getWidth();
        int sizeY = getHeight();
        Color text = image.getForeground();
        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();

        int highestBucket = Integer.MIN_VALUE;
        for (int i : buckets) highestBucket = Math.max(highestBucket, i);

        graphics.setColor(text);
        graphics.drawString("" + highestBucket, 0, -1 + 20);

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < ((double) buckets[x] / highestBucket) * ((double) sizeY); y++) {
                image.setRGB(x, sizeY - y - 1, text.getRGB());
            }
        }


        return image;
    }

    private static int buildRGBA(int in) {
        return (255 << 24)
                + (in << 16)
                + (in << 8)
                + in;
    }

    public void error() {
        image.setIcon(new TextIcon(image, "An error occurred."));
    }
}
