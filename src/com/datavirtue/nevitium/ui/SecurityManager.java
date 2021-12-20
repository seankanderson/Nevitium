/*
 * SecurityManager.java
 *
 * Created on July 26, 2007, 11:49 AM
 */
package com.datavirtue.nevitium.ui;

import com.datavirtue.nevitium.models.security.UsersTableModel;
import com.google.inject.Injector;
import datavirtue.*;
import com.datavirtue.nevitium.services.DiService;
import java.awt.Image;
import java.awt.Toolkit;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.UserService;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import com.datavirtue.nevitium.models.security.User;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Data Virtue
 */
public class SecurityManager extends javax.swing.JDialog {

    private final UserService userService;
    
    public SecurityManager(java.awt.Frame parent, boolean modal, DbEngine db) {
        super(parent, modal);
        initComponents();

        Toolkit tools = Toolkit.getDefaultToolkit();
        Image winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        this.setIconImage(winIcon);

        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        
        
        Injector injector = DiService.getInjector();
        userService = injector.getInstance(UserService.class);

        this.db = db;
        init();

    }
    private DbEngine db;

    private void init() {
        refreshTable();
        setSecurityStatus();
    }

    private void setSecurityStatus() {

        try {
            var admin = userService.getRootAdminUser();
            if (admin != null && StringUtils.isEmpty(admin.getPassword())) {
                statBox.setText("Security Is DISABLED.");
                statBox.setBackground(new java.awt.Color(255, 102, 102));
            } else {
                statBox.setText("Security Is ACTIVE.");
                statBox.setBackground(new java.awt.Color(128, 255, 128));
            }
        } catch (SQLException e) {
            ExceptionService.showErrorDialog(this, e, "Error fetching the admin user");
        }
    }

    private void refreshTable() {

        try {
            userTable.setModel(new UsersTableModel(userService.getAll()));
        } catch (SQLException e) {
            ExceptionService.showErrorDialog(this, e, "Error getting users");
        }

    }

    private void resetPassword() {
        if (currentUser == null) {
            return;
        }

        String one, two;
        one = new String(passField1.getPassword()).trim();
        two = new String(passField2.getPassword()).trim();

        if (one.isEmpty() && two.isEmpty()) {
            savePassword(one);
            return;
        }

        if (one.equals(two)) {

            try {
                String cipher = PBE.encrypt(one.toCharArray(), one);
                String uncipher = PBE.decrypt(one.toCharArray(), cipher);
                if (uncipher.equals(one)) {
                    savePassword(one);
                    return;
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "The cipher did not compute!");
                    return;
                }

            } catch (Exception e) {
                ExceptionService.showErrorDialog(this, e, "Error encrypting password");
                return;
            }

        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "The two passwords did not match.  Try Again.");
        }
    }

    private void savePassword(String password) {
        currentUser.setPassword(password);
        try {
            userService.save(currentUser);
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error saving password");
            return;
        }
        javax.swing.JOptionPane.showMessageDialog(null, "Password for " + currentUser.getUserName() + " reset.");
        passField1.setText("");
        passField2.setText("");
        resetButton.setEnabled(false);
    }

    private boolean mapRolePermissions() {
        try {
            currentUser.setAdmin(currentUser.getUserName().equalsIgnoreCase("admin") ? true : masterRadio.isSelected());
            currentUser.setInventory(Integer.parseInt(inventoryField.getText()));
            currentUser.setContacts(Integer.parseInt(connField.getText()));
            currentUser.setInvoices(Integer.parseInt(invoiceField.getText()));
            currentUser.setInvoiceManager(Integer.parseInt(managerField.getText()));
            currentUser.setReports(Integer.parseInt(reportField.getText()));
            currentUser.setChecks(Integer.parseInt(checkField.getText()));
            currentUser.setExports(Integer.parseInt(exportField.getText()));
            currentUser.setSettings(Integer.parseInt(settingsField.getText()));
        } catch (NumberFormatException e) {
            ExceptionService.showErrorDialog(this, e, "Error converting permissions to numbers");
            return false;
        }
        return true;
    }

    private void newAccount() throws SQLException {

        var currentUsers = userService.getAll();

        String newUserName = javax.swing.JOptionPane.showInputDialog("Type a user name.");

        var any = currentUsers.stream().filter(p -> p.getUserName().equalsIgnoreCase(newUserName));

        if (any.count() > 0) {
            JOptionPane.showConfirmDialog(this, "User name already exists: " + newUserName);
        }

        var newUser = new User();
        newUser.setUserName(newUserName);

        var templateUser = currentUsers
                .stream()
                .filter(p -> p.getUserName().equalsIgnoreCase("admin"))
                .findAny()
                .get();

        newUser.setAdmin(false);
        newUser.setInventory(templateUser.getInventory());
        newUser.setContacts(templateUser.getContacts());
        newUser.setInvoices(templateUser.getInvoices());
        newUser.setInvoiceManager(templateUser.getInvoiceManager());
        newUser.setReports(templateUser.getReports());
        newUser.setChecks(templateUser.getChecks());
        newUser.setExports(templateUser.getExports());
        newUser.setSettings(templateUser.getSettings());
        newUser.setPassword("");

        userService.save(newUser);

        refreshTable();

    }

    private void removeAccount() {
        if (currentUser.getUserName().equalsIgnoreCase("admin")) {
            javax.swing.JOptionPane.showMessageDialog(null, "You cannot delete the root admin account.");
        } else {
            try {
                userService.delete(currentUser);
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error deleting the current user");
            }
            refreshTable();
        }
    }

    private void saveCurrentUser() {

        if (!this.mapRolePermissions()) {
            return;
        }
        try {
            userService.save(currentUser);
        } catch (SQLException e) {
            ExceptionService.showErrorDialog(this, e, "Error saving the current user");
        }
        refreshTable();
        saveButton.setEnabled(false);
        setSecurityStatus();
    }

    private User currentUser;

    private void populateUser() {

        int row = userTable.getSelectedRow();

        if (row > -1) {

            var tableModel = (UsersTableModel) userTable.getModel();
            currentUser = (User) tableModel.getValueAt(row);
            if (currentUser.isAdmin()) {
                masterRadio.setSelected(true);
            } else {
                userRadio.setSelected(true);
            }

            if (currentUser.getUserName().equalsIgnoreCase("admin")) {

                helpBox.setText(""
                        + "The admin account security settings do not apply to "
                        + "the admin account.  They are used as a default template "
                        + "for each new user account that you create.  This allows you "
                        + "to easily setup user accounts with a set of standard "
                        + "access restrictions." + nl + nl
                        + "To enable security you must set a password for the 'Master' "
                        + "user account.  To disable security, just set a blank "
                        + "password for the 'Master' user.");
            }

            inventoryField.setText(Integer.toString(currentUser.getInventory()));
            connField.setText(Integer.toString(currentUser.getContacts()));
            invoiceField.setText(Integer.toString(currentUser.getInvoices()));
            managerField.setText(Integer.toString(currentUser.getInvoiceManager()));
            reportField.setText(Integer.toString(currentUser.getReports()));
            checkField.setText(Integer.toString(currentUser.getChecks()));
            exportField.setText(Integer.toString(currentUser.getExports()));
            settingsField.setText(Integer.toString(currentUser.getSettings()));
            saveButton.setEnabled(true);
            resetButton.setEnabled(true);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        statBox = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        passField1 = new javax.swing.JPasswordField();
        passField2 = new javax.swing.JPasswordField();
        resetButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        helpBox = new javax.swing.JTextPane();
        jLabel4 = new javax.swing.JLabel();
        masterRadio = new javax.swing.JRadioButton();
        userRadio = new javax.swing.JRadioButton();
        jToolBar2 = new javax.swing.JToolBar();
        saveButton = new javax.swing.JButton();
        inventoryField = new javax.swing.JTextField();
        connField = new javax.swing.JTextField();
        invoiceField = new javax.swing.JTextField();
        managerField = new javax.swing.JTextField();
        reportField = new javax.swing.JTextField();
        checkField = new javax.swing.JTextField();
        exportField = new javax.swing.JTextField();
        settingsField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nevitium Security Manager");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("User Accounts"));

        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setSelectionBackground(new java.awt.Color(204, 255, 255));
        userTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userTableMouseClicked(evt);
            }
        });
        userTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(userTable);

        statBox.setEditable(false);
        statBox.setText("Security Status");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Security.png"))); // NOI18N
        newButton.setText("New ");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(newButton);

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Delete.png"))); // NOI18N
        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(removeButton);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Cantarell", 0, 14)); // NOI18N
        jLabel2.setText("User Password");

        resetButton.setText("Reset password");
        resetButton.setEnabled(false);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(resetButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, passField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, passField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(6, 6, 6)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(passField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(passField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resetButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .add(statBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(statBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("User Rights"));

        jLabel3.setText("Inventory");

        jLabel5.setText("My Connections");

        jLabel6.setText("Invoices");

        jLabel7.setText("Invoice Manager");

        jLabel8.setText("Reports");

        jLabel9.setText("Checks");

        jLabel10.setText("Exports");

        jLabel11.setText("Settings");

        jScrollPane2.setViewportView(helpBox);

        jLabel4.setText("User Role: ");

        buttonGroup1.add(masterRadio);
        masterRadio.setText("Master");
        masterRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        buttonGroup1.add(userRadio);
        userRadio.setSelected(true);
        userRadio.setText("Restricted");
        userRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userRadioActionPerformed(evt);
            }
        });

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        saveButton.setText("Save User Permissions");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(saveButton);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(inventoryField)
                            .add(connField)
                            .add(managerField)
                            .add(reportField)
                            .add(checkField)
                            .add(exportField)
                            .add(settingsField)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createSequentialGroup()
                                .add(invoiceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(masterRadio)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(userRadio))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(masterRadio)
                    .add(userRadio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 54, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(inventoryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(connField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(invoiceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(managerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(reportField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(exportField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(settingsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed

        removeAccount();

    }//GEN-LAST:event_removeButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed

        resetPassword();
        setSecurityStatus();

    }//GEN-LAST:event_resetButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        try {
            newAccount();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error creating new user");
            return;
        }
        setSecurityStatus();
    }//GEN-LAST:event_newButtonActionPerformed

    private void userTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMouseClicked
        int mouseButton = evt.getButton();
        if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) {
            return;
        }
        populateUser();
    }//GEN-LAST:event_userTableMouseClicked


    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        saveCurrentUser();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void userTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userTableKeyReleased
        populateUser();
    }//GEN-LAST:event_userTableKeyReleased

    private void userRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userRadioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userRadioActionPerformed

    private String nl = System.getProperty("line.separator");
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField checkField;
    private javax.swing.JTextField connField;
    private javax.swing.JTextField exportField;
    private javax.swing.JTextPane helpBox;
    private javax.swing.JTextField inventoryField;
    private javax.swing.JTextField invoiceField;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTextField managerField;
    private javax.swing.JRadioButton masterRadio;
    private javax.swing.JButton newButton;
    private javax.swing.JPasswordField passField1;
    private javax.swing.JPasswordField passField2;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField reportField;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField settingsField;
    private javax.swing.JTextField statBox;
    private javax.swing.JRadioButton userRadio;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables

}
