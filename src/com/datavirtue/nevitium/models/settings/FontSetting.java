package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class FontSetting {
    
    public FontSetting() {
        
    }
    public FontSetting(String name, int style, int size) {
        fontName = name;
        fontStyle = style;
        fontSize = size;
    }
    private String fontName;
    private int fontStyle;
    private int fontSize;
}
