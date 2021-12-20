/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.ui.util;

import java.text.NumberFormat;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Data Virtue
 */
public class DecimalCellRenderer extends DefaultTableCellRenderer {

    public DecimalCellRenderer(int integer, int fraction, int align){
        this.integer = integer;
        this.fraction = fraction;
        this.align = align;
    }

    protected void setValue(Object value){

        if (value != null && value instanceof Number){
            formatter.setMaximumIntegerDigits(integer);
            formatter.setMaximumFractionDigits(fraction);
            formatter.setMinimumFractionDigits(2);
            setText(formatter.format(((Number)value).floatValue()));

        }else {
            super.setValue(value);
        }
        this.setHorizontalAlignment(align);


    }

    protected int integer;
    protected int fraction;
    protected int align;
    protected static NumberFormat formatter = NumberFormat.getInstance();

}
