/*
 * MiscItemDialog.java
 *
 * Created on December 10, 2006, 4:29 PM
 */
package com.datavirtue.nevitium.ui.invoices;

import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.datavirtue.nevitium.models.settings.AppSettings;
import com.datavirtue.nevitium.models.settings.LocalAppSettings;
import com.datavirtue.nevitium.services.AppSettingsService;
import com.datavirtue.nevitium.services.DiService;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.InventoryService;
import com.datavirtue.nevitium.services.LocalSettingsService;
import com.datavirtue.nevitium.services.util.CurrencyUtil;
import com.datavirtue.nevitium.services.util.DV;

import com.datavirtue.nevitium.ui.util.JTextFieldFilter;
import com.datavirtue.nevitium.ui.util.LimitedDocument;
import com.datavirtue.nevitium.ui.VATCalculator;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.Date;
import java.util.prefs.BackingStoreException;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2008, 2022 All Rights Reserved.
 */
public class MiscItemDialog extends javax.swing.JDialog {

    private AppSettingsService appSettingsService;
    private AppSettings appSettings;
    private InventoryService inventoryService;
    private LocalAppSettings localSettings;
    private InvoiceItem currentItem;
    
    private String itemLongNote = "";

    public MiscItemDialog(java.awt.Frame parent, boolean modal, InvoiceItem item) {
        super(parent, modal);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {

                try {
                    recordWindowSizeAndPosition();
                } catch (BackingStoreException ex) {
                    ExceptionService.showErrorDialog(e.getComponent(), ex, "Error saving local screen preferences");
                }
            }
        });
        initComponents();

        var injector = DiService.getInjector();
        appSettingsService = injector.getInstance(AppSettingsService.class);
        appSettingsService.setObjectType(AppSettings.class);
        inventoryService = injector.getInstance(InventoryService.class);
        
        item = item == null ? new InvoiceItem() : item;
        
        populate(item);        

    }

    private void recordWindowSizeAndPosition() throws BackingStoreException {
        var screenSettings = localSettings.getScreenSettings();
        var sizeAndPosition = LocalSettingsService.getWindowSizeAndPosition(this);
        screenSettings.setInvoiceMiscItem(sizeAndPosition);
        LocalSettingsService.saveLocalAppSettings(localSettings);
    }

    private void restoreSavedWindowSizeAndPosition() throws BackingStoreException {
        var screenSettings = localSettings.getScreenSettings().getInvoiceMiscItem();
        LocalSettingsService.applyScreenSizeAndPosition(screenSettings, this);
    }

    public void display() throws BackingStoreException {
        this.localSettings = LocalSettingsService.getLocalAppSettings();
        restoreSavedWindowSizeAndPosition();
        try {
            appSettings = appSettingsService.getObject();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error fetching app settings from database");
        }

        unitField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        JTextFieldFilter tf = (JTextFieldFilter) unitField.getDocument();
        tf.setNegativeAccepted(true);

        costField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        descField.setDocument(new LimitedDocument(50));
        longNoteBox.setDocument(new LimitedDocument(1000));

        this.populateItemList();

        tax1Box.setText(appSettings.getInvoice().getTax1Name());
        tax2Box.setText(appSettings.getInvoice().getTax2Name());

        this.setVisible(true);
    }

    private void populate(InvoiceItem item) {
        descField.setText(item.getDescription());
        unitField.setText(CurrencyUtil.money(item.getUnitPrice()));
        tax1Box.setSelected(item.isTaxable1());
        tax2Box.setSelected(item.isTaxable2());
        costField.setText(CurrencyUtil.money(item.getCost()));
        descField.requestFocus();
        currentItem = item;
    }

    private java.util.ArrayList itemList;

    private void populateItemList() {

//        itemList = new java.util.ArrayList();
//        itemList.trimToSize();
//
//        TableModel cat_tm = db.createTableModel("miscitems");
//
//        if (cat_tm != null && cat_tm.getRowCount() > 0) {
//
//            for (int r = 0; r < cat_tm.getRowCount(); r++) {
//                itemList.add((String) cat_tm.getValueAt(r, 1));
//            }
//        } else {
//
//            itemList.add("N/A");
//        }
//
//        descField.setDocument(new AutoCompleteDocument(descField, itemList));
    }

    private void normalizeItemList(String s) {

//        String txm;
//
//        java.util.ArrayList al;
//
//        al = db.search("miscitems", 1, s, false);
//
//        if (al == null) {
//
//            db.saveRecord("miscitems", new Object[]{new Integer(0), new String(s)}, false);
//
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        descField = new javax.swing.JTextField();
        tax1Box = new javax.swing.JCheckBox();
        tax2Box = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        itemNote = new javax.swing.JCheckBox();
        unitField = new javax.swing.JTextField();
        costField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        longNoteBox = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        VATButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Non-Inventory Item");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        descField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                descFieldFocusGained(evt);
            }
        });

        tax1Box.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tax1Box.setText("Tax 1");
        tax1Box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tax1Box.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        tax1Box.setIconTextGap(2);

        tax2Box.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tax2Box.setText("Tax 2");
        tax2Box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tax2Box.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        tax2Box.setIconTextGap(2);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Description");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Price");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Cost");

        itemNote.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        itemNote.setText("Designate this invoice item as a \"note.\"");
        itemNote.setToolTipText("Prevents pricing or code information from displaying on the invoice.");
        itemNote.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        itemNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNoteActionPerformed(evt);
            }
        });

        unitField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        unitField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                unitFieldFocusGained(evt);
            }
        });

        costField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        costField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                costFieldFocusGained(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Taxable");

        longNoteBox.setColumns(20);
        longNoteBox.setLineWrap(true);
        longNoteBox.setRows(5);
        longNoteBox.setWrapStyleWord(true);
        jScrollPane1.setViewportView(longNoteBox);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(descField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 337, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 328, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(6, 6, 6)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(unitField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(tax1Box)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(tax2Box))
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(costField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, itemNote))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(17, 17, 17)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jLabel2)
                    .add(jLabel5)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(descField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tax1Box)
                    .add(unitField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tax2Box)
                    .add(costField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(itemNote)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setText("Enter the true cost to keep the COGS report valid.");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        VATButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Calculator.png"))); // NOI18N
        VATButton.setText("VAT");
        VATButton.setFocusable(false);
        VATButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        VATButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        VATButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VATButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(VATButton);

        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Create.png"))); // NOI18N
        addButton.setText("Add Item");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addButton);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                        .add(41, 41, 41)
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNoteActionPerformed

        toggleItemNote();


    }//GEN-LAST:event_itemNoteActionPerformed

    private void toggleItemNote() {
        boolean sw;

        if (itemNote.isSelected()) {
            sw = false;
        } else {
            sw = true;
        }

        unitField.setText("0.00");
        unitField.setEnabled(sw);
        costField.setText("0.00");
        costField.setEnabled(sw);
        tax1Box.setSelected(false);
        tax2Box.setSelected(false);
        tax1Box.setEnabled(sw);
        tax2Box.setEnabled(sw);

    }

    private void addItemAndExit() throws SQLException {
        var description = descField.getText().trim();

        if (description.equals("") || description.length() < 5) {
            JOptionPane.showMessageDialog(this, "Please provide a thorough description.", "Important Field", JOptionPane.OK_OPTION);
            return;
        }

        var descriptionAlreadyExists = inventoryService.doesInventoryByDescriptionExist(description);

        if (descriptionAlreadyExists) {

            javax.swing.JOptionPane.showMessageDialog(null, "Duplicate description found in inventory database, action cancelled.");
            descField.requestFocus();
            descField.selectAll();
            return;
        }

        String price = unitField.getText().trim();
        String cost = costField.getText().trim();

        if (!DV.validFloatString(price)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Unit Price: '0.00' numbers and decimal only.", price, JOptionPane.OK_OPTION);
            return;
        }

        if (!DV.validFloatString(cost)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Cost Price: '0.00' numbers and decimal only.", cost, JOptionPane.OK_OPTION);
            return;
        }

        double unit = 0.00f;
        double costf = 0.00f;
        costf = Float.parseFloat(cost);
        unit = Float.parseFloat(price);

        currentItem.setCode("MISC");
        currentItem.setDate(new Date());
        currentItem.setCost(costf);
        currentItem.setUnitPrice(unit);
        currentItem.setTaxable1(tax1Box.isSelected());
        currentItem.setTaxable2(tax2Box.isSelected());
        currentItem.setTaxable1Rate(appSettings.getInvoice().getTax1Rate());
        currentItem.setTaxable2Rate(appSettings.getInvoice().getTax2Rate());
        currentItem.setDescription(description);

        itemLongNote = longNoteBox.getText();

        this.normalizeItemList(description);
        this.VATButton.setVisible(appSettings.getInvoice().getTax1Name().equalsIgnoreCase("vat"));
        this.setVisible(false);
        
    }
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            this.addItemAndExit();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error checking inventory database");
            this.setVisible(false);
        }
        
    }//GEN-LAST:event_addButtonActionPerformed

    private void unitFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_unitFieldFocusGained
        unitField.selectAll();
    }//GEN-LAST:event_unitFieldFocusGained

    private void costFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_costFieldFocusGained
        costField.selectAll();
    }//GEN-LAST:event_costFieldFocusGained

    private void descFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_descFieldFocusGained
        descField.selectAll();
    }//GEN-LAST:event_descFieldFocusGained

    private void VATButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VATButtonActionPerformed
        new VATCalculator(null, true, appSettings.getInvoice().getTax1Rate());
    }//GEN-LAST:event_VATButtonActionPerformed

    public InvoiceItem getItem() {
        return currentItem;
    }

    public String getLongNote() {
        return itemLongNote;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton VATButton;
    private javax.swing.JButton addButton;
    private javax.swing.JTextField costField;
    private javax.swing.JTextField descField;
    private javax.swing.JCheckBox itemNote;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextArea longNoteBox;
    private javax.swing.JCheckBox tax1Box;
    private javax.swing.JCheckBox tax2Box;
    private javax.swing.JTextField unitField;
    // End of variables declaration//GEN-END:variables

}
