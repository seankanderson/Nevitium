/*
 * LimitedDocument.java
 *
 * Created on July 9, 2007, 10:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.ui.util;
import javax.swing.text.*;

/**
 *
 * @author Data Virtue
 */
public class LimitedDocument extends PlainDocument {
    private int char_limit = 0;
    
    
    /** Creates a new instance of LimitedDocument */
    public LimitedDocument(int char_limit) {
        
        this.char_limit = char_limit;
        
    }
    
    public void insertString (int offset, String s, AttributeSet attributeSet) throws BadLocationException{
        
        if (this.getLength() + s.length() > char_limit){
            
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }
        else {
            
            super.insertString(offset, s, attributeSet);
            
        }
        
    }
    
}
