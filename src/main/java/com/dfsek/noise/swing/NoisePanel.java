package com.dfsek.noise.swing;

import com.dfsek.noise.platform.DummyPack;
import com.dfsek.tectonic.yaml.YamlConfiguration;
import com.dfsek.terra.api.Platform;
import com.dfsek.terra.api.noise.NoiseSampler;
import com.dfsek.terra.api.util.collection.ProbabilityCollection;
import com.dfsek.terra.api.util.mutable.MutableBoolean;
import net.worldsynth.glpreview.heightmap.Heightmap3DGLPreviewBufferedGL;
import net.worldsynth.glpreview.voxel.Blockspace3DGLPreviewBufferedGL;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class NoisePanel extends JPanel {
    private final RSyntaxTextArea textArea;

    private final JLabel image;

    private final Heightmap3DGLPreviewBufferedGL noise3d;
    private final Blockspace3DGLPreviewBufferedGL noise3dVox;

    private final JTextArea statisticsPanel;

    private final NoiseDistributionPanel distributionPanel;

    private final MutableBoolean chunk = new MutableBoolean();

    private final NoiseSettingsPanel settingsPanel;
    private final MutableBoolean error;
    private final MutableBoolean moved;
    private BufferedImage render;

    private NoiseSampler noiseSeeded;
    private ProbabilityCollection<Integer> colorCollection;
    private final Platform platform;

    public NoisePanel(RSyntaxTextArea textArea, Heightmap3DGLPreviewBufferedGL noise3d, Blockspace3DGLPreviewBufferedGL noise3dVox, JTextArea statisticsPanel, NoiseDistributionPanel distributionPanel, final NoiseSettingsPanel settingsPanel, Platform platform) {
        this.textArea = textArea;
        this.noise3d = noise3d;
        this.noise3dVox = noise3dVox;
        this.statisticsPanel = statisticsPanel;
        this.distributionPanel = distributionPanel;
        this.settingsPanel = settingsPanel;
        this.platform = platform;
        this.image = new JLabel();
        this.error = new MutableBoolean();
        this.moved = new MutableBoolean();
        this.moved.set(false);
        error.set(false);
        add(new JPanel() {
            private volatile int screenX;

            private volatile int screenY;

            private volatile int myX;

            private volatile int myY;

            {
                add(image);
                addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.nanoTime();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!error.get()) {
                            screenX = e.getXOnScreen();
                            screenY = e.getYOnScreen();

                            myX = getX();
                            myY = getY();
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (!error.get() && moved.get()) {
                            moved.set(false);
                            int deltaX = e.getXOnScreen() - screenX;
                            int deltaY = e.getYOnScreen() - screenY;

                            settingsPanel.setOriginX(settingsPanel.getOriginX() - deltaX);
                            settingsPanel.setOriginZ(settingsPanel.getOriginZ() - deltaY);
                            NoisePanel.this.update();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }

                });
                addMouseMotionListener(new MouseMotionListener() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (!error.get()) {
                            moved.set(true);
                            int deltaX = e.getXOnScreen() - screenX;
                            int deltaY = e.getYOnScreen() - screenY;

                            setLocation(myX + deltaX, myY + deltaY);
                        }
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                    }

                });
            }
        });
    }

    private static int normal(double in, double out, double min, double max) {
        double range = max - min;
        return (int) ((in - min) * out / range);
    }

    private static int buildRGBA(int in) {
        return -16777216 + (in << 16) + (in << 8) + in;
    }

    public void update() {
        if (this.error.get()) return;
        this.error.set(true);
        try {
            this.render = getImage(this.settingsPanel.getSeed());
            this.image.setIcon(new ImageIcon(this.render));

            double[][] noiseVals = getNoiseVals(this.settingsPanel.getSeed());
            this.noise3d.setColorScale(settingsPanel.getColorScale());
            this.noise3d.setHeightmap(noiseVals);

            if (this.settingsPanel.getVoxelResolution() > 0) {
                boolean[][][] noiseValsVox = getNoiseVals3d(this.settingsPanel.getSeed());
                this.noise3dVox.setBlockspace(noiseValsVox);
            } else {
                this.noise3dVox.clearBlockspace();
            }

            this.error.set(false);
        } catch (Exception e) {
            e.printStackTrace();
            this.image.setIcon(new TextIcon(this, "An error occurred. "));
            this.noise3d.clearHeightmap();
            this.noise3dVox.clearBlockspace();
            this.statisticsPanel.setText("An error occurred.");
            this.distributionPanel.error();
        }
    }

    public void reload() {
        this.error.set(true);
        try {
            DummyPack pack = new DummyPack(platform, new YamlConfiguration(this.textArea.getText(), "Noise Config"));
            this.noiseSeeded = pack.getNoiseSampler();
            this.error.set(false);
        } catch (Exception e) {
            e.printStackTrace();
            this.image.setIcon(new TextIcon(this, "An error occurred. "));
            this.noise3d.clearHeightmap();
            this.noise3dVox.clearBlockspace();
            this.statisticsPanel.setText("An error occurred.");
            this.distributionPanel.error();
        }
    }

    public BufferedImage getRender() {
        return this.render;
    }

    public MutableBoolean getChunk() {
        return this.chunk;
    }

    private double[][] getNoiseVals(long seed) {
        int sizeX = getWidth();
        int sizeZ = getHeight();
        double originX = this.settingsPanel.getOriginX();
        double originZ = this.settingsPanel.getOriginZ();

        double[][] noiseVals = new double[sizeX][sizeZ];
        for (int x = 0; x < noiseVals.length; x++) {
            for (int z = 0; z < (noiseVals[x]).length; z++) {
                double n = noiseSeeded.noise(seed, x + originX, z + originZ);
                noiseVals[x][z] = n;
            }
        }
        return noiseVals;
    }

    private boolean[][][] getNoiseVals3d(long seed) {
        double originX = this.settingsPanel.getOriginX();
        double originZ = this.settingsPanel.getOriginZ();

        int sampleRes = this.settingsPanel.getVoxelResolution();
        int sampleYMin = this.settingsPanel.getVoxelBottomY();
        int sampleYMax = this.settingsPanel.getVoxelTopY();

        boolean[][][] noiseVals = new boolean[sampleRes][sampleYMax - sampleYMin][sampleRes];
        for (int x = 0; x < noiseVals.length; x++) {
            for (int y = 0; y < noiseVals[x].length; y++) {
                for(int z = 0; z < (noiseVals[x][y]).length; z++) {
                    double n = noiseSeeded.noise(seed, x + originX, y + sampleYMin, z + originZ);
                    noiseVals[x][y][z] = n > 0;
                }
            }
        }
        return noiseVals;
    }

    private BufferedImage getImage(long seed) {
        System.out.println("Rendering noise with seed " + seed);

        int sizeX = getWidth();
        int sizeY = getHeight();
        double originX = this.settingsPanel.getOriginX();
        double originZ = this.settingsPanel.getOriginZ();
        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);
        double[][] noiseVals = new double[sizeX][sizeY];


        long startTime = System.nanoTime();
        for (int x = 0; x < noiseVals.length; x++) {
            for (int z = 0; z < (noiseVals[x]).length; z++) {
                double n = noiseSeeded.noise(seed, x + originX, z + originZ);
                noiseVals[x][z] = n;
            }
        }
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1000000.0D;

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (double[] noiseVal : noiseVals) { // do this separately to give more accurate measure of performance.
            for (double v : noiseVal) {
                max = Math.max(v, max);
                min = Math.min(v, min);
            }
        }


        int[] buckets = new int[sizeX];
        for (int x = 0; x < noiseVals.length; x++) {
            for (int z = 0; z < (noiseVals[x]).length; z++) {
                if (colorCollection != null) {
                    image.setRGB(x, z, colorCollection.get(noiseSeeded, x + originX, z + originZ, seed) - 16777216);
                } else {
                    image.setRGB(x, z, this.settingsPanel.getColorScale().valueToIRgb(noiseVals[x][z], min, max));
                }
                buckets[normal(noiseVals[x][z], (sizeX - 1), min, max)] = buckets[normal(noiseVals[x][z], (sizeX - 1), min, max)] + 1;
            }
        }

        if (this.chunk.get()) {
            for (int x = 0; x < Math.floorDiv(image.getWidth(), 16); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    image.setRGB(x * 16, y, buildRGBA(0));
                }
            }
            for (int y = 0; y < Math.floorDiv(image.getHeight(), 16); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    image.setRGB(x, y * 16, buildRGBA(0));
                }
            }
        }
        this.statisticsPanel.setText("min: " + min + "\nmax: " + max + "\nseed: " + seed + "\ntime: " + timeMs + "ms");
        this.distributionPanel.update(buckets);
        System.out.println("Rendered " + (sizeX * sizeY) + " points in " + timeMs + "ms.");
        return image;
    }
}
