package com.dfsek.noise.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class PosterizationPanel extends JScrollPane {
    private final Map<Integer, Integer> colors = new LinkedHashMap<>();
    private final DefaultListModel<Section> defaultListModel = new DefaultListModel<>();
    private final JList<Section> sections = new JList<>(defaultListModel);

    private final JColorChooser colorChooser = new JColorChooser();

    private Section selected;

    public PosterizationPanel() {
        super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JButton apply = new JButton("Apply Color");

        apply.addActionListener(e -> selected.setColor(colorChooser.getColor()));
        content.add(colorChooser);
        content.add(apply);
        content.add(new JSeparator());
        content.add(sections);


        JButton add = new JButton("+");


        add.addActionListener(e -> defaultListModel.addElement(new Section()));


        sections.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if(isSelected) {
                value.select();
                selected = value;
            } else value.unselect();
            return value;
        });
        content.add(add);

        setViewportView(content);

    }

    public static ImageIcon createImageIcon(Color color, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, width, height);
        return new ImageIcon(image);
    }

    private class Section extends JPanel {
        private final JSpinner weight = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        private final JLabel c = new JLabel();

        private Color color = Color.BLUE;

        public void setColor(Color color) {
            this.color = color;
            c.setIcon(createImageIcon(color, 20, 20));
        }

        public Color getColor() {
            return color;
        }

        public Section() {
            JButton delete = new JButton("Delete");
            delete.addActionListener(e -> sections.remove(this));

            c.setIcon(createImageIcon(color, 20, 20));
            add(c);
            add(weight);
            add(delete);
        }

        public void select() {
            setBorder(new LineBorder(sections.getSelectionBackground(), 2));
        }

        public void unselect() {
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }
    }

}
