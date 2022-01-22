package com.datavirtue.nevitium.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author SeanAnderson
 */
public class EnhancedTableCellRenderer extends DefaultTableCellRenderer {
    
    private JCheckBox renderer;
    
    public EnhancedTableCellRenderer() {
        renderer = new JCheckBox();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value != null && value instanceof Boolean) {
            Boolean b = (Boolean) value;
            renderer.setSelected(b);
            renderer.setOpaque(true);

            if (isSelected) {
                renderer.setForeground(table.getSelectionForeground());
                renderer.setBackground(table.getSelectionBackground());
            } else {
                Color bg = getBackground();
                renderer.setForeground(getForeground());

                // We have to create a new color object because Nimbus returns
                // a color of type DerivedColor, which behaves strange, not sure why.
                renderer.setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
            }

            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            return renderer;
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
