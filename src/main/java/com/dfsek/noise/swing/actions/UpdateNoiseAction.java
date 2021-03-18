package com.dfsek.noise.swing.actions;

import com.dfsek.noise.swing.NoisePanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UpdateNoiseAction extends AbstractAction {
    private final NoisePanel noisePanel;

    public UpdateNoiseAction(NoisePanel noisePanel) {
        super("Render");
        this.noisePanel = noisePanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        noisePanel.update();
    }
}
