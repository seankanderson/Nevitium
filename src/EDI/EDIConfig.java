/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EDIConfig.java
 *
 * Created on Sep 4, 2011, 12:50:24 PM
 */
package EDI;

import RuntimeManagement.GlobalApplicationDaemon;
import businessmanager.Common.Tools;
import datavirtue.DV;
import datavirtue.Settings;
import java.io.File;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Data Virtue
 */
public class EDIConfig extends javax.swing.JDialog {
    private String workingPath = "";
    private Settings EDIprops;
    private String key ="";
    private boolean secure = false;
    GlobalApplicationDaemon application;
    private EDIPushDAO dao;
    
    /** Creates new form EDIConfig */
    public EDIConfig(java.awt.Frame parent, boolean modal, GlobalApplicationDaemon application) {
        super(parent, modal);
        initComponents();
        this.workingPath = application.getWorkingPath();
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);
        this.application = application;
        PINGrabber pin = new PINGrabber(null, true);
        key = pin.getKey();
        secure = true;
        if (key.equals("")) {
            secure = false;
            key = "X";
        }
        pin.dispose();
        
        if (loadEDIStation()){
            this.setVisible(true);
        }else this.dispose();
        
        
    }

    private void getHostName(){
        String computerName="Unknown";
            try {
                java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
                computerName = localMachine.getHostName();
            }catch(Exception ex) {
                computerName = "Unknown";
            }
            pcNameField.setText(computerName);
    }
    
    private boolean loadEDIStation(){
        
       try { 
            new File(workingPath+"EDI/").mkdir();
            new File(workingPath+"EDI/logs/").mkdir();
            
            new File(workingPath+"EDI/out/").mkdir();
            new File(workingPath+"EDI/out/email/").mkdir();
            new File(workingPath+"EDI/out/folder/").mkdir();
            new File(workingPath+"EDI/out/ftp/").mkdir();
            
            new File(workingPath+"EDI/in/").mkdir();
            new File(workingPath+"EDI/in/email/").mkdir();
            new File(workingPath+"EDI/in/folder/").mkdir();
            new File(workingPath+"EDI/in/ftp/").mkdir();
            
       }catch(Exception e){
           javax.swing.JOptionPane.showMessageDialog(null, 
                   "A problem occured while building EDI folders. Verify permissions or contact support at datavirtue.com");
           return false;
       }
        
        if (!new File(workingPath+"EDI/edi.ini").exists()){
            
            /* Create Email Resolver Settings */
            
            /* Check for missing edi.ini */
            EDIprops = new Settings(workingPath+"EDI/edi.ini");
            
           
            /* Create Folder Resolver Settings */
            
            getHostName();
            EDIprops.setProp("CHECKSUM", this.encrypt("TEST"));
            
        }
        
        /* Test for proper password  */
        
        loadLocalSettings();
        
        this.clearEmailForm();
        this.loadEmailTargets();
        
                       
        if (!secure){
            //get PWTEST and check against TEST if fails throw credntial error!
            String test = EDIprops.getProp("CHECKSUM");
            if (test.equals("TEST")){
                return true;
            }else{
                //you provided no password yet the local EDI settings are encrypted.
                javax.swing.JOptionPane.showMessageDialog(null, 
                        "You provided no password yet the local EDI settings are encrypted.");
                return false;
            }
        }
        
        if (secure){
            String test = decrypt(EDIprops.getProp("CHECKSUM"));
            if (test==null || test.equals("")) test = "FAIL";
            String crypt = "TEST";
            if (test.equals(crypt)){
                return true;
            }else{
                //you provided the wrong password
                javax.swing.JOptionPane.showMessageDialog(null, 
                        "The password you provided could not be used to decrypt the local EDI settings.");
                return false;
            }
                                
        }
     
        /* Load Settings */
        
        return true;
    }
    
    private String decrypt(String text){
        try {
            String cipher = EDIPBE.decrypt(key.toCharArray(), text);    
            if (cipher==null) return "";
            return cipher;
            }catch(Exception e){
                return text;
            }
    }
    
    private String encrypt(String text){
        try {
            String cipher = EDIPBE.encrypt(key.toCharArray(), text);
            if (cipher==null) return "";
            return cipher;
            }catch(Exception e){
                return text;
            }
    }
    
    private void loadEmailTargets(){
        emailTable.setModel(dao.getEDIEmailTargets());
        this.setEmailView();
        
    }
    
    public void setEmailView (){
        if (emailTable.getColumnCount() < 9) return;
        int [] cols = {0,1,1,1,1,1,2,2};
        if (emailTable.getModel().getRowCount() > 0){
        TableColumnModel cm = emailTable.getColumnModel();
        TableColumn tc;
        //connTable.setCellEditor(null);
        
            for (int i =0; i < cols.length; i++){            
                tc = cm.getColumn(cols[i]);
                emailTable.removeColumn(tc);            
            }               
        }        
    }
    
    private Object [] emailTarget;
    private void populateEmailForm(){
        if (emailTable.getSelectedRow() < 0) return;
        int row = emailTable.getSelectedRow();
        emailTarget = DV.getRow(emailTable.getModel(), row);    
        
        emailTextBox.setText((String)emailTarget[1]);
        emailContactsBox.setSelected((Boolean)emailTarget[2]);
        emailInventoryBox.setSelected((Boolean)emailTarget[3]);
        emailInvoicesBox.setSelected((Boolean)emailTarget[4]);
        emailPrepaidBox.setSelected((Boolean)emailTarget[5]);
        emailSyncBox.setSelected((Boolean)emailTarget[6]);
        emailActiveBox.setSelected((Boolean)emailTarget[7]);
        emailCryptoBox.setSelected((Boolean)emailTarget[8]);
        emailTarget[9] = true;
        
        
    }
    
    private void saveEmailTarget(){
        if (emailTextBox.getText().trim().equals("")) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                    "You must provide an email address to save the target.");
        }
        emailTarget[1] = emailTextBox.getText();
        emailTarget[2] = emailContactsBox.isSelected();
        emailTarget[3] = emailInventoryBox.isSelected();
        emailTarget[4] = emailInvoicesBox.isSelected();
        emailTarget[5] = emailPrepaidBox.isSelected();
        emailTarget[6] = emailSyncBox.isSelected();
        emailTarget[7] = emailActiveBox.isSelected();
        emailTarget[8] = emailCryptoBox.isSelected();
        dao.updateEmailTarget(emailTarget);
        this.loadEmailTargets();
        this.clearEmailForm();
        
    }
    
    private void clearEmailForm(){
        emailTarget = new Object [10];
        emailTarget[0] = new Integer(0);
        emailTarget[9] = true;
        emailTextBox.setText("");
        emailContactsBox.setSelected(false);
        emailInventoryBox.setSelected(false);
        emailInvoicesBox.setSelected(false);
        emailPrepaidBox.setSelected(false);
        emailSyncBox.setSelected(false);
        emailActiveBox.setSelected(false);
        emailCryptoBox.setSelected(false);
        emailTextBox.requestFocus();
                
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        emailTable = new javax.swing.JTable();
        removeEmailButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        emailTextBox = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        emailSyncBox = new javax.swing.JCheckBox();
        emailActiveBox = new javax.swing.JCheckBox();
        emailCryptoBox = new javax.swing.JCheckBox();
        jToolBar1 = new javax.swing.JToolBar();
        addEmailButton = new javax.swing.JButton();
        clearEmailFormButton = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        emailContactsBox = new javax.swing.JCheckBox();
        emailInventoryBox = new javax.swing.JCheckBox();
        emailInvoicesBox = new javax.swing.JCheckBox();
        emailPrepaidBox = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        folderTable = new javax.swing.JTable();
        removeFolderButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        folderTextBox = new javax.swing.JTextField();
        targetBrowseButton = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        folderSyncBox = new javax.swing.JCheckBox();
        targetFolderActiveBox = new javax.swing.JCheckBox();
        addFolderButton = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        folderContactsBox = new javax.swing.JCheckBox();
        folderInventoryBox = new javax.swing.JCheckBox();
        folderInvoicesBox = new javax.swing.JCheckBox();
        folderPrepaidBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ftpTable = new javax.swing.JTable();
        removeFTPButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        FTPServerField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        FTPUserNameField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        FTPPasswordField = new javax.swing.JPasswordField();
        FTPPortField = new javax.swing.JTextField();
        targetSFTPBox = new javax.swing.JCheckBox();
        jPanel16 = new javax.swing.JPanel();
        addFTPButton = new javax.swing.JButton();
        FTPSyncBox = new javax.swing.JCheckBox();
        FTPActiveBox = new javax.swing.JCheckBox();
        jPanel19 = new javax.swing.JPanel();
        ftpContactsBox = new javax.swing.JCheckBox();
        ftpInventoryBox = new javax.swing.JCheckBox();
        ftpInvoicesBox = new javax.swing.JCheckBox();
        ftpPrepaidBox = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        FTPPathField = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        testEDIButton = new javax.swing.JButton();
        saveStationButton = new javax.swing.JButton();
        testButton = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        smtpHostField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        smtpUserField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        smtpPasswordField = new javax.swing.JPasswordField();
        smtpSSLBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        pop3HostField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        pop3UserField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        pop3PasswordField = new javax.swing.JPasswordField();
        pop3SSLBox = new javax.swing.JCheckBox();
        smtpPortField = new javax.swing.JTextField();
        pop3PortField = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        emailEDIAttemptSpinner = new javax.swing.JSpinner();
        emailEncryptBox = new javax.swing.JCheckBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        pcNameField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        sourceFolderField = new javax.swing.JTextField();
        sourcelBrowseButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        folderActiveBox = new javax.swing.JCheckBox();
        folderEncryptBox = new javax.swing.JCheckBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        ftpServerField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        ftpAccountField = new javax.swing.JTextField();
        ftpPortField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        ftpPasswordField = new javax.swing.JPasswordField();
        SFTPBox = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        ftpPathField = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        ftpAttemptSpinner = new javax.swing.JSpinner();
        ftpEncryptBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        sharedKeyField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Electronic Data Interchange (EDI) Configuration Module");
        setIconImage(null);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(" Target Stations (Push)"));

        emailTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        emailTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                emailTableMouseClicked(evt);
            }
        });
        emailTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                emailTableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(emailTable);

        removeEmailButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        removeEmailButton.setText("Remove");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(" Add Email Target "));

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        emailSyncBox.setText("Auto Sync");

        emailActiveBox.setText("Active");

        emailCryptoBox.setText("Encrypt Data");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        addEmailButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        addEmailButton.setText("Save");
        addEmailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEmailButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addEmailButton);

        clearEmailFormButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Corrupt text.png"))); // NOI18N
        clearEmailFormButton.setText("New");
        clearEmailFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearEmailFormButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(clearEmailFormButton);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(emailSyncBox)
                .addGap(18, 18, 18)
                .addComponent(emailActiveBox)
                .addGap(18, 18, 18)
                .addComponent(emailCryptoBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(emailSyncBox)
                        .addComponent(emailActiveBox)
                        .addComponent(emailCryptoBox)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        emailContactsBox.setText("My Connections");

        emailInventoryBox.setText("Inventory");

        emailInvoicesBox.setText("Invoices");
        emailInvoicesBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailInvoicesBoxActionPerformed(evt);
            }
        });

        emailPrepaidBox.setText("Prepaid");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(emailContactsBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailInventoryBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailInvoicesBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailPrepaidBox)
                .addContainerGap(132, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailContactsBox)
                    .addComponent(emailInventoryBox)
                    .addComponent(emailInvoicesBox)
                    .addComponent(emailPrepaidBox))
                .addContainerGap())
        );

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Email Address");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addComponent(removeEmailButton))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeEmailButton)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Email Targets", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Address book.png")), jPanel3); // NOI18N

        folderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(folderTable);

        removeFolderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        removeFolderButton.setText("Remove");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(" Add Folder Target "));

        folderTextBox.setToolTipText("Begin with ftp:// to create an FTP target.");

        targetBrowseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Find in folder.png"))); // NOI18N

        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        folderSyncBox.setText("Auto Sync");

        targetFolderActiveBox.setText("Active");

        addFolderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        addFolderButton.setText("Save");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(folderSyncBox)
                .addGap(18, 18, 18)
                .addComponent(targetFolderActiveBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 199, Short.MAX_VALUE)
                .addComponent(addFolderButton)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(folderSyncBox)
                        .addComponent(targetFolderActiveBox))
                    .addComponent(addFolderButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        folderContactsBox.setText("My Connections");

        folderInventoryBox.setText("Inventory");

        folderInvoicesBox.setText("Invoices");

        folderPrepaidBox.setText("Prepaid");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(folderContactsBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(folderInventoryBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(folderInvoicesBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(folderPrepaidBox)
                .addContainerGap(132, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(folderContactsBox)
                    .addComponent(folderInventoryBox)
                    .addComponent(folderInvoicesBox)
                    .addComponent(folderPrepaidBox))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(folderTextBox, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(targetBrowseButton))
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(folderTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addComponent(removeFolderButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeFolderButton)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Folder Targets", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Folder tree.png")), jPanel4); // NOI18N

        ftpTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(ftpTable);

        removeFTPButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        removeFTPButton.setText("Remove");

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(" Add FTP Target "));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("User Name");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Password");

        FTPPortField.setText("21");

        targetSFTPBox.setText("SFTP");

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        addFTPButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        addFTPButton.setText("Save");

        FTPSyncBox.setText("Auto Sync");

        FTPActiveBox.setText("Active");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FTPSyncBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(FTPActiveBox)
                .addGap(199, 199, 199)
                .addComponent(addFTPButton)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addFTPButton)
                    .addComponent(FTPActiveBox)
                    .addComponent(FTPSyncBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ftpContactsBox.setText("My Connections");

        ftpInventoryBox.setText("Inventory");

        ftpInvoicesBox.setText("Invoices");

        ftpPrepaidBox.setText("Prepaid");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ftpContactsBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ftpInventoryBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ftpInvoicesBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ftpPrepaidBox)
                .addContainerGap(132, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ftpContactsBox)
                    .addComponent(ftpInventoryBox)
                    .addComponent(ftpInvoicesBox)
                    .addComponent(ftpPrepaidBox))
                .addContainerGap())
        );

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("FTP Server");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("FTP Path");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(FTPServerField, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(FTPPortField, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                                    .addComponent(FTPPathField, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FTPPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(FTPUserNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(targetSFTPBox)
                        .addGap(176, 176, 176))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FTPServerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FTPPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(FTPPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(FTPUserNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(FTPPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(targetSFTPBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addComponent(removeFTPButton))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeFTPButton)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("FTP Targets", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Site map.png")), jPanel7); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
        );

        testEDIButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Refresh.png"))); // NOI18N
        testEDIButton.setText("Test This Station's Settings");

        saveStationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        saveStationButton.setText("Save");
        saveStationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveStationButtonActionPerformed(evt);
            }
        });

        testButton.setText("TEST");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(testEDIButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(testButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addComponent(saveStationButton)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testEDIButton)
                    .addComponent(saveStationButton)
                    .addComponent(testButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(" Local Station (Pull)"));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("SMTP Server");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("SMTP User Account");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("SMTP Password");

        smtpSSLBox.setText("SMTP SSL");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Email Address");

        emailField.setToolTipText("Email addressed checked by this station.");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("POP3 Server");

        pop3HostField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pop3HostFieldActionPerformed(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("POP3 User Account");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("POP3 Password");

        pop3SSLBox.setText("POP3 SSL");
        pop3SSLBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        pop3SSLBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        smtpPortField.setText("25");
        smtpPortField.setPreferredSize(new java.awt.Dimension(24, 20));
        smtpPortField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smtpPortFieldActionPerformed(evt);
            }
        });

        pop3PortField.setText("110");

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setText("EDI Attempt");

        emailEDIAttemptSpinner.setOpaque(false);

        emailEncryptBox.setText("Encrypt/Decrypt Data");
        emailEncryptBox.setToolTipText("All participating stations (targets) must have the same setting.");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailEDIAttemptSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                .addComponent(emailEncryptBox)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(emailEDIAttemptSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailEncryptBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pop3UserField, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(pop3PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pop3SSLBox))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(pop3HostField, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pop3PortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(smtpUserField, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(smtpHostField, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(smtpPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(smtpPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(smtpSSLBox))
                            .addComponent(emailField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)))
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(smtpHostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(smtpPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(smtpUserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(smtpPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(smtpSSLBox))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(pop3HostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pop3PortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(pop3UserField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(pop3PasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pop3SSLBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Email Resolver", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Address book.png")), jPanel1); // NOI18N

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Station Name");

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Source Folder");

        sourcelBrowseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Find in folder.png"))); // NOI18N

        jLabel15.setText("Must be unique.");

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        folderActiveBox.setText("Active");

        folderEncryptBox.setText("Encrypt/Decrypt Data");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(folderActiveBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 272, Short.MAX_VALUE)
                .addComponent(folderEncryptBox)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(folderActiveBox)
                    .addComponent(folderEncryptBox))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(pcNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(sourceFolderField, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sourcelBrowseButton)))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(pcNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sourceFolderField)
                        .addComponent(jLabel14))
                    .addComponent(sourcelBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(257, 257, 257))
        );

        jTabbedPane2.addTab(" Folder Resolver", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Folder tree.png")), jPanel11); // NOI18N

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("FTP Server");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("FTP Account");

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("FTP Password");

        SFTPBox.setText("SFTP");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("FTP Path");

        ftpPathField.setText("/");

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("EDI Attempt");

        ftpEncryptBox.setText("Encrypt/Decrypt Data");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ftpAttemptSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE)
                .addComponent(ftpEncryptBox)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(ftpAttemptSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftpEncryptBox))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(ftpServerField, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftpPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(ftpPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(SFTPBox))
                            .addComponent(ftpAccountField, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftpPathField, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(ftpServerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftpPortField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(ftpAccountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(ftpPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SFTPBox))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(ftpPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(167, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("FTP Resolver", new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Site map.png")), jPanel12); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Shared Key");

        sharedKeyField.setToolTipText("Used to encrypt and decrypt data - all targets must have the same key.");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sharedKeyField, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sharedKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pop3HostFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pop3HostFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pop3HostFieldActionPerformed

    private void smtpPortFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smtpPortFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_smtpPortFieldActionPerformed

    private void saveStationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveStationButtonActionPerformed
        saveLocalSettings();
        
        
    }//GEN-LAST:event_saveStationButtonActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_testButtonActionPerformed

    private void emailTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emailTableMouseClicked
        this.populateEmailForm();
    }//GEN-LAST:event_emailTableMouseClicked

    private void clearEmailFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearEmailFormButtonActionPerformed
        this.clearEmailForm();
    }//GEN-LAST:event_clearEmailFormButtonActionPerformed

    private void emailTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailTableKeyPressed
        this.populateEmailForm();
    }//GEN-LAST:event_emailTableKeyPressed

    private void addEmailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEmailButtonActionPerformed
        saveEmailTarget();
    }//GEN-LAST:event_addEmailButtonActionPerformed

    private void emailInvoicesBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailInvoicesBoxActionPerformed
    
        if (emailInvoicesBox.isSelected()) {
            emailInventoryBox.setSelected(true);
            emailContactsBox.setSelected(true);
        }
            
    }//GEN-LAST:event_emailInvoicesBoxActionPerformed

    private void loadLocalSettings(){
        
        /* Load Email Settings */
        EDIprops = new Settings(workingPath+"EDI/edi.ini");
        
        this.emailField.setText(EDIprops.getProp("EMAIL ADDRESS"));
        this.smtpHostField.setText(EDIprops.getProp("SMTP SERVER"));
        this.smtpPortField.setText(EDIprops.getProp("SMTP PORT"));
        this.smtpUserField.setText(EDIprops.getProp("SMTP USER"));
        this.smtpPasswordField.setText(this.decrypt(EDIprops.getProp("SMTP PW")));
        this.smtpSSLBox.setSelected(DV.parseBool(EDIprops.getProp("SMTP SSL"), false));
        
        this.pop3HostField.setText(EDIprops.getProp("POP SERVER"));
        this.pop3PortField.setText(EDIprops.getProp("POP PORT"));
        this.pop3UserField.setText(EDIprops.getProp("POP USER"));
        this.pop3PasswordField.setText(this.decrypt(EDIprops.getProp("POP PW")));
        this.pop3SSLBox.setSelected(DV.parseBool(EDIprops.getProp("POP SSL"), false));
        
        this.emailEDIAttemptSpinner.setValue(DV.parseInt(EDIprops.getProp("EMAIL ATTEMPT")));
        this.emailEncryptBox.setSelected(DV.parseBool(EDIprops.getProp("EMAIL CRYPTO"), false));
        
        /* Load Folder Settings */
        this.pcNameField.setText(EDIprops.getProp("STATION NAME"));
        
        if (pcNameField.getText().equals("")){
            getHostName();
        }
        
        this.sourceFolderField.setText(EDIprops.getProp("SOURCE FOLDER"));
        this.folderActiveBox.setSelected(DV.parseBool(EDIprops.getProp("FOLDER ACTIVE"), false));
        this.folderEncryptBox.setSelected(DV.parseBool(EDIprops.getProp("FOLDER CRYPTO"), false));
        
        /* Load FTP Settings */
        this.ftpServerField.setText(EDIprops.getProp("FTP SERVER"));
        this.ftpPortField.setText(EDIprops.getProp("FTP PORT"));
        this.ftpAccountField.setText(EDIprops.getProp("FTP ACCT"));
        this.ftpPasswordField.setText(this.decrypt(EDIprops.getProp("FTP PW")));
        this.SFTPBox.setSelected(DV.parseBool(EDIprops.getProp("SFTP"), false));
        this.ftpPathField.setText(EDIprops.getProp("FTP PATH"));
        this.ftpAttemptSpinner.setValue(DV.parseInt(EDIprops.getProp("FTP ATTEMPT")));
        this.ftpEncryptBox.setSelected(DV.parseBool(EDIprops.getProp("FTP CRYPTO"), false));
        
        /* Load Shared Key */
        this.sharedKeyField.setText(this.decrypt(EDIprops.getProp("SHARED KEY")));
        dao = new EDIPushDAO(application);        
    }   
    
    
    private void saveLocalSettings(){
        
        /* Save Email Settings  */
        EDIprops.setProp("EMAIL ADDRESS", this.emailField.getText());
        EDIprops.setProp("SMTP SERVER", this.smtpHostField.getText());
        EDIprops.setProp("SMTP PORT", this.smtpPortField.getText());
        EDIprops.setProp("SMTP USER", this.smtpUserField.getText());
        EDIprops.setProp("SMTP PW", this.encrypt(new String(this.smtpPasswordField.getPassword())));
        EDIprops.setProp("SMTP SSL", Tools.getBoolString(this.smtpSSLBox.isSelected()));
        
        EDIprops.setProp("POP SERVER", this.pop3HostField.getText());
        EDIprops.setProp("POP PORT", this.pop3PortField.getText());
        EDIprops.setProp("POP USER", this.pop3UserField.getText());
        EDIprops.setProp("POP PW", this.encrypt((new String(this.pop3PasswordField.getPassword()))));
        EDIprops.setProp("POP SSL", Tools.getBoolString(this.pop3SSLBox.isSelected()));
        EDIprops.setProp("EMAIL ATTEMPT", Integer.toString((Integer)this.emailEDIAttemptSpinner.getValue()));
        EDIprops.setProp("EMAIL CRYPTO", Tools.getBoolString(this.emailEncryptBox.isSelected()));
        
        /* Save Folder Settings */
        EDIprops.setProp("STATION NAME", this.pcNameField.getText());
        EDIprops.setProp("SOURCE FOLDER", this.sourceFolderField.getText());
        EDIprops.setProp("FOLDER ACTIVE", Tools.getBoolString(this.folderActiveBox.isSelected()));
        EDIprops.setProp("FOLDER CRYPTO", Tools.getBoolString(this.folderEncryptBox.isSelected()));
        
        /* Save FTP Settings */
        EDIprops.setProp("FTP SERVER", this.ftpServerField.getText());
        EDIprops.setProp("FTP PORT", this.ftpPortField.getText());
        EDIprops.setProp("FTP ACCT", this.ftpAccountField.getText());
        EDIprops.setProp("FTP PW", this.encrypt(new String(this.ftpPasswordField.getPassword())));
        EDIprops.setProp("SFTP", Tools.getBoolString(this.SFTPBox.isSelected()));
        EDIprops.setProp("FTP PATH", this.ftpPathField.getText());
        EDIprops.setProp("FTP ATTEMPT", Integer.toString((Integer)this.ftpAttemptSpinner.getValue()));
        EDIprops.setProp("FTP CRYPTO", Tools.getBoolString(this.ftpEncryptBox.isSelected()));
                
        /* Save Shared Key */
        EDIprops.setProp("SHARED KEY", this.encrypt(new String(this.sharedKeyField.getPassword())));
      
        /* Save Checksum for password */
        EDIprops.setProp("CHECKSUM", this.encrypt("TEST"));
        
        try {
            EDIprops.setProp("PASSWORD", EDIPBE.encrypt("ass".toCharArray(), key));
        }catch(Exception e){
            
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem storing the key. It will be stored as plain-text in edi.ini");
            EDIprops.setProp("PASSWORD", key);
        }
        
        EDIprops.setProp("EDI CONFIG", "true");
        dao = new EDIPushDAO(application);
        
        }
 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox FTPActiveBox;
    private javax.swing.JPasswordField FTPPasswordField;
    private javax.swing.JTextField FTPPathField;
    private javax.swing.JTextField FTPPortField;
    private javax.swing.JTextField FTPServerField;
    private javax.swing.JCheckBox FTPSyncBox;
    private javax.swing.JTextField FTPUserNameField;
    private javax.swing.JCheckBox SFTPBox;
    private javax.swing.JButton addEmailButton;
    private javax.swing.JButton addFTPButton;
    private javax.swing.JButton addFolderButton;
    private javax.swing.JButton clearEmailFormButton;
    private javax.swing.JCheckBox emailActiveBox;
    private javax.swing.JCheckBox emailContactsBox;
    private javax.swing.JCheckBox emailCryptoBox;
    private javax.swing.JSpinner emailEDIAttemptSpinner;
    private javax.swing.JCheckBox emailEncryptBox;
    private javax.swing.JTextField emailField;
    private javax.swing.JCheckBox emailInventoryBox;
    private javax.swing.JCheckBox emailInvoicesBox;
    private javax.swing.JCheckBox emailPrepaidBox;
    private javax.swing.JCheckBox emailSyncBox;
    private javax.swing.JTable emailTable;
    private javax.swing.JTextField emailTextBox;
    private javax.swing.JCheckBox folderActiveBox;
    private javax.swing.JCheckBox folderContactsBox;
    private javax.swing.JCheckBox folderEncryptBox;
    private javax.swing.JCheckBox folderInventoryBox;
    private javax.swing.JCheckBox folderInvoicesBox;
    private javax.swing.JCheckBox folderPrepaidBox;
    private javax.swing.JCheckBox folderSyncBox;
    private javax.swing.JTable folderTable;
    private javax.swing.JTextField folderTextBox;
    private javax.swing.JTextField ftpAccountField;
    private javax.swing.JSpinner ftpAttemptSpinner;
    private javax.swing.JCheckBox ftpContactsBox;
    private javax.swing.JCheckBox ftpEncryptBox;
    private javax.swing.JCheckBox ftpInventoryBox;
    private javax.swing.JCheckBox ftpInvoicesBox;
    private javax.swing.JPasswordField ftpPasswordField;
    private javax.swing.JTextField ftpPathField;
    private javax.swing.JTextField ftpPortField;
    private javax.swing.JCheckBox ftpPrepaidBox;
    private javax.swing.JTextField ftpServerField;
    private javax.swing.JTable ftpTable;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField pcNameField;
    private javax.swing.JTextField pop3HostField;
    private javax.swing.JPasswordField pop3PasswordField;
    private javax.swing.JTextField pop3PortField;
    private javax.swing.JCheckBox pop3SSLBox;
    private javax.swing.JTextField pop3UserField;
    private javax.swing.JButton removeEmailButton;
    private javax.swing.JButton removeFTPButton;
    private javax.swing.JButton removeFolderButton;
    private javax.swing.JButton saveStationButton;
    private javax.swing.JPasswordField sharedKeyField;
    private javax.swing.JTextField smtpHostField;
    private javax.swing.JPasswordField smtpPasswordField;
    private javax.swing.JTextField smtpPortField;
    private javax.swing.JCheckBox smtpSSLBox;
    private javax.swing.JTextField smtpUserField;
    private javax.swing.JTextField sourceFolderField;
    private javax.swing.JButton sourcelBrowseButton;
    private javax.swing.JButton targetBrowseButton;
    private javax.swing.JCheckBox targetFolderActiveBox;
    private javax.swing.JCheckBox targetSFTPBox;
    private javax.swing.JButton testButton;
    private javax.swing.JButton testEDIButton;
    // End of variables declaration//GEN-END:variables
}
