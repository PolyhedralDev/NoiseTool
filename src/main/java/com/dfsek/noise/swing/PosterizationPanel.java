package com.dfsek.noise.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class PosterizationPanel extends JScrollPane {
    private final Map<Integer, Integer> colors = new LinkedHashMap<>();
    private final JPanel sections = new JPanel();
    private final JColorChooser colorChooser = new JColorChooser();

    public PosterizationPanel() {
        super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(colorChooser);
        content.add(new JSeparator());
        content.add(sections);

        sections.setLayout(new BoxLayout(sections, BoxLayout.Y_AXIS));
        JButton add = new JButton("+");

        add.addActionListener(e -> sections.add(new Section()));
        content.add(add);

        setViewportView(content);

    }

    private class Section extends JPanel {
        private final JSpinner weight = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        public Section() {
            JButton delete = new JButton("Delete");
            delete.addActionListener(e -> sections.remove(this));
            setBorder(new LineBorder(Color.BLACK, 2));
            add(new JLabel(createImageIcon(Color.BLUE, 20, 20)));
            add(weight);
            add(delete);
        }
    }

    public static ImageIcon createImageIcon(Color color, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect (0, 0, width, height);
        return new ImageIcon(image);
    }

}
