package com.datavirtue.nevitium.models.settings;

import com.lowagie.text.Font;
import java.awt.Point;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 
 */
@Getter @Setter
public class CheckSettings {
   
    private Point checkDate = new Point(490, 65);
    private Point payee = new Point(65, 90);
    private Point amount = new Point(495, 105);
    private Point spelling = new Point(26, 142);
    private Point memo = new Point(51, 206);
    private Point signature = new Point(380, 145);
    private boolean useDefaultCheckSettings = true;
    private boolean printSignatureOnChecks = false;
    
    private FontSetting payeeFont = new FontSetting("helvetica", Font.NORMAL, 12);
    private FontSetting checkFont= new FontSetting("helvetica", Font.NORMAL, 12);
    
    private Base64EncodedImage base64EncodedImage;    
    
}
