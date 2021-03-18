package com.dfsek.noise.swing.actions;

import com.dfsek.noise.NoiseTool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

public class SaveRenderAsAction extends AbstractAction {
    private final NoiseTool noiseTool;

    public SaveRenderAsAction(NoiseTool noiseTool) {
        super("Save Render As");
        this.noiseTool = noiseTool;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int returnVal = noiseTool.getImageChooser().showOpenDialog(noiseTool);

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = noiseTool.getImageChooser().getSelectedFile();

            if(!file.getName().endsWith(".png")) {
                System.out.println("ERROR: image file is not of type PNG");
                return;
            }

            System.out.println("Saving to " + file.getAbsolutePath());
            try(FileOutputStream writer = new FileOutputStream(file)) {
                if(!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                ImageIO.write(noiseTool.getNoise().getRender(), "png", writer);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Operation cancelled by user.");
        }
    }
}
