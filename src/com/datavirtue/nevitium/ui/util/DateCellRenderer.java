
package com.datavirtue.nevitium.ui.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author SeanAnderson
 */
public class DateCellRenderer extends DefaultTableCellRenderer {
    String formatString = "MMM d, yyyy";
    
    public DateCellRenderer(String formatThingy){
        this.formatString = formatThingy;
    }

    public DateCellRenderer() {
        
    }
    
    protected void setValue(Object value){

        if (value != null && value instanceof Date){
            var formatter = new SimpleDateFormat(formatString);
            setText(formatter.format(((Date)value)));

        }else {
            super.setValue(value);
        }
        this.setHorizontalAlignment(SwingConstants.LEFT);

    }

    protected int integer;
    protected int fraction;
    protected int align;
    protected static NumberFormat formatter = NumberFormat.getInstance();

}
