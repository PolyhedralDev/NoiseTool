package com.dfsek.noise.swing;

import com.dfsek.noise.NoiseTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Shows the Find dialog.
 */
public class ShowFindDialogAction extends AbstractAction {

    private final NoiseTool noiseTool;

    public ShowFindDialogAction(NoiseTool noiseTool) {
        super("Find...");
        this.noiseTool = noiseTool;
        int c = noiseTool.getToolkit().getMenuShortcutKeyMask();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (noiseTool.getReplaceDialog().isVisible()) {
            noiseTool.getReplaceDialog().setVisible(false);
        }
        noiseTool.getFindDialog().setVisible(true);
    }

}
