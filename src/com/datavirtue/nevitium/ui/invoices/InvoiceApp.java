/*
 * invDialog.java
 *
 * Created on June 25, 2006, 11:16 AM
 **
 */
package com.datavirtue.nevitium.ui.invoices;

import com.datavirtue.nevitium.services.PosPrinterService;
import RuntimeManagement.KeyCard;
import RuntimeManagement.GlobalApplicationDaemon;
import com.datavirtue.nevitium.ui.util.Tools;
import com.datavirtue.nevitium.ui.util.LimitedDocument;
import com.datavirtue.nevitium.database.reports.ReportModel;
import com.datavirtue.nevitium.database.reports.ReportFactory;
import com.datavirtue.nevitium.database.reports.ReportDialog;

import com.datavirtue.nevitium.ui.util.JTextFieldFilter;
import com.datavirtue.nevitium.ui.contacts.ContactsApp;
import com.datavirtue.nevitium.ui.contacts.ContactShippingDialog;
import com.datavirtue.nevitium.ui.util.NewEmail;
import com.datavirtue.nevitium.models.invoices.old.OldInvoice;
import com.datavirtue.nevitium.models.invoices.old.InvoiceModel;
import com.datavirtue.nevitium.ui.VATCalculator;
import com.datavirtue.nevitium.ui.inventory.MyInventoryApp;
import com.datavirtue.nevitium.ui.layoutdesigner.DocumentElementTable;
import com.datavirtue.nevitium.ui.layoutdesigner.DocumentLayout;
import com.datavirtue.nevitium.ui.layoutdesigner.InvoicePrintPanel;
import com.datavirtue.nevitium.ui.layoutdesigner.PrintPreview;
import com.datavirtue.nevitium.models.invoices.old.PDFInvoice;
import com.datavirtue.nevitium.models.invoices.old.PaymentActivityDialog;
import com.datavirtue.nevitium.models.invoices.old.Quote;

import datavirtue.*;
import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import javax.swing.JTextField;
import javax.swing.table.*;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.ArrayList;
import java.text.DateFormat;
import java.util.Date;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.event.*;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.AbstractAction;
import com.lowagie.text.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.util.Calendar;
import java.util.GregorianCalendar;
import com.datavirtue.nevitium.models.contacts.Contact;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.services.AppSettingsService;
import com.datavirtue.nevitium.services.ContactService;
import com.datavirtue.nevitium.services.InvoiceItemService;
import com.datavirtue.nevitium.services.InvoiceService;

/**
 *
 * @author Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2008, 2009 All Rights Reserved.
 */
public class InvoiceApp extends javax.swing.JDialog {

    private KeyCard accessKey;
    private boolean debug = false;
    private GlobalApplicationDaemon application;
    private boolean addCategoryInfo = false;
    private Invoice currentInvoice;
    private Contact customer;
    private AppSettingsService settingsService;
    private InvoiceService invoiceService;
    private InvoiceItemService itemService;
    private ContactService contactService;

    public InvoiceApp(java.awt.Frame parent, boolean modal, com.datavirtue.nevitium.models.invoices.Invoice quote, GlobalApplicationDaemon application) {

        super(parent, modal);
        accessKey = application.getKey_card();
        workingPath = application.getWorkingPath();
        db = application.getDb();
        this.application = application;
        setVAT();
        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));

        initComponents();
        optionsToolbar.setLayout(new FlowLayout());

        qtyTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        toolBar.add(paymentButton);
        toolBar.add(statementButton);
        toolBar.add(historyButton);

        /*statementButton.setVisible(false);
        paymentButton.setVisible(false);
        historyButton.setVisible(false);*/
        toolBar.setVisible(false);
        printReceiptButton.setVisible(false);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {

                recordScreenPosition();

            }
        });
        /* Close dialog on escape */
        javax.swing.ActionMap am = getRootPane().getActionMap();
        javax.swing.InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object windowCloseKey = new Object();
        KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action windowCloseAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                int a = javax.swing.JOptionPane.showConfirmDialog(null, "Discard this invoice?", "Exit", JOptionPane.YES_NO_OPTION);

                if (a == 0) {
                    setVisible(false);
                    dispose();
                }
                return;  //no exit
            }
        };

        imap.put(windowCloseStroke, windowCloseKey);
        am.put(windowCloseKey, windowCloseAction);
        /**/

        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, 1);

        invoice = new OldInvoice(application);  //blank invoice object
        invoice.setVAT(VAT);
        invoice.setTaxes(taxRate1, taxRate2);

        invoice.setInvoiceDialogModel((DefaultTableModel) invTable.getModel());
        theQuote = new Quote(db);//blank quote object
        theQuote.setItemModel((DefaultTableModel) invTable.getModel());

        parentWin = parent;
        tm = invoice.getItems();  //assign the netbeans tablemodel to the 'global tm'

        //create a property file instance
        props = application.getProps();

        addCategoryInfo = Tools.getStringBool(props.getProp("CATLINE"));
        custButton.setText(props.getProp("BILLTO"));

        ignore_qty = DV.parseBool(props.getProp("IGNOREQTY"), false);

        init();
        //voidBox.setVisible(false);

        paymentCheckBox.setSelected(Boolean.parseBoolean(props.getProp("PROCESSPAYMENT")));

        invoicePrefix = props.getProp("INVOICE PREFIX");
        quotePrefix = props.getProp("QUOTE PREFIX");

        if (quote == null) {
            //Set Tax rates & date for New Invoice/Quote
            invoice.setCustomer("S A L E");

            setDefaultMessage();

            custTextArea.setText(invoice.getCustomer());
            convertButton.setVisible(false);
            printButton.setVisible(false);
            quote.setQuote(true);
            savedQuote = false;
            setInvoiceNumber();

        } else {
            //load saved quote
            getQuote(quoteKey);
            //quote = true;
            savedQuote = true;
            invoiceNumberEditCheckBox.setEnabled(false);
            convertButton.setVisible(true);
            postButton.setEnabled(false);
            //printButton.setVisible(false);
            closeButton.setText("CLOSE");
        }

        String c = props.getProp("SCAN FIELD");
        if (c.equalsIgnoreCase("UPC")) {
            upcCombo.setSelectedItem("UPC");
        }
        if (c.equalsIgnoreCase("CODE")) {
            upcCombo.setSelectedItem("Code");
        }
        if (c.equalsIgnoreCase("DESC")) {
            upcCombo.setSelectedItem("Desc");
        }

        //Version 1.5
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        datePicker1.setDateFormat(df);

        getTaxRates();
        invoice.setTaxes(taxRate1, taxRate2);
        theQuote.setTaxes(taxRate1, taxRate2);
        computePrices();
        setView();

        clearFields();

        /* Quote mode is the default until the user posts the invoice,
         db loaded invoices should not be set to quote mode,
         this mode is triggered by a diff constructor though*/

 /* This prints the invoice as a quote without creating a quote! */
        //quote = true;
        //paidCheckBox.setVisible(false);
        viewReturnsButton.setVisible(false);

        if (this.checkForScreenSettings()) {
            this.restoreScreenPosition();//restore saved screen size
        } else {
            java.awt.Dimension d = DV.computeCenter((java.awt.Window) this);
            this.setLocation(d.width, d.height);
        }

    }

    /* This constructor is for viewing an invoice <---ONLY!! */
    public InvoiceApp(java.awt.Frame parent, boolean modal, GlobalApplicationDaemon application, Invoice invoice) {

        super(parent, modal);

        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));

        initComponents();
        optionsToolbar.setLayout(new FlowLayout());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {

                recordScreenPosition();

            }
        });

        accessKey = application.getKey_card();
        toolBar.add(paymentButton);
        toolBar.add(statementButton);
        toolBar.add(historyButton);
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, 1);
        qtyTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        currentInvoice = invoice == null ? new com.datavirtue.nevitium.models.invoices.Invoice() : invoice;

        if (currentInvoice.getId() == null) {

            /* Close dialog on escape */
            javax.swing.ActionMap am = getRootPane().getActionMap();
            javax.swing.InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            Object windowCloseKey = new Object();
            KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            Action windowCloseAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {

                    int a = javax.swing.JOptionPane.showConfirmDialog(null, "Discard this invoice?", "Exit", JOptionPane.YES_NO_OPTION);

                    if (a == 0) {

                        setVisible(false);
                        dispose();

                    }
                    return;  //no exit

                }
            };
            imap.put(windowCloseStroke, windowCloseKey);
            am.put(windowCloseKey, windowCloseAction);
            /**/

        } else {

            javax.swing.ActionMap am = getRootPane().getActionMap();
            javax.swing.InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            Object windowCloseKey = new Object();
            KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            Action windowCloseAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
            };
            imap.put(windowCloseStroke, windowCloseKey);
            am.put(windowCloseKey, windowCloseAction);

            autoInvoiceNumberButton.setEnabled(false);
            invoiceNumberEditCheckBox.setEnabled(false);
            datePicker1.setEnabled(false);
            receiptCheckBox.setEnabled(false);
        }
        workingPath = application.getWorkingPath();
        db = application.getDb();
        this.application = application;
        setVAT();
        parentWin = parent;
        props = new Settings(workingPath + "settings.ini");
        init();

        //Version 1.5
        //getInvoice(invoice_key); //Get invoice to view
        populateInvoice();
        //invoice .setItemModel((DefaultTableModel)invTable.getModel());
        viewPrint = true;

        saveButton.setEnabled(false);
        postButton.setEnabled(false);
        upcCombo.setEnabled(false);
        upcField.setEnabled(false);
        miscButton.setVisible(false);

        calcButton.setVisible(false);
        removeButton.setVisible(false);

        custButton.setVisible(false);

        discountButton.setVisible(false);
        //shippingButton.setVisible(false);
        //packingslipButton.setVisible(false);
        messageButton.setVisible(false);
        paymentCheckBox.setVisible(false);

        custTextArea.setEditable(false);
        shipToTextArea.setEditable(false);

        shipToButton.setVisible(false);
        clearShipToButton.setVisible(false);
        copyBillToButton.setVisible(false);
        convertButton.setVisible(false);

        qtyTextField.setEditable(false);

        //jTextArea1.setEditable (false);
        messageButton.setEnabled(false);

        closeButton.setText("CLOSE");

        // TODO: Get returns for invoice
        
//        if (invoice.getReturnCount() > 0) {
//            viewReturnsButton.setVisible(true);
//        } else {
//            viewReturnsButton.setVisible(false);
//        }

            
        if (this.checkForScreenSettings()) {
            this.restoreScreenPosition();//restore saved screen size
        } else {
            java.awt.Dimension d = DV.computeCenter((java.awt.Window) this);
            this.setLocation(d.width, d.height);
        }

    }

    private void setVAT() {

        boolean vat = DV.parseBool(application.getProps().getProp("VAT"), false);
        VAT = vat;
    }

    private void init() {

        invTable.getColumn(invTable.getColumnName(6)).setHeaderValue(props.getProp("TAX1NAME"));
        invTable.getColumn(invTable.getColumnName(7)).setHeaderValue(props.getProp("TAX2NAME"));
        t1Label.setText(props.getProp("TAX1NAME"));
        t2Label.setText(props.getProp("TAX2NAME"));

        pos = Boolean.parseBoolean(props.getProp("POS"));

        if (pos) {
            receiptCheckBox.setSelected(true);
            paymentCheckBox.setSelected(true);
            receiptCheckBox.setEnabled(false);
            paymentCheckBox.setEnabled(false);
            jPanel2.setVisible(false);
            packingslipButton.setVisible(false);
            shippingButton.setVisible(false);
            printButton.setVisible(false);
            saveButton.setVisible(false);
            viewReturnsButton.setVisible(false);
        }

        /* Set currency field alignment justification */
        itemTotalField.setHorizontalAlignment(JTextField.RIGHT);
        t1Field.setHorizontalAlignment(JTextField.RIGHT);
        t2Field.setHorizontalAlignment(JTextField.RIGHT);
        grandTotalField.setHorizontalAlignment(JTextField.RIGHT);

        if (!DV.parseBool(props.getProp("SHOW TAX 2"), true)) {
            t2Field.setVisible(false);
            t2Label.setVisible(false);
        }

        this.VATButton.setVisible(DV.parseBool(application.getProps().getProp("VAT"), true));

    }

    private void recordScreenPosition() {

        Point p = this.getLocationOnScreen();
        Dimension d = this.getSize();

        props.setProp("INVPOS", p.x + "," + p.y);
        props.setProp("INVSIZE", d.width + "," + d.height);

    }

    private Point defaultScreenPosition;
    private Dimension defaultWindowSize;

    private boolean checkForScreenSettings() {
        String pt = props.getProp("INVPOS");
        String dim = props.getProp("INVSIZE");
        if (pt.equals("")) {
            return false;
        }
        if (dim.equals("")) {
            return false;
        }
        if ((Tools.parsePoint(pt)) == null) {
            return false;
        }
        if ((Tools.parseDimension(dim)) == null) {
            return false;
        }
        return true;

    }

    private void storeDefaultScreen() {

        try {
            defaultScreenPosition = this.getLocationOnScreen();
        } catch (Exception e) {
            defaultScreenPosition = new Point(0, 0);
        }

        defaultWindowSize = this.getSize();

    }

    private void restoreDefaultScreenSize() {

        this.setSize(this.defaultWindowSize);
    }

    private void restorDefaultScreen() {
        restoreDefaultScreenSize();
        restoreDefaultScreenLocation();
    }

    private void restoreDefaultScreenLocation() {
        this.setLocation(this.defaultScreenPosition);
    }

    private void restoreScreenPosition() {

        String pt = props.getProp("INVPOS");
        String dim = props.getProp("INVSIZE");
        //System.out.println("Point "+pt+"  Dim"+dim);
        Point p = Tools.parsePoint(pt);
        if (p == null) {
            p = this.defaultScreenPosition;
        }
        if (p == null) {
            p = new Point(0, 0);
        }
        this.setLocation(p);
        Dimension d = Tools.parseDimension(dim);
        if (d == null) {
            d = this.defaultWindowSize;
        }
        this.setSize(d);

    }

    private void setDefaultMessage() {
        //get default name
        //search for mesage
        //if found set, otherwise leave program default in place.

        String msg = props.getProp("DEFAULT MSG");

        if (msg != null && msg.length() > 0) {
            ArrayList al = db.search("messages", 1, msg, false);
            if (al != null) {
                Object[] m = db.getRecord("messages", ((Integer) al.get(0)));
                invoiceMessage = (String) m[2];

            } else {

            }

        }

        messageButton.setToolTipText(invoiceMessage);
    }

    private void getInvoice(int key) {

        quote = false;
        //printButton.setText("Print to PDF");

        invoice = new OldInvoice(application, key);
        invoice.setVAT(VAT);
        /* Removed 06-02-2011 */
        //invoice.setTaxes(taxRate1, taxRate2);

        if (!invoice.isPopulated()) {

            javax.swing.JOptionPane.showMessageDialog(null, "Invoice was not found.");
            invoice = null;
            this.dispose();

        }

        //get items sold
        TableModel items = invoice.getItems();
        //when a blank invoice is voided this tm is null

        /* This table layout was setup in netbeans so we must insert the items into
         it instead of just passing a tablemodel */
        tm = (DefaultTableModel) invTable.getModel();

        tm.setRowCount(items.getRowCount());

        int rows = tm.getRowCount();
        int cols = tm.getColumnCount();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tm.setValueAt(items.getValueAt(row, col), row, col);  //skip key?
            }
        }
        invTable.setModel(tm);

        //add the other data detail
        numberField.setText(invoice.getInvoiceNumber());
        try {

            datePicker1.setDate(new Date(invoice.getDate()));  //Version 1.5

        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        custTextArea.setText(invoice.getCustomer());

        String sta = invoice.getShipToAddress();
        if (!sta.equals("")) {
            shipToTextArea.setText(sta);
        }

        /*
         *  Find any 'Ship To' data and plug into interface.
         */
        invoiceMessage = invoice.getMessage();

        taxRate1 = invoice.getTax1Rate();
        taxRate2 = invoice.getTax2Rate();
        //voidBox.setSelected(invoice.isVoid());

        cust_key = invoice.getCustKey();

        //paidCheckBox.setSelected(invoice.isPaid());
        setView();
        computePrices();

    }

    private void populateInvoice() {
        quote = false;
        //printButton.setText("Print to PDF");

        TableModel items = invoice.getItems();

        tm = (DefaultTableModel) invTable.getModel();

        tm.setRowCount(items.getRowCount());

        int rows = tm.getRowCount();
        int cols = tm.getColumnCount();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tm.setValueAt(items.getValueAt(row, col), row, col);  //skip key?
            }
        }
        invTable.setModel(tm);

        //add the other data detail
        numberField.setText(invoice.getInvoiceNumber());
        try {

            datePicker1.setDate(new Date(invoice.getDate()));  //Version 1.5

        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        custTextArea.setText(invoice.getCustomer());

        String sta = invoice.getShipToAddress();
        if (!sta.equals("")) {
            shipToTextArea.setText(sta);
        }

        /*
         *  Find any 'Ship To' data and plug into interface.
         */
        invoiceMessage = invoice.getMessage();

        taxRate1 = invoice.getTax1Rate();
        taxRate2 = invoice.getTax2Rate();
        //voidBox.setSelected(invoice.isVoid());

        cust_key = invoice.getCustKey();

        //paidCheckBox.setSelected(invoice.isPaid());
        setView();
        computePrices();

    }

    private void getQuote(int key) {

        quote = true;

        theQuote = new Quote(db, key);

        if (!theQuote.isPopulated()) {

            javax.swing.JOptionPane.showMessageDialog(null, "Quote was not found.");
            theQuote = null;
            this.dispose();

        }

        //get items
        TableModel items = theQuote.getItems();
        //when a blank quote is voided this tm is null ?*?

        /* This table layout was setup in netbeans so we must insert the items into
         it instead of just passing a tablemodel */

 /* Set the global items tm to the invTable model */
        tm = (DefaultTableModel) invTable.getModel();

        tm.setRowCount(items.getRowCount());
        if (debug) {
            System.out.println("qitems row count in id getQuote() :" + items.getRowCount());
        }

        //TODO:WORKING
        int rows = tm.getRowCount();
        int cols = tm.getColumnCount();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tm.setValueAt(items.getValueAt(row, col), row, col);  //skip key?
            }
        }
        //invTable.setModel(tm);
        theQuote.setItemModel((DefaultTableModel) invTable.getModel());

        //add the other data detail
        numberField.setText(theQuote.getQuoteNumber());

        try {

            datePicker1.setDate(new Date(theQuote.getDate()));  //Version 1.5

        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        custTextArea.setText(theQuote.getCustomer());
        custTextArea.setEditable(false);

        String sta = theQuote.getShipToAddress();
        if (!sta.equals("")) {
            shipToTextArea.setText(sta);
        }
        /*
         *  Find any 'Ship To' data and plug into interface.
         */

        invoiceMessage = theQuote.getMessage();
        messageButton.setToolTipText(invoiceMessage);

        taxRate1 = theQuote.getTax1Rate();
        taxRate2 = theQuote.getTax2Rate();
        //voidBox.setSelected(theQuote.isVoid());

        cust_key = theQuote.getCustKey();

        //paidCheckBox.setSelected(false);
        setView();
        computePrices();

    }

    private void getTaxRates() {
        try {

            taxRate1 = Float.parseFloat(props.getProp("TAX1"));

        } catch (NumberFormatException ex) {

            taxRate1 = 0.00f;

        }

        try {

            taxRate2 = Float.parseFloat(props.getProp("TAX2"));

        } catch (NumberFormatException ex) {

            taxRate2 = 0.00f;

        }

    }

    private void testPrintLayout() {

        String currency = props.getProp("SYM");
        PageFormat pageFormat = new PageFormat();
        Paper paper = new Paper();

        DocumentLayout layout = new DocumentLayout(application.getWorkingPath() + "layouts/layout.invoice.xml");

        float[] pageSize = layout.getElement("pageSettings").getPaperSize();
        boolean portrait = layout.getElement("pageSettings").isPortrait();

        paper.setImageableArea(0, 0, pageSize[0], pageSize[1]);
        pageFormat.setPaper(paper);
        int orientation = PageFormat.PORTRAIT;
        if (portrait) {
            orientation = PageFormat.PORTRAIT;
        } else {
            orientation = PageFormat.LANDSCAPE;
        }
        pageFormat.setOrientation(orientation);

        final Book invoiceStack = new Book();

        DocumentElementTable tbl = (DocumentElementTable) layout.getElement("itemTable").getTable();

        int max_per_page = tbl.getRow_count();//how many items can the invoice hold per page?
        max_per_page--; //???

        if (debug) {
            System.out.println("max per page: " + max_per_page);
        }

        int number_of_invoice_items = invoice.getItemCount(); //actual: num_items - 1

        if (debug) {
            System.out.println("number of invoice items: " + number_of_invoice_items);
        }

        /* This only counts the base with no remainder */
        int number_of_pages = number_of_invoice_items / max_per_page;

        if (debug) {
            System.out.println("number of calculated pages: " + number_of_pages);
        }

        /* see if we need to tack on a page do to a remainder */
        if ((number_of_invoice_items % max_per_page) > 0) {
            number_of_pages++;
        }

        System.out.println("number of calculated pages (plus remainder): " + number_of_pages);

        if (max_per_page >= invoice.getItemCount()) {//                                               ???
            invoiceStack.append(new InvoicePrintPanel(layout, this.invoice, 0, invoice.getItemCount() - 1, 1.0), pageFormat);
            new PrintPreview(invoiceStack, pageSize);
            /*java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    new PrintPreviewFrame(invoiceStack).setVisible(true);
                }
            });*/
            return; //we are done, the page has been added to the stack 

        }

        int start = 1;  //<---WOW! Don't start at zero. DUH!
        int end = 0;

        if (number_of_invoice_items <= max_per_page) {
            end = number_of_invoice_items;
        } else {
            if (number_of_invoice_items >= max_per_page) {
                end = max_per_page;
            }
        }

        for (int p = 1; p <= number_of_pages; p++) {

            //System.out.println("Adding new page: start:"+start+"  End:"+end);
            invoiceStack.append(new InvoicePrintPanel(layout, this.invoice, start, end, 1.0), pageFormat);
            if (p + 1 < number_of_pages) {
                start = end + 1;
                end = end + max_per_page;
            } else {
                start = end + 1;
                end = (number_of_invoice_items - 1);
            }

        }

        new PrintPreview(invoiceStack, pageSize);

    }

    private void testprintPDF() {

        String currency = props.getProp("SYM");

        boolean ink = Boolean.parseBoolean(props.getProp("INK SAVER"));

        String color = props.getProp("INCOLOR");

        java.awt.Color rowColor = Tools.stringToColor(color);

        float std_row_height = 14f;  //row height to account for added rows to fill incomplete pages
        int headerwidths[] = {8, 16, 49, 11, 2, 2, 12};  //header widths for invoice detail
        String nl = System.getProperty("line.separator");

        String num = "";
        String type = "";
        String folder = "";

        if (quote) {

            num = "Quote_" + numberField.getText();
            type = props.getProp("QUOTE NAME");
            folder = props.getProp("QUOTE FOLDER");

        } else {

            num = "Invoice_" + numberField.getText();
            type = props.getProp("INVOICE NAME");
            folder = props.getProp("INVOICE FOLDER");
        }
        if (type.trim().equals("") && quote) {
            type = "Q U O T E";
        }
        if (type.trim().equals("") && !quote) {
            type = "I N V O I C E";
        }

        ReportDialog rd = new ReportDialog(null, true, folder, false,
                type + " PDF", props, true, false);
        if (!rd.getState()) {

            if (viewPrint || savedQuote) {
                return;
            }
            javax.swing.JOptionPane.showMessageDialog(null, num + ".pdf was NOT generated. Action canceled by user." + nl + "The invoice WAS posted.");

            return;
        }

        String file = rd.getFile() + num + ".pdf";  //folder + invoice number + extension

        if (!DV.isFileAccessible(file, "PDF file")) {
            return;
        }

        num = numberField.getText();

        /*  START TABLE WORK  */
        String company = props.getProp("CO NAME") + nl
                + props.getProp("CO OTHER") + nl
                + props.getProp("CO ADDRESS") + nl
                + props.getProp("CO CITY") + nl
                + props.getProp("CO PHONE");

        String statement = " BAL DUE";

        if (invoice.isPaid()) {
            statement = " P A I D";
        }

        PdfPTable head = new PdfPTable(2);  //setup a two col header table

        PdfPTable foot = new PdfPTable(2); //Setup a three col footer table

        PDFInvoice inv = new PDFInvoice(file, null);;

        try {

            //company info font
            String prop_font = props.getProp("FONT");
            String prop_font_size = props.getProp("FONT SIZE");
            String prop_font_style = props.getProp("FONT STYLE");

            int pts = 14;  //default
            int sty = Font.NORMAL;

            try {

                pts = Integer.parseInt(prop_font_size);

            } catch (NumberFormatException ex) {

                pts = 14; //default

            }

            Font font;

            if (prop_font.equalsIgnoreCase("Times New Roman") || prop_font.equalsIgnoreCase("Roman")) {
                font = new Font(Font.TIMES_ROMAN, pts, sty);

            } else if (prop_font.equalsIgnoreCase("Helvetica")) {
                font = new Font(Font.HELVETICA, pts, sty);

            } else if (prop_font.equalsIgnoreCase("Courier")) {

                font = new Font(Font.COURIER, pts, sty);

            } else {

                font = new Font(Font.HELVETICA, pts, sty);

            }

            PdfPCell cell;

            /**
             *
             * Build the header table
             *
             */
            File f = new File(props.getProp("LOGO"));

            if (f.exists()) {

                cell = new PdfPCell(Image.getInstance(props.getProp("LOGO")), true);

            } else {

                cell = new PdfPCell(new Phrase(company, font));

            }

            cell.setBorder(Rectangle.NO_BORDER);
            cell.setFixedHeight(100);
            head.addCell(cell);

            font = new Font(Font.COURIER, 10, Font.NORMAL);


            /* Create a table to hold two cells */
            PdfPTable trHeader = new PdfPTable(1);

            PdfPCell titleCell = new PdfPCell(new Phrase(type));

            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
            titleCell.setFixedHeight(50);

            Barcode128 code = new Barcode128();
            code.setCode(num);

            PdfPCell barcodeCell = new PdfPCell(code.createImageWithBarcode(inv.getContentByte(), null, null));

            barcodeCell.setBorder(Rectangle.NO_BORDER);
            barcodeCell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
            barcodeCell.setFixedHeight(50);

            trHeader.addCell(titleCell);
            trHeader.addCell(barcodeCell);

            cell = new PdfPCell(trHeader);

            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
            cell.setFixedHeight(100);
            head.addCell(cell);

            String s = "BILL TO:";
            if (quote) {
                s = "FOR:";
            }
            if (!props.getProp("BILLTO").equals("")) {
                s = props.getProp("BILLTO");
            }

            cell = new PdfPCell(new Phrase(s + nl + custTextArea.getText(), font));
            //cell = new PdfPCell (new Phrase(custTextArea.getText(), font));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setFixedHeight(80);
            cell.setVerticalAlignment(Rectangle.TOP);

            head.addCell(cell);

            /**
             * Ship To CEll *
             */
            if (shipToTextArea.getText().trim().length() > 0) {
                s = "SHIP TO:";
                cell = new PdfPCell(new Phrase(s + nl + shipToTextArea.getText(), font));
                //cell = new PdfPCell (new Phrase(shipToTextArea.getText(), font));
            } else {
                cell = new PdfPCell(new Phrase(""));
            }

            cell.setBorder(Rectangle.NO_BORDER);
            cell.setFixedHeight(80);
            head.addCell(cell);

            PdfPTable dateline = new PdfPTable(3);

            dateline.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            int[] widths = new int[]{33, 33, 33};
            dateline.setWidths(widths);

            font = new Font(Font.COURIER, 12, Font.NORMAL);  //get new font
            //Version 1.5

            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

            cell = new PdfPCell(new Phrase(df.format(datePicker1.getDate().getTime()), font));
            cell.setBorder(Rectangle.BOX);
            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
            cell.setPaddingBottom(3);
            dateline.addCell(cell);

            dateline.addCell("");

            cell = new PdfPCell(new Phrase("Doc# " + num, font));
            cell.setBorder(Rectangle.BOX);
            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
            cell.setPaddingBottom(3);
            dateline.addCell(cell);

            dateline.setTotalWidth(com.lowagie.text.PageSize.LETTER.width());

            cell = new PdfPCell(dateline);  //put this tble in a cell

            cell.setBorder(Rectangle.BOX);
            cell.setColspan(2);

            head.addCell(cell);  //magic method


            /*  END HEADER TABLE */
            /**
             *
             * Build the Item header for the last row of the header table
             *
             */
            PdfPTable itemHeader = new PdfPTable(7);

            itemHeader.setWidths(headerwidths);

            font = new Font(Font.TIMES_ROMAN, 10, Font.BOLD);

            cell = new PdfPCell(new Phrase("Qty", font));

            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);

            itemHeader.addCell(cell);

            cell = new PdfPCell(new Phrase("Code", font));

            itemHeader.addCell(cell);

            cell = new PdfPCell(new Phrase("Description", font));

            itemHeader.addCell(cell);

            cell = new PdfPCell(new Phrase("Unit " + currency, font));

            itemHeader.addCell(cell);

            //Version 1.5
            String tax1 = props.getProp("TAX1NAME");
            String tax2 = props.getProp("TAX2NAME");
            if (tax2.equals("")) {
                tax2 = " ";
            }
            if (tax1.equals("")) {
                tax1 = " ";
            }
            itemHeader.addCell(new PdfPCell(new Phrase(tax1.substring(0, 1), font)));
            itemHeader.addCell(new PdfPCell(new Phrase(tax2.substring(0, 1), font)));
            itemHeader.addCell(new PdfPCell(new Phrase("Total " + currency, font)));

            itemHeader.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);

            cell = new PdfPCell(itemHeader);
            cell.setColspan(2);

            cell.setPaddingTop(5);      //space between itemheader and dateline

            cell.setBorder(Rectangle.NO_BORDER);

            head.addCell(cell);

            inv.setHeader(head);
            inv.setWatermarkEnabled(false);

            /*  END ITEM HEADER TABLE */
            /**
             *
             * build the summary table
             *
             */
            int ftheaderwidths[] = {70, 30};
            font = new Font(Font.COURIER, 10, Font.NORMAL);

            foot.setWidths(ftheaderwidths);

            cell = new PdfPCell(new Phrase(invoiceMessage, font));  //this stays
            foot.addCell(cell);

            /*
          * check paidCheckBox
          * if paid get last payment and balance due
          * if not config default vals

             */
            //Version 1.5
            float payment = 0.00f;
            float balance_due = 0.00f;

            balance_due = Math.abs(invoice.getInvoiceDueNow()); //assure positive display

            payment = invoice.getTotalPayments();

            PdfPTable totals = new PdfPTable(2);

            totals.addCell(new Phrase("Sub-Total", font));
            totals.addCell(new Phrase(itemTotalField.getText(), font));

            if (tax1.equalsIgnoreCase("GST")) {
                tax1 = "GST Content";
            }
            if (tax1.equalsIgnoreCase("VAT")) {
                tax1 = "VAT Content";
            }

            totals.addCell(new Phrase(tax1, font));
            totals.addCell(new Phrase(t1Field.getText(), font));

            totals.addCell(new Phrase(tax2, font));
            totals.addCell(new Phrase(t2Field.getText(), font));

            totals.addCell(new Phrase("TOTAL", font));
            totals.addCell(new Phrase(grandTotalField.getText(), font));

            totals.addCell(new Phrase("Payment", font));

            totals.addCell(new Phrase(DV.money(payment), font));

            totals.addCell(new Phrase("BAL DUE  " + currency, font));
            totals.addCell(new Phrase(DV.money(balance_due), font));

            //Changed to 180 from 130
            totals.setTotalWidth(130);

            foot.addCell(totals);

            inv.addTagline(props.getProp("CO NAME") + " / " + props.getProp("CO ADDRESS") + " / "
                    + props.getProp("CO CITY") + " / " + props.getProp("CO PHONE"));

            cell = new PdfPCell(new Phrase("Page: ", inv.getDocFont(-4, 0)));
            cell.setColspan(2);

            foot.addCell(cell);

            inv.setFooter(foot);  //magic method

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (BadElementException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        /*  END SUMMARY TABLE */
        /**
         * Build actual Items table
         *
         */
        boolean print_zeros = Tools.getStringBool(props.getProp("PRINT ZEROS"));
        ReportModel rm = new ReportModel(invTable.getModel());

        int cols = invTable.getModel().getColumnCount();
        int rows = invTable.getModel().getRowCount();

        PdfPTable items = new PdfPTable(7);

        try {

            //items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
            items.setWidths(headerwidths);

        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        Font f = new Font(Font.COURIER, 10, Font.NORMAL);
        PdfPCell cell;
        int a = 1;
        String cellData = "";
        String qty, code, unit, tot;
        do {

            for (int i = 2; i < cols - 1; i++) {   //start grabbing data after invoice and inventory keys

                cellData = rm.getValueAt(i);

                //System.out.println("Cell Data: "+cellData+ "  COL:"+i);
                qty = rm.getValueAt(2);//qty
                code = rm.getValueAt(3);//code
                unit = rm.getValueAt(5); //price
                tot = rm.getValueAt(8);
                /* id misc and zero do not print qty or zeros, if print zeros then print only zeros for non MISC items */

                if (i == 5 && code.equals("MISC") && unit.equals("0.00")) { //get rid of price if zero
                    cellData = " ";
                }
                if (i == 3 && code.equals("MISC") && unit.equals("0.00")) { //get rid of misc code if zero
                    cellData = " ";
                }
                if (i == 2 && code.equals("MISC") && unit.equals("0.00")) { //get rid of qty if misc and zero
                    cellData = " ";
                }
                if (i == 8 && code.equals("MISC") && unit.equals("0.00")) {
                    cellData = " ";
                }
                if ((i == 8 && cellData.equals("0.00")) || (i == 5 && cellData.equals("0.00"))) {
                    if (!print_zeros) {
                        cellData = " ";
                    }
                }

                cell = new PdfPCell(new Phrase(cellData, f));

                cell.setBorder(Rectangle.NO_BORDER);

                if (i == cols - 2 || i == cols - 5) {

                    cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);

                }

                /* if qty or code field reduce font size */
                if (i == 2 || i == 3 || i == 5 || i == 8) {
                    Font fnt = new Font(Font.COURIER, 8, Font.NORMAL);

                    cell = new PdfPCell(new Phrase(cellData, fnt));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setVerticalAlignment(cell.ALIGN_BOTTOM);
                    if (i == cols - 2 || i == cols - 5) {

                        cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);

                    }
                }

                if ((a % 2) != 0) {

                    //Do not print if inksaver is on
                    //if (!ink) cell.setGrayFill(.80f);
                    if (!ink) {
                        cell.setBackgroundColor(rowColor);
                    }
                }

                items.addCell(cell);

            }

            a++;

        } while (rm.next());

        items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);

        /*  END ITEMS TABLE */
        /**
         *
         * Make the invoice pretty COSMETICS (Skipped if 1 page or more)
         *
         */
        float pages = inv.howManyPages(items);  //how many pages will it take to write invoice

        if (pages < 1) {   //if less than a full page fill it out

            pages = pages % 1.0f;

            float hf = inv.getHeaderSize() + inv.getFooterSize();

            float bs = inv.getBodySize() - 10;  //fudge

            float remain = bs - items.getTotalHeight();  //how much space is left after the table

            float row_height = std_row_height;    //standard row height??  15points

            float z = remain / row_height;  //how many Standard rows can be written to fill (no remainder)

            boolean odd = true;
            if (items.getRows().size() % 2 == 0) {
                odd = false;
            }

            for (int i = 1; i < z; i++) {

                for (int x = 2; x < cols - 1; x++) {   //simulate the data loop

                    cell = new PdfPCell(new Phrase(" ", f));  //place ' ' instead of data

                    cell.setBorder(Rectangle.NO_BORDER);

                    cell.setFixedHeight(row_height);

                    if (!odd) {

                        //Do not print if inksaver is on
                        //if (!ink) cell.setGrayFill(.80f);
                        if (!ink) {
                            cell.setBackgroundColor(rowColor);
                        }
                    }

                    items.addCell(cell);
                }

                if (odd) {
                    odd = false;
                } else {
                    odd = true;
                }

            }

        }

        /*  END COSMETICS */

 /*  END INVOICE TABLES  */
        inv.setBody(items);   //add the items table to the PDFInvoice

        inv.build();

        inv.finish();

        if (rd.isView()) {

            stat = ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);

        }

        if (rd.getWinPrn()) {

            ReportFactory.windowsFastPrint(file, props);
            if (rd.isDuplicate()) {
                ReportFactory.windowsFastPrint(file, props);
            }

        }

        if (rd.isEmail()) {
            /* get recipients email address */
            String ea = "";
            if (quote) {
                ea = theQuote.getEmail();
            } else {
                ea = invoice.getEmail();
            }

            /* TODO: Check email address */
            System.out.println("Invoice reported email address: " + ea);
            sendEmail(ea, file);
        }

    }

    private boolean printReciept() {

        String currency = props.getProp("SYM");

        int mm;

        try {

            mm = Integer.parseInt(props.getProp("ROLL WIDTH"));

        } catch (Exception e) {

            mm = 80;  //default
        }

        float width = mm * 0.0393700787f;

        PosPrinterService posPrinter = new PosPrinterService(new java.awt.Font("Courier", java.awt.Font.BOLD, 8), true, width);

        posPrinter.newLine();
        posPrinter.newLine();
        posPrinter.addLine(props.getProp("CO NAME"));
        posPrinter.addLine(props.getProp("CO OTHER"));
        posPrinter.addLine(props.getProp("CO ADDRESS"));
        posPrinter.addLine(props.getProp("CO CITY"));
        posPrinter.addLine(props.getProp("CO PHONE"));

        posPrinter.newLine();
        posPrinter.newLine();

        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();

        ReportModel rm = new ReportModel(invTable.getModel());

        String spc = " ";
        String bite = "";

        posPrinter.addLine(DV.addSpace("QTY   CODE ", 32, ' ') + "PRICE");
        posPrinter.addLine("ITEM DESCRIPTION");
        posPrinter.addLine(DV.addSpace("", 40, '-'));

        do {

            for (int i = 2; i < 6; i++) {   //start grabbing data after invoice and inventory keys

                spc = " ";

                if (i == 2) {
                    spc = "";
                }

                bite = rm.getValueAt(i);

                if (i == 2 || i == 3 || i == 5) {

                    //add formatting crap
                    if (i == 3) {
                        // Add spaces to line up prices
                        bite = DV.addSpace(bite, 27, ' ');
                        // chop the description down
                        //if (bite.length() > 26) bite = bite.substring(0, 25);
                    }
                    if (i == 2) {
                        bite = bite + ' ';
                    }
                    line1.append(bite);
                }
                if (i == 4) {
                    line2.append(bite);
                }


                /*
                     
                     if (i == 4){
                         // Add spaces to line up prices
                         bite = DV.padString(bite, 26);
                         // chop the description down
                         if (bite.length() > 26) bite = bite.substring(0, 25);
                     }
                     sb.append(spc + bite + spc);*/
                //if col 3 addline
            }

            posPrinter.addLine(line1.toString());
            posPrinter.addLine(line2.toString());
            posPrinter.addLine(" ");
            line1 = new StringBuilder();
            line2 = new StringBuilder();

        } while (rm.next());

        posPrinter.addLine(DV.addSpace("", 40, '-'));
        posPrinter.newLine();
        posPrinter.addLine("Item Total: " + currency + " " + itemTotalField.getText());
        posPrinter.addLine(t1Label.getText() + " Total: " + currency + " " + t1Field.getText());
        posPrinter.addLine(t2Label.getText() + " Total: " + currency + " " + t2Field.getText());
        posPrinter.addLine("GrandTotal: " + currency + " " + grandTotalField.getText());
        posPrinter.newLine();
        posPrinter.newLine();
        posPrinter.addLine("Reference Number: " + numberField.getText());
        Calendar cal = Calendar.getInstance();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.MEDIUM);
        posPrinter.addLine(df.format(cal.getTime()));
        posPrinter.go();

        return false;

    }

    private boolean print(boolean q) {

        computePrices();
        testprintPDF();
        //testPrintLayout();
        return true;

    }

    private void setView() {

        String inum = numberField.getText();
        numberField.setDocument(new LimitedDocument(8));
        numberField.setText(inum);

        clearFields();

        TableColumnModel cm = invTable.getColumnModel();
        TableColumn tc;

        if (cm.getColumnCount() < 8) {
            return;
        }

        tc = cm.getColumn(0);
        invTable.removeColumn(tc);//remove key column
        tc = cm.getColumn(0);
        invTable.removeColumn(tc);//remove item key column
        tc = cm.getColumn(7);
        invTable.removeColumn(tc);//remove cost

        /* if (!DV.parseBool(props.getProp("SHOW TAX 2"),true)) {
            tc = cm.getColumn(5);
            invTable.removeColumn(tc);//remove cost
        }*/
        invTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumn col = invTable.getColumnModel().getColumn(0);
        col.setPreferredWidth(30);
        col = invTable.getColumnModel().getColumn(1);
        col.setPreferredWidth(100);
        col = invTable.getColumnModel().getColumn(2);
        col.setPreferredWidth(300);

        col = invTable.getColumnModel().getColumn(4);
        col.setPreferredWidth(30);
        col = invTable.getColumnModel().getColumn(5);
        col.setPreferredWidth(30);

    }

    private void clearFields() {

        qtyTextField.setText("1.00");
        upcField.setText("");
        upcField.requestFocus();
        //qtyTextField.requestFocus();  //custom

    }

    private float computeVAT(String inventory_desc, float model_unit_price, boolean model_GST) {

        if (!VAT) {
            return model_unit_price;
        }

        /* Build unit prices from tax selection and actual inventory price */
 /* compare inventory price to the one in the table, if lower than the inventory price plus GST - use it and add GST
             if higher, ?? use inventory price + GST (if GST is selected)*/

 /* Search for inventory item with that description */
        //InventoryDAO dao = new InventoryDAO(application.getDb());
        ArrayList al = db.searchFast("inventory", 3, inventory_desc, false);

        if (al != null) {
            Object[] rec = db.getRecord("inventory", ((Long) al.get(0)));
            /* Get price amount from rec */
            float inventory_price = (Float) rec[8];//inventory price
            float price_with_GST = inventory_price + (inventory_price * this.taxRate1);
            /* Leave the price alone if it appears adjusted by the user (lower than inventory price with GST) */
 /* if (model_unit_price < price_with_GST){
                return model_unit_price;
            }*/

            if (model_GST) { //if tax1
                return price_with_GST;
            } else {
                //return inventory_price;
                return model_unit_price;
            }

        }
        return model_unit_price;
    }

    private void computePrices() {

        /*TODO calaulation is not changing the table and taxes are not getting set right */
        int rows = tm.getRowCount();
        //itemCountLabel.setText(Integer.toString(rows));

        float totalTax1 = 0.00f;
        float totalTax2 = 0.00f;
        float grandTotal = 0.00f;
        float total = 0.00f; //scratch

        shipping = 0.00f;

        //run through and build totals on unit * qty
        String cde;

        for (int r = 0; r < rows; r++) {
            total = (Float) tm.getValueAt(r, 2) * (Float) tm.getValueAt(r, 5);//qty * unit            
            tm.setValueAt(Tools.round(total), r, 8);
        }

        total = invoice.getItemTotal();

        totalTax1 = invoice.getTax1Total();
        totalTax2 = invoice.getTax2Total();
        if (debug) {
            System.out.println("InvoiceDialog: totalTax1" + totalTax1 + " TotalTax2" + totalTax2);
        }
        // update tax fields

        t1Field.setText(DV.money(totalTax1));
        t2Field.setText(DV.money(totalTax2));
        itemTotalField.setText(DV.money(total));

        // upadte Grand total
        grandTotal = invoice.getInvoiceTotal();
        grandTotalField.setText(DV.money(grandTotal));
    }

    private void miscAction() {

        MiscItemDialog mi = new MiscItemDialog(null, true, application, null);

        if (mi.getStat()) {
            addItem(mi.getItem(), false, -1, -1); //no replace and no insert
        }
        mi.dispose();
        upcField.requestFocus();
    }

    private boolean isItemValid(int inventory_key, float needed, boolean chk_availability) {

        //checks the requested value against inventeory
        Object[] rec = db.getRecord("inventory", inventory_key);

        return isItemValid(rec, needed, chk_availability);
    }

    private boolean ignore_qty = false;

    private boolean isItemValid(Object[] rec, float qty_needed, boolean chk_availability) {

        boolean rval = true;
        float status = invoice.checkInventory((Integer) rec[0], qty_needed);
        if (chk_availability) {

            if (status == -1) {

                int a = JOptionPane.showConfirmDialog(this, (String) rec[3] + " is marked as Unavailable. " + nl
                        + "Would you like to sell it anyway?", "Item Unavailable", JOptionPane.YES_NO_OPTION);

                if (a == 0) {
                    rval = true;
                } else {
                    return false;
                }
            }
        }
        /**/
        if (status == -3) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    (String) rec[3] + " cannot be sold in partial quantities.");
            return false;
        }
        String category = (String) rec[9];
        /* if the item is a service or the qty is vazlid ignore warning */
        if (qty_needed <= ((Float) rec[6]) || category.equalsIgnoreCase("Service")) {
        } else {

            if (!ignore_qty) {
                int a = JOptionPane.showConfirmDialog(this, "Not enough in stock to complete the sale. Available: " + ((Float) rec[6]) + nl
                        + "Would you like to add " + '"' + (String) rec[3] + '"' + " anyway?", "Inventory Warning", JOptionPane.YES_NO_OPTION);
                if (a == 0) {
                    rval = true;
                } else {
                    return false;
                }
            }
        }
        return rval;
    }

    /* getinventory us addItem */
    private void addItem(Object[] rec, boolean addCat, int replaceRow, int insert) {

        float quantity = DV.parseFloat(qtyTextField.getText());
        String code = (String) rec[2]; //Code

        //check here to make sure qty and availablity are ok
        if (!code.trim().equals("MISC") && !code.trim().equals("DISC")) {

            if (!isItemValid(rec, quantity, true)) {
                return;
            }
        }

        //boolean taxable1 = false;
        //if (custTax1 && ((Boolean)rec[13])) taxable1 = true;
        //if (!cu stTax1 && ((Boolean)rec[13])) taxable1 = true;
        int insertedAt = -1;
        insertedAt = invoice.addItem(rec, quantity, custTax1, custTax2, addCat, replaceRow, insert);

        if (insertedAt > -1) {
            invTable.changeSelection(insertedAt, 0, false, false);
        }

        computePrices();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        custButton = new javax.swing.JButton();
        numberField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        custTextArea = new javax.swing.JTextArea();
        logoPanel = new javax.swing.JPanel();
        datePicker1 = new com.michaelbaranov.microba.calendar.DatePicker();
        jScrollPane4 = new javax.swing.JScrollPane();
        shipToTextArea = new javax.swing.JTextArea();
        shipToButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        invoiceNumberEditCheckBox = new javax.swing.JCheckBox();
        autoInvoiceNumberButton = new javax.swing.JButton();
        convertButton = new javax.swing.JButton();
        copyBillToButton = new javax.swing.JButton();
        clearShipToButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        invTable = new javax.swing.JTable();
        upcField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        optionsToolbar = new javax.swing.JToolBar();
        messageButton = new javax.swing.JButton();
        viewReturnsButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        printReceiptButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        itemTotalField = new javax.swing.JTextField();
        grandTotalField = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        calcButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        postButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        jToolBar3 = new javax.swing.JToolBar();
        receiptCheckBox = new javax.swing.JCheckBox();
        paymentCheckBox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        t1Field = new javax.swing.JTextField();
        t2Field = new javax.swing.JTextField();
        t1Label = new javax.swing.JLabel();
        t2Label = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        paymentButton = new javax.swing.JButton();
        statementButton = new javax.swing.JButton();
        historyButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        datePicker2 = new com.michaelbaranov.microba.calendar.DatePicker();
        qtyTextField = new javax.swing.JTextField();
        jToolBar4 = new javax.swing.JToolBar();
        VATButton = new javax.swing.JButton();
        shippingButton = new javax.swing.JButton();
        packingslipButton = new javax.swing.JButton();
        jToolBar5 = new javax.swing.JToolBar();
        miscButton = new javax.swing.JButton();
        discountButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        upcCombo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Invoice / Quote");
        setIconImage(winIcon);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        custButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Customers.png"))); // NOI18N
        custButton.setText("Bill To:");
        custButton.setToolTipText("Select a customer from My Connections");
        custButton.setIconTextGap(1);
        custButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                custButtonActionPerformed(evt);
            }
        });

        numberField.setEditable(false);
        numberField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        numberField.setToolTipText("8 Alphanumeric character max");

        custTextArea.setColumns(20);
        custTextArea.setFont(new java.awt.Font("Courier", 0, 14)); // NOI18N
        custTextArea.setRows(5);
        custTextArea.setToolTipText("Customer Billing Information");
        jScrollPane1.setViewportView(custTextArea);

        org.jdesktop.layout.GroupLayout logoPanelLayout = new org.jdesktop.layout.GroupLayout(logoPanel);
        logoPanel.setLayout(logoPanelLayout);
        logoPanelLayout.setHorizontalGroup(
            logoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        logoPanelLayout.setVerticalGroup(
            logoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        datePicker1.setToolTipText("Click the Calendar icon to select a date.");
        datePicker1.setFieldEditable(false);
        datePicker1.setShowNoneButton(false);
        datePicker1.setStripTime(true);
        datePicker1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datePicker1PropertyChange(evt);
            }
        });

        shipToTextArea.setColumns(20);
        shipToTextArea.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        shipToTextArea.setRows(5);
        shipToTextArea.setToolTipText("DO NOT use this as the customer field, it is for Shipping directives only.");
        jScrollPane4.setViewportView(shipToTextArea);

        shipToButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Airplane.png"))); // NOI18N
        shipToButton.setText("Ship To:");
        shipToButton.setToolTipText("Select a shipping address from My Connections");
        shipToButton.setMaximumSize(new java.awt.Dimension(39, 23));
        shipToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shipToButtonActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        invoiceNumberEditCheckBox.setText("Edit");
        invoiceNumberEditCheckBox.setToolTipText("Select this to Manually Edit the Invoice Number");
        invoiceNumberEditCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        invoiceNumberEditCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceNumberEditCheckBoxActionPerformed(evt);
            }
        });

        autoInvoiceNumberButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        autoInvoiceNumberButton.setText("Invoice #");
        autoInvoiceNumberButton.setToolTipText("Get Auto Invoice Number");
        autoInvoiceNumberButton.setEnabled(false);
        autoInvoiceNumberButton.setMargin(new java.awt.Insets(2, 8, 2, 8));
        autoInvoiceNumberButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoInvoiceNumberButtonActionPerformed(evt);
            }
        });

        convertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Copy document.png"))); // NOI18N
        convertButton.setText("Copy To Invoice");
        convertButton.setToolTipText("Uses this Quote to Build a New Invoice");
        convertButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        convertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convertButtonActionPerformed(evt);
            }
        });

        copyBillToButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/RRZE/template16.png"))); // NOI18N
        copyBillToButton.setText("Copy");
        copyBillToButton.setToolTipText("Copies the 'Bill To'  into 'Ship To'");
        copyBillToButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        copyBillToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBillToButtonActionPerformed(evt);
            }
        });

        clearShipToButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Erase.png"))); // NOI18N
        clearShipToButton.setText("Clear");
        clearShipToButton.setToolTipText("Clears the 'Ship To' info");
        clearShipToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearShipToButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 302, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(custButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(copyBillToButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 297, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(shipToButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(clearShipToButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(logoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(autoInvoiceNumberButton)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(numberField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(invoiceNumberEditCheckBox))
                    .add(convertButton)
                    .add(datePicker1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(copyBillToButton)
                            .add(clearShipToButton)
                            .add(custButton)
                            .add(shipToButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(logoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(19, 19, 19))
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(datePicker1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(convertButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 39, Short.MAX_VALUE)
                        .add(autoInvoiceNumberButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(numberField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(invoiceNumberEditCheckBox)))
                    .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setOpaque(false);

        invTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        invTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "KEY", "Date", "QTY", "CODE", "DESCRIPTION", "UNIT", "Tax 1", "Tax 2", "TOTAL", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        invTable.setInheritsPopupMenu(true);
        invTable.setSelectionBackground(new java.awt.Color(204, 255, 255));
        invTable.setDefaultRenderer(java.lang.Float.class,  new FractionCellRenderer (10, 2, javax.swing.SwingConstants.RIGHT));
        invTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invTableMouseClicked(evt);
            }
        });
        invTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                invTableKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(invTable);

        upcField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        upcField.setToolTipText("Double-click or hit ENTER to select from inventory (F12 for Misc)");
        upcField.setNextFocusableComponent(miscButton);
        upcField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                upcFieldMouseClicked(evt);
            }
        });
        upcField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                upcFieldFocusGained(evt);
            }
        });
        upcField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                upcFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                upcFieldKeyReleased(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        optionsToolbar.setFloatable(false);
        optionsToolbar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        optionsToolbar.setRollover(true);

        messageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Get message.png"))); // NOI18N
        messageButton.setText("Invoice Message");
        messageButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        messageButton.setPreferredSize(new java.awt.Dimension(160, 33));
        messageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageButtonActionPerformed(evt);
            }
        });
        optionsToolbar.add(messageButton);

        viewReturnsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/RRZE/monitoring16.png"))); // NOI18N
        viewReturnsButton.setText("View Returns");
        viewReturnsButton.setToolTipText("Product Returns for this Invoice");
        viewReturnsButton.setPreferredSize(new java.awt.Dimension(160, 29));
        viewReturnsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewReturnsButtonActionPerformed(evt);
            }
        });
        optionsToolbar.add(viewReturnsButton);

        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Print preview.png"))); // NOI18N
        printButton.setText("Print Preview");
        printButton.setToolTipText("Print");
        printButton.setPreferredSize(new java.awt.Dimension(160, 29));
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        optionsToolbar.add(printButton);

        printReceiptButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Form 3d.png"))); // NOI18N
        printReceiptButton.setText("Print Receipt");
        printReceiptButton.setFocusable(false);
        printReceiptButton.setPreferredSize(new java.awt.Dimension(160, 29));
        printReceiptButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        printReceiptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printReceiptButtonActionPerformed(evt);
            }
        });
        optionsToolbar.add(printReceiptButton);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(optionsToolbar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(optionsToolbar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setPreferredSize(new java.awt.Dimension(296, 116));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Item Total");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Grand Total");

        itemTotalField.setEditable(false);
        itemTotalField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        itemTotalField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        itemTotalField.setToolTipText("Total for All Items (Taxes Not Included)");

        grandTotalField.setEditable(false);
        grandTotalField.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        grandTotalField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        calcButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Grid.png"))); // NOI18N
        calcButton.setText("Prices");
        calcButton.setToolTipText("Tally Invoice Totals");
        calcButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        calcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(calcButton);

        saveButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        saveButton.setText("Quote");
        saveButton.setToolTipText("Save As a Quote");
        saveButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveButton.setIconTextGap(8);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveButton);

        postButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        postButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Next.png"))); // NOI18N
        postButton.setText(" Post");
        postButton.setToolTipText("Save As an Invoice");
        postButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        postButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(postButton);

        closeButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Cancel.png"))); // NOI18N
        closeButton.setText("Discard");
        closeButton.setToolTipText("Cancel / Close");
        closeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(closeButton);

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        receiptCheckBox.setText("Print Receipt ");
        receiptCheckBox.setToolTipText("Select this to Print to a Reciept Printer");
        receiptCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        receiptCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jToolBar3.add(receiptCheckBox);

        paymentCheckBox.setText("Take Payment ");
        paymentCheckBox.setToolTipText("When posting, this setting displays the payment box before printing");
        paymentCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        paymentCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jToolBar3.add(paymentCheckBox);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(grandTotalField)
                            .add(itemTotalField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(itemTotalField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(grandTotalField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        t1Field.setEditable(false);
        t1Field.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        t1Field.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        t1Field.setToolTipText("Total for All Items");

        t2Field.setEditable(false);
        t2Field.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        t2Field.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        t2Field.setToolTipText("Total for All Items");

        t1Label.setText("Tax1");

        t2Label.setText("Tax2");

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        paymentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Money.png"))); // NOI18N
        paymentButton.setText("Activity");
        paymentButton.setToolTipText("Process Payments and Adjustments for this Invoice");
        paymentButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        paymentButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        paymentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentButtonActionPerformed(evt);
            }
        });
        toolBar.add(paymentButton);

        statementButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Period end.png"))); // NOI18N
        statementButton.setText("Statement");
        statementButton.setToolTipText("Detailed Statement of Activity for this Invoice");
        statementButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        statementButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        statementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statementButtonActionPerformed(evt);
            }
        });
        toolBar.add(statementButton);

        historyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Case history.png"))); // NOI18N
        historyButton.setText("History");
        historyButton.setToolTipText("Invoice History for the Associated Customer");
        historyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        historyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        historyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                historyButtonActionPerformed(evt);
            }
        });
        toolBar.add(historyButton);

        jLabel1.setText("Due Date");

        datePicker2.setEnabled(false);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datePicker2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, datePicker2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(t1Label)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(t1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(t2Label)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(t2Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                        .add(30, 30, 30))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(toolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(t1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(t1Label)
                    .add(t2Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(t2Label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addContainerGap())
        );

        qtyTextField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        qtyTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                qtyTextFieldFocusGained(evt);
            }
        });

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        VATButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Calculator.png"))); // NOI18N
        VATButton.setText("VAT  ");
        VATButton.setToolTipText("VAT - GST Tax Calculator");
        VATButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VATButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(VATButton);

        shippingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Balance.png"))); // NOI18N
        shippingButton.setText("Weight");
        shippingButton.setToolTipText("Calculate the weight of the select items");
        shippingButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        shippingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shippingButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(shippingButton);

        packingslipButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Package.png"))); // NOI18N
        packingslipButton.setText("Packing");
        packingslipButton.setToolTipText("Print a packing slip of the selected items");
        packingslipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packingslipButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(packingslipButton);

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);

        miscButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Login.png"))); // NOI18N
        miscButton.setText("Misc Item");
        miscButton.setToolTipText("Add Non-Inventory Item");
        miscButton.setNextFocusableComponent(qtyTextField);
        miscButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miscButtonActionPerformed(evt);
            }
        });
        jToolBar5.add(miscButton);

        discountButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Percent.png"))); // NOI18N
        discountButton.setText("Disc");
        discountButton.setToolTipText("Insert a discount");
        discountButton.setFocusable(false);
        discountButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        discountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountButtonActionPerformed(evt);
            }
        });
        jToolBar5.add(discountButton);

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        removeButton.setText("Remove");
        removeButton.setToolTipText("Removes the Selected Items");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        jToolBar5.add(removeButton);

        upcCombo.setFont(new java.awt.Font("DejaVu Sans", 0, 12)); // NOI18N
        upcCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "UPC", "Code" }));
        upcCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upcComboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 896, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                        .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 306, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                        .add(qtyTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(upcCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(upcField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jToolBar5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jToolBar4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 276, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jToolBar4, 0, 0, Short.MAX_VALUE)
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, upcField)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, qtyTextField)
                        .add(upcCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewReturnsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewReturnsButtonActionPerformed

        int r[] = {0, 0, 0, 4, 4, 4};
        int w[] = {50, 100, 300, 80};

        new TableBrowseDialog(parentWin, true, invoice.getReturns(),
                "Invoice " + invoice.getInvoiceNumber() + " Returns", r, w);

    }//GEN-LAST:event_viewReturnsButtonActionPerformed

    private void autoInvoiceNumberButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoInvoiceNumberButtonActionPerformed

        numberField.setText(props.getProp("INVOICE PREFIX") + props.getProp("NEXT NUMBER"));
        invoiceNumberEditCheckBox.setSelected(false);
        numberField.setEditable(invoiceNumberEditCheckBox.isSelected());

    }//GEN-LAST:event_autoInvoiceNumberButtonActionPerformed
    private String mem_invoiceNumber = "";
    private void invoiceNumberEditCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceNumberEditCheckBoxActionPerformed
        boolean selected = invoiceNumberEditCheckBox.isSelected();
        numberField.setEditable(selected);
        autoInvoiceNumberButton.setEnabled(selected);
        if (selected) {
            mem_invoiceNumber = numberField.getText();
        } else {
            numberField.setText(mem_invoiceNumber);
        }

    }//GEN-LAST:event_invoiceNumberEditCheckBoxActionPerformed

    private void shipToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shipToButtonActionPerformed

        shipToAction();

    }//GEN-LAST:event_shipToButtonActionPerformed

    private void packingslipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packingslipButtonActionPerformed

        createPackingSlip();


    }//GEN-LAST:event_packingslipButtonActionPerformed

    private void createPackingSlip() {

        String measure = props.getProp("MEASURE");
        datavirtue.LinePrinter lp = new datavirtue.LinePrinter(true);
        ArrayList al = calcWeight(false);
        String zone = props.getProp("ADDRESS STYLE");
        StringBuilder sb = new StringBuilder();

        lp.addLine("P A C K I N G   S L I P" + "                     " + DV.getShortDate());
        lp.newLine();
        lp.addLine("FROM:");

        String[] co_info = new String[]{props.getProp("CO NAME"),
            props.getProp("CO ADDRESS"),
            props.getProp("CO CITY"),
            props.getProp("CO PHONE")};

        lp.addLines(co_info);
        lp.newLine();
        lp.newLine();

        lp.addLine("TO:");

        if (shipToTextArea.getText().trim().length() > 0) {

            lp.addLines(shipToTextArea.getText().split(System.getProperty("line.separator")));

        } else {

            lp.addLines(custTextArea.getText().split(System.getProperty("line.separator")));

        }
        lp.newLine();
        lp.newLine();

        Object[] itemInfo = new Object[2];

        float totalLineWeight;
        int tableRow;

        if (al != null) {
            al.trimToSize();
            for (int i = 0; i < al.size(); i++) {

                itemInfo = (Object[]) al.get(i);

                tableRow = (Integer) itemInfo[0];
                totalLineWeight = (Float) itemInfo[1];

                String qt = Float.toString((Float) invTable.getModel().getValueAt(tableRow, 2));

                sb.append(DV.addSpace(qt, 4, ' '));
                sb.append(DV.addSpace((String) invTable.getModel().getValueAt(tableRow, 4), 52, ' '));
                sb.append(Float.toString(totalLineWeight) + ' ' + measure);

                lp.addLine(sb.toString());
                sb = new StringBuilder();
            }

            lp.newLine();
            lp.newLine();
            lp.addLine("Item(s) Weight: " + totalWeight + ' ' + measure + "  -  Shipping Weight:_________________");

            lp.formFeed();
            lp.go();
        } else {

            javax.swing.JOptionPane.showMessageDialog(null, "Please select the items to include on the packing list. (Hold Ctrl and Click each row.)");
            return;
        }
    }

    private void shippingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shippingButtonActionPerformed

        /*
         *  scan through and get and calc weight of each item SOLD
         *
         *  total weight of items and display
         *
         */
        calcWeight(true);


    }//GEN-LAST:event_shippingButtonActionPerformed

    private float totalWeight = 0;

    private ArrayList calcWeight(boolean show_total) {

        String measure = props.getProp("MEASURE");

        float weight = 0;
        float total_weight = 0;

        float item_qty = 0;

        String desc = "";
        ArrayList al;
        Object[] rec;
        int[] a = invTable.getSelectedRows();
        ArrayList items = new ArrayList();

        if (a.length < 1) {

            if (show_total) {
                javax.swing.JOptionPane.showMessageDialog(null, "Please select some items to calculate. (Hold Ctrl and Click each row.)");
            }
            return null;
        }

        for (int i = 0; i < a.length; i++) {

            desc = (String) invTable.getValueAt(a[i], 2);
            item_qty = (Float) invTable.getValueAt(a[i], 0);

            al = db.searchFast("inventory", 3, desc, false);
            if (al != null) {

                rec = db.getRecord("inventory", (Long) al.get(0));

                try {

                    weight = Float.parseFloat((String) rec[5]);
                    total_weight += (weight * item_qty);

                    if (debug) {
                        System.out.println("Table row " + a[i]);
                    }

                    items.add(new Object[]{new Integer(a[i]), new Float(weight * item_qty)});

                } catch (NumberFormatException ex) {

                    total_weight += 0;
                    items.add(new Object[]{new Integer(a[i]), new Float(0.00f)});

                }
            }
        }
        totalWeight = total_weight;

        if (show_total) {
            javax.swing.JOptionPane.showMessageDialog(null, "Total Weight: " + total_weight + ' ' + measure);
        }

        return items;
    }

    private void invTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invTableKeyPressed

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE && !viewPrint) {

            removeRows();

        }


    }//GEN-LAST:event_invTableKeyPressed

    private void miscButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miscButtonActionPerformed

        /*
         *Bring up a dialog to enter a non-inventory item.
         *
         *
         */
        miscAction();

    }//GEN-LAST:event_miscButtonActionPerformed

    private void scanAction() {

        int[] rvals = null;

        if (DV.validFloatString(qtyTextField.getText())) {

            if (upcField.getText().trim().equals("")) {   // blank upcField ENTER pressed

                MyInventoryApp id = new MyInventoryApp(this.parentWin, true, application, true);

                rvals = id.getReturnValue();  //stores one or many item selection(s) from the inventory module

                if (rvals == null || rvals[0] == -1) {

                    clearFields();
                    return;

                }
                id.dispose();

            } else {  //value entered into upcField and ENTER pressed

                String t = upcField.getText().trim();

                if (t.startsWith("/")) {

                    /* clip everything past 'grp=' & store in grpname */
                    String grpname = t.substring(1, t.length());

                    /* scan the grps dir for the specified group */
                    String path = workingPath + "grps/";

                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File f = new File(path + grpname);
                    String line;
                    int k = 0;
                    boolean skip = false;
                    if (f.exists()) {

                        BufferedReader in = null;
                        try {
                            in = new BufferedReader(
                                    new FileReader(f));
                            do {

                                skip = false;
                                line = in.readLine();
                                try {
                                    if (line != null) {
                                        k = Integer.valueOf(line);
                                    } else {
                                        skip = true;
                                    }
                                } catch (NumberFormatException ex) {
                                    /* ignore bad numbers */
                                    skip = true;
                                }

                                if (!skip) {
                                    currentItem = db.getRecord("inventory", k);
                                    this.addItem(currentItem, addCategoryInfo, -1, -1); //neg one means no replace
                                    /*add category line item here?? added in InvoiceModel  */

                                }
                                //System.out.println(line);

                            } while (line != null);

                            in.close();
                            upcField.setText("");
                            clearFields();
                            return;

                        } catch (Exception e) {
                            e.printStackTrace();
                            javax.swing.JOptionPane.showMessageDialog(null, "An error occured while processing group: " + grpname);
                            try {
                                in.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            return;
                        }

                    } else {

                        javax.swing.JOptionPane.showMessageDialog(null, "The group doesn't exist.");

                    }

                }//end group code

//do big fat search: find the product based on the code recieved from upcField
                int col = 0;

                /*if (upcRadio.isSelected()) col = 1;  //adjust the search based on user settings
                 else col = 2;
                 if (upcCombo.getSelectedIndex()==0) col = 1;
                 if (upcCombo.getSelectedIndex()==1) col = 2;
                 if (upcCombo.getSelectedIndex()==2) col = 3;*/
                col = upcCombo.getSelectedIndex() + 1;

                //change searc based on desc col 3
                /*TODO: Move out of the gui into a DAO */
                ArrayList al = db.search("inventory", col, upcField.getText(), false);//false means exact search

                if (al != null) {

                    al.trimToSize();
                    // rvals = (Integer) al.toArray();
                    rvals = new int[al.size()];

                    for (int i = 0; i < al.size(); i++) {
                        rvals[i] = (Integer) al.get(i);
                    }

                } else {
                    String searchType = "";
                    if (col == 1) {
                        searchType = "UPC";
                    }
                    if (col == 2) {
                        searchType = "Code";
                    }

                    int choice;

                    choice = javax.swing.JOptionPane.showConfirmDialog(null,
                            "Would you like to open the inventory manager?",
                            searchType + ": " + upcField.getText() + " was not found",
                            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (choice == JOptionPane.YES_OPTION) {
                        MyInventoryApp id = new MyInventoryApp(this.parentWin, true, application, true);

                        rvals = id.getReturnValue();  //stores one or many item selection(s) from the inventory module

                        if (rvals == null || rvals[0] == -1) {
                            clearFields();
                            return;
                        }

                        id.dispose();
                    } else {
                        return;
                    }
                }
            }
            if (rvals != null) {

                for (int i = 0; i < rvals.length; i++) {  //process 'all' of the selections
                    //System.out.println("rvals "+rvals[i]);
                    currentItem = db.getRecord("inventory", rvals[i]);
                    //DV.expose(currentItem);
                    this.addItem(currentItem, addCategoryInfo, -1, -1); //no replace and no insert

                    /* add category line item here */
                }
                clearFields();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Make sure Quantity is a valid number.", "Form Problem!", JOptionPane.OK_OPTION);
        }

        clearFields();

    }

    private void upcFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_upcFieldKeyPressed

        //System.out.println("Key Code:" + evt.getKeyCode());
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {

            scanAction();

        }

        if (evt.getKeyCode() == 123) {  //press F12 for misc item

            miscAction();

        }

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_HOME) {

            post();

        }

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_END) {

            int a = javax.swing.JOptionPane.showConfirmDialog(null, "Discard this invoice?", "Exit", JOptionPane.YES_NO_OPTION);

            if (a == 0) {

                closeInvoiceWindow();

            }
        }
    }//GEN-LAST:event_upcFieldKeyPressed

    private void post() {
        if (invTable.getRowCount() < 1) {
            return;
        }

        if (debug) {
            System.out.println("Entering InvoiceDialog post() <>");
        }
        if (!verifyInvoiceNumber(numberField.getText())) {
            return;
        }
        if (debug) {
            System.out.println("InvoiceDialog post(), inv number ok <>");
        }
        int r = tm.getRowCount();
        if (debug) {
            System.out.println("InvoiceDialog post(), item row count: " + r);
        }
        if (r < 1) {
            return;
        }

        if (custTextArea.getText().trim().equals("") || custTextArea.getText().trim().length() < 4) {

            javax.swing.JOptionPane.showMessageDialog(null, "Please provide some valid customer information.");
            return;
        }

        int a = javax.swing.JOptionPane.showConfirmDialog(null, "Would you like to commit this invoice?", "Post?", JOptionPane.YES_NO_OPTION);
        if (a == 0) {
            quote = false;
        } else {
            return;
        }

        /* Re-verify all inventory again without availability check */
        ArrayList al;
        Object[] rec;
        if (debug) {
            System.out.println("InvoiceDialog post(), reverifying inventory <>");
        }
        for (int x = 0; x < r; x++) {

            al = db.search("inventory", 3, (String) tm.getValueAt(x, 4), false);

            if (al != null) {

                rec = db.getRecord("inventory", (Integer) al.get(0));

                if (!isItemValid(rec, (Float) tm.getValueAt(x, 2), false)) {

                    if (debug) {
                        System.out.println("InvoiceDialog post(), item invalid <>");
                    }
                    invTable.changeSelection(x, 0, false, false);
                    return;

                }

            }

        }
        /* END Re-verify */

        if (!postInvoice(paymentCheckBox.isSelected())) {

            quote = true;  //we go back to being a quote if it fails
            return;  //turn back if the invoice post fails.

        } else {
            if (debug) {
                System.out.println("Next Invoice Number: " + nextAutoInvoiceNumber);
            }
            if (debug) {
                System.out.println("Last Recorded Number: " + lastRecInvNum);
            }
            if (new String(invoicePrefix + nextAutoInvoiceNumber).equals(lastRecInvNum)) {
                incrInvoice();
            }

        }
        /* Adjust inventory qty for all items sold  */

        String cde;
        float need;

        for (int x = 0; x < r; x++) {

            cde = (String) tm.getValueAt(x, 3);

            if (cde.equals("MISC"));//skip
            else {

                al = db.search("inventory", 3, (String) tm.getValueAt(x, 4), false);

                if (al != null) {

                    rec = db.getRecord("inventory", (Integer) al.get(0));

                    if (rec != null) {
                        //Version 1.5
                        need = (Float) tm.getValueAt(x, 2);
                        rec[6] = new Float((Float) rec[6] - need);  //reduce inventory qty
                        rec[16] = new Long(new Date().getTime()); //date for last sale

                        db.saveRecord("inventory", rec, false);

                    }

                }

            }

        }

        if (receiptCheckBox.isSelected()) {
            printReciept();
        } else {
            print(false);
        }
        this.dispose();
    }

    private void sendEmail(String toAddress, String file) {

        if (!Tools.verifyEmailAddress(toAddress)) {
            return;
        }

        String type = props.getProp("INVOICE NAME");
        if (quote) {
            type = props.getProp("QUOTE NAME");
        }
        NewEmail email = new NewEmail();
        email.setAttachment(file);
        email.setRecipent(toAddress);
        email.setText(type + " from " + props.getProp("CO NAME"));
        email.setSubject(type + " number: " + numberField.getText() + " From " + props.getProp("CO NAME"));
        email.setFrom(props.getProp("EMAIL ADDRESS"));
        email.setServer(props.getProp("EMAIL SERVER"));
        email.setPort(props.getProp("EMAIL PORT"));
        email.setUsername(props.getProp("EMAIL USER"));
        email.setPassword(props.getProp("EMAIL PWD"));
        email.setSSL(DV.parseBool(props.getProp("SSL"), false));
        email.sendEmail();

    }

    private void postButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postButtonActionPerformed

        post();

    }//GEN-LAST:event_postButtonActionPerformed


    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed

        if (invTable.getRowCount() > 0) {

            if (viewPrint) {

                getInvoice(invoiceKey);
            }

            print(quote);
        }

    }//GEN-LAST:event_printButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed

        closeInvoiceWindow();

    }//GEN-LAST:event_closeButtonActionPerformed

    private void closeInvoiceWindow() {

        if (hold) {
            _void = true;
            postInvoice(false);

        }

        this.dispose();

    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        saveQuote();

    }//GEN-LAST:event_saveButtonActionPerformed

    private void saveQuote() {

        if (invTable.getRowCount() < 1) {
            return;
        }
        String qmsg = "this as a quote?";
        if (savedQuote) {
            qmsg = "this quote?";
        }
        int a = javax.swing.JOptionPane.showConfirmDialog(null,
                "Do you want to save " + qmsg, "Save Quote", JOptionPane.YES_NO_OPTION);
        if (a == 0) {
        } else {
            return;
        }
        boolean good = true;
        // get next quote number

        good = setQuoteNumber();

        /* convert all the data from invoice object to quote object  */
        computePrices();

        /*this method check and displays warnings */
        if (!good) {
            return;
        }
        if (debug) {
            System.out.println("Entering InvoiceDialog saveQuote(), inv num ok <>");
        }
        theQuote.setDate(new Long(datePicker1.getDate().getTime()));  //Version 1.5

        theQuote.setCustomer(custTextArea.getText());
        theQuote.setShipToAddress(shipToTextArea.getText());
        theQuote.setShipping(shipping);

        theQuote.setMessage(invoiceMessage);

        theQuote.setTaxes(taxRate1, taxRate2);

        //theQuote.setPaid(false); //paid
        theQuote.setVoid(_void); //void?
        theQuote.setCustKey(cust_key);

        theQuote.setItemModel(tm);

        quoteKey = theQuote.saveQuote();
        this.incrQuote();

        if (quoteKey < 1) {
            if (debug) {
                System.out.println("<><> Quote post failed!! <><>");
            }
            return;
        }

        //get items
        // print to pdf
        this.savedQuote = true;
        quote = true;
        this.print(true);  //prints a quote from this invoice
        // save data

        this.dispose();

    }

    private void setTaxRates(float tax1, float tax2) {

        taxRate1 = tax1;
        taxRate2 = tax2;

    }

    /**
     * This is called once by contructor(s).
     */
    private void setInvoiceNumber() {

        //ArrayList al;
        nextAutoInvoiceNumber = props.getProp("NEXT NUMBER");

        /* Check for any possible duplicate invoice numbers */
        //al = db.search("invoice", 1, invoicePrefix + props.getProp("NEXT NUMBER"), false);
        if (invoice.checkInvoiceNumber(invoicePrefix + props.getProp("NEXT NUMBER"))) {
            numberField.setText(invoicePrefix + props.getProp("NEXT NUMBER"));
        } else {
            /* If we happen to find a duplicate */
            while (true) {  //Increase the next invoice number until no duplicate is found.

                incrInvoice();

                if (invoice.checkInvoiceNumber(invoicePrefix + props.getProp("NEXT NUMBER"))) {

                    numberField.setText(invoicePrefix + props.getProp("NEXT NUMBER"));
                    break;

                }

            }

        }

    }

    private void incrInvoice() {

        if (!DV.validIntString(props.getProp("NEXT NUMBER"))) {

            javax.swing.JOptionPane.showMessageDialog(null, " Problem accessing inumber, settings.ini is corrupt or missing!  You need to close Nevitium now. ");

        } else {

            if (debug) {
                System.out.println("Incrementing the invoice number.");
            }
            int inum = Integer.parseInt(props.getProp("NEXT NUMBER").trim());
            inum++;
            props.setProp("NEXT NUMBER", Integer.toString(inum));

        }

    }

    private boolean setQuoteNumber() {

        if (converted) {
            theQuote = new Quote(db);
            savedQuote = false;
        }

        if (savedQuote) {

            theQuote.setQuoteNumber(numberField.getText(), false);

            return true;
        }

        //ArrayList al;
        nextAutoQuoteNumber = quotePrefix + props.getProp("NEXT QUOTE");

        /* Check for any possible duplicate invoice numbers */
        //al = db.search("quote", 1, quotePrefix + props.getProp("NEXT QUOTE"), false);
        if (theQuote.checkQuoteNumber(quotePrefix + props.getProp("NEXT QUOTE"), true)) {
            numberField.setText(quotePrefix + props.getProp("NEXT QUOTE"));

            theQuote.setQuoteNumber(numberField.getText(), true);
            return true;
        } else {
            /* If we happen to find a duplicate */
            while (true) {  //Increase the next invoice number until no duplicate is found.

                incrQuote();

                if (theQuote.checkQuoteNumber(quotePrefix + props.getProp("NEXT QUOTE"), true)) {

                    numberField.setText(quotePrefix + props.getProp("NEXT QUOTE"));
                    theQuote.setQuoteNumber(numberField.getText(), true);
                    break;

                }

            }
            return true;
        }

    }

    private void incrQuote() {

        if (!DV.validIntString(props.getProp("NEXT QUOTE"))) {

            javax.swing.JOptionPane.showMessageDialog(null, " Problem accessing qnumber, settings.ini is corrupt or missing!  You need to close Nevitium now. ");

        } else {

            if (debug) {
                System.out.println("Incrementing the quote number.");
            }
            int inum = Integer.parseInt(props.getProp("NEXT QUOTE").trim());
            inum++;
            props.setProp("NEXT QUOTE", Integer.toString(inum));

        }

    }

    private boolean verifyInvoiceNumber(String invoiceNumber) {
        //TODO: am I allowing duplicates?
        invoiceNumber = invoiceNumber.trim();

        return invoice.setInvoiceNumber(invoiceNumber);

    }
    private String lastRecInvNum = "";

    private boolean postInvoice(boolean payment) {
        if (debug) {
            System.out.println("Entering InvoiceDialog postInvoice() <>");
        }

        /* ?create a transaction system in case this fails at any point? */
        computePrices();

        /*this method check and displays warnings */
        boolean good = invoice.setInvoiceNumber(numberField.getText().trim());
        if (!good) {
            return false;
        }
        if (debug) {
            System.out.println("Entering InvoiceDialog postInvoice(), inv num ok <>");
        }
        invoice.setDate(new Long(datePicker1.getDate().getTime()));  //Version 1.5

        invoice.setCustomer(custTextArea.getText());
        invoice.setShipToAddress(shipToTextArea.getText());

        invoice.setShipping(shipping);

        invoice.setMessage(invoiceMessage);

        invoice.setTaxes(taxRate1, taxRate2);

        invoice.setPaid(false); //paid
        invoice.setVoid(_void); //void?
        invoice.setCustKey(cust_key);

        invoiceKey = invoice.postInvoice();

        if (invoiceKey < 1) {
            if (debug) {
                System.out.println("<><> Invoice post failed!! <><>");
            }
            return false;
        }

        lastRecInvNum = invoice.getInvoiceNumber();
        if (debug) {
            System.out.println("Last recorded invoice number: " + lastRecInvNum);
        }

        //invoice = null;
        if (payment) {

            PaymentDialog pd = new PaymentDialog(parentWin, true, invoiceKey, application);
            pd.setVisible(true);
            invoice = new OldInvoice(application, invoiceKey);
            //paidCheckBox.setSelected(invoice.isPaid());
        }

        /* delete quote? */
        if (removeQuote && quoteToRemove > 0) {

            Quote aQuote = new Quote(db, quoteToRemove);
            int tmpKey;

            // delete quote items
            DefaultTableModel items = aQuote.getItems();
            if (debug) {
                System.out.println("Trying to remove qitems: " + items.getRowCount());
            }
            for (int r = 0; r < items.getRowCount(); r++) {

                tmpKey = (Integer) items.getValueAt(r, 0);
                db.removeRecord("qitems", tmpKey);

            }

            db.removeRecord("quote", quoteToRemove);

            // delete quote shipto
            tmpKey = aQuote.getShipToKey();
            if (tmpKey > 0) {
                db.removeRecord("qshipto", tmpKey);
            }
            aQuote = null;
        }//end quote delete


        /* ?END TRANSACTION */
        return true;
    }

    private void calcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcButtonActionPerformed
        if (invTable.getRowCount() < 1) {
            return;
        }

        if (!viewPrint) {
            computePrices();
        }

    }//GEN-LAST:event_calcButtonActionPerformed

    private void invTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invTableMouseClicked

        int mouseButton = evt.getButton();
        if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) {
            return;
        }
        if (evt.getClickCount() == 2) {
            if (viewPrint) {
                return;
            }
            //get current row
            //check for misc
            //if misc then open for edit
            //return
            //remove current row
            //insert new row at previous position

            int selectedRow = invTable.getSelectedRow();

            Object[] modRow = DV.getRow(tm, selectedRow);
            if (((String) modRow[3]).equals("MISC")) {
                MiscItemDialog mi = new MiscItemDialog(null, true, application, modRow);//send the misc item to edit

                if (mi.getStat()) {
                    //DV.expose(mi.getItem());
                    addItem(mi.getItem(), false, selectedRow, -1); //this call to additem replaces the row at selectedRow
                }

                mi.dispose();
            }

        }

        if (!viewPrint) {
            computePrices();
        }

    }//GEN-LAST:event_invTableMouseClicked

    private void removeRows() {
        if (invTable.getRowCount() < 1) {
            return;
        }
        int[] rows = invTable.getSelectedRows();
        int c = 0;
        int zkey = 0;

        if (rows != null && rows.length > 0) {

            for (int r = 0; r < rows.length; r++) {

                if (quote) {
                    /* get the quote item key to remove */
                    zkey = (Integer) tm.getValueAt(rows[r], 0);
                    theQuote.removeItem(zkey);
                }
                tm.removeRow(rows[r] - c);
                c++;
            }
            computePrices();
            upcField.requestFocus();
        }

    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed

        removeRows();

    }//GEN-LAST:event_removeButtonActionPerformed

    private void messageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageButtonActionPerformed

        NoteDialog nd = new NoteDialog(parentWin, true, application, true);
        nd.setVisible(true);
        String m = nd.getReturnValue();
        if (m.length() > 1) {
            invoiceMessage = nd.getReturnValue();
            messageButton.setToolTipText(invoiceMessage);

        }
        nd.dispose();
    }//GEN-LAST:event_messageButtonActionPerformed

    private void shipToAction() {

        if (this.currentInvoice.getCustomerId() != null) {

            int a = javax.swing.JOptionPane.showConfirmDialog(null,
                    "Would you like to select from this customer's shipping addresses? ",
                    "Shipping Address Option", JOptionPane.YES_NO_OPTION);
            if (a == JOptionPane.YES_OPTION) {

                var shippingDialog
                        = new ContactShippingDialog(parentWin, true, this.currentInvoice.getCustomerId(), true, application);
                shippingDialog.display();

                var address = shippingDialog.getSelectedAddress();

                if (address != null) {

                    String[] ship = Tools.formatAddress(address);

                    shipToTextArea.setText("");

                    for (int i = 0; i < ship.length; i++) {
                        shipToTextArea.append((String) ship[i]);
                    }

                    //if (shipToKey > 0) shipToTextArea.setEditable(false);
                    invoice.setShipToAddress(shipToTextArea.getText());
                    theQuote.setShipToAddress(shipToTextArea.getText());
                }
                return;
            }
        }

        // open MyConnectionsApp and get ANY address
        ContactsApp cd = new ContactsApp(this.parentWin, true, application,
                true, true, false);

        var shippingContact = cd.getReturnValue();

        if (shippingContact == null) {
            return;
        }
        shipToTextArea.setText("");

        if (shippingContact != null) {

            String[] cust = formatAddress(shippingContact, false);

            shipToTextArea.setText("");
            for (int i = 0; i < cust.length; i++) {
                shipToTextArea.append((String) cust[i]);

            }
            shipToTextArea.setEditable(false);

        } else {
            shipToTextArea.setEditable(true);
        }

        invoice.setShipToAddress(shipToTextArea.getText());
        theQuote.setShipToAddress(shipToTextArea.getText());

        cd.dispose(); //dont call dispose before finsihing with method
        cd = null;

    }

    private void custAction() {

        ContactsApp cd
                = new ContactsApp(this.parentWin, true, application, true, true, false);

        var contact = cd.getReturnValue();  //real value

        if (contact == null) {
            return;
        }

        custTextArea.setText("");

        this.customer = contact;
        this.currentInvoice.setCustomerId(contact.getId());

        cd.dispose();
        cd = null; 

        if (contact != null) {

            String[] cust = formatAddress(contact, true);

            shipToButton.setEnabled(true);

            for (int i = 0; i < cust.length; i++) {
                custTextArea.append((String) cust[i]);
            }
            custTextArea.setEditable(false);
        } else {
            cust_key = 0;
            custTextArea.setText("S A L E");
            custTextArea.setEditable(true);
            custTax1 = true;
            custTax2 = true;
        }
        
        

        upcField.requestFocus();
    }

    private String[] formatAddress(Contact contact, boolean billTo) {

        String[] address = Tools.formatAddress(contact);

        if (billTo) {
            custTax1 = contact.isTaxable1();
            custTax2 = contact.isTaxable2();
        }

        return address;
    }


    private void custButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_custButtonActionPerformed

        custAction();

    }//GEN-LAST:event_custButtonActionPerformed

    private void convertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertButtonActionPerformed

        convertToInvoice();

    }//GEN-LAST:event_convertButtonActionPerformed

    private void copyBillToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBillToButtonActionPerformed
        if (invoice.getCustKey() > 0) {
            shipToTextArea.setText(invoice.getCustomer());
            invoice.setShipToAddress(shipToTextArea.getText());
        }

        if (theQuote.getCustKey() > 0) {
            shipToTextArea.setText(theQuote.getCustomer());
            theQuote.setShipToAddress(shipToTextArea.getText());
        }

        //shipToTextArea.setEditable(false);

    }//GEN-LAST:event_copyBillToButtonActionPerformed

    private void clearShipToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearShipToButtonActionPerformed

        shipToTextArea.setText("");
        shipToTextArea.setEditable(true);

    }//GEN-LAST:event_clearShipToButtonActionPerformed

    private void paymentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentButtonActionPerformed
        doPayment();
    }//GEN-LAST:event_paymentButtonActionPerformed

    private void statementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statementButtonActionPerformed
        doStatement();
    }//GEN-LAST:event_statementButtonActionPerformed

    private void historyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historyButtonActionPerformed
        doHistory();
    }//GEN-LAST:event_historyButtonActionPerformed

    private void qtyTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_qtyTextFieldFocusGained
        qtyTextField.selectAll();
    }//GEN-LAST:event_qtyTextFieldFocusGained

    private void upcFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_upcFieldFocusGained
        upcField.selectAll();
    }//GEN-LAST:event_upcFieldFocusGained

    private void upcFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upcFieldMouseClicked
        if (evt.getClickCount() == 2) {
            scanAction();
        }
    }//GEN-LAST:event_upcFieldMouseClicked

    private void VATButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VATButtonActionPerformed
        new VATCalculator(parentWin, true, this.taxRate1);
    }//GEN-LAST:event_VATButtonActionPerformed

    private void printReceiptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printReceiptButtonActionPerformed
        this.printReciept();
    }//GEN-LAST:event_printReceiptButtonActionPerformed

private void datePicker1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datePicker1PropertyChange
    Calendar gc = new GregorianCalendar();
    gc.setTime(datePicker1.getDate());
    //dueDateChooser.setCurrent(gc);// TODO add your handling code here:
}//GEN-LAST:event_datePicker1PropertyChange

    private void upcFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_upcFieldKeyReleased
        if (upcField.getText().equals("+")) {
            String num = qtyTextField.getText();
            float q = DV.parseFloat(num);
            q++;
            qtyTextField.setText(DV.money(q));
            upcField.setText("");
            upcField.requestFocus();

        }

        if (upcField.getText().equals("-")) {
            String num = qtyTextField.getText();
            float q = DV.parseFloat(num);
            if (q > 1) {
                q--;
            }
            qtyTextField.setText(DV.money(q));
            upcField.setText("");
            upcField.requestFocus();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_upcFieldKeyReleased

    private void upcComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upcComboActionPerformed
        upcField.requestFocus();
    }//GEN-LAST:event_upcComboActionPerformed

    private void discountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountButtonActionPerformed
        //cycle thru selected items and total prices
        //if one do something with the desc in the discdialog?
        //erect discdialog
        //resume from dialog and apply new item

        if (invTable.getSelectedRows() == null || invTable.getSelectedRow() < 0) {

            //display a warning dialog, JIDE???
            return;
        }

        int[] selectedRows = invTable.getSelectedRows();
        float tot = 0.00f;
        boolean tx1 = false;
        boolean tx2 = false;
        for (int r = 0; r < selectedRows.length; r++) {
            tot += ((Float) invTable.getModel().getValueAt(selectedRows[r], 8)); //add the totals of the selected items
            if ((Boolean) invTable.getModel().getValueAt(selectedRows[r], 7)) {
                tx2 = true;
            }
            if ((Boolean) invTable.getModel().getValueAt(selectedRows[r], 6)) {
                tx1 = true;
            }
        }

        if (tot <= 0) {
            //gripe
            return;
        }

        DiscountDialog disc = new DiscountDialog(null, true, application, "Discount", tot, tx1, tx2);
        Object[] discountItem;
        if (disc.getStat()) {
            discountItem = disc.getDisc();
        } else {
            return;
        }

        int insert = -1;

        if (selectedRows[selectedRows.length - 1] >= invTable.getRowCount() - 1) {
            insert = -1;
        } else {
            insert = selectedRows[(selectedRows.length - 1)] + 1;
        }//if the last row is not selected make it the insert point

        if (discountItem != null) {
            this.addItem(discountItem, false, -1, insert);
        }

        disc.dispose();

    }//GEN-LAST:event_discountButtonActionPerformed

    private void doPayment() {
        new PaymentActivityDialog(null, true, application, invoice);

    }

    private void doStatement() {

        if (!accessKey.checkInvoice(500)) {
            accessKey.showMessage("Statements");
            return;
        }

        int k = invoice.getInvoiceKey();

        ReportFactory.generateStatements(application, k);

    }

    private void doHistory() {

        if (!accessKey.checkReports(500)) {
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }
      
        if (customer != null) {
            ReportFactory.generateCustomerStatement(application, customer);
        } else {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "This invoice is not assigned to a specific customer.");
        }
    }

    private boolean removeQuote = false;
    private int quoteToRemove;
    private boolean converted = false;

    private void convertToInvoice() {

        saveButton.setEnabled(false);
        /* int a = javax.swing.JOptionPane.showConfirmDialog(null,
                "Do you want to copy this quote to a new invoice?",
                "Convert Quote",  JOptionPane.YES_NO_OPTION);*/

        Object[] options = {"Convert", "Copy", "Cancel"};
        int a = JOptionPane.showOptionDialog(null,
                "Choose 'Convert' if you DO NOT want to keep a copy of this quote." + nl + "Select 'Copy' to preserve this quote for later use." + nl + "If you select 'Convert,' the quote is only deleted if you post the invoice.",
                "Preserve Quote?",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        //Cancel
        if (a == 2) {

            saveButton.setEnabled(true);
            return;
        }
        //Convert
        if (a == 0) {
            removeQuote = true;
            quoteToRemove = theQuote.getQuoteKey();
        }
        //Copy
        if (a == 1) {
            removeQuote = false;
        }

        invoice = new OldInvoice(application);
        invoice.setVAT(VAT);
        invoice.setTaxes(taxRate1, taxRate2);
        this.setInvoiceNumber();

        invoice.setTaxes(taxRate1, taxRate2);

        try {
            datePicker1.setDate(new Date());
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }
        /* must completely repopulate invoice object */

 /* Make sure we have reference to the InvoiceDialog invTable model */
        invoice.setInvoiceDialogModel((DefaultTableModel) invTable.getModel());

        /* Make sure we have a copy of the raw invItems for the quote */
        //invoice.setInvItems(theQuote.getInvItems());
        //tm = invoice.getItems();
        invoice.setCustomer(theQuote.getCustomer());
        invoice.setCustKey(theQuote.getCustKey());

        invoice.setShipToAddress(theQuote.getShipToAddress());

        invoice.setInvoiceNumber(numberField.getText());

        invoice.setMessage(theQuote.getMessage());

        autoInvoiceNumberButton.setVisible(true);
        printButton.setVisible(true);

        this.quote = false;
        this.savedQuote = false;
        converted = true; //this guages next quote number
        convertButton.setVisible(false);
        invoiceNumberEditCheckBox.setEnabled(true);
        printButton.setVisible(false);
        //theQuote = new Quote(db);
        this.computePrices();
        postButton.setEnabled(true);
        upcField.requestFocus();

    }

    private void printCustomInvoice() {

        //load document layout
        //start a new book
        //initiate an InvoicePrintPanel passing in Book and documentLayout
        //cycle through invitems
        /*  get item, see if the item carries an annotation
         *  calculate the space needed to render the item (plus annotation if required)
         *  make sure annotation and item fit on the page
         *  render item and annotaion (subtract available space)
         *  repeat, when out of space, stuff into Book and launch another InvoicePrintPanel?
         *  What about a PrinterJob?
         *
         */
    }

    public String getStat() {
        return stat;
    }
    /* properties */
    OldInvoice invoice;
    Quote theQuote;
    private boolean pos = false;
    private float taxRate1;
    private float taxRate2;
    private boolean VAT = false;
    private String invoicePrefix = "";
    private String quotePrefix = "";
    private float shipping = 0.00f;
    private int cust_key = 0;
    private int invoiceKey = 0;
    private int quoteKey = 0;
    private boolean quote = false;
    private boolean savedQuote = false;
    private String workingPath = "";
    private String nextAutoInvoiceNumber = "";
    private String nextAutoQuoteNumber = "";
    private String invoiceMessage = "Thank You!";
    //regular instance vars
    private char dash = '-';
    private javax.swing.table.DefaultTableModel tm;
    private Object[] currentItem;
    private java.awt.Frame parentWin;
    private DbEngine db;
    private Object[] dataOut = new Object[12]; //invoice detail
    private InvoiceModel im = new InvoiceModel();
    private boolean hold = false;
    private boolean _void = false;
    private Settings props;
    private String nl = System.getProperty("line.separator");
    private String stat = "";
    private boolean viewPrint = false;

    private boolean custTax1 = true;
    private boolean custTax2 = true;
    private java.awt.Image winIcon;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton VATButton;
    private javax.swing.JButton autoInvoiceNumberButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton calcButton;
    private javax.swing.JButton clearShipToButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton convertButton;
    private javax.swing.JButton copyBillToButton;
    private javax.swing.JButton custButton;
    private javax.swing.JTextArea custTextArea;
    private com.michaelbaranov.microba.calendar.DatePicker datePicker1;
    private com.michaelbaranov.microba.calendar.DatePicker datePicker2;
    private javax.swing.JButton discountButton;
    private javax.swing.JTextField grandTotalField;
    private javax.swing.JButton historyButton;
    private javax.swing.JTable invTable;
    private javax.swing.JCheckBox invoiceNumberEditCheckBox;
    private javax.swing.JTextField itemTotalField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JPanel logoPanel;
    private javax.swing.JButton messageButton;
    private javax.swing.JButton miscButton;
    private javax.swing.JTextField numberField;
    private javax.swing.JToolBar optionsToolbar;
    private javax.swing.JButton packingslipButton;
    private javax.swing.JButton paymentButton;
    private javax.swing.JCheckBox paymentCheckBox;
    private javax.swing.JButton postButton;
    private javax.swing.JButton printButton;
    private javax.swing.JButton printReceiptButton;
    private javax.swing.JTextField qtyTextField;
    private javax.swing.JCheckBox receiptCheckBox;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton shipToButton;
    private javax.swing.JTextArea shipToTextArea;
    private javax.swing.JButton shippingButton;
    private javax.swing.JButton statementButton;
    private javax.swing.JTextField t1Field;
    private javax.swing.JLabel t1Label;
    private javax.swing.JTextField t2Field;
    private javax.swing.JLabel t2Label;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JComboBox upcCombo;
    private javax.swing.JTextField upcField;
    private javax.swing.JButton viewReturnsButton;
    // End of variables declaration//GEN-END:variables

}
