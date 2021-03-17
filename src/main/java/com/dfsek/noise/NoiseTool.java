package com.dfsek.noise;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import com.dfsek.noise.swing.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.apache.commons.io.IOUtils;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;


public final class NoiseTool extends JFrame implements SearchListener {

    private final CollapsibleSectionPanel csp;
    private final RSyntaxTextArea textArea;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private FindToolBar findToolBar;
    private ReplaceToolBar replaceToolBar;
    private final StatusBar statusBar;

    private final NoisePanel noise;


    private NoiseTool() {
        initSearchDialogs();

        JPanel contentPane = new JPanel(new BorderLayout());

        GridLayout layout = new GridLayout(1, 2);
        setLayout(layout);

        textArea = new RSyntaxTextArea(25, 80);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setMarkOccurrences(true);


        this.noise = new NoisePanel(textArea);

        add(contentPane);
        add(noise);

        csp = new CollapsibleSectionPanel();
        contentPane.add(csp);

        setJMenuBar(createMenuBar());


        try {
            textArea.setText(IOUtils.toString(NoiseTool.class.getResourceAsStream("/config.yml"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        RTextScrollPane sp = new RTextScrollPane(textArea);
        csp.add(sp);

        ErrorStrip errorStrip = new ErrorStrip(textArea);
        contentPane.add(errorStrip, BorderLayout.LINE_END);

        statusBar = new StatusBar();
        contentPane.add(statusBar, BorderLayout.SOUTH);

        setTitle("Noise Tool");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        FlatDarculaLaf.install();

        SwingUtilities.updateComponentTreeUI(NoiseTool.this);
        if (findDialog!=null) {
            findDialog.updateUI();
            replaceDialog.updateUI();
        }
        pack();

        pack();
        setLocationRelativeTo(null);

    }


    private void addItem(Action a, ButtonGroup bg, JMenu menu) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
        bg.add(item);
        menu.add(item);
    }


    private JMenuBar createMenuBar() {

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Search");
        menu.add(new JMenuItem(new ShowFindDialogAction(this)));
        menu.add(new JMenuItem(new ShowReplaceDialogAction(this)));
        menu.add(new JMenuItem(new GoToLineAction(this)));
        menu.addSeparator();

        int ctrl = getToolkit().getMenuShortcutKeyMask();
        int shift = InputEvent.SHIFT_MASK;
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl|shift);
        Action a = csp.addBottomComponent(ks, findToolBar);
        a.putValue(Action.NAME, "Show Find Search Bar");
        menu.add(new JMenuItem(a));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl|shift);
        a = csp.addBottomComponent(ks, replaceToolBar);
        a.putValue(Action.NAME, "Show Replace Search Bar");
        menu.add(new JMenuItem(a));

        mb.add(menu);

        menu = new JMenu("Theme");
        ButtonGroup bg = new ButtonGroup();
        FlatLightLaf.installLafInfo();
        FlatDarculaLaf.installLafInfo();
        FlatDarkLaf.installLafInfo();
        LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo info : infos) {
            addItem(new LookAndFeelAction(this, info), bg, menu);
        }
        mb.add(menu);

        menu = new JMenu("Noise");
        menu.add(new UpdateNoiseAction(noise));
        menu.add(new MutableBooleanAction(noise.getChunk(), "Toggle Chunk Borders"));
        menu.add(new MutableBooleanAction(noise.getDistribution(), "Toggle Distribution Plot"));
        mb.add(menu);

        return mb;

    }


    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }


    /**
     * Creates our Find and Replace dialogs.
     */
    private void initSearchDialogs() {

        findDialog = new FindDialog(this, this);
        replaceDialog = new ReplaceDialog(this, this);

        // This ties the properties of the two dialogs together (match case,
        // regex, etc.).
        SearchContext context = findDialog.getSearchContext();
        replaceDialog.setSearchContext(context);

        // Create tool bars and tie their search contexts together also.
        findToolBar = new FindToolBar(this);
        findToolBar.setSearchContext(context);
        replaceToolBar = new ReplaceToolBar(this);
        replaceToolBar.setSearchContext(context);

    }


    /**
     * Listens for events from our search dialogs and actually does the dirty
     * work.
     */
    @Override
    public void searchEvent(SearchEvent e) {

        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null, result.getCount() +
                        " occurrences replaced.");
                break;
        }

        String text;
        if (result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        }
        else if (type==SearchEvent.Type.MARK_ALL) {
            if (result.getMarkedCount()>0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            }
            else {
                text = "";
            }
        }
        else {
            text = "Text not found";
        }
        statusBar.setLabel(text);

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//					UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new NoiseTool().setVisible(true);
        });
    }

    public ReplaceDialog getReplaceDialog() {
        return replaceDialog;
    }

    public FindDialog getFindDialog() {
        return findDialog;
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }


}
