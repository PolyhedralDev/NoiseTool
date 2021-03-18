package com.dfsek.noise.swing;

import com.dfsek.noise.config.ColorConfigTemplate;
import com.dfsek.noise.config.NoiseConfigTemplate;
import com.dfsek.tectonic.config.ConfigTemplate;
import com.dfsek.tectonic.exception.ConfigException;
import com.dfsek.tectonic.loading.ConfigLoader;
import com.dfsek.tectonic.loading.TypeLoader;
import com.dfsek.tectonic.loading.TypeRegistry;
import com.dfsek.terra.api.math.noise.NoiseSampler;
import com.dfsek.terra.api.util.collections.ProbabilityCollection;
import com.dfsek.terra.api.util.mutable.MutableBoolean;
import com.dfsek.terra.api.util.seeded.NoiseSeeded;
import com.dfsek.terra.config.GenericLoaders;
import com.dfsek.terra.config.fileloaders.FolderLoader;
import com.dfsek.terra.config.fileloaders.Loader;
import com.dfsek.terra.config.loaders.ProbabilityCollectionLoader;
import com.dfsek.terra.config.loaders.config.BufferedImageLoader;
import com.dfsek.terra.config.loaders.config.sampler.NoiseSamplerBuilderLoader;
import com.dfsek.terra.registry.config.NoiseRegistry;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import net.jafama.FastMath;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class NoisePanel extends JPanel {
    private final RSyntaxTextArea textArea;

    private final JLabel image;

    private final JTextArea statisticsPanel;

    private final NoiseDistributionPanel distributionPanel;

    private final MutableBoolean chunk = new MutableBoolean();

    private final NoiseSettingsPanel settingsPanel;

    private BufferedImage render;

    public NoisePanel(RSyntaxTextArea textArea, JTextArea statisticsPanel, NoiseDistributionPanel distributionPanel, final NoiseSettingsPanel settingsPanel) {
        this.textArea = textArea;
        this.statisticsPanel = statisticsPanel;
        this.distributionPanel = distributionPanel;
        this.settingsPanel = settingsPanel;
        this.image = new JLabel();
        add(new JPanel() {
            private volatile int screenX;

            private volatile int screenY;

            private volatile int myX;

            private volatile int myY;
            {
                add(image);
                addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) { }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        screenX = e.getXOnScreen();
                        screenY = e.getYOnScreen();

                        myX = getX();
                        myY = getY();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        int deltaX = e.getXOnScreen() - screenX;
                        int deltaY = e.getYOnScreen() - screenY;

                        settingsPanel.setOriginX(settingsPanel.getOriginX() - (myX + deltaX));
                        settingsPanel.setOriginZ(settingsPanel.getOriginZ() - (myY + deltaY));
                        NoisePanel.this.update();
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) { }

                    @Override
                    public void mouseExited(MouseEvent e) { }

                });
                addMouseMotionListener(new MouseMotionListener() {

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        int deltaX = e.getXOnScreen() - screenX;
                        int deltaY = e.getYOnScreen() - screenY;

                        setLocation(myX + deltaX, myY + deltaY);
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) { }

                });
            }
        });
    }

    public void update() {
        try {
            this.render = getImage(this.settingsPanel.getSeed());
            this.image.setIcon(new ImageIcon(this.render));
        } catch (Exception e) {
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

    private BufferedImage getImage(long seed) throws ConfigException, FileNotFoundException {
        System.out.println("Rendering noise with seed " + seed);
        FolderLoader folderLoader = new FolderLoader(Paths.get("./"));
        ConfigLoader loader = new ConfigLoader();
        loader.registerLoader(NoiseSeeded.class, new NoiseSamplerBuilderLoader(new NoiseRegistry()))
                .registerLoader(BufferedImage.class, new BufferedImageLoader(folderLoader))
                .registerLoader(ProbabilityCollection.class, new ProbabilityCollectionLoader());
        (new GenericLoaders(null)).register(loader);
        NoiseConfigTemplate template = new NoiseConfigTemplate();
        File colorFile = new File("./color.yml");
        boolean colors = false;
        ColorConfigTemplate color = new ColorConfigTemplate();
        if (colorFile.exists()) {
            loader.load(color, new FileInputStream(colorFile));
            colors = color.enable();
        }
        ProbabilityCollection<Integer> colorCollection = color.getColors();
        loader.load(template, this.textArea.getText());
        System.out.println(template.getBuilder().getDimensions() + " Dimensions.");
        NoiseSampler noise = template.getBuilder().apply(seed);
        int sizeX = getWidth();
        int sizeY = getHeight();
        double originX = this.settingsPanel.getOriginX();
        double originZ = this.settingsPanel.getOriginZ();
        BufferedImage image = new BufferedImage(sizeX, sizeY, 2);
        double[][] noiseVals = new double[sizeX][sizeY];
        int[][] rgbVals = new int[sizeX][sizeY];
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        int[] buckets = new int[sizeX];
        long s = System.nanoTime();
        for (int x = 0; x < noiseVals.length; x++) {
            for (int z = 0; z < (noiseVals[x]).length; z++) {
                double n = noise.getNoise(x + originX, z + originZ);
                noiseVals[x][z] = n;
                max = Math.max(n, max);
                min = Math.min(n, min);
                if (colors)
                    rgbVals[x][z] = colorCollection.get(noise, x + originX, z + originZ);
            }
        }
        long time = System.nanoTime() - s;
        double ms = time / 1000000.0D;
        int i;
        for (i = 0; i < noiseVals.length; i++) {
            for (int z = 0; z < (noiseVals[i]).length; z++) {
                if (colors) {
                    image.setRGB(i, z, rgbVals[i][z] + -16777216);
                } else {
                    image.setRGB(i, z, buildRGBA(normal(noiseVals[i][z], 255.0D, min, max)));
                }
                buckets[normal(noiseVals[i][z], (sizeX - 1), min, max)] = buckets[normal(noiseVals[i][z], (sizeX - 1), min, max)] + 1;
            }
        }
        if (this.chunk.get()) {
            for (i = 0; i < FastMath.floorDiv(image.getWidth(), 16); i++) {
                for (int j = 0; j < image.getHeight(); ) {
                    image.setRGB(i * 16, j, buildRGBA(0));
                    j++;
                }
            }
            for (int y = 0; y < FastMath.floorDiv(image.getHeight(), 16); y++) {
                for (int j = 0; j < image.getWidth(); ) {
                    image.setRGB(j, y * 16, buildRGBA(0));
                    j++;
                }
            }
        }
        this.statisticsPanel.setText("min: " + min + "\nmax: " + max + "\nseed: " + seed + "\ntime: " + ms + "ms");
        this.distributionPanel.update(buckets);
        System.out.println("Rendered " + (sizeX * sizeY) + " points in " + ms + "ms.");
        return image;
    }

    private static int normal(double in, double out, double min, double max) {
        double range = max - min;
        return (int)((in - min) * out / range);
    }

    private static int buildRGBA(int in) {
        return -16777216 + (in << 16) + (in << 8) + in;
    }
}
