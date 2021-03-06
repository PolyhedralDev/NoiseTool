package com.dfsek.noise.swing;

import com.dfsek.noise.config.ColorConfigTemplate;
import com.dfsek.noise.config.NoiseConfigTemplate;
import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;
import com.dfsek.terra.api.math.noise.NoiseSampler;
import com.dfsek.terra.api.util.collections.ProbabilityCollection;
import com.dfsek.terra.api.util.mutable.MutableBoolean;
import com.dfsek.terra.api.util.seeded.NoiseSeeded;
import com.dfsek.terra.config.GenericLoaders;
import com.dfsek.terra.config.fileloaders.FolderLoader;
import com.dfsek.terra.config.loaders.ProbabilityCollectionLoader;
import com.dfsek.terra.config.loaders.config.BufferedImageLoader;
import com.dfsek.terra.config.loaders.config.sampler.NoiseSamplerBuilderLoader;
import com.dfsek.terra.registry.config.NoiseRegistry;
import net.jafama.FastMath;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class NoisePanel extends JPanel {
    private final RSyntaxTextArea textArea;

    private final JLabel image;

    private final JTextArea statisticsPanel;

    private final NoiseDistributionPanel distributionPanel;

    private final MutableBoolean chunk = new MutableBoolean();

    private final NoiseSettingsPanel settingsPanel;
    private final MutableBoolean error;
    private final MutableBoolean moved;
    private BufferedImage render;

    private NoiseSeeded noiseSeeded;
    private ProbabilityCollection<Integer> colorCollection;
    private final MutableBoolean color = new MutableBoolean();

    public NoisePanel(RSyntaxTextArea textArea, JTextArea statisticsPanel, NoiseDistributionPanel distributionPanel, final NoiseSettingsPanel settingsPanel) {
        this.textArea = textArea;
        this.statisticsPanel = statisticsPanel;
        this.distributionPanel = distributionPanel;
        this.settingsPanel = settingsPanel;
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
                        if(!error.get()) {
                            screenX = e.getXOnScreen();
                            screenY = e.getYOnScreen();

                            myX = getX();
                            myY = getY();
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(!error.get() && moved.get()) {
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
                        if(!error.get()) {
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
        if(this.error.get()) return;
        this.error.set(true);
        try {
            this.render = getImage(this.settingsPanel.getSeed());
            this.image.setIcon(new ImageIcon(this.render));
            this.error.set(false);
        } catch(Exception e) {
            e.printStackTrace();
            this.image.setIcon(new TextIcon(this, "An error occurred. "));
            this.statisticsPanel.setText("An error occurred.");
            this.distributionPanel.error();
        }
    }

    public void reload() {
        this.error.set(true);
        try {
            FolderLoader folderLoader = new FolderLoader(Paths.get("./"));
            ConfigLoader loader = new ConfigLoader();
            loader.registerLoader(NoiseSeeded.class, new NoiseSamplerBuilderLoader(new NoiseRegistry()))
                    .registerLoader(BufferedImage.class, new BufferedImageLoader(folderLoader))
                    .registerLoader(ProbabilityCollection.class, new ProbabilityCollectionLoader());
            new GenericLoaders(null).register(loader);
            NoiseConfigTemplate template = new NoiseConfigTemplate();
            File colorFile = new File("./color.yml");

            ColorConfigTemplate colorConfigTemplate = new ColorConfigTemplate();
            if(colorFile.exists()) {
                loader.load(colorConfigTemplate, new FileInputStream(colorFile));
                color.set(colorConfigTemplate.enable());
            }
            this.colorCollection = colorConfigTemplate.getColors();
            loader.load(template, this.textArea.getText());
            System.out.println(template.getBuilder().getDimensions() + " Dimensions.");
            this.noiseSeeded = template.getBuilder();
            this.error.set(false);
        } catch(Exception e) {
            e.printStackTrace();
            this.image.setIcon(new TextIcon(this, "An error occurred. "));
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

    private BufferedImage getImage(long seed) {
        System.out.println("Rendering noise with seed " + seed);

        NoiseSampler noise = noiseSeeded.apply(seed);
        int sizeX = getWidth();
        int sizeY = getHeight();
        double originX = this.settingsPanel.getOriginX();
        double originZ = this.settingsPanel.getOriginZ();
        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);
        double[][] noiseVals = new double[sizeX][sizeY];



        long startTime = System.nanoTime();
        for(int x = 0; x < noiseVals.length; x++) {
            for(int z = 0; z < (noiseVals[x]).length; z++) {
                double n = noise.getNoise(x + originX, z + originZ);
                noiseVals[x][z] = n;
            }
        }
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1000000.0D;

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for(double[] noiseVal : noiseVals) { // do this separately to give more accurate measure of performance.
            for(double v : noiseVal) {
                max = FastMath.max(v, max);
                min = FastMath.min(v, min);
            }
        }


        int[] buckets = new int[sizeX];
        for(int x = 0; x < noiseVals.length; x++) {
            for(int z = 0; z < (noiseVals[x]).length; z++) {
                if(color.get()) {
                    image.setRGB(x, z, colorCollection.get(noise, x + originX, z + originZ) + -16777216);
                } else {
                    image.setRGB(x, z, buildRGBA(normal(noiseVals[x][z], 255.0D, min, max)));
                }
                buckets[normal(noiseVals[x][z], (sizeX - 1), min, max)] = buckets[normal(noiseVals[x][z], (sizeX - 1), min, max)] + 1;
            }
        }

        if(this.chunk.get()) {
            for(int x = 0; x < FastMath.floorDiv(image.getWidth(), 16); x++) {
                for(int y = 0; y < image.getHeight(); y++) {
                    image.setRGB(x * 16, y, buildRGBA(0));
                }
            }
            for(int y = 0; y < FastMath.floorDiv(image.getHeight(), 16); y++) {
                for(int x = 0; x < image.getWidth(); x++) {
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
