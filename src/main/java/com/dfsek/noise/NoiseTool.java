package com.dfsek.noise;

import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;
import com.dfsek.terra.api.math.noise.samplers.NoiseSampler;
import com.dfsek.terra.config.loaders.config.NoiseBuilderLoader;
import com.dfsek.terra.generation.config.NoiseBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class NoiseTool {
    public static void main(String... args) throws ConfigException, IOException {
        JFrame frame = new JFrame("Noise Viewer");

        AtomicInteger seed = new AtomicInteger(2403);
        JLabel label = new JLabel(new ImageIcon(load(seed.get(), false)));
        frame.add(label);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);



        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == 'r') {
                    try {
                        label.setIcon(new ImageIcon(load(seed.get(), false)));
                    } catch(ConfigException | IOException configException) {
                        configException.printStackTrace();
                    }
                } else if(e.getKeyChar() == 's') {
                    try {
                        seed.set(ThreadLocalRandom.current().nextInt());
                        label.setIcon(new ImageIcon(load(seed.get(), false)));
                    } catch(ConfigException | IOException configException) {
                        configException.printStackTrace();
                    }
                } else if(e.getKeyChar() == 'd') {
                    try {
                        label.setIcon(new ImageIcon(load(seed.get(), true)));
                    } catch(ConfigException | IOException configException) {
                        configException.printStackTrace();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        frame.setVisible(true);
    }

    private static int normal(double in, double out, double min, double max) {
        double range = max - min;
        return (int) (((in - min) * out) / range);
    }
    private static int buildRGBA(int in) {
        return (255 << 24)
                + (in << 16)
                + (in << 8)
                + in;
    }
    private static BufferedImage load(int seed, boolean distribution) throws ConfigException, IOException {
        long s = System.nanoTime();

        ConfigLoader loader = new ConfigLoader();
        loader.registerLoader(NoiseBuilder.class, new NoiseBuilderLoader());
        NoiseConfigTemplate template = new NoiseConfigTemplate();

        File file = new File("./config.yml");

        System.out.println(file.getAbsolutePath());
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            FileUtils.copyInputStreamToFile(NoiseTool.class.getResourceAsStream("/config.yml"), file);
        }

        loader.load(template, new FileInputStream(file));
        NoiseSampler noise = template.getBuilder().build(seed);

        int size = 1024;

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        double[][] noiseVals = new double[size][size];
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        int[] buckets = new int[1024];

        for(int x = 0; x < noiseVals.length; x++) {
            for(int z = 0; z < noiseVals[x].length; z++) {
                double n = noise.getNoise(x, z);
                noiseVals[x][z] = n;
                max = Math.max(n, max);
                min = Math.min(n, min);
            }
        }

        for(int x = 0; x < noiseVals.length; x++) {
            for(int z = 0; z < noiseVals[x].length; z++) {
                image.setRGB(x, z, buildRGBA(normal(noiseVals[x][z], 255, min, max)));
                buckets[normal(noiseVals[x][z], size-1, min, max)]++;
            }
        }

        long time = System.nanoTime() - s;

        double ms = time / 1000000d;

        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 325, 90);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Monospace", Font.BOLD, 20));
        graphics.drawString("min: " + min, 0, 20);
        graphics.drawString("max: " + max, 0, 40);
        graphics.drawString("seed: " + seed, 0, 60);
        graphics.drawString("time: " + ms + "ms", 0, 80);

        if(distribution) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, size - (size/4) - 1, size, (size/4) - 1);
            int highestBucket = Integer.MIN_VALUE;
            for(int i : buckets) highestBucket = Math.max(highestBucket, i);
            graphics.setColor(Color.BLACK);
            graphics.drawString("" + highestBucket, 0, size - (size/4) - 1 + 20);

            for(int x = 0; x < size; x++) {
                for(int y = 0; y < ((double) buckets[x]/highestBucket) * ((double) size/4); y++){
                    image.setRGB(x, size-y-1, buildRGBA(0));
                }
            }

        }

        return image;
    }
}
