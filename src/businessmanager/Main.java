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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import datavirtue.DV;
import com.jtattoo.plaf.aero.AeroLookAndFeel;
import com.jtattoo.plaf.aluminium.AluminiumLookAndFeel;
import com.jtattoo.plaf.bernstein.BernsteinLookAndFeel;
import com.jtattoo.plaf.fast.FastLookAndFeel;
import com.jtattoo.plaf.graphite.GraphiteLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.luna.LunaLookAndFeel;
import com.jtattoo.plaf.mcwin.McWinLookAndFeel;
import com.jtattoo.plaf.mint.MintLookAndFeel;
import com.jtattoo.plaf.noire.NoireLookAndFeel;
import com.jtattoo.plaf.smart.SmartLookAndFeel;
import di.GuiceBindingModule;
import java.util.Properties;
import models.Inventory;
import services.DatabaseService;
import services.InventoryService;

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
        Injector injector = Guice.createInjector(new GuiceBindingModule());
        
        ControlCenter control = injector.getInstance(ControlCenter.class);
        
        InventoryService inventoryService = injector.getInstance(InventoryService.class);
        DatabaseService.createTables();
        var inventory = new Inventory();
        inventory.setDescription("Battlestar Galactica: Miniseries");
        inventory.setCode("025192792823");
        inventory.setQuantity(10.00);
        inventory.setCost(7.69);
        inventory.setPrice(14.99);
        inventory.setCategory("DVD - SciFi");          
        inventoryService.save(inventory);

        inventory = new Inventory();
        inventory.setDescription("Star Wars");
        inventory.setCode("045892797824");
        inventory.setQuantity(10.00);
        inventory.setCost(8.69);
        inventory.setPrice(17.99);
        inventory.setCategory("DVD - SciFi");          
        inventoryService.save(inventory);
        
        inventory = new Inventory();
        inventory.setDescription("Gaming Keyboard");
        inventory.setCode("015692731899");
        inventory.setQuantity(10.00);
        inventory.setCost(58.76);
        inventory.setPrice(199.99);
        inventory.setCategory("USB - GAMING");          
        inventoryService.save(inventory);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    
                    String os = System.getProperty("os.name").toLowerCase();
                                           
                    setLookAndFeel();
                    control.setVisible(true);
                
                }catch(java.lang.UnsupportedClassVersionError e){
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Nevitium encountered: Unsupported Class Version error. Try updating Java.");
                }catch(Exception e) {//consume the linux crap out
                }   
                
            }
        });
    }


    private static void setLookAndFeel() throws Exception {

        //com.jtattoo.plaf.acryl.AcrylLookAndFeel
        //com.jtattoo.plaf.aero.AeroLookAndFeel
        //com.jtattoo.plaf.aluminium.AluminiumLookAndFeel
        //com.jtattoo.plaf.bernstein.BernsteinLookAndFeel
        //com.jtattoo.plaf.fast.FastLookAndFeel
        //com.jtattoo.plaf.graphite.GraphiteLookAndFeel
        //com.jtattoo.plaf.hifi.HiFiLookAndFeel
        //com.jtattoo.plaf.luna.LunaLookAndFeel
        //com.jtattoo.plaf.mcwin.McWinLookAndFeel
        //com.jtattoo.plaf.mint.MintLookAndFeel
        //com.jtattoo.plaf.noire.NoireLookAndFeel
        //com.jtattoo.plaf.smart.SmartLookAndFeel


        Properties props = new Properties();
        props.put("logoString", "Nevitium 1.5.9");
        props.put("licenseKey", "9da3-xq85-p1ft-guz5");
        
        String LAF = DV.readFile("theme.ini");
        LAF = LAF.trim();
        
        if (LAF.equals("com.jtattoo.plaf.acryl.AcrylLookAndFeel")) AcrylLookAndFeel.setCurrentTheme(props);;
        if (LAF.equals("com.jtattoo.plaf.aero.AeroLookAndFeel")) AeroLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel")) AluminiumLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel")) BernsteinLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.fast.FastLookAndFeel")) FastLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.graphite.GraphiteLookAndFeel")) GraphiteLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) HiFiLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.luna.LunaLookAndFeel")) LunaLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.mcwin.McWinLookAndFeel")) McWinLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.mint.MintLookAndFeel")) MintLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.noire.NoireLookAndFeel")) NoireLookAndFeel.setCurrentTheme(props);
        if (LAF.equals("com.jtattoo.plaf.smart.SmartLookAndFeel"))  SmartLookAndFeel.setCurrentTheme(props);

        if (LAF==null || LAF.equals("")) LAF = "DEFAULT";
        try {
            if (LAF.equals("DEFAULT")){
                return;
                //don't set any look and feel
            }else {
                System.out.println(LAF);
                javax.swing.UIManager.setLookAndFeel(LAF);
            }

        }catch (Exception ex){
            DV.writeFile("theme.ini", "DEFAULT", false);
           ex.printStackTrace();
            /*Contimue with default theme */
        }

    }



}
