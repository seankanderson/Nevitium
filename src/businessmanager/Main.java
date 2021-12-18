/*
 * Main.java
 *
 * Created on June 22, 2006, 9:44 AM
 *
 * This application will contain various modules (JDialogs) that operate
 *on the various databases allowing modification and providing consolidated
 *veiws of the data to help manage a service or retail oriented business
 ** Copyright (c) Data Virtue 2006
 *
 */
package businessmanager;

import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import di.DiService;
import java.sql.SQLException;
import javax.swing.JFrame;
import services.ExceptionService;
import services.LocalSettingsService;
import services.TestDataService;
import ui.LocalSettingsDialog;

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    public static void main(String args[]) throws Exception {
        setLookAndFeel();
        var frame = new JFrame();
        frame.setVisible(true);
        var localSettingsApp = new LocalSettingsDialog(frame, true);
        localSettingsApp.display();
        frame.dispose();
        if (LocalSettingsService.getLocalAppSettings() == null) {
            System.exit(-1);
        } 
        setLookAndFeel();
        try {
            TestDataService.populateTestData();  
        }catch(SQLException e) {
            ExceptionService.showErrorDialog(frame, e, "Error accessing database");
            System.exit(-2);
        }
        var injector = DiService.getInjector();
        ControlCenter control = injector.getInstance(ControlCenter.class);
                    
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    control.setVisible(true);                
                }catch(java.lang.UnsupportedClassVersionError e){
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Nevitium encountered: Unsupported Class Version error. Try updating Java.");
                }catch(Exception e) {//consume linux exceptions
                }                 
            }
        });
    }
    
    private static void setLookAndFeel() throws Exception {        
        // https://www.formdev.com/flatlaf/themes/#intellij_themes_pack
        //      Documentation on how to create custom .json themes
        // https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes
        //      Documentation for LeF themes...just new-up the class you want
     
        if (LocalSettingsService.getLocalAppSettings() == null) {
            javax.swing.UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
            return;
        }
        
        var theme = LocalSettingsService.getLocalAppSettings().getTheme();
        
        if (theme.equals(LocalSettingsService.ARC_ORANGE_THEME)) {
             javax.swing.UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
        }else if (theme.equals(LocalSettingsService.PURPLE_DARK_THEME)) {
            javax.swing.UIManager.setLookAndFeel(new FlatDarkPurpleIJTheme());
        } else if (theme.equals(LocalSettingsService.HIGH_CONTRAST_THEME)) {
            javax.swing.UIManager.setLookAndFeel(new FlatHighContrastIJTheme());
        }
       
        
    }
}
