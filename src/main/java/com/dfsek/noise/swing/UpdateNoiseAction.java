package com.dfsek.noise.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UpdateNoiseAction  extends AbstractAction {
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
