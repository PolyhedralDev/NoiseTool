package com.dfsek.noise.swing;

import com.dfsek.noise.NoiseTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Shows the Replace dialog.
 */
public class ShowReplaceDialogAction extends AbstractAction {

    private final NoiseTool noiseTool;

    public ShowReplaceDialogAction(NoiseTool noiseTool) {
        super("Replace...");
        this.noiseTool = noiseTool;
        int c = noiseTool.getToolkit().getMenuShortcutKeyMask();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (noiseTool.getFindDialog().isVisible()) {
            noiseTool.getFindDialog().setVisible(false);
        }
        noiseTool.getReplaceDialog().setVisible(true);
    }

}
