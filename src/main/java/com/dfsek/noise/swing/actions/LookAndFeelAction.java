package com.dfsek.noise.swing.actions;

import com.dfsek.noise.NoiseTool;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Changes the Look and Feel.
 */
public class LookAndFeelAction extends AbstractAction {

    private final NoiseTool noiseTool;
    private final UIManager.LookAndFeelInfo info;

    public LookAndFeelAction(NoiseTool noiseTool, UIManager.LookAndFeelInfo info) {
        this.noiseTool = noiseTool;
        putValue(NAME, info.getName());
        this.info = info;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel(info.getClassName());
            SwingUtilities.updateComponentTreeUI(noiseTool);
            if (noiseTool.getFindDialog() != null) {
                noiseTool.getFindDialog().updateUI();
                noiseTool.getReplaceDialog().updateUI();
                noiseTool.getNoise().update();
            }
            noiseTool.pack();
        } catch (RuntimeException re) {
            throw re; // FindBugs
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
