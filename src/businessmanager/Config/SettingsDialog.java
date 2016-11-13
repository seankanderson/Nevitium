/*
 * SettingsDialog.java
 *
 * Created on July 4, 2006, 11:38 AM
 ** Copyright (c) Data Virtue 2006
 */

package businessmanager.Config;
import EDI.EDIConfig;
import RuntimeManagement.GlobalApplicationDaemon;
import RuntimeManagement.KeyCard;
import java.io.*;
import datavirtue.*;
import java.awt.*;
import javax.swing.*;
import businessmanager.Common.*;
/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class SettingsDialog extends javax.swing.JDialog {
    
       private DbEngine db;
       private String workingPath;
       private boolean debug = false;
       private boolean safe = true;
       private GlobalApplicationDaemon application;
    /** Creates new form SettingsDialog */
    public SettingsDialog(java.awt.Frame parent, boolean modal,
            GlobalApplicationDaemon application, boolean safe, int tabIndex) {
        super(parent, modal);
        parentWin = parent;
        this.safe = safe;
        this.application = application;
        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));

        initComponents();
        //jLabel3.setVisible(false);
        
        if (!safe){
            configEDIButton.setEnabled(false);
        }
        
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        this.addWindowListener(new java.awt.event.WindowAdapter(){
	public void windowClosing(java.awt.event.WindowEvent e){

            int a = javax.swing.JOptionPane.showConfirmDialog(null, "Do you want to save any changes?","Save Settings?",  JOptionPane.YES_NO_OPTION);
            if (a == 0){
                saveSettings();

            }else {
                ((JDialog)e.getSource()).dispose();
            }

	}} );

        this.db = application.getDb();
        this.workingPath = application.getWorkingPath();
        accessKey = application.getKey_card();
        
        this.addTab(0, "My Company", "/businessmanager/res/Aha-16/enabled/Globe.png");
        this.addTab(1, "Internet  ", "/businessmanager/res/Aha-16/enabled/Address book.png");
        this.addTab(2, "Backups   ", "/businessmanager/res/Aha-16/enabled/Archive.png");
        this.addTab(3, "Security  ", "/businessmanager/res/Aha-16/enabled/Lock.png");
        this.addTab(4, "Invoice   ", "/businessmanager/res/Aha-16/enabled/Barcode scanner1.png");
        this.addTab(5, "Layouts   ", "/businessmanager/res/Aha-16/enabled/Measure.png");
        this.addTab(6, "Inventory ", "/businessmanager/res/Aha-16/enabled/Book of records.png");
        this.addTab(7, "Output    ", "/businessmanager/res/Aha-16/enabled/Documents.png");
        this.addTab(8, "Info      ", "/businessmanager/res/Aha-16/enabled/Info.png");
              
        props = new Settings (workingPath + "settings.ini");
        layoutPath.setText(workingPath+"layouts/");
        iPrefixField.setDocument(new LimitedDocument(3));
        qPrefixField.setDocument(new LimitedDocument(3));
        jTabbedPane1.setSelectedIndex(tabIndex);
        init();
    }
    
    private void addTab(int index, String title, String iconRes) {
        //jTabbedPane1.add(jTabbedPane1.getComponentAt(index));

        JLabel lbl = new JLabel(title);
        Icon icon = new ImageIcon(getClass().getResource(iconRes));
        lbl.setIcon(icon);

        // Add some spacing between text and icon, and position text to the RHS.
        lbl.setIconTextGap(5);
        lbl.setHorizontalTextPosition(JLabel.RIGHT);
        lbl.setHorizontalAlignment(JLabel.RIGHT);
        lbl.setFont(new Font("courier", 0, 12));
        //lbl.setVerticalTextPosition(SwingConstants.CENTER);
        jTabbedPane1.setTabComponentAt(index, lbl);
}

    
    private void init () {
        
    /*Float value = new Float(3.0f); 
    Float min = new Float(0.0f);
    Float max = new Float(4.0f); 
    Float step = new Float(.1f); 
    SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step); 
    paperSpinner.setModel(model);
    
    
    int fifty = model.getNumber().intValue(); */
 
        paperSpinner.getModel().setValue(80);
     
        boolean exists = (new File(workingPath + "settings.ini")).exists();
        
        if (!exists) {
        //build defaults first
           /*
            * jLabel28.setText(System.getProperty("os.name")+" "+System.getProperty("os.version")+" : "+ System.getProperty("sun.os.patch.level"));
           jLabel30.setText(System.getProperty("java.runtime.name")+ " "+ System.getProperty("java.vm.version"));
           jLabel31.setText( System.getProperty("user.dir"));
           userLabel.setText(System.getProperty("user.name"));
            *
            */
            
            String usrdir = System.getProperty("user.dir");
            
            String home = System.getProperty("user.home")+fileSep;
            String adobe = getAdobe();
                                  
            String drv = usrdir.substring( 0, usrdir.indexOf(fileSep) );
            String os = System.getProperty("os.name").toLowerCase();

            /* Windows Setup */
            if (os.contains("windows")){
                
                home = System.getProperty("user.home") + fileSep + "My Documents" + fileSep;
                
            }
            
            /* X Setup */
            if (os.contains("nix") || os.contains("nux") || os.contains("mac")){
                
                home = System.getProperty("user.home")+ fileSep;
                                
            }
            
            props.setProp("REMOTE MESSAGE", "false");
            props.setProp("SHOW TOOLBAR", "true");
            /* themes */

           String o = DV.getOS();

           props.setProp("LAF", "DEFAULT");

           if (o.equals("xp")){
                props.setProp("LAF", "DEFAULT");
           }
           if (o.equals("vista") || o.equals("7")){
                props.setProp("LAF", "DEFAULT");
           }
           if (o.equals("win")){
                props.setProp("LAF", "DEFAULT");
           }
           if (o.equals("x")){//Linux or Unix
                props.setProp("LAF", "DEFAULT");
           }
           if (o.equals("mac")){
                props.setProp("LAF", "DEFAULT");
           }

         

           if (debug) System.out.println("DV.getOS results: " + o);

           props.setProp("CO NAME", "My Company");
           props.setProp("CO ADDRESS","1515 Acorn Drive");
           props.setProp("CO CITY", "SomeCity, Ohio 45000" );
           props.setProp("CO PHONE", "513-555-7777" );
           props.setProp("CO OTHER", "");
           props.setProp("FONT", "ROMAN");
           props.setProp("FONT SIZE", "14");
           props.setProp("FONT STYLE", "1");
           
           props.setProp("ADDRESS STYLE", "US");
           props.setProp("STATUS LIGHT", "true");

           props.setProp("EMAIL SERVER", "");
           props.setProp("EMAIL PORT", "");
           props.setProp("SSL", "false");
           props.setProp("EMAIL ADDRESS", "");
           props.setProp("EMAIL USER","");
           props.setProp("EMAIL PWD", "");

           props.setProp("LOGO", "");
           props.setProp("SCREEN", "");
           
           props.setProp("DATA FOLDER", workingPath );  //important
           
           //makes adjustments for linux
           props.setProp("PAYMENT URL", "");
           props.setProp("PAYMENT WEB", "false");
           props.setProp("CC PAYMENT", "false");
           props.setProp("CHK PAYMENT", "false");

           props.setProp("REPORT FOLDER", home + "1Nevitium_Reports");
           props.setProp("QUOTE FOLDER", home + "1Nevitium_Quotes");
           props.setProp("INVOICE FOLDER", home + "1Nevitium_Invoices");
           props.setProp("ACROEXE", adobe );
           boolean desktop = false;
           if (os.contains("nix") || os.contains("nux") || os.contains("mac")){
                desktop = Desktop.isDesktopSupported();
           }
           
           props.setProp("INVOICE LAYOUT", "None");
           
           props.setProp("DESKTOP SUPPORTED", Boolean.toString(desktop));//desktop mime integration JDK6
           
           props.setProp("WATERMARK", "");
           props.setProp("PRINT WM", "false");
           
           props.setProp("BACKUP FOLDER", home );
           props.setProp("SECONDARY BACKUP", "false" );
           props.setProp("SECONDARY FOLDER", "" );
           
                     
           props.setProp("TAX1", ".07" );
           props.setProp("TAX2", ".00" );
           
           //Version 1.5
           props.setProp("TAX1NAME", "Tax1");
           props.setProp("TAX2NAME", "Tax2");

           //version 1.5.7
           props.setProp("SHOW TAX 1", "true");
           props.setProp("SHOW TAX 2", "true");
           props.setProp("VAT", "false");
           props.setProp("CASHRND", "N/A");  

           props.setProp("ROLL WIDTH", "80");
           
           props.setProp("INVOICE PREFIX", "I" );
           props.setProp("QUOTE PREFIX", "Q");
           
           props.setProp("CR RATE", ".24" );
           props.setProp("GRACE", "30");
           props.setProp("SCAN FIELD","UPC");
                    
           props.setProp("INVOICE NAME", "I N V O I C E");
           props.setProp("QUOTE NAME", "Q U O T E");

           props.setProp("INCOLOR","232,231,231");//light light grey
           props.setProp("STCOLOR","209,207,240");//light purple/blue
           
           props.setProp("INK SAVER", "false");
           props.setProp("MEASURE", "lbs");
           
           props.setProp("BILLTO","BILL TO:");
           props.setProp("CATLINE", "false");
           props.setProp("SYM", "$");
           props.setProp("PRINT ZEROS", "true");

           String iValue = "10000";
           String qValue = "10000";
           String tmp = "";
           
           if (!new File(workingPath + "encode.char").exists()){
                
               int a = javax.swing.JOptionPane.showConfirmDialog(null, "Do you need support for non-English text?","Character Encoding",  JOptionPane.YES_NO_OPTION);
                if (a == 0){
            
                    DV.writeFile(workingPath + "encode.char", "UTF", false);
            
                }else {
                    DV.writeFile(workingPath + "encode.char", "ASCII", false);
                }
                }
           do {                
               
                tmp = javax.swing.JOptionPane.showInputDialog("Please provide a starting INVOICE # between 1000 and 50000", iValue);
                
                if (tmp == null) iValue = "10000";
                else iValue = tmp.trim();
                                              
               if (DV.validIntString(iValue) && Integer.parseInt(iValue) < 50000 && Integer.parseInt(iValue) > 999) break;
               else continue;
           
           } while (true);
           
           props.setProp("NEXT NUMBER", iValue);

           do { //setup starting quote number


                tmp = javax.swing.JOptionPane.showInputDialog("Please provide a starting QUOTE # between 1000 and 50000", qValue);

                if (tmp == null) qValue = "10000";
                else qValue = tmp.trim();

               if (DV.validIntString(qValue) && Integer.parseInt(qValue) < 50000 && Integer.parseInt(iValue) > 999) break;
               else continue;

           } while (true);


           props.setProp("NEXT QUOTE", qValue);

           //Version 1.5
           props.setProp("POS", "false");
           
           props.setProp("PROCESSPAYMENT", "true"); 
           
           props.setProp("INVENTORY SEARCH", "UPC");
           props.setProp("MARKUP", "2.5");
           props.setProp("IGNOREQTY", "false");
        }   


//Load all settings   
        
        this.enumerateLayoutFiles(invoiceComboBox, "layout.invoice", props.getProp("INVOICE LAYOUT"));
        
        messageBox.setSelected(DV.parseBool(props.getProp("REMOTE MESSAGE"), false));

        showToolbar.setSelected(DV.parseBool(props.getProp("SHOW TOOLBAR"), true));
        /* Themes */
        String LAF = DV.readFile("theme.ini");
        LAF=LAF.trim();
        if (LAF.equals("com.jtattoo.plaf.acryl.AcrylLookAndFeel")) themeCombo.setSelectedItem("Acrylic");
        if (LAF.equals("com.jtattoo.plaf.aero.AeroLookAndFeel")) themeCombo.setSelectedItem("Aero");
        if (LAF.equals("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel")) themeCombo.setSelectedItem("Aluminum");
        if (LAF.equals("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel")) themeCombo.setSelectedItem("Bernstein");
        if (LAF.equals("com.jtattoo.plaf.fast.FastLookAndFeel")) themeCombo.setSelectedItem("Fast");
        if (LAF.equals("com.jtattoo.plaf.graphite.GraphiteLookAndFeel")) themeCombo.setSelectedItem("Graphite");
        if (LAF.equals("com.jtattoo.plaf.hifi.HiFiLookAndFeel")) themeCombo.setSelectedItem("HiFi");
        if (LAF.equals("com.jtattoo.plaf.luna.LunaLookAndFeel")) themeCombo.setSelectedItem("Luna");
        if (LAF.equals("com.jtattoo.plaf.mcwin.McWinLookAndFeel")) themeCombo.setSelectedItem("McWin");
        if (LAF.equals("com.jtattoo.plaf.mint.MintLookAndFeel")) themeCombo.setSelectedItem("Mint");
        if (LAF.equals("com.jtattoo.plaf.noire.NoireLookAndFeel")) themeCombo.setSelectedItem("Noire");
        if (LAF.equals("com.jtattoo.plaf.smart.SmartLookAndFeel")) themeCombo.setSelectedItem("Smart");
        if (LAF.equals("DEFAULT")) themeCombo.setSelectedItem("DEFAULT");
        
        coField.setText(props.getProp("CO NAME" ));
           addressField.setText(props.getProp("CO ADDRESS"));
           cityField.setText(props.getProp("CO CITY"));
           phoneField.setText(props.getProp("CO PHONE"));
           otherField.setText(props.getProp("CO OTHER"));
           
           String zone = props.getProp("ADDRESS STYLE");
           if (zone.equals("US")) countryCombo.setSelectedItem("US");
           if (zone.equals("CA")) countryCombo.setSelectedItem("CA");
           if (zone.equals("AU")) countryCombo.setSelectedItem("AU");
           if (zone.equals("UK")) countryCombo.setSelectedItem("UK");
           if (zone.equals("IN")) countryCombo.setSelectedItem("IN");
           if (zone.equals("ZA")) countryCombo.setSelectedItem("ZA");
           if (zone.equals("NZ")) countryCombo.setSelectedItem("NZ");
           if (zone.equals("PH")) countryCombo.setSelectedItem("PH");
           
           taxIDField.setText(props.getProp("TAXID"));
           displayTaxIDCheckBox.setSelected(Tools.getStringBool(props.getProp("DISPLAYTAXID")));
           
          /* email settings */
           smtpServer.setText(props.getProp("EMAIL SERVER"));
           smtpPortField.setText(props.getProp("EMAIL PORT"));
           SSLBox.setSelected(DV.parseBool(props.getProp("SSL"), false));
           emailAddressField.setText(props.getProp("EMAIL ADDRESS"));
           emailUserName.setText(props.getProp("EMAIL USER"));
           emailPassword.setText(props.getProp("EMAIL PWD"));


           statusBox.setSelected(Tools.getStringBool(props.getProp("STATUS LIGHT")));
           //Version 1.5
           /* Company Font  - TODO: check for parse error*/
           try {
           String fontName = props.getProp("FONT");
           int fontStyle = Integer.parseInt(props.getProp("FONT STYLE"));
           int fontSize = Integer.parseInt(props.getProp("FONT SIZE"));
           companyFont = new java.awt.Font(fontName, fontStyle, fontSize);
           //fontLabel.setFont(companyFont);
           coField.setFont(companyFont);
           }catch (Exception e){
               
               DV.writeFile("error.log", "Error parsing Font from the settings.ini file.",true);
               companyFont = new java.awt.Font("Roman", Font.PLAIN, 12);
               
           }
           logoField.setText(props.getProp("LOGO"));
           screenPicField.setText(props.getProp("SCREEN"));
           
           dataFolderField.setText(props.getProp("DATA FOLDER"));
           
           reportFolderField.setText(props.getProp("REPORT FOLDER"));
           quoteFolderField.setText(props.getProp("QUOTE FOLDER"));
           invoiceFolderField.setText(props.getProp("INVOICE FOLDER"));
          
           acroField.setText(props.getProp("ACROEXE"));
           desktopPDFBox.setSelected(DV.parseBool(props.getProp("DESKTOP SUPPORTED"), false));

           paymentField.setText(props.getProp("PAYMENT URL"));
           paymentWebBox.setSelected(Tools.getStringBool(props.getProp("WEB PAYMENT")));
           ccPaymentBox.setSelected(Tools.getStringBool(props.getProp("CC PAYMENT")));
           checkPaymentBox.setSelected(Tools.getStringBool(props.getProp("CHK PAYMENT")));
           
           watermarkField.setText(props.getProp("WATERMARK"));
           waterBox.setSelected(DV.parseBool(props.getProp("PRINT WM"), false));
           
          
           
           backupField.setText(props.getProp( "BACKUP FOLDER"));
           secondaryCheckBox.setSelected( DV.parseBool(props.getProp("SECONDARY BACKUP"), false));
           secondaryField.setText(props.getProp("SECONDARY FOLDER"));
           
           //secCheckBox.setSelected( Boolean.parseBoolean( props.getProp("SECURITY") ));
           
           tax1Field.setText(props.getProp("TAX1"));
           tax2Field.setText(props.getProp( "TAX2"));
           
           //Version 1.5
           tax1NameField.setText(props.getProp("TAX1NAME"));
           tax2NameField.setText(props.getProp("TAX2NAME"));
           
           //version 1.5.7
           showTax1Box.setSelected(DV.parseBool(props.getProp("SHOW TAX 1"),true));
           showTax2Box.setSelected(DV.parseBool(props.getProp("SHOW TAX 2"),true));
           
           String rounding = props.getProp("CASHRND");
           
           roundingCombo.setSelectedItem("N/A");
           if (rounding.equals(".05")) roundingCombo.setSelectedItem(".05");
           if (rounding.equals(".10")) roundingCombo.setSelectedItem(".10");
           
           try {
           paperSpinner.getModel().setValue(Integer.parseInt(props.getProp("ROLL WIDTH")));
           
           }catch (Exception e) {
               
               paperSpinner.getModel().setValue(80);  //Default
               
           }
           
           updateInches();
           
           iPrefixField.setText(props.getProp("INVOICE PREFIX"));
           qPrefixField.setText(props.getProp("QUOTE PREFIX"));

           crField.setText(props.getProp("CR RATE"));
           //default invoice scan field setting
           String t = props.getProp("SCAN FIELD");
           
           if (t.equals("UPC")) {
                            
               upcRadio.setSelected(true);
               //
           }else {
               
               //upcRadio.setSelected(false);
               codeRadio2.setSelected(true);
               
           }           
           
           graceField.setText(props.getProp("GRACE"));
           
           posBox.setSelected(DV.parseBool(props.getProp("POS"), false));
           
           paymentBox.setSelected(DV.parseBool(props.getProp("PROCESSPAYMENT"), true));
           
           inkCheckBox.setSelected(DV.parseBool(props.getProp("INK SAVER"), true));

           invoiceColorField.setBackground(Tools.stringToColor(props.getProp("INCOLOR")));
           stColorField.setBackground(Tools.stringToColor(props.getProp("STCOLOR")));
           
           String weight = props.getProp("MEASURE");
           if (weight.equals("lbs")) lbsRadio.setSelected(true);
           else kgsRadio.setSelected(true);
           
          
           invoiceNameField.setText(props.getProp("INVOICE NAME"));
           quoteNameField.setText(props.getProp("QUOTE NAME"));

           billToTextField.setText(props.getProp("BILLTO"));
           catLineCheckBox.setSelected(DV.parseBool(props.getProp("CATLINE"), false));
           
           String currency = props.getProp("SYM");
           //Version 1.5
           currencyField.setText(currency);
           
           boolean print_zeros = Tools.getStringBool(props.getProp("PRINT ZEROS"));
           printZerosCheckBox.setSelected(print_zeros);
           
           //default inventory search field setting
           t = props.getProp("INVENTORY SEARCH");
          
           markupField.setText(props.getProp("MARKUP"));

           quantityCheckBox.setSelected(DV.parseBool(props.getProp("IGNOREQTY"), false));
           
           jLabel28.setText(System.getProperty("os.name")+" "+System.getProperty("os.version")+" : "+ System.getProperty("sun.os.patch.level"));
           jLabel30.setText(System.getProperty("java.runtime.name")+ " "+ System.getProperty("java.vm.version"));
           jLabel31.setText( System.getProperty("user.dir"));
           userLabel.setText(System.getProperty("user.name"));
           //System.getProperty("user.home")
           //lockPicLabel.setVisible(secCheckBox.isSelected());
           
    }
       
    private void updateInches() {
        
        float inches = (Integer)paperSpinner.getModel().getValue() * 0.0393700787f;
        inchLabel.setText(DV.money(inches));
        
    }
    private void enumerateLayoutFiles(JComboBox combo, String filter, String key){
        File layoutPath = new File(workingPath+"/layouts/");
        String [] files = layoutPath.list();
        if (files == null || files.length < 1) return;
        combo.addItem("None");
        for (int i = 0; i < files.length; i++){
            if (files[i].toLowerCase().contains(filter)) combo.addItem(files[i]);
        }
        
        combo.setSelectedItem(props.getProp(key));
    }
    
    private void saveSettings () {
    
           //set Control Settings properties 

        
        
              props.setProp("SHOW TOOLBAR", Tools.getBoolString(showToolbar.isSelected()));
        
              String LAF = "Plastic3D";
              LAF = (String)themeCombo.getSelectedItem();
              props.setProp("LAF", LAF);
              String themeClass = "Acrylic";
              if (LAF.equals("Acrylic")) themeClass = "com.jtattoo.plaf.acryl.AcrylLookAndFeel";
              if (LAF.equals("Aero")) themeClass = "com.jtattoo.plaf.aero.AeroLookAndFeel";
              if (LAF.equals("Aluminum")) themeClass = "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel";
              if (LAF.equals("Bernstein")) themeClass = "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel";
              if (LAF.equals("Fast")) themeClass = "com.jtattoo.plaf.fast.FastLookAndFeel";
              if (LAF.equals("Graphite")) themeClass = "com.jtattoo.plaf.graphite.GraphiteLookAndFeel";
              if (LAF.equals("HiFi")) themeClass = "com.jtattoo.plaf.hifi.HiFiLookAndFeel ";
              if (LAF.equals("Luna")) themeClass = "com.jtattoo.plaf.luna.LunaLookAndFeel";
              if (LAF.equals("McWin")) themeClass = "com.jtattoo.plaf.mcwin.McWinLookAndFeel";
              if (LAF.equals("Mint")) themeClass = "com.jtattoo.plaf.mint.MintLookAndFeel";
              if (LAF.equals("Noire")) themeClass = "com.jtattoo.plaf.noire.NoireLookAndFeel";
              if (LAF.equals("Smart")) themeClass = "com.jtattoo.plaf.smart.SmartLookAndFeel";
              if (LAF.equals("DEFAULT")) themeClass = "DEFAULT";

             // System.out.println(themeClass);

              DV.writeFile("theme.ini", themeClass, false);

              props.setProp("REMOTE MESSAGE", Boolean.toString(messageBox.isSelected()));
              
           props.setProp("CO NAME", coField.getText());
            props.setProp("CO ADDRESS", addressField.getText());
                props.setProp("CO CITY", cityField.getText());
                props.setProp("CO OTHER", otherField.getText().trim());    
                props.setProp("CO PHONE", phoneField.getText());
                    
                /*  Co FONT  */
                companyFont = coField.getFont();
                    props.setProp("FONT", companyFont.getFamily());
                    props.setProp("FONT STYLE", Integer.toString(companyFont.getStyle()));
                    props.setProp("FONT SIZE", Integer.toString(companyFont.getSize()));

                    String zone = "US";
                    zone = (String)countryCombo.getSelectedItem();
                    props.setProp("ADDRESS STYLE", zone);
                    props.setProp("TAXID",taxIDField.getText());
                    props.setProp("DISPLAYTAXID", Tools.getBoolString(displayTaxIDCheckBox.isSelected()));
                    
                     props.setProp("EMAIL SERVER", smtpServer.getText());
                     props.setProp("EMAIL PORT", smtpPortField.getText());
                     props.setProp("SSL", Boolean.toString(SSLBox.isSelected()));
                     props.setProp("EMAIL ADDRESS", emailAddressField.getText());
                     props.setProp("EMAIL USER",emailUserName.getText());
                     props.setProp("EMAIL PWD", emailPassword.getText());
                    

                    props.setProp("STATUS LIGHT", Tools.getBoolString(statusBox.isSelected()));

                        props.setProp("LOGO", logoField.getText());
           
                        props.setProp("SCREEN", screenPicField.getText());
                        
           props.setProp("DATA FOLDER", DV.verifyPath ( dataFolderField.getText() ));
           
           props.setProp("REPORT FOLDER", DV.verifyPath ( reportFolderField.getText() ));
           props.setProp("QUOTE FOLDER", DV.verifyPath(quoteFolderField.getText()));
           props.setProp("INVOICE FOLDER", DV.verifyPath ( invoiceFolderField.getText() ));
           
           props.setProp("ACROEXE", acroField.getText());
           props.setProp("DESKTOP SUPPORTED", Boolean.toString(desktopPDFBox.isSelected()));
           props.setProp("PAYMENT URL", paymentField.getText());
           props.setProp("WEB PAYMENT", Tools.getBoolString(paymentWebBox.isSelected()));
           props.setProp("CC PAYMENT", Tools.getBoolString(ccPaymentBox.isSelected()));
           props.setProp("CHK PAYMENT", Tools.getBoolString(checkPaymentBox.isSelected()));

           props.setProp("WATERMARK", watermarkField.getText());
           props.setProp("PRINT WM", Boolean.toString(waterBox.isSelected()));
           
           
           props.setProp("BACKUP FOLDER",DV.verifyPath ( backupField.getText() ));
           props.setProp("SECONDARY BACKUP",DV.convertToString(secondaryCheckBox.isSelected()));
           props.setProp("SECONDARY FOLDER",DV.verifyPath (secondaryField.getText() ));
           
          
           props.setProp( "TAX1", tax1Field.getText());
           props.setProp( "TAX2", tax2Field.getText());
           
           //1.5.7
           props.setProp("SHOW TAX 1", DV.convertToString(showTax1Box.isSelected()));
           props.setProp("SHOW TAX 2", DV.convertToString(showTax2Box.isSelected()));
           String cashrnd = (String)roundingCombo.getSelectedItem();
           props.setProp("CASHRND", cashrnd);
           
           if (tax1NameField.getText().equals("VAT") || tax1NameField.getText().equals("GST")){
                props.setProp("VAT", "true");
           }else {
               props.setProp("VAT", "false");
           }


           props.setProp("TAX1NAME", tax1NameField.getText());
           props.setProp("TAX2NAME", tax2NameField.getText());
           
           props.setProp("ROLL WIDTH", Integer.toString((Integer)paperSpinner.getModel().getValue()));
           
           
           props.setProp("INVOICE PREFIX",DV.chop(iPrefixField.getText().trim(), 3));
           props.setProp("QUOTE PREFIX",DV.chop(qPrefixField.getText().trim(), 3));

           props.setProp( "CR RATE", crField.getText());
           
           //default invoice scan field 
           if (upcRadio.isSelected()) {
               
               props.setProp("SCAN FIELD", "UPC");
           }    
           
           if (codeRadio2.isSelected()) {
               
               props.setProp("SCAN FIELD", "CODE");
           }    
           
           if (descRadio.isSelected()) {
               
               props.setProp("SCAN FIELD", "DESC");
           }    
           
           
           props.setProp("GRACE", graceField.getText());
           
           //Version 1.5
           props.setProp("POS", DV.convertToString(posBox.isSelected()));
           
           props.setProp("PROCESSPAYMENT", DV.convertToString(paymentBox.isSelected()));
             
           props.setProp("INK SAVER", Boolean.toString(inkCheckBox.isSelected()));
           
           if (lbsRadio.isSelected()) props.setProp("MEASURE", "lbs");
           else props.setProp("MEASURE", "kgs");
              
           
           props.setProp("INVOICE NAME", invoiceNameField.getText().trim());
           props.setProp("QUOTE NAME", quoteNameField.getText().trim());
           
           props.setProp("BILLTO", billToTextField.getText());
           props.setProp("CATLINE", Tools.getBoolString(catLineCheckBox.isSelected()));
           
           String currency = currencyField.getText().trim();
           //only allow a single character for currency
           if (currency.equals("")) currency = "$";
           if (currency.length() > 3) currency = currency.substring(0,3);
           props.setProp("SYM", currency);
           
           props.setProp("INVOICE LAYOUT", (String)invoiceComboBox.getSelectedItem());           
           
           props.setProp("PRINT ZEROS", Tools.getBoolString(printZerosCheckBox.isSelected()));
           
           String inColor = Tools.colorToString(invoiceColorField.getBackground());
           String stColor = Tools.colorToString(stColorField.getBackground());
           props.setProp("INCOLOR", inColor);
           props.setProp("STCOLOR", stColor);
          
           props.setProp("MARKUP", markupField.getText());
           props.setProp("IGNOREQTY", Boolean.toString(quantityCheckBox.isSelected()));
           
           this.dispose();
       
}
   
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        buttonGroup7 = new javax.swing.ButtonGroup();
        buttonGroup8 = new javax.swing.ButtonGroup();
        addressButtonGroup = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        companyInfoPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        logoField = new javax.swing.JTextField();
        logoBrowse = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        coField = new javax.swing.JTextField();
        otherField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        cityField = new javax.swing.JTextField();
        phoneField = new javax.swing.JTextField();
        fontButton = new javax.swing.JButton();
        taxIDField = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        displayTaxIDCheckBox = new javax.swing.JCheckBox();
        jLabel54 = new javax.swing.JLabel();
        countryCombo = new javax.swing.JComboBox();
        jLabel56 = new javax.swing.JLabel();
        screenPicField = new javax.swing.JTextField();
        screenLogoBrowse = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        backupPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dataFolderField = new javax.swing.JTextField();
        backupFolderButton = new javax.swing.JButton();
        backupField = new javax.swing.JTextField();
        secondaryCheckBox = new javax.swing.JCheckBox();
        secondaryButton = new javax.swing.JButton();
        secondaryField = new javax.swing.JTextField();
        securityPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        secButton = new javax.swing.JButton();
        logButton = new javax.swing.JButton();
        statusBox = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        invoicePanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        inkCheckBox = new javax.swing.JCheckBox();
        paymentBox = new javax.swing.JCheckBox();
        jLabel26 = new javax.swing.JLabel();
        upcRadio = new javax.swing.JRadioButton();
        codeRadio2 = new javax.swing.JRadioButton();
        posBox = new javax.swing.JCheckBox();
        paperSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        inchLabel = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        descRadio = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        currencyField = new javax.swing.JTextField();
        printZerosCheckBox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        invoiceColorField = new javax.swing.JTextField();
        stColorField = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        tax1NameField = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        tax2NameField = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        invoiceNameField = new javax.swing.JTextField();
        iPrefixField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        qPrefixField = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        quoteNameField = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        billToTextField = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tax1Field = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tax2Field = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        crField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        graceField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        showTax1Box = new javax.swing.JCheckBox();
        showTax2Box = new javax.swing.JCheckBox();
        jLabel62 = new javax.swing.JLabel();
        roundingCombo = new javax.swing.JComboBox();
        layoutPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        layoutPathLabel = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        invoiceComboBox = new javax.swing.JComboBox();
        layoutPath = new javax.swing.JTextField();
        inventoryPanel = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        markupField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        lbsRadio = new javax.swing.JRadioButton();
        kgsRadio = new javax.swing.JRadioButton();
        quantityCheckBox = new javax.swing.JCheckBox();
        catLineCheckBox = new javax.swing.JCheckBox();
        outputPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        invoiceButton = new javax.swing.JButton();
        invoiceFolderField = new javax.swing.JTextField();
        quoteButton = new javax.swing.JButton();
        quoteFolderField = new javax.swing.JTextField();
        reportButton = new javax.swing.JButton();
        reportFolderField = new javax.swing.JTextField();
        watermarkBrowse = new javax.swing.JButton();
        watermarkField = new javax.swing.JTextField();
        waterBox = new javax.swing.JCheckBox();
        jLabel43 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        paymentField = new javax.swing.JTextField();
        paymentWebBox = new javax.swing.JCheckBox();
        paymentBrowseButton = new javax.swing.JButton();
        jLabel53 = new javax.swing.JLabel();
        ccPaymentBox = new javax.swing.JCheckBox();
        checkPaymentBox = new javax.swing.JCheckBox();
        jLabel47 = new javax.swing.JLabel();
        acroField = new javax.swing.JTextField();
        acroBrowseButton = new javax.swing.JButton();
        pdfAutoFindButton = new javax.swing.JButton();
        pdfRevertButton = new javax.swing.JButton();
        desktopPDFBox = new javax.swing.JCheckBox();
        infoPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        checkUpdatesButton = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        themeCombo = new javax.swing.JComboBox();
        jLabel63 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        showToolbar = new javax.swing.JCheckBox();
        EDIPanel = new javax.swing.JPanel();
        messageBox = new javax.swing.JCheckBox();
        jPanel19 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        smtpServer = new javax.swing.JTextField();
        emailAddressField = new javax.swing.JTextField();
        emailUserName = new javax.swing.JTextField();
        emailPassword = new javax.swing.JPasswordField();
        testEmailButton = new javax.swing.JButton();
        jLabel60 = new javax.swing.JLabel();
        smtpPortField = new javax.swing.JTextField();
        SSLBox = new javax.swing.JCheckBox();
        jLabel35 = new javax.swing.JLabel();
        configEDIButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jLabel50 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nevitium Settings");
        setIconImage(winIcon);
        setResizable(false);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("This logo appears on your invoices.  The above fields will not appear on invoices if a logo is used.");

        logoField.setToolTipText("Maximum optimal size of 300x200 pixels.");

        logoBrowse.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        logoBrowse.setText("Browse File");
        logoBrowse.setToolTipText("Press to select a logo file.");
        logoBrowse.setMargin(new java.awt.Insets(1, 4, 1, 4));
        logoBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoBrowseActionPerformed(evt);
            }
        });

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(" My Address "));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Co Name");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Address");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Phone");

        fontButton.setText("Font");
        fontButton.setToolTipText("Set the font formatting for invoices.");
        fontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontButtonActionPerformed(evt);
            }
        });

        taxIDField.setToolTipText("This is used to store and display a Tax ID on invoices for compliance.");

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel39.setText("Tax ID");

        displayTaxIDCheckBox.setText("Tax ID on Invoice");

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel54.setText("Address Format:");

        countryCombo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        countryCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "US", "CA", "AU", "UK", "ZA", "IN", "NZ", "PH" }));
        countryCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countryComboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel39, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(countryCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel54))
                    .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, cityField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel17Layout.createSequentialGroup()
                            .add(phoneField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 149, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(fontButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, coField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, addressField)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, otherField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel17Layout.createSequentialGroup()
                            .add(taxIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(displayTaxIDCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(coField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(otherField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cityField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(phoneField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(fontButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(taxIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel39)
                    .add(displayTaxIDCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel54)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(countryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/RRZEglobe.png"))); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 265, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel56, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addContainerGap())
        );

        screenLogoBrowse.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        screenLogoBrowse.setText("Browse File");
        screenLogoBrowse.setMargin(new java.awt.Insets(1, 4, 1, 4));
        screenLogoBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                screenLogoBrowseActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("This picture appears on the main screen.");
        jLabel34.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("You may use GIF, JPG or PNG files for these images only.");

        org.jdesktop.layout.GroupLayout companyInfoPanelLayout = new org.jdesktop.layout.GroupLayout(companyInfoPanel);
        companyInfoPanel.setLayout(companyInfoPanelLayout);
        companyInfoPanelLayout.setHorizontalGroup(
            companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, companyInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, companyInfoPanelLayout.createSequentialGroup()
                        .add(logoBrowse)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(logoField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, companyInfoPanelLayout.createSequentialGroup()
                        .add(screenLogoBrowse)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(screenPicField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                            .add(jLabel44, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, companyInfoPanelLayout.createSequentialGroup()
                        .add(90, 90, 90)
                        .add(companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 504, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        companyInfoPanelLayout.setVerticalGroup(
            companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, companyInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(logoBrowse)
                    .add(logoField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel34)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(companyInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(screenLogoBrowse)
                    .add(screenPicField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel44)
                .addContainerGap())
        );

        jTabbedPane1.addTab("My Company", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Globe.png")), companyInfoPanel); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(" Backups "));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Data Folder");

        dataFolderField.setEditable(false);

        backupFolderButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        backupFolderButton.setText("Backup Folder");
        backupFolderButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        backupFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backupFolderButtonActionPerformed(evt);
            }
        });

        backupField.setToolTipText("This folder will be presented as the primary backup folder.");

        secondaryCheckBox.setText("Secondary Backup");
        secondaryCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        secondaryCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secondaryCheckBoxActionPerformed(evt);
            }
        });

        secondaryButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        secondaryButton.setText("Second Folder");
        secondaryButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        secondaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secondaryButtonActionPerformed(evt);
            }
        });

        secondaryField.setToolTipText("Nevitium will always attempt to backup here without prompting.");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(backupFolderButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(secondaryButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dataFolderField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                    .add(secondaryCheckBox)
                    .add(secondaryField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                    .add(backupField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(dataFolderField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(backupFolderButton)
                    .add(backupField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(secondaryCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(secondaryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(secondaryButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout backupPanelLayout = new org.jdesktop.layout.GroupLayout(backupPanel);
        backupPanel.setLayout(backupPanelLayout);
        backupPanelLayout.setHorizontalGroup(
            backupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(backupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        backupPanelLayout.setVerticalGroup(
            backupPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(backupPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(330, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Backups", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Archive.png")), backupPanel); // NOI18N

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        secButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/onebit_25.gif"))); // NOI18N
        secButton.setText("Manage Users");
        secButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        secButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        secButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secButtonActionPerformed(evt);
            }
        });

        logButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Information.png"))); // NOI18N
        logButton.setText("View Log");
        logButton.setEnabled(false);
        logButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        logButton.setMaximumSize(new java.awt.Dimension(135, 41));
        logButton.setMinimumSize(new java.awt.Dimension(135, 41));
        logButton.setPreferredSize(new java.awt.Dimension(139, 57));

        statusBox.setSelected(true);
        statusBox.setText("Show status icon on the main screen");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setText("Data Integrity");

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(jSeparator5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, logButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, secButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                            .add(jLabel17)
                            .add(statusBox))
                        .add(239, 239, 239))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(secButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(logButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 37, Short.MAX_VALUE)
                .add(jLabel17)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(statusBox)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout securityPanelLayout = new org.jdesktop.layout.GroupLayout(securityPanel);
        securityPanel.setLayout(securityPanelLayout);
        securityPanelLayout.setHorizontalGroup(
            securityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(securityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        securityPanelLayout.setVerticalGroup(
            securityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(securityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Security", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Lock.png")), securityPanel); // NOI18N

        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        inkCheckBox.setText("Ink Saver");
        inkCheckBox.setToolTipText("Ink Saver prevents color on documents except for logo.");
        inkCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        paymentBox.setText("Process Payment When Posting");
        paymentBox.setToolTipText("Auto selects the Take Payment check box on the invoice.");
        paymentBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jLabel26.setText("Default Scan Field:");

        buttonGroup2.add(upcRadio);
        upcRadio.setSelected(true);
        upcRadio.setText("UPC");
        upcRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        buttonGroup2.add(codeRadio2);
        codeRadio2.setText("Code");
        codeRadio2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        posBox.setText("POS Mode");
        posBox.setToolTipText("Assumes the receipt printer is your default printer and changes the invoice screen.");
        posBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        paperSpinner.setFont(new java.awt.Font("Courier", 0, 14)); // NOI18N
        paperSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                paperSpinnerStateChanged(evt);
            }
        });

        jLabel1.setText("Paper Roll Width (mm):");

        inchLabel.setText("3.15");

        jLabel49.setText("Inches");

        buttonGroup2.add(descRadio);
        descRadio.setText("Desc");
        descRadio.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(inkCheckBox)
                    .add(paymentBox)
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(paperSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(inchLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel49))
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(jLabel26)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(upcRadio)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(codeRadio2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(descRadio))
                    .add(posBox))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(inkCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(paymentBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26)
                    .add(upcRadio)
                    .add(codeRadio2)
                    .add(descRadio))
                .add(22, 22, 22)
                .add(posBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(paperSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(inchLabel)
                    .add(jLabel49))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel40.setText("Currency Symbol: ");

        currencyField.setToolTipText("Select your currency symbol from the Chracter Map (Windows)");

        printZerosCheckBox.setText("Print Zeros");
        printZerosCheckBox.setToolTipText("Enabling this causes zero amounts to be printed for invoice items.");
        printZerosCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printZerosCheckBoxActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(" Document Colors "));

        invoiceColorField.setText("Invoice Color");
        invoiceColorField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invoiceColorFieldMouseClicked(evt);
            }
        });

        stColorField.setText("Statement Color");
        stColorField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stColorFieldMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, stColorField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, invoiceColorField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(invoiceColorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 9, Short.MAX_VALUE)
                .add(stColorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12Layout.createSequentialGroup()
                        .add(jLabel40)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(currencyField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(printZerosCheckBox)))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 54, Short.MAX_VALUE)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel40)
                    .add(currencyField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(printZerosCheckBox))
                .addContainerGap())
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(" Customizations "));

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Tax1 Name");

        tax1NameField.setToolTipText("Limit to three or four characters");

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Tax2 Name");

        tax2NameField.setToolTipText("Limit to three or four characters");

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel45.setText("Invoice Name");

        iPrefixField.setToolTipText("Invoice numbers are stored with a max 8 characters this is included");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Invoice Prefix");

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Quote Prefix");

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel55.setText("Quote Name");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Bill To Label");

        billToTextField.setText("BILL TO:");

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel33, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel45, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel51, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .add(jLabel46, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .add(jLabel55, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .add(jLabel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(iPrefixField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(invoiceNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tax2NameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(tax1NameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(qPrefixField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(quoteNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(billToTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel33)
                    .add(tax1NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tax2NameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel51))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(invoiceNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel45))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(iPrefixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .add(8, 8, 8)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(quoteNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel55))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(qPrefixField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel46))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(billToTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel20))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(" Taxes & Interest "));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tax 1 Rate");

        tax1Field.setToolTipText("Example: .06 = 6%");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Tax 2 Rate");

        tax2Field.setToolTipText("Example: .06 = 6%");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("(Interest Rate");

        crField.setToolTipText("Simple Interest");

        jLabel25.setText("/ 365) * #days past the grace period * Balance");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Grace Period");

        graceField.setToolTipText("Amount of days before interest is charged on balances.");

        jLabel24.setText("Days");

        showTax1Box.setText("Show Tax 1");

        showTax2Box.setText("Show Tax 2");

        jLabel62.setText("Round cash sales to nearest:");

        roundingCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "N/A", ".05", ".10" }));
        roundingCombo.setToolTipText("Takes affect when applying a cash payment.");

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(jLabel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel62, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(graceField)
                            .add(crField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel25)
                            .add(jLabel24)))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, tax2Field)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, tax1Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(showTax2Box)
                            .add(showTax1Box)))
                    .add(roundingCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tax1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(showTax1Box))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(tax2Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(showTax2Box))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25)
                    .add(crField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel24)
                    .add(graceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel23))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel62)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(roundingCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout invoicePanelLayout = new org.jdesktop.layout.GroupLayout(invoicePanel);
        invoicePanel.setLayout(invoicePanelLayout);
        invoicePanelLayout.setHorizontalGroup(
            invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(invoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(invoicePanelLayout.createSequentialGroup()
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(invoicePanelLayout.createSequentialGroup()
                        .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        invoicePanelLayout.setVerticalGroup(
            invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(invoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Invoice", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Order form.png")), invoicePanel); // NOI18N

        layoutPathLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        layoutPathLabel.setText("Layout Path");

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel61.setText("Invoice Layout");

        invoiceComboBox.setToolTipText("Selct a custom layout for the invoices.");
        invoiceComboBox.setEnabled(false);

        layoutPath.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel61, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layoutPathLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(invoiceComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layoutPath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(layoutPathLabel)
                    .add(layoutPath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(invoiceComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel61))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layoutPanelLayout = new org.jdesktop.layout.GroupLayout(layoutPanel);
        layoutPanel.setLayout(layoutPanelLayout);
        layoutPanelLayout.setHorizontalGroup(
            layoutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layoutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layoutPanelLayout.setVerticalGroup(
            layoutPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layoutPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(400, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Layouts", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Measure.png")), layoutPanel); // NOI18N

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel21.setText("Default Markup:");

        markupField.setFont(new java.awt.Font("OCR B MT", 0, 14)); // NOI18N
        markupField.setText("2.7");
        markupField.setToolTipText("Standard markup for auto calculating retail price.");

        jLabel22.setText("Points");

        jLabel41.setText("Weight Unit: ");

        buttonGroup7.add(lbsRadio);
        lbsRadio.setText("lbs");
        lbsRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        buttonGroup7.add(kgsRadio);
        kgsRadio.setText("kgs");
        kgsRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        quantityCheckBox.setText("Ignore Quantity Warnings");
        quantityCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityCheckBoxActionPerformed(evt);
            }
        });

        catLineCheckBox.setText("Add category line to invoice items");
        catLineCheckBox.setToolTipText("When using Nevitium as an inventory deployment tracker.");

        org.jdesktop.layout.GroupLayout jPanel15Layout = new org.jdesktop.layout.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel15Layout.createSequentialGroup()
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel15Layout.createSequentialGroup()
                                .add(jLabel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(markupField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel22)
                                .add(26, 26, 26))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel15Layout.createSequentialGroup()
                                .add(jLabel41)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(kgsRadio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(lbsRadio)))
                        .add(508, 508, 508))
                    .add(jPanel15Layout.createSequentialGroup()
                        .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, catLineCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, quantityCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel21)
                    .add(markupField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel22))
                .add(18, 18, 18)
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbsRadio)
                    .add(kgsRadio)
                    .add(jLabel41))
                .add(18, 18, 18)
                .add(quantityCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(catLineCheckBox)
                .addContainerGap(329, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout inventoryPanelLayout = new org.jdesktop.layout.GroupLayout(inventoryPanel);
        inventoryPanel.setLayout(inventoryPanelLayout);
        inventoryPanelLayout.setHorizontalGroup(
            inventoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(inventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        inventoryPanelLayout.setVerticalGroup(
            inventoryPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(inventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Inventory", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Book of records.png")), inventoryPanel); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(" Output "));

        invoiceButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        invoiceButton.setText("Invoice Folder");
        invoiceButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });

        invoiceFolderField.setToolTipText("this is where the Invoices are sent (.pdf)");
        invoiceFolderField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceFolderFieldActionPerformed(evt);
            }
        });

        quoteButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        quoteButton.setText("Quote Folder");
        quoteButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        quoteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quoteButtonActionPerformed(evt);
            }
        });

        reportButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        reportButton.setText("Report Folder");
        reportButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        reportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });

        reportFolderField.setToolTipText("This is where the reports are saved (.pdf)");

        watermarkBrowse.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        watermarkBrowse.setText("Watermark");
        watermarkBrowse.setMargin(new java.awt.Insets(1, 1, 1, 1));
        watermarkBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                watermarkBrowseActionPerformed(evt);
            }
        });

        watermarkField.setToolTipText("Leave this blank to remove watermark from reports");

        waterBox.setText("Use Watermark in Reports");
        waterBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel43.setText("(JPG, BMP, GIF, PNG)");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(quoteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(reportButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, invoiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, watermarkBrowse, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(waterBox)
                        .add(18, 18, 18)
                        .add(jLabel43))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, watermarkField)
                        .add(reportFolderField)
                        .add(quoteFolderField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                        .add(invoiceFolderField)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(invoiceButton)
                    .add(invoiceFolderField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(quoteButton)
                    .add(quoteFolderField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(7, 7, 7)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(reportButton)
                    .add(reportFolderField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(watermarkBrowse)
                    .add(watermarkField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(waterBox)
                    .add(jLabel43))
                .add(16, 16, 16))
        );

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("Pmt Sys:");

        paymentField.setToolTipText("Link to an application OR a website to process payments (Ex. PC Charge Pro)");
        paymentField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                paymentFieldMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                paymentFieldMouseExited(evt);
            }
        });
        paymentField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                paymentFieldFocusLost(evt);
            }
        });
        paymentField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                paymentFieldKeyTyped(evt);
            }
        });

        paymentWebBox.setText("Web Based");
        paymentWebBox.setToolTipText("Launches the URL in your web browser");
        paymentWebBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentWebBoxActionPerformed(evt);
            }
        });

        paymentBrowseButton.setText("Browse");
        paymentBrowseButton.setToolTipText("Browse to a payment application on your system");
        paymentBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentBrowseButtonActionPerformed(evt);
            }
        });

        jLabel53.setText("Use For:");

        ccPaymentBox.setText("Credit/Debit Card - EFT");
        ccPaymentBox.setToolTipText("Enable to automatically select the payment system check box in the payment window");

        checkPaymentBox.setText("Check");
        checkPaymentBox.setToolTipText("Enable to automatically select the payment system check box in the payment window");
        checkPaymentBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkPaymentBoxActionPerformed(evt);
            }
        });

        jLabel47.setText("PDF Reader: ");

        acroField.setText("C:\\Program Files\\Adobe\\");
            acroField.setToolTipText("You need to specify this to View reports.");

            acroBrowseButton.setText("Browse");
            acroBrowseButton.setToolTipText("Browse and select a PDF reader program.");
            acroBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    acroBrowseButtonActionPerformed(evt);
                }
            });

            pdfAutoFindButton.setText("Auto Find");
            pdfAutoFindButton.setToolTipText("Works for Windows and some versions of Linux only.");
            pdfAutoFindButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
            pdfAutoFindButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    pdfAutoFindButtonActionPerformed(evt);
                }
            });

            pdfRevertButton.setText("Revert");
            pdfRevertButton.setToolTipText("Reverts to previous path after clicking browse or Auto Find.");
            pdfRevertButton.setEnabled(false);
            pdfRevertButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
            pdfRevertButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    pdfRevertButtonActionPerformed(evt);
                }
            });

            desktopPDFBox.setText("Use System Default");
            desktopPDFBox.setToolTipText("Nevitium will use the default application associated with pdf files.");
            desktopPDFBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    desktopPDFBoxActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel52, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .add(jLabel47))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                            .add(acroBrowseButton)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pdfAutoFindButton)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(pdfRevertButton)
                            .add(18, 18, 18)
                            .add(desktopPDFBox))
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, acroField)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                                .add(paymentBrowseButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel53)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(ccPaymentBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(checkPaymentBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(paymentWebBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, paymentField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 598, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(25, Short.MAX_VALUE)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel47)
                        .add(acroField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(acroBrowseButton)
                                .add(pdfAutoFindButton)
                                .add(pdfRevertButton)
                                .add(desktopPDFBox))
                            .add(10, 10, 10)
                            .add(paymentField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                            .add(51, 51, 51)
                            .add(jLabel52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(paymentBrowseButton)
                        .add(jLabel53)
                        .add(ccPaymentBox)
                        .add(checkPaymentBox)
                        .add(paymentWebBox))
                    .addContainerGap())
            );

            org.jdesktop.layout.GroupLayout outputPanelLayout = new org.jdesktop.layout.GroupLayout(outputPanel);
            outputPanel.setLayout(outputPanelLayout);
            outputPanelLayout.setHorizontalGroup(
                outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );
            outputPanelLayout.setVerticalGroup(
                outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(18, 18, 18)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(73, 73, 73))
            );

            jTabbedPane1.addTab("Output", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Documents.png")), outputPanel); // NOI18N

            jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
            jLabel11.setText("Nevitium Invoice Manager 1.5.8.7 HotBuild Maint-Release - 01 July 2013");

            jLabel12.setForeground(new java.awt.Color(0, 51, 255));
            jLabel12.setText("www.datavirtue.com");
            jLabel12.setToolTipText("Click this link to visit datavirtue.com");
            jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jLabel12MouseClicked(evt);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    jLabel12MouseEntered(evt);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    jLabel12MouseExited(evt);
                }
            });

            jLabel13.setForeground(new java.awt.Color(0, 51, 255));
            jLabel13.setText("software@datavirtue.com");
            jLabel13.setToolTipText("Click this link to email Data Virtue");
            jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jLabel13MouseClicked(evt);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    jLabel13MouseEntered(evt);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    jLabel13MouseExited(evt);
                }
            });

            jLabel14.setText("Copyright Data Virtue 2007-2013 - All Rights Reserved.");

            jLabel15.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
            jLabel15.setText("Developed with Java SE 1.6 & NetBeans IDE 7.2");

            jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
            jLabel3.setText("Updates:");

            jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
            jLabel48.setText("Support: ");

            checkUpdatesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/RRZE/synchronized.png"))); // NOI18N
            checkUpdatesButton.setText("Check For Updates");
            checkUpdatesButton.setToolTipText("Tells you if there are new updates for Nevitium.");
            checkUpdatesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    checkUpdatesButtonActionPerformed(evt);
                }
            });

            jLabel18.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
            jLabel18.setText("Select Theme:");

            themeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Acrylic", "Aero ", "Aluminum", "Bernstein", "Fast", "Graphite", "HiFi", "Luna", "McWin", "Mint", "Noire", "Smart", "DEFAULT" }));
            themeCombo.setToolTipText("If you have problems after switching themes you need to restart Nevitium");

            jLabel63.setText("Restart Nevitium after changing themes.");

            jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(" My System "));

            jLabel27.setText("Operating System:");

            jLabel28.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
            jLabel28.setText("jLabel28");

            jLabel29.setText("Java Version:");

            jLabel30.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
            jLabel30.setText("jLabel30");

            jLabel32.setText("Working Folder:");

            jLabel31.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
            jLabel31.setText("jLabel31");

            jLabel38.setText("User: ");

            userLabel.setText("User Info");

            org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
            jPanel6.setLayout(jPanel6Layout);
            jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel6Layout.createSequentialGroup()
                            .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jPanel6Layout.createSequentialGroup()
                                    .add(jLabel27)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(jLabel28))
                                .add(jPanel6Layout.createSequentialGroup()
                                    .add(jLabel29)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(jLabel30))
                                .add(jPanel6Layout.createSequentialGroup()
                                    .add(jLabel32)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(jLabel31)))
                            .addContainerGap(554, Short.MAX_VALUE))
                        .add(jPanel6Layout.createSequentialGroup()
                            .add(jLabel38)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(userLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                            .add(570, 570, 570))))
            );
            jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel27)
                        .add(jLabel28))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel29)
                        .add(jLabel30))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel32)
                        .add(jLabel31))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel38)
                        .add(userLabel))
                    .addContainerGap(15, Short.MAX_VALUE))
            );

            jLabel4.setText("Show Toolbar:");

            showToolbar.setSelected(true);

            org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
            jPanel7.setLayout(jPanel7Layout);
            jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel14)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                        .add(checkUpdatesButton)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7Layout.createSequentialGroup()
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(6, 6, 6)
                                .add(jLabel48)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(jPanel7Layout.createSequentialGroup()
                            .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel18, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jPanel7Layout.createSequentialGroup()
                                    .add(themeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                    .add(jLabel63, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
                                .add(showToolbar))))
                    .addContainerGap())
            );
            jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jLabel11)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel14)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel3)
                        .add(jLabel13)
                        .add(jLabel48)
                        .add(jLabel12))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel15)
                    .add(18, 18, 18)
                    .add(checkUpdatesButton)
                    .add(18, 18, 18)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel18)
                        .add(themeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel63))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel4)
                        .add(showToolbar))
                    .addContainerGap(117, Short.MAX_VALUE))
            );

            org.jdesktop.layout.GroupLayout infoPanelLayout = new org.jdesktop.layout.GroupLayout(infoPanel);
            infoPanel.setLayout(infoPanelLayout);
            infoPanelLayout.setHorizontalGroup(
                infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(infoPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );
            infoPanelLayout.setVerticalGroup(
                infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(infoPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );

            jTabbedPane1.addTab("Info", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Info.png")), infoPanel); // NOI18N

            messageBox.setText("Show Remote Message (Grabs a small message from datavirtue.com)");
            messageBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            messageBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    messageBoxActionPerformed(evt);
                }
            });

            jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(" Email Settings (Send Documents) "));

            jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel42.setText("SMTP Server:");

            jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel57.setText("My Address:");

            jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel58.setText("SMTP User Name:");

            jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel59.setText("SMTP Password:");

            smtpServer.setToolTipText("This is the mail server's address. (Port 25)");

            emailAddressField.setToolTipText("Your address known to the mail server. Must correspond to the user name and password.");

            emailUserName.setToolTipText("Your user name for your account on the mail server.");
            emailUserName.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    emailUserNameActionPerformed(evt);
                }
            });

            emailPassword.setToolTipText("The password for your email account on the mail server.");

            testEmailButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/RRZE/wifi16.png"))); // NOI18N
            testEmailButton.setText("Send Test Message");
            testEmailButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    testEmailButtonActionPerformed(evt);
                }
            });

            jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel60.setText("Port");

            smtpPortField.setText("25");

            SSLBox.setText("SSL");
            SSLBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SSLBoxActionPerformed(evt);
                }
            });

            jLabel35.setText("The test message may be rejected by your email service because of an attachment.");

            org.jdesktop.layout.GroupLayout jPanel19Layout = new org.jdesktop.layout.GroupLayout(jPanel19);
            jPanel19.setLayout(jPanel19Layout);
            jPanel19Layout.setHorizontalGroup(
                jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel19Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel59, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel58, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel57, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel42, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(emailAddressField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                        .add(emailUserName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel19Layout.createSequentialGroup()
                            .add(smtpServer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(smtpPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel19Layout.createSequentialGroup()
                            .add(emailPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(SSLBox)
                            .add(0, 0, Short.MAX_VALUE))
                        .add(jPanel19Layout.createSequentialGroup()
                            .add(testEmailButton)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel35, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
            );
            jPanel19Layout.setVerticalGroup(
                jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel19Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel42)
                        .add(smtpServer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(smtpPortField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel60))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel57)
                        .add(emailAddressField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel58)
                        .add(emailUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel59)
                        .add(emailPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(SSLBox))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                    .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(testEmailButton)
                        .add(jLabel35)))
            );

            configEDIButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Data transmission.png"))); // NOI18N
            configEDIButton.setText("Configure EDI");
            configEDIButton.setToolTipText("Configure EDI to support multiple users and/or locations.");
            configEDIButton.setEnabled(false);
            configEDIButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    configEDIButtonActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout EDIPanelLayout = new org.jdesktop.layout.GroupLayout(EDIPanel);
            EDIPanel.setLayout(EDIPanelLayout);
            EDIPanelLayout.setHorizontalGroup(
                EDIPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(EDIPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(EDIPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(messageBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                        .add(configEDIButton))
                    .addContainerGap())
            );
            EDIPanelLayout.setVerticalGroup(
                EDIPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(EDIPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(messageBox)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(jPanel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                    .add(configEDIButton)
                    .addContainerGap(230, Short.MAX_VALUE))
            );

            jTabbedPane1.addTab("Internet", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Address book.png")), EDIPanel); // NOI18N

            saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
            saveButton.setText("Close/Save");
            saveButton.setToolTipText("Click to save and exit.");
            saveButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    saveButtonActionPerformed(evt);
                }
            });

            jLabel50.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
            jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel50.setText("Questions? Contact: software@datavirtue.com");

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE)
                        .add(layout.createSequentialGroup()
                            .add(jLabel50, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
                            .add(18, 18, 18)
                            .add(saveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 496, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel50, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                        .add(saveButton))
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void checkUpdatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdatesButtonActionPerformed
    
        checkForUpdates();
        
    }//GEN-LAST:event_checkUpdatesButtonActionPerformed
private void checkForUpdates() {
        
        String localString = DV.readFile("ver.inf");
        
        String siteData="";
        
        siteData = DVNET.HTTPGetFile("http://www.datavirtue.com/nevitium/update/nevupdate.html", "Problem retreiving update status.", false);
        if (!siteData.contains("ERR:")){
            float currentVersion=0.0f;
            float localVersion=0.0f;
            try {
                
                currentVersion = Float.parseFloat(siteData);
                
            } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "There was a problem processing the remote update data. >>"+siteData);
            }
          
            
            try {
                
                localVersion = Float.parseFloat(localString);
                
            } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "There was a problem processing the local version data. >>"+localString);
            }
            
            if (localVersion < currentVersion){
                String nl = System.getProperty("line.separator");
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Your Version: "+localVersion + "     Current Release: "+currentVersion+nl+
                        "Visit datavirtue.com to download the latest version.");
                
            }else {
                
                javax.swing.JOptionPane.showMessageDialog(null,"No updates needed. Visit the website for Hot Builds.");
            }
        }   
    }
    private void paperSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_paperSpinnerStateChanged
        
        updateInches();
        
    }//GEN-LAST:event_paperSpinnerStateChanged

    private void screenLogoBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_screenLogoBrowseActionPerformed
        
        JFileChooser fileChooser = DV.getFileChooser(screenPicField.getText());
            
        File curFile = fileChooser.getSelectedFile();
         if (curFile == null) return;
        if (curFile != null ) screenPicField.setText( curFile.toString() );
        if (!curFile.toString().toLowerCase().contains(".gif")  &&
                !curFile.toString().toLowerCase().contains(".jpg") &&
                !curFile.toString().toLowerCase().contains(".png")) screenPicField.setText("");
        
        
        
    }//GEN-LAST:event_screenLogoBrowseActionPerformed

    private void stColorFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stColorFieldMouseClicked
        
        ColorChooser colorDialog = new ColorChooser(null, true);
        colorDialog.setColor(stColorField.getBackground());
        colorDialog.setVisible(true);
        stColorField.setBackground(colorDialog.getColor());
        stColorField.setText("Statement Color");
        
        colorDialog.dispose();
        
        
}//GEN-LAST:event_stColorFieldMouseClicked

    private void invoiceColorFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invoiceColorFieldMouseClicked
        
        ColorChooser colorDialog = new ColorChooser(null, true);
        colorDialog.setColor(invoiceColorField.getBackground());
        colorDialog.setVisible(true);
        invoiceColorField.setBackground(colorDialog.getColor());
        invoiceColorField.setText("Invoice Color");
        colorDialog.dispose();
        
}//GEN-LAST:event_invoiceColorFieldMouseClicked

    private void secButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secButtonActionPerformed
        if (accessKey.isMaster()){
            new businessmanager.SecurityManager(null, true, db).setVisible(true);
        }else {
            accessKey.showMessage("Security");
        }

    }//GEN-LAST:event_secButtonActionPerformed

    private void pdfRevertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdfRevertButtonActionPerformed
        
        acroField.setText(props.getProp("ACROEXE"));
        pdfRevertButton.setEnabled(false);
        
    }//GEN-LAST:event_pdfRevertButtonActionPerformed

    private void pdfAutoFindButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdfAutoFindButtonActionPerformed
        
          acroField.setText(getAdobe());  
        pdfRevertButton.setEnabled(true);
        
    }//GEN-LAST:event_pdfAutoFindButtonActionPerformed

    private String getAdobe() {
        
        String usrdir = System.getProperty("user.dir");
                      
            String adobe=".";
                                  
            String drv = usrdir.substring( 0, usrdir.indexOf(fileSep) );
            
            if (System.getProperty("os.name").contains("Windows")){
                                                
                adobe = drv + "\\Program Files\\Adobe\\";
                
                if (new File(adobe).exists()){
                
                    File ad = new File(adobe);
                
                    String [] ls = ad.list();
                if (ls.length > 1){
                    int lnk [] = DV.whichContains(ls, "Acrobat");
                
                    int w = 0;
                    String tmp;
                    float v = 1.0f;
                
                    for (int i = 0; i < lnk.length; i++){
                    
                      tmp = ls[i].substring(ls[i].length()-3);
                      
                      if (DV.validFloatString(tmp)){
                        
                            if (Float.valueOf(tmp) > v ) {
                            
                                v = Float.valueOf(tmp);
                                w = i;
                            }     
                        
                        }
                   
                    }
                                
                    if (w == 0);
                    else adobe = adobe + ls[w]+ "\\Reader\\AcroRd32.exe";
                
                }else adobe = adobe + ls[0]+ "\\Reader\\AcroRd32.exe";
                      
                }
                
            }//end Windows setup
            
            if (System.getProperty("os.name").contains("nux")){
                               
                adobe = "evince";
                                
            }
            
            return adobe;
                
        
    }
    
    
    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        DV.launchURL("http://" + jLabel12.getText());
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        
        DV.launchURL("mailto:" + jLabel13.getText());
        
    }//GEN-LAST:event_jLabel13MouseClicked

    private void watermarkBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_watermarkBrowseActionPerformed
            
        JFileChooser fileChooser = DV.getFileChooser(logoField.getText());
            
        File curFile = fileChooser.getSelectedFile();
       
         if (curFile == null) return;
        if (curFile != null ) watermarkField.setText( curFile.toString() );
        if (!curFile.toString().contains(".gif")  && !curFile.toString().contains(".jpg") && !curFile.toString().contains(".bmp")) logoField.setText("");
        
        
        
    }//GEN-LAST:event_watermarkBrowseActionPerformed

    private void logoBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoBrowseActionPerformed
            
        JFileChooser fileChooser = DV.getFileChooser(logoField.getText());
            
        File curFile = fileChooser.getSelectedFile();
         if (curFile == null) return;
        if (curFile != null ) logoField.setText( curFile.toString() );
        if (!curFile.toString().toLowerCase().contains(".gif")  &&
                !curFile.toString().toLowerCase().contains(".jpg") &&
                !curFile.toString().toLowerCase().contains(".png")) logoField.setText("");
        
        
    }//GEN-LAST:event_logoBrowseActionPerformed

    private void acroBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acroBrowseButtonActionPerformed
        
        JFileChooser fileChooser = DV.getFileChooser(acroField.getText());
            
        File curFile = fileChooser.getSelectedFile();
        if (curFile != null ) acroField.setText( curFile.toString() );
        
        pdfRevertButton.setEnabled(true);
        
    }//GEN-LAST:event_acroBrowseButtonActionPerformed

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportButtonActionPerformed
        JFileChooser fileChooser = DV.getDirChooser(reportFolderField.getText(), parentWin);
            
        File curFile = fileChooser.getSelectedFile();
        if (curFile != null ) reportFolderField.setText( DV.verifyPath(curFile.toString()) );
    }//GEN-LAST:event_reportButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        
        JFileChooser fileChooser = DV.getDirChooser(invoiceFolderField.getText(), parentWin);
            
        File curFile = fileChooser.getSelectedFile();
        if (curFile != null ) invoiceFolderField.setText( DV.verifyPath(curFile.toString()) );
        
    }//GEN-LAST:event_invoiceButtonActionPerformed
   
    
    private void secondaryCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secondaryCheckBoxActionPerformed
        updateSecondary();
    }//GEN-LAST:event_secondaryCheckBoxActionPerformed
private void updateSecondary () {
    
    secondaryField.setEnabled(secondaryCheckBox.isSelected());
    secondaryButton.setEnabled(secondaryCheckBox.isSelected());
    
}

    private void backupFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backupFolderButtonActionPerformed
        
        JFileChooser fileChooser = DV.getDirChooser(backupField.getText(), parentWin);
            
        File curFile = fileChooser.getSelectedFile();
        if (curFile != null ) backupField.setText( DV.verifyPath(curFile.toString()) );
        
    }//GEN-LAST:event_backupFolderButtonActionPerformed

    private void secondaryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secondaryButtonActionPerformed
        
        JFileChooser fileChooser = DV.getDirChooser(secondaryField.getText(), parentWin);
            File curFile = fileChooser.getSelectedFile();
            if (curFile != null ) secondaryField.setText( DV.verifyPath(curFile.toString()) );
       
        
    }//GEN-LAST:event_secondaryButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveSettings();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void paymentBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentBrowseButtonActionPerformed
        JFileChooser fileChooser = DV.getFileChooser(paymentField.getText());

        File curFile = fileChooser.getSelectedFile();
        if (curFile != null ) paymentField.setText( curFile.toString() );
        checkForApp();
       
    }//GEN-LAST:event_paymentBrowseButtonActionPerformed

    private void paymentFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_paymentFieldFocusLost
        checkForApp();
    }//GEN-LAST:event_paymentFieldFocusLost

    private void paymentFieldMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentFieldMouseEntered
        checkForApp();       
    }//GEN-LAST:event_paymentFieldMouseEntered

    private void paymentFieldMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentFieldMouseExited
        checkForApp();   
    }//GEN-LAST:event_paymentFieldMouseExited

    private void paymentWebBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentWebBoxActionPerformed
        checkForApp();
    }//GEN-LAST:event_paymentWebBoxActionPerformed

    private void paymentFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentFieldKeyTyped
        checkForApp();
    }//GEN-LAST:event_paymentFieldKeyTyped

    private void fontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontButtonActionPerformed

        FontDialog fd = new FontDialog(null,true,coField.getFont());
        fd.setVisible(true);
        java.awt.Font f = fd.getChosenFont();
        //fontLabel.setFont(f);
        coField.setFont(f);
        fd.dispose();

        /* Store these */

    }//GEN-LAST:event_fontButtonActionPerformed

    private void jLabel12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseEntered
        changeHandCursor();
    }//GEN-LAST:event_jLabel12MouseEntered

    private void jLabel13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseEntered
        changeHandCursor();
    }//GEN-LAST:event_jLabel13MouseEntered

    private void jLabel12MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseExited
        revertCursor();
    }//GEN-LAST:event_jLabel12MouseExited

    private void jLabel13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseExited
        revertCursor();
    }//GEN-LAST:event_jLabel13MouseExited

    private void quoteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quoteButtonActionPerformed
        JFileChooser fileChooser = DV.getDirChooser(quoteFolderField.getText(), parentWin);

        File curFile = fileChooser.getSelectedFile();
        if (curFile != null ) invoiceFolderField.setText( DV.verifyPath(curFile.toString()) );
    }//GEN-LAST:event_quoteButtonActionPerformed

    private void messageBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_messageBoxActionPerformed

    private void countryComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countryComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_countryComboActionPerformed

    private void emailUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailUserNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailUserNameActionPerformed

    private void testEmailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testEmailButtonActionPerformed
        NewEmail email = new NewEmail();
        email.setFrom(emailAddressField.getText());
        email.setRecipent(emailAddressField.getText());
        email.setServer(smtpServer.getText());
        email.setPort(smtpPortField.getText());
        email.setUsername(emailUserName.getText());
        email.setSSL(SSLBox.isSelected());
        email.setPassword(emailPassword.getText());
        email.setSubject("Nevitium Test Email");
        email.setText("Nevitium Email Test Successful!  Visit datavirtue.com for updates and support.");
        email.setAttachment("ver.inf");
        email.sendEmail();
        
    }//GEN-LAST:event_testEmailButtonActionPerformed

    private void desktopPDFBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_desktopPDFBoxActionPerformed
        if (desktopPDFBox.isSelected()){
            System.out.println(Desktop.getDesktop().toString());
            if(!Desktop.isDesktopSupported()){
                javax.swing.JOptionPane.showMessageDialog
                        (null, Desktop.getDesktop().toString()+" is not supported.");
                desktopPDFBox.setSelected(false);
                return;
            }

            acroField.setEnabled(false);
        }else {
            acroField.setEnabled(true);
        }
    }//GEN-LAST:event_desktopPDFBoxActionPerformed

    private void checkPaymentBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkPaymentBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkPaymentBoxActionPerformed

    private void SSLBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SSLBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SSLBoxActionPerformed

    private void quantityCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityCheckBoxActionPerformed

    private void printZerosCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printZerosCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printZerosCheckBoxActionPerformed

private void invoiceFolderFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceFolderFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_invoiceFolderFieldActionPerformed

    private void configEDIButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configEDIButtonActionPerformed
        new EDIConfig(null, true, application);
    }//GEN-LAST:event_configEDIButtonActionPerformed

    private void changeHandCursor() {
        saveCursor();
        Cursor c = new Cursor ( Cursor.HAND_CURSOR );
        jTabbedPane1.setCursor(c);
    }

    private void revertCursor(){

        if (defaultCursor == null){            
            defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);            
        }
        jTabbedPane1.setCursor(defaultCursor);
    }

    private void saveCursor(){
       defaultCursor = jTabbedPane1.getCursor();
    }




    private void checkForApp(){
        String app = paymentField.getText();
        app = app.toLowerCase();
        if (app.endsWith(".exe") || app.endsWith(".app") || !app.contains(".")) paymentWebBox.setSelected(false);
        
    }
    
    
    private java.awt.Frame parentWin;
    private java.awt.Font companyFont = new java.awt.Font("Roman", Font.PLAIN, 12);
    private KeyCard accessKey;
    private Settings props; 
    private String fileSep = System.getProperty("file.separator");
    private String nl = System.getProperty("line.separator");
    private Cursor defaultCursor;
    private Image winIcon;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel EDIPanel;
    private javax.swing.JCheckBox SSLBox;
    private javax.swing.JButton acroBrowseButton;
    private javax.swing.JTextField acroField;
    private javax.swing.ButtonGroup addressButtonGroup;
    private javax.swing.JTextField addressField;
    private javax.swing.JTextField backupField;
    private javax.swing.JButton backupFolderButton;
    private javax.swing.JPanel backupPanel;
    private javax.swing.JTextField billToTextField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.JCheckBox catLineCheckBox;
    private javax.swing.JCheckBox ccPaymentBox;
    private javax.swing.JCheckBox checkPaymentBox;
    private javax.swing.JButton checkUpdatesButton;
    private javax.swing.JTextField cityField;
    private javax.swing.JTextField coField;
    private javax.swing.JRadioButton codeRadio2;
    private javax.swing.JPanel companyInfoPanel;
    private javax.swing.JButton configEDIButton;
    private javax.swing.JComboBox countryCombo;
    private javax.swing.JTextField crField;
    private javax.swing.JTextField currencyField;
    private javax.swing.JTextField dataFolderField;
    private javax.swing.JRadioButton descRadio;
    private javax.swing.JCheckBox desktopPDFBox;
    private javax.swing.JCheckBox displayTaxIDCheckBox;
    private javax.swing.JTextField emailAddressField;
    private javax.swing.JPasswordField emailPassword;
    private javax.swing.JTextField emailUserName;
    private javax.swing.JButton fontButton;
    private javax.swing.JTextField graceField;
    private javax.swing.JTextField iPrefixField;
    private javax.swing.JLabel inchLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JCheckBox inkCheckBox;
    private javax.swing.JPanel inventoryPanel;
    private javax.swing.JButton invoiceButton;
    private javax.swing.JTextField invoiceColorField;
    private javax.swing.JComboBox invoiceComboBox;
    private javax.swing.JTextField invoiceFolderField;
    private javax.swing.JTextField invoiceNameField;
    private javax.swing.JPanel invoicePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton kgsRadio;
    private javax.swing.JPanel layoutPanel;
    private javax.swing.JTextField layoutPath;
    private javax.swing.JLabel layoutPathLabel;
    private javax.swing.JRadioButton lbsRadio;
    private javax.swing.JButton logButton;
    private javax.swing.JButton logoBrowse;
    private javax.swing.JTextField logoField;
    private javax.swing.JTextField markupField;
    private javax.swing.JCheckBox messageBox;
    private javax.swing.JTextField otherField;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JSpinner paperSpinner;
    private javax.swing.JCheckBox paymentBox;
    private javax.swing.JButton paymentBrowseButton;
    private javax.swing.JTextField paymentField;
    private javax.swing.JCheckBox paymentWebBox;
    private javax.swing.JButton pdfAutoFindButton;
    private javax.swing.JButton pdfRevertButton;
    private javax.swing.JTextField phoneField;
    private javax.swing.JCheckBox posBox;
    private javax.swing.JCheckBox printZerosCheckBox;
    private javax.swing.JTextField qPrefixField;
    private javax.swing.JCheckBox quantityCheckBox;
    private javax.swing.JButton quoteButton;
    private javax.swing.JTextField quoteFolderField;
    private javax.swing.JTextField quoteNameField;
    private javax.swing.JButton reportButton;
    private javax.swing.JTextField reportFolderField;
    private javax.swing.JComboBox roundingCombo;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton screenLogoBrowse;
    private javax.swing.JTextField screenPicField;
    private javax.swing.JButton secButton;
    private javax.swing.JButton secondaryButton;
    private javax.swing.JCheckBox secondaryCheckBox;
    private javax.swing.JTextField secondaryField;
    private javax.swing.JPanel securityPanel;
    private javax.swing.JCheckBox showTax1Box;
    private javax.swing.JCheckBox showTax2Box;
    private javax.swing.JCheckBox showToolbar;
    private javax.swing.JTextField smtpPortField;
    private javax.swing.JTextField smtpServer;
    private javax.swing.JTextField stColorField;
    private javax.swing.JCheckBox statusBox;
    private javax.swing.JTextField tax1Field;
    private javax.swing.JTextField tax1NameField;
    private javax.swing.JTextField tax2Field;
    private javax.swing.JTextField tax2NameField;
    private javax.swing.JTextField taxIDField;
    private javax.swing.JButton testEmailButton;
    private javax.swing.JComboBox themeCombo;
    private javax.swing.JRadioButton upcRadio;
    private javax.swing.JLabel userLabel;
    private javax.swing.JCheckBox waterBox;
    private javax.swing.JButton watermarkBrowse;
    private javax.swing.JTextField watermarkField;
    // End of variables declaration//GEN-END:variables
    
}
