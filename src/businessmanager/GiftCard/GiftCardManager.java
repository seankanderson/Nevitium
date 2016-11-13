/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GiftCardManager.java
 *
 * Created on Oct 13, 2009, 9:28:08 PM
 */

package businessmanager.GiftCard;


import RuntimeManagement.GlobalApplicationDaemon;
import RuntimeManagement.KeyCard;
import businessmanager.Common.JTextFieldFilter;
import businessmanager.Common.LimitedDocument;
import businessmanager.InvoiceQuote.InvoiceDialog;

import datavirtue.DV;
import datavirtue.DbEngine;
import java.awt.Point;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Data Virtue
 */
public class GiftCardManager extends javax.swing.JDialog {

    /** Creates new form GiftCardManager */
    GlobalApplicationDaemon application;
    public GiftCardManager(java.awt.Frame parent, boolean modal, 
            GlobalApplicationDaemon application) {
        super(parent, modal);
        initComponents();
        accessKey = application.getKey_card();
        workingPath = application.getWorkingPath();
                
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        db = application.getDb();
        refreshCardTable();

        useageTable.setModel(new DefaultTableModel());

        balanceField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        cardField.setDocument(new LimitedDocument(16));
        nameField.setDocument(new LimitedDocument(25));
        //notePane.setDocument(new LimitedDocument(100));
        selectButton.setVisible(false);
        cancelButton.setVisible(false);
        clearFields();

        this.setVisible(true);

    }

    private void refreshCardTable(){

      DefaultTableModel tm = (DefaultTableModel)db.createTableModel("card");
      cardTable.setModel(tm);
      setCardView();


    }
    private void setCardView(){

         if (cardTable.getColumnCount() <= 0) return;

      int [] cols = {0,3,3,3};
            TableColumnModel cm = cardTable.getColumnModel();
            TableColumn tc;

            for (int i =0; i < cols.length; i++){

                tc = cm.getColumn(cols[i]);
                cardTable.removeColumn(tc);

            }

    }
    private void populateFields(){
        int row = cardTable.getSelectedRow();
        if (row < 0) return;

        TableModel tm = cardTable.getModel();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        dataOut[0] = new Integer((Integer)tm.getValueAt(row, 0));

        String cardNo = (String)tm.getValueAt(row, 1);
        cardField.setText(cardNo);

        nameField.setText((String)tm.getValueAt(row, 2));
        balanceField.setText(DV.money((Float)tm.getValueAt(row, 3)));

        lastUseField.setText(df.format((Long)tm.getValueAt(row, 4)));
        dataOut[4] = new Long((Long)tm.getValueAt(row, 4));

        inceptionField.setText(df.format((Long)tm.getValueAt(row, 5)));
        dataOut[5] = new Long((Long)tm.getValueAt(row, 5));

        notePane.setText((String)tm.getValueAt(row, 6));

        setFieldsEnabled(true);
        statusBar.setText("Remember to Click 'Save' After Making Changes.");
        
        /* Populate useage Table */
        ArrayList al = db.search("payments", 4, cardNo, false);
        TableModel um ;

        if (al != null) {

            um = (TableModel)db.createTableModel("payments", al, true);
            useageTable.setModel(um);

            /* Clean up table */
            if (useageTable.getColumnCount() == 0){
                return;
            }

            int [] cols = {0,2,2,2};
            TableColumnModel cm = useageTable.getColumnModel();
            TableColumn tc;

            for (int i =0; i < cols.length; i++){

                tc = cm.getColumn(cols[i]);
                useageTable.removeColumn(tc);

            }

        }else {

            useageTable.setModel(new DefaultTableModel());
        }


        
    }

    private void clearFields(){
        dataOut[0] = new Integer(0);
        
        dataOut[4] = new Long(new Date().getTime());
        dataOut[5] = new Long(new Date().getTime());

        cardField.setText("");
        nameField.setText("");
        balanceField.setText("0.00");
        lastUseField.setText(df.format(new Date()));
        inceptionField.setText(df.format(new Date()));
        notePane.setText("");

        setFieldsEnabled(false);
         useageTable.setModel(new DefaultTableModel());
         statusBar.setText("Click the 'Acct Number' Field to Start a New Record.");
         searchField.requestFocus();

    }

    private void setFieldsEnabled(boolean enabled){

        cardField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        balanceField.setEnabled(enabled);
        notePane.setEnabled(enabled);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        cardTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        useageTable = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        nameRadio = new javax.swing.JRadioButton();
        acctRadio = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cardField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        balanceField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lastUseField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        inceptionField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notePane = new javax.swing.JTextPane();
        jToolBar1 = new javax.swing.JToolBar();
        jToolBar3 = new javax.swing.JToolBar();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        statusBar = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        selectButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pre-Paid Account Manager");

        cardTable.setFont(new java.awt.Font("Tahoma", 0, 14));
        cardTable.setModel(new javax.swing.table.DefaultTableModel(
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
        cardTable.setSelectionBackground(new java.awt.Color(204, 255, 255));
        cardTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        cardTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cardTableKeyPressed(evt);
            }
        });
        cardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(cardTable);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setText("Useage");

        useageTable.setFont(new java.awt.Font("Tahoma", 0, 14));
        useageTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        useageTable.setSelectionBackground(new java.awt.Color(204, 255, 204));
        useageTable.setSelectionForeground(new java.awt.Color(0, 0, 0));
        useageTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                useageTableKeyPressed(evt);
            }
        });
        useageTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                useageTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(useageTable);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Zoom.png"))); // NOI18N

        searchField.setFont(new java.awt.Font("Tahoma", 0, 18));
        searchField.setToolTipText("Type a First or Last Name or Card# and Hit ENTER");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchFieldFocusGained(evt);
            }
        });
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchFieldKeyPressed(evt);
            }
        });

        buttonGroup1.add(nameRadio);
        nameRadio.setText("Name");

        buttonGroup1.add(acctRadio);
        acctRadio.setSelected(true);
        acctRadio.setText("Acct#");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Acct Number");

        cardField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cardField.setToolTipText("Gift Card Number");
        cardField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardFieldMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Name");

        nameField.setFont(new java.awt.Font("Tahoma", 0, 18));
        nameField.setToolTipText("Name, if any, that the account is assigned to.  Useage is not constrained by this field.");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Balance");

        balanceField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        balanceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        balanceField.setToolTipText("Current Balance Left on the Account / Card");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Last Use");

        lastUseField.setEditable(false);
        lastUseField.setFont(new java.awt.Font("Tahoma", 0, 18));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Inception");

        inceptionField.setEditable(false);
        inceptionField.setFont(new java.awt.Font("Tahoma", 0, 18));
        inceptionField.setToolTipText("Date the Account was Created");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Note");

        notePane.setFont(new java.awt.Font("Tahoma", 0, 14));
        notePane.setToolTipText("Account notes, Limit 100 Characters");
        jScrollPane2.setViewportView(notePane);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Corrupt text.png"))); // NOI18N
        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jToolBar3.add(clearButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar3.add(saveButton);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, inceptionField)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, balanceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lastUseField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cardField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .add(jToolBar3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(cardField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(4, 4, 4)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(balanceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(lastUseField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(inceptionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        statusBar.setEditable(false);
        statusBar.setText("Click the 'Acct Number' Field to Start a New Record.");
        statusBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusBarActionPerformed(evt);
            }
        });

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/OK.png"))); // NOI18N
        selectButton.setText("Select");
        selectButton.setFocusable(false);
        selectButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        selectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(selectButton);

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/No.png"))); // NOI18N
        cancelButton.setText("None");
        cancelButton.setFocusable(false);
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cancelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(cancelButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(deleteButton);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nameRadio)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(acctRadio)
                        .add(18, 18, 18)
                        .add(jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(nameRadio)
                        .add(acctRadio)
                        .add(searchField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cardFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cardFieldMouseClicked
        if (!cardField.isEnabled()){
            dataOut [0] = new Integer (0);
            clearFields();
            setFieldsEnabled(true);
            statusBar.setText("Remember to Click 'Save' After Making Changes.");
            cardField.requestFocus();
        }
    }//GEN-LAST:event_cardFieldMouseClicked

    private void useageTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_useageTableKeyPressed
        
    }//GEN-LAST:event_useageTableKeyPressed

    private void useageTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_useageTableMouseClicked
        int mouseButton = evt.getButton();
        if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) return;
        String inumber = "";

        if (evt.getClickCount() == 2){
            Point p = evt.getPoint();

            int row = useageTable.rowAtPoint(new Point(evt.getX(), evt.getY()));

             if (useageTable.getSelectedRow() > -1) {

                inumber = (String) useageTable.getModel().getValueAt(row, 1);

                ArrayList al = db.search("invoice", 1, inumber, false);
                if (al != null) {
                    int k = (Integer)al.get(0);
                    InvoiceDialog id = new InvoiceDialog (null, true, application, k);
                    id.setVisible(true);
                }

             }

           }
    }//GEN-LAST:event_useageTableMouseClicked

    private void cardTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cardTableKeyPressed
        populateFields();
    }//GEN-LAST:event_cardTableKeyPressed

    private void cardTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cardTableMouseClicked
        populateFields();
    }//GEN-LAST:event_cardTableMouseClicked

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        boolean valid = validateForm();
        if (!valid) return;

        /* Check for duplicate acct number */
        int x = (Integer)dataOut[0];

        if (x==0){
            ArrayList al = db.search("card", 1, cardField.getText(), false);
            if (al != null && al.size() > 0){
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Duplicate Account Number!");
                return;
            }
        }


        dataOut[1] = new String(cardField.getText().trim());
        dataOut[2] = new String(nameField.getText());
        dataOut[3] = new Float(Float.parseFloat(balanceField.getText()));
        
        dataOut[6] = new String(notePane.getText());
        db.saveRecord("card", dataOut, false);
        clearFields();
        refreshCardTable();


    }//GEN-LAST:event_saveButtonActionPerformed

    private boolean validateForm(){

        String acct = cardField.getText();
        float balance = Float.parseFloat(balanceField.getText());

        if (acct.trim().equals("")){
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Account number is required to save.");
            return false;
        }
        return true;

    }

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearFields();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void searchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
            search();
    }//GEN-LAST:event_searchFieldKeyPressed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        deleteAcct();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void statusBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusBarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusBarActionPerformed

    private void searchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusGained
        searchField.selectAll();
    }//GEN-LAST:event_searchFieldFocusGained

    private void deleteAcct(){
        int row = cardTable.getSelectedRow();
        if (row < 0) return;

        int acctKey = (Integer)cardTable.getModel().getValueAt(row, 0);

        String iValue = javax.swing.JOptionPane.showInputDialog(
                "Type 'Delete' to Remove This Account.");

        if (iValue != null && iValue.equalsIgnoreCase("delete")){
            db.removeRecord("card", acctKey);
            refreshCardTable();
            clearFields();
        }

    }
    private void search(){

        String text = searchField.getText().trim();
        TableModel tm;

        /* build search from button status  */

        if (acctRadio.isSelected()) { //search for acct

             java.util.ArrayList al = db.search("card",1, text, false);

             if (al == null) {
                refreshCardTable();
                clearFields();

             }else {
                tm = db.createTableModel("card", al, true);

                cardTable.setModel(tm);

                setCardView();

                //match = true;

                searchField.selectAll();
             }
        }

        if (nameRadio.isSelected()) { //search for name

             java.util.ArrayList al = db.search("card",2, text, false);

             if (al == null) {
                refreshCardTable();
                clearFields();

             }else {
                tm = db.createTableModel("card", al, true);

                cardTable.setModel(tm);

                setCardView();

                //match = true;

                searchField.selectAll();
             }
        }


}
private DbEngine db;
private Object [] dataOut = new Object [7];
private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
private KeyCard accessKey;
private String workingPath;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton acctRadio;
    private javax.swing.JTextField balanceField;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField cardField;
    private javax.swing.JTable cardTable;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField inceptionField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JTextField lastUseField;
    private javax.swing.JTextField nameField;
    private javax.swing.JRadioButton nameRadio;
    private javax.swing.JTextPane notePane;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton selectButton;
    private javax.swing.JTextField statusBar;
    private javax.swing.JTable useageTable;
    // End of variables declaration//GEN-END:variables

}
