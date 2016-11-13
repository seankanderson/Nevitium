/*
 * SecurityManager.java
 *
 * Created on July 26, 2007, 11:49 AM
 */

package businessmanager;
import datavirtue.*;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.table.*;

/**
 *
 * @author  Data Virtue
 */
public class SecurityManager extends javax.swing.JDialog {
    
    /** Creates new form SecurityManager */
    public SecurityManager(java.awt.Frame parent, boolean modal, DbEngine db) {
        super(parent, modal);
        initComponents();

        Toolkit tools = Toolkit.getDefaultToolkit();
        Image winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        this.setIconImage(winIcon);

        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);


        this.db = db;
        init();
        
    }
    private DbEngine db;
    private Object [] user;
    
    private void init() {
        
        refreshTable();
        inventoryField.setValue(new Long(100));
        connField.setValue(new Long(100));
        invoiceField.setValue(new Long(100));
        managerField.setValue(new Long(100));
        reportField.setValue(new Long(100));
        checkField.setValue(new Long(100));
        exportField.setValue(new Long(100));
        configField.setValue(new Long(100));
        setStatus();
        
    }

    private void setStatus(){
        java.util.ArrayList al = db.search("users", 1, "Master", false);
        if (al != null){
            user = db.getRecord("users", (Integer)al.get(0));
            String pw = (String)user[2];
            if (pw.equals("")) {

                /*try {
                    

                    final java.awt.image.BufferedImage image;

                    //image = tools.getImage();
                    image = javax.imageio.ImageIO.read(new java.io.File("images/redshade.jpg"));
                    statBox = new JTextField() {

                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            int y = (getHeight() - image.getHeight()) / 2;
                            g.drawImage(image, 0, 0, this);
                        }
                    };


                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                statBox.setText("Security Is DISABLED.");
                statBox.setBackground(new java.awt.Color(255,102,102));

            }else {
                statBox.setText("Security System Is Active.");
                statBox.setBackground(new java.awt.Color(128,255,128));
            }
        }
    }

    private void refreshTable() {
        
        userTable.setModel(db.createTableModel("users", userTable));
        
        setView(new int [] {0,1,2,2,2,2,2,2,2,2});
        
    }
    
    public void setView (int [] cols){
        
        if (userTable.getModel().getRowCount() > 0){
        TableColumnModel cm = userTable.getColumnModel();
        TableColumn tc;
        
        
        for (int i =0; i < cols.length; i++){
            
            tc = cm.getColumn(cols[i]);
            userTable.removeColumn(tc);
            
            
        }
        
        int a = userTable.getColumnCount();
       
        
        }
        
        
    }
        
    private void resetPassword() {
        int r = userTable.getSelectedRow();
        
        if (r < 0) return;
        
        String one, two;
        one = new String (passField1.getPassword());
        two = new String (passField2.getPassword());
        
        
        if (one.equals("") && two.equals("")){
            
            
            int key = (Integer)userTable.getModel().getValueAt(r, 0);
                    String acct = (String)userTable.getModel().getValueAt(r, 1);

                    /* Prevent master from being assigned as a regular user */
                    String u = (String)userTable.getModel().getValueAt(r, 1);
                    if (u.equalsIgnoreCase("Master")) masterRadio.setSelected(true);

                    /* Record new password */
                    user = new Object [12];
                    user[0] = new Integer(key);
                    user[1] = new String(acct);
                    user[2] = new String("");//key
                    user[3] = new Boolean(masterRadio.isSelected());
                    user[4] = new Long((Long)inventoryField.getValue());
                    user[5] = new Long((Long)connField.getValue());
                    user[6] = new Long((Long)invoiceField.getValue());
                    user[7] = new Long((Long)managerField.getValue());
                    user[8] = new Long((Long)reportField.getValue());
                    user[9] = new Long((Long)checkField.getValue());
                    user[10] = new Long((Long)exportField.getValue());
                    user[11] = new Long((Long)configField.getValue());

                    db.saveRecord("users", user, false);
                    passField1.setText("");
                    passField2.setText("");
                    
                    javax.swing.JOptionPane.showMessageDialog(null, "Password for "+ acct+" reset.");
                    resetButton.setEnabled(false);
                    return;
        }
        
        if (one.equals(two)){
            try {
                                
                    
                String cipher = PBE.encrypt(passField1.getPassword(), one);
                
                //System.out.println(cipher);
                
                String uncipher = PBE.decrypt(passField1.getPassword(), cipher);
                
                //System.out.println(uncipher);
                
                
                if (uncipher.equals(one)){
                    
                    
                    int key = (Integer)userTable.getModel().getValueAt(r, 0);
                    String acct = (String)userTable.getModel().getValueAt(r, 1);

                    /* Prevent master from being assigned as a regular user */
                    String u = (String)userTable.getModel().getValueAt(r, 1);
                    if (u.equalsIgnoreCase("Master")) masterRadio.setSelected(true);

                    /* Record new password */
                    user = new Object [12];
                    user[0] = new Integer(key);
                    user[1] = new String(acct);
                    user[2] = new String(cipher);//key
                    user[3] = new Boolean(masterRadio.isSelected());
                    user[4] = new Long((Long)inventoryField.getValue());
                    user[5] = new Long((Long)connField.getValue());
                    user[6] = new Long((Long)invoiceField.getValue());
                    user[7] = new Long((Long)managerField.getValue());
                    user[8] = new Long((Long)reportField.getValue());
                    user[9] = new Long((Long)checkField.getValue());
                    user[10] = new Long((Long)exportField.getValue());
                    user[11] = new Long((Long)configField.getValue());


                    db.saveRecord("users", user, false);
                    passField1.setText("");
                    passField2.setText("");
                    
                    javax.swing.JOptionPane.showMessageDialog(null, "Password for "+ acct+" reset.");
                    resetButton.setEnabled(false);
                    return;
                    
                }else {
                    
                    javax.swing.JOptionPane.showMessageDialog(null, "The cipher did not compute!");
                    
                }
                
                
                
            }catch(Exception e) {
                
                e.printStackTrace();
                
            }
           
            
        }else {
            
            javax.swing.JOptionPane.showMessageDialog(null, "The two passwords did not match.  Try Again.");
            
        }
    }
    
    private void viewLog() {
        
        
    }
    
    private void updateRole() {
        
        int r = userTable.getSelectedRow();
        if (r > -1){//prevent master
            
        /* Prevent master from being assigned as a regular user */
        String u = (String)userTable.getModel().getValueAt(r, 1);
        if (u.equalsIgnoreCase("Master")) masterRadio.setSelected(true);
        
        user[3] = new Boolean(masterRadio.isSelected());
        user[4] = new Long((Long)inventoryField.getValue());
        user[5] = new Long((Long)connField.getValue());
        user[6] = new Long((Long)invoiceField.getValue());
        user[7] = new Long((Long)managerField.getValue());
        user[8] = new Long((Long)reportField.getValue());
        user[9] = new Long((Long)checkField.getValue());
        user[10] = new Long((Long)exportField.getValue());
        user[11] = new Long((Long)configField.getValue());

        db.saveRecord("users", user, false);
        }      
        refreshTable();
        
    }

    
    private void newAccount() {
        
        String iValue = javax.swing.JOptionPane.showInputDialog("Type a user name."); 
        user = new Object [12];

        /* Use the saved access restrictions from the master user */
        java.util.ArrayList al = db.search("users", 1, "Master", false);
        if (al != null){
            user = db.getRecord("users", (Integer)al.get(0));
        }

        if (iValue != null){


            //check for user
            al = db.search("users", 1, iValue, false);
            if (al != null){
                javax.swing.JOptionPane.showMessageDialog(null,
                        "This user ("+iValue+") already exsists.");
                return;
            }
            
            user[0] = new Integer(0);
            user[1] = new String(iValue.trim());
            user[2] = new String("");//key
            user[3] = new Boolean(false);
            /*user[4] = new Long((Long)inventoryField.getValue());
            user[5] = new Long((Long)connField.getValue());
            user[6] = new Long((Long)invoiceField.getValue());
            user[7] = new Long((Long)managerField.getValue());
            user[8] = new Long((Long)reportField.getValue());
            user[9] = new Long((Long)checkField.getValue());
            user[10] = new Long((Long)exportField.getValue());
            user[11] = new Long((Long)configField.getValue());*/
            db.saveRecord("users", user, false);
              
            refreshTable();
            
        }
        
    }
    
    
    private void removeAccount() {
        
        int r = userTable.getSelectedRow();

        /* Prevent master from being assigned as a regular user */
        String u = (String)userTable.getModel().getValueAt(r, 1);
        if (u.equalsIgnoreCase("master")){

            javax.swing.JOptionPane.showMessageDialog(null, "You cannot delete the master account.");

        }else {

            int key = (Integer)userTable.getModel().getValueAt(r, 0);
            db.removeRecord("users", key);
            refreshTable();

        }
        
    }

    private void populateUser(){

        int r = userTable.getSelectedRow();

        if (r > -1) {


           boolean master = (Boolean)userTable.getModel().getValueAt(r, 3);
           if (master) masterRadio.setSelected(true);
           else userRadio.setSelected(true);
           String cipher = (String)userTable.getModel().getValueAt(r, 2);
           String acct = (String)userTable.getModel().getValueAt(r, 1);

           if (acct.equalsIgnoreCase("Master")){

               helpBox.setText(""+
                       "The Master account security settings do not apply to "+
                       "the Master account.  They are used as a template for "+
                       "each new user account that you create.  This allows you "+
                       "to easily setup user accounts with a set of standard "+
                       "access restrictions."+nl+nl+
                       "To enable security you must set a password for the 'Master' "+
                       "user account.  To disable security, just set a blank "+
                       "password for the 'Master' user.");
           }

           int key = (Integer)userTable.getModel().getValueAt(r,0);

           user = new Object [12];
           user[0] = new Integer(key);
           user[1] = new String(acct);
           user[2] = new String(cipher);//key
           user[3] = new Boolean(master);
           user[4] = new Long((Long)userTable.getModel().getValueAt(r,4));
           user[5] = new Long((Long)userTable.getModel().getValueAt(r,5));
           user[6] = new Long((Long)userTable.getModel().getValueAt(r,6));
           user[7] = new Long((Long)userTable.getModel().getValueAt(r,7));
           user[8] = new Long((Long)userTable.getModel().getValueAt(r,8));
           user[9] = new Long((Long)userTable.getModel().getValueAt(r,9));
           user[10] = new Long((Long)userTable.getModel().getValueAt(r,10));
           user[11] = new Long((Long)userTable.getModel().getValueAt(r,11));

           inventoryField.setValue((Long)user[4]);
           connField.setValue((Long)user[5]);
           invoiceField.setValue((Long)user[6]);
           managerField.setValue((Long)user[7]);
           reportField.setValue((Long)user[8]);
           checkField.setValue((Long)user[9]);
           exportField.setValue((Long)user[10]);
           configField.setValue((Long)user[11]);

           saveButton.setEnabled(true);
            resetButton.setEnabled(true);

        }



    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        inventoryField = new javax.swing.JFormattedTextField();
        connField = new javax.swing.JFormattedTextField();
        invoiceField = new javax.swing.JFormattedTextField();
        managerField = new javax.swing.JFormattedTextField();
        reportField = new javax.swing.JFormattedTextField();
        checkField = new javax.swing.JFormattedTextField();
        exportField = new javax.swing.JFormattedTextField();
        configField = new javax.swing.JFormattedTextField();
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
        userTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userTableKeyReleased(evt);
            }
        });
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userTableMouseClicked(evt);
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

        jLabel2.setFont(new java.awt.Font("Cantarell", 0, 14));
        jLabel2.setText("User Password (7 Char. Limit)");

        resetButton.setText("(Re)Set Password");
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
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(statBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("User Rights"));

        inventoryField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        inventoryField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                inventoryFieldFocusGained(evt);
            }
        });

        connField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        connField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                connFieldFocusGained(evt);
            }
        });

        invoiceField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        invoiceField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                invoiceFieldFocusGained(evt);
            }
        });

        managerField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        managerField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                managerFieldFocusGained(evt);
            }
        });

        reportField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        reportField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                reportFieldFocusGained(evt);
            }
        });

        checkField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        checkField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                checkFieldFocusGained(evt);
            }
        });

        exportField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        exportField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                exportFieldFocusGained(evt);
            }
        });

        configField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        configField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                configFieldFocusGained(evt);
            }
        });

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
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, configField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, exportField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, checkField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, reportField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, managerField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, invoiceField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, connField, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, inventoryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 8, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(inventoryField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(connField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(invoiceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(managerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(reportField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(checkField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(exportField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(configField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
        setStatus();
        
    }//GEN-LAST:event_resetButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        
        newAccount();
        setStatus();
               
        
    }//GEN-LAST:event_newButtonActionPerformed

    private void userTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMouseClicked
        int mouseButton = evt.getButton();
        if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) return;
        populateUser();
    }//GEN-LAST:event_userTableMouseClicked



    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        updateRole();
        saveButton.setEnabled(false);
        setStatus();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void inventoryFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_inventoryFieldFocusGained

        String help = ""+
                "100: No Create, Edit, Delete or Cost" + nl+
                "200: No Edit, No Delete, No Cost" + nl +
                "300: No Edit, No Delete"+ nl +
                "400: No Delete" + nl +
                "500: Full Control";
        helpBox.setText(help);

    }//GEN-LAST:event_inventoryFieldFocusGained

    private void connFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_connFieldFocusGained
        String help = ""+
                "100: No Create, Edit, Delete" + nl+
                "200: No Delete, No Labels" + nl +
                "300: No Delete"+ nl +

                "500: Full Control";
        helpBox.setText(help);
    }//GEN-LAST:event_connFieldFocusGained

    private void invoiceFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_invoiceFieldFocusGained
        String help = ""+
                "100: No Invoice or Quote" + nl+
                
                "300: Quote Only"+ nl +
                
                "500: Full Control";
        helpBox.setText(help);
    }//GEN-LAST:event_invoiceFieldFocusGained

    private void managerFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_managerFieldFocusGained
        String help = ""+
                "100: No Invoice Manager" + nl+
                
                "300: View & Payments Only"+ nl +
                
                "500: Full Control";
        helpBox.setText(help);
    }//GEN-LAST:event_managerFieldFocusGained

    private void reportFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_reportFieldFocusGained
        String help = ""+
                "100: No Reports" + nl+

                "300: Open Invoice and Inventory Only "+ nl +
                
                "500: Full Control";
        helpBox.setText(help);
    }//GEN-LAST:event_reportFieldFocusGained

    private void checkFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_checkFieldFocusGained
        String help = ""+
                "100: No Check Printing" + nl+

                "500: Full Control";
        helpBox.setText(help);
    }//GEN-LAST:event_checkFieldFocusGained

    private void exportFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_exportFieldFocusGained
        String help = ""+
                "100: No Exports" + nl+



                "500: Full Control";
        helpBox.setText(help);
    }//GEN-LAST:event_exportFieldFocusGained

    private void configFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_configFieldFocusGained
        String help = ""+
                "100: No Settings Access" + nl+

                "500: Full Control" + nl +
                "Only a Master can change security.";
        helpBox.setText(help);
    }//GEN-LAST:event_configFieldFocusGained

    private void userTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userTableKeyReleased
        populateUser();
    }//GEN-LAST:event_userTableKeyReleased

    private void userRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userRadioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userRadioActionPerformed
    
   
    private String nl = System.getProperty("line.separator");
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JFormattedTextField checkField;
    private javax.swing.JFormattedTextField configField;
    private javax.swing.JFormattedTextField connField;
    private javax.swing.JFormattedTextField exportField;
    private javax.swing.JTextPane helpBox;
    private javax.swing.JFormattedTextField inventoryField;
    private javax.swing.JFormattedTextField invoiceField;
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
    private javax.swing.JFormattedTextField managerField;
    private javax.swing.JRadioButton masterRadio;
    private javax.swing.JButton newButton;
    private javax.swing.JPasswordField passField1;
    private javax.swing.JPasswordField passField2;
    private javax.swing.JButton removeButton;
    private javax.swing.JFormattedTextField reportField;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField statBox;
    private javax.swing.JRadioButton userRadio;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
    
}
