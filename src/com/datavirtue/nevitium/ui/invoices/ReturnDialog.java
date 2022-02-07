/*
 * ReturnDialog.java
 *
 * Created on January 4, 2007, 8:38 PM
 */

package com.datavirtue.nevitium.ui.invoices;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.datavirtue.nevitium.models.invoices.InvoiceItemsTableModel;
import com.datavirtue.nevitium.models.invoices.InvoiceReturnsTableModel;
import com.datavirtue.nevitium.services.DiService;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.InvoiceService;
import com.datavirtue.nevitium.services.exceptions.InvoiceItemAlreadyReturnedException;
import com.datavirtue.nevitium.services.exceptions.InvoiceVoidedException;
import com.datavirtue.nevitium.services.exceptions.PartialQuantityException;
import com.datavirtue.nevitium.services.util.DV;
import com.datavirtue.nevitium.ui.util.JTextFieldFilter;
import com.google.inject.Injector;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.table.*;
import java.sql.SQLException;


/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2022 All Rights Reserved.
 */

public class ReturnDialog extends javax.swing.JDialog {

    private Image winIcon;
    private InvoiceService invoiceService;
    private Invoice currentInvoice;
    private InvoiceItem currentItem;
    
    public ReturnDialog(java.awt.Frame parent, boolean modal, Invoice invoice) {
        super(parent, modal);

        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        initComponents();
        
        Injector injector = DiService.getInjector();
        invoiceService = injector.getInstance(InvoiceService.class);
        //appSettingsService.setObjectType(AppSettings.class);
        
        qtyField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        priceField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
 
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);
        
        this.currentInvoice = invoice;        
         
        var tableModel = new InvoiceItemsTableModel(new ArrayList(invoice.getItems()));
        invoiceReturnsTable.setModel(tableModel);
                
        setView();
       
        this.setVisible(true);        
    }
    
    private void setView () {
       
        TableColumnModel cm = invoiceReturnsTable.getColumnModel();
        TableColumn tc;
        
        int [] cols = new int [] {0,0,0,4,4,4};
        
        for (int i = 0; i < cols.length; i++){
        
            tc = cm.getColumn(cols[i]);
        
            invoiceReturnsTable.removeColumn(tc);//remove key column                        
        
        }        
        invoiceReturnsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
            tc = invoiceReturnsTable.getColumnModel().getColumn(0);
            tc.setPreferredWidth(40);
            tc = invoiceReturnsTable.getColumnModel().getColumn(2);
            tc.setPreferredWidth(350);
        
    }
    
    private void populateForm () {
       
        int selectedRow = invoiceReturnsTable.getSelectedRow();
        
        var tableModel = (InvoiceReturnsTableModel)this.invoiceReturnsTable.getModel();
        
        var itemReturn = tableModel.getValueAt(selectedRow);
        
        qtyField.setText(Double.toString(itemReturn.getQuantity()));
        codeField.setText(itemReturn.getCode());
        descField.setText(itemReturn.getDescription());
        priceField.setText(DV.money(itemReturn.getUnitPrice()));
       
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        invoiceReturnsTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        codeField = new javax.swing.JTextField();
        descField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        datePicker1 = new com.michaelbaranov.microba.calendar.DatePicker();
        priceField = new javax.swing.JTextField();
        qtyField = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        returnButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Product Return");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        invoiceReturnsTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        invoiceReturnsTable.setModel(new javax.swing.table.DefaultTableModel(
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
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        invoiceReturnsTable.setGridColor(new java.awt.Color(0, 0, 0));
        invoiceReturnsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invoiceReturnsTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(invoiceReturnsTable);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        codeField.setEditable(false);
        codeField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        descField.setEditable(false);
        descField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setText("Ret Qty");

        jLabel2.setText("Code");

        jLabel3.setText("Description");

        jLabel4.setText("Credit Amt");

        datePicker1.setToolTipText("Return Date");
        datePicker1.setFieldEditable(false);
        datePicker1.setShowNoneButton(false);
        datePicker1.setStripTime(true);

        priceField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        priceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        qtyField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        qtyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        returnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/OK.png"))); // NOI18N
        returnButton.setText("Process");
        returnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(returnButton);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(qtyField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(codeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(descField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                            .add(jLabel3))
                        .add(6, 6, 6)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(priceField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel4)))
                    .add(datePicker1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(datePicker1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 16, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jLabel2)
                    .add(jLabel4)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, descField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, codeField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, priceField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, qtyField))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /* The never ending nightmare method from hell
     that most likely contains more than one error. */
    /** 
     Return product to inventory
     *  
     Refund money to customer
     */

    private void processReturn() 
            throws SQLException, 
            PartialQuantityException, 
            InvoiceItemAlreadyReturnedException, 
            InvoiceVoidedException
    {
        if (invoiceReturnsTable.getSelectedRow() > -1){

        }else {
            return;
        }

        if (!DV.validFloatString(priceField.getText())){
            javax.swing.JOptionPane.showMessageDialog(null,
                    "The credit amount must be a valid decimal number.");
            priceField.selectAll();
            priceField.requestFocus();
            return;
        }
        if (!DV.validFloatString(qtyField.getText())){
            javax.swing.JOptionPane.showMessageDialog(null,
                    "The quantity must be a valid decimal number.");
            qtyField.selectAll();
            qtyField.requestFocus();
            return;
        }
                              
        float refundAmount = Float.parseFloat(priceField.getText());
        float userSuppliedReturnQty = Float.parseFloat(qtyField.getText());
        var returnDate = datePicker1.getDate();
        
        var suggestedRefundPayment = this.invoiceService.returnInvoiceItem(currentItem, userSuppliedReturnQty, refundAmount, returnDate);
                //invoice.saveInvoice();
        
        if (suggestedRefundPayment != null) {
            // fire off payment dialog with this payment populated
        }
    }
    
    private void returnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnButtonActionPerformed
        
        try {
            //if (!DV.isValidShortDate(dateField.getText(), true)) return; /* DATE CHECK */
            processReturn();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error accessing invoice database while processing return");
        } catch (PartialQuantityException ex) {
            ExceptionService.showErrorDialog(this, ex, "Tried to return partial quantity");
        } catch (InvoiceItemAlreadyReturnedException ex) {
            ExceptionService.showErrorDialog(this, ex, "Item was already returned");
        } catch (InvoiceVoidedException ex) {
            ExceptionService.showErrorDialog(this, ex, "Invalid operation on voided invoice");
        }
        
    }//GEN-LAST:event_returnButtonActionPerformed

    private void invoiceReturnsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invoiceReturnsTableMouseClicked
    int mouseButton = evt.getButton();
    if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) return;    
        if (invoiceReturnsTable.getSelectedRow() > -1){
            
            String code = (String)invoiceReturnsTable.getModel().getValueAt(invoiceReturnsTable.getSelectedRow(),4);
            if (code.equalsIgnoreCase("RETURN")){
                
                qtyField.setText("0.0");
                codeField.setText("");
                descField.setText("");
                priceField.setText("");
                returnButton.setEnabled(false);
                return;
                
            }
            returnButton.setEnabled(true);
            populateForm();
            
        }
        
        
    }//GEN-LAST:event_invoiceReturnsTableMouseClicked
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField codeField;
    private com.michaelbaranov.microba.calendar.DatePicker datePicker1;
    private javax.swing.JTextField descField;
    private javax.swing.JTable invoiceReturnsTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField priceField;
    private javax.swing.JTextField qtyField;
    private javax.swing.JButton returnButton;
    // End of variables declaration//GEN-END:variables
    
}