package com.dfsek.noise.swing;

import com.dfsek.noise.utils.SwingUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.concurrent.atomic.AtomicBoolean;


public class NoiseSettingsPanel extends JPanel {
    private final JSpinner seedSpinner = new JSpinner(new SpinnerNumberModel(2403, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner xOrigin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner zOrigin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

    // Color scale presets
    private final ColorScale customColorScalePreset = new ColorScale("Custom", (float[][]) null);
    private final ColorScale[] presets = {
        ColorScale.SOLID,
        ColorScale.GRAYSCALE_0_1,
        ColorScale.GRAYSCALE_0_256,
        ColorScale.GRAYSCALE_N64_320,
        ColorScale.COLORED_0_320,
        customColorScalePreset
    };
    private ColorScale customColorScale;

    // Render settings
    private final JComboBox<ColorScale> colorScalePresets = new JComboBox<>(presets);
    private final JTextArea colorScaleEditor = new JTextArea();

    public NoiseSettingsPanel() {
        super(new SpringLayout());
        add(new JLabel("Seed: "));
        add(seedSpinner);
        add(new JLabel("X Origin: "));
        add(xOrigin);
        add(new JLabel("Z Origin: "));
        add(zOrigin);

        add(new JLabel("Color scale preset: "));
        colorScalePresets.setSelectedItem(ColorScale.SOLID);
        add(colorScalePresets);

        colorScaleEditor.setRows(10);
        colorScaleEditor.setText(((ColorScale) colorScalePresets.getSelectedItem()).getScaleAsText());
        add(new JLabel("Color scale: "));
        add(new JScrollPane(colorScaleEditor));

        AtomicBoolean ignoreTextChange = new AtomicBoolean(false);
        colorScalePresets.addActionListener(e -> {
            ColorScale selection = (ColorScale) colorScalePresets.getSelectedItem();
            if (selection == customColorScalePreset) return;

            ignoreTextChange.set(true);
            colorScaleEditor.setText(selection.getScaleAsText());
            ignoreTextChange.set(false);
        });

        colorScaleEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (ignoreTextChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleEditor.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (ignoreTextChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleEditor.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (ignoreTextChange.get()) return;
                colorScalePresets.setSelectedItem(customColorScalePreset);
                customColorScale = new ColorScale("Custom", colorScaleEditor.getText());
            }
        });

        SwingUtils.makeCompactGrid(this, 5, 2, 10, 10, 10, 10);
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
