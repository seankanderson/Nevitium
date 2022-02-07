/*
 * PaymentDialog.java
 *
 * Created on July 22, 2006, 12:29 PM
 ** Copyright (c) Data Virtue 2006, 2022
 */
package com.datavirtue.nevitium.ui.invoices;

import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.datavirtue.nevitium.models.invoices.InvoicePaymentType;
import com.datavirtue.nevitium.models.invoices.PaymentTypeComboModel;
import com.datavirtue.nevitium.services.DiService;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.InvoicePaymentService;
import com.datavirtue.nevitium.services.InvoiceService;
import com.datavirtue.nevitium.ui.util.Tools;
import com.datavirtue.nevitium.ui.util.JTextFieldFilter;
import com.datavirtue.nevitium.services.util.DV;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DateFormat;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class PaymentDialog extends javax.swing.JDialog {

    private InvoiceService invoiceService;
    private Invoice currentInvoice;
    private InvoicePaymentService paymentService;
    private List<InvoicePaymentType> paymentTypes;

    public PaymentDialog(java.awt.Frame parent, boolean modal, Invoice invoice) {

        super(parent, modal);
        this.currentInvoice = invoice;
        initComponents();
        paymentAmountField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        var injector = DiService.getInjector();
        this.invoiceService = injector.getInstance(InvoiceService.class);
        this.paymentService = injector.getInstance(InvoicePaymentService.class);

        /* Close dialog on escape */
        ActionMap am = getRootPane().getActionMap();
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object windowCloseKey = new Object();
        KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action windowCloseAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        im.put(windowCloseStroke, windowCloseKey);
        am.put(windowCloseKey, windowCloseAction);
        /**/

        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        //props = new Settings (workingPath + "settings.ini");
        //paymentURL = props.getProp("PAYMENT URL");
        if (paymentURL.length() > 0) {
            usePaymentSystem = true;
//        webPayment = DV.parseBool(props.getProp("WEB PAYMENT"), false);
//        ccPayment = DV.parseBool(props.getProp("CC PAYMENT"), false);
//        chkPayment = DV.parseBool(props.getProp("CHK PAYMENT"), false);

        }

        //theInvoice = new OldInvoice(application, i_key);
        try {

            //daily_rate = Float.parseFloat( props.getProp("CR RATE")) / 365;
        } catch (Exception ex) {

            //daily_rate = 0.00f;
        }

    }

    public void display() throws SQLException {
        modelToView(currentInvoice);

        // get payment types
        this.paymentTypes = paymentService.getPaymentTypes();

        this.paymentTypeCombo.setModel(new PaymentTypeComboModel(this.paymentTypes));
        this.paymentTypeCombo.setSelectedIndex(0);
        setDetails();
        this.setVisible(true);

    }

    private void modelToView(Invoice invoice) throws SQLException {

        this.invoiceNumberField.setText(invoice.getInvoiceNumber());
        DateFormat df = paymentDatePicker.getDateFormat();
        this.invoiceDateField.setText(df.format(invoice.getInvoiceDate()));

        this.balanceDue = invoiceService.calculateInvoiceAmountDue(invoice);
        this.previousBalanceField.setText(DV.money(balanceDue));
        this.interestField.setText("0.00");
        this.balanceDueField.setText(DV.money(balanceDue));

//        inv_num = theInvoice.getInvoiceNumber();
//        issueDate = new Date(theInvoice.getDate());
//        invoiceField.setText(inv_num);
//
//        DateFormat df = datePicker1.getDateFormat();
//
//        dateField.setText(df.format(issueDate));
//        invoice_total = theInvoice.getInvoiceTotal();
//
//        if (debug) {
//            System.out.println("Payment Dialog: invoice total " + invoice_total);
//        }
//        refField.requestFocus();
//
//        setInterest();
    }

    private void setInterest() {
        /* Start at the top of the payments and subtract the date from  */
 /* Take todays date  */
 /* Keep a record of the lowest mtch and compare */
//        long paymentDate = datePicker1.getDate().getTime();
//
//        //Search through and get the date of the last credit
//        last_payment_date
//                = theInvoice.getLastPaymentDate(paymentDate);
//
//        int h = DV.howManyDays(last_payment_date, datePicker1.getDate().getTime());
//
//        prev_balance = theInvoice.getTotalDue();
//        if (debug) {
//            System.out.println("Payment Dialog: prev balance: " + prev_balance);
//        }
//        //if (h < 0) h = 0;        
//        int grace = 30;
//        try {
//
//            //grace = Integer.parseInt(props.getProp("GRACE"));
//        } catch (NumberFormatException ex) {
//
//            grace = 30;
//
//        }
//        h = h - grace;
//
//        if (h > 0 && intBox.isSelected()) {
//            current_interest = Tools.round(prev_balance * daily_rate * h);
//
//            due_now = Tools.round(prev_balance + current_interest);
//
//        } else {
//
//            current_interest = 0.00f;
//            due_now = Tools.round(prev_balance + current_interest);
//
//        }
//        previousField.setText(DV.money(prev_balance));
//        interestField.setText(DV.money(current_interest));
//        dueField.setText(DV.money(due_now));
//        amtField.setText(DV.money(due_now));

    }

    private boolean postPaymentOld() {

//
//        float total_payment = 0.00f;
//
//        paymentType = (String) typeCombo.getSelectedItem();
//
//        /* check this error */
//        try {
//            total_payment = Float.parseFloat(amtField.getText());
//            if (debug) {
//                System.out.println("Value obtained from amtField " + total_payment);
//            }
//
//            if (total_payment < .01f) {
//                javax.swing.JOptionPane.showMessageDialog(null, "Cannot Enter a Zero or Negative Payment Amount");
//                return false;
//            }
//        } catch (Exception e) {
//
////            javax.swing.JOptionPane.showMessageDialog(null, "Enter a valid amount, Example: "+
////                    props.getProp("SYM") + Tools.round(due_now));
//            return false;
//
//        }
//
//        if (paymentType.equalsIgnoreCase("Prepaid")) {
//            GiftCardDAO gc = new GiftCardDAO(db);
//            boolean acctValid = gc.loadAccount(refField.getText().trim());
//
//            if (!acctValid) {
//
//                javax.swing.JOptionPane.showMessageDialog(null,
//                        "Could not find the Prepaid Account number");
//                return false;
//            }
//
//            float acctBal = gc.getBalance();
//
//            if (acctBal <= 0) {
//
//                javax.swing.JOptionPane.showMessageDialog(null,
//                        "No Available Balance on this Account.");
//                return false;
//            }
//
//            if (acctBal < total_payment) {
//                javax.swing.JOptionPane.showMessageDialog(null,
//                        "The Prepaid account only has a balance of " + DV.money(acctBal) + nl
//                        + "Only " + DV.money(acctBal) + " will be applied as a payment.");
//                total_payment = Tools.round(acctBal);
//                gc.useFunds(acctBal);
//            } else {
//
//                gc.useFunds(total_payment);
//
//            }
//        }
//
//        float balance = 0.00f;
//        Object[] payment = new Object[7];
//
//        /* If interest is generated record a debit */
//        if (current_interest > 0.00f && intBox.isSelected()) {
//
//            //System.out.println("made it in"+current_interest);
//            /* Interest Charge */
//            payment[0] = new Integer(0);  //all payments recored will be new
//            payment[1] = new String(inv_num);  //invoice number (FK)
//            //Version 1.5
//            payment[2] = new Long(datePicker1.getDate().getTime());  //payment date
//            payment[3] = new String("Interest");  //cash cc chk int
//            payment[4] = new String("CHARGE");  //memo
//            payment[5] = new Float(current_interest);  //Debit, interest charge amount
//
//            payment[6] = new Float(0.00f);  //cr
//            //payment [7] = new Float( due_now );  //balalnce adj
//            theInvoice.recordPayment(payment);
//        }
//
//        /* due_now already has the interest tagged onto the previous balance */
//        if (paymentType.equalsIgnoreCase("Fee") || paymentType.equalsIgnoreCase("Refund")) {
//            balance = due_now + total_payment;
//        } else {
//            balance = due_now - total_payment;
//        }
//
//        boolean printCheck = false;
//        float overPayment = 0.00f;
//
//        /* Setup rounding scheme and add "payment" reflecting the change in amount due */
//        if (paymentType.equalsIgnoreCase("cash")) {
//
////            if ((props.getProp("CASHRND").equals(".05") && (getHundredth(getDecimal(due_now)) != 5))
////                    || (props.getProp("CASHRND").equals(".10") && (getHundredth(getDecimal(due_now)) != 0))) {
////
////                /* Payment record setup */
////                payment[0] = new Integer(0);  //all payments recored will be new
////                payment[1] = new String(inv_num);  //invoice number (FK)
////                payment[2] = new Long(datePicker1.getDate().getTime());  //payment date
////
////                float diff;
////                float new_amt;
////
////                /* 10th */
////                if (props.getProp("CASHRND").equals(".10")) {
////
////                    new_amt = this.roundToNearest10th(due_now);
////                    diff = due_now - new_amt;
////                    if (diff < 0) { //amount was increased, need to add a fee for diff amount
////                        payment[3] = new String("Fee");  //cash cc chk int
////                        payment[4] = new String("CASH SALE ROUNDING OFFSET");  //memo
////                        diff = diff * -1;
////                        payment[5] = new Float(Tools.round(diff));  //Debit, interest charge amount
////                        payment[6] = new Float(0.00f);  //cr
////                        theInvoice.recordPayment(payment);
////                    }
////
////                    if (diff > 0) { //amount was decreased, need to add a credit for diff amount
////                        payment[3] = new String("Credit");  //cash cc chk int
////                        payment[4] = new String("CASH SALE ROUNDING OFFSET");  //memo
////                        payment[5] = new Float(0.00f);  //db
////                        payment[6] = new Float(Tools.round(diff));  //cr
////                        theInvoice.recordPayment(payment);
////                    }
////                }
////                /* 5th */
////                if (props.getProp("CASHRND").equals(".05")) {
////                    new_amt = this.roundToNearest5th(due_now);
////                    diff = due_now - new_amt;
////                    if (diff < 0) { //amount was increased, need to add a fee for diff amount
////                        payment[3] = new String("Fee");  //cash cc chk int
////                        payment[4] = new String("CASH SALE ROUNDING OFFSET");  //memo
////                        payment[5] = new Float(Tools.round(diff));  //Debit, interest charge amount
////                        payment[6] = new Float(0.00f);  //cr
////                        theInvoice.recordPayment(payment);
////                    }
////
////                    if (diff > 0) { //amount was decreased, need to add a credit for diff amount
////                        payment[3] = new String("Credit");  //cash cc chk int
////                        payment[4] = new String("CASH SALE ROUNDING OFFSET");  //memo
////                        payment[5] = new Float(0.00f);  //db
////                        payment[6] = new Float(Tools.round(diff));  //cr
////                        theInvoice.recordPayment(payment);
////                    }
////                }
////
////            }
//
//        }//END ROUNDING CODE
//
//        /* Principle Payment */
//        payment[0] = new Integer(0);  //make sure to reset the key int
//        payment[1] = new String(inv_num);  //invoice number
//
//        long dateStamp = datePicker1.getDate().getTime();
//
//        //TODO: Adjust interest calculation based on last credit date
//        //Currently only grabbing date from last payment record
//        payment[2] = new Long(dateStamp);  //payment date
//        payment[3] = new String(paymentType);  //cash cc chk AND int payment
//        payment[4] = new String(refField.getText());  //memo
//
//        if (paymentType.equalsIgnoreCase("Fee")
//                || paymentType.equalsIgnoreCase("Refund")) {
//            payment[5] = new Float(total_payment);  //principle payment amount
//            payment[6] = new Float(0.00f);
//            //payment [7] = new Float(due_now + total_payment);
//
//        } else {
//            payment[5] = new Float(0.00f);
//            payment[6] = new Float(total_payment);
//            //payment [7] = new Float(due_now - total_payment);
//        }
//
//        theInvoice.recordPayment(payment);
//
//        /* Show change/refund amount  */
//        if (total_payment > due_now
//                && !paymentType.equalsIgnoreCase("Fee")
//                && !paymentType.equalsIgnoreCase("Refund")) {
//
//            overPayment = total_payment - due_now;
//
//            /* Record Refund Adjustment */
//            payment[0] = new Integer(0);  //all payments recored will be new
//            payment[1] = new String(inv_num);  //invoice number (FK)
//            //Version 1.5
//            payment[2] = new Long(datePicker1.getDate().getTime());  //payment date
//            payment[3] = new String("Refund");  //cash cc chk int
//            payment[4] = new String("Over Paid");  //memo
//            payment[5] = new Float(overPayment);
//            payment[6] = new Float(0.00f);  //balalnce adj
//            //payment [7] = new Float(balance + overPayment);  //balalnce adj
//            theInvoice.recordPayment(payment);
//
//            int a = javax.swing.JOptionPane.showConfirmDialog(null,
//                    "A refund adjustment for " + DV.money(overPayment) + " was recorded." + nl
//                    + "Do you want to print a check for this amount?", "Over Payment", JOptionPane.YES_NO_OPTION);
//            if (a == JOptionPane.YES_OPTION) {
//                printCheck = true;
//            }
//        }
//
//        payment = null;
//
//        if (printCheck) {
//
////               if (accessKey.checkCheck(500)){
//////                    new CheckDialog(null,true, application,
//////                       theInvoice.getCustKey(), overPayment,
//////                       "Invoice Overpayment: "+theInvoice.getInvoiceNumber());
////               }
//        }
//
//        balance = theInvoice.getTotalDue();
//
//        if (balance > 0) {
//            theInvoice.setPaid(false);
//            theInvoice.saveInvoice();
//        } else {
//            theInvoice.setPaid(true);
//            theInvoice.saveInvoice();
//        }
//        theInvoice = null;
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        previousBalanceField = new javax.swing.JTextField();
        interestField = new javax.swing.JTextField();
        balanceDueField = new javax.swing.JTextField();
        intBox = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        paymentTypeCombo = new javax.swing.JComboBox();
        paymentMemoField = new javax.swing.JTextField();
        paymentAmountField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        memoLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        paymentSystemBox = new javax.swing.JCheckBox();
        helpTextField = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        postButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        invoiceNumberField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        invoiceDateField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        paymentDatePicker = new com.michaelbaranov.microba.calendar.DatePicker();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Payment Entry");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel2.setFocusable(false);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Previous Balance: ");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Current Interest: ");

        previousBalanceField.setEditable(false);
        previousBalanceField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        interestField.setEditable(false);
        interestField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        balanceDueField.setEditable(false);
        balanceDueField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        intBox.setSelected(true);
        intBox.setText("Include Interest");
        intBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        intBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        intBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intBoxActionPerformed(evt);
            }
        });

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Balance Due: ");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(previousBalanceField)
                            .add(interestField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(0, 65, Short.MAX_VALUE)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, intBox)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                                .add(jLabel11)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(balanceDueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(previousBalanceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(interestField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(intBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(balanceDueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel11))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        paymentTypeCombo.setToolTipText("Select The Payment / Fee Type");
        paymentTypeCombo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paymentTypeComboMouseClicked(evt);
            }
        });
        paymentTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentTypeComboActionPerformed(evt);
            }
        });

        paymentMemoField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paymentMemoFieldKeyPressed(evt);
            }
        });

        paymentAmountField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        paymentAmountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paymentAmountFieldKeyPressed(evt);
            }
        });

        jLabel4.setText("Payment Type");

        memoLabel.setText("Memo");

        jLabel6.setText("Amount");

        paymentSystemBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        paymentSystemBox.setText("Launch Payment System");
        paymentSystemBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        paymentSystemBox.setIconTextGap(7);

        helpTextField.setEditable(false);
        helpTextField.setText("A payment designated as cash received from the customer.");
        helpTextField.setToolTipText("Payment Type Details");
        helpTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpTextFieldActionPerformed(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        postButton.setText("Post");
        postButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(paymentSystemBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(paymentTypeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(memoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(paymentMemoField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 241, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(92, 92, 92))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(paymentAmountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(postButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE))
                    .add(helpTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(memoLabel)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(paymentAmountField)
                            .add(postButton)
                            .add(paymentMemoField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(paymentTypeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(19, 19, 19)))
                .add(paymentSystemBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(helpTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18))
        );

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Invoice#:");

        invoiceNumberField.setEditable(false);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Invoice Date:");

        invoiceDateField.setEditable(false);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Payment Date: ");

        paymentDatePicker.setFieldEditable(false);
        paymentDatePicker.setShowNoneButton(false);
        paymentDatePicker.setStripTime(true);
        paymentDatePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentDatePickerActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(paymentDatePicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .add(invoiceDateField)
                    .add(invoiceNumberField))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(invoiceNumberField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(invoiceDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(paymentDatePicker, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(11, 11, 11)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void paymentMemoFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentMemoFieldKeyPressed

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {

            paymentAmountField.requestFocus();

        }

    }//GEN-LAST:event_paymentMemoFieldKeyPressed

    private void post() throws SQLException {

        var paymentTypeModel = (PaymentTypeComboModel) paymentTypeCombo.getModel();
        var paymentType = (InvoicePaymentType) paymentTypeModel.getType(this.paymentTypeCombo.getSelectedIndex());

        if (paymentType == null) {
            return;
        }
        var balanceDue = invoiceService.calculateInvoiceAmountDue(currentInvoice);

        
        if (balanceDue < 0 && !paymentType.getName().equalsIgnoreCase("refund")) {

            JOptionPane.showMessageDialog(null,
                    "Only refunds can be applied to invoices with a negative balance.",
                    "Payment Type", JOptionPane.OK_OPTION);

            return;
        }

        if (paymentType.getName().equalsIgnoreCase("refund")) {

            if (balanceDue >= 0) {

                JOptionPane.showMessageDialog(null,
                        "Refunds are only applied to invoices that have a negative balance." + nl
                        + "Try applying a credit instead.", "Refund Status", JOptionPane.OK_OPTION);
                return;
            }

        }

        if (paymentType.getName().equalsIgnoreCase("credit")) {

            if (balanceDue < 0) {

                JOptionPane.showMessageDialog(null,
                        "Credits should not be applied to invoices with a negative balance." + nl
                        + "Try issuing a refund instead.", "Refund Status", JOptionPane.OK_OPTION);
                return;
            }
        }

        if (paymentMemoField.getText().trim().equalsIgnoreCase("RETURN")) {

            JOptionPane.showMessageDialog(null, "You cannot use 'RETURN' as a memo.", "Form Problem!", JOptionPane.OK_OPTION);
            return;

        }

        if (Float.parseFloat(paymentAmountField.getText()) <= 0.00f) {

            JOptionPane.showMessageDialog(null,
                    "You have to supply an amount greater than zero.",
                    "Form Problem!", JOptionPane.OK_OPTION);
            return;
        }

        
        if (paymentType.isInvoiceCredit()) { 
            postCredit();
        }else if (paymentType.isInvoiceDebit()) {
            postDebit();
        }
                
        if (usePaymentSystem && paymentSystemBox.isSelected()) {

            if (webPayment) {

                int a = DV.launchURL(paymentURL);
                if (a < 1) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "There was a problem trying to launch your web browser."
                            + nl + "This may not be supported by your Operating System.");
                }

            } else {

                launchPaymentSystem();
            }

        }

        
        balanceDue = invoiceService.calculateInvoiceAmountDue(currentInvoice);
        
        if (balanceDue > 0 && paymentType.getName().equals("Prepaid")) {
            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "The Prepaid Account was not able to payout the invoice.  Would you like to accept another payment?",
                    "Another Payment?", JOptionPane.YES_NO_OPTION);
            if (a == 0) {
                return;
            }
        }
        
        if (balanceDue <= 0) {
            this.currentInvoice.setPaid(true);
            this.invoiceService.save(currentInvoice);
        }
        this.dispose();

    }

    private InvoicePayment primeNewInvoicePayment() {

        var paymentTypeModel = (PaymentTypeComboModel) paymentTypeCombo.getModel();
        var paymentType = (InvoicePaymentType) paymentTypeModel.getType(this.paymentTypeCombo.getSelectedIndex());

        if (paymentType == null) {
            return null;
        }

        var payment = new InvoicePayment();
        payment.setInvoice(currentInvoice);
        payment.setMemo(this.paymentMemoField.getText());
        payment.setPaymentActivityDate(new Date());
        payment.setPaymentEffectiveDate(this.paymentDatePicker.getDate());
        payment.setPaymentType(paymentType);
        return payment;
    }

    private void postCredit() {
        var amount = Double.parseDouble(this.paymentAmountField.getText());
        var payment = primeNewInvoicePayment();
        payment.setCredit(amount);
        try {
            paymentService.save(payment);
            
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error saving payment to database");
        }

        return;
    }

    /**
     * Revenue payments
     *
     * @param paymentType
     * @return
     */
    private void postDebit() {
        var amount = Double.parseDouble(this.paymentAmountField.getText());
        var payment = primeNewInvoicePayment();
        payment.setDebit(amount);
        try {
            paymentService.save(payment);
            
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error saving payment to database");
        }
        return;
    }

    private void launchPaymentSystem() {

        String nl = System.getProperty("line.separator");

        String osName = System.getProperty("os.name");

        try {

            if (osName.equals("Windows")) {
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


    private void postButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postButtonActionPerformed

        if (memoLabel.getText().equals("Acct#") && paymentMemoField.getText().trim().equals("")) {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "You need to provide an account number to process a prepaid account payment.");
            paymentMemoField.requestFocus();
            return;
        }

        try {
            this.post();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error accessing invoice database");
        }

    }//GEN-LAST:event_postButtonActionPerformed

    private void helpTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpTextFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_helpTextFieldActionPerformed

    private void paymentTypeComboMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paymentTypeComboMouseClicked

        setDetails();

    }//GEN-LAST:event_paymentTypeComboMouseClicked

    private void paymentTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentTypeComboActionPerformed
        setDetails();
    }//GEN-LAST:event_paymentTypeComboActionPerformed

    private void paymentAmountFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paymentAmountFieldKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            try {
                this.post();
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error accessing invoice database");
            }
        }

    }//GEN-LAST:event_paymentAmountFieldKeyPressed

    private void paymentDatePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentDatePickerActionPerformed
        setInterest();
    }//GEN-LAST:event_paymentDatePickerActionPerformed

    private void intBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intBoxActionPerformed
        setInterest();
    }//GEN-LAST:event_intBoxActionPerformed

    private String roundAmountDue() {

        double decimal = Tools.round((balanceDue % 1) * 100);
        decimal = decimal - (decimal % 1);
        double hundredth = Tools.round(((decimal * .1f) % 1) * 10);

//        /* 10th */
//        if (props.getProp("CASHRND").equals(".10")) {
//            if (hundredth <= 5) {
//                val -= (hundredth * .01); //rounded down to nearest 10th
//            }
//            if (hundredth > 5) {
//                val += (.10 - (hundredth * .01)); //rounded up to nearest 10th
//            }
//            return DV.money(val);
//        }
//        /* 5th */
//        if (props.getProp("CASHRND").equals(".05")) {
//            if (hundredth <= 3) {
//                val -= (hundredth * .01); //rounded down to nearest 5th
//            }
//            if (hundredth > 3 && hundredth < 5) {
//                val += (.05f - (hundredth * .01f)); //rounded up to nearest 5th
//            }
//            if (hundredth > 5) {
//                val += (.10f - (hundredth * .01f)); //rounded up to nearest 5th
//            }
//            return DV.money(val);
//        }
        return DV.money(balanceDue);

    }

    private double balanceDue = 0.00;

    private void setDetails() {

        var paymentTypeModel = (PaymentTypeComboModel) paymentTypeCombo.getModel();
        var paymentType = (InvoicePaymentType) paymentTypeModel.getType(this.paymentTypeCombo.getSelectedIndex());

        if (paymentType == null) {
            return;
        }

        memoLabel.setText("Memo");

        helpTextField.setText(paymentType.getDescription());

        if (paymentType.isInvoiceCredit()) {
            paymentAmountField.setText(DV.money(balanceDue));
        }

        if (paymentType.getName().equals("card")) {
            paymentSystemBox.setSelected(true);
        }

        if (paymentType.getName().equalsIgnoreCase("cash")) {

//            String rounding = props.getProp("CASHRND");
//            String msg = "A payment designated as cash received from the customer.";
//            if (rounding.equals(".05") || rounding.equals(".10")) {
//                msg = "A payment designated as cash received from the customer. ROUNDED TO: " + this.roundAmountDue();
//            }
//            detailsBox.setText(msg);
//            paymentSystemBox.setSelected(false);
//            amtField.setText(this.roundAmountDue());
        }

        if (balanceDue < 0 && paymentType.isInvoiceCredit()) {
            helpTextField.setText(
                    "A credit can only be applied to a positive balance, try a refund instead.");
        }

        if (balanceDue > 0 && paymentType.getName().equalsIgnoreCase("refund")) {
            helpTextField.setText(
                    "A Refund can only be applied to a negative balance.");
        }
    }

    private String nl = System.getProperty("line.separator");

    String paymentURL = "";
    boolean usePaymentSystem = false;
    boolean webPayment = false;
    boolean ccPayment = false;
    boolean chkPayment = false;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField balanceDueField;
    private javax.swing.JTextField helpTextField;
    private javax.swing.JCheckBox intBox;
    private javax.swing.JTextField interestField;
    private javax.swing.JTextField invoiceDateField;
    private javax.swing.JTextField invoiceNumberField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel memoLabel;
    private javax.swing.JTextField paymentAmountField;
    private com.michaelbaranov.microba.calendar.DatePicker paymentDatePicker;
    private javax.swing.JTextField paymentMemoField;
    private javax.swing.JCheckBox paymentSystemBox;
    private javax.swing.JComboBox paymentTypeCombo;
    private javax.swing.JButton postButton;
    private javax.swing.JTextField previousBalanceField;
    // End of variables declaration//GEN-END:variables

}
