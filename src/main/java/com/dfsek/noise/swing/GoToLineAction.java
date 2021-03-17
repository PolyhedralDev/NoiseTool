package com.dfsek.noise.swing;

import com.dfsek.noise.NoiseTool;
import org.fife.rsta.ui.GoToDialog;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Opens the "Go to Line" dialog.
 */
public class GoToLineAction extends AbstractAction {

    private final NoiseTool noiseTool;

    public GoToLineAction(NoiseTool noiseTool) {
        super("Go To Line...");
        this.noiseTool = noiseTool;
        int c = noiseTool.getToolkit().getMenuShortcutKeyMask();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (noiseTool.getFindDialog().isVisible()) {
            noiseTool.getFindDialog().setVisible(false);
        }
        if (noiseTool.getReplaceDialog().isVisible()) {
            noiseTool.getReplaceDialog().setVisible(false);
        }
        GoToDialog dialog = new GoToDialog(noiseTool);
        dialog.setMaxLineNumberAllowed(noiseTool.getTextArea().getLineCount());
        dialog.setVisible(true);
        int line = dialog.getLineNumber();
        if (line > 0) {
            try {
                noiseTool.getTextArea().setCaretPosition(noiseTool.getTextArea().getLineStartOffset(line - 1));
            } catch (BadLocationException ble) { // Never happens
                UIManager.getLookAndFeel().provideErrorFeedback(noiseTool.getFindDialog());
                ble.printStackTrace();
            }
        }
    }

}
