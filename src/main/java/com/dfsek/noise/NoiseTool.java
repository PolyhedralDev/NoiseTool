package com.dfsek.noise;

import com.dfsek.noise.platform.DummyPack;
import com.dfsek.noise.platform.PlatformImpl;
import com.dfsek.noise.swing.NoiseDistributionPanel;
import com.dfsek.noise.swing.NoisePanel;
import com.dfsek.noise.swing.NoiseSettingsPanel;
import com.dfsek.noise.swing.StatusBar;
import com.dfsek.noise.swing.actions.*;
import com.dfsek.tectonic.api.config.template.object.ObjectTemplate;
import com.dfsek.tectonic.yaml.YamlConfiguration;
import com.dfsek.terra.api.noise.NoiseSampler;
import com.dfsek.terra.api.registry.Registry;
import com.dfsek.terra.api.util.reflection.TypeKey;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import net.worldsynth.glpreview.heightmap.Heightmap3DGLPreviewBufferedGL;
import net.worldsynth.glpreview.util.GLUtil;
import net.worldsynth.glpreview.voxel.Blockspace3DGLPreviewBufferedGL;
import org.apache.commons.io.IOUtils;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.search.*;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;


public final class NoiseTool extends JFrame implements SearchListener {

    private final CollapsibleSectionPanel csp;
    private final RSyntaxTextArea textArea;
    private final StatusBar statusBar;
    private final JFileChooser fileChooser = new JFileChooser();
    private final JFileChooser imageChooser = new JFileChooser();
    private final NoisePanel noise;
    private FindDialog findDialog;
    private ReplaceDialog replaceDialog;
    private FindToolBar findToolBar;
    private ReplaceToolBar replaceToolBar;

    private static final TypeKey<Supplier<ObjectTemplate<NoiseSampler>>> NOISE_REGISTRY_KEY = new TypeKey<>() {};


    private NoiseTool() throws IOException {
        String config = IOUtils.toString(Objects.requireNonNull(NoiseTool.class.getResourceAsStream("/config.yml")), StandardCharsets.UTF_8);
        initSearchDialogs();

        // Use a border layout as the root layout
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        // Status bar at the bottom of the window
        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);

        // Text area and error strip to the right of the text area
        JPanel textPanel = new JPanel(new BorderLayout());

        textArea = new RSyntaxTextArea(35, 45);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setMarkOccurrences(true);
        textArea.setTabsEmulated(true);
        textArea.setTabSize(2);

        textArea.setText(config);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        csp = new CollapsibleSectionPanel();
        csp.add(sp);
        textPanel.add(csp, BorderLayout.CENTER);

        ErrorStrip errorStrip = new ErrorStrip(textArea);
        textPanel.add(errorStrip, BorderLayout.LINE_END);

        // Nose panels and other stuff at the right side
        PlatformImpl platform = new PlatformImpl();
        DummyPack pack = new DummyPack(platform, new YamlConfiguration(config, "Noise Config"));

        CompletionProvider provider = createCompletionProvider(pack.getRegistry(NOISE_REGISTRY_KEY));

        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(textArea);
        ac.setShowDescWindow(true);
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoCompleteSingleChoices(false);
        ac.setAutoActivationDelay(200);

        JTextArea statisticsPanel = new JTextArea();
        statisticsPanel.setEditable(false);

        NoiseDistributionPanel distributionPanel = new NoiseDistributionPanel();

        NoiseSettingsPanel settingsPanel = new NoiseSettingsPanel();

        GLUtil.logGLProfiles();
        Heightmap3DGLPreviewBufferedGL noise3d = new Heightmap3DGLPreviewBufferedGL();
        Blockspace3DGLPreviewBufferedGL noise3dVox = new Blockspace3DGLPreviewBufferedGL();

        this.noise = new NoisePanel(textArea, noise3d, noise3dVox, statisticsPanel, distributionPanel, settingsPanel, platform, statusBar);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Render", noise);

        tabbedPane.addTab("Render 3D", noise3d);
        tabbedPane.addTab("Render Voxel", noise3dVox);

        tabbedPane.addTab("Settings", settingsPanel);

        tabbedPane.addTab("Statistics", statisticsPanel);

        tabbedPane.addTab("Distribution", distributionPanel);

        JTextArea sysout = new JTextArea();
        sysout.setEditable(false);

        System.setOut(new PrintStream(new TextAreaOutputStream(sysout)));
        System.setErr(new PrintStream(new TextAreaOutputStream(sysout)));

        tabbedPane.addTab("Console", new JScrollPane(sysout));

        tabbedPane.setSelectedIndex(0);

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        GridLayout gridLayout = new GridLayout(1, 2);
        JPanel contentPanel = new JPanel(gridLayout);

        add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(textPanel);
        contentPanel.add(tabbedPane);


        setJMenuBar(createMenuBar());


        setTitle("Noise Tool");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        FlatDarculaLaf.setup();

        SwingUtilities.updateComponentTreeUI(NoiseTool.this);
        if(findDialog != null) {
            findDialog.updateUI();
            replaceDialog.updateUI();
        }
        pack();

        pack();
        setLocationRelativeTo(null);

        noise.reload();
        noise.update();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                new NoiseTool().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private CompletionProvider createCompletionProvider(Registry<Supplier<ObjectTemplate<NoiseSampler>>> registry) {
        DefaultCompletionProvider noiseTypeProvider = new DefaultCompletionProvider();
        noiseTypeProvider.setAutoActivationRules(true, null);

        registry.keys().forEach(key -> noiseTypeProvider.addCompletion(new BasicCompletion(noiseTypeProvider, key.toString(), null, key + " noise type")));

        DefaultCompletionProvider basicProvider = new DefaultCompletionProvider();
        basicProvider.setAutoActivationRules(true, null);

        registry.keys().forEach(key -> basicProvider.addCompletion(new BasicCompletion(basicProvider, key.toString(), null, key + " noise type")));
        basicProvider.addCompletion(new BasicCompletion(basicProvider, "type", null, "Sets the noise type for this sampler."));
        basicProvider.addCompletion(new BasicCompletion(basicProvider, "frequency", null, "Sets the frequency for this sampler."));

        LanguageAwareCompletionProvider provider = new LanguageAwareCompletionProvider(basicProvider);
        basicProvider.setAutoActivationRules(true, null);

        provider.setStringCompletionProvider(noiseTypeProvider);

        return provider;

    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    private void addItem(Action a, ButtonGroup bg, JMenu menu) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
        bg.add(item);
        menu.add(item);
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu menu = new JMenu("File");

        Action open = new OpenFileAction(this);
        Action save = new SaveAction(this);
        Action saveAs = new SaveAsAction(this);
        Action saveRender = new SaveRenderAsAction(this);

        open.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        save.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        saveAs.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
        saveRender.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK));

        menu.add(open);
        menu.add(save);
        menu.add(saveAs);
        menu.add(saveRender);


        mb.add(menu);

        menu = new JMenu("Search");
        menu.add(new JMenuItem(new ShowFindDialogAction(this)));
        menu.add(new JMenuItem(new ShowReplaceDialogAction(this)));
        menu.add(new JMenuItem(new GoToLineAction(this)));
        menu.addSeparator();

        int ctrl = getToolkit().getMenuShortcutKeyMask();
        int shift = InputEvent.SHIFT_MASK;
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl | shift);
        Action a = csp.addBottomComponent(ks, findToolBar);
        a.putValue(Action.NAME, "Show Find Search Bar");
        menu.add(new JMenuItem(a));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl | shift);
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
        for(LookAndFeelInfo info : infos) {
            addItem(new LookAndFeelAction(this, info), bg, menu);
        }
        mb.add(menu);

        menu = new JMenu("Noise");

        Action up = new UpdateNoiseAction(noise);

        up.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        menu.add(up);
        menu.add(new MutableBooleanAction(noise.getChunk(), "Toggle Chunk Borders"));
        mb.add(menu);

        return mb;

    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }

    public NoisePanel getNoise() {
        return noise;
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

        switch(type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if(!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if(!result.wasFound() || result.isWrapped()) {
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
        if(result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        } else if(type == SearchEvent.Type.MARK_ALL) {
            if(result.getMarkedCount() > 0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            } else {
                text = "";
            }
        } else {
            text = "Text not found";
        }
        statusBar.setLabel(text);

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


    public JFileChooser getImageChooser() {
        return imageChooser;
    }
}
