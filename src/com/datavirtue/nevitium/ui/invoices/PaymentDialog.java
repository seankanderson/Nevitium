/*
 * PaymentDialog.java
 *
 * Created on July 22, 2006, 12:29 PM
 ** Copyright (c) Data Virtue 2006
 */
package com.datavirtue.nevitium.ui.invoices;

import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.services.DiService;
import com.datavirtue.nevitium.services.InvoiceService;
import com.datavirtue.nevitium.ui.util.Tools;
import com.datavirtue.nevitium.ui.util.JTextFieldFilter;
import com.datavirtue.nevitium.services.util.DV;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DateFormat;
import java.sql.SQLException;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class PaymentDialog extends javax.swing.JDialog {

    private InvoiceService invoiceService;
    private Invoice currentInvoice;
    
    public PaymentDialog(java.awt.Frame parent, boolean modal, Invoice invoice) {

        super(parent, modal);
        this.currentInvoice = invoice;
        initComponents();
        amtField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        var injector = DiService.getInjector();
        this.invoiceService = injector.getInstance(InvoiceService.class);

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
        setDetails();
        this.setVisible(true);
        
    }
    
    private void modelToView(Invoice invoice) throws SQLException {

        this.invoiceNumberField.setText(invoice.getInvoiceNumber());
        DateFormat df = paymentDatePicker.getDateFormat();
        this.invoiceDateField.setText(df.format(invoice .getInvoiceDate()));
        var balanceDue = invoiceService.calculateInvoiceAmountDue(invoice); 
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

    private float getHundredth(float decimal) {
        float hundredth = Tools.round(((decimal * .1f) % 1) * 10);
        return hundredth;
    }

    private float getDecimal(float amt) {
        float decimal = Tools.round((amt % 1) * 100);
        decimal = decimal - (decimal % 1);
        return decimal;
    }

    private float roundToNearest5th(float amt) {
        float hundredth = getHundredth(getDecimal(amt));
        if (hundredth <= 3) {
            return (amt -= (hundredth * .01)); //rounded down to nearest 5th
        }
        if (hundredth > 3) {
            return (amt += (.05 - (hundredth * .01))); //rounded up to nearest 5th
        }
        return amt;
    }

    private float roundToNearest10th(float amt) {
        float hundredth = getHundredth(getDecimal(amt));
        if (hundredth <= 5) {
            return (amt -= (hundredth * .01)); //rounded down to nearest 10th
        }
        if (hundredth > 5) {
            return (amt += (.10 - (hundredth * .01))); //rounded up to nearest 10th
        }
        return amt;
    }

    private boolean postPayment() {
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
        typeCombo = new javax.swing.JComboBox();
        refField = new javax.swing.JTextField();
        amtField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        memoLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        paymentSystemBox = new javax.swing.JCheckBox();
        detailsBox = new javax.swing.JTextField();
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

        typeCombo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        typeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cash", "Check", "CC", "Debit/EFT", "Credit", "Prepaid", "Fee", "Refund" }));
        typeCombo.setToolTipText("Select The Payment / Fee Type");
        typeCombo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                typeComboMouseClicked(evt);
            }
        });
        typeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboActionPerformed(evt);
            }
        });

        refField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        refField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                refFieldKeyPressed(evt);
            }
        });

        amtField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        amtField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        amtField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                amtFieldKeyPressed(evt);
            }
        });

        jLabel4.setText("Payment Type");

        memoLabel.setText("Memo");

        jLabel6.setText("Amount");

        paymentSystemBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        paymentSystemBox.setText("Launch Payment System");
        paymentSystemBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        paymentSystemBox.setIconTextGap(7);

        detailsBox.setEditable(false);
        detailsBox.setText("A payment designated as cash received from the customer.");
        detailsBox.setToolTipText("Payment Type Details");
        detailsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsBoxActionPerformed(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        postButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/OK.png"))); // NOI18N
        postButton.setText("Post");
        postButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(postButton);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(paymentSystemBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(typeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(refField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(memoLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(amtField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                    .add(detailsBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
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
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, refField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, amtField)
                    .add(typeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(paymentSystemBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(detailsBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void refFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_refFieldKeyPressed

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {

            amtField.requestFocus();

        }

        // TODO add your handling code here:
    }//GEN-LAST:event_refFieldKeyPressed

    String paymentType = "";

    private void post() {

        //check for proper input
        paymentType = (String) typeCombo.getSelectedItem();

        if (due_now < 0 && !paymentType.equalsIgnoreCase("refund")) {

            JOptionPane.showMessageDialog(null,
                    "Only refunds can be applied to invoices with a negative balance.",
                    "Payment Type", JOptionPane.OK_OPTION);

            return;
        }

        if (paymentType.equalsIgnoreCase("refund")) {

            if (due_now >= 0) {

                JOptionPane.showMessageDialog(null,
                        "Refunds are only applied to invoices that have a negative balance." + nl
                        + "Try applying a credit instead.", "Refund Status", JOptionPane.OK_OPTION);
                return;
            }

        }

        if (paymentType.equalsIgnoreCase("credit")) {

            if (due_now < 0) {

                JOptionPane.showMessageDialog(null,
                        "Credits should not be applied to invoices with a negative balance." + nl
                        + "Try issuing a refund instead.", "Refund Status", JOptionPane.OK_OPTION);
                return;
            }

        }

        if (refField.getText().trim().equalsIgnoreCase("RETURN")) {

            JOptionPane.showMessageDialog(null, "You cannot use 'RETURN' as a memo.", "Form Problem!", JOptionPane.OK_OPTION);
            return;

        }

        if (Float.parseFloat(amtField.getText()) <= 0.00f) {

            JOptionPane.showMessageDialog(null,
                    "You have to supply an amount greater than zero.",
                    "Form Problem!", JOptionPane.OK_OPTION);
            return;
        }

        boolean pmtstat = postPayment();

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

        if (pmtstat == false && paymentType.equals("Prepaid")) {
            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "The Prepaid Account was not able to payout the invoice.  Would you like to accept another payment?",
                    "Another Payment?", JOptionPane.YES_NO_OPTION);
            if (a == 0) {
                return;
            }
        }
        this.dispose();

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

        if (memoLabel.getText().equals("Acct#") && refField.getText().trim().equals("")) {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "You need to provide an Account Number to process a Prepaid Account payment.");
            refField.requestFocus();
            return;
        }

        post();

    }//GEN-LAST:event_postButtonActionPerformed

    private void detailsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_detailsBoxActionPerformed

    private void typeComboMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_typeComboMouseClicked

        setDetails();

    }//GEN-LAST:event_typeComboMouseClicked

    private void typeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboActionPerformed
        setDetails();
    }//GEN-LAST:event_typeComboActionPerformed

    private void amtFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_amtFieldKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            this.post();
        }

    }//GEN-LAST:event_amtFieldKeyPressed

    private void paymentDatePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentDatePickerActionPerformed
        setInterest();
    }//GEN-LAST:event_paymentDatePickerActionPerformed

    private void intBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intBoxActionPerformed
        setInterest();
    }//GEN-LAST:event_intBoxActionPerformed

    private String roundAmountDue() {
        float val = due_now;
        float decimal = Tools.round((val % 1) * 100);
        decimal = decimal - (decimal % 1);
        float hundredth = Tools.round(((decimal * .1f) % 1) * 10);

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
        return DV.money(val);

    }

    private void setDetails() {

        String p = (String) typeCombo.getSelectedItem();

        memoLabel.setText("Memo");

        if (p.equalsIgnoreCase("Cash")) {

//            String rounding = props.getProp("CASHRND");
//            String msg = "A payment designated as cash received from the customer.";
//            if (rounding.equals(".05") || rounding.equals(".10")) {
//                msg = "A payment designated as cash received from the customer. ROUNDED TO: " + this.roundAmountDue();
//            }
//            detailsBox.setText(msg);
//            paymentSystemBox.setSelected(false);
//            amtField.setText(this.roundAmountDue());
        }

        if (p.equalsIgnoreCase("CC")) {
            detailsBox.setText(
                    "A payment designated as a credit card received from the customer.");
            if (ccPayment) {
                paymentSystemBox.setSelected(true);
            }
            amtField.setText(DV.money(due_now));
        }

        if (p.equalsIgnoreCase("Check")) {
            detailsBox.setText(
                    "A payment designated as a check received from the customer.");
            if (chkPayment) {
                paymentSystemBox.setSelected(true);
            }
            amtField.setText(DV.money(due_now));
        }
        if (p.equalsIgnoreCase("Debit/EFT")) {
            detailsBox.setText(
                    "A payment by electronic funds transfer received from the customer.");
            if (ccPayment) {
                paymentSystemBox.setSelected(true);
            }
            amtField.setText(DV.money(due_now));
        }

        if (p.equalsIgnoreCase("Prepaid")) {
            memoLabel.setText("Acct#");
            detailsBox.setText(
                    "Prepaid Account (Swipe or Scan a Prepaid Card)");
            amtField.setText(DV.money(due_now));
            refField.requestFocus();
        }

        if (p.equalsIgnoreCase("Credit")) {
            paymentSystemBox.setSelected(false);
            detailsBox.setText(
                    "Applies a credit to the invoice but is not counted as revenue.");
            if (due_now < 0) {
                detailsBox.setText(
                        "A credit can only be applied to a positive balance, try a refund instead.");
            }
        }

        if (p.equalsIgnoreCase("Fee")) {
            paymentSystemBox.setSelected(false);
            detailsBox.setText(
                    "A fee or charge against an invoice; NSF etc..");
        }

        if (p.equalsIgnoreCase("Refund")) {
            paymentSystemBox.setSelected(false);
            detailsBox.setText(
                    "Use to adjust overpaid invoices.");
            if (due_now > 0) {
                detailsBox.setText(
                        "A Refund can only be applied to a negative balance.");
            }
        }

    }

  
    private String nl = System.getProperty("line.separator");
   
    private float due_now;
   
    String paymentURL = "";
    boolean usePaymentSystem = false;
    boolean webPayment = false;
    boolean ccPayment = false;
    boolean chkPayment = false;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField amtField;
    private javax.swing.JTextField balanceDueField;
    private javax.swing.JTextField detailsBox;
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
    private com.michaelbaranov.microba.calendar.DatePicker paymentDatePicker;
    private javax.swing.JCheckBox paymentSystemBox;
    private javax.swing.JButton postButton;
    private javax.swing.JTextField previousBalanceField;
    private javax.swing.JTextField refField;
    private javax.swing.JComboBox typeCombo;
    // End of variables declaration//GEN-END:variables

}
