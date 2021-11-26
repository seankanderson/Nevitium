/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package validators;

import java.math.BigDecimal;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author SeanAnderson
 */
public class DecimalPrecisionInputVerifier extends InputVerifier {

    private int precision;
    public DecimalPrecisionInputVerifier(int precision) {
        
    }
    
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        try {
            var value = new BigDecimal(text);
            return (value.scale() <= Math.abs(precision));  
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
}
