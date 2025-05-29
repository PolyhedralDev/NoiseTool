package com.dfsek.noise.swing;

import org.fife.rsta.ui.SizeGripIcon;

import javax.swing.*;
import java.awt.*;

/**
 * The status bar for this application.
 */
public class StatusBar extends JPanel {

    private final JLabel label;
    private final JLabel coordinatesLabel;

    public StatusBar() {
        setLayout(new BorderLayout());

        // Create grouping for components on the left side
        JPanel leftGroup = new JPanel(new FlowLayout());
        add(leftGroup, BorderLayout.LINE_START);

        label = new JLabel("Ready");
        leftGroup.add(label, BorderLayout.LINE_START);

        // Create grouping for components on the right side
        JPanel rightGroup = new JPanel(new FlowLayout());
        add(rightGroup, BorderLayout.LINE_END);

        coordinatesLabel = new JLabel("Coordinates");
        rightGroup.add(coordinatesLabel);

        rightGroup.add(new JLabel(new SizeGripIcon()));
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setCoordinates(int x, int z) {
        coordinatesLabel.setText("X=" + x + "   Z=" + z);
    }

    public void clearCoordinates() {
        coordinatesLabel.setText(" ");
    }
}
