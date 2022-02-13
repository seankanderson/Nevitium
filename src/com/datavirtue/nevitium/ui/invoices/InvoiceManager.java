/*
 * InvoiceManager.java
 *
 * Created on July 8, 2006, 8:25 AM
 ** Copyright (c) Data Virtue 2006, 2022
 */
package com.datavirtue.nevitium.ui.invoices;

import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceManagerTableModel;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.datavirtue.nevitium.models.invoices.PaymentActivityTableModel;
import com.datavirtue.nevitium.models.settings.AppSettings;
import com.datavirtue.nevitium.models.settings.LocalAppSettings;
import com.datavirtue.nevitium.services.AppSettingsService;
import com.datavirtue.nevitium.services.DiService;
import com.datavirtue.nevitium.services.ExceptionService;
import com.datavirtue.nevitium.services.InvoiceService;
import com.datavirtue.nevitium.services.LocalSettingsService;
import com.datavirtue.nevitium.services.util.CurrencyUtil;
import com.datavirtue.nevitium.ui.util.DateCellRenderer;
import com.datavirtue.nevitium.ui.util.DecimalCellRenderer;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.prefs.BackingStoreException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2022 All Rights Reserved.
 */
public class InvoiceManager extends javax.swing.JDialog {

    private boolean searchResults = false;
    private String searchString = "";
    private AppSettingsService appSettingsService;
    private InvoiceService invoiceService;
    private AppSettings appSettings;
    private LocalAppSettings localSettings;

    public InvoiceManager(java.awt.Frame parent, boolean modal) {

        super(parent, modal);
        this.parentWin = parent;
        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        initComponents();

        var injector = DiService.getInjector();
        appSettingsService = injector.getInstance(AppSettingsService.class);
        appSettingsService.setObjectType(AppSettings.class);
        invoiceService = injector.getInstance(InvoiceService.class);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    recordWindowSizeAndPosition();
                } catch (BackingStoreException ex) {
                    ExceptionService.showErrorDialog(e.getComponent(), ex, "Error saving local screen preferences");
                }
            }
        });

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

    }

    public void display() throws BackingStoreException, SQLException {

        this.localSettings = LocalSettingsService.getLocalAppSettings();
        this.appSettings = appSettingsService.getObject();

        restoreSavedWindowSizeAndPosition();

        String qname = appSettings.getInvoice().getQuoteName();

        if (qname != null) {
            quoteRadio.setText(qname);
        }

        refreshTables();

        this.selectFirstRow(invoiceTable);
        iPrefix = appSettings.getInvoice().getInvoicePrefix();
        qPrefix = appSettings.getInvoice().getQuotePrefix();
        searchField.setText(iPrefix);
        searchField.requestFocus();
        statusToolbar.setLayout(new FlowLayout());
        actionToolbar.setLayout(new FlowLayout());

        this.setVisible(true);
    }

    private void recordWindowSizeAndPosition() throws BackingStoreException {
        var screenSettings = localSettings.getScreenSettings();
        var sizeAndPosition = LocalSettingsService.getWindowSizeAndPosition(this);
        sizeAndPosition.setSplitFactor(this.invoiceManagerSplitPane.getDividerLocation());
        screenSettings.setInvoiceManager(sizeAndPosition);
        LocalSettingsService.saveLocalAppSettings(localSettings);
    }

    private void restoreSavedWindowSizeAndPosition() throws BackingStoreException {

        var screenSettings = localSettings.getScreenSettings().getInvoiceManager();
        LocalSettingsService.applyScreenSizeAndPosition(screenSettings, this);

        var splitfactor = Double.valueOf(screenSettings.getSplitFactor()).intValue();

        if (splitfactor > 100) {
            this.invoiceManagerSplitPane.setDividerLocation(splitfactor);
        } else {
            this.invoiceManagerSplitPane.setDividerLocation(300);
        }

    }

    private int rememberedRow = 0;
    private String iPrefix = "";
    private String qPrefix = "";

    private void rememberRow() {

        rememberedRow = invoiceTable.getSelectedRow();

    }

    private void restoreRow() {

        if (invoiceTable.getRowCount() > rememberedRow) {

            invoiceTable.changeSelection(rememberedRow, 0, false, false);
        }

    }

    private void selectFirstRow(javax.swing.JTable jt) {

        if (jt.getRowCount() > 0) {
            jt.changeSelection(0, 0, false, false);
            this.setPayments();
        }

    }

    private void customizeView() {
        if (invoiceTable.getRowCount() < 0) {
            return;
        }

        invoiceTable.getColumnModel().getColumn(0).setCellRenderer(new DateCellRenderer());
        invoiceTable.getColumnModel().getColumn(3).setCellRenderer(new DecimalCellRenderer(18, 2, SwingConstants.RIGHT));

        invoiceTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] widths = new int[]{45, 50, 270, 55};
        for (int i = 0; i < widths.length; i++) {
            invoiceTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        invoiceTable.setCellSelectionEnabled(false);
        invoiceTable.setRowSelectionAllowed(true);

        paymentTable.setCellSelectionEnabled(false);
        paymentTable.setRowSelectionAllowed(true);

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

        this.paymentTable.setModel(new PaymentActivityTableModel(null));

        this.cachedPayments = new HashMap();
        restoreSearch();
        if (searchResults) {
            return;
        }

        if (this.quoteRadio.isSelected()) {
            try {
                var invoices = invoiceService.getAllQuotes();
                var tableModel = new InvoiceManagerTableModel(invoices);
                this.invoiceTable.setModel(tableModel);

            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error getting invoices from database");
                return;
            }
        }

        if (this.unpaidRadio.isSelected()) {
            try {
                var invoices = invoiceService.getUnpaidInvoices();
                var tableModel = new InvoiceManagerTableModel(invoices);
                this.invoiceTable.setModel(tableModel);
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error getting invoices from database");
                return;
            }
        }

        if (this.paidRadio.isSelected()) {
            try {
                var invoices = invoiceService.getPaidInvoices();
                var tableModel = new InvoiceManagerTableModel(invoices);
                this.invoiceTable.setModel(tableModel);
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error getting invoices from database");
                return;
            }
        }

        if (this.voidRadio.isSelected()) {
            try {
                var invoices = invoiceService.getVoidInvoices();
                var tableModel = new InvoiceManagerTableModel(invoices);
                this.invoiceTable.setModel(tableModel);
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error getting invoices from database");
                return;
            }
        }
        customizeView();

    }

    private HashMap<UUID, List<InvoicePayment>> cachedPayments = new HashMap();

    private List<InvoicePayment> getPayments(Invoice invoice) throws SQLException {

        var payments = cachedPayments.get(invoice.getId());

        if (payments == null) {
            payments = invoiceService.getPaymentsForInvoice(invoice);
            this.cachedPayments.put(invoice.getId(), payments);
        } else {
            //System.out.println("cache hit!");
        }

        return payments;
    }

    private void setPayments() {

        var selectedRow = this.invoiceTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        var tableModel = (InvoiceManagerTableModel) this.invoiceTable.getModel();

        var invoice = tableModel.getValueAt(selectedRow);

        List<InvoicePayment> payments = null;

        try {
            payments = this.getPayments(invoice);
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error getting payments from database");
            return;
        }

        this.paymentTable.setModel(new PaymentActivityTableModel(payments));

        var columnModel = this.paymentTable.getColumnModel();

        this.paymentTable.removeColumn(columnModel.getColumn(1));
        this.paymentTable.removeColumn(columnModel.getColumn(5));

        columnModel.getColumn(0).setCellRenderer(new DateCellRenderer());
        columnModel.getColumn(3).setCellRenderer(new DecimalCellRenderer(18, 2, SwingConstants.RIGHT));
        columnModel.getColumn(4).setCellRenderer(new DecimalCellRenderer(18, 2, SwingConstants.RIGHT));
//        if (invoiceTable.getRowCount() > 0) {
//            //
//        }
//
//        if (invoiceTable.getSelectedRow() < 0) {
//            return;
//        }
//
//        if (quoteRadio.isSelected() || voidRadio.isSelected()) {
//            paymentTable.setModel(new DefaultTableModel());
//            return;
//
//        }
//
//        int invKey = (Integer) tm.getValueAt(invoiceTable.getSelectedRow(), 0);
//
//        OldInvoice inv = new OldInvoice(application, invKey);
//
//        //System.out.println("DEGUG: 11"); //DEBUG
//        paymentTable.setModel(inv.getPayments());
//        //System.out.println("DEGUG: 12"); //DEBUG
//
//        /* Nasty bug unless we skip col mods on no payments */
//        if (paymentTable.getRowCount() <= 0) {
//            return;
//        }
//
//        //remove cols 0 1          
//        TableColumnModel cm = paymentTable.getColumnModel();
//        TableColumn tc;
//
//        //setup hold table view
//        tc = cm.getColumn(0);
//        paymentTable.removeColumn(tc);//remove key column 
//        tc = cm.getColumn(0);
//        paymentTable.removeColumn(tc);//remove inv # column 
//
//        if (paymentTable.getRowCount() > 0) {
//
//            paymentTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
//            int[] widths = new int[]{85, 60, 110, 60, 60, 60};
//
//            for (int i = 0; i < widths.length; i++) {
//
//                tc = paymentTable.getColumnModel().getColumn(i);
//                tc.setPreferredWidth(widths[i]);
//            }
//        }
    }

    private void findInvoice() {

        boolean found = false;

        //int len = tm.getRowCount();

//        if (searchCombo.getSelectedIndex() == 0) {
//            for (int i = 0; i < len; i++) {
//                if (searchField.getText().equalsIgnoreCase((String) tm.getValueAt(i, 1))) {
//                    invoiceTable.changeSelection(i, 0, false, false);
//                    setPayments();
//                    found = true;
//                }
//            }
//            resetSearch();
//        }

        if (searchCombo.getSelectedIndex() == 2) { //search customers
            //search invoices for substring of customer
            //apply table model
            //setview - setpayments
            //clear table filters buttons (reset)

//            ArrayList al = db.search("invoice", 3, searchField.getText(), true);
//
//            if (al != null) {
//                tm = db.createTableModel("invoice", al, true);
//                invoiceTable.setModel(tm);
//
//                paymentTable.setModel(new DefaultTableModel());
//
//                this.customizeView();
//
//                //this.setPayments();
//                buttonGroup1.clearSelection();
//                unpaidRadio.setSelected(false);
//                paidRadio.setSelected(false);
//                quoteRadio.setSelected(false);
//                voidRadio.setSelected(false);
//
//                found = true;
//                searchResults = true;
//                searchString = searchField.getText();
//            }
        }

        if (searchCombo.getSelectedIndex() == 1) { //search invoice items
            //look for all items found to match search text
            //cycle thru and build arraylist of invoice keys without duplcates
            //get table model 
            //store search sttate
            ArrayList al = null; //db.search("invitems", 5, searchField.getText(), true); //search invitems description

            if (al != null) {
                ArrayList clean = new ArrayList();
                clean.trimToSize(); //going to be resizing a lot!!!! not optimal

//                DefaultTableModel tmptm = (DefaultTableModel) db.createTableModel("invitems", al, false);
//                int tkey, ckey;
//                boolean used = false;
//
//                for (int t = 0; t < tmptm.getRowCount(); t++) {
//                    used = false;
//                    tkey = (Integer) tmptm.getValueAt(t, 1);//get invoice key from invitem
//
//                    //cycle thru clean al and record new invoice key if it is not encountered
//                    for (int c = 0; c < clean.size(); c++) {
//                        ckey = (Integer) clean.get(c);
//                        if (ckey == tkey) {
//                            used = true;
//                            break;
//                        }
//                    }
//
//                    if (!used) {
//                        clean.add(tkey); //geerally causes a resize of the clean AL
//                    }
//                }
                // tm = db.createTableModel("invoice", clean, invoiceTable);
                //invoiceTable.setModel(tm);

                this.customizeView();

                //this.setPayments();
                buttonGroup1.clearSelection();
                unpaidRadio.setSelected(false);
                paidRadio.setSelected(false);
                quoteRadio.setSelected(false);
                voidRadio.setSelected(false);

                //tmptm = null;
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

    private void closeInvoice(int row) {

        if (invoiceTable.getSelectedRow() < 0) {
            return;
        }
        if ((Boolean) invoiceTable.getModel().getValueAt(row, 8) || (Boolean) invoiceTable.getModel().getValueAt(row, 8)) {

            javax.swing.JOptionPane.showMessageDialog(null, "You cannot close an invoice which is already PAID or VOID.");
            return;

        }

        //get typed verification
        //pay out the invoice and record a refund against it
        //refund == total cost of all items minus the amount that has been paid already
        String iValue = JOptionPane.showInputDialog("To \"write off\" this invoice type CLOSE and click OK.");

        if (iValue != null && iValue.trim().equalsIgnoreCase("close")) {

            //get invoice number
            int invoice_key = (Integer) invoiceTable.getModel().getValueAt(row, 0);

            //System.out.println(invoice_key);
//            invoice = new OldInvoice(application, invoice_key);
//
//            invoice.setPaid(true);
//            invoice.saveInvoice();
        }

        this.refreshTables();

    }

    private void deleteQuote(int key) {

        int a = javax.swing.JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this quote?", "Delete Quote", JOptionPane.YES_NO_OPTION);
        if (a != 0) {
            return;
        }

//        Quote theQuote = new Quote(db, key);
//        int tmpKey;
//
//        // delete quote items
//        DefaultTableModel items = theQuote.getItems();
//
//        for (int r = 0; r < items.getRowCount(); r++) {
//
//            tmpKey = (Integer) items.getValueAt(r, 0);
//            db.removeRecord("qitems", tmpKey);
//
//        }
//        //this.refreshTables();
//
//        db.removeRecord("quote", key);
//
//        // delete quote shipto
//        tmpKey = theQuote.getShipToKey();
//        if (tmpKey > 0) {
//            db.removeRecord("qshipto", tmpKey);
//        }
//
//        JOptionPane.showMessageDialog(null, "Quote was deleted.");
//
//        theQuote = null;
//        this.refreshTables();
    }

    private void deletePayment() throws SQLException {

        int selectedPaymentRow = paymentTable.getSelectedRow();

        if (selectedPaymentRow < 0) {
            return;
        }

        var paymentTableModel = (PaymentActivityTableModel) paymentTable.getModel();

        var payment = paymentTableModel.getValueAt(selectedPaymentRow);

        if (payment.getPaymentType().getName().equalsIgnoreCase("return")) {

            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "Deleting a payment entry generated by a product return will NOT reverse the return." + nl
                    + "The products will still show as being returned on this invoice." + nl
                    + "This action is not recommended.  Do you still want to delete it?",
                    "(Return) Credit Delete", JOptionPane.YES_NO_OPTION);
            if (a == 0) {
            } else {
                return;
            }
        }

        if (payment.getPaymentType().getName().equalsIgnoreCase("fee")) {

            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "The best way to reverse a fee is to issue a credit." + nl
                    + "Do you still want to delete it?", "Fee Debit Delete", JOptionPane.YES_NO_OPTION);
            if (a == 0) {
            } else {
                return;
            }
        }

        /* Fall-through action */
        var confirmation = javax.swing.JOptionPane.showInputDialog("Type DELETE to continue.");
        if (confirmation == null || !confirmation.equalsIgnoreCase("delete")) {
            return;
        }

        this.invoiceService.deletePayment(payment);

        var invoiceTableModel = (InvoiceManagerTableModel) invoiceTable.getModel();

        var selectedInvoiceRow = invoiceTable.getSelectedRow();

        var invoice = invoiceTableModel.getValueAt(selectedInvoiceRow);

        var balanceDue = invoiceService.calculateInvoiceAmountDue(invoice);

        if (balanceDue > 0) {
            boolean prevPdStatus = invoice.isPaid();
            invoice.setPaid(false);
            invoiceService.save(invoice);
            this.refreshTables();
            if (prevPdStatus) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "The invoice now shows a balance due of " + CurrencyUtil.money(balanceDue) + nl
                        + "The status of the invoice has been changed to unpaid.");
            }
            return;
        }

        if (balanceDue < 0) {

            invoice.setPaid(false);
            invoiceService.save(invoice);
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

    private void voidAction() throws SQLException {
        var selectedRow = invoiceTable.getSelectedRow();

        if (selectedRow < 0 || voidRadio.isSelected()) {
            return;
        }

//        if (row > -1 && !voidRadio.isSelected()) {
//
////            if (!accessKey.checkManager(500)) {
////                accessKey.showMessage("Void");
////                return;
////            }
//            if (quoteRadio.isSelected()) {
//
//                deleteQuote((Integer) invoiceTable.getModel().getValueAt(row, 0));
//                return;
//            }
//
        var proceed = JOptionPane.showConfirmDialog(this, "Sure you want to VOID the selected invoice?", "V O I D", JOptionPane.YES_NO_OPTION);

        if (proceed == JOptionPane.NO_OPTION || proceed == JOptionPane.CANCEL_OPTION || proceed == JOptionPane.CLOSED_OPTION) {
            return;
        }

        var confirmation = JOptionPane.showInputDialog("To void this invoice type VOID and click OK.");

        if (confirmation == null || !confirmation.equalsIgnoreCase("void")) {
            return;
        }

        var tableModel = (InvoiceManagerTableModel) invoiceTable.getModel();

        var invoice = tableModel.getValueAt(selectedRow);

        invoice.setVoided(true);

        invoiceService.save(invoice);
        refreshTables();
    }

    private void voidRadioAction() {
        voidRadio.setSelected(true);
        resetSearch();
        refreshTables();
        searchField.requestFocus();
        voidButton.setText("VOID");
        statementButton.setEnabled(false);
        returnButton.setEnabled(false);
        payButton.setEnabled(false);
        closeButton.setEnabled(false);
        voidButton.setEnabled(false);
        historyButton.setEnabled(false);
        this.selectFirstRow(invoiceTable);
    }

    private void unpaidRadioAction() {
        unpaidRadio.setSelected(true);
        resetSearch();
        refreshTables();
        searchField.requestFocus();
        voidButton.setText("VOID");
        openButton.setEnabled(true);
        statementButton.setEnabled(true);
        historyButton.setEnabled(true);
        returnButton.setEnabled(true);
        payButton.setEnabled(true);
        closeButton.setEnabled(true);
        voidButton.setEnabled(true);
        this.selectFirstRow(invoiceTable);    
    }

    private void paidRadioAction() {
        paidRadio.setSelected(true);
        resetSearch();
        refreshTables();
        voidButton.setText("VOID");
        searchField.requestFocus();
        openButton.setEnabled(true);
        statementButton.setEnabled(true);
        historyButton.setEnabled(true);
        returnButton.setEnabled(true);
        payButton.setEnabled(false);
        closeButton.setEnabled(false);
        voidButton.setEnabled(true);
        this.selectFirstRow(invoiceTable);
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
        this.selectFirstRow(invoiceTable);
    }

    
    private void openInvoice() {

        if (voidRadio.isSelected()) {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Voided invoices contain no data.");
            return;
        }

        rememberRow();

        int selectedRow = invoiceTable.getSelectedRow();

        if (selectedRow > -1) {

            var tableModel = (InvoiceManagerTableModel) this.invoiceTable.getModel();

            var invoice = tableModel.getValueAt(selectedRow);

            var invoiceApp = new InvoiceApp(this.parentWin, true);
            invoiceApp.setInvoice(invoice);
            invoiceApp.display();
            invoiceApp.dispose();
//            if (quoteRadio.isSelected()) {
//                /* Opening quotes, the key is used before the application to load quotes */
//                //id = new InvoiceDialog (parentWin, true, k, application); //no select
//
//            } else {
//
////                if (!accessKey.checkInvoice(300)) {
////                    accessKey.showMessage("Close");
////                    return;
////                }
//                //id = new InvoiceDialog (parentWin, true, application, k); //no select
//
//            }

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

        private void takePayment() {

        this.rememberRow();

        int selectedRow = invoiceTable.getSelectedRow();

        if (selectedRow < 0) {
            return; // TODO: complain via dialog?
        }

        var tableModel = (InvoiceManagerTableModel) this.invoiceTable.getModel();
        var invoice = tableModel.getValueAt(selectedRow);

        if (invoice.isPaid()) {

            JOptionPane.showMessageDialog(this, "Invoice number " + invoice.getInvoiceNumber() + " is marked as paid.");

        } else {
            var paymentDialog = new PaymentDialog(parentWin, true, invoice);

            try {
                paymentDialog.display();
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error accessing invoice payments in database");
            }

            refreshTables();
        }

        this.restoreRow();
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
        invoiceManagerSplitPane = new javax.swing.JSplitPane();
        paymentActivityPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        paymentTable = new javax.swing.JTable();
        deleteButton = new javax.swing.JButton();
        invoicePanel = new javax.swing.JPanel();
        invoiceTableScrollPane = new javax.swing.JScrollPane();
        invoiceTable = new javax.swing.JTable();
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

        invoiceManagerSplitPane.setDividerLocation(400);
        invoiceManagerSplitPane.setDividerSize(10);
        invoiceManagerSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        invoiceManagerSplitPane.setResizeWeight(0.8);

        paymentActivityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Payment Activity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        paymentTable.setToolTipText("Invoice Activity");
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

        org.jdesktop.layout.GroupLayout paymentActivityPanelLayout = new org.jdesktop.layout.GroupLayout(paymentActivityPanel);
        paymentActivityPanel.setLayout(paymentActivityPanelLayout);
        paymentActivityPanelLayout.setHorizontalGroup(
            paymentActivityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, paymentActivityPanelLayout.createSequentialGroup()
                .add(paymentActivityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                    .add(paymentActivityPanelLayout.createSequentialGroup()
                        .addContainerGap(688, Short.MAX_VALUE)
                        .add(deleteButton)))
                .addContainerGap())
        );
        paymentActivityPanelLayout.setVerticalGroup(
            paymentActivityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(paymentActivityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteButton)
                .addContainerGap())
        );

        invoiceManagerSplitPane.setBottomComponent(paymentActivityPanel);

        invoicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Posted Invoices", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        invoiceTableScrollPane.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        invoiceTableScrollPane.setViewportView(invoiceTable);

        invoiceTable.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.white, null));
        invoiceTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        invoiceTable.setToolTipText("Double-Click an Invoice to View or Print");
        invoiceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invoiceTableMouseClicked(evt);
            }
        });
        invoiceTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                invoiceTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                invoiceTableKeyReleased(evt);
            }
        });
        invoiceTableScrollPane.setViewportView(invoiceTable);

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

        org.jdesktop.layout.GroupLayout invoicePanelLayout = new org.jdesktop.layout.GroupLayout(invoicePanel);
        invoicePanel.setLayout(invoicePanelLayout);
        invoicePanelLayout.setHorizontalGroup(
            invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, invoicePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(actionToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, invoiceTableScrollPane)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, invoicePanelLayout.createSequentialGroup()
                        .add(statusToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 384, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(searchCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchField)))
                .addContainerGap())
        );
        invoicePanelLayout.setVerticalGroup(
            invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(invoicePanelLayout.createSequentialGroup()
                .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(invoicePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, searchField, 0, 0, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, searchCombo))
                    .add(statusToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(invoiceTableScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(actionToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        invoiceManagerSplitPane.setLeftComponent(invoicePanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(invoiceManagerSplitPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(invoiceManagerSplitPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void invoiceTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invoiceTableKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            openInvoice();
        }
        if (evt.getKeyCode() == evt.VK_ADD)
            takePayment();
    }//GEN-LAST:event_invoiceTableKeyPressed

    private void statementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementButtonActionPerformed

//        if (!accessKey.checkInvoice(500)) {
//            accessKey.showMessage("Statements");
//            return;
//        }
        if (invoiceTable.getSelectedRow() > -1 && paymentTable.getRowCount() > 0) {
            //int k = (Integer) tm.getValueAt(invoiceTable.getSelectedRow(), 0);
            //ReportFactory.generateStatements(application, k);
        }


    }//GEN-LAST:event_statementButtonActionPerformed

    private void returnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnButtonActionPerformed

//        if (!accessKey.checkManager(500)) {
//            accessKey.showMessage("Returns");
//            return;
//        }
        if (invoiceTable.getSelectedRow() > -1) {

            int selectedRow = invoiceTable.getSelectedRow();
            var tableModel = (InvoiceManagerTableModel) this.invoiceTable.getModel();
            var invoice = tableModel.getValueAt(selectedRow);
            var returnDialog = new ReturnDialog(null, true, invoice);
            try {
                returnDialog.display();
            } catch (SQLException ex) {
                ExceptionService.showErrorDialog(this, ex, "Error getting returns from database");
            }
            
            if (returnDialog.isRefreshNeeded()) {
                this.refreshTables();
            }
            
            
        }

    }//GEN-LAST:event_returnButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed

        if (invoiceTable.getRowCount() < 1) {
            return;
        }

//        if (!accessKey.checkManager(500)) {
//            accessKey.showMessage("Close");
//            return;
//        }
        int row = -1;

        if (invoiceTable.getSelectedRow() > -1) {

            row = invoiceTable.getSelectedRow();
        }

        if (!quoteRadio.isSelected()) {
            closeInvoice(row);
        }


    }//GEN-LAST:event_closeButtonActionPerformed


    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

        try {
            deletePayment();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error deleting payment from database");
        }

    }//GEN-LAST:event_deleteButtonActionPerformed


    private void voidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voidButtonActionPerformed
        try {
            voidAction();
        } catch (SQLException ex) {
            ExceptionService.showErrorDialog(this, ex, "Error voiding invoice in database");
        }
    }//GEN-LAST:event_voidButtonActionPerformed


    private void invoiceTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invoiceTableKeyReleased

        if (evt.getKeyCode() == evt.VK_CONTROL || evt.getKeyCode() == evt.VK_SHIFT) {
            return;
        }

        if (invoiceTable.getSelectedRow() < 0) {
            return;
        }

        setPayments();

        if (paymentTable.getRowCount() < 1) {
            statementButton.setEnabled(false);
        } else {
            statementButton.setEnabled(true);
        }

        //jTextArea2.setText((String) tm.getValueAt(jTable1.getSelectedRow(), 3));

    }//GEN-LAST:event_invoiceTableKeyReleased

    private void searchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchFieldFocusGained

        searchField.selectAll();

    }//GEN-LAST:event_searchFieldFocusGained


    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed

        openInvoice();


    }//GEN-LAST:event_openButtonActionPerformed

    private void searchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyPressed

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            findInvoice();
        }

    }//GEN-LAST:event_searchFieldKeyPressed

    private void invoiceTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invoiceTableMouseClicked
        int mouseButton = evt.getButton();
        if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) {
            return;
        }
        if (evt.getClickCount() == 2) {

            openInvoice();

        }

        if (invoiceTable.getSelectedRow() > -1) {

            if (!voidRadio.isSelected()) {
                setPayments();
            }

            if (paymentTable.getRowCount() < 1) {

                statementButton.setEnabled(false);

            } else {
                statementButton.setEnabled(true);
            }

        }

    }//GEN-LAST:event_invoiceTableMouseClicked



    private void payButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payButtonActionPerformed

        takePayment();

    }//GEN-LAST:event_payButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
//        if (!accessKey.checkInvoice(300)) {
//            accessKey.showMessage("Invoice/Quote");
//            return;
//        }

        var invoiceApp = new InvoiceApp(parentWin, true);

        invoiceApp.display();

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
        quoteRadioAction();
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

        int r = invoiceTable.getSelectedRow();

        if (r < 0) {
            return;
        }

//        if (!accessKey.checkReports(500)) {
//            accessKey.showMessage("Customer/Supplier Reports");
//            return;
//        }
        int k = (Integer) invoiceTable.getModel().getValueAt(r, 11);

        if (k > 0) {
            // ReportFactory.generateCustomerStatement(application, new Contact());
        } else {
            String type = " invoice ";
            if (quoteRadio.isSelected()) {
                type = " quote ";
            }
            javax.swing.JOptionPane.showMessageDialog(null,
                    "This" + type + "is not assigned to a specific customer.");
        }

    }


    private java.awt.Frame parentWin;
    
    private String nl = System.getProperty("line.separator");

    private Image winIcon;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar actionToolbar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton historyButton;
    private javax.swing.JSplitPane invoiceManagerSplitPane;
    private javax.swing.JPanel invoicePanel;
    private javax.swing.JTable invoiceTable;
    private javax.swing.JScrollPane invoiceTableScrollPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton newButton;
    private javax.swing.JButton openButton;
    private javax.swing.JToggleButton paidRadio;
    private javax.swing.JButton payButton;
    private javax.swing.JPanel paymentActivityPanel;
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
