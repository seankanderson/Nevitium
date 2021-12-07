/*
 * ControlCenter.java
 *
 * Created on June 22, 2006, 9:47 AM
 ** Copyright (c) Data Virtue 2006
 */

package businessmanager;

import RuntimeManagement.KeyCard;
import RuntimeManagement.GlobalApplicationDaemon;

import businessmanager.Common.Tools;
import businessmanager.Config.SettingsDialog;
import businessmanager.Reports.ReportFactory;
import businessmanager.InvoiceQuote.InvoiceDialog;
import businessmanager.InvoiceQuote.InvoiceManager;
import businessmanager.Connections.MyConnectionsApp;
import businessmanager.GiftCard.GiftCardManager;
import businessmanager.Inventory.MyInventoryApp;
import businessmanager.InvoiceQuote.LayoutManager.InvoiceLayoutManager;
import businessmanager.Reports.ReportTableDialog;
import datavirtue.*;
import businessmanager.checkMod.*;
import de.schlichtherle.io.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import services.DatabaseService;

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class ControlCenter extends javax.swing.JFrame {

    private KeyCard accessKey;
    private boolean log = false;
    private boolean unicode = false;
    private Toolkit tools = Toolkit.getDefaultToolkit();
    
    /** Creates new form ControlCenter */
    public ControlCenter() {       
        
        //this.getLookAndFeel();
        
        
        this.addWindowListener(new java.awt.event.WindowAdapter(){
	public void windowClosing(java.awt.event.WindowEvent e){
	                        
            closeAll();
                        
	}} );

        System.setProperty("http.agent", "Nevitium 1.5.9");
                /*
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("file.separator"));
        System.out.println(System.getProperty("sun.cpu.isalist"));
        */
        
        
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        
            initComponents();

            buildMenu();
            mainToolbar.setLayout(new FlowLayout());
            statusToolbar.setLayout(new FlowLayout());
            //invoiceButton.setPreferredSize(new Dimension(91,81));
            
            dbsys = new datavirtue.DbEngine ("data/main.dsf", application, unicode);
            
            
            
            boolean per = Tools.verifyElevatedPermissions("ver.inf");
            if (!per) {
                JOptionPane.showMessageDialog(null, 
                        "Nevitium was not able to access the program directory. Please contact software@datavirtue.com for support.");    
                System.exit(-7);
                }
            DV.writeFile("ver.inf", "1.59", false);
            
            /* Check to see if the user can read/write lastco.inf */

            workingPath = getLastCo("lastco.inf");  //**** Retrieves and setup last co that was open from text file ****
            per = Tools.verifyElevatedPermissions(workingPath);
            if (!per) System.exit(-7);
            
            /* Check to see if workingPath.conn.db can read / write */
            boolean setCo = false;
            application.setWorkingPath(workingPath);
            
            application.setDb(this.dbsys);
            setCo = this.previousCompany(workingPath);

            while (setCo == false){  //**** The directory was moved or invalid ****
                    
                    setCo = getCoDialog(true);  //**** Send user back to the Open Co Dialog ****
                    
                } 
           
            mainToolbar.setVisible(DV.parseBool(props.getProp("SHOW TOOLBAR"), true));
            
            pathLabel.setText(workingPath);
                       
                       
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        
        this.setLocation(dim.width, dim.height);
        updateMessage();
        showStatus ();
        
        setBG();

        dbsys.setOptimized(true);
    
    }

    private void getUnicodeProperty(String path){
        
        if (!new File(path + "encode.char").exists()) DV.writeFile(path+"encode.char", "ASCII", false);
        
        String char_encoding = DV.readFile(path+"encode.char");
            
            //System.out.println(char_encoding);
            if (char_encoding.trim().equalsIgnoreCase("utf") || char_encoding.trim().equalsIgnoreCase("unicode")){
                unicode = true;
                System.out.println("UNICODE");
            }else {
                unicode = false;
                System.out.println("NOT UNICODE");
            }
            
            dbsys.setUnicode(unicode, "data/main.dsf");

    }
    

    private void setBG() {
        
        String screenPic = props.getProp("SCREEN");
        picLabel.setIcon(new javax.swing.ImageIcon(screenPic));
         
    }

    
    private void updateMessage() {
        
        boolean get;
        get = DV.parseBool(props.getProp("REMOTE MESSAGE"), true);
        if (!get){
                internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Disconnect.png")));
            }else{
                internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Connect.png")));
            }
        if (get){
            /* Thread Example */
            ReturnMessageThread rm = new ReturnMessageThread("http://datavirtue.com/nevitium/update/nevstat.txt",
                    remoteMessageBox, internetStatus);
            rm.start();
            
            
        }else {

            remoteMessageBox.setText(" Please visit datavirtue.com for updates & support.");

        }
    }
    
    private String getLastCo(String f) {
        
        if (new File(f).exists() && new File(f).length() > 0){
            
           return DV.readFile(f).trim();
            
        }
        return "data/";  /* Default, if directory is not present or available */
        
    }
    /* Displays the role of the current user and changes the win title to reflect the company name. */
    private void showRole() {
        
        userButton.setText(accessKey.getUserName());
        
        if (userButton.getText().equals("No Security")){
            userButton.setIcon(new javax.swing.ImageIcon(
                        getClass().getResource("/businessmanager/res/Aha-48/Unlock.png")));
        }
        
        String role = "Master";
        
        /* Show the role and change the color based on the role, red for master users and green for regular users */
        //roleButton.setForeground(new java.awt.Color(153,0,0));
        //change icon
        roleButton.setIcon(new javax.swing.ImageIcon(
                        getClass().getResource("/businessmanager/res/Aha-48/Boss.png")));
        
        if (!accessKey.isMaster()){
            
            roleButton.setIcon(new javax.swing.ImageIcon(
                        getClass().getResource("/businessmanager/res/Aha-48/User.png")));
            role = "User";
        }
        roleButton.setText(role);
        
        this.setTitle("Nevitium  "+'('+props.getProp("CO NAME")+')');
        
    }
    
    private void showStatus () {        
        
        ArrayList errors = application.getRuntimeIncidentList();

        if (errors.size() > 0) {          
          
                statusButton.setIcon(new javax.swing.ImageIcon(
                        getClass().getResource("/businessmanager/res/Aha-48/Single problem.png")));
                pathLabel.setBackground(new java.awt.Color(255,102,102));
            
        }else {
            
            dbProblem = false;
            statusButton.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/businessmanager/res/Aha-48/Green earth.png")));
                           
        }

       boolean statShow = DV.parseBool(props.getProp("STATUS LIGHT"), true);
       statusButton.setVisible(statShow);
       
    }

    private void loadSettings ()  {
     
            props = new Settings(workingPath + "settings.ini");
            application.setProps(props);

            this.setTitle("Nevitium  "+'('+props.getProp("CO NAME")+')');
            //props.setProp("DATA FOLDER", workingPath);
    }

    
     private void backup () {
       
        if (!dbProblem) {
        String coName = props.getProp("CO NAME");
        coName = coName.replace(',', ' ').replace('*', ' ');
        boolean problem = false;
        String date = DV.getFullDate().replace('/','-').replace(' ','-').replace(',',' ');
        
        String message="There was a problem creating the backup!";
        FileDialog fd = new FileDialog(this, true, props.getProp("BACKUP FOLDER"), coName + "_Backup_"+date+ ".zip");
        fd.setVisible(true);
        
        if (fd.getPath().equals("")) return;
        
        try {
        if (new File(fd.getFolder()).exists()){ 
        
            javax.swing.JOptionPane.showMessageDialog(null, 
                    "Make sure no one attempts to access "+workingPath+" with another copy of Nevitium during the backup.");
            
            unlockData();
            
            File backup1 = new File(fd.getPath());
        
            File dataFolder = new File (workingPath);  //updated        
        
            dataFolder.archiveCopyAllTo(backup1);
            
            if (!backup1.exists()){
                problem = true;
                message = "There was a problem trying to create the zip file. Make sure there are no invalid characters in the file name.";
            }

            backup1 = null;
            dataFolder = null;
            
            if (lockData(workingPath)==false){
             closeAll();
            }                    
        }else{
            
            message = "The Backup Folder" + System.getProperty ("line.separator") +
                 fd.getPath() + System.getProperty ("line.separator") + "   does NOT exsist.";
            
            problem = true;
        }
        
       if (DV.parseBool(props.getProp("SECONDARY BACKUP"), false) && new File(props.getProp("SECONDARY FOLDER")).exists()){
        
            File backup2 = new File(props.getProp("SECONDARY FOLDER")+coName+"_Backup_"+date+ ".zip");
            //TODO: change to working folder
            File dataFolder2 = new File (this.workingPath);
          
            dataFolder2.archiveCopyAllTo(backup2);
            if (!backup2.exists()){
                problem = true;
                message = "There was a problem trying to create the secondary zip file. Make sure there are no invalid characters in the file name.";
            }
            backup2 = null;
            dataFolder2 = null;
                    
        }else{            
           if (props.getProp("SECONDARY BACKUP").equalsIgnoreCase("true")){  //if i'm in here
                 message = "The Backup Folder" + System.getProperty ("line.separator") +
                 props.getProp("SECONDARY FOLDER") + "   does NOT exsist.";
                        
               problem = true;               
           }           
        }        
        }catch (Exception e) {
            
            problem = true;
            DV.writeFile("backup.err",e.toString() + System.getProperty("line.separator") + DV.getShortDate(),true );
            
        }
        if (problem) JOptionPane.showMessageDialog(this, message);
        else JOptionPane.showMessageDialog(this, "Backup : "+ fd.getPath()+nl + " Successful.");
        
        } else JOptionPane.showMessageDialog(this, "The Database is reporting problems, backup aborted.");
    }
    
    private void restore () {
        
        boolean problem = false;
        String message = "There was a problem restoring the backup!";
        
        try {
        
        //insert file mamanger / picker
        File file;
        String f;
        
                
        if (props.getProp("BACKUP FOLDER").equals("")) f = ".";
        else f = props.getProp("BACKUP FOLDER");
            
            //System.out.println(f);
            
            file = new File(f);
            JFileChooser fileChooser = new JFileChooser(file);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = fileChooser.showOpenDialog(this);
            java.io.File curFile = fileChooser.getSelectedFile();
           
            if (returnVal == JFileChooser.CANCEL_OPTION) return;
            
            if (curFile == null ) return  ;
            
            //File backup1 = new File(curFile.toString());
            File dataFolder = new File (workingPath);
            File backup = new File (curFile.toString());
            
            //check to make sure we are restoring nevitium backup
            
            String [] files  = backup.list();  //get a list of files in .zip file
                       
            Arrays.sort(files);  //sort the dang thing before you can do a search
                                   
            if (Arrays.binarySearch(files, "conn.db") > -1){  //search and compare return value
                 
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Make sure no one attempts to access "+workingPath+" with another copy of Nevitium during the restore.");
            
                
                unlockData();
                               
                backup.archiveCopyAllTo(dataFolder);  //restore the data
                
                //lockData(workingPath);
                
            }else {
                
                message = "The file you tried to restore was not a valid Nevitium Backup.";
                problem = true;
            }
            
            
        }catch (Exception e) {
            
            problem = true;
            DV.writeFile("restore.err",e.toString() + System.getProperty("line.separator") + DV.getShortDate(), true );
            
        }
        //insert message
        if (problem) JOptionPane.showMessageDialog(this, message);
        else JOptionPane.showMessageDialog(this, "Restore Successful");
                
        if (dbProblem && !problem) {
            
            props = new Settings(workingPath + "settings.ini");
            application.setProps(props);
            dbsys = new DbEngine ("data/main.dsf", application, unicode);
            showStatus();
        }
    }
    
    private void secure(boolean menu, boolean change) {
        
        //System.out.println("Secure run!");
        
        AccessDialog ad = new AccessDialog(null, true, dbsys, workingPath);
        
        if (ad.isSecure()){
            if (ad.isAllowed()){
                accessKey  = ad.getKeyCard();
                application.setKey_card(accessKey);
                if (debug) System.out.println("Keycard assigned!");
                ad.dispose();
                ad = null;
            }else {                
                if (!change) System.exit(-3);
                return;
            }
        }else {
            if (menu) javax.swing.JOptionPane.showMessageDialog(null, "Security is not currently enabled.  Go to File-->Settings-->Security, to enable security & manage users. ");
            Object [] defUser = new Object [] {new Integer(0),
            "No Security", "", true};
            accessKey = new KeyCard(defUser); //default full control
            application.setKey_card(accessKey);
            ad.dispose();
            ad = null;
        }
        
        showRole();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        picLabel = new javax.swing.JLabel();
        remoteMessageBox = new javax.swing.JTextField();
        mainToolbar = new javax.swing.JToolBar();
        connectionsButton = new javax.swing.JButton();
        inventoryButton = new javax.swing.JButton();
        activityButton = new javax.swing.JButton();
        invoiceButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        statusToolbar = new javax.swing.JToolBar();
        statusButton = new javax.swing.JButton();
        userButton = new javax.swing.JButton();
        roleButton = new javax.swing.JButton();
        pathLabel = new javax.swing.JLabel();
        internetStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Nevitium Invoice Manager");
        setIconImage(winIcon);

        picLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jScrollPane1.setViewportView(picLabel);

        remoteMessageBox.setEditable(false);

        mainToolbar.setFloatable(false);
        mainToolbar.setRollover(true);
        mainToolbar.setBorderPainted(false);

        connectionsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Conference.png"))); // NOI18N
        connectionsButton.setText("Connections");
        connectionsButton.setFocusable(false);
        connectionsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        connectionsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        connectionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionsButtonActionPerformed(evt);
            }
        });
        mainToolbar.add(connectionsButton);

        inventoryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Tables.png"))); // NOI18N
        inventoryButton.setText("Inventory");
        inventoryButton.setFocusable(false);
        inventoryButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        inventoryButton.setMinimumSize(new java.awt.Dimension(91, 81));
        inventoryButton.setPreferredSize(new java.awt.Dimension(91, 81));
        inventoryButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        inventoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryButtonActionPerformed(evt);
            }
        });
        mainToolbar.add(inventoryButton);

        activityButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Money.png"))); // NOI18N
        activityButton.setText("Invoices");
        activityButton.setFocusable(false);
        activityButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        activityButton.setPreferredSize(new java.awt.Dimension(91, 81));
        activityButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        activityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activityButtonActionPerformed(evt);
            }
        });
        mainToolbar.add(activityButton);

        invoiceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Barcode scanner1.png"))); // NOI18N
        invoiceButton.setText("SALE");
        invoiceButton.setFocusable(false);
        invoiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        invoiceButton.setMinimumSize(new java.awt.Dimension(91, 81));
        invoiceButton.setPreferredSize(new java.awt.Dimension(91, 81));
        invoiceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        invoiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceButtonActionPerformed(evt);
            }
        });
        mainToolbar.add(invoiceButton);

        settingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Configuration.png"))); // NOI18N
        settingsButton.setText("Setup");
        settingsButton.setFocusable(false);
        settingsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        settingsButton.setPreferredSize(new java.awt.Dimension(91, 81));
        settingsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });
        mainToolbar.add(settingsButton);

        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Close.png"))); // NOI18N
        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setPreferredSize(new java.awt.Dimension(91, 81));
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        mainToolbar.add(exitButton);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        statusToolbar.setFloatable(false);
        statusToolbar.setRollover(true);

        statusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Green earth.png"))); // NOI18N
        statusButton.setText("Status");
        statusButton.setToolTipText("Data Access Status");
        statusButton.setFocusable(false);
        statusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        statusButton.setPreferredSize(new java.awt.Dimension(98, 81));
        statusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        statusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusButtonActionPerformed(evt);
            }
        });
        statusToolbar.add(statusButton);

        userButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Lock.png"))); // NOI18N
        userButton.setText("Security");
        userButton.setToolTipText("Security Status - Change User");
        userButton.setFocusable(false);
        userButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        userButton.setPreferredSize(new java.awt.Dimension(98, 81));
        userButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        userButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userButtonActionPerformed(evt);
            }
        });
        statusToolbar.add(userButton);

        roleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-48/Boss.png"))); // NOI18N
        roleButton.setText("Master");
        roleButton.setToolTipText("Current Security Role - Manage Security");
        roleButton.setFocusable(false);
        roleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        roleButton.setPreferredSize(new java.awt.Dimension(98, 81));
        roleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        roleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roleButtonActionPerformed(evt);
            }
        });
        statusToolbar.add(roleButton);

        pathLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pathLabel.setText("Folder Path");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pathLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, statusToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pathLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 87, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        internetStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Connect.png"))); // NOI18N
        internetStatus.setToolTipText("Nevitium Internet Status");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                    .add(mainToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(remoteMessageBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE)
                        .add(1, 1, 1)
                        .add(internetStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(mainToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(remoteMessageBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(internetStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    
    private void custInvoiceHistory() {
        
      MyConnectionsApp cd = new MyConnectionsApp (this, true, application, true, true, false);
        cd.setVisible(true);
        
        int k = cd.getReturnValue() ;  //real value
        
        if (k == -1) return;
               
        cd.dispose(); //dont call dispose before finsihing with method
        cd = null;    
        
        if (k > 0) {
            ReportFactory.generateCustomerStatement(application, k);
            
        }
    }
    
    private boolean getCoDialog(boolean exitOnCancel) {
      
        OpenCompanyDialog ocd = new OpenCompanyDialog(null, true, "prev.inf");
      String action = ocd.getStatus();
      
      if (action.equals("cancel")) {
          
          if (exitOnCancel) System.exit(0);
          return true;          
      }
      if (action.equals("open")) return this.openCompany();
      if (action.equals("create")) return this.newCompany();
      if (action.equals("previous")) return this.previousCompany(ocd.getPath());
      ocd = null;
      
      return false;
      
    }

    private boolean previousCompany(String folder){

        if (!new File(folder).exists()){
            javax.swing.JOptionPane.showMessageDialog(null,
                        folder + "  Is not available or doesn't exist.");
            return false;
        }

        if (isFolderNevitium(folder)){
                return this.setCompany(folder);
           }else if (Tools.isFolderEmpty(folder)) {
                return this.setCompany(folder);
            }else {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Nevitium tried unsuccessfully to load a database from "+folder);
                return false;
            }
         
    }

    private void addToPrevious (String newFolder) {
               
        String newLine = System.getProperty("line.separator");
       
        boolean match = false;
         
        java.util.ArrayList al = new java.util.ArrayList();
                
                
       java.io.BufferedReader in=null;
    try {
                
        
        if (!new File("prev.inf").exists()) {
                        
            DV.writeFile("prev.inf", workingPath + newLine, true);
        }
        
        
        in = new java.io.BufferedReader(new java.io.FileReader("prev.inf"));
                                
                
                String line="";
                                                                              
               while (line != null)    {
                    
                   
                    line = in.readLine();
                    al.add(line);
                                        
                    }
                al.trimToSize();
                                   
                in.close();
                        in = null;    //clean up
                        line = null;
                
                        String tmp;
                        for (int i = 0; i < al.size(); i++){
                            
                            tmp = (String)al.get(i);
                            if (tmp == null) break;
                            
                            if (tmp.equals(newFolder)) match = true;                            
                        }
                        
    }catch (Exception e) {
             
        e.printStackTrace();
        
    }finally {
        try {
                     
                if (in != null) in.close();
                in = null;
                                               
            } catch (IOException ex) {
                
                ex.printStackTrace();
                
            }}
        
        if (!match){
            
            DV.writeFile("prev.inf", newFolder + newLine, true);
        }
    }
    
    private boolean newCompany() {
        
       
       /*
         *Ask to carry over Settings.ini and users.db, automatically carry over stored misc items
         *Create a ver.inf file
         *Cycle through and change each db in the data system  with dbsys.changePath()
         *
         */

        NewCoFileDialog ncd = new NewCoFileDialog(null, true, "C:/", "My Company");
        String newPath = ncd.getPath();

                 
        if (debug) System.out.println("NEW CO Path Name: "+newPath);
        /* Make sure the folder is empty */    
        /* Create the ver.inf OR open.run file */    
            
        if (newPath.trim().equals("")) return false;

        if (!Tools.isFolderEmpty(newPath)){
            javax.swing.JOptionPane.showMessageDialog(null,
            "The selected folder is not valid or already contains files; please select an empty folder.");
            return false;
        }

        int a = javax.swing.JOptionPane.showConfirmDialog(null, "Do you need support for non-English text?","Character Encoding",  JOptionPane.YES_NO_OPTION);
            if (a == 0){
            
                DV.writeFile(newPath + "encode.char", "UTF", false);
            
            }else {
                DV.writeFile(newPath + "encode.char", "ASCII", false);
            }
        
        
        return createCompany(newPath);
            
        
    }

    private boolean createCompany(String path){
        /* Set company with no security because we have not created
             the Master user yet */
            this.setCompany(path, false);  ///no security yet

            /* New company folder is set now create the Master
             user with no password hence security is disabled */
            try {
            Object [] user = new Object [12];
            user[0] = new Integer(0);
            user[1] = "Master";
            user[2] = "";//password
            user[3] = true;//master
            user[4] = new Long(300);
            user[5] = new Long(300);
            user[6] = new Long(500);
            user[7] = new Long(300);
            user[8] = new Long(100);
            user[9] = new Long(100);
            user[10] = new Long(100);
            user[11] = new Long(100);
            dbsys.saveRecord("users", user, false);
            }catch(Exception e){
                return false;
            }
            /* We now run a security check but the Master has no password
             so the security check will automatically clear*/
            this.secure(true, false);
            this.setCompany(path);

            return true;
    }

    private boolean openCompany() {
        
        
        JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(this);
            java.io.File curFile = fileChooser.getSelectedFile();
           
            if (returnVal == JFileChooser.CANCEL_OPTION) return false;
            
            if (curFile == null ) return false;
            String newPath = curFile.getPath()+ System.getProperty("file.separator");
            
            /* Check to make sure the selected folder is a valid Nevitium folder. */

            /* Check to see if the folder is empty, if yes ask to setup a new co if no move on */
            if (Tools.isFolderEmpty(newPath)){

                int a = javax.swing.JOptionPane.showConfirmDialog(null,
                        "The folder you selected is empty; do you want to create a company there?","Empty Folder",  JOptionPane.YES_NO_OPTION);
                if (a == 0){
                    return createCompany(newPath);
                }else {
                    return false;
                }

            }

            if (this.isFolderNevitium(newPath)){
                return this.setCompany(newPath);
            }else {
              javax.swing.JOptionPane.showMessageDialog(null,
                        newPath + "  Is not a Nevitium folder.");
              return false;
            }
            
    }

    private boolean isFolderNevitium(String folder){
        /* I should probably make this more comprehensive. */
        if (new File(folder+"conn.db").exists() || new File(folder+"invoice.db").exists()){
            return true;
        }
        return false;

    }


    private boolean setCompany (String path){
        
        return setCompany(path, true);
        
    }
    
private void unlockData() {
    
    try {
            if (fl != null) fl.release();
        } catch (IOException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem closing the control file; contact support for help.");
        }
    
    
}

private boolean lockData(String path){
  
     unlockData();
    
     java.io.File ctrlFile;
     
    try {            
            ctrlFile = new java.io.File(path + "data.run");
          
            fos = new java.io.FileOutputStream(ctrlFile);
            fl = fos.getChannel().tryLock();
            
            if (fl == null){
               
                javax.swing.JOptionPane.showMessageDialog(null, "Only one user can access "+path);
                
                return false;                                
            }
        } catch (Exception ex) {
            
           
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem accessing: '"+path+"'"+nl+
                    "You may not have permissions to access this folder.");
            return false;                        
        }
     return true;
    
}

    /* Used by openCompany, newCompany, getCoDialog, Constructor */
    private boolean setCompany(String path, boolean secure) {
          
        if (path == null) return false;
      
        /*  
         Try to get a lock on data.run before trying to access the data files.
         
         */
          
        if (!lockData(path)){
           
           return false;
           
          }     
        


        if (new File(path).isDirectory()){
            
                workingPath = path;
                this.getUnicodeProperty(path);
                this.changeDbPaths(path, false);  //Change the directory of the db files with no security check ****
                        
                if (!new File(path + "settings.ini").exists()){
            
                application.setWorkingPath(path);
                
                application.setDb(dbsys);
                
                //where is security setting ?
                application.setKey_card(accessKey);
                new SettingsDialog (this, true, application, false, 0).setVisible(true);
                               
            }
        }
                
                this.loadSettings();
                String layout_folder = path+"layouts"+this.file_sep;

                //System.out.println(layout_folder);

                new File(layout_folder).mkdir();
               
                if (!new File(layout_folder+"layout.workorder.xml").exists()){
                    new File("layouts"+file_sep+"layout.workorder.xml").copyTo(new File(layout_folder+file_sep+"layout.workorder.xml"));
                }
                if (!new File(layout_folder+"layout.invoice.xml").exists()){
                    new File("layouts"+file_sep+"layout.invoice.xml").copyTo(new File(layout_folder+file_sep+"layout.invoice.xml"));
                }
                if (!new File(layout_folder+"layout.statement.xml").exists()){
                    new File("layouts"+file_sep+"layout.statement.xml").copyTo(new File(layout_folder+file_sep+"layout.statement.xml"));
                }
                if (secure) this.secure(false, false);
                
                props.setProp("DATA FOLDER", workingPath);
                application.setWorkingPath(workingPath);
                                
                setBG();
           
           this.addToPrevious(workingPath);
            
           return true;
            
            
    }
        
    
    private void changeDbPaths(String path, boolean sec) {
          
        
        dbsys.changePaths(path);
        workingPath = path;
        pathLabel.setText(workingPath);
        
        if (sec) this.secure(false, false);
        
    }
    
    
    private void conversion14to15(){
        JFileChooser fileChooser = DV.getFileChooser("..");

        java.io.File curFile = fileChooser.getSelectedFile();
         if (curFile == null) return;

        if (!curFile.exists()) {

            return;

        }

        String p = curFile.toString();

        String current_version = DV.readFile("ver.inf").trim();

        de.schlichtherle.io.File f = new de.schlichtherle.io.File(p);

        String [] files = f.list();

        boolean good_import = true;

        if (files != null){

            int [] results  = DV.whichContains(files, "ver.inf");
            if (results.length < 1) good_import = false;

        }else {good_import = false;}

        if (!good_import){

            /* tell the asshole */
            javax.swing.JOptionPane.showMessageDialog(null, "The file you tried to import is not a Nevitium Full Export.");
            return;

        }

        /*Grab the ver file from the import  */
        new de.schlichtherle.io.File(p+"/ver.inf").copyTo(new File ("impver.inf"));

        String import_version = DV.readFile("impver.inf");
        new de.schlichtherle.io.File ("impver.inf").delete();

        if (!import_version.trim().equals(current_version.trim()) &&
                !import_version.trim().equals("Version 1.3")){  // || OR this to allow more versions

            javax.swing.JOptionPane.showMessageDialog(null, "Version mismatch.  Needed: "+current_version+"   Found: "+import_version);
            return;
        }

        //dbsys.csvImport("...", f,true, a );


        f = new de.schlichtherle.io.File(p+"/inventory.csv");
        int [] a = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "inventory",a ,true, true);


        f = new de.schlichtherle.io.File(p+"/connections.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "conn",a ,true, true, true);


        f = new de.schlichtherle.io.File(p+"/messages.csv");
        a = new int [] {0,1,2};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "messages",a ,true,true);


        f = new de.schlichtherle.io.File(p+"/invcat.csv");
        a = new int [] {0,1};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invcat",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/imageref.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "imageref",a ,true,true);


        f = new de.schlichtherle.io.File(p+"/miscitems.csv");
        a = new int [] {0,1};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "miscitems",a ,true, true);


        f = new de.schlichtherle.io.File(p+"/invtcat.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invtcat",a ,true, true);

        f = new de.schlichtherle.io.File(workingPath + "grps/");
        if (f.exists())new de.schlichtherle.io.File(p + "/grps/").copyAllTo(f);

        f = new de.schlichtherle.io.File(workingPath + "jrnls/");
        if (f.exists())new de.schlichtherle.io.File(p + "/jrnls/").copyAllTo(f);

        /*f = new de.schlichtherle.io.File(workingPath + "settings.ini");
        if (f.exists())new de.schlichtherle.io.File(p + "/settings.ini").copyTo(f);
        */

        try {

            f.umount(true,true,true,true);

        } catch (ArchiveException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, ex.toString());
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Please Restart Nevitium.");
        }



    }


    private void upgradeImport(){
              
        JFileChooser fileChooser = DV.getFileChooser("..");
            
        java.io.File curFile = fileChooser.getSelectedFile();
         if (curFile == null) return;
        
        if (!curFile.exists()) {
            
            return;
            
        }        
        
        String p = curFile.toString();
         
        String current_version = DV.readFile("ver.inf").trim();
        
        de.schlichtherle.io.File f = new de.schlichtherle.io.File(p);       
        
        String [] files = f.list();
        
        boolean good_import = true;
        
        if (files != null){
            
            int [] results  = DV.whichContains(files, "ver.inf");
            if (results.length < 1) good_import = false;
            
        }else {good_import = false;}
        
        if (!good_import){
            
            /* tell the asshole */
            javax.swing.JOptionPane.showMessageDialog(null, "The file you tried to import is not a Nevitium Full Export.");
            return;
            
        }
        
        /*Grab the ver file from the import  */
        new de.schlichtherle.io.File(p+"/ver.inf").copyTo(new File ("impver.inf")); 
        
        String import_version = DV.readFile("impver.inf");
        new de.schlichtherle.io.File ("impver.inf").delete();
        
        if (!import_version.trim().equals(current_version.trim()) &&
                !import_version.trim().equals("Version 1.3")){  // || OR this to allow more versions
            
            javax.swing.JOptionPane.showMessageDialog(null, "Version mismatch.  Needed: "+current_version+"   Found: "+import_version);
            return;
        }
          
        //dbsys.csvImport("...", f,true, a );

        
        f = new de.schlichtherle.io.File(p+"/inventory.csv");
        int [] a = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "inventory",a ,true, true);

        f = new de.schlichtherle.io.File(p+"/invnotes.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invnotes",a ,true, true);

        f = new de.schlichtherle.io.File(p+"/conn.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "conn",a ,true, true);
        
        f = new de.schlichtherle.io.File(p+"/connship.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "connship",a ,true, true);

        f = new de.schlichtherle.io.File(p+"/invoice.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invoice",a ,true,true);
                
        f = new de.schlichtherle.io.File(p+"/shipto.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "shipto",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/invitems.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invitems",a ,true,true);
                
        f = new de.schlichtherle.io.File(p+"/payments.csv");
        a = new int [] {0,1,2,3,4,5,6};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "payments",a ,true,true);
       
        f = new de.schlichtherle.io.File(p+"/returns.csv");
        a = new int [] {0,1,2,3,4,5,6,7};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "returns",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/messages.csv");
        a = new int [] {0,1,2};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "messages",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/quote.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "quote",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/qitems.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "qitems",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/qshipto.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "qshipto",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/invcat.csv");
        a = new int [] {0,1};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invcat",a ,true,true);
        
        f = new de.schlichtherle.io.File(p+"/imageref.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "imageref",a ,true,true);
        
        f = new de.schlichtherle.io.File(p+"/card.csv");
        a = new int [] {0,1,2,3,4,5,6};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "card",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/users.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "users",a ,true,true);
        
        f = new de.schlichtherle.io.File(p+"/countries.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "countries",a ,true,true);

        f = new de.schlichtherle.io.File(p+"/auditlog.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "auditlog",a ,true, true);

        f = new de.schlichtherle.io.File(p+"/miscitems.csv");
        a = new int [] {0,1};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "miscitems",a ,true, true);

        f = new de.schlichtherle.io.File(p+"/checkpayee.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "checkpayee",a ,true, true);

        f = new de.schlichtherle.io.File(p+"/invtcat.csv");
        a = new int [] {0,1,2,3};
        if (f.exists())new ImportDialog(null, true, f, dbsys, "invtcat",a ,true, true);
        
        f = new de.schlichtherle.io.File(workingPath + "grps/");
        if (f.exists())new de.schlichtherle.io.File(p + "/grps/").copyAllTo(f);
        
        f = new de.schlichtherle.io.File(workingPath + "jrnls/");
        if (f.exists())new de.schlichtherle.io.File(p + "/jrnls/").copyAllTo(f);
           
        f = new de.schlichtherle.io.File(workingPath + "settings.ini");
        if (f.exists())new de.schlichtherle.io.File(p + "/settings.ini").copyTo(f);
        
        try {
             
            f.umount(true,true,true,true);
            
        } catch (ArchiveException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, ex.toString());
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Please Restart Nevitium.");
        }
       
        
    }
    
    private void upgradeExport () {
        
        String tag = "";
        if (System.getProperty("os.name").contains("Windows") ) tag = "\\My Documents\\";
        
        businessmanager.FileDialog fd = new FileDialog (null, true, System.getProperty("user.home")+tag, "Upgrade_Export_" + DV.getShortDate().replace('/','-'));
        fd.setVisible(true); 
        
        if (fd.getPath().equals("")) return;
        // start here
        String p = fd.getPath();
        
        if (!p.toLowerCase().endsWith(".zip")) p = p + ".zip";

        de.schlichtherle.io.File file = new de.schlichtherle.io.File(p);
      
            file.mkdirs();

            StatusDialog sd = new StatusDialog(this, false, "Full Export Status", true);
            sd.changeMessage("Exporting database files to .csv format");

            /*  */
            sd.addStatus("Initializing Files...");
            sd.addStatus("Exporting Inventory...");
        de.schlichtherle.io.File f = new de.schlichtherle.io.File(p+"/inventory.csv");
        int [] a = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        dbsys.csvExport("inventory", f, a );

        sd.addStatus("Exporting Inventory Notes...");
        f = new de.schlichtherle.io.File(p+"/invnotes.csv");
        a = new int [] {0,1,2,3};
        dbsys.csvExport("invnotes", f, a );

        sd.addStatus("Exporting My Connections...");
        f = new de.schlichtherle.io.File(p+"/conn.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        dbsys.csvExport("conn", f, a );

        sd.addStatus("Exporting My Connections Ship To...");
        f = new de.schlichtherle.io.File(p+"/connship.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        dbsys.csvExport("connship", f, a );

        sd.addStatus("Exporting Country data...");
        f = new de.schlichtherle.io.File(p+"/countries.csv");
        a = new int [] {0,1,2,3,4,5};
        dbsys.csvExport("countries", f, a );

        sd.addStatus("Exporting Invoices...");
        f = new de.schlichtherle.io.File(p+"/invoice.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        dbsys.csvExport("invoice", f, a );

        sd.addStatus("Exporting Invoice ShipTo...");
        f = new de.schlichtherle.io.File(p+"/shipto.csv");
        a = new int [] {0,1,2,3};
        dbsys.csvExport("shipto", f, a );

        sd.addStatus("Exporting Invoice Items...");
        f = new de.schlichtherle.io.File(p+"/invitems.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9};
        dbsys.csvExport("invitems", f, a );

        sd.addStatus("Exporting Invoice Returns...");
        f = new de.schlichtherle.io.File(p+"/returns.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9};
        dbsys.csvExport("returns", f, a );

        sd.addStatus("Exporting Invoice Payments...");
        f = new de.schlichtherle.io.File(p+"/payments.csv");
        a = new int [] {0,1,2,3,4,5,6};
        dbsys.csvExport("payments", f, a );

        sd.addStatus("Exporting Quotes...");
        f = new de.schlichtherle.io.File(p+"/quote.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        dbsys.csvExport("quote", f, a );

        sd.addStatus("Exporting Quote Items...");
        f = new de.schlichtherle.io.File(p+"/qitems.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9};
        dbsys.csvExport("qitems", f, a );

        sd.addStatus("Exporting Quote ShipTo...");
        f = new de.schlichtherle.io.File(p+"/qshipto.csv");
        a = new int [] {0,1,2,3};
        dbsys.csvExport("qshipto", f, a );

        sd.addStatus("Exporting Prepaid Accounts...");
        f = new de.schlichtherle.io.File(p+"/card.csv");
        a = new int [] {0,1,2,3,4,5,6};
        dbsys.csvExport("card", f, a );


        sd.addStatus("Exporting Users...");
        f = new de.schlichtherle.io.File(p+"/users.csv");
        a = new int [] {0,1,2,3,4,5,6,7,8,9,10,11};
        dbsys.csvExport("users", f, a);

        sd.addStatus("Exporting Audit Log...");
        f = new de.schlichtherle.io.File(p+"/auditlog.csv");
        a = new int [] {0,1,2,3};
        dbsys.csvExport("auditlog", f, a );

        sd.addStatus("Exporting Misc Items Memory...");
        f = new de.schlichtherle.io.File(p+"/miscitems.csv");
        a = new int [] {0,1};
        dbsys.csvExport("miscitems", f, a );

        sd.addStatus("Exporting Check Payees Memory...");
        f = new de.schlichtherle.io.File(p+"/chkpayee.csv");
        a = new int [] {0,1};
        dbsys.csvExport("chkpayee", f, a );

        sd.addStatus("Exporting Invoice Messages...");
        f = new de.schlichtherle.io.File(p+"/messages.csv");
        a = new int [] {0,1,2};
        dbsys.csvExport("messages", f, a );
        
        sd.addStatus("Exporting Inventory Category Memory...");
        f = new de.schlichtherle.io.File(p+"/invtcat.csv");
        a = new int [] {0,1};
        dbsys.csvExport("invtcat", f, a );
        
        sd.addStatus("Exporting Inventory Image References...");
        f = new de.schlichtherle.io.File(p+"/imageref.csv");
        a = new int [] {0,1,2,3};
        dbsys.csvExport("imgref", f, a );
                
        sd.addStatus("Copying Inventory Groups...");
        f = new de.schlichtherle.io.File(p+ "/grps/");
        new de.schlichtherle.io.File(workingPath + "grps").copyAllTo(f);
        
        sd.addStatus("Copying My Connections Journals...");
        f = new de.schlichtherle.io.File(p+"/jrnls/");
        new de.schlichtherle.io.File(workingPath + "jrnls").copyAllTo(f);
                
        sd.addStatus("Copying Settings.ini...");
        new de.schlichtherle.io.File(workingPath + "settings.ini").copyTo(new de.schlichtherle.io.File(p+"/settings.ini"));
                
        sd.addStatus("Copying ver.inf...");
        new de.schlichtherle.io.File("ver.inf").copyTo(new de.schlichtherle.io.File(p+"/ver.inf"));

        
        try {
            f.umount(true, true, true, true);
            sd.addStatus("Data successfully archived to "+p);
        } catch (ArchiveException ex) {
            sd.addStatus("An Archive Exception has occured trying to create "+ p);
        }


        
    }
    
    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        if (accessKey.checkConfig(500)) {
        new SettingsDialog (this, true, application, true, 0).setVisible(true);
        loadSettings();
        updateMessage();
        showStatus();
        setLookAndFeel();
        setBG();
        mainToolbar.setVisible(DV.parseBool(props.getProp("SHOW TOOLBAR"), true));
        }else {

            accessKey.showMessage("Settings");

        }
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void connectionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionsButtonActionPerformed
        //Tools.playSound(getClass().getResource("/businessmanager/res/slip.wav"));
        MyConnectionsApp cd = new MyConnectionsApp (this, true, application,false, true,true);
        //cd.setVisible(true);
        
        showStatus ();

    }//GEN-LAST:event_connectionsButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
            if (!accessKey.checkInvoice(300)){
            accessKey.showMessage("Invoice/Quote");
            return;
        }

        
            InvoiceDialog id = new InvoiceDialog(this, true, 0, application); //no select            id.setVisible(true);
            id.setVisible(true);
            sys_stat = id.getStat();
            id.dispose(); id = null;
            showStatus ();
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void activityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activityButtonActionPerformed
         if (!accessKey.checkManager(300)){
            accessKey.showMessage("Invoice Manager");
            return;
        }
        //InvoiceModel temp = (InvoiceModel) DV.DeSerial("data/hold/I.10010.inv");
        //  InvoiceModel temp = null;             
        //invDialog id = new invDialog (this, true, dbsys, cso, temp); //no select
        InvoiceManager i = new InvoiceManager(this, true, application);
        
        
        sys_stat = i.getStat();
        
        i.dispose();
        
        showStatus ();
    }//GEN-LAST:event_activityButtonActionPerformed

    private void inventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryButtonActionPerformed
        
            java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        MyInventoryApp id = new MyInventoryApp (null, true, application, false);
                        
                    }
                });
            
            showStatus ();
      
    }//GEN-LAST:event_inventoryButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        closeAll();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void statusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusButtonActionPerformed
        DV.showErrorDialog(application.getRuntimeIncidentList());
    }//GEN-LAST:event_statusButtonActionPerformed

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        secure(true, true);
    }//GEN-LAST:event_userButtonActionPerformed

    private void roleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roleButtonActionPerformed
        if (accessKey.checkConfig(500)) {
        new SettingsDialog (this, true, application, true, 3).setVisible(true);
        loadSettings();
        updateMessage();
        showStatus();
        setLookAndFeel();
        setBG();
        mainToolbar.setVisible(DV.parseBool(props.getProp("SHOW TOOLBAR"), true));
        }else {

            accessKey.showMessage("Settings");

        }
    }//GEN-LAST:event_roleButtonActionPerformed

    
private void openInvoiceReport(){

    if (!accessKey.checkReports(300)){
            accessKey.showMessage("Reports");
            return;
        }

        sys_stat = ReportFactory.generateOpenInvoiceReport(application);

        if (sys_stat.equals("none")) {

            javax.swing.JOptionPane.showMessageDialog(null, "No open invoices found.");
            sys_stat = "";
        }

        showStatus ();
}
    private void goSettings(){

        if (accessKey.checkConfig(500)) {
        new SettingsDialog (this, true, application, true, 8).setVisible(true);
        loadSettings();
        updateMessage();
        showStatus();
        setLookAndFeel();
        setBG();
        mainToolbar.setVisible(DV.parseBool(props.getProp("SHOW TOOLBAR"), true));
        }else {

            accessKey.showMessage("Settings");

        }

    }

    private void setLookAndFeel(){
        String newLook = DV.readFile("theme.ini");
        newLook = newLook.trim();
        if (newLook.equals(currentLookAndFeel)) {
            return;
        }

        try {
            
            String os = System.getProperty("os.name").toLowerCase();
                                
                    /* X Setup */
                    if (!os.contains("nix") && !os.contains("nux") && !os.contains("mac")){
                
                        //setLookAndFeel();
                        return;     
                    }         
            
            
            if (newLook.equalsIgnoreCase("default")){
                //don't set any look and feel
            }else {
                javax.swing.UIManager.setLookAndFeel(newLook);
            }
        }catch (Exception ex){

            DV.writeFile("theme.ini", "DEFAULT", false);
           /*Contimue with default theme */
        }
    }

    private void getLookAndFeel(){
        currentLookAndFeel = DV.readFile("theme.ini");
    }

    private String file_sep = System.getProperty("file.separator");

    private void launchPaymentSystem(){

        boolean usePaymentSystem = false;

        String paymentURL = props.getProp("PAYMENT URL");

        if (paymentURL.length() > 0) {
            usePaymentSystem = true;
        }else {
            usePaymentSystem = false;
        }
        

        if (usePaymentSystem == false){
            
            javax.swing.JOptionPane.showMessageDialog(null,
            "It appears that you have not configured an external payment system." + nl +
                    "Go to File-->Settings-->Output to configure a payment system.");
            
            return;
        }

        if (usePaymentSystem){

            boolean webPayment = DV.parseBool(props.getProp("WEB PAYMENT"), false);
                if (webPayment){
                    String url = paymentURL;
                    if (!url.contains("http://") && !url.contains("HTTP://")
                && !url.contains("https://") && !url.contains("HTTPS://")){

                        javax.swing.JOptionPane.showMessageDialog(null,
                                "You must spcifiy a protocol in the web address" +nl +
                                "Example: http://www.paypal.com instead of just www.paypal.com");
                        return;
                    }
                    int a = DV.launchURL(paymentURL);
                    if (a < 1)
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "There was a problem trying to launch your web browser." +
                            nl + "This may not be supported by your Operating System." );

                }else {

                            String nl = System.getProperty("line.separator");

        String osName = System.getProperty("os.name" );

            try {

                if(osName.contains("Windows")){
                Runtime.getRuntime().exec('"' + paymentURL + '"');
                }
                //FOR WINDOWS NT/XP/2000 USE CMD.EXE
                else {

                    //System.out.println(acro + " " + file);
                    Runtime.getRuntime().exec(paymentURL);

                }
            } catch (IOException ex) {

                javax.swing.JOptionPane.showMessageDialog(null,
                        "error: There was a problem launching the payment system!"+ nl +
                        "<<" + paymentURL + ">>");
                //ex.printStackTrace();
            }

                }

            }


    }

    

private void closeAll() {
    
    try {
            
        if (fl != null){
            fl.release();
            fos.close();
        }    
        
          
        java.io.File f = new java.io.File("data.run");
            f.delete();
        f = new java.io.File("nevitium.run");
            f.delete();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
              
        DV.writeFile("lastco.inf", workingPath, false);
        
        dbsys.close();
        dbsys = null;
        
        System.exit(0);
    
    
}

	public void buildMenu(){
		/* BEGIN MENU INST */
		jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newCompanyItem = new javax.swing.JMenuItem();
        backupItem = new javax.swing.JMenuItem();
        exportItem = new javax.swing.JMenuItem();
        upgradeExportItem = new javax.swing.JMenuItem();
        upgradeImportItem = new javax.swing.JMenuItem();
        conversionImport = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        settingsItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        switchItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        enhancedModeBox = new javax.swing.JCheckBoxMenuItem();
        toolsMenu = new javax.swing.JMenu();
        connectionsItem = new javax.swing.JMenuItem();
        inventoryItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        invoiceItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        quickItem = new javax.swing.JMenuItem();
        checkMenuItem = new javax.swing.JMenuItem();
        workOrderItem = new javax.swing.JMenuItem();
        layoutManagerItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        paymentSystemMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        prepaidItem = new javax.swing.JMenuItem();
        reportMenu = new javax.swing.JMenu();
        outstandingItem = new javax.swing.JMenuItem();
        salesItem = new javax.swing.JMenuItem();
        revenueItem = new javax.swing.JMenuItem();
        miscInvoiceReportItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        inventoryReportItem = new javax.swing.JMenuItem();
        reorderReport = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        custReport = new javax.swing.JMenuItem();
        CustPhoneList = new javax.swing.JMenuItem();
        vendorList = new javax.swing.JMenuItem();
        VendorPhoneList = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpItem = new javax.swing.JMenuItem();
        manualItem = new javax.swing.JMenuItem();
        infoItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        creditsItem = new javax.swing.JMenuItem();
		/* END MENU INST */
        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        
        newCompanyItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Open file.png"))); // NOI18N
        newCompanyItem.setText("Open/Create New Company Folder");
        newCompanyItem.setToolTipText("Browse - Open or create a new company");
        newCompanyItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCompanyItemActionPerformed(evt);
            }
        });
        fileMenu.add(newCompanyItem);

        backupItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Archive.png")));
        backupItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        backupItem.setText("Backup to ZIP File");
        backupItem.setToolTipText("Create a backup of the company folder in a compressed file");
        backupItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backupItemActionPerformed(evt);
            }
        });
        fileMenu.add(backupItem);

        exportItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Export text.png")));
        exportItem.setText("Basic Export");
        exportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportItem);

        upgradeExportItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Export table.png")));
        upgradeExportItem.setText("Full Export");
        upgradeExportItem.setToolTipText("Creates a ZIP file containing a text file for each data table");
        upgradeExportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upgradeExportItemActionPerformed(evt);
            }
        });
        fileMenu.add(upgradeExportItem);

        upgradeImportItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Import table.png")));
        upgradeImportItem.setText("Full Import");
        upgradeImportItem.setToolTipText("Import a full text file backup into the current company");
        upgradeImportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upgradeImportItemActionPerformed(evt);
            }
        });
        fileMenu.add(upgradeImportItem);

        conversionImport.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Import text.png")));
        conversionImport.setText("Convert from v1.4 Full Export");
        conversionImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conversionImportActionPerformed(evt);
            }
        });
        fileMenu.add(conversionImport);
        fileMenu.add(jSeparator1);

        settingsItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Configuration.png")));
        settingsItem.setText("Settings");
        settingsItem.setToolTipText("Customize & Configure Nevitium to your needs");
        settingsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsItemActionPerformed(evt);
            }
        });
        fileMenu.add(settingsItem);
        fileMenu.add(jSeparator3);

        switchItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/User login.png")));
        switchItem.setText("Change User");
        switchItem.setToolTipText("Protect your data with security");
        switchItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchItemActionPerformed(evt);
            }
        });
        fileMenu.add(switchItem);

        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        exitItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Close.png")));
        exitItem.setText("EXIT");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);
        fileMenu.add(jSeparator10);


        enhancedModeBox.setSelected(true);
        enhancedModeBox.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Data.png")));
        enhancedModeBox.setText("Enhanced Mode");
        enhancedModeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enhancedModeBoxActionPerformed(evt);
            }
        });
        fileMenu.add(enhancedModeBox);

        jMenuBar1.add(fileMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");
        toolsMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toolsMenuMouseClicked(evt);
            }
        });

        connectionsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        connectionsItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Conference.png")));
        connectionsItem.setText("My Connections");
        connectionsItem.setToolTipText("Manage your contact information");
        connectionsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionsItemActionPerformed(evt);
            }
        });
        toolsMenu.add(connectionsItem);

        inventoryItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        inventoryItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Tables.png")));
        inventoryItem.setText("My Inventory");
        inventoryItem.setToolTipText("Explore and modify your inventory and services");
        inventoryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryItemActionPerformed(evt);
            }
        });
        toolsMenu.add(inventoryItem);
        toolsMenu.add(jSeparator4);

        invoiceItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        invoiceItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Money.png"))); // NOI18N
        invoiceItem.setMnemonic('F');
        invoiceItem.setText("Invoice Activity");
        invoiceItem.setToolTipText("Manage invoices and quotes or take payments and process returns");
        invoiceItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceItemActionPerformed(evt);
            }
        });
        toolsMenu.add(invoiceItem);
        toolsMenu.add(jSeparator6);

        quickItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        quickItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Barcode scanner1.png"))); // NOI18N
        quickItem.setText("Quick Invoice");
        quickItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quickItemActionPerformed(evt);
            }
        });
        toolsMenu.add(quickItem);

        checkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        checkMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Notebook.png"))); // NOI18N
        checkMenuItem.setText("Print Checks");
        checkMenuItem.setToolTipText("Print standard computer checks on a laser or inkjet printer");
        checkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(checkMenuItem);

        workOrderItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Edit document 3d.png")));
        workOrderItem.setText("Blank Work Order");
        workOrderItem.setToolTipText("Prints a Blank Work Order");
        workOrderItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workOrderItemActionPerformed(evt);
            }
        });
        toolsMenu.add(workOrderItem);

        layoutManagerItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Measure.png")));
        layoutManagerItem.setText("Form Builder");
        layoutManagerItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutManagerItemActionPerformed(evt);
            }
        });
        //toolsMenu.add(layoutManagerItem);
        toolsMenu.add(jSeparator8);

        paymentSystemMenuItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Card terminal.png")));
        paymentSystemMenuItem.setText("Launch Payment System");
        paymentSystemMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentSystemMenuItemActionPerformed1(evt);
            }
        });
        toolsMenu.add(paymentSystemMenuItem);
        toolsMenu.add(jSeparator9);

        prepaidItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Credit cards.png")));
        prepaidItem.setText("Prepaid Account Manager");
        prepaidItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prepaidItemActionPerformed(evt);
            }
        });
        toolsMenu.add(prepaidItem);

        jMenuBar1.add(toolsMenu);

        reportMenu.setMnemonic('R');
        reportMenu.setText("Reports");

        outstandingItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Event manager.png")));
        outstandingItem.setText("Unpaid Invoice Report");
        outstandingItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.CTRL_MASK));
        outstandingItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outstandingItemActionPerformed(evt);
            }
        });
        reportMenu.add(outstandingItem);

        salesItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Datasheet.png")));
        salesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, java.awt.event.InputEvent.CTRL_MASK));
        salesItem.setText("Sales (COGS) Report");
        salesItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesItemActionPerformed(evt);
            }
        });
        reportMenu.add(salesItem);

        revenueItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Datasheet.png")));
        revenueItem.setText("Revenue Report");
        revenueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revenueItemActionPerformed(evt);
            }
        });
        reportMenu.add(revenueItem);

        miscInvoiceReportItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Items.png")));
        miscInvoiceReportItem.setText("Misc Invoice Report");
        miscInvoiceReportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miscInvoiceReportItemActionPerformed(evt);
            }
        });
        reportMenu.add(miscInvoiceReportItem);
        reportMenu.add(jSeparator2);

        inventoryReportItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List 3d.png")));
        inventoryReportItem.setText("Inventory Status Report");
        inventoryReportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryReportItemActionPerformed(evt);
            }
        });
        reportMenu.add(inventoryReportItem);

        reorderReport.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List 3d.png")));
        reorderReport.setText("Inventory Reorder Report");
        reorderReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reorderReportActionPerformed(evt);
            }
        });
        reportMenu.add(reorderReport);
        reportMenu.add(jSeparator5);

        custReport.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List.png")));
        custReport.setText("Customer List");
        custReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                custReportActionPerformed(evt);
            }
        });
        reportMenu.add(custReport);

        CustPhoneList.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Call.png")));
        CustPhoneList.setText("Customer Phone List");
        CustPhoneList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustPhoneListActionPerformed(evt);
            }
        });
        reportMenu.add(CustPhoneList);

        vendorList.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List.png")));
        vendorList.setText("Supplier List");
        vendorList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vendorListActionPerformed(evt);
            }
        });
        reportMenu.add(vendorList);

        VendorPhoneList.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Call.png")));
        VendorPhoneList.setText("Supplier Phone List");
        VendorPhoneList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VendorPhoneListActionPerformed(evt);
            }
        });
        reportMenu.add(VendorPhoneList);

        jMenuBar1.add(reportMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        helpItem.setText("User Manual (www)");
        helpMenu.add(helpItem);

        manualItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        manualItem.setText("User Manual (local)");
        manualItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualItemActionPerformed(evt);
            }
        });
        helpMenu.add(manualItem);

        infoItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Info.png")));
        infoItem.setText("Info");
        infoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoItemActionPerformed(evt);
            }
        });
        helpMenu.add(infoItem);
        helpMenu.add(jSeparator7);

        creditsItem.setIcon (new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Information.png")));
        creditsItem.setText("Credits");
        creditsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditsItemActionPerformed(evt);
            }
        });
        helpMenu.add(creditsItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

		/* END MENU VARS */

	}


    
    private boolean debug = false;
    
    private String workingPath = "data/";
    private Settings props = new Settings(workingPath + "settings.ini");
    private GlobalApplicationDaemon application = new GlobalApplicationDaemon();
    private datavirtue.DbEngine dbsys;
    
    private String unclear = "";
    private Image winIcon;
    private char sep1 = '.';
    private char sep2 = '.';
    private boolean dbProblem = false;
    private String nl = System.getProperty("line.separator");
    private String sys_stat = "";
    
    private DefaultTableModel tm;
    private static boolean RUNNING = false;
    
    private java.io.FileOutputStream fos;
    private java.nio.channels.FileLock fl;
    private String currentLookAndFeel = "DEFAULT";

		/* BEGIN MENU VARS */
	private javax.swing.JMenuItem CustPhoneList;
    private javax.swing.JMenuItem VendorPhoneList;
    private javax.swing.JMenuItem backupItem;
    private javax.swing.JMenuItem checkMenuItem;
    private javax.swing.JMenuItem connectionsItem;
    private javax.swing.JMenuItem conversionImport;
    private javax.swing.JMenuItem creditsItem;
    private javax.swing.JMenuItem custReport;
    private javax.swing.JCheckBoxMenuItem enhancedModeBox;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenuItem exportItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem helpItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem infoItem;
    private javax.swing.JMenuItem inventoryItem;
    private javax.swing.JMenuItem inventoryReportItem;
    private javax.swing.JMenuItem invoiceItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuItem layoutManagerItem;
    private javax.swing.JMenuItem manualItem;
    private javax.swing.JMenuItem miscInvoiceReportItem;
    private javax.swing.JMenuItem newCompanyItem;
    private javax.swing.JMenuItem outstandingItem;
    
    private javax.swing.JMenuItem paymentSystemMenuItem;
    
    private javax.swing.JMenuItem prepaidItem;
    private javax.swing.JMenuItem quickItem;
    
    private javax.swing.JMenuItem reorderReport;
    private javax.swing.JMenu reportMenu;
    private javax.swing.JMenuItem revenueItem;
    
    private javax.swing.JMenuItem salesItem;
    private javax.swing.JMenuItem settingsItem;
    
    private javax.swing.JMenuItem switchItem;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem upgradeExportItem;
    private javax.swing.JMenuItem upgradeImportItem;
    
    private javax.swing.JMenuItem vendorList;
    private javax.swing.JMenuItem workOrderItem;
	

		/* BEGIN ACT PERF */
    private void workOrderItemActionPerformed(java.awt.event.ActionEvent evt) {//event_workOrderItemActionPerformed
        
        boolean stat = ReportFactory.generateWorkOrder(props);
        if (stat == false) {
            javax.swing.JOptionPane.showMessageDialog(null, "A problem occurred while building the workorder.");
        }
        
		}//event_workOrderItemActionPerformed

		private void switchItemActionPerformed(java.awt.event.ActionEvent evt) {//event_switchItemActionPerformed
			secure(true, true);
		}//event_switchItemActionPerformed

		private void checkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//event_checkMenuItemActionPerformed
        
        if (accessKey.checkCheck(500)){
        new CheckDialog(this,true, application, 0, 0, "");
        }else {
            
            accessKey.showMessage("Check Printing");
            
        }
        
		}//event_checkMenuItemActionPerformed

		private void upgradeImportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_upgradeImportItemActionPerformed
        
        
        if (accessKey.checkExports(500)){
        int a = javax.swing.JOptionPane.showConfirmDialog(null,
                "This feature is intended for importing data from a Full Export of "+
                System.getProperty("line.separator")+
                "Nevitium into a blank database and will overwrite your data."+
                System.getProperty("line.separator")+"Do you want to continue?",
                "WARNING",JOptionPane.WARNING_MESSAGE);
        if (a == 0){upgradeImport();}
        }else {
            
            accessKey.showMessage("Export/Import");
                    
        }
        
		}//event_upgradeImportItemActionPerformed

		private void upgradeExportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_upgradeExportItemActionPerformed
        
        /*
         *Get file name to export to (.zip file)
         *
         *Export all .db files including key fields
         *Export jrnls, hold & grps folders
         *
         */
        
        if (accessKey.checkExports(500)){
        upgradeExport();
        }else {
            
            accessKey.showMessage("Export");
            
        }
		}//event_upgradeExportItemActionPerformed

		private void exportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_exportItemActionPerformed
       
        if (accessKey.checkExports(500)){
        new ExportDialog (null, true, dbsys);
        }else {
            
            accessKey.showMessage("Export");
            
        }
		}//event_exportItemActionPerformed

		private void reorderReportActionPerformed(java.awt.event.ActionEvent evt) {//event_reorderReportActionPerformed
        
        if (!accessKey.checkReports(300)){
            accessKey.showMessage("Reports");
            return;
        }
        sys_stat = ReportFactory.generateReorderReport(dbsys, props);
        showStatus();
        
		}//event_reorderReportActionPerformed

		private void vendorListActionPerformed(java.awt.event.ActionEvent evt) {//event_vendorListActionPerformed
                

        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
        sys_stat = ReportFactory.generateCustomerReport (dbsys, props, true);
		showStatus (); 
              
		}//event_vendorListActionPerformed

		private void VendorPhoneListActionPerformed(java.awt.event.ActionEvent evt) {//event_VendorPhoneListActionPerformed
        
        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }

        sys_stat = ReportFactory.generatePhoneList(dbsys, props, false, 11);
        showStatus();
        
		}//event_VendorPhoneListActionPerformed

		private void CustPhoneListActionPerformed(java.awt.event.ActionEvent evt) {//event_CustPhoneListActionPerformed
        

        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
        sys_stat = ReportFactory.generatePhoneList(dbsys, props, true, 11);
        showStatus();
        
		}//event_CustPhoneListActionPerformed

		private void manualItemActionPerformed(java.awt.event.ActionEvent evt) {//event_manualItemActionPerformed
        
        
        DV.launchURL("file://"+ System.getProperty("user.dir") + 
                System.getProperty("file.separator")+
                "doc" + System.getProperty("file.separator") +"manual"+ System.getProperty("file.separator") + "index.html");
        
        
    }//event_manualItemActionPerformed

    private void helpItemActionPerformed(java.awt.event.ActionEvent evt) {//event_helpItemActionPerformed
        
        DV.launchURL("http://www.datavirtue.com/nevitium/manual/");
        
        
    }//event_helpItemActionPerformed

    private void revenueItemActionPerformed(java.awt.event.ActionEvent evt) {//event_revenueItemActionPerformed
        

        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Financial Reports");
            return;
        }
        new ReportTableDialog(this, false, application, "revenue");

        
    }//event_revenueItemActionPerformed

    private void infoItemActionPerformed(java.awt.event.ActionEvent evt) {//event_infoItemActionPerformed
        
        goSettings();
        
    }//event_infoItemActionPerformed

    private void inventoryReportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_inventoryReportItemActionPerformed

        if (!accessKey.checkReports(300)){
            accessKey.showMessage("Reports");
            return;
        }

       sys_stat = ReportFactory.generateInventoryStatusReport(dbsys, props);
       showStatus ();
        
    }//event_inventoryReportItemActionPerformed

    private void newCompanyItemActionPerformed(java.awt.event.ActionEvent evt){

        getCoDialog(false);

    }

    private void backupItemActionPerformed(java.awt.event.ActionEvent evt) {//event_backupItemActionPerformed
        backup();
    }//event_backupItemActionPerformed

    private void outstandingItemActionPerformed(java.awt.event.ActionEvent evt) {//event_outstandingItemActionPerformed
        
        openInvoiceReport();
        
    }//event_outstandingItemActionPerformed
	
	private void custReportActionPerformed(java.awt.event.ActionEvent evt) {//event_custReportActionPerformed
        

        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
        sys_stat = ReportFactory.generateCustomerReport (dbsys, props, false);
        showStatus ();
        
    }//event_custReportActionPerformed

    private void salesItemActionPerformed(java.awt.event.ActionEvent evt) {//event_salesItemActionPerformed
        
        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Financial Reports");
            return;
        }
        new ReportTableDialog(this, false, application, "sales");
    }//event_salesItemActionPerformed

    private void quickItemActionPerformed(java.awt.event.ActionEvent evt) {//event_quickItemActionPerformed

        if (!accessKey.checkInvoice(300)){
            accessKey.showMessage("Invoice/Quote");
            return;
        }

        
            InvoiceDialog id = new InvoiceDialog(this, true, 0, application); //no select            id.setVisible(true);
            id.setVisible(true);
            sys_stat = id.getStat();
            id.dispose(); id = null;
            showStatus ();

        
    }//event_quickItemActionPerformed

    private void invoiceItemActionPerformed(java.awt.event.ActionEvent evt) {//event_invoiceItemActionPerformed

        if (!accessKey.checkManager(300)){
            accessKey.showMessage("Invoice Manager");
            return;
        }
        //InvoiceModel temp = (InvoiceModel) DV.DeSerial("data/hold/I.10010.inv");
        //  InvoiceModel temp = null;             
        //invDialog id = new invDialog (this, true, dbsys, cso, temp); //no select
        InvoiceManager i = new InvoiceManager(this, true, application);
        
        
        sys_stat = i.getStat();
        
        i.dispose();
        
        showStatus ();
    }//event_invoiceItemActionPerformed

    private void inventoryItemActionPerformed(java.awt.event.ActionEvent evt) {//event_inventoryItemActionPerformed
        
            java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        MyInventoryApp id = new MyInventoryApp (null, true, application, false);
                        
                    }
                });
            
            showStatus ();
      
    }//event_inventoryItemActionPerformed

    private void connectionsItemActionPerformed(java.awt.event.ActionEvent evt) {//event_connectionsItemActionPerformed
        Tools.playSound(getClass().getResource("/businessmanager/res/slip.wav"));
        MyConnectionsApp cd = new MyConnectionsApp (this, true, application,false, true,true);
        //cd.setVisible(true);
        
        showStatus ();
    }//event_connectionsItemActionPerformed

    private void settingsItemActionPerformed(java.awt.event.ActionEvent evt) {//event_settingsItemActionPerformed
        
        goSettings();
    }//event_settingsItemActionPerformed

	
	private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {//event_exitItemActionPerformed
      
        closeAll();
        
    }//event_exitItemActionPerformed

    private void paymentSystemMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//event_paymentSystemMenuItemActionPerformed

        launchPaymentSystem();

    }//event_paymentSystemMenuItemActionPerformed

    private void creditsItemActionPerformed(java.awt.event.ActionEvent evt) {//event_creditsItemActionPerformed
        new CreditsDialog(this, true, "credits.txt");
    }//event_creditsItemActionPerformed

    private void prepaidItemActionPerformed(java.awt.event.ActionEvent evt) {//event_prepaidItemActionPerformed
        new GiftCardManager(this, true, application);
    }//event_prepaidItemActionPerformed

    private void conversionImportActionPerformed(java.awt.event.ActionEvent evt) {//event_conversionImportActionPerformed
        conversion14to15();
    }//event_conversionImportActionPerformed

   
    private void toolsMenuMouseClicked(java.awt.event.MouseEvent evt) {//event_toolsMenuMouseClicked
        Tools.playSound(getClass().getResource("/businessmanager/res/slip.wav"));
    }//event_toolsMenuMouseClicked

    private void enhancedModeBoxActionPerformed(java.awt.event.ActionEvent evt) {//event_enhancedModeBoxActionPerformed
        dbsys.setOptimized(enhancedModeBox.isSelected());
    }//event_enhancedModeBoxActionPerformed

    private void layoutManagerItemActionPerformed(java.awt.event.ActionEvent evt) {//event_layoutManagerItemActionPerformed
        new InvoiceLayoutManager(application, "layout.invoice.xml");
    }//event_layoutManagerItemActionPerformed

    private void miscInvoiceReportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_miscInvoiceReportItemActionPerformed
        ReportFactory.generateCustomerStatement(application, 0);
    }//event_miscInvoiceReportItemActionPerformed

    private void paymentSystemMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {
        launchPaymentSystem();
    }
			/* END ACTION PERF */
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activityButton;
    private javax.swing.JButton connectionsButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel internetStatus;
    private javax.swing.JButton inventoryButton;
    private javax.swing.JButton invoiceButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar mainToolbar;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JLabel picLabel;
    private javax.swing.JTextField remoteMessageBox;
    private javax.swing.JButton roleButton;
    private javax.swing.JButton settingsButton;
    private javax.swing.JButton statusButton;
    private javax.swing.JToolBar statusToolbar;
    private javax.swing.JButton userButton;
    // End of variables declaration//GEN-END:variables

}

