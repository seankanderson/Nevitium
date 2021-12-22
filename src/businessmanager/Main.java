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

import com.datavirtue.nevitium.ui.ControlCenter;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.datavirtue.nevitium.services.DiService;
import java.awt.Toolkit;
import java.sql.SQLException;
import javax.swing.JFrame;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.LocalSettingsService;
import com.datavirtue.nevitium.services.TestDataService;
import com.datavirtue.nevitium.ui.LocalSettingsDialog;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;

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
        LocalSettingsService.setLookAndFeel();
        var frame = new JFrame();        
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ControlCenter.class.getResource("/businessmanager/res/Orange.png")));
        frame.setVisible(true);
        var localSettingsApp = new LocalSettingsDialog(frame, true);
        localSettingsApp.display();
        frame.dispose();
        if (LocalSettingsService.getLocalAppSettings() == null) {
            System.exit(-1);
        } 
        LocalSettingsService.setLookAndFeel();
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
    
    
}
