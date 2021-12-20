package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class ColorSetting {
    
    public ColorSetting(int r, int g, int b, int a) {
        red = r;
        green = g;
        blue = b;
        alpha = a;
    }
    public ColorSetting() {
        
    }
    private int red;
    private int green;
    private int blue;
    private int alpha;    
    
}
