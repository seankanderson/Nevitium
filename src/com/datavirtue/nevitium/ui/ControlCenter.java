/*
 * ControlCenter.java
 *
 * Created on June 22, 2006, 9:47 AM
 ** Copyright (c) Data Virtue 2006 - 2022
 */
package com.datavirtue.nevitium.ui;

import com.datavirtue.nevitium.ui.checks.CheckDialog;
import com.datavirtue.nevitium.ui.inventory.MyInventoryApp;
import com.datavirtue.nevitium.ui.contacts.ContactsApp;
import RuntimeManagement.KeyCard;
import RuntimeManagement.GlobalApplicationDaemon;

import com.datavirtue.nevitium.ui.util.Tools;
import com.datavirtue.nevitium.database.reports.ReportFactory;
import com.datavirtue.nevitium.ui.invoices.InvoiceApp;
import com.datavirtue.nevitium.ui.invoices.InvoiceManager;
import com.datavirtue.nevitium.ui.layoutdesigner.InvoiceLayoutManager;
import com.datavirtue.nevitium.database.reports.ReportTableDialog;
import businessmanager.ReturnMessageThread;
import com.datavirtue.nevitium.models.invoices.Invoice;
import datavirtue.*;
import de.schlichtherle.io.*;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import com.datavirtue.nevitium.models.settings.AppSettings;
import com.datavirtue.nevitium.models.settings.LocalAppSettings;
import com.datavirtue.nevitium.services.AppSettingsService;
import com.datavirtue.nevitium.services.DatabaseService;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.LocalSettingsService;
import com.datavirtue.nevitium.services.UserService;
import com.google.inject.Inject;
import java.sql.SQLException;
import java.util.prefs.BackingStoreException;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2022 All Rights Reserved.
 */
public class ControlCenter extends javax.swing.JFrame {

    private KeyCard accessKey;
    private Toolkit tools = Toolkit.getDefaultToolkit();
    @Inject
    private final AppSettingsService appSettingsService = null;
    private AppSettings appSettings;
    private LocalAppSettings localSettings;
    @Inject
    private UserService userService;

    /**
     * Creates new form ControlCenter
     */
    public ControlCenter() {

        //this.getLookAndFeel();
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    recordWindowSizeAndPosition();
                } catch (BackingStoreException ex) {
                    ExceptionService.showErrorDialog(e.getComponent(), ex, "Error saving local screen preferences");
                }
                closeAll();
            }
        });

        System.setProperty("http.agent", "Nevitium");
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        initComponents();
        buildMenu();
         
    }
    
    private void recordWindowSizeAndPosition() throws BackingStoreException {
        var screenSettings = localSettings.getScreenSettings();     
        var sizeAndPosition = LocalSettingsService.getWindowSizeAndPosition(this);
        screenSettings.setMain(sizeAndPosition);
        LocalSettingsService.saveLocalAppSettings(localSettings);
    }

    private void restoreSavedWindowSizeAndPosition() throws BackingStoreException {
        var screenSettings = localSettings.getScreenSettings().getMain();       
        LocalSettingsService.applyScreenSizeAndPosition(screenSettings, this);
    }


    public void display() throws BackingStoreException {
        
        appSettingsService.setObjectType(AppSettings.class);
        localSettings = LocalSettingsService.getLocalAppSettings();
        
        mainToolbar.setLayout(new FlowLayout());
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        try {

            appSettings = this.appSettingsService.getObject();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error fetching settings from database");
        }
        updateMessage();
        setBG();
        restoreSavedWindowSizeAndPosition();
        this.setVisible(true);
        this.authenticateUser();
    }

    private void setBG() {

        try {
            String screenPic = appSettingsService.getObject().getCompany().getMainScreenLogo();
            picLabel.setIcon(new javax.swing.ImageIcon(screenPic));
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error fetching settings");
        }

    }

    private void updateMessage() {

        if (!appSettings.getInternet().isShowRemoteMessage()) {
            internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Disconnect.png")));
        } else {
            internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Connect.png")));
        }
        if (appSettings.getInternet().isShowRemoteMessage()) {
            /* Thread Example */
            ReturnMessageThread rm = new ReturnMessageThread("http://datavirtue.com/nevitium/update/nevstat.txt",
                    remoteMessageBox, internetStatus);
            rm.start();

        } else {

            remoteMessageBox.setText(" Please visit datavirtue.com for updates & support.");

        }
    }

    private void backup() {

    }

    private void restore() {

    }

    private void authenticateUser() {
        try {
            if (this.userService.isSecurityEnabled()) {
                var accessDialog = new AccessDialog(this, true);

                accessDialog.display();

                if (accessDialog.wasCanceled() || accessDialog.getUser() == null) {
                    System.exit(0);
                } else {
                    UserService.setCurrentUser(accessDialog.getUser());
                }
            }

        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error checking security status in database");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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
        statusMessagePanel = new javax.swing.JPanel();
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

        statusMessagePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout statusMessagePanelLayout = new org.jdesktop.layout.GroupLayout(statusMessagePanel);
        statusMessagePanel.setLayout(statusMessagePanelLayout);
        statusMessagePanelLayout.setHorizontalGroup(
            statusMessagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 627, Short.MAX_VALUE)
        );
        statusMessagePanelLayout.setVerticalGroup(
            statusMessagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 124, Short.MAX_VALUE)
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
                    .add(statusMessagePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusMessagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(remoteMessageBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(internetStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void custInvoiceHistory() {

        ContactsApp cd = new ContactsApp(this, true, application, true, true, false);
        cd.setVisible(true);

        var contact = cd.getReturnValue();  //real value

        if (contact == null) {
            return;
        }

        cd.dispose();
        ReportFactory.generateCustomerStatement(application, contact);

    }

    private boolean getCoDialog(boolean exitOnCancel) {

        OpenCompanyDialog ocd = new OpenCompanyDialog(null, true, "prev.inf");
        String action = ocd.getStatus();

        if (action.equals("cancel")) {

            if (exitOnCancel) {
                System.exit(0);
            }
            return true;
        }
        if (action.equals("open")) {
            return this.openCompany();
        }
        if (action.equals("create")) {
            return this.newCompany();
        }
        if (action.equals("previous")) {
            //return this.previousCompany(ocd.getPath());
        }
        ocd = null;

        return false;

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

        if (debug) {
            System.out.println("NEW CO Path Name: " + newPath);
        }
        /* Make sure the folder is empty */
 /* Create the ver.inf OR open.run file */

        if (newPath.trim().equals("")) {
            return false;
        }

        if (!Tools.isFolderEmpty(newPath)) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "The selected folder is not valid or already contains files; please select an empty folder.");
            return false;
        }

        int a = javax.swing.JOptionPane.showConfirmDialog(null, "Do you need support for non-English text?", "Character Encoding", JOptionPane.YES_NO_OPTION);
        if (a == 0) {

            DV.writeFile(newPath + "encode.char", "UTF", false);

        } else {
            DV.writeFile(newPath + "encode.char", "ASCII", false);
        }

        return createCompany(newPath);

    }

    private boolean createCompany(String path) {

        return true;
    }

    private boolean openCompany() {

        return true;
    }

    /* Used by openCompany, newCompany, getCoDialog, Constructor */
    private boolean setCompany() {

        return true;
    }

    private void upgradeImport() {

        JFileChooser fileChooser = DV.getFileChooser("..");

        java.io.File curFile = fileChooser.getSelectedFile();
        if (curFile == null) {
            return;
        }

        if (!curFile.exists()) {

            return;

        }

        String p = curFile.toString();

        String current_version = DV.readFile("ver.inf").trim();

        de.schlichtherle.io.File f = new de.schlichtherle.io.File(p);

        String[] files = f.list();

        boolean good_import = true;

        if (files != null) {

            int[] results = DV.whichContains(files, "ver.inf");
            if (results.length < 1) {
                good_import = false;
            }

        } else {
            good_import = false;
        }

        if (!good_import) {

            /* tell the asshole */
            javax.swing.JOptionPane.showMessageDialog(null, "The file you tried to import is not a Nevitium Full Export.");
            return;

        }

        /*Grab the ver file from the import  */
        new de.schlichtherle.io.File(p + "/ver.inf").copyTo(new File("impver.inf"));

        String import_version = DV.readFile("impver.inf");
        new de.schlichtherle.io.File("impver.inf").delete();

        if (!import_version.trim().equals(current_version.trim())
                && !import_version.trim().equals("Version 1.3")) {  // || OR this to allow more versions

            javax.swing.JOptionPane.showMessageDialog(null, "Version mismatch.  Needed: " + current_version + "   Found: " + import_version);
            return;
        }

        //dbsys.csvImport("...", f,true, a );
        f = new de.schlichtherle.io.File(p + "/inventory.csv");
        int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        if (f.exists()) {
            //new ImportDialog(null, true, f, dbsys, "inventory", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/invnotes.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "invnotes", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/conn.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "conn", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/connship.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "connship", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/invoice.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "invoice", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/shipto.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "shipto", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/invitems.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "invitems", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/payments.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "payments", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/returns.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "returns", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/messages.csv");
        a = new int[]{0, 1, 2};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "messages", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/quote.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "quote", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/qitems.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "qitems", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/qshipto.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "qshipto", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/invcat.csv");
        a = new int[]{0, 1};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "invcat", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/imageref.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "imageref", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/card.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "card", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/users.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "users", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/countries.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "countries", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/auditlog.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "auditlog", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/miscitems.csv");
        a = new int[]{0, 1};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "miscitems", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/checkpayee.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "checkpayee", a, true, true);
        }

        f = new de.schlichtherle.io.File(p + "/invtcat.csv");
        a = new int[]{0, 1, 2, 3};
        if (f.exists()) {
//            new ImportDialog(null, true, f, dbsys, "invtcat", a, true, true);
        }

//        f = new de.schlichtherle.io.File(workingPath + "grps/");
        if (f.exists()) {
            new de.schlichtherle.io.File(p + "/grps/").copyAllTo(f);
        }

//        f = new de.schlichtherle.io.File(workingPath + "jrnls/");
        if (f.exists()) {
            new de.schlichtherle.io.File(p + "/jrnls/").copyAllTo(f);
        }

//        f = new de.schlichtherle.io.File(workingPath + "settings.ini");
        if (f.exists()) {
            new de.schlichtherle.io.File(p + "/settings.ini").copyTo(f);
        }

        try {

            f.umount(true, true, true, true);

        } catch (ArchiveException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, ex.toString());
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Please Restart Nevitium.");
        }

    }

    private void upgradeExport() {

        String tag = "";
        if (System.getProperty("os.name").contains("Windows")) {
            tag = "\\My Documents\\";
        }

        com.datavirtue.nevitium.ui.FileDialog fd = new FileDialog(null, true, System.getProperty("user.home") + tag, "Upgrade_Export_" + DV.getShortDate().replace('/', '-'));
        fd.setVisible(true);

        if (fd.getPath().equals("")) {
            return;
        }
        // start here
        String p = fd.getPath();

        if (!p.toLowerCase().endsWith(".zip")) {
            p = p + ".zip";
        }

        de.schlichtherle.io.File file = new de.schlichtherle.io.File(p);

        file.mkdirs();

        StatusDialog sd = new StatusDialog(this, false, "Full Export Status", true);
        sd.changeMessage("Exporting database files to .csv format");

        /*  */
        sd.addStatus("Initializing Files...");
        sd.addStatus("Exporting Inventory...");
        de.schlichtherle.io.File f = new de.schlichtherle.io.File(p + "/inventory.csv");
        int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
//        dbsys.csvExport("inventory", f, a);

        sd.addStatus("Exporting Inventory Notes...");
        f = new de.schlichtherle.io.File(p + "/invnotes.csv");
        a = new int[]{0, 1, 2, 3};
//        dbsys.csvExport("invnotes", f, a);

        sd.addStatus("Exporting My Connections...");
        f = new de.schlichtherle.io.File(p + "/conn.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        //dbsys.csvExport("conn", f, a);

        sd.addStatus("Exporting My Connections Ship To...");
        f = new de.schlichtherle.io.File(p + "/connship.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        //dbsys.csvExport("connship", f, a);

        sd.addStatus("Exporting Country data...");
        f = new de.schlichtherle.io.File(p + "/countries.csv");
        a = new int[]{0, 1, 2, 3, 4, 5};
        //dbsys.csvExport("countries", f, a);

        sd.addStatus("Exporting Invoices...");
        f = new de.schlichtherle.io.File(p + "/invoice.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        //dbsys.csvExport("invoice", f, a);

        sd.addStatus("Exporting Invoice ShipTo...");
        f = new de.schlichtherle.io.File(p + "/shipto.csv");
        a = new int[]{0, 1, 2, 3};
        //dbsys.csvExport("shipto", f, a);

        sd.addStatus("Exporting Invoice Items...");
        f = new de.schlichtherle.io.File(p + "/invitems.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        //dbsys.csvExport("invitems", f, a);

        sd.addStatus("Exporting Invoice Returns...");
        f = new de.schlichtherle.io.File(p + "/returns.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        //dbsys.csvExport("returns", f, a);

        sd.addStatus("Exporting Invoice Payments...");
        f = new de.schlichtherle.io.File(p + "/payments.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6};
        //dbsys.csvExport("payments", f, a);

        sd.addStatus("Exporting Quotes...");
        f = new de.schlichtherle.io.File(p + "/quote.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        //dbsys.csvExport("quote", f, a);

        sd.addStatus("Exporting Quote Items...");
        f = new de.schlichtherle.io.File(p + "/qitems.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        //dbsys.csvExport("qitems", f, a);

        sd.addStatus("Exporting Quote ShipTo...");
        f = new de.schlichtherle.io.File(p + "/qshipto.csv");
        a = new int[]{0, 1, 2, 3};
        //dbsys.csvExport("qshipto", f, a);

        sd.addStatus("Exporting Prepaid Accounts...");
        f = new de.schlichtherle.io.File(p + "/card.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6};
        //dbsys.csvExport("card", f, a);

        sd.addStatus("Exporting Users...");
        f = new de.schlichtherle.io.File(p + "/users.csv");
        a = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        //dbsys.csvExport("users", f, a);

        sd.addStatus("Exporting Audit Log...");
        f = new de.schlichtherle.io.File(p + "/auditlog.csv");
        a = new int[]{0, 1, 2, 3};
        //dbsys.csvExport("auditlog", f, a);

        sd.addStatus("Exporting Misc Items Memory...");
        f = new de.schlichtherle.io.File(p + "/miscitems.csv");
        a = new int[]{0, 1};
        //dbsys.csvExport("miscitems", f, a);

        sd.addStatus("Exporting Check Payees Memory...");
        f = new de.schlichtherle.io.File(p + "/chkpayee.csv");
        a = new int[]{0, 1};
        //dbsys.csvExport("chkpayee", f, a);

        sd.addStatus("Exporting Invoice Messages...");
        f = new de.schlichtherle.io.File(p + "/messages.csv");
        a = new int[]{0, 1, 2};
        //dbsys.csvExport("messages", f, a);

        sd.addStatus("Exporting Inventory Category Memory...");
        f = new de.schlichtherle.io.File(p + "/invtcat.csv");
        a = new int[]{0, 1};
        //dbsys.csvExport("invtcat", f, a);

        sd.addStatus("Exporting Inventory Image References...");
        f = new de.schlichtherle.io.File(p + "/imageref.csv");
        a = new int[]{0, 1, 2, 3};
        //dbsys.csvExport("imgref", f, a);

        sd.addStatus("Copying Inventory Groups...");
        f = new de.schlichtherle.io.File(p + "/grps/");
//        new de.schlichtherle.io.File(workingPath + "grps").copyAllTo(f);

        sd.addStatus("Copying My Connections Journals...");
        f = new de.schlichtherle.io.File(p + "/jrnls/");
//        new de.schlichtherle.io.File(workingPath + "jrnls").copyAllTo(f);
//
        sd.addStatus("Copying Settings.ini...");
//        new de.schlichtherle.io.File(workingPath + "settings.ini").copyTo(new de.schlichtherle.io.File(p + "/settings.ini"));

        sd.addStatus("Copying ver.inf...");
        new de.schlichtherle.io.File("ver.inf").copyTo(new de.schlichtherle.io.File(p + "/ver.inf"));

        try {
            f.umount(true, true, true, true);
            sd.addStatus("Data successfully archived to " + p);
        } catch (ArchiveException ex) {
            sd.addStatus("An Archive Exception has occured trying to create " + p);
        }

    }

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        var settingsDialog = new SettingsDialog(this, true, 0);
        settingsDialog.display();
        updateMessage();
        setBG();
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void connectionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectionsButtonActionPerformed
        //Tools.playSound(getClass().getResource("/businessmanager/res/slip.wav"));
        ContactsApp cd = new ContactsApp(this, true, application, false, true, true);

        try {
            cd.display();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error getting contacts");
        }

    }//GEN-LAST:event_connectionsButtonActionPerformed

    private void invoiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceButtonActionPerformed
        
        var invoiceDialog = new InvoiceApp(this, true);
        invoiceDialog.display();
        invoiceDialog.dispose();
    }//GEN-LAST:event_invoiceButtonActionPerformed

    private void activityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activityButtonActionPerformed
        if (!accessKey.checkManager(300)) {
            accessKey.showMessage("Invoice Manager");
            return;
        }
        //InvoiceModel temp = (InvoiceModel) DV.DeSerial("data/hold/I.10010.inv");
        //  InvoiceModel temp = null;             
        //invDialog id = new invDialog (this, true, dbsys, cso, temp); //no select
        InvoiceManager i = new InvoiceManager(this, true, application);

        sys_stat = i.getStat();

        i.dispose();


    }//GEN-LAST:event_activityButtonActionPerformed

    private void inventoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventoryButtonActionPerformed
        launchInventoryApp();

    }//GEN-LAST:event_inventoryButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        closeAll();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void closeAll() {
       DatabaseService.closeDatabaseConnections();       
       System.exit(0);
    }

    private void openInvoiceReport() {

        if (!accessKey.checkReports(300)) {
            accessKey.showMessage("Reports");
            return;
        }

        sys_stat = ReportFactory.generateOpenInvoiceReport(application);

        if (sys_stat.equals("none")) {

            javax.swing.JOptionPane.showMessageDialog(null, "No open invoices found.");
            sys_stat = "";
        }

    }

    private void goSettings() {

        var settingsDialog = new SettingsDialog(this, true, 8);
        settingsDialog.display();
        updateMessage();
        setBG();

    }

    private String file_sep = System.getProperty("file.separator");

    private void launchPaymentSystem() {

        boolean usePaymentSystem = false;

        String paymentURL = appSettings.getOutput().getPaymentSystemUri();

        if (paymentURL.length() > 0) {
            usePaymentSystem = true;
        } else {
            usePaymentSystem = false;
        }

        if (usePaymentSystem == false) {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "It appears that you have not configured an external payment system." + nl
                    + "Go to File-->Settings-->Output to configure a payment system.");

            return;
        }

        if (usePaymentSystem) {

            boolean webPayment = appSettings.getOutput().isPaymentSystemUriIsWeblink();
            if (webPayment) {
                String url = paymentURL;
                if (!url.contains("http://") && !url.contains("HTTP://")
                        && !url.contains("https://") && !url.contains("HTTPS://")) {

                    javax.swing.JOptionPane.showMessageDialog(null,
                            "You must spcifiy a protocol in the web address" + nl
                            + "Example: http://www.stripe.com instead of just: www.stripe.com");
                    return;
                }
                int a = DV.launchURL(paymentURL);
                if (a < 1) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "There was a problem trying to launch your web browser."
                            + nl + "This may not be supported by your operating system.");
                }

            } else {

                String nl = System.getProperty("line.separator");

                String osName = System.getProperty("os.name");

                try {

                    if (osName.contains("Windows")) {
                        Runtime.getRuntime().exec('"' + paymentURL + '"');
                    } //FOR WINDOWS NT/XP/2000 USE CMD.EXE
                    else {

                        //System.out.println(acro + " " + file);
                        Runtime.getRuntime().exec(paymentURL);

                    }
                } catch (IOException ex) {

                    javax.swing.JOptionPane.showMessageDialog(null,
                            "error: There was a problem launching the payment system!" + nl
                            + "<<" + paymentURL + ">>");
                    //ex.printStackTrace();
                }
            }
        }
    }

    public void buildMenu() {

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

        backupItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Archive.png")));
        backupItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        backupItem.setText("Backup to ZIP File");
        backupItem.setToolTipText("Create a backup of the company folder in a compressed file");
        backupItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backupItemActionPerformed(evt);
            }
        });
        fileMenu.add(backupItem);

        exportItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Export text.png")));
        exportItem.setText("Basic Export");
        exportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportItem);

        upgradeExportItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Export table.png")));
        upgradeExportItem.setText("Full Export");
        upgradeExportItem.setToolTipText("Creates a ZIP file containing a text file for each data table");
        upgradeExportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upgradeExportItemActionPerformed(evt);
            }
        });
        fileMenu.add(upgradeExportItem);

        upgradeImportItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Import table.png")));
        upgradeImportItem.setText("Full Import");
        upgradeImportItem.setToolTipText("Import a full text file backup into the current company");
        upgradeImportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upgradeImportItemActionPerformed(evt);
            }
        });
        fileMenu.add(upgradeImportItem);

        conversionImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Import text.png")));
        conversionImport.setText("Convert from v1.4 Full Export");
        conversionImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conversionImportActionPerformed(evt);
            }
        });
        fileMenu.add(conversionImport);
        fileMenu.add(jSeparator1);

        settingsItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Configuration.png")));
        settingsItem.setText("Settings");
        settingsItem.setToolTipText("Customize & Configure Nevitium to your needs");
        settingsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsItemActionPerformed(evt);
            }
        });
        fileMenu.add(settingsItem);
        fileMenu.add(jSeparator3);

        switchItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/User login.png")));
        switchItem.setText("Change User");
        switchItem.setToolTipText("Protect your data with security");
        switchItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchItemActionPerformed(evt);
            }
        });
        fileMenu.add(switchItem);

        exitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        exitItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Close.png")));
        exitItem.setText("EXIT");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);
        fileMenu.add(jSeparator10);

        enhancedModeBox.setSelected(true);
        enhancedModeBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Data.png")));
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
        connectionsItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Conference.png")));
        connectionsItem.setText("My Connections");
        connectionsItem.setToolTipText("Manage your contact information");
        connectionsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectionsItemActionPerformed(evt);
            }
        });
        toolsMenu.add(connectionsItem);

        inventoryItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        inventoryItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Tables.png")));
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

        workOrderItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Edit document 3d.png")));
        workOrderItem.setText("Blank Work Order");
        workOrderItem.setToolTipText("Prints a Blank Work Order");
        workOrderItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workOrderItemActionPerformed(evt);
            }
        });
        toolsMenu.add(workOrderItem);

        layoutManagerItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Measure.png")));
        layoutManagerItem.setText("Form Builder");
        layoutManagerItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutManagerItemActionPerformed(evt);
            }
        });
        //toolsMenu.add(layoutManagerItem);
        toolsMenu.add(jSeparator8);

        paymentSystemMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Card terminal.png")));
        paymentSystemMenuItem.setText("Launch Payment System");
        paymentSystemMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentSystemMenuItemActionPerformed1(evt);
            }
        });
        toolsMenu.add(paymentSystemMenuItem);
        toolsMenu.add(jSeparator9);

        prepaidItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Credit cards.png")));
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

        outstandingItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Event manager.png")));
        outstandingItem.setText("Unpaid Invoice Report");
        outstandingItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.CTRL_MASK));
        outstandingItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outstandingItemActionPerformed(evt);
            }
        });
        reportMenu.add(outstandingItem);

        salesItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Datasheet.png")));
        salesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, java.awt.event.InputEvent.CTRL_MASK));
        salesItem.setText("Sales (COGS) Report");
        salesItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesItemActionPerformed(evt);
            }
        });
        reportMenu.add(salesItem);

        revenueItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Datasheet.png")));
        revenueItem.setText("Revenue Report");
        revenueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revenueItemActionPerformed(evt);
            }
        });
        reportMenu.add(revenueItem);

        miscInvoiceReportItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Items.png")));
        miscInvoiceReportItem.setText("Misc Invoice Report");
        miscInvoiceReportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miscInvoiceReportItemActionPerformed(evt);
            }
        });
        reportMenu.add(miscInvoiceReportItem);
        reportMenu.add(jSeparator2);

        inventoryReportItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List 3d.png")));
        inventoryReportItem.setText("Inventory Status Report");
        inventoryReportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inventoryReportItemActionPerformed(evt);
            }
        });
        reportMenu.add(inventoryReportItem);

        reorderReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List 3d.png")));
        reorderReport.setText("Inventory Reorder Report");
        reorderReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reorderReportActionPerformed(evt);
            }
        });
        reportMenu.add(reorderReport);
        reportMenu.add(jSeparator5);

        custReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List.png")));
        custReport.setText("Customer List");
        custReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                custReportActionPerformed(evt);
            }
        });
        reportMenu.add(custReport);

        CustPhoneList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Call.png")));
        CustPhoneList.setText("Customer Phone List");
        CustPhoneList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustPhoneListActionPerformed(evt);
            }
        });
        reportMenu.add(CustPhoneList);

        vendorList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/List.png")));
        vendorList.setText("Supplier List");
        vendorList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vendorListActionPerformed(evt);
            }
        });
        reportMenu.add(vendorList);

        VendorPhoneList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Call.png")));
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

        infoItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Info.png")));
        infoItem.setText("Info");
        infoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoItemActionPerformed(evt);
            }
        });
        helpMenu.add(infoItem);
        helpMenu.add(jSeparator7);

        creditsItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Information.png")));
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

    private GlobalApplicationDaemon application = new GlobalApplicationDaemon();

    private Image winIcon;

    private String nl = System.getProperty("line.separator");
    private String sys_stat = "";

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

        boolean stat = ReportFactory.generateWorkOrder(null);
        if (stat == false) {
            javax.swing.JOptionPane.showMessageDialog(null, "A problem occurred while building the workorder.");
        }

    }//event_workOrderItemActionPerformed

    private void switchItemActionPerformed(java.awt.event.ActionEvent evt) {//event_switchItemActionPerformed
        this.authenticateUser();
    }//event_switchItemActionPerformed

    private void checkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//event_checkMenuItemActionPerformed

        if (accessKey.checkCheck(500)) {
            new CheckDialog(this, true, application, null, 0, "");
        } else {

            accessKey.showMessage("Check Printing");

        }

    }//event_checkMenuItemActionPerformed

    private void upgradeImportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_upgradeImportItemActionPerformed

        if (accessKey.checkExports(500)) {
            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "This feature is intended for importing data from a Full Export of "
                    + System.getProperty("line.separator")
                    + "Nevitium into a blank database and will overwrite your data."
                    + System.getProperty("line.separator") + "Do you want to continue?",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
            if (a == 0) {
                upgradeImport();
            }
        } else {

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
        if (accessKey.checkExports(500)) {
            upgradeExport();
        } else {

            accessKey.showMessage("Export");

        }
    }//event_upgradeExportItemActionPerformed

    private void exportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_exportItemActionPerformed

        if (accessKey.checkExports(500)) {
            new ExportDialog(null, true, null);
        } else {

            accessKey.showMessage("Export");

        }
    }//event_exportItemActionPerformed

    private void reorderReportActionPerformed(java.awt.event.ActionEvent evt) {//event_reorderReportActionPerformed

        if (!accessKey.checkReports(300)) {
            accessKey.showMessage("Reports");
            return;
        }
        sys_stat = ReportFactory.generateReorderReport(null, null);

    }//event_reorderReportActionPerformed

    private void vendorListActionPerformed(java.awt.event.ActionEvent evt) {//event_vendorListActionPerformed

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
        sys_stat = ReportFactory.generateCustomerReport(null, null, true);

    }//event_vendorListActionPerformed

    private void VendorPhoneListActionPerformed(java.awt.event.ActionEvent evt) {//event_VendorPhoneListActionPerformed

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }

        sys_stat = ReportFactory.generatePhoneList(null, null, false, 11);

    }//event_VendorPhoneListActionPerformed

    private void CustPhoneListActionPerformed(java.awt.event.ActionEvent evt) {//event_CustPhoneListActionPerformed

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
        sys_stat = ReportFactory.generatePhoneList(null, null, true, 11);

    }//event_CustPhoneListActionPerformed

    private void manualItemActionPerformed(java.awt.event.ActionEvent evt) {//event_manualItemActionPerformed

        DV.launchURL("file://" + System.getProperty("user.dir")
                + System.getProperty("file.separator")
                + "doc" + System.getProperty("file.separator") + "manual" + System.getProperty("file.separator") + "index.html");

    }//event_manualItemActionPerformed

    private void helpItemActionPerformed(java.awt.event.ActionEvent evt) {//event_helpItemActionPerformed

        DV.launchURL("http://www.datavirtue.com/nevitium/manual/");

    }//event_helpItemActionPerformed

    private void revenueItemActionPerformed(java.awt.event.ActionEvent evt) {//event_revenueItemActionPerformed

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Financial Reports");
            return;
        }
        new ReportTableDialog(this, false, application, "revenue");

    }//event_revenueItemActionPerformed

    private void infoItemActionPerformed(java.awt.event.ActionEvent evt) {//event_infoItemActionPerformed

        goSettings();

    }//event_infoItemActionPerformed

    private void inventoryReportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_inventoryReportItemActionPerformed

        if (!accessKey.checkReports(300)) {
            accessKey.showMessage("Reports");
            return;
        }

        sys_stat = ReportFactory.generateInventoryStatusReport(null, null);

    }//event_inventoryReportItemActionPerformed

    private void newCompanyItemActionPerformed(java.awt.event.ActionEvent evt) {

        getCoDialog(false);

    }

    private void backupItemActionPerformed(java.awt.event.ActionEvent evt) {//event_backupItemActionPerformed
        backup();
    }//event_backupItemActionPerformed

    private void outstandingItemActionPerformed(java.awt.event.ActionEvent evt) {//event_outstandingItemActionPerformed

        openInvoiceReport();

    }//event_outstandingItemActionPerformed

    private void custReportActionPerformed(java.awt.event.ActionEvent evt) {//event_custReportActionPerformed

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
        sys_stat = ReportFactory.generateCustomerReport(null, null, false);

    }//event_custReportActionPerformed

    private void salesItemActionPerformed(java.awt.event.ActionEvent evt) {//event_salesItemActionPerformed

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Financial Reports");
            return;
        }
        new ReportTableDialog(this, false, application, "sales");
    }//event_salesItemActionPerformed

    private void quickItemActionPerformed(java.awt.event.ActionEvent evt) {//event_quickItemActionPerformed
        
        var invoiceDialog = new InvoiceApp(this, true);
        invoiceDialog.display();
        invoiceDialog.dispose();
    }//event_quickItemActionPerformed

    private void invoiceItemActionPerformed(java.awt.event.ActionEvent evt) {//event_invoiceItemActionPerformed

        if (!accessKey.checkManager(300)) {
            accessKey.showMessage("Invoice Manager");
            return;
        }
        //InvoiceModel temp = (InvoiceModel) DV.DeSerial("data/hold/I.10010.inv");
        //  InvoiceModel temp = null;             
        //invDialog id = new invDialog (this, true, dbsys, cso, temp); //no select
        InvoiceManager i = new InvoiceManager(this, true, application);

        sys_stat = i.getStat();

        i.dispose();

    }//event_invoiceItemActionPerformed

    private void inventoryItemActionPerformed(java.awt.event.ActionEvent evt) {//event_inventoryItemActionPerformed

     this.launchInventoryApp();

    }//event_inventoryItemActionPerformed

    private void connectionsItemActionPerformed(java.awt.event.ActionEvent evt) {//event_connectionsItemActionPerformed
        Tools.playSound(getClass().getResource("/businessmanager/res/slip.wav"));
        ContactsApp cd = new ContactsApp(this, true, application, false, true, true);
        //cd.setVisible(true);

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

    }//event_conversionImportActionPerformed

    private void toolsMenuMouseClicked(java.awt.event.MouseEvent evt) {//event_toolsMenuMouseClicked
        Tools.playSound(getClass().getResource("/businessmanager/res/slip.wav"));
    }//event_toolsMenuMouseClicked

    private void enhancedModeBoxActionPerformed(java.awt.event.ActionEvent evt) {//event_enhancedModeBoxActionPerformed
        //dbsys.setOptimized(enhancedModeBox.isSelected());
    }//event_enhancedModeBoxActionPerformed

    private void layoutManagerItemActionPerformed(java.awt.event.ActionEvent evt) {//event_layoutManagerItemActionPerformed
        new InvoiceLayoutManager(application, "layout.invoice.xml");
    }//event_layoutManagerItemActionPerformed

    private void miscInvoiceReportItemActionPerformed(java.awt.event.ActionEvent evt) {//event_miscInvoiceReportItemActionPerformed
        ReportFactory.generateCustomerStatement(application, null);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar mainToolbar;
    private javax.swing.JLabel picLabel;
    private javax.swing.JTextField remoteMessageBox;
    private javax.swing.JButton settingsButton;
    private javax.swing.JPanel statusMessagePanel;
    // End of variables declaration//GEN-END:variables

    private void launchInventoryApp() {
         var inventoryApp = new MyInventoryApp(null, true, false);
        try {
            inventoryApp.display();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error starting the inventory app");
        } catch (BackingStoreException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error fetching local settings from the OS");
        }
    }

}
