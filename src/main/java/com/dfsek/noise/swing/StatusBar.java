package com.dfsek.noise.swing;

import org.fife.rsta.ui.SizeGripIcon;

import javax.swing.*;
import java.awt.*;

/**
 * The status bar for this application.
 */
public class StatusBar extends JPanel {

    private final JLabel label;

    public StatusBar() {
        label = new JLabel("Ready");
        setLayout(new BorderLayout());
        add(label, BorderLayout.LINE_START);
        add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

}
