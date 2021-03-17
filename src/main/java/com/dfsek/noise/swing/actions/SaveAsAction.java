package com.dfsek.noise.swing.actions;

import com.dfsek.noise.NoiseTool;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

public class SaveAsAction extends AbstractAction {
    private final NoiseTool noiseTool;

    public SaveAsAction(NoiseTool noiseTool) {
        super("Save As");
        this.noiseTool = noiseTool;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int returnVal = noiseTool.getFileChooser().showOpenDialog(noiseTool);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = noiseTool.getFileChooser().getSelectedFile();
            System.out.println("Saving to " + file.getAbsolutePath());
            try(FileWriter writer = new FileWriter(file))  {
                if(!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                IOUtils.write(noiseTool.getTextArea().getText(), writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Operation cancelled by user.");
        }
    }
}
