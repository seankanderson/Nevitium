/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author SeanAnderson
 */
public class ExceptionService {
    
    public static void showErrorDialog(Component parent, Exception e, String title) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), title, JOptionPane.OK_OPTION);
    }
    
}
