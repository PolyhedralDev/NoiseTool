package com.dfsek.noise.swing;

import com.dfsek.noise.utils.SwingUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;


public class NoiseSettingsPanel extends JPanel {
    private final JSpinner seedSpinner = new JSpinner(new SpinnerNumberModel(2403, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner xOrigin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner zOrigin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

    private final JCheckBox useLetExpressions = new JCheckBox();

    // Voxel preview settings
    private final JSpinner voxelResolution = new JSpinner(new SpinnerNumberModel(128, 0, Integer.MAX_VALUE, 1));
    private final JSpinner voxelBottomY = new JSpinner(new SpinnerNumberModel(-64, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner voxelTopY = new JSpinner(new SpinnerNumberModel(319, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

    // Color scale presets
    private final ColorScale customColorScalePreset = new ColorScale("Custom", false, (float[][]) null);
    private final ColorScale[] presets = {
        ColorScale.SOLID,
        ColorScale.GRAYSCALE_NORMALIZED,
        ColorScale.GRAYSCALE_0_1,
        ColorScale.GRAYSCALE_0_256,
        ColorScale.GRAYSCALE_N64_320,
        ColorScale.COLORED_0_320,
        customColorScalePreset
    };
    private ColorScale customColorScale;

    // Color scale settings
    private final JComboBox<ColorScale> colorScalePresets = new JComboBox<>(presets);
    private final JCheckBox colorScaleNormalized = new JCheckBox();
    private final JTextArea colorScaleEditor = new JTextArea();

    public NoiseSettingsPanel() {
        super(new SpringLayout());
        add(new JLabel("Seed: "));
        add(seedSpinner);
        add(new JLabel("X Origin: "));
        add(xOrigin);
        add(new JLabel("Z Origin: "));
        add(zOrigin);

        add(new JLabel("Use Let Expressions: "));
        add(useLetExpressions);

        add(new JLabel("Voxel preview resolution: "));
        add(voxelResolution);

        add(new JLabel("Voxel preview bottom Y: "));
        add(voxelBottomY);
        add(new JLabel("Voxel preview top Y: "));
        add(voxelTopY);

        add(new JLabel("Color scale preset: "));
        colorScalePresets.setSelectedItem(ColorScale.GRAYSCALE_NORMALIZED);
        add(colorScalePresets);

        colorScaleNormalized.setSelected(((ColorScale) colorScalePresets.getSelectedItem()).getNormalized());
        add(new JLabel("Color scale normalization: "));
        add(colorScaleNormalized);

        colorScaleEditor.setRows(10);
        colorScaleEditor.setText(((ColorScale) colorScalePresets.getSelectedItem()).getScaleAsText());
        add(new JLabel("Color scale: "));
        add(new JScrollPane(colorScaleEditor));

        AtomicBoolean ignoreColorScaleChange = new AtomicBoolean(false);
        colorScalePresets.addActionListener(e -> {
            ColorScale selection = (ColorScale) colorScalePresets.getSelectedItem();
            if (selection == customColorScalePreset) return;

            ignoreColorScaleChange.set(true);
            colorScaleEditor.setText(selection.getScaleAsText());
            colorScaleNormalized.setSelected(selection.getNormalized());
            ignoreColorScaleChange.set(false);
        });

        colorScaleEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (ignoreColorScaleChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleNormalized.isSelected(), colorScaleEditor.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (ignoreColorScaleChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleNormalized.isSelected(), colorScaleEditor.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (ignoreColorScaleChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleNormalized.isSelected(), colorScaleEditor.getText());
            }
        });

        colorScaleNormalized.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ignoreColorScaleChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleNormalized.isSelected(), colorScaleEditor.getText());
            }
        });

        SwingUtils.makeCompactGrid(this, 10, 2, 10, 10, 10, 10);

    }


    public int getSeed() {
        return ((Number) seedSpinner.getValue()).intValue();
    }

    public double getOriginX() {
        return ((Number) xOrigin.getValue()).doubleValue();
    }

    public void setOriginX(double x) {
        xOrigin.setValue(x);
    }

    public double getOriginZ() {
        return ((Number) zOrigin.getValue()).doubleValue();
    }

    public void setOriginZ(double z) {
        zOrigin.setValue(z);
    }

    public boolean isUseLetExpressions() {
        return useLetExpressions.isSelected();
    }

    public int getVoxelResolution() {
        return ((Number) voxelResolution.getValue()).intValue();
    }

    public int getVoxelBottomY() {
        return ((Number) voxelBottomY.getValue()).intValue();
    }

    public int getVoxelTopY() {
        return ((Number) voxelTopY.getValue()).intValue();
    }

    public ColorScale getColorScale() {
        ColorScale selection = (ColorScale) colorScalePresets.getSelectedItem();
        if (selection == customColorScalePreset) {
            return customColorScale;
        }
        else {
            return selection;
        }
    }
}
