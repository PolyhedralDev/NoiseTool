package com.dfsek.noise.swing.actions;

import com.dfsek.noise.NoiseTool;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class OpenFileAction extends AbstractAction {
    private final NoiseTool noiseTool;

    public OpenFileAction(NoiseTool noiseTool) {
        super("Open");
        this.noiseTool = noiseTool;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int returnVal = noiseTool.getFileChooser().showOpenDialog(noiseTool);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = noiseTool.getFileChooser().getSelectedFile();
            System.out.println("Opening " + file.getAbsolutePath());
            try {
                noiseTool.getTextArea().setText(IOUtils.toString(new FileInputStream(file), Charset.defaultCharset()));
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Operation cancelled by user.");
        }
    }
}
