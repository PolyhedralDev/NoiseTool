package com.dfsek.noise.swing;

import com.dfsek.noise.utils.SwingUtils;

import javax.swing.*;

public class NoiseSettingsPanel extends JPanel {
    private final JSpinner seedSpinner = new JSpinner(new SpinnerNumberModel(2403, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner xOrigin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
    private final JSpinner zOrigin = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

    public NoiseSettingsPanel() {
        super(new SpringLayout());
        add(new JLabel("Seed: "));
        add(seedSpinner);
        add(new JLabel("X Origin: "));
        add(xOrigin);
        add(new JLabel("Z Origin: "));
        add(zOrigin);

        SwingUtils.makeCompactGrid(this, 3, 2, 10, 10, 10, 10);
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
}
