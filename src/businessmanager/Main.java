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
import com.google.inject.Guice;
import com.google.inject.Injector;
import di.GuiceBindingModule;
import services.TestDataService;

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
        
        Injector injector = Guice.createInjector(new GuiceBindingModule());
        
        ControlCenter control = injector.getInstance(ControlCenter.class);
        
        TestDataService.populateTestData();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    control.setVisible(true);                
                }catch(java.lang.UnsupportedClassVersionError e){
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Nevitium encountered: Unsupported Class Version error. Try updating Java.");
                }catch(Exception e) {//consume the linux crap ????
                }                 
            }
        });
    }
    
    private static void setLookAndFeel() throws Exception {        
        // https://www.formdev.com/flatlaf/themes/#intellij_themes_pack
        //      Documentation on how to create custom .json themes
        // https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes
        //      Documentation for LeF themes...just new-up the class you want
        //javax.swing.UIManager.setLookAndFeel(new FlatDarkPurpleIJTheme());
        javax.swing.UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
        //javax.swing.UIManager.setLookAndFeel(new FlatHighContrastIJTheme());
    }
}
