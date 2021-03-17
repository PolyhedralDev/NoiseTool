package com.dfsek.noise.swing.actions;

import com.dfsek.noise.NoiseTool;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveAction extends AbstractAction {
    private final NoiseTool noiseTool;

    public SaveAction(NoiseTool noiseTool) {
        super("Save");
        this.noiseTool = noiseTool;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        File file = noiseTool.getFileChooser().getSelectedFile();

        if (file == null) {
            System.out.println("No file is selected.");
            return;
        }

        System.out.println("Saving to " + file.getAbsolutePath());
        try (FileWriter writer = new FileWriter(file)) {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            IOUtils.write(noiseTool.getTextArea().getText(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
