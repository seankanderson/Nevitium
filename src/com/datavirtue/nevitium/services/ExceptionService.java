package com.datavirtue.nevitium.services;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author SeanAnderson
 */
public class ExceptionService {
    
    public static void showErrorDialog(Component parent, Exception e, String title) {
        var message = e != null ? e.getMessage() : "support: software@datavirtue.com";
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.OK_OPTION);
    }
    
}
