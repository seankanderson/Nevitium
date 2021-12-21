/*
 * InvoiceManager.java
 *
 * Created on July 8, 2006, 8:25 AM
 ** Copyright (c) Data Virtue 2006
 */
package com.datavirtue.nevitium.ui.invoices;

import RuntimeManagement.KeyCard;
import RuntimeManagement.GlobalApplicationDaemon;
import com.datavirtue.nevitium.models.invoices.old.OldInvoice;
import com.datavirtue.nevitium.models.invoices.old.Quote;
import com.datavirtue.nevitium.database.reports.ReportFactory;
import com.datavirtue.nevitium.models.contacts.Contact;

import datavirtue.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.awt.event.*;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class InvoiceManager extends javax.swing.JDialog {

    private final KeyCard accessKey;
    private final boolean debug = false;
    private final GlobalApplicationDaemon application;
    private boolean searchResults = false;
    private String searchString = "";

    /**
     * Creates new form InvoiceManager
     */
    public InvoiceManager(java.awt.Frame parent, boolean modal, GlobalApplicationDaemon application) {

        super(parent, modal);

        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        initComponents();

        statusToolbar.setLayout(new FlowLayout());
        actionToolbar.setLayout(new FlowLayout());
        accessKey = application.getKey_card();

        this.application = application;

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

        props = application.getProps();//new Settings (workingPath + "settings.ini");

        String qname = props.getProp("QUOTE NAME");

        if (qname != null) {
            quoteRadio.setText(qname);
        }

        db = application.getDb();
        parentWin = parent;

        refreshTables();

        this.selectFirstRow(jTable1);
        iPrefix = props.getProp("INVOICE PREFIX");
        qPrefix = props.getProp("QUOTE PREFIX");
        searchField.setText(iPrefix);
        searchField.requestFocus();
        this.setVisible(true);
    }
    private int rememberedRow = 0;
    private String iPrefix = "";
    private String qPrefix = "";

    private void rememberRow() {

        rememberedRow = jTable1.getSelectedRow();

    }

    private void restoreRow() {

        if (jTable1.getRowCount() > rememberedRow) {

            jTable1.changeSelection(rememberedRow, 0, false, false);
        }

    }

    private void selectFirstRow(javax.swing.JTable jt) {

        if (jt.getRowCount() > 0) {
            jt.changeSelection(0, 0, false, false);
            this.setPayments();
        }

    }

    private void setView() {

        TableColumnModel cm;
        TableColumn tc;

        if (jTable1.getRowCount() > 0) {

            cm = jTable1.getColumnModel();

            for (int i = 0; i < cols.length; i++) {

                tc = cm.getColumn(cols[i]);
                jTable1.removeColumn(tc);

            }

            //properly size each col for the invoices
            jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
            int[] widths = new int[]{45, 50, 270, 55};

            for (int i = 0; i < widths.length; i++) {

                tc = jTable1.getColumnModel().getColumn(i);
                tc.setPreferredWidth(widths[i]);

            }
        }
    }

    private void resetSearch() {
        searchResults = false;
        searchString = "";
    }

    private void restoreSearch() {
        if (searchResults && searchCombo.getSelectedIndex() > 0) { //if set to customers and already searched
            searchField.setText(searchString);//restore our search with possible new data
            findInvoice();
            return;
        }
    }

    //refereshTables will attempt to restore he search
    private void refreshTables() {

        //added 11-25-2011 for V1.6
        restoreSearch();
        if (searchResults) {
            return;
        }

        boolean quotes = quoteRadio.isSelected();

        ArrayList al = new ArrayList();

        boolean sel = paidRadio.isSelected();

        if (!voidRadio.isSelected() && !quotes) {  //dont do this if void was selected
            // System.out.println("DEGUG: 3");
            al = db.search("invoice", 8, Boolean.toString(sel), false);  //list of all marked paid or unpaid
            //  System.out.println("DEGUG: 4");
        }

        if (quotes) {
            al = null;
            searchField.setText(qPrefix);
            tm = db.createTableModel("quote", jTable1);
            if (tm.getRowCount() < 1) {
                tm = new DefaultTableModel();
            }
        } else {
            searchField.setText(iPrefix);
        }

        if (voidRadio.isSelected()) {
            al = db.search("invoice", 9, "true", false);  //list of voided invoices
        }

        if (al == null && !quotes) {  //if no records set a blank table model

            tm = new DefaultTableModel();
            jTable1.setModel(tm);

            paymentTable.setModel(new DefaultTableModel());

            setView();
            return;
        } else {

            if (!quotes) {
                tm = db.createTableModel("invoice", al, jTable1);  //get a model from the list of all
            }
        }

        /* Remove any voids */
        if (!voidRadio.isSelected() && al != null && !quotes) {  //if void was not selected

            al = new ArrayList(); //al.clear();
            for (int r = 0; r < tm.getRowCount(); r++) {

                if (!(Boolean) tm.getValueAt(r, 9)) {
                    al.add((Integer) tm.getValueAt(r, 0));
                }
            }

            if (al.size() < 1) {
                tm = new DefaultTableModel();
            } else {
                tm = db.createTableModel("invoice", al, jTable1);
            }

        }  //end void removal

        jTable1.setModel(tm);
        paymentTable.setModel(new DefaultTableModel());
        setView();

    }

    private void setPayments() {

        if (jTable1.getRowCount() > 0) {
            //
        }

        if (jTable1.getSelectedRow() < 0) {
            return;
        }

        if (quoteRadio.isSelected() || voidRadio.isSelected()) {
            paymentTable.setModel(new DefaultTableModel());
            return;

        }

        int invKey = (Integer) tm.getValueAt(jTable1.getSelectedRow(), 0);

        OldInvoice inv = new OldInvoice(application, invKey);

        //System.out.println("DEGUG: 11"); //DEBUG
        paymentTable.setModel(inv.getPayments());
        //System.out.println("DEGUG: 12"); //DEBUG

        /* Nasty bug unless we skip col mods on no payments */
        if (paymentTable.getRowCount() <= 0) {
            return;
        }

        //remove cols 0 1          
        TableColumnModel cm = paymentTable.getColumnModel();
        TableColumn tc;

        //setup hold table view
        tc = cm.getColumn(0);
        paymentTable.removeColumn(tc);//remove key column 
        tc = cm.getColumn(0);
        paymentTable.removeColumn(tc);//remove inv # column 

        if (paymentTable.getRowCount() > 0) {

            paymentTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
            int[] widths = new int[]{85, 60, 110, 60, 60, 60};

            for (int i = 0; i < widths.length; i++) {

                tc = paymentTable.getColumnModel().getColumn(i);
                tc.setPreferredWidth(widths[i]);
            }
        }
    }

    private void findInvoice() {

        boolean found = false;

        int len = tm.getRowCount();

        if (searchCombo.getSelectedIndex() == 0) {
            for (int i = 0; i < len; i++) {
                if (searchField.getText().equalsIgnoreCase((String) tm.getValueAt(i, 1))) {
                    jTable1.changeSelection(i, 0, false, false);
                    setPayments();
                    found = true;
                }
            }
            resetSearch();
        }

        if (searchCombo.getSelectedIndex() == 2) { //search customers
            //search invoices for substring of customer
            //apply table model
            //setview - setpayments
            //clear table filters buttons (reset)

            ArrayList al = db.search("invoice", 3, searchField.getText(), true);

            if (al != null) {
                tm = db.createTableModel("invoice", al, true);
                jTable1.setModel(tm);

                paymentTable.setModel(new DefaultTableModel());

                this.setView();

                //this.setPayments();
                buttonGroup1.clearSelection();
                unpaidRadio.setSelected(false);
                paidRadio.setSelected(false);
                quoteRadio.setSelected(false);
                voidRadio.setSelected(false);

                found = true;
                searchResults = true;
                searchString = searchField.getText();
            }

        }

        if (searchCombo.getSelectedIndex() == 1) { //search invoice items
            //look for all items found to match search text
            //cycle thru and build arraylist of invoice keys without duplcates
            //get table model 
            //store search sttate
            ArrayList al = db.search("invitems", 5, searchField.getText(), true); //search invitems description

            if (al != null) {
                ArrayList clean = new ArrayList();
                clean.trimToSize(); //going to be resizing a lot!!!! not optimal

                DefaultTableModel tmptm = (DefaultTableModel) db.createTableModel("invitems", al, false);
                int tkey, ckey;
                boolean used = false;

                for (int t = 0; t < tmptm.getRowCount(); t++) {
                    used = false;
                    tkey = (Integer) tmptm.getValueAt(t, 1);//get invoice key from invitem

                    //cycle thru clean al and record new invoice key if it is not encountered
                    for (int c = 0; c < clean.size(); c++) {
                        ckey = (Integer) clean.get(c);
                        if (ckey == tkey) {
                            used = true;
                            break;
                        }
                    }

                    if (!used) {
                        clean.add(tkey); //geerally causes a resize of the clean AL
                    }
                }

                tm = db.createTableModel("invoice", clean, jTable1);
                jTable1.setModel(tm);

                this.setView();

                //this.setPayments();
                buttonGroup1.clearSelection();
                unpaidRadio.setSelected(false);
                paidRadio.setSelected(false);
                quoteRadio.setSelected(false);
                voidRadio.setSelected(false);

                tmptm = null;
                al = null; //help GC a little bit??
                found = true;
                searchResults = true;
                searchString = searchField.getText();
            }

        }

        if (!found) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Unable to find matching records.");
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
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        buttonGroup2 = new NonSelectedButtonGroup();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        searchField = new javax.swing.JTextField();
        actionToolbar = new javax.swing.JToolBar();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        payButton = new javax.swing.JButton();
        returnButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        voidButton = new javax.swing.JButton();
        statementButton = new javax.swing.JButton();
        historyButton = new javax.swing.JButton();
        statusToolbar = new javax.swing.JToolBar();
        unpaidRadio = new javax.swing.JToggleButton();
        paidRadio = new javax.swing.JToggleButton();
        quoteRadio = new javax.swing.JToggleButton();
        voidRadio = new javax.swing.JToggleButton();
        searchCombo = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        paymentTable = new javax.swing.JTable();
        deleteButton = new javax.swing.JButton();

        jLabel5.setText("Customer");

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("OCR B MT", 0, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Invoice Manager");
        setIconImage(winIcon);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Posted Invoices", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        jScrollPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTable1.setDefaultRenderer(java.lang.Float.class,  new FractionCellRenderer (10, 2, javax.swing.SwingConstants.RIGHT));

        jTable1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.white, null));
        jTable1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setToolTipText("Double-Click an Invoice to View or Print");
        jTable1.setDefaultRenderer(java.lang.Float.class,  new FractionCellRenderer (10, 2, javax.swing.SwingConstants.RIGHT));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        searchField.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        searchField.setToolTipText("Find an invoice IN THIS FILTER only.");
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

        actionToolbar.setFloatable(false);
        actionToolbar.setRollover(true);

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Barcode scanner1.png"))); // NOI18N
        newButton.setText("New");
        newButton.setToolTipText("Launch a New Invoice / Quote");
        newButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        newButton.setPreferredSize(new java.awt.Dimension(85, 61));
        newButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Eye.png"))); // NOI18N
        openButton.setText("View");
        openButton.setToolTipText("View & Print an Invoice");
        openButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        openButton.setPreferredSize(new java.awt.Dimension(85, 61));
        openButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(openButton);

        payButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Payment.png"))); // NOI18N
        payButton.setText("Payment");
        payButton.setToolTipText("Take a Payment, Record a Credit or Add Fees to the Selected Invoice");
        payButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        payButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        payButton.setPreferredSize(new java.awt.Dimension(85, 61));
        payButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        payButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(payButton);

        returnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Cycle.png"))); // NOI18N
        returnButton.setText("Returns");
        returnButton.setToolTipText("Process product returns from an invoice");
        returnButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        returnButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        returnButton.setPreferredSize(new java.awt.Dimension(85, 61));
        returnButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        returnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(returnButton);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Abort.png"))); // NOI18N
        closeButton.setText("Close");
        closeButton.setToolTipText("\"Write Off\" the Invoice");
        closeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 61));
        closeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(closeButton);

        voidButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Burn document.png"))); // NOI18N
        voidButton.setText("VOID");
        voidButton.setToolTipText("VOID - The only way to \"delete\" an invoice");
        voidButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        voidButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        voidButton.setPreferredSize(new java.awt.Dimension(80, 61));
        voidButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        voidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voidButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(voidButton);

        statementButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Period end.png"))); // NOI18N
        statementButton.setText("Statement");
        statementButton.setToolTipText("Statement for the Highlighted Invoice");
        statementButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        statementButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        statementButton.setPreferredSize(new java.awt.Dimension(96, 61));
        statementButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        statementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(statementButton);

        historyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Case history.png"))); // NOI18N
        historyButton.setText("History");
        historyButton.setToolTipText("Invoice History for the Customer on the Highlighted Invoice");
        historyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        historyButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        historyButton.setPreferredSize(new java.awt.Dimension(80, 61));
        historyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        historyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyButtonActionPerformed(evt);
            }
        });
        actionToolbar.add(historyButton);

        statusToolbar.setFloatable(false);
        statusToolbar.setRollover(true);

        buttonGroup2.add(unpaidRadio);
        unpaidRadio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Red message.png"))); // NOI18N
        unpaidRadio.setSelected(true);
        unpaidRadio.setText("UNPAID");
        unpaidRadio.setFocusable(false);
        unpaidRadio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        unpaidRadio.setPreferredSize(new java.awt.Dimension(75, 49));
        unpaidRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        unpaidRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unpaidRadioActionPerformed(evt);
            }
        });
        statusToolbar.add(unpaidRadio);

        buttonGroup2.add(paidRadio);
        paidRadio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Green message.png"))); // NOI18N
        paidRadio.setText("PAID");
        paidRadio.setFocusable(false);
        paidRadio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        paidRadio.setPreferredSize(new java.awt.Dimension(75, 49));
        paidRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        paidRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paidRadioActionPerformed(evt);
            }
        });
        statusToolbar.add(paidRadio);

        buttonGroup2.add(quoteRadio);
        quoteRadio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Handshake.png"))); // NOI18N
        quoteRadio.setText("Quotes");
        quoteRadio.setFocusable(false);
        quoteRadio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        quoteRadio.setPreferredSize(new java.awt.Dimension(75, 49));
        quoteRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        quoteRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quoteRadioActionPerformed(evt);
            }
        });
        statusToolbar.add(quoteRadio);

        buttonGroup2.add(voidRadio);
        voidRadio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Burn document 3d.png"))); // NOI18N
        voidRadio.setText("VOIDS");
        voidRadio.setFocusable(false);
        voidRadio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        voidRadio.setPreferredSize(new java.awt.Dimension(75, 49));
        voidRadio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        voidRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voidRadioActionPerformed(evt);
            }
        });
        statusToolbar.add(voidRadio);

        searchCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Invoice Number", "Invoice Item (Desc)" }));
        searchCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchComboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
                    .add(actionToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(statusToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 384, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(searchCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, searchField, 0, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, searchCombo))
                    .add(statusToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 273, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(actionToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Payment Activity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        paymentTable.setDefaultRenderer(java.lang.Float.class,  new FractionCellRenderer (10, 2, javax.swing.SwingConstants.RIGHT));

        paymentTable.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        paymentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "DATE", "TYPE", "REF", "AMOUNT", "BALANCE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        paymentTable.setToolTipText("Invoice Activity");
        paymentTable.setDefaultRenderer(java.lang.Float.class,  new FractionCellRenderer (10, 2, SwingConstants.RIGHT));
        jScrollPane3.setViewportView(paymentTable);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Deletes entries from Payment Activity.");
        deleteButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, deleteButton))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            openInvoice();
        }
        if (evt.getKeyCode() == evt.VK_ADD)
            takePayment();
    }//GEN-LAST:event_jTable1KeyPressed

    private void statementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementButtonActionPerformed

        if (!accessKey.checkInvoice(500)) {
            accessKey.showMessage("Statements");
            return;
        }
        if (jTable1.getSelectedRow() > -1 && paymentTable.getRowCount() > 0) {
            int k = (Integer) tm.getValueAt(jTable1.getSelectedRow(), 0);
            ReportFactory.generateStatements(application, k);
        }


    }//GEN-LAST:event_statementButtonActionPerformed

    private void returnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnButtonActionPerformed

        if (!accessKey.checkManager(500)) {
            accessKey.showMessage("Returns");
            return;
        }

        if (jTable1.getSelectedRow() > -1) {

            int the_row = jTable1.getSelectedRow();
            OldInvoice invoice = new OldInvoice(application, (Integer) tm.getValueAt(the_row, 0));
            new ReturnDialog(null, true, invoice, application);

        }

    }//GEN-LAST:event_returnButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed

        if (jTable1.getRowCount() < 1) {
            return;
        }

        if (!accessKey.checkManager(500)) {
            accessKey.showMessage("Close");
            return;
        }

        int row = -1;

        if (jTable1.getSelectedRow() > -1) {

            row = jTable1.getSelectedRow();
        }

        if (!quoteRadio.isSelected()) {
            closeInvoice(row);
        }


    }//GEN-LAST:event_closeButtonActionPerformed

    private void closeInvoice(int row) {

        if (jTable1.getSelectedRow() < 0) {
            return;  //geesh, this wastn here for several versions
        }
        if ((Boolean) jTable1.getModel().getValueAt(row, 8) || (Boolean) jTable1.getModel().getValueAt(row, 8)) {

            javax.swing.JOptionPane.showMessageDialog(null, "You cannot close an invoice which is already PAID or VOID.");
            return;

        }

        //get typed verification
        //pay out the invoice and record a refund against it
        //refund == total cost of all items minus the amount that has been paid already
        String iValue = JOptionPane.showInputDialog("To \"write off\" this invoice type CLOSE and click OK.");

        if (iValue != null && iValue.trim().equalsIgnoreCase("close")) {

            //get invoice number
            int invoice_key = (Integer) jTable1.getModel().getValueAt(row, 0);

            //System.out.println(invoice_key);
            invoice = new OldInvoice(application, invoice_key);

            invoice.setPaid(true);
            invoice.saveInvoice();

        }

        this.refreshTables();

    }

    private void deleteQuote(int key) {

        int a = javax.swing.JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this quote?", "Delete Quote", JOptionPane.YES_NO_OPTION);
        if (a != 0) {
            return;
        }

        Quote theQuote = new Quote(db, key);
        int tmpKey;

        // delete quote items
        DefaultTableModel items = theQuote.getItems();
        if (debug) {
            System.out.println("Trying to remove qitems: " + items.getRowCount());
        }
        for (int r = 0; r < items.getRowCount(); r++) {

            tmpKey = (Integer) items.getValueAt(r, 0);
            db.removeRecord("qitems", tmpKey);

        }
        //this.refreshTables();

        db.removeRecord("quote", key);

        // delete quote shipto
        tmpKey = theQuote.getShipToKey();
        if (tmpKey > 0) {
            db.removeRecord("qshipto", tmpKey);
        }

        JOptionPane.showMessageDialog(null, "Quote was deleted.");

        theQuote = null;
        this.refreshTables();
    }


    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

        deletePayment();

    }//GEN-LAST:event_deleteButtonActionPerformed

    private void deletePayment() {

        int row = paymentTable.getSelectedRow();

        if (row < 0) {
            return;
        }

        int paymentKey = (Integer) paymentTable.getModel().getValueAt(row, 0);
        String paymentType = (String) paymentTable.getModel().getValueAt(row, 3);

        if (paymentType.toLowerCase().equals("return")) {

            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "Deleting a payment entry generated by a product Return will NOT reverse the return." + nl
                    + "The products will still show as being returned on this invoice." + nl
                    + "This action is not recommended.  Do you still want to delete it?",
                    "(Return) Credit Delete", JOptionPane.YES_NO_OPTION);
            if (a == 0) {
            } else {
                return;
            }
        }

        if (paymentType.toLowerCase().equals("fee")) {

            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "The best way to reverse a fee is to issue a credit." + nl
                    + "Do you still want to delete it?", "Fee Debit Delete", JOptionPane.YES_NO_OPTION);
            if (a == 0) {
            } else {
                return;
            }
        }

        /* Fall-through action */
        String iValue = javax.swing.JOptionPane.showInputDialog("Type DELETE to continue.");
        if (iValue != null && iValue.equalsIgnoreCase("delete")) {
            db.removeRecord("payments", paymentKey);

        }

        int invRow = jTable1.getSelectedRow();
        int invKey = (Integer) jTable1.getModel().getValueAt(invRow, 0);

        /* Get an Invoice instance for this invoice and check balance */
 /* if the balance is over 0.00 mark unpaid, save and refresh tables */
        OldInvoice inv = new OldInvoice(application, invKey);

        float balance = inv.getInvoiceDueNow();

        if (balance > 0) {
            boolean prevPdStatus = inv.isPaid();
            inv.setPaid(false);
            inv.saveInvoice();
            this.refreshTables();
            if (prevPdStatus) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "The invoice now shows a balance due of " + DV.money(balance) + nl
                        + "The status of the invoice has been changed to unpaid.");
            }
            return;
        }

        if (balance < 0) {

            inv.setPaid(false);
            inv.saveInvoice();
            this.refreshTables();

            javax.swing.JOptionPane.showMessageDialog(null,
                    "The invoice now has a negative balance," + nl
                    + "showing that the customer has overpaid." + nl
                    + "Its status has been changed to unpaid so that" + nl
                    + " you can reconcile the invoice by issuing a refund.");

            return;
        }

        setPayments();

    }

    private void voidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voidButtonActionPerformed
        voidAction();
    }//GEN-LAST:event_voidButtonActionPerformed

    private void voidAction() {

        int row = jTable1.getSelectedRow();

        if (row > -1 && !voidRadio.isSelected()) {

            if (!accessKey.checkManager(500)) {
                accessKey.showMessage("Void");
                return;
            }

            if (quoteRadio.isSelected()) {

                deleteQuote((Integer) jTable1.getModel().getValueAt(row, 0));
                return;
            }

            //int a = JOptionPane.showConfirmDialog(this, "Sure you want to VOID the selected invoice?" + System.getProperty("line.separator") +"VOID is Permanent!","V O I D",  JOptionPane.YES_NO_OPTION);
            String iValue = JOptionPane.showInputDialog("To void this invoice type VOID and click OK.");

            if (iValue != null && iValue.equalsIgnoreCase("void")) {

                int r = jTable1.getSelectedRow();

                Object[] dataOut = db.getRecord("invoice", (Integer) tm.getValueAt(r, 0));

                dataOut[9] = new Boolean(true);  //VOID it!

                int savekey = db.saveRecord("invoice", dataOut, false);


                /* Blast sales and payments for this invoice */
                String inum = (String) dataOut[1];
                String invoice_key = Integer.toString((Integer) dataOut[0]);

                /* Kill invoice payments */
                ArrayList al = db.search("payments", 1, inum, false);

                if (al != null) {

                    for (int i = 0; i < al.size(); i++) {

                        db.removeRecord("payments", (Integer) al.get(i));
                    }
                }

                /* Kill invoice items */
                al = db.search("invitems", 1, invoice_key, false);
                Object[] rec;
                String desc;
                String type;
                float qty;

                ArrayList temp;
                if (al != null) {

                    for (int i = 0; i < al.size(); i++) {
                        rec = db.getRecord("invitems", (Integer) al.get(i));
                        desc = (String) rec[5];  //desc
                        type = (String) rec[4];  //code
                        qty = (Float) rec[3]; //qty

                        temp = db.search("inventory", 3, desc, false);

                        if (temp != null) {
                            rec = db.getRecord("inventory", (Integer) temp.get(0));

                            if (type.equalsIgnoreCase("RETURN")) {
                                rec[6] = (Float) rec[6] + (qty * -1);
                            } else {
                                rec[6] = (Float) rec[6] + qty;
                            }
                            db.removeRecord("invitems", (Integer) al.get(i));
                            db.saveRecord("inventory", rec, false);

                        }
                    }
                }
                this.refreshTables();

                if (savekey == -1) {
                    JOptionPane.showMessageDialog(null, "Problem accessing database, invoice was NOT voided.", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Invoice was VOIDED.");
                }

            } else {

                JOptionPane.showMessageDialog(null, "Invoice was NOT voided.");

            }
        }

    }

    private void voidRadioAction() {
        voidRadio.setSelected(true);
        resetSearch();
        refreshTables();
        searchField.requestFocus();

        //jLabel4.setText("Find Invoice #");
        voidButton.setText("VOID");

        // openButton.setEnabled(false); //view
        statementButton.setEnabled(false);
        returnButton.setEnabled(false);
        payButton.setEnabled(false);
        //refundButton.setEnabled(false);
        closeButton.setEnabled(false);
        voidButton.setEnabled(false);
        historyButton.setEnabled(false);
        this.selectFirstRow(jTable1);
    }

    private void unpaidRadioAction() {
        unpaidRadio.setSelected(true);
        resetSearch();
        refreshTables();
        searchField.requestFocus();
        //jLabel4.setText("Find Invoice #");
        voidButton.setText("VOID");

        openButton.setEnabled(true);
        statementButton.setEnabled(true);
        historyButton.setEnabled(true);
        returnButton.setEnabled(true);
        payButton.setEnabled(true);
        //refundButton.setEnabled(true);
        closeButton.setEnabled(true);
        voidButton.setEnabled(true);

        this.selectFirstRow(jTable1);
        //toggleArrows();

    }

    private void paidRadioAction() {
        paidRadio.setSelected(true);
        resetSearch();
        refreshTables();
        //jLabel4.setText("Find Invoice #");
        voidButton.setText("VOID");

        searchField.requestFocus();
        openButton.setEnabled(true);
        statementButton.setEnabled(true);
        historyButton.setEnabled(true);
        returnButton.setEnabled(true);
        payButton.setEnabled(false);
        //refundButton.setEnabled(true);
        closeButton.setEnabled(false);
        voidButton.setEnabled(true);

        this.selectFirstRow(jTable1);
    }

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased

        if (evt.getKeyCode() == evt.VK_CONTROL || evt.getKeyCode() == evt.VK_SHIFT) {
            return;
        }

        if (jTable1.getSelectedRow() < 0) {
            return;
        }

        Integer key = (Integer) jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 0);

        setPayments();

        if (paymentTable.getRowCount() < 1) {
            statementButton.setEnabled(false);
        } else {
            statementButton.setEnabled(true);
        }

        //jTextArea2.setText((String) tm.getValueAt(jTable1.getSelectedRow(), 3));

    }//GEN-LAST:event_jTable1KeyReleased

    private void searchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusGained

        searchField.selectAll();

    }//GEN-LAST:event_searchFieldFocusGained

    private void openInvoice() {

        if (voidRadio.isSelected()) {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Voided invoices contain no data.");
            return;
        }

        rememberRow();

        int r = jTable1.getSelectedRow();

        if (r > -1) {

            int k = (Integer) jTable1.getModel().getValueAt(r, 0);

            InvoiceApp id;

            if (quoteRadio.isSelected()) {
                /* Opening quotes, the key is used before the application to load quotes */
                //id = new InvoiceDialog (parentWin, true, k, application); //no select

            } else {

                if (!accessKey.checkInvoice(300)) {
                    accessKey.showMessage("Close");
                    return;
                }
                //id = new InvoiceDialog (parentWin, true, application, k); //no select

            }

            //id.setVisible(true);
            //stat = id.getStat();
            //id.dispose();
        }

        if (searchResults && searchCombo.getSelectedIndex() == 1) { //if set to customers and already searched
            searchField.setText(searchString);//restore our search with possible new data
            findInvoice();
            this.restoreRow();
            return;
        }
        this.refreshTables();
        this.restoreRow();

    }

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed

        openInvoice();


    }//GEN-LAST:event_openButtonActionPerformed

    private void searchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyPressed

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            findInvoice();
        }

    }//GEN-LAST:event_searchFieldKeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int mouseButton = evt.getButton();
        if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) {
            return;
        }
        if (evt.getClickCount() == 2) {

            openInvoice();

        }

        if (jTable1.getSelectedRow() > -1) {

            if (!voidRadio.isSelected()) {
                setPayments();
            }

            if (paymentTable.getRowCount() < 1) {

                statementButton.setEnabled(false);

            } else {
                statementButton.setEnabled(true);
            }

        }

    }//GEN-LAST:event_jTable1MouseClicked

    private void takePayment() {

        this.rememberRow();

        int r = jTable1.getSelectedRow();

        if (r > -1) {

            if ((Boolean) tm.getValueAt(r, 8) == true) {

                javax.swing.JOptionPane.showMessageDialog(this, "Invoice number " + (String) tm.getValueAt(r, 1) + " is marked as paid.");

            } else {

                int key = (Integer) tm.getValueAt(r, 0);

                PaymentDialog pd = new PaymentDialog(parentWin, true, key, application);
                pd.setVisible(true);
                refreshTables();
            }

        }
        this.restoreRow();
    }

    private void payButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payButtonActionPerformed

        takePayment();

    }//GEN-LAST:event_payButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        if (!accessKey.checkInvoice(300)) {
            accessKey.showMessage("Invoice/Quote");
            return;
        }

        InvoiceApp id = new InvoiceApp(parentWin, true, null, application); //no select
        id.setVisible(true);
        id.dispose();
        id = null;

        //refreshTables();  changed 11-25-2011
        unpaidRadioAction();

    }//GEN-LAST:event_newButtonActionPerformed

    private void historyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyButtonActionPerformed
        doHistoryReport();
    }//GEN-LAST:event_historyButtonActionPerformed

    private void searchComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchComboActionPerformed
        if (searchCombo.getSelectedIndex() == 0) {
            if (quoteRadio.isSelected()) {
                searchField.setText(qPrefix);
            } else {
                searchField.setText(iPrefix);
            }
            resetSearch();

        }
        searchField.requestFocus();

    }//GEN-LAST:event_searchComboActionPerformed

    private void quoteRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quoteRadioActionPerformed
        quoteRadioAction();        // TODO add your handling code here:
    }//GEN-LAST:event_quoteRadioActionPerformed

    private void unpaidRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unpaidRadioActionPerformed
        this.unpaidRadioAction();

    }//GEN-LAST:event_unpaidRadioActionPerformed

    private void paidRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paidRadioActionPerformed
        paidRadioAction();
    }//GEN-LAST:event_paidRadioActionPerformed

    private void voidRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voidRadioActionPerformed
        voidRadioAction();
    }//GEN-LAST:event_voidRadioActionPerformed

    private void doHistoryReport() {

        int r = jTable1.getSelectedRow();

        if (r < 0) {
            return;
        }

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }

        int k = (Integer) jTable1.getModel().getValueAt(r, 11);

        if (k > 0) {
            ReportFactory.generateCustomerStatement(application, new Contact());
        } else {
            String type = " invoice ";
            if (quoteRadio.isSelected()) {
                type = " quote ";
            }
            javax.swing.JOptionPane.showMessageDialog(null,
                    "This" + type + "is not assigned to a specific customer.");
        }

    }

    private void quoteRadioAction() {
        quoteRadio.setSelected(true);
        resetSearch();
        refreshTables();
        searchField.requestFocus();
        voidButton.setText("VOID");
        openButton.setEnabled(true);
        statementButton.setEnabled(false);
        historyButton.setEnabled(true);
        returnButton.setEnabled(false);
        payButton.setEnabled(false);
        closeButton.setEnabled(false);

        voidButton.setEnabled(true);

        this.selectFirstRow(jTable1);

    }

    public String getStat() {

        return stat;

    }

    private java.awt.Frame parentWin;
    private javax.swing.table.TableModel tm;
    private DbEngine db;
    private OldInvoice invoice;
    private int[] cols = new int[]{0, 3, 3, 3, 3, 6, 3, 3};
    private String stat = "";
    private String nl = System.getProperty("line.separator");
    private Settings props;
    private Image winIcon;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar actionToolbar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton historyButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton newButton;
    private javax.swing.JButton openButton;
    private javax.swing.JToggleButton paidRadio;
    private javax.swing.JButton payButton;
    private javax.swing.JTable paymentTable;
    private javax.swing.JToggleButton quoteRadio;
    private javax.swing.JButton returnButton;
    private javax.swing.JComboBox searchCombo;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton statementButton;
    private javax.swing.JToolBar statusToolbar;
    private javax.swing.JToggleButton unpaidRadio;
    private javax.swing.JButton voidButton;
    private javax.swing.JToggleButton voidRadio;
    // End of variables declaration//GEN-END:variables

}

final class NonSelectedButtonGroup extends ButtonGroup {

    @Override
    public void setSelected(ButtonModel model, boolean selected) {

        if (selected) {

            super.setSelected(model, selected);

        } else {

            clearSelection();
        }
    }
}
