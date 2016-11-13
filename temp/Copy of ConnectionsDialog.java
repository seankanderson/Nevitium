/*
 * ConnectionsDialog.java
 *
 * Created on June 22, 2006, 10:08 AM
 ** Copyright (c) Data Virtue 2006
 */

package businessmanager.Connections;
import businessmanager.Common.TableView;
import businessmanager.Common.LimitedDocument;
import businessmanager.InvoiceQuote.PaymentDialog;
import businessmanager.Reports.ReportModel;
import businessmanager.InvoiceQuote.InvoiceDialog;
import businessmanager.*;
import businessmanager.Common.Tools;
import businessmanager.InvoiceQuote.InvoiceDialogSmall;
import businessmanager.Reports.PurchaseHistoryReport;
import businessmanager.Reports.ReportFactory;
import businessmanager.Reports.ReportTableDialog;
import datavirtue.*;
import javax.swing.table.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.*;


import java.awt.*;
import java.net.URI;
/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2008, 2009, 2010 All Rights Reserved.
 */
public class ConnectionsDialog extends javax.swing.JDialog {
    private KeyCard accessKey;
    private boolean debug = false;
    /** Creates new form ConnectionsDialog */
    public ConnectionsDialog(java.awt.Frame parent, boolean modal, DbEngine dbe,
            boolean select, boolean customers, boolean suppliers, String path, KeyCard ak ) {

        super(parent,modal);
        //super();
        initComponents();

        workingPath = path;
        accessKey = ak;
        setDbEngine (dbe);        
        
        props = new Settings (workingPath + "settings.ini");
        //dont address components before init!!!!

        int c = Tools.getStringInt(props.getProp("CONN COL"), 0);
        searchFieldCombo.setSelectedIndex(c);

        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));

        
        jScrollPane5.getVerticalScrollBar().setUnitIncrement(16);
        jButton1.setVisible(false);


        small = Tools.getStringBool(props.getProp("SMALL SCREEN"));
        if (small){
            jScrollPane5.setPreferredSize(new Dimension(900, 200));
            this.setPreferredSize(new Dimension(1067, 550));
            this.pack();
        }

        /* Limit chars availble in textfields */
        
        companyTextField.setDocument(new LimitedDocument(35));
        firstTextField.setDocument(new LimitedDocument(20));
        lastTextField.setDocument(new LimitedDocument(20));
        addressTextField.setDocument(new LimitedDocument(40));
        suiteTextField.setDocument(new LimitedDocument(40));
        cityTextField.setDocument(new LimitedDocument(30));
        stateTextField.setDocument(new LimitedDocument(20));
        zipTextField.setDocument(new LimitedDocument(10));
        contactTextField.setDocument(new LimitedDocument(20));
        phoneTextField.setDocument(new LimitedDocument(20));
        faxTextField.setDocument(new LimitedDocument(20));
        emailTextField.setDocument(new LimitedDocument(40));
        wwwTextField.setDocument(new LimitedDocument(50));
        notesTextArea.setDocument(new LimitedDocument(100));
       
        
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
        /* End Close Dialog on Escape*/
                
        parentWin = parent;
        
        fileList.setModel(lm);
        
        if (customers && !suppliers) custRadio.setSelected(true);
        if (suppliers && !customers) suppRadio.setSelected(true);
        if (customers && suppliers) allRadio.setSelected(true);

        connDAO = new ConnectionsDAO(db);
        edit_key = connDAO.getKey();
        
        connTable.setModel(filter());
     
        setView (vals);
        selectMode = select;
        
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, 1);
        
        if (select) {
            saveButton.setVisible(true);
            saveButton.setEnabled(false);
            selectButton.setVisible(true);
            voidButton.setVisible(true);
            setFieldsEnabled(false);
            
        }else {
            saveButton.setVisible(true);
            saveButton.setEnabled(false);
            selectButton.setVisible(false);
            voidButton.setVisible(false);
            setFieldsEnabled(false);
            }
        
        findField.requestFocus();
        
        zip = new DbEngine();
        zip.loadSchema("zip.sch");

        String tax1name = props.getProp("TAX1NAME");
        String tax2name = props.getProp("TAX2NAME");
        tax1CheckBox.setText(tax1name);
        tax2CheckBox.setText(tax2name);
        connTable.setSelectionForeground(new java.awt.Color (0,0,0));
        
    }
    
    private String workingPath = "";
    
    private TableModel filter () {
        
        if (allRadio.isSelected()) {
            return connDAO.getMyConnectionsTable(connTable);           
        }        
        if (custRadio.isSelected()) {
            return connDAO.getCustomerTable(connTable);            
        }        
        if (suppRadio.isSelected()) {         
            return connDAO.getVendorTable(connTable);            
        }        
        return connDAO.getMyConnectionsTable(connTable);
    }
    
    //JOptionPane
    public int getReturnValue () {
        
        return returnValue;        
    }
    
    private void clearFields () {
       
        
         
         edit_key = 0;

         connDAO = new ConnectionsDAO(db);

         String zone = props.getProp("ADDRESS STYLE");
         keyLabel.setText(Integer.toString(edit_key));  //show the user the key for the record
         
         populateInvoices(false);
         
        companyTextField.setText("");
        firstTextField.setText("");
        lastTextField.setText("");
        addressTextField.setText("");
        suiteTextField.setText("");
        cityTextField.setText("");
        stateTextField.setText("");
        zipTextField.setText("");
        countryCombo.setSelectedItem(zone);
        contactTextField.setText("");
        phoneTextField.setText("");
        faxTextField.setText("");
        emailTextField.setText("");
        wwwTextField.setText("http://");
        notesTextArea.setText("");
        custCheckBox.setSelected(false);
        supplierCheckBox.setSelected(false);
        tax1CheckBox.setSelected(false);
        tax2CheckBox.setSelected(false);

        fileList.setModel(new javax.swing.DefaultListModel());
    
        journalTextArea.setText("");
    
    }
    
    
    private void setFieldsEnabled (boolean enabled) {
        
    companyTextField.setEnabled(enabled);
    firstTextField.setEnabled(enabled);
    lastTextField.setEnabled(enabled);
    addressTextField.setEnabled(enabled);
    suiteTextField.setEnabled(enabled);
    cityTextField.setEnabled(enabled);
    stateTextField.setEnabled(enabled);
    zipTextField.setEnabled(enabled);
    countryCombo.setEnabled(enabled);
    contactTextField.setEnabled(enabled);
    phoneTextField.setEnabled(enabled);
    faxTextField.setEnabled(enabled);
    emailTextField.setEnabled(enabled);
    wwwTextField.setEnabled(enabled);
    notesTextArea.setEnabled(enabled);
    custCheckBox.setEnabled(enabled);
    supplierCheckBox.setEnabled(enabled);
    tax1CheckBox.setEnabled(enabled);
    tax2CheckBox.setEnabled(enabled);
    shipToButton.setEnabled(enabled);    
    zipButton.setEnabled(enabled);    
    saveButton.setEnabled(enabled);   
    viewButton.setEnabled(enabled);    
    fileList.setEnabled(enabled);    
    journalTextArea.setEnabled(enabled);
    
    if (edit_key != 0 ) newButton.setEnabled(enabled);
    else {       
        newButton.setEnabled(false);        
    }
    
    if (enabled){            
            messageField.setText("Remember to click 'Save' when you modify a record.");
        }else {
        
        messageField.setText("Click the Company Field to start a new record.");
        
        }
    
    }
    
    private void populateFields () {
        
        if (connTable.getSelectedRow() > -1) {  
                int key = (Integer) connTable.getModel().getValueAt(connTable.getSelectedRow(), 0);
                connDAO = new ConnectionsDAO(db, key);
                edit_key = connDAO.getKey();
                
            }else return;

            companyTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 1));
            firstTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 2));
            lastTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 3));
            addressTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 4));
            suiteTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 5));
            cityTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 6));
            stateTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 7));
            zipTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 8));
            countryCombo.setSelectedItem((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 17));
            contactTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 9));
            phoneTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 10));
            faxTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 11));
            emailTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 12));
            wwwTextField.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 13));
            notesTextArea.setText((String) connTable.getModel().getValueAt(connTable.getSelectedRow(), 14));
            custCheckBox.setSelected((Boolean) connTable.getModel().getValueAt(connTable.getSelectedRow(), 15));
            supplierCheckBox.setSelected((Boolean) connTable.getModel().getValueAt(connTable.getSelectedRow(), 16));
            tax1CheckBox.setSelected((Boolean) connTable.getModel().getValueAt(connTable.getSelectedRow(), 18));
            tax2CheckBox.setSelected((Boolean) connTable.getModel().getValueAt(connTable.getSelectedRow(), 19));

            //Set active field based on supplier

            //activeCheckBox.setEnabled( supplierCheckBox.isSelected() );
            populateInvoices(false);
            populateJournals();
            
            keyLabel.setText(Integer.toString(edit_key));  //show the user the key for the record
            this.setFieldsEnabled(true);

    }
    
   private void populateInvoices (boolean change) {

       if (connTable.getSelectedRow() < 0) return;

       /* Determin the table we wil work from, quotes or invoices */
       String table = "invoice";
       
       if (invoiceToggleButton.getText().endsWith("Quotes")){
           if (change){
           table = "quote";
           invoiceLabel.setText("Quotes ");
           
           invoiceTable.setToolTipText("Quotes found for this contact");
           invoiceToggleButton.setText("Show Invoices");
           }else table = "invoice";
       }else{
           if (change){
           table = "invoice";
           invoiceLabel.setText("Invoices ");
           
           invoiceTable.setToolTipText("Invoices found for this contact");
           invoiceToggleButton.setText("Show Quotes");
           }else table = "quote";           
       }
       
       int key = (Integer)connTable.getModel().getValueAt(connTable.getSelectedRow(), 0);
       
       /* the custom TableModel is assigned to the My Connections invoiceTable */
       invoiceTable.setModel(connDAO.getInvoiceTableModel(table, key));
       
       /* remove key field - clean up*/
       setView();
              
   }
    
   private void setView () {
       
       TableColumnModel cm = invoiceTable.getColumnModel();
        TableColumn tc;
        invoiceTable.setSelectionForeground(Color.BLACK);
        
        if (invoiceTable.getColumnCount() > 2) {
                        
            //setup hold table view
            tc = cm.getColumn(0);
            invoiceTable.removeColumn(tc);//remove key column
        }
       
        tc = invoiceTable.getColumnModel().getColumn(0);
        tc.setPreferredWidth(90);
        tc = invoiceTable.getColumnModel().getColumn(1);
        tc.setPreferredWidth(40);
       
   } 
   
   
  
    
    public void setView (int [] cols){
        
        if (connTable.getModel().getRowCount() > 0){
        TableColumnModel cm = connTable.getColumnModel();
        TableColumn tc;
        //connTable.setCellEditor(null);
        
        for (int i =0; i < cols.length; i++){            
            tc = cm.getColumn(cols[i]);
            connTable.removeColumn(tc);            
        }
        
        int a = connTable.getColumnCount();
        javax.swing.JTextField tf = new javax.swing.JTextField();
        tf.setEditable(false);
        for (int i=0; i < a; i++){
            
            cm.getColumn(i).setCellEditor(new javax.swing.DefaultCellEditor(tf));
        }
        
        }
        
    }
    
    public void setDbEngine (DbEngine dbe){
        
        db = dbe;
        
    }
        
    private void find () {
        
        if (!findField.getText().equals("")){
            
            searchColumn = searchFieldCombo.getSelectedIndex()+1;
        
            ArrayList al = connDAO.search(searchColumn, findField.getText());
            
            if (al != null){
                
                connTable.setModel(connDAO.getSearchResultTable(al, true));
                setView(vals);
                /* remember search col */
            
                props.setProp("CONN COL", Integer.toString(searchColumn-1));

            }else JOptionPane.showMessageDialog(this, "No matching records were found.","Find Failed",  JOptionPane.OK_OPTION);
                        
        }else refreshTable();
        
    }
    
    private void export (String filename) {
        
        //System.out.println(filename);
        ReportModel rm = new ReportModel(connTable.getModel());
        StringBuilder sb = new StringBuilder();
        int col_count = connTable.getModel().getColumnCount();
        
        /* Headers  */
            if (!new File(filename).exists()){  
                
                String [] headers = db.getFieldNames("conn");
                
                for (int i = 0; i < headers.length; i++){
                    
                    sb.append(headers[i]);
                    if (i < headers.length - 1) sb.append(',');
            
             }
        
             sb.append(System.getProperty("line.separator"));
        
        }
        
          /* Data  */    
        do {
            
            for (int c = 0; c < col_count ; c++){
                
                sb.append(rm.getValueAt(c).replace(',',';'));
                if (c < connTable.getModel().getColumnCount()-1) sb.append(',');
                
            }
            
            sb.append(System.getProperty("line.separator"));
            
        }while (rm.next()); 
        
        DV.writeFile(filename, sb.toString(), true);
        
    }
    
    private void newJournal () {
        
        String date = DV.getShortDate().replace('/', '-');
        String tmp="";
        int elements = lm.getSize();
        
        boolean match=false;
        
        for (int e = 0; e < elements; e++){
            
            tmp = (String)lm.getElementAt(e);
            
            if (tmp.equals(date) ) match = true;            
            
        }
        
        if (!match  && edit_key != 0) {
            
            File jFile = new File(workingPath + "jrnls/"+Integer.toString(edit_key)+"/");
            
            if (!jFile.exists()) jFile.mkdirs();
            
            DV.writeFile(jFile.toString() + "/" + date, DV.getFullDate(), false);
            lm.insertElementAt(date, 0);
                       
        }
        
    }
    
    private void saveJournal () {
        
       int idx = fileList.getSelectedIndex();
       fileList.setEnabled(true);
       
        String text = journalTextArea.getText();
       
        if (!text.equals("") && idx > -1){
            
            DV.writeFile(workingPath + "jrnls/"+Integer.toString(edit_key) + "/" + (String) lm.getElementAt(idx), text, false);
            
            
        }else journalTextArea.setText("");
            
            
        
    }
    
    
    private void getJournal () {
        
        
        int sel = fileList.getSelectedIndex();
        
        if (sel > -1){            
            
            String file = (String) lm.getElementAt(sel);
            
            journalTextArea.setText(DV.readFile(workingPath + "jrnls/" + Integer.toString(edit_key) + "/" + file ));
            
        }
        
    }
    
    
     public static void launch (String com, String target) {
     
     
        String osName = System.getProperty("os.name" );
            
            try {
                
                if(osName.contains("Windows")){
                //Runtime.getRuntime().exec('"' + acro + '"' + " " + file.replace('/','\\'));
                   
                    String [] cm = {"cmd.exe", com + target};
                    Runtime.getRuntime().exec(cm);
                    
                }
                //FOR WINDOWS NT/XP/2000 USE CMD.EXE
                else {
                    
                    Runtime.getRuntime().exec(com + target);
                   //System.out.println("cmd.exe start " + '"' + "c:\\Program Files\\Adobe\\Acrobat*\\Acrobat\\acrobat " + file.replace('/','\\') + '"');
                } 
            } catch (IOException ex) {
                ex.printStackTrace();
            }
     
     
     
 }   
    
    
    private void zipAction() {
        
      
        java.util.ArrayList al = null;
        
        if (zipTextField.getText().length() > 4 && zipTextField.getText().length() < 6  && DV.validIntString(zipTextField.getText()) ) {
        
            al = zip.searchFast("zip", 1, zipTextField.getText(), false );
            
        }
                
        if (al != null){
            Object [] zipinfo = new Object [6];            
            zipinfo = zip.getRecord("zip", (Long) al.get(0));            
            cityTextField.setText((String) zipinfo[2]);
            stateTextField.setText((String) zipinfo[3]);
        }
        
    }
    
    private void populateJournals () {
        
        lm = new javax.swing.DefaultListModel();
        
        
        journalTextArea.setText("");
        
        String path = workingPath + "jrnls/" + Integer.toString(edit_key) + "/";
        
        File dir = new File (path);
        if (!dir.exists()) dir.mkdirs();
        
        String [] files = dir.list();
        
        for (int i = files.length-1; i > -1; i--){
        
            lm.addElement(files[i]);
            
        }
        if (files.length < 1) journalTextArea.setEnabled(false);
        
        fileList.setModel(lm);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        connTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        findField = new javax.swing.JTextField();
        deleteButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        searchFieldCombo = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        allRadio = new javax.swing.JRadioButton();
        custRadio = new javax.swing.JRadioButton();
        suppRadio = new javax.swing.JRadioButton();
        jLabel16 = new javax.swing.JLabel();
        selectButton = new javax.swing.JButton();
        voidButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        toggleButton = new javax.swing.JButton();
        labelButton = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        addressPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        companyTextField = new javax.swing.JTextField();
        firstTextField = new javax.swing.JTextField();
        lastTextField = new javax.swing.JTextField();
        addressTextField = new javax.swing.JTextField();
        suiteTextField = new javax.swing.JTextField();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        custCheckBox = new javax.swing.JCheckBox();
        supplierCheckBox = new javax.swing.JCheckBox();
        keyLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        zipButton = new javax.swing.JButton();
        zipTextField = new javax.swing.JTextField();
        stateTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cityTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        countryCombo = new javax.swing.JComboBox();
        shipToButton = new javax.swing.JButton();
        messageField = new javax.swing.JTextField();
        tax1CheckBox = new javax.swing.JCheckBox();
        tax2CheckBox = new javax.swing.JCheckBox();
        journalPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        notesTextArea = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList();
        newButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        journalTextArea = new javax.swing.JTextPane();
        jLabel17 = new javax.swing.JLabel();
        contactPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        contactTextField = new javax.swing.JTextField();
        phoneTextField = new javax.swing.JTextField();
        faxTextField = new javax.swing.JTextField();
        emailTextField = new javax.swing.JTextField();
        wwwTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        invoiceTable = new javax.swing.JTable();
        viewButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        emailButton = new javax.swing.JButton();
        wwwButton = new javax.swing.JButton();
        invoiceToggleButton = new javax.swing.JButton();
        invoiceLabel = new javax.swing.JLabel();
        purchaseHistoryButton = new javax.swing.JButton();
        invoiceReportButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("My Connections");
        setIconImage(winIcon);
        setModal(true);
        setResizable(false);

        connTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        connTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        connTable.setSelectionBackground(new java.awt.Color(204, 255, 255));
        connTable.setSelectionForeground(new java.awt.Color(0, 51, 51));
        connTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                connTableMouseClicked(evt);
            }
        });
        connTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                connTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                connTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(connTable);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        findField.setToolTipText("Input Search Text Here and Hit ENTER to Search");
        findField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                findFieldFocusGained(evt);
            }
        });
        findField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                findFieldKeyPressed(evt);
            }
        });

        deleteButton.setFont(new java.awt.Font("Tahoma", 1, 13));
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/del.gif"))); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Permenant Delete!");
        deleteButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/magGlass.png"))); // NOI18N

        searchFieldCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Company", "First", "Last", "Address", "Addr #2", "City/Town", "State/Prov/Region", "Post Code", "Contact", "Phone", "Fax", "Email", "WWW", "Misc" }));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel15)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(searchFieldCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(findField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(477, 477, 477)
                        .add(deleteButton)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel15)
                        .add(searchFieldCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(findField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(allRadio);
        allRadio.setSelected(true);
        allRadio.setText("All Records");
        allRadio.setToolTipText("Click this to show all Business Connections");
        allRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        allRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        allRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allRadioActionPerformed(evt);
            }
        });

        buttonGroup1.add(custRadio);
        custRadio.setText("Customers");
        custRadio.setToolTipText("Click for Customers ONLY");
        custRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        custRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        custRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                custRadioActionPerformed(evt);
            }
        });

        buttonGroup1.add(suppRadio);
        suppRadio.setText("Suppliers");
        suppRadio.setToolTipText("Click for Vendors ONLY");
        suppRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        suppRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        suppRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suppRadioActionPerformed(evt);
            }
        });

        jLabel16.setText("Filter:");

        selectButton.setFont(new java.awt.Font("Tahoma", 0, 13));
        selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/ok.gif"))); // NOI18N
        selectButton.setText("Select");
        selectButton.setToolTipText("Returns the Selected Row & Closes the Window");
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        voidButton.setFont(new java.awt.Font("Tahoma", 0, 13));
        voidButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/cancel.gif"))); // NOI18N
        voidButton.setText("None");
        voidButton.setToolTipText("Returns No Selection");
        voidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voidButtonActionPerformed(evt);
            }
        });

        exportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/csv.png"))); // NOI18N
        exportButton.setText("Export");
        exportButton.setToolTipText("Export the Current Table to a (.csv text) File");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        toggleButton.setText("Less");
        toggleButton.setToolTipText("Click this to Toggle the Form On or Off");
        toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonActionPerformed(evt);
            }
        });

        labelButton.setText("Labels");
        labelButton.setToolTipText("Select Rows and Click this Button to Generate Labels");
        labelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(allRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(custRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(suppRadio)
                .add(45, 45, 45)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(selectButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(voidButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 137, Short.MAX_VALUE)
                .add(toggleButton)
                .add(18, 18, 18)
                .add(labelButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(exportButton)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(allRadio)
                    .add(custRadio)
                    .add(suppRadio)
                    .add(voidButton)
                    .add(selectButton)
                    .add(exportButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1)
                    .add(labelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(toggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        addressPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Company");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("First");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Last");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Address");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Addr #2");

        companyTextField.setBackground(new java.awt.Color(255, 255, 204));
        companyTextField.setColumns(35);
        companyTextField.setToolTipText("Click here to Create a New Contact [35 Char] [Company or First Name REQUIRED]");
        companyTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                companyTextFieldMouseClicked(evt);
            }
        });

        firstTextField.setBackground(new java.awt.Color(255, 255, 204));
        firstTextField.setColumns(20);
        firstTextField.setToolTipText("[20 Char] Company or First Name REQUIRED");

        lastTextField.setBackground(new java.awt.Color(255, 255, 204));
        lastTextField.setColumns(20);
        lastTextField.setToolTipText("[20 Char]");

        addressTextField.setColumns(40);
        addressTextField.setToolTipText("[40 Char] 38 Characters is the Suggested Limit for Address Lines");

        suiteTextField.setColumns(10);
        suiteTextField.setToolTipText("[40 Char] 38 Characters is the Suggested Limit for Address Lines");

        clearButton.setFont(new java.awt.Font("Tahoma", 0, 13));
        clearButton.setText("Clear");
        clearButton.setToolTipText("Clears the Form");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        saveButton.setFont(new java.awt.Font("Tahoma", 1, 13));
        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/save.gif"))); // NOI18N
        saveButton.setText("Save");
        saveButton.setToolTipText("Save Modifications");
        saveButton.setMaximumSize(new java.awt.Dimension(79, 25));
        saveButton.setMinimumSize(new java.awt.Dimension(79, 25));
        saveButton.setNextFocusableComponent(companyTextField);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        custCheckBox.setText("Customer");
        custCheckBox.setToolTipText("Marks This Contact as a Customer");
        custCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        custCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        custCheckBox.setNextFocusableComponent(supplierCheckBox);

        supplierCheckBox.setText("Supplier");
        supplierCheckBox.setToolTipText("Marks This Contact as a Supplier");
        supplierCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        supplierCheckBox.setNextFocusableComponent(contactTextField);

        keyLabel.setFont(new java.awt.Font("Tahoma", 0, 10));
        keyLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        keyLabel.setText("ID");
        keyLabel.setEnabled(false);

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        zipButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        zipButton.setText("Post Code");
        zipButton.setToolTipText("Find the city and state");
        zipButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        zipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zipButtonActionPerformed(evt);
            }
        });

        zipTextField.setColumns(10);
        zipTextField.setToolTipText("[10 Char Stored]");
        zipTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                zipTextFieldKeyPressed(evt);
            }
        });

        stateTextField.setColumns(2);
        stateTextField.setToolTipText("[2 Char] Locality");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("State/Prov/Region");

        cityTextField.setColumns(30);
        cityTextField.setToolTipText("[30 Char] Dependent Locality");
        cityTextField.setNextFocusableComponent(stateTextField);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("City/Town");

        jLabel14.setText("Format");

        countryCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "US", "CA", "AU", "GB", "ZA", "IN" }));
        countryCombo.setToolTipText("Sets the Country Code for this Contact");

        shipToButton.setText("Ship To");
        shipToButton.setToolTipText("Create or Modify Shipping Addresses");
        shipToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shipToButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(zipButton)
                        .add(6, 6, 6))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(zipTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel14)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(countryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(55, 55, 55)
                        .add(shipToButton))
                    .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, stateTextField)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, cityTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cityTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(stateTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(zipTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(zipButton)
                    .add(jLabel14)
                    .add(countryCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(shipToButton))
                .addContainerGap())
        );

        messageField.setEditable(false);

        tax1CheckBox.setText("Tax 1");
        tax1CheckBox.setToolTipText("Tax Status");

        tax2CheckBox.setText("Tax 2");
        tax2CheckBox.setToolTipText("Tax Status");

        org.jdesktop.layout.GroupLayout addressPanelLayout = new org.jdesktop.layout.GroupLayout(addressPanel);
        addressPanel.setLayout(addressPanelLayout);
        addressPanelLayout.setHorizontalGroup(
            addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(addressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(messageField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(addressPanelLayout.createSequentialGroup()
                        .add(clearButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 226, Short.MAX_VALUE)
                        .add(saveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(addressPanelLayout.createSequentialGroup()
                        .add(custCheckBox)
                        .add(18, 18, 18)
                        .add(supplierCheckBox)
                        .add(18, 18, 18)
                        .add(tax1CheckBox)
                        .add(18, 18, 18)
                        .add(tax2CheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 144, Short.MAX_VALUE)
                        .add(keyLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(addressPanelLayout.createSequentialGroup()
                        .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(companyTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 255, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(addressTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .add(suiteTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                            .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, lastTextField, 0, 0, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, firstTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        addressPanelLayout.setVerticalGroup(
            addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(addressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(companyTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(firstTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(lastTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(addressTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(suiteTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(keyLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(custCheckBox)
                    .add(supplierCheckBox)
                    .add(tax2CheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tax1CheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(25, 25, 25)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(clearButton)
                    .add(saveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(messageField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        journalPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED, java.awt.Color.white, null));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Misc:");

        notesTextArea.setToolTipText("[100 Character Limit]");
        notesTextArea.setNextFocusableComponent(saveButton);
        notesTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                notesTextAreaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                notesTextAreaFocusLost(evt);
            }
        });

        fileList.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 204)));
        fileList.setToolTipText("Daily Journals");
        fileList.setSelectionBackground(new java.awt.Color(204, 255, 255));
        fileList.setSelectionForeground(new java.awt.Color(0, 51, 51));
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(fileList);

        newButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        newButton.setText("New");
        newButton.setToolTipText("Create new journal for today");
        newButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        journalTextArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 204)));
        journalTextArea.setFont(new java.awt.Font("Tahoma", 0, 12));
        journalTextArea.setToolTipText("The selected journal's text");
        journalTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                journalTextAreaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                journalTextAreaFocusLost(evt);
            }
        });
        journalTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                journalTextAreaMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(journalTextArea);

        jLabel17.setText("Journal:");

        org.jdesktop.layout.GroupLayout journalPanelLayout = new org.jdesktop.layout.GroupLayout(journalPanel);
        journalPanel.setLayout(journalPanelLayout);
        journalPanelLayout.setHorizontalGroup(
            journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(journalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(journalPanelLayout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(notesTextArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE))
                    .add(journalPanelLayout.createSequentialGroup()
                        .add(journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(journalPanelLayout.createSequentialGroup()
                                .add(newButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel17))
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)))
                .addContainerGap())
        );
        journalPanelLayout.setVerticalGroup(
            journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(journalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(notesTextArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(journalPanelLayout.createSequentialGroup()
                        .add(journalPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel17)
                            .add(newButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                .addContainerGap())
        );

        contactPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("Contact");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Phone");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Fax");

        contactTextField.setBackground(new java.awt.Color(255, 255, 204));
        contactTextField.setColumns(20);
        contactTextField.setToolTipText("[20 Char]");
        contactTextField.setNextFocusableComponent(phoneTextField);
        contactTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                contactTextFieldFocusGained(evt);
            }
        });

        phoneTextField.setBackground(new java.awt.Color(255, 255, 204));
        phoneTextField.setColumns(12);
        phoneTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        phoneTextField.setToolTipText("[20 Characters]");
        phoneTextField.setNextFocusableComponent(faxTextField);

        faxTextField.setColumns(12);
        faxTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        faxTextField.setToolTipText("[20 Characters]");
        faxTextField.setNextFocusableComponent(emailTextField);

        emailTextField.setColumns(40);
        emailTextField.setToolTipText("[40 Char]");

        wwwTextField.setColumns(50);
        wwwTextField.setText("http://");
        wwwTextField.setToolTipText("[50 Char] Must Contain a Protocol Such as http:// or https://");
        wwwTextField.setNextFocusableComponent(notesTextArea);

        invoiceTable.setModel(new javax.swing.table.DefaultTableModel(
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
        invoiceTable.setToolTipText("Invoices found for this contact");
        invoiceTable.setSelectionBackground(new java.awt.Color(204, 255, 204));
        invoiceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invoiceTableMouseClicked(evt);
            }
        });
        invoiceTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                invoiceTableKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(invoiceTable);

        viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/eye.png"))); // NOI18N
        viewButton.setText("Open");
        viewButton.setToolTipText("Open an Invoice or Quote");
        viewButton.setMargin(new java.awt.Insets(1, 14, 1, 14));
        viewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        emailButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        emailButton.setText("email");
        emailButton.setToolTipText("New Email");
        emailButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        emailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailButtonActionPerformed(evt);
            }
        });

        wwwButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        wwwButton.setText("www");
        wwwButton.setToolTipText("Launch");
        wwwButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        wwwButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wwwButtonActionPerformed(evt);
            }
        });

        invoiceToggleButton.setText("Show Quotes");
        invoiceToggleButton.setToolTipText("Toggles the Display of Quotes or Invoices");
        invoiceToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceToggleButtonActionPerformed(evt);
            }
        });

        invoiceLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        invoiceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        invoiceLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/smdwn.gif"))); // NOI18N
        invoiceLabel.setText("Invoices");
        invoiceLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        purchaseHistoryButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/report.jpg"))); // NOI18N
        purchaseHistoryButton.setText("Purchase History");
        purchaseHistoryButton.setToolTipText("Customer's product purchase history report");
        purchaseHistoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseHistoryButtonActionPerformed(evt);
            }
        });

        invoiceReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/report.jpg"))); // NOI18N
        invoiceReportButton.setText("Invoice History");
        invoiceReportButton.setToolTipText("Customer's invoice history statement");
        invoiceReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoiceReportButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout contactPanelLayout = new org.jdesktop.layout.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contactPanelLayout.createSequentialGroup()
                .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(emailButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(wwwButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(wwwTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                            .add(contactTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                            .add(faxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(phoneTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(emailTextField, 0, 0, Short.MAX_VALUE)))
                    .add(contactPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(purchaseHistoryButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(invoiceReportButton)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .add(invoiceToggleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .add(invoiceLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, viewButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                .addContainerGap())
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactPanelLayout.createSequentialGroup()
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(contactTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel11)
                            .add(phoneTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel12)
                            .add(faxTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(emailButton)
                            .add(emailTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(wwwTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(wwwButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 34, Short.MAX_VALUE)
                        .add(contactPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(purchaseHistoryButton)
                            .add(invoiceReportButton)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, contactPanelLayout.createSequentialGroup()
                        .add(invoiceToggleButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(invoiceLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(viewButton)))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(addressPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(journalPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(contactPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(journalPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(addressPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
                .addContainerGap())
        );

        jScrollPane5.setViewportView(jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 968, Short.MAX_VALUE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void invoiceTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invoiceTableKeyPressed
        
        int kc = evt.getKeyCode();
        
        if (kc == evt.VK_ADD) {
            
            takePayment();
            return;
            
        }
        
        if (kc == evt.VK_ENTER) {
            
            viewInvoice();
            return;
            
        }
        
    }//GEN-LAST:event_invoiceTableKeyPressed

    private void toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonActionPerformed
        
        if (jScrollPane5.isVisible()) {
            
            /* addressPanel.setVisible(false);
            contactPanel.setVisible(false);
            journalPanel.setVisible(false);*/
            jScrollPane5.setVisible(false);
            toggleButton.setText("More");
            
        }else {
            
            
            /*addressPanel.setVisible(true);
            contactPanel.setVisible(true);
            journalPanel.setVisible(true);*/
            jScrollPane5.setVisible(true);
            toggleButton.setText("Less");
            
            
        }
        
        findField.requestFocus();
    }//GEN-LAST:event_toggleButtonActionPerformed

    private void invoiceTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invoiceTableMouseClicked

       

        if (evt.getClickCount() == 2){

             if (!accessKey.checkManager(300)){
                accessKey.showMessage("Invoice Manager");
                return;
            }

            viewInvoice();
            
            
           }
        
    }//GEN-LAST:event_invoiceTableMouseClicked

    private void labelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelButtonActionPerformed
         if (!accessKey.checkConnections(300)){
            accessKey.showMessage("Labels");
            return;
        }
      
        if (connTable.getSelectedRow() > -1){
            
        
        new ConnLabelDialog(null, true, connTable.getModel(), connTable.getSelectedRows(), workingPath, props);
        
        }else {
            
             javax.swing.JOptionPane.showMessageDialog(null, "Select rows from the Connections table to create labels.");
            
        }
        
    }//GEN-LAST:event_labelButtonActionPerformed

    private void contactTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_contactTextFieldFocusGained
        

        if (contactTextField.getText().trim().equals("")) {
            
            contactTextField.setText(firstTextField.getText().trim() + " " + lastTextField.getText().trim());
                        
        }
        
        
    }//GEN-LAST:event_contactTextFieldFocusGained

    private void wwwButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wwwButtonActionPerformed
        /* Add protocol info if none is specified */
        if (!wwwTextField.getText().toUpperCase().contains("HTTP://") && !wwwTextField.getText().toUpperCase().contains("FTP://") )
            wwwTextField.setText("http://" + wwwTextField.getText());

        boolean desktop = DV.parseBool(props.getProp("DESKTOP SUPPORTED"), false);
        if(Desktop.isDesktopSupported() && desktop){
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                try {
                    
                    Desktop.getDesktop().mail(new URI(wwwTextField.getText()));
                    return;
                } catch (Exception ex) {
                  //try the old manual method below
                }
         }
        }

    
        int a = DV.launchURL(wwwTextField.getText());
        if (a < 1) 
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem trying to launch a web browser." + nl + "This may not be supported by your Operating System." );
        //process errors
        
    }//GEN-LAST:event_wwwButtonActionPerformed

    private void emailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailButtonActionPerformed
        
        if (emailTextField.getText().equals("") || emailTextField.getText().length() < 8){  /*REGEX*/
            
            javax.swing.JOptionPane.showMessageDialog(null, "You need to enter a good email address.");
            return;
            
        }
        boolean desktop = DV.parseBool(props.getProp("DESKTOP SUPPORTED"), false);
        if(Desktop.isDesktopSupported() && desktop){
            if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)){
                try {
                    
                    Desktop.getDesktop().mail(new URI("mailto:"+emailTextField.getText()));
                    return;
                } catch (Exception ex) {
                  //try the old manual method below
                }
         }
        }
        int a = DV.launchURL("mailto:"+emailTextField.getText());
        if (a < 1) 
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem trying to launch an email application." + nl + "This may not be supported by your Operating System." );
        
    
        
    }//GEN-LAST:event_emailButtonActionPerformed

    private void zipTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_zipTextFieldKeyPressed
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            
            zipAction();
            
        }
        
        
    }//GEN-LAST:event_zipTextFieldKeyPressed

    private void zipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zipButtonActionPerformed
        
        zipAction();
        
        
    }//GEN-LAST:event_zipButtonActionPerformed

    private void takePayment() {
        
           int r = invoiceTable.getSelectedRow();
        TableModel tm = invoiceTable.getModel();
        
        if (r > -1) {
        
            if ((Boolean) tm.getValueAt(r, 2) == true) {
                
                javax.swing.JOptionPane.showMessageDialog(this, "Invoice is marked as paid.");
                invoiceTable.changeSelection(r,0,false,false);
                invoiceTable.requestFocus();
                return;
                
            }
            else{ 
                
                int key = (Integer)  tm.getValueAt(r, 0);
        
                PaymentDialog pd = new PaymentDialog (parentWin, true, key, db, workingPath, accessKey );
                pd.setVisible(true);
                populateInvoices(false);
            }
            
            
        invoiceTable.changeSelection(r,0,false,false);
        invoiceTable.requestFocus();
        
        }
        
    }
    
    private void findFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_findFieldFocusGained
        
        findField.selectAll();
        
    }//GEN-LAST:event_findFieldFocusGained

    private void viewInvoice() {

        //boolean small = Tools.getStringBool(props.getProp("SMALL SCREEN"));
        int row = invoiceTable.getSelectedRow();
        
          if (invoiceTable.getSelectedRow() > -1){
            
            int key = (Integer) invoiceTable.getModel().getValueAt(invoiceTable.getSelectedRow(), 0);
            
            if (invoiceToggleButton.getText().endsWith("Quotes")){

                if (key > 0) {
                    
                    if (small){
                        InvoiceDialogSmall id = new InvoiceDialogSmall (parentWin, true, db,
                                accessKey, key, workingPath ); //no select
            
                        id.setVisible(true);
                        id.dispose();
                    }else {
                        InvoiceDialog id = new InvoiceDialog (parentWin, true, db,
                                accessKey, key, workingPath ); //no select

                        id.setVisible(true);
                        id.dispose();

                    }
                }
            }else {
                if (key > 0) {
                    
                    if (small){
                        InvoiceDialogSmall id = new InvoiceDialogSmall (parentWin, true, db,
                                key, workingPath, accessKey ); //no select
            
                        id.setVisible(true);
                        id.dispose();
                    }else {
                        InvoiceDialog id = new InvoiceDialog (parentWin, true, db,
                                key, workingPath, accessKey ); //no select

                        id.setVisible(true);
                        id.dispose();

                    }

                }
                
            }


          }
        
        invoiceTable.changeSelection(row,0,false,false);
        invoiceTable.requestFocus();
        
    }
    
    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed

        if (!accessKey.checkManager(300)){
            accessKey.showMessage("Invoice Manager");
            return;
        }
      viewInvoice();
        
    }//GEN-LAST:event_viewButtonActionPerformed

    private void journalTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_journalTextAreaMouseClicked
      
       
       
    }//GEN-LAST:event_journalTextAreaMouseClicked

    private void journalTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_journalTextAreaFocusLost
            
        
        
        if (!fileList.isEnabled()) {
            
                
                saveJournal();
            fileList.requestFocus();
            
            
            
            } 
        
      
    }//GEN-LAST:event_journalTextAreaFocusLost
    
    private void journalTextAreaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_journalTextAreaFocusGained
        fileList.setEnabled(false);
    }//GEN-LAST:event_journalTextAreaFocusGained

    private void connTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_connTableKeyReleased
        
        if (connTable.getSelectedRow() > -1) {  
            Integer key = (Integer) connTable.getModel().getValueAt(connTable.getSelectedRow(), 0);
            edit_key = key;
            populateFields();
            setFieldsEnabled(true);    
        
            saveButton.setEnabled(true);
                
        }
        
    }//GEN-LAST:event_connTableKeyReleased

    private void fileListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileListMouseClicked
        
        if (!fileList.isEnabled()  && companyTextField.isEnabled()) {
            saveJournal();
            fileList.requestFocus();
            
        }
        
        if (companyTextField.isEnabled()) getJournal();
        
        
               
    }//GEN-LAST:event_fileListMouseClicked

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
      
        if (edit_key > 0)  newJournal();
        
    }//GEN-LAST:event_newButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed

        if (connTable.getModel().getRowCount() > 0){        
            if (!accessKey.checkExports(500)){                
                accessKey.showMessage("Export");
                return;
            }
            String home = System.getProperty("user.home");
            if (System.getProperty("os.name").contains("Windows")) home =  home + '\\' + "My Documents";
            
            datavirtue.FileDialog fd = new datavirtue.FileDialog(parentWin, true, home, "export.csv");
        
            fd.setVisible(true);
        
            if (!fd.getPath().equals("") ){        
                    export( fd.getPath() );                    
             }
        }
    }//GEN-LAST:event_exportButtonActionPerformed

    private void findFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_findFieldKeyPressed
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
           
            
                find();
           
        }
    }//GEN-LAST:event_findFieldKeyPressed

    private void connTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_connTableKeyPressed
        
        
        if (selectMode) {
            
            if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
                                            
                if (connTable.getSelectedRow() > -1 && selectMode) {  
                    returnValue = (Integer) connTable.getModel().getValueAt(connTable.getSelectedRow(), 0);
                    this.setVisible(false);
                }
           
            }
        
        }
        
        
    }//GEN-LAST:event_connTableKeyPressed

    private void companyTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_companyTextFieldMouseClicked
        
        if (!companyTextField.isEnabled()) {
            
            if (!accessKey.checkConnections(200)){
                accessKey.showMessage("Create");
                return;
            }

            clearFields();
            setFieldsEnabled(true);
            
            companyTextField.requestFocus();
            
        }
        
        
    }//GEN-LAST:event_companyTextFieldMouseClicked

    private void suppRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suppRadioActionPerformed
        
        refreshTable ();
        clearFields();
        this.setFieldsEnabled(false);
        
    }//GEN-LAST:event_suppRadioActionPerformed

    private void custRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_custRadioActionPerformed
        
        refreshTable ();
        clearFields();
        this.setFieldsEnabled(false);
        
    }//GEN-LAST:event_custRadioActionPerformed

    private void allRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allRadioActionPerformed
        
        refreshTable ();
        clearFields();
        this.setFieldsEnabled(false);
        
    }//GEN-LAST:event_allRadioActionPerformed

    private void connTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_connTableMouseClicked
    int mouseButton = evt.getButton();
    if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) return;    
            //on Double Click
         
        if (selectMode){ 
        
           if (evt.getClickCount() == 2){
                        
            int row = connTable.rowAtPoint(new Point(evt.getX(), evt.getY()));
            
             if (connTable.getSelectedRow() > -1) {  
                
                returnValue = (Integer) connTable.getModel().getValueAt(row, 0);
              
                this.setVisible(false);
             }
            
           }
                   
        }
        
            populateFields();
       
    }//GEN-LAST:event_connTableMouseClicked

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        
                
        if (connTable.getSelectedRow() > -1) {

            if (!accessKey.checkConnections(500)){
                accessKey.showMessage("Delete");
                return;
            }

            int a = JOptionPane.showConfirmDialog(this, "Delete Selected Record?","ERASE",  JOptionPane.YES_NO_OPTION);
          
            if (a == 0){

                int key = (Integer) connTable.getModel().getValueAt(connTable.getSelectedRow(), 0) ;
                  
                boolean delete_successful = connDAO.deleteRecord(key);

                if (delete_successful){
                    clearFields();
                    setFieldsEnabled(false);
                    refreshTable ();
                    JOptionPane.showMessageDialog(null, "The record was deleted.");
                }else {

                    JOptionPane.showMessageDialog(null, "The record was NOT deleted.");
                }
                  
            }
         
        
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void voidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voidButtonActionPerformed
        returnValue = 0;
        this.setVisible(false);
    }//GEN-LAST:event_voidButtonActionPerformed
    
    private void refreshTable () {
        
        connTable.setModel(filter());
        
        setView (vals);
        
    }
    
    
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        
       
        clearFields();
        setFieldsEnabled(false);
        
        
    }//GEN-LAST:event_clearButtonActionPerformed

    
    
    private boolean checkDuplicates() {
        
        ArrayList al = new ArrayList ();
                
        al = db.search("conn", 9, phoneTextField.getText().trim(), false);
        /* DV.scanArrayList() checks through the al for the specified int value, if found the int is returned */
        if (al == null || DV.scanArrayList(al, (Integer) edit_key) == edit_key);
        else{
            
            JOptionPane.showMessageDialog(this, "Phone number is used in another record.","Duplicate Data!",  JOptionPane.OK_OPTION);
            return false;
            
            
        }
        
        al = db.search("conn", 1, companyTextField.getText().trim(), false);
        
        if (al == null || DV.scanArrayList(al, edit_key) == edit_key);
        else{
            
            JOptionPane.showMessageDialog(this, "Company name is used in another record.","Duplicate Data!",  JOptionPane.OK_OPTION);
            return false;
            
            
        }
        
        al = db.search("conn", 12, emailTextField.getText().trim(), false);
        
        if (al == null || DV.scanArrayList(al, edit_key) == edit_key);
        else{
            
            JOptionPane.showMessageDialog(this, "Email address is used in another record.","Duplicate Data!",  JOptionPane.OK_OPTION);
            return false;
            
            
        }
       
       return true;
        
    }
    
    private void saveAction(){

        if (!accessKey.checkConnections(200)){
            accessKey.showMessage("Create, Edit");
            return;
        }

        if (custCheckBox.isSelected() == false && supplierCheckBox.isSelected() == false) {
            
           int a = JOptionPane.showConfirmDialog(this, "You did NOT select 'Customer' OR 'Supplier'.  Is this ok? ","No Category",  JOptionPane.YES_NO_OPTION);
          
           //System.out.println("OPTION " + a);
          
            if (a != 0) return;
            
        }
        
        if (companyTextField.getText().trim().equals("") && firstTextField.getText().trim().equals("") &&
                contactTextField.getText().trim().equals("")){
            
            JOptionPane.showMessageDialog(this, "You have to provide some type of contact data; Company, First Name, or Contact.","Form Problem!",  JOptionPane.OK_OPTION);
            return;
        }
        
        if (!checkDuplicates()) return;
        
        //connDAO = new ConnectionsDAO(db);


/*        dataOut [1] = (String) companyTextField.getText().trim();
        dataOut [2] = (String) firstTextField.getText().trim();
        dataOut [3] = (String) lastTextField.getText().trim();
        dataOut [4] = (String) addressTextField.getText().trim();
        dataOut [5] = (String) suiteTextField.getText().trim();
        dataOut [6] = (String) cityTextField.getText().trim();
        dataOut [7] = (String) stateTextField.getText().trim();
        dataOut [8] = (String) zipTextField.getText().trim();
        dataOut [9] = (String) contactTextField.getText().trim();
        dataOut [10] = (String) phoneTextField.getText().trim();
        dataOut [11] = (String) faxTextField.getText().trim();
        dataOut [12] = (String) emailTextField.getText().trim();
        dataOut [13] = (String) wwwTextField.getText().trim();
        dataOut [14] = (String) notesTextArea.getText().trim();
        dataOut [15] = (Boolean) custCheckBox.isSelected();
        dataOut [16] = (Boolean) supplierCheckBox.isSelected();
        dataOut [17] = (String) countryCombo.getSelectedItem();
        dataOut [18] = (Boolean) tax1CheckBox.isSelected();
        dataOut [19] = (Boolean) tax2CheckBox.isSelected();
*/

        connDAO.setCompany(companyTextField.getText().trim());
        connDAO.setFirstName(firstTextField.getText().trim());
        connDAO.setLastName(lastTextField.getText().trim());
        connDAO.setStreet(addressTextField.getText().trim());
        connDAO.setAddr2(suiteTextField.getText().trim());
        connDAO.setCity(cityTextField.getText().trim());
        connDAO.setState(stateTextField.getText().trim());
        connDAO.setPostCode(zipTextField.getText().trim());
        connDAO.setContact(contactTextField.getText().trim());
        connDAO.setPhone(phoneTextField.getText().trim());
        connDAO.setFax(faxTextField.getText().trim());
        connDAO.setEmail(emailTextField.getText().trim());
        connDAO.setWWW(wwwTextField.getText().trim());
        connDAO.setMisc(notesTextArea.getText().trim());
        connDAO.setCustomer(custCheckBox.isSelected());
        connDAO.setSupplier(supplierCheckBox.isSelected());
        connDAO.setAlphaCountryCode((String)countryCombo.getSelectedItem());
        connDAO.setTax1(tax1CheckBox.isSelected());
        connDAO.setTax2(tax2CheckBox.isSelected());


        if (debug) System.out.println("The current propsed key: "+edit_key);
        
        //int zx = db.saveRecord("conn", dataOut, false);
        int zx = connDAO.saveRecord();

        if (debug) System.out.println("Recorded as key: "+zx);
        
        clearFields();
        setFieldsEnabled(false);
        
        saveButton.setEnabled(false);
        //dataOut = new Object [20];
        
        allRadio.setSelected(true);
        
        refreshTable();
                
        /*  select  */
                
        int row = DV.searchTable(connTable.getModel(), 0, zx);
        
        if (row > connTable.getModel().getRowCount());
        else{
            
            connTable.changeSelection(row,0,false,false);
            
        }
      
        
    }
    
    
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        
        saveAction();
        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        
        if (connTable.getSelectedRow() > -1) {  
        returnValue = (Integer) connTable.getModel().getValueAt(connTable.getSelectedRow(), 0);
        
        this.setVisible(false);
        }
        
    }//GEN-LAST:event_selectButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = DV.getFileChooser("c:/1Data/Data Virtue/Research/International/");

        File f = fileChooser.getSelectedFile();

        int [] r = {0,1,2,3,4,5};
        db.csvImport("countries", f, false, r, false);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void notesTextAreaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesTextAreaFocusGained
        messageField.setText("The Misc field can be used to store any information. (Searchable)");
    }//GEN-LAST:event_notesTextAreaFocusGained

    private void notesTextAreaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesTextAreaFocusLost
        messageField.setText("Remember to click 'Save' when you modify a record.");
    }//GEN-LAST:event_notesTextAreaFocusLost

    private void shipToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shipToButtonActionPerformed
        if (edit_key > 0) shipToAction();
    }//GEN-LAST:event_shipToButtonActionPerformed

    private void invoiceToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceToggleButtonActionPerformed
        populateInvoices(true);
    }//GEN-LAST:event_invoiceToggleButtonActionPerformed

    private void purchaseHistoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchaseHistoryButtonActionPerformed
        doPurchaseReport();
    }//GEN-LAST:event_purchaseHistoryButtonActionPerformed

    private void invoiceReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoiceReportButtonActionPerformed

        doInvoiceReport();

    }//GEN-LAST:event_invoiceReportButtonActionPerformed

    private void doInvoiceReport(){
        int r = connTable.getSelectedRow();

        if (r < 0) return;


        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }

        int k = (Integer)connTable.getModel().getValueAt(r, 0);

        if (k > 0) ReportFactory.generateCustomerStatement(db, props, k);
        
    }

    private void doPurchaseReport(){

        if (!accessKey.checkReports(500)){
            accessKey.showMessage("Customer/Supplier Reports");
            return;
        }


        int row = connTable.getSelectedRow();

        if (row < 0) return;

        int k = edit_key;


       java.util.ArrayList al = db.search("invoice", 11, Integer.toString(k), false);

       if (al == null || al.size() < 1){

           javax.swing.JOptionPane.showMessageDialog(null,
                   "No invoices found for this contact.");
           return;
       }

       
        k = (Integer)connTable.getModel().getValueAt(row, 0);
        PurchaseHistoryReport phr = new PurchaseHistoryReport(db);
        phr.SetTitle("Customer Purchase History Report");
        phr.setCustomer(k);
        phr.buildReport();
        new ReportTableDialog(parentWin, true, phr, props);
        
        
        
    }

    private void shipToAction() {

        //utilize connctions key to create a new shipping address
        new ConnectionsShippingDialog(parentWin,true,db, edit_key, false);
    }

    private void getCountry() {

        int [] r = {0};

        TableView tv = new TableView(parentWin, true, db, "countries", 0,
                "Select a country from the table.",r);

        tv.dispose();

    }
   
   
    Settings props;
    private DbEngine db=null;
    private ConnectionsDAO connDAO=null;
    private int returnValue = -1;
    private boolean selectMode = false;
    //private Object [] dataOut = new Object [20];
    private int edit_key = 0;
    private int [] vals = {0,15,14,13,12,11,10,3,3,3,3,3,6,6}; //col view removal
    private java.awt.Frame parentWin;
    private javax.swing.DefaultListModel lm = new javax.swing.DefaultListModel ();
    private DbEngine zip;
    private String nl = System.getProperty("line.separator");
    private Image winIcon;
    private boolean small = false;
    private int searchColumn = 1;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addressPanel;
    private javax.swing.JTextField addressTextField;
    private javax.swing.JRadioButton allRadio;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JTextField cityTextField;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField companyTextField;
    private javax.swing.JTable connTable;
    private javax.swing.JPanel contactPanel;
    private javax.swing.JTextField contactTextField;
    private javax.swing.JComboBox countryCombo;
    private javax.swing.JCheckBox custCheckBox;
    private javax.swing.JRadioButton custRadio;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton emailButton;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JButton exportButton;
    private javax.swing.JTextField faxTextField;
    private javax.swing.JList fileList;
    private javax.swing.JTextField findField;
    private javax.swing.JTextField firstTextField;
    private javax.swing.JLabel invoiceLabel;
    private javax.swing.JButton invoiceReportButton;
    private javax.swing.JTable invoiceTable;
    private javax.swing.JButton invoiceToggleButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPanel journalPanel;
    private javax.swing.JTextPane journalTextArea;
    private javax.swing.JLabel keyLabel;
    private javax.swing.JButton labelButton;
    private javax.swing.JTextField lastTextField;
    private javax.swing.JTextField messageField;
    private javax.swing.JButton newButton;
    private javax.swing.JTextField notesTextArea;
    private javax.swing.JTextField phoneTextField;
    private javax.swing.JButton purchaseHistoryButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox searchFieldCombo;
    private javax.swing.JButton selectButton;
    private javax.swing.JButton shipToButton;
    private javax.swing.JTextField stateTextField;
    private javax.swing.JTextField suiteTextField;
    private javax.swing.JRadioButton suppRadio;
    private javax.swing.JCheckBox supplierCheckBox;
    private javax.swing.JCheckBox tax1CheckBox;
    private javax.swing.JCheckBox tax2CheckBox;
    private javax.swing.JButton toggleButton;
    private javax.swing.JButton viewButton;
    private javax.swing.JButton voidButton;
    private javax.swing.JButton wwwButton;
    private javax.swing.JTextField wwwTextField;
    private javax.swing.JButton zipButton;
    private javax.swing.JTextField zipTextField;
    // End of variables declaration//GEN-END:variables
    
}
