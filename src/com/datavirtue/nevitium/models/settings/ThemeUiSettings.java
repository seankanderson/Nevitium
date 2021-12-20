package com.datavirtue.nevitium.models.settings;

import java.awt.Font;
import javax.swing.JLabel;

/**
 *
 * @author SeanAnderson
 */
public class ThemeUiSettings {
    private FontSetting companyFont = new FontSetting(new JLabel().getFont().getFontName(), Font.BOLD, 14);
    private ColorSetting invoiceOddLineColor = new ColorSetting();
    private ColorSetting statementOddLineColor = new ColorSetting();
    
}
