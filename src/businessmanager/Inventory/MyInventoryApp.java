/*
 * InventoryDialog.java
 *
 * Created on June 23, 2006, 9:29 AM
 *
 */

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2008, 2009, 2010, 2011 All Rights Reserved.
 */
package businessmanager.Inventory;
import RuntimeManagement.KeyCard;
import RuntimeManagement.GlobalApplicationDaemon;
import businessmanager.Common.LimitedDocument;
import businessmanager.Connections.MyConnectionsApp;
import businessmanager.*;

import businessmanager.Common.JTextFieldFilter;
import businessmanager.Common.Tools;
import datavirtue.*;
import java.sql.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.*;
import java.awt.*;
public class MyInventoryApp extends javax.swing.JDialog {
    private KeyCard accessKey;
    private boolean debug = false;
    
    //Custom constructor
    /** Creates new form InventoryDialog */
    public MyInventoryApp(java.awt.Frame parent, boolean modal, GlobalApplicationDaemon application, boolean select) {
        
        super(parent, modal);
        
        initComponents();
        
        Toolkit tools = Toolkit.getDefaultToolkit();
        winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        this.setIconImage(winIcon);

        this.application = application;
        accessKey = application.getKey_card();
        
        //Adjust references, to this context, for the needed objects 
        
        db = application.getDb();
        selectMode = select;
        
        workingPath = application.getWorkingPath();
        
        
        iTable.setSelectionForeground(Color.BLACK);
        
        if (selectMode) receiveModeBox.setVisible(false);
        props = new Settings (workingPath+"settings.ini");
        String coName = props.getProp("CO NAME");
        this.setTitle(coName + " Inventory Manager");
        
        /* Limit field characters */
        
        qtyTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        costTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
        priceTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        /* Allow the price field to accept negative numbers */
        JTextFieldFilter tf = (JTextFieldFilter)priceTextField.getDocument();
        tf.setNegativeAccepted(true);

        weightTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));

        upcTextField.setDocument(new LimitedDocument(14));
        nameTextField.setDocument(new LimitedDocument(16));
        descTextField.setDocument(new LimitedDocument(50));
        sizeTextField.setDocument(new LimitedDocument(15));  //float?  ??
        weightTextField.setDocument(new LimitedDocument(15)); //float?  ??
        catTextField.setDocument(new LimitedDocument(20));

        //Version 1.5
        /* Set tax names */
        taxCheckBox.setText(props.getProp("TAX1NAME"));
        tax2CheckBox.setText(props.getProp("TAX2NAME"));
        
        
        /* Set the spinner  */
        /*                                              val min max  incr      */
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, null, 1); 
        jSpinner1.setModel(model);
        

        this.addWindowListener(new java.awt.event.WindowAdapter(){
	public void windowClosing(java.awt.event.WindowEvent e){

            recordScreenPosition();
            props.setProp("INVENTORY SEARCH", Integer.toString(searchFieldCombo.getSelectedIndex()));

	}} );

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
                       
        if (select) {
                        
            tabPanel.setVisible(false);
            savePanel.setVisible(false);
            toggleButton.setText("More");
           
        }        
        this.populateCategoryList();
        
        
        SwingWorker worker = new SwingWorker() {

            public Object doInBackground() {
                StatusDialog sd = new StatusDialog(parentWin, false, "Please Wait", false);
                sd.changeMessage("Initializing Inventory");
                sd.addStatus("Building inventory table...");
                tm = db.createTableModel("inventory", iTable);//time consuming
                sd.addStatus("Inventory table Complete.");
                sd.addStatus("Configuring inventory table...");
                return sd;
            }


            public void done() {
                iTable.setModel(tm);
                init();
                StatusDialog d = null;
                try {
                    d = (StatusDialog) get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MyInventoryApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(MyInventoryApp.class.getName()).log(Level.SEVERE, null, ex);
                }
                d.addStatus("<<< Complete >>>");
                d.dispose();
            }
        };
        worker.execute();

        /*  (new Thread(){
            public void run(){
            final StatusDialog sd = new StatusDialog(parentWin, false, "Please Wait", false);
            sd.changeMessage("Initializing Inventory");
            sd.addStatus("Building inventory table...");
            tm = db.createTableModel("inventory", iTable);//time consuming
            sd.addStatus("Inventory table Complete.");
            sd.addStatus("Configuring inventory table...");
            // Place the GUI update code back on the EDT
            SwingUtilities.invokeLater(new Runnable() {
            public void run()  {
            iTable.setModel(tm);
            init();
            sd.dispose();
            }});
            }}).start();*/
            if (this.checkForScreenSettings()){
            this.restoreScreenPosition();//restore saved screen size
        }else {
            java.awt.Dimension d = DV.computeCenter((java.awt.Window) this);
            this.setLocation(d.width, d.height);
        }
       
        this.setVisible(true);//release to the user
        
    }//end constructor
    
    private String workingPath="";
    
    private void clearSupplierKeys() {
        
        for (int i = 0; i < supp.length; i++){  //3_LOOP            
            supp [i] = 0;  //initialize "supplier" key holder array to zeros            
        }
    }   
   
    /* My initializer method */
    private void init () {
        
        setView(vals);
                
        clearSupplierKeys();
        
        if (selectMode) {       /* Setup Select Mode */
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

        int c = Tools.getStringInt(props.getProp("INVENTORY SEARCH"), 2); //default to desc
        searchFieldCombo.setSelectedIndex(c);
        
        taxCheckBox.setToolTipText(props.getProp("TAX1"));
        tax2CheckBox.setToolTipText(props.getProp("TAX2"));
        
        findTextField.requestFocus();        
    }

   private void recordScreenPosition(){

        Point p = this.getLocationOnScreen();
        Dimension d = this.getSize();

        props.setProp("INVTPOS", p.x+","+p.y);
        props.setProp("INVTSIZE", d.width+","+d.height);

    }

    private Point defaultScreenPosition;
    private Dimension defaultWindowSize;

    private boolean checkForScreenSettings(){
        String pt = props.getProp("INVTPOS");
        String dim = props.getProp("INVTSIZE");
        if (pt.equals("")) return false;
        if (dim.equals("")) return false;
        if ((Tools.parsePoint(pt))==null) return false;
        if ((Tools.parseDimension(dim))==null) return false;
        return true;

    }

    private void storeDefaultScreen(){

        try{
            defaultScreenPosition = this.getLocationOnScreen();
        }catch(Exception e){
            defaultScreenPosition = new Point(0,0);
        }

        defaultWindowSize = this.getSize();

    }

    private void restoreDefaultScreenSize(){

        this.setSize(this.defaultWindowSize);
    }
    private void restorDefaultScreen(){
        restoreDefaultScreenSize();
        restoreDefaultScreenLocation();
    }
    private void restoreDefaultScreenLocation(){
        this.setLocation(this.defaultScreenPosition);
    }

    private void restoreScreenPosition(){

        String pt = props.getProp("INVTPOS");
        String dim = props.getProp("INVTSIZE");
        //System.out.println("Point "+pt+"  Dim"+dim);
        Point p = Tools.parsePoint(pt);
        if (p==null) p = this.defaultScreenPosition;
        if (p==null) p = new Point(0,0);
        this.setLocation(p);
        Dimension d = Tools.parseDimension(dim);
        if (d==null) d = this.defaultWindowSize;
        this.setSize(d);

    }

    private java.util.ArrayList catList;
    
    private void populateCategoryList (){
                
        catList = new java.util.ArrayList();
        catList.trimToSize();
        
        TableModel cat_tm = db.createTableModel("invtcat");
        
        if (cat_tm != null && cat_tm.getRowCount() > 0){
            
            for (int r = 0; r < cat_tm.getRowCount(); r++){
                catList.add((String) cat_tm.getValueAt(r, 1));
            }
        }else {
            
            catList.add("N/A");
        }        
        catTextField.setDocument(new AutoCompleteDocument( catTextField, catList ));
        
    }
    
    private void normalizeCategoryList (String s){  //checks for s and if not found it is added to the db        
        
        if (s.trim().equals("")) return;        
       
        java.util.ArrayList al;
        
        al = db.search("invtcat", 1, s, false);
        
        if (al == null){
            
            db.saveRecord("invtcat",new Object [] {new Integer(0), new String (s)} ,false);
            //db.close();
            
            populateCategoryList();            
        }
    }
    
       
    private void setFieldsEnabled (boolean enabled) {
        
        upcTextField.setEnabled(enabled);
        nameTextField.setEnabled(enabled);
        descTextField.setEnabled(enabled);
        sizeTextField.setEnabled(enabled);
        weightTextField.setEnabled(enabled);
        qtyTextField.setEnabled(enabled);
        costTextField.setEnabled(enabled);
        priceTextField.setEnabled(enabled);
        catTextField.setEnabled(enabled);
        serviceBox.setEnabled(enabled);
        jSpinner1.setEnabled(enabled);
        
        picField.setEnabled(enabled);
        
        taxCheckBox.setEnabled(enabled);
        tax2CheckBox.setEnabled(enabled);
        availableCheckBox.setEnabled(enabled);
        
        picButton.setEnabled(enabled);
        
        
        if (dataOut != null && dataOut[0] != null && (Integer)dataOut[0] > 0){
            
            receiveButton.setEnabled(enabled);
            
            
        }else{
            
            receiveButton.setEnabled(false);
        }
        
        if (enabled){
            
            helpBox.setText("Remember to click 'Save' after making changes.");
            
        }else {
            
            helpBox.setText("Click the UPC field to start a new record.");
            
        }
        noteButton.setEnabled(enabled);
                
    }

    private void clearFields () {
               
        upcTextField.setText("");
        nameTextField.setText("");
        descTextField.setText("");
        sizeTextField.setText("");
        weightTextField.setText("");
        qtyTextField.setText("0.000");
        costTextField.setText("0.00");
        priceTextField.setText("0.00");
        
        catTextField.setText("");
        
        saleField.setText("");
        recvdField.setText("");
        
        jSpinner1.setValue(new Integer(0));
        
        supp1TextField.setText("");
        supp2TextField.setText("");
        supp3TextField.setText("");
        
        picField.setText("");
        
        taxCheckBox.setSelected(false);
        tax2CheckBox.setSelected(false);
        availableCheckBox.setSelected(false);
        costTotalLabel.setText("0.00");
        retailTotalLabel.setText("0.00");
                      
        viewButton.setEnabled(false);
        
        findTextField.requestFocus();
        
        serviceBox.setSelected(false);
        
    }
    
    /** This method completes the setup of the actual
     * JTable look in an appropriate fashion for the
     * specific data 
     */
    private void setView (int [] cols){
        int col_count = iTable.getColumnCount();
        if (col_count == 0 || col_count < 6) return;
        
        TableColumnModel cm = iTable.getColumnModel();
        TableColumn tc;
        
        for (int i =0; i < cols.length; i++){
            
            tc = cm.getColumn(cols[i]);
            
            iTable.removeColumn(tc);
            
        }

        //Set the width of the most important col in inventory table
        iTable.getColumn(iTable.getColumnName(1)).setPreferredWidth(220);
        iTable.getColumn(iTable.getColumnName(2)).setPreferredWidth(25);
        iTable.getColumn(iTable.getColumnName(3)).setPreferredWidth(50);
        
    } 
           
    /** Will find ALL occurances of matching records and selects these multiples in the table view. */
    private void search () {
        
        boolean match = false;
        
        if (findTextField.getText().trim().equals("")) {
            
            refreshTable();
            clearFields();
            setFieldsEnabled(false);
            saveButton.setEnabled(false);
            
            findTextField.requestFocus();
            
            return;
        }
        
        /* get text from findTextField.getText(); */
        
        String text = findTextField.getText();
               
        /* build search from button status  */


        int col = searchFieldCombo.getSelectedIndex() + 1;


        if (col == 4){ //size

            java.util.ArrayList al = db.search("inventory", 4, text, true);

            if (al == null) {
                refreshTable();
                findTextField.requestFocus();
                findTextField.selectAll();  
             }else {
                tm = db.createTableModel("inventory", al, true);
                iTable.setModel(tm);
                if (tm != null) setView (vals);
                match = true;
                findTextField.selectAll();
                return;
             }

        }

        if (col == 5){ //weight

            java.util.ArrayList al = db.search("inventory", 5, text, true);

            if (al == null) {
                refreshTable();
                findTextField.requestFocus();
                findTextField.selectAll();
             }else {
                tm = db.createTableModel("inventory", al, true);
                iTable.setModel(tm);
                if (tm != null) setView (vals);
                match = true;
                findTextField.selectAll();
                return;
             }

        }

        if (col == 6){ //category

            java.util.ArrayList al = db.search("inventory", 9, text, true);

            if (al == null) {
                refreshTable();
                findTextField.requestFocus();
                findTextField.selectAll();
             }else {
                tm = db.createTableModel("inventory", al, true);
                iTable.setModel(tm);
                if (tm != null) setView (vals);
                match = true;
                findTextField.selectAll();
                return;
             }
        }

        if (col == 3) { //search for desc
            
             java.util.ArrayList al = db.search("inventory",3, text, true );
            
             if (al == null) {
                refreshTable(); 
                findTextField.requestFocus();
                findTextField.selectAll();
                
             }else {
                tm = db.createTableModel("inventory", al, true);
            
                iTable.setModel(tm);
            
                if (tm != null) setView (vals);
                
                match = true;
                
                findTextField.selectAll();                
             }
        }
        
        if (col == 1)  { //UPC
                        
            java.util.ArrayList row = DV.searchTableMulti(iTable.getModel(), 1, text);
            
            if (row != null){
                
                iTable.clearSelection();
                
                for (int  r = 0; r < row.size(); r++){
                //iTable.changeSelection((Integer) row.get(r), 0, true, true);
                iTable.addRowSelectionInterval((Integer) row.get(r),(Integer) row.get(r));
                }
                populateFields();
                match = true;
            }else {
                
                if (receiveModeBox.isSelected()){
                int a = 
                        javax.swing.JOptionPane.showConfirmDialog(null,
                        "No record for this UPC was found would you like to add a new one?","No Match",  JOptionPane.YES_NO_OPTION);
                if (a == 0){                    
                    
                    dataOut = new Object [20];
                    dataOut [0] = new Integer (0);
                    picRef [0] = new Integer(0);
                    clearSupplierKeys();
                    
                    clearFields();
                    setFieldsEnabled (true);
                    upcTextField.setText(findTextField.getText());
                    upcTextField.requestFocus();
                    saveButton.setEnabled(true);
                    return;
                    
                }else {
                    
                    
                }
            }
            }            
            
            findTextField.selectAll();            
        }
        
       if (col == 2)  {  //Code
            
            java.util.ArrayList row = DV.searchTableMulti(iTable.getModel(), 2, text);
            //int row = DV.searchTable(iTable.getModel(), 2, text);
            
             if (row != null){
                
                iTable.clearSelection();
                
                for (int  r = 0; r < row.size(); r++){
                //iTable.changeSelection((Integer) row.get(r), 0, true, true);
                iTable.addRowSelectionInterval((Integer) row.get(r),(Integer) row.get(r));
                }
                
                populateFields();
                
                match = true;
            }else {
                
                if (receiveModeBox.isSelected()){
                int a = 
                        javax.swing.JOptionPane.showConfirmDialog(null,
                        "No record for this CODE was found would you like to add a new one?","No Match",  JOptionPane.YES_NO_OPTION);
                if (a == 0){
                    
                    dataOut = new Object [20];
                    dataOut [0] = new Integer (0);
                    picRef [0] = new Integer(0);
                    clearSupplierKeys();
                     
                    clearFields();
                    setFieldsEnabled (true);
                    
                    saveButton.setEnabled(true);
                    nameTextField.setText(findTextField.getText());
                    upcTextField.requestFocus();
                    
                    return;
                    
                }else {
                    
                    
                }
            }
            } 
            findTextField.selectAll();            
        }
        
        if (catTextField.isEnabled()) saveButton.setEnabled(true);
        
        if (!match) {
            
            clearFields();
            this.setFieldsEnabled(false);
            this.saveButton.setEnabled(false);
            
        }else {            
            if (receiveModeBox.isSelected()){                
                this.receive();                
            }
        }
    }
    
    private void calculateMarkup(){
        if (!priceTextField.getText().trim().equals("") &&
                !priceTextField.getText().trim().equals("0.00")) return;
        float points=0f;
        if (DV.validFloatString(props.getProp("MARKUP"))){
            points = Float.parseFloat(props.getProp("MARKUP"));
        }else return;

        float cost = Float.parseFloat(costTextField.getText());
        float price = cost * points;
        priceTextField.setText(DV.money(price));

    }
       
    private void refreshTable () {
        tm =  db.createTableModel ("inventory",iTable);
        iTable.setModel(tm);
        setView (vals);
        //tm = iTable.getModel();
        
       
    }
    /** Called to populate form JTextFields 
     * when a user clicks a row on the JTable
     */
    private void populateFields () {

        int selectedRow = iTable.getSelectedRow();
        //Saves the key in the data Object[] taken from the TableModel
        dataOut [0] = (Integer) tm.getValueAt(selectedRow, 0) ; 
        int refKey = (Integer)dataOut[0];
        
        getNote(refKey);

        upcTextField.setText((String) tm.getValueAt(selectedRow, 1));
        nameTextField.setText((String) tm.getValueAt(selectedRow, 2));
        descTextField.setText((String) tm.getValueAt(selectedRow, 3));
        sizeTextField.setText((String) tm.getValueAt(selectedRow, 4));
        weightTextField.setText((String) tm.getValueAt(selectedRow, 5));
        
        //Version 1.5 Change to float
        
        qtyTextField.setText( Float.toString((Float) tm.getValueAt(selectedRow, 6)));

        if (accessKey.checkInventory(300)){
            costTextField.setText(DV.money((Float) tm.getValueAt(selectedRow, 7)));
        }
        priceTextField.setText(DV.money((Float) tm.getValueAt(selectedRow, 8)));

        String category = (String) tm.getValueAt(selectedRow, 9);
       
        if (debug) System.out.println(category);
        
        catTextField.setText(category);
        //Version 1.5
        //        
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        
        lastSaleDate = (Long)tm.getValueAt(selectedRow, 16);
        lastRecvDate = (Long)tm.getValueAt(selectedRow, 17);
        
        String lastSale = df.format(lastSaleDate);
        String recvDate = df.format(lastRecvDate);
        
        saleField.setText(lastSale);
        recvdField.setText(recvDate );
        
        
        jSpinner1.setValue((Integer) tm.getValueAt(selectedRow, 18));
        
        
        //BEGIN "Relational" Code
        
        int col = 10;
        for (int i = 0; i < supp.length; i++) {
            
            supp [i] = Integer.valueOf( (Integer) tm.getValueAt(selectedRow,col ) );
            //System.out.println("supplier " + supp[i]);
            col++;
        }
        
        
        // Suppliers
        if (supp[0] != 0) {
        //System.out.println("Supplier 1 getting record!!!");
        Object [] record = db.getRecord("conn", supp[0]);
        //DV.expose(record);
        if (record != null) {
            supp1TextField.setText( (String) record [1] );  
            }else supp1TextField.setText("");
        
        
        }else supp1TextField.setText("");
        
        //
        
        if (supp[1] != 0) {
            
        Object [] record = db.getRecord("conn", supp[1]);
        
            if (record != null) {
            
                supp2TextField.setText( (String) record [1] );  //bad component name
            }else supp2TextField.setText("");
        
        }else supp2TextField.setText("");
        
        //
        
        if (supp[2] != 0) {
            
        Object [] record = db.getRecord("conn", supp[2]);
        
        if (record != null) {
            
                supp3TextField.setText( (String) record [1] );  //bad component name
            }else supp3TextField.setText("");
        
        
        }else supp3TextField.setText("");
        
        //END Relational Code
        
        
        /* Begin pic code */
        
        /*search for pic records with this dataOut[0] as fkey - col 1 */
        java.util.ArrayList pic_list = db.search("imgref", 1, Integer.toString((Integer) dataOut[0]), false);
        
        if (pic_list == null) {
            
            picRef[0] = new Integer (0);
            picField.setText("");
            viewButton.setEnabled(false);
        }
        else {
            
            /* class var */             
            picRef = db.getRecord("imgref", (Integer) pic_list.get(0) );
            picField.setText( (String)picRef[2] );//show user the path
            
            if (picField.getText().trim().equals("")) viewButton.setEnabled(false);
                else viewButton.setEnabled(true);
        }
               
        /* End pic code  */
        
        //check box
        taxCheckBox.setSelected((Boolean) tm.getValueAt(selectedRow, 13));
        tax2CheckBox.setSelected((Boolean) tm.getValueAt(selectedRow, 14));
        partialBox.setSelected((Boolean) tm.getValueAt(selectedRow, 19));
        availableCheckBox.setSelected((Boolean) tm.getValueAt(selectedRow, 15));
        
        
        computePrices();
        setFieldsEnabled (true);
        //Version 1.5
        if (category.equalsIgnoreCase("Service")){
            serviceBox.setSelected(true);
        }else {
            serviceBox.setSelected(false);
        }
        checkService();
        noteButton.setEnabled(true);
        
    }    


    private void getNote(int invKey){

        Object [] note=null;
        java.util.ArrayList al;
        
        al = db.search("invnotes", 1, Integer.toString(invKey), false);

        if (al != null){

            note = db.getRecord("invnotes", (Integer)al.get(0));
            notesPane.setText((String)note[2]);
            notesPane.setCaretPosition(0);

        }else {

            notesPane.setText("");
        }
    }
    
    private void computePrices() {
        
        if (!qtyTextField.getText().equals("") &&
                !costTextField.getText().equals("") &&
                !priceTextField.getText().equals("")){
        
            //Version 1.5
            //int qty = Integer.parseInt( qtyTextField.getText() );
        
            //float qty = Float.parseFloat( qtyTextField.getText() );
            float qty = Float.parseFloat(qtyTextField.getText());
            
            if (qty > 0) {
        
                float cost = Float.parseFloat(costTextField.getText());
                
                float price = Float.parseFloat( priceTextField.getText());  //error
                
                costTotalLabel.setText(DV.money(cost * qty));
                retailTotalLabel.setText(DV.money(price * qty));
            
            }else {
            
                costTotalLabel.setText("0.00");
                retailTotalLabel.setText("0.00");
                
            }
        }
    }
    
    private void receive() {
        
        if (catTextField.getText().equalsIgnoreCase("Service")) return;
        if (!upcTextField.isEnabled()) return;
        
        InventoryReceivePromptDialog rpd = 
                new InventoryReceivePromptDialog(null, true, descTextField.getText());
        
        boolean negZero = rpd.isNegZero();
        
        if (rpd.getInAmount() == 0) return;
        
        
            float current=0f;
            String _q=qtyTextField.getText();

            if (DV.validFloatString(_q)){
                current = Float.parseFloat(_q);
            }

            /*  */
            if (current < 0){
                
                if (negZero) current = 0.00f;
                
            }
            
            float received = rpd.getInAmount();
 
            rpd = null;
            
            current += received;
        
            qtyTextField.setText(DV.money(current));
        
            //Version 1.5
            lastRecvDate = new java.util.Date().getTime();
            DateFormat df = new SimpleDateFormat();
            recvdField.setText(df.format(new Date (lastRecvDate)));
            saveRecord();           
            findTextField.selectAll();
            findTextField.requestFocus();         
    }
    
    
    /* Public method to grab the needed value in Select Mode */
    public int[] getReturnValue () {
        
        return returnValue;
        
    }    
        
    /* NetBeans-generated actionPerformance methods */
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        savePanel = new javax.swing.JPanel();
        helpBox = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        clearButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        iTable = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        toggleButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();
        groupButton = new javax.swing.JButton();
        labelButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        selectButton = new javax.swing.JButton();
        voidButton = new javax.swing.JButton();
        findTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        receiveModeBox = new javax.swing.JCheckBox();
        tabPanel = new javax.swing.JPanel();
        detailTabPane = new javax.swing.JTabbedPane();
        detailPanel = new javax.swing.JPanel();
        assField = new javax.swing.JLabel();
        costTotalLabel = new javax.swing.JLabel();
        retailTotalLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        sizeTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        supp1TextField = new javax.swing.JTextField();
        catTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        supp3TextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        costTextField = new javax.swing.JTextField();
        descTextField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        tax2CheckBox = new javax.swing.JCheckBox();
        taxCheckBox = new javax.swing.JCheckBox();
        partialBox = new javax.swing.JCheckBox();
        receiveButton = new javax.swing.JButton();
        serviceBox = new javax.swing.JCheckBox();
        qtyTextField = new javax.swing.JTextField();
        priceTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        upcTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        weightTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        supp2TextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        statusPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        saleField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        recvdField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        availableCheckBox = new javax.swing.JCheckBox();
        notesScrollPane = new javax.swing.JScrollPane();
        notesPane = new javax.swing.JTextPane();
        noteButton = new javax.swing.JButton();
        picturesPanel = new javax.swing.JPanel();
        picField = new javax.swing.JTextField();
        picButton = new javax.swing.JButton();
        searchFieldCombo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Inventory Manager");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        savePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        savePanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                savePanelKeyPressed(evt);
            }
        });

        helpBox.setEditable(false);
        helpBox.setText("Click the UPC field to start a new record.");

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Corrupt text.png"))); // NOI18N
        clearButton.setText("Clear");
        clearButton.setToolTipText("Clear/Cancel");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(clearButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(saveButton);

        org.jdesktop.layout.GroupLayout savePanelLayout = new org.jdesktop.layout.GroupLayout(savePanel);
        savePanel.setLayout(savePanelLayout);
        savePanelLayout.setHorizontalGroup(
            savePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, savePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(savePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, helpBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE))
                .addContainerGap())
        );
        savePanelLayout.setVerticalGroup(
            savePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, savePanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(helpBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tablePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(14, 14));
        jScrollPane1.setRequestFocusEnabled(false);

        iTable.setFont(new java.awt.Font("Tahoma", 0, 12));
        iTable.setToolTipText("Click a row and press F9 to receive.");
        iTable.setSelectionBackground(new java.awt.Color(204, 255, 255));
        iTable.setDefaultRenderer(java.lang.Float.class,  new FractionCellRenderer (10, 2, javax.swing.SwingConstants.RIGHT));
        iTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iTableMouseClicked(evt);
            }
        });
        iTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                iTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                iTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(iTable);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        toggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Back.png"))); // NOI18N
        toggleButton.setText("Less Detail");
        toggleButton.setToolTipText("Hide/View Details");
        toggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toggleButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        toggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(toggleButton);

        viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Camera.png"))); // NOI18N
        viewButton.setText("View Pic (F12)");
        viewButton.setEnabled(false);
        viewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        viewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(viewButton);

        groupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Site map.png"))); // NOI18N
        groupButton.setText("Groups");
        groupButton.setToolTipText("Highlight Inventory Items and Hit this Button to Create Product Groups");
        groupButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        groupButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        groupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                groupButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(groupButton);

        labelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Tag.png"))); // NOI18N
        labelButton.setText("Labels");
        labelButton.setToolTipText("Select rows above and press this button to create labels.");
        labelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        labelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(labelButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Delete.png"))); // NOI18N
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Deleting In-Use Inventory Items is Not Recommended");
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteButton);

        org.jdesktop.layout.GroupLayout tablePanelLayout = new org.jdesktop.layout.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
            .add(tablePanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .add(10, 10, 10))
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tablePanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        selectButton.setFont(new java.awt.Font("Tahoma", 0, 12));
        selectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/OK.png"))); // NOI18N
        selectButton.setText("Select");
        selectButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });

        voidButton.setFont(new java.awt.Font("Tahoma", 0, 12));
        voidButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/No.png"))); // NOI18N
        voidButton.setText("None");
        voidButton.setMargin(new java.awt.Insets(2, 10, 2, 10));
        voidButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voidButtonActionPerformed(evt);
            }
        });

        findTextField.setBackground(new java.awt.Color(0, 0, 0));
        findTextField.setFont(new java.awt.Font("Courier New", 1, 13)); // NOI18N
        findTextField.setForeground(new java.awt.Color(0, 255, 0));
        findTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        findTextField.setToolTipText("Press F9 to Toggle Receive Mode");
        findTextField.setCaretColor(new java.awt.Color(51, 255, 255));
        findTextField.setNextFocusableComponent(iTable);
        findTextField.setSelectionColor(new java.awt.Color(0, 255, 51));
        findTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                findTextFieldFocusGained(evt);
            }
        });
        findTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                findTextFieldKeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));

        receiveModeBox.setBackground(new java.awt.Color(204, 204, 204));
        receiveModeBox.setText("Receive Mode");
        receiveModeBox.setToolTipText("Allows You to Quickly Update Inventory Quantities for Scanned Items.");
        receiveModeBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        receiveModeBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                receiveModeBoxMouseClicked(evt);
            }
        });
        receiveModeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiveModeBoxActionPerformed(evt);
            }
        });

        detailTabPane.setFont(new java.awt.Font("Tahoma", 1, 14));

        detailPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        assField.setFont(new java.awt.Font("Tahoma", 0, 10));
        assField.setText("(EAN/UPC/GTIN Barcode)");

        costTotalLabel.setFont(new java.awt.Font("OCR B MT", 0, 12));
        costTotalLabel.setText("0.00");

        retailTotalLabel.setFont(new java.awt.Font("OCR B MT", 0, 12));
        retailTotalLabel.setText("0.00");

        jLabel4.setText("Size");

        sizeTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        sizeTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        sizeTextField.setToolTipText("[15 Char] ");
        sizeTextField.setNextFocusableComponent(weightTextField);
        sizeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                sizeTextFieldFocusGained(evt);
            }
        });

        jLabel11.setText("Supplier 2");

        supp1TextField.setEditable(false);
        supp1TextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        supp1TextField.setToolTipText("Click this field to add a Supplier from Connections");
        supp1TextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supp1TextFieldMouseClicked(evt);
            }
        });

        catTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        catTextField.setToolTipText("Use Standard Categories to Help Sort Products");
        catTextField.setNextFocusableComponent(taxCheckBox);
        catTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                catTextFieldFocusGained(evt);
            }
        });
        catTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                catTextFieldKeyPressed(evt);
            }
        });

        jLabel12.setText("Supplier 3");

        supp3TextField.setEditable(false);
        supp3TextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        supp3TextField.setToolTipText("Click this field to add a Supplier from Connections");
        supp3TextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supp3TextFieldMouseClicked(evt);
            }
        });

        jLabel10.setText("Supplier 1");

        nameTextField.setColumns(16);
        nameTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        nameTextField.setToolTipText("[16 Char] In-House Code System  [Code or UPC REQUIRED]");
        nameTextField.setNextFocusableComponent(descTextField);
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Price");

        costTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        costTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        costTextField.setToolTipText("Item Cost");
        costTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                costTextFieldFocusGained(evt);
            }
        });

        descTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        descTextField.setToolTipText("[50 Char] Appears on the Invoices and Quotes  [REQUIRED FIELD]");
        descTextField.setNextFocusableComponent(sizeTextField);

        tax2CheckBox.setFont(new java.awt.Font("Tahoma", 0, 12));
        tax2CheckBox.setText("Tax 2");
        tax2CheckBox.setToolTipText("See Invoice Settings");
        tax2CheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tax2CheckBox.setNextFocusableComponent(availableCheckBox);

        taxCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12));
        taxCheckBox.setText("Tax 1");
        taxCheckBox.setToolTipText("See Invoice Settings");
        taxCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        taxCheckBox.setNextFocusableComponent(tax2CheckBox);

        partialBox.setFont(new java.awt.Font("Tahoma", 0, 12));
        partialBox.setText("Allow Partial Quantities");
        partialBox.setToolTipText("Allows Fractional Quantities to be Sold");

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(taxCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tax2CheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(partialBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(taxCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tax2CheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(partialBox))
                .addContainerGap())
        );

        receiveButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        receiveButton.setText("Receive");
        receiveButton.setToolTipText("This button updates the Quanity and saves the record");
        receiveButton.setEnabled(false);
        receiveButton.setMargin(new java.awt.Insets(1, 6, 1, 4));
        receiveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiveButtonActionPerformed(evt);
            }
        });

        serviceBox.setText("Service");
        serviceBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        serviceBox.setEnabled(false);
        serviceBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceBoxActionPerformed(evt);
            }
        });

        qtyTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        qtyTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        qtyTextField.setToolTipText("Quantity On Hand");

        priceTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        priceTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        priceTextField.setToolTipText("Selling Price");
        priceTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                priceTextFieldFocusGained(evt);
            }
        });

        jLabel7.setText("Cost");

        upcTextField.setColumns(14);
        upcTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        upcTextField.setToolTipText("Click Here to Create a New Item [14 Char] [Code or UPC REQUIRED]");
        upcTextField.setNextFocusableComponent(nameTextField);
        upcTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                upcTextFieldMouseClicked(evt);
            }
        });
        upcTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                upcTextFieldFocusGained(evt);
            }
        });

        jLabel1.setText("UPC");

        jLabel2.setText("Code");

        jLabel3.setText("Desc.");

        jLabel9.setText("Category");

        weightTextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        weightTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        weightTextField.setToolTipText("[15 Char] Input a Raw Number to Allow Weight Calculations on the Invoices");
        weightTextField.setNextFocusableComponent(qtyTextField);
        weightTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                weightTextFieldFocusGained(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial Black", 1, 12));
        jLabel16.setText("=  ");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Quantity");

        supp2TextField.setEditable(false);
        supp2TextField.setFont(new java.awt.Font("Tahoma", 0, 12));
        supp2TextField.setToolTipText("Click this field to add a Supplier from Connections");
        supp2TextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                supp2TextFieldMouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial Black", 1, 12));
        jLabel14.setText("=  ");

        jLabel5.setText("Weight");

        jLabel17.setForeground(new java.awt.Color(255, 0, 0));
        jLabel17.setText("*");

        org.jdesktop.layout.GroupLayout detailPanelLayout = new org.jdesktop.layout.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(detailPanelLayout.createSequentialGroup()
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jLabel12)
                                .add(jLabel11)
                                .add(jLabel10))
                            .add(jLabel4)
                            .add(jLabel3)
                            .add(jLabel5)
                            .add(jLabel6)
                            .add(jLabel7)
                            .add(jLabel8)
                            .add(jLabel9)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(detailPanelLayout.createSequentialGroup()
                                .add(upcTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 87, Short.MAX_VALUE)
                                .add(assField))
                            .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(detailPanelLayout.createSequentialGroup()
                                .add(catTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 203, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(serviceBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                            .add(detailPanelLayout.createSequentialGroup()
                                .add(descTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 293, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel17))
                            .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, supp1TextField)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, supp2TextField)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, supp3TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(detailPanelLayout.createSequentialGroup()
                                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, sizeTextField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, weightTextField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, qtyTextField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, costTextField)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, priceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(detailPanelLayout.createSequentialGroup()
                                            .add(jLabel16)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(retailTotalLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, detailPanelLayout.createSequentialGroup()
                                            .add(jLabel14)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                            .add(costTotalLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .add(receiveButton)))))
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(upcTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(assField))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(descTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(detailPanelLayout.createSequentialGroup()
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(sizeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(weightTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(qtyTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(costTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel8)
                            .add(priceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(detailPanelLayout.createSequentialGroup()
                        .add(receiveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel14)
                            .add(costTotalLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel16)
                            .add(retailTotalLabel))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(catTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(serviceBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(supp1TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(supp2TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(supp3TextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        detailTabPane.addTab("Detail", detailPanel);

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel15.setText("Last Sale");

        saleField.setEditable(false);
        saleField.setFont(new java.awt.Font("Courier New", 0, 12));
        saleField.setToolTipText("Last Time this Item was Sold on an Invoice ");

        jLabel19.setText("Last Received");

        recvdField.setEditable(false);
        recvdField.setFont(new java.awt.Font("Courier New", 0, 12));
        recvdField.setToolTipText("The Last Time Inventory was Added with the Recieve Function");

        jLabel20.setText("Reorder @");

        jSpinner1.setToolTipText("Lowest Amount of Available Stock Before Reorder");

        availableCheckBox.setText("Unavailable");
        availableCheckBox.setToolTipText("Unavailable items fire a warning when you try to sell them");
        availableCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        availableCheckBox.setNextFocusableComponent(picButton);

        notesPane.setBackground(new java.awt.Color(219, 216, 216));
        notesPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        notesPane.setEditable(false);
        notesPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                notesPaneMouseClicked(evt);
            }
        });
        notesScrollPane.setViewportView(notesPane);

        noteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Message.png"))); // NOI18N
        noteButton.setText("Edit Notes");
        noteButton.setToolTipText("Keep Small Notes About Each Item");
        noteButton.setEnabled(false);
        noteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusPanelLayout.createSequentialGroup()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(notesScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                    .add(statusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel19)
                            .add(jLabel15)
                            .add(jLabel20))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(saleField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .add(recvdField)
                            .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 94, Short.MAX_VALUE)
                        .add(availableCheckBox))
                    .add(statusPanelLayout.createSequentialGroup()
                        .addContainerGap(279, Short.MAX_VALUE)
                        .add(noteButton)))
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(saleField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(recvdField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(availableCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(notesScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(noteButton)
                .addContainerGap())
        );

        detailTabPane.addTab("Status", statusPanel);

        picturesPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        picField.setColumns(50);
        picField.setFont(new java.awt.Font("Courier New", 0, 11));
        picField.setToolTipText("Picture Reference");
        picField.setNextFocusableComponent(saveButton);

        picButton.setFont(new java.awt.Font("Tahoma", 0, 10));
        picButton.setText("Picture");
        picButton.setToolTipText("Browse to a Picture");
        picButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
        picButton.setNextFocusableComponent(picField);
        picButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                picButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout picturesPanelLayout = new org.jdesktop.layout.GroupLayout(picturesPanel);
        picturesPanel.setLayout(picturesPanelLayout);
        picturesPanelLayout.setHorizontalGroup(
            picturesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(picturesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(picButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(picField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );
        picturesPanelLayout.setVerticalGroup(
            picturesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(picturesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(picturesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(picButton)
                    .add(picField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(347, Short.MAX_VALUE))
        );

        detailTabPane.addTab("Pictures", picturesPanel);

        org.jdesktop.layout.GroupLayout tabPanelLayout = new org.jdesktop.layout.GroupLayout(tabPanel);
        tabPanel.setLayout(tabPanelLayout);
        tabPanelLayout.setHorizontalGroup(
            tabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detailTabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
        );
        tabPanelLayout.setVerticalGroup(
            tabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detailTabPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
        );

        searchFieldCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "UPC", "Code", "Description", "Size", "Weight", "Category" }));
        searchFieldCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldComboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tabPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(savePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(selectButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(layout.createSequentialGroup()
                                .add(93, 93, 93)
                                .add(jLabel13))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(voidButton)
                                .add(18, 18, 18)
                                .add(searchFieldCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(findTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(17, 17, 17))
                    .add(receiveModeBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(selectButton)
                            .add(jLabel13)
                            .add(voidButton)
                            .add(findTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(searchFieldCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(receiveModeBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tablePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(tabPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(savePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void serviceBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serviceBoxActionPerformed
        if (serviceBox.isSelected()){
            catTextField.setText("Service");
        }else {
            catTextField.setText("");
        }
        checkService();
    }//GEN-LAST:event_serviceBoxActionPerformed

   private void checkService() {
       
       boolean enabled = true;
       
       if (serviceBox.isSelected()) enabled = false;

            if (!enabled){
                sizeTextField.setText("0");
                weightTextField.setText("0");
            }
           catTextField.setEnabled(enabled);
                      
           qtyTextField.setEnabled(enabled);
                      
           sizeTextField.setEnabled(enabled);
                      
           weightTextField.setEnabled(enabled);
                      
           receiveButton.setEnabled(enabled);
            
   }
    
    
    private void receiveModeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiveModeBoxActionPerformed
        
        findTextField.requestFocus();
        
    }//GEN-LAST:event_receiveModeBoxActionPerformed

    private void receiveModeBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_receiveModeBoxMouseClicked
        
        findTextField.requestFocus();
        
    }//GEN-LAST:event_receiveModeBoxMouseClicked

    private void noteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteButtonActionPerformed

        if ((Integer)dataOut[0] < 1) return;
        new InventoryNoteDialog(null,true, db, (Integer)dataOut[0], descTextField.getText());
        
        
    }//GEN-LAST:event_noteButtonActionPerformed

    private void manageInventoryNotes() {
        
        
        
    }

    private void calculateTotalInventoryCost() {

        int rows = tm.getRowCount();
        if (rows < 1) return;
        float qty_TMP=0f;
        float cost_TMP;
        float total=0f;

        for (int r = 0; r < rows; r++){
           qty_TMP = (Float)tm.getValueAt(r, 6);//qty
           cost_TMP = (Float)tm.getValueAt(r, 7);//cost per
           if (qty_TMP > 0){
                total += (qty_TMP * cost_TMP);
           }
        }

      
    }
    
    private void groupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_groupButtonActionPerformed
        
        int [] a = iTable.getSelectedRows();
          
        int [] v = new int [a.length];
        
        for (int i = 0; i < v.length; i++){
            
            v[i] = (Integer)iTable.getModel().getValueAt(a[i], 0);
                        
        }    
            new InventoryGroupDialog (null,true,v, workingPath).setVisible(true);
           
        
    }//GEN-LAST:event_groupButtonActionPerformed

    private void catTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_catTextFieldKeyPressed
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            
            picField.requestFocus();
            
        }
        
        
    }//GEN-LAST:event_catTextFieldKeyPressed

    private void savePanelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_savePanelKeyPressed
        
        if (iTable.getSelectedRow() > -1 ){
            
            
            viewPic();
            
            
        }
        
    }//GEN-LAST:event_savePanelKeyPressed

    private void picButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_picButtonActionPerformed
        
         try {
        
            java.io.File file;
            String f;
        
            file = new java.io.File(picField.getText());
            JFileChooser fileChooser = new JFileChooser(file);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = fileChooser.showOpenDialog(null);
            java.io.File curFile = fileChooser.getSelectedFile();
           
            if (returnVal == JFileChooser.CANCEL_OPTION) return;
            
            if (curFile == null ) return  ;
            
            picField.setText(curFile.getPath());
            
            picField.setText(DV.verifyPath(picField.getText()));
            
            
        }catch (Exception e) {
            
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem with the file system.");
            
        }
        
        
        
        
    }//GEN-LAST:event_picButtonActionPerformed

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed

        if (iTable.getSelectedRow() > -1  && !picField.getText().equals("")){
            
            viewPic();
            
        }
        
    }//GEN-LAST:event_viewButtonActionPerformed

    private void viewPic () {
        
        new PictureDialog (null, true, picField.getText(), true);
        
        iTable.requestFocus();
        
    }
    
    private void labelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelButtonActionPerformed
        
        if (iTable.getSelectedRow() > -1){
        
            new InventoryLabelsDialog (null, true, iTable.getModel(), iTable.getSelectedRows(), workingPath, props);
            
        }else {
            
            javax.swing.JOptionPane.showMessageDialog(null, "Select rows from the Inventory table to create labels.");
            
        }
        
        
        
    }//GEN-LAST:event_labelButtonActionPerformed

    private void iTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iTableKeyReleased
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F12){
           
            if (!viewButton.isEnabled()) return;
            
            viewPic();
            return;
        }
        
        if (iTable.getSelectedRow() > -1){
            
            populateFields ();
        
            
            
            saveButton.setEnabled(true);        
            
        }
        
        
    }//GEN-LAST:event_iTableKeyReleased

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F9 && receiveButton.isEnabled()){
            
            receive();
            
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F11){

            togglePanels();

        }
        
        
        
    }//GEN-LAST:event_formKeyPressed

    private void receiveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiveButtonActionPerformed
        
        receive();
        
    }//GEN-LAST:event_receiveButtonActionPerformed

    private void findTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_findTextFieldFocusGained
        findTextField.selectAll();
    }//GEN-LAST:event_findTextFieldFocusGained

    private void supp3TextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supp3TextFieldMouseClicked
        
        if (upcTextField.isEnabled())  {
            
        
        MyConnectionsApp cd = new MyConnectionsApp (this.parentWin, true, application, true,false,true);
        //cd.setVisible(true);
        int rv;
        rv = cd.getReturnValue() ;  //real value        
        
        cd.dispose(); //dont call dispose before finsihing with method
        cd = null;
        
        if (rv == -1) return;  //user closed Connections window - do nothing
        
        if (rv == 0) {
            
            supp[2] = new Integer (0);  //none
            
        }else supp[2] = new Integer (rv);  //selected Connection record
        
        
        
        if (supp[2] > 0) {  //
        Object [] suppRecord = db.getRecord("conn",supp[2]);
        supp3TextField.setText( (String) suppRecord[1] );
        
        } else supp3TextField.setText("");
        
    }
        
        
    }//GEN-LAST:event_supp3TextFieldMouseClicked

    private void supp2TextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supp2TextFieldMouseClicked
        
        if (upcTextField.isEnabled())  {
            
        
        MyConnectionsApp cd = new MyConnectionsApp (this.parentWin, true, application, true, false, true);
        //cd.setVisible(true);
        int rv;
        rv = cd.getReturnValue() ;  //real value
        
        
        cd.dispose(); //dont call dispose before finsihing with method
        cd = null;
        
        if (rv == -1) return;  //user closed Connections window - do nothing
        
        if (rv == 0) {
            
            supp[1] = new Integer (0);  //none
            
        }else supp[1] = new Integer (rv);  //selected Connection record
        
        
        
        if (supp[1] > 0) {
        Object [] suppRecord = db.getRecord("conn",supp[1]);
        supp2TextField.setText( (String) suppRecord[1] );
        
        } else supp2TextField.setText("");
        
        }
        
    }//GEN-LAST:event_supp2TextFieldMouseClicked


    private void supp1TextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_supp1TextFieldMouseClicked
        
          
        if (upcTextField.isEnabled())  {
            
        
        MyConnectionsApp cd = new MyConnectionsApp (parentWin, true, application, true, false, true);
        //cd.setVisible(true);
        int rv;
        rv = cd.getReturnValue() ;  //real value
        //System.out.println("1 Conn return value from inventory: "+rv);
        
        cd.dispose(); //dont call dispose before finsihing with method
        cd = null;    
        
        if (rv == -1) return;  //user closed Connections window - do nothing
        
        if (rv == 0) {
            
            supp[0] = new Integer (0);  //none
            
        }else supp[0] = new Integer (rv);  //selected Connection record
        
        //System.out.println("Supplier 1 value from inventory: "+supp[0]);
        
        if (supp[0] > 0) {
        Object [] suppRecord = db.getRecord("conn",supp[0]);
        //DV.expose(suppRecord);
        supp1TextField.setText( (String) suppRecord[1] );
       
        } else supp1TextField.setText("");
        
        }

                
    }//GEN-LAST:event_supp1TextFieldMouseClicked

    
    private void toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonActionPerformed
        
        togglePanels();
        
        
    }//GEN-LAST:event_toggleButtonActionPerformed

    private void togglePanels(){
        if (savePanel.isVisible()) {

            savePanel.setVisible(false);
            this.tabPanel.setVisible(false);
            toggleButton.setText("More Detail");
            //totalInventoryCostLabel.setVisible(false);
            toggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Forward.png")));

        }else {

            savePanel.setVisible(true);
            this.tabPanel.setVisible(true);
            toggleButton.setText("Less Detail");
            //totalInventoryCostLabel.setVisible(true);
            toggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Back.png")));
        }
        findTextField.requestFocus();
    }

    private void iTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iTableKeyPressed
        
        if (selectMode) {
            
            if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
             if (iTable.getSelectedRow() > -1  && selectMode) {  
        
                 setReturnValue();
        
                this.setVisible(false);
             }
            }
        }
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F9 && receiveButton.isEnabled()){
            
                receive();
        }
                
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_HOME){
            
            new InventoryNoteDialog(null,true, db, (Integer)dataOut[0], descTextField.getText());
            
        }
        
    }//GEN-LAST:event_iTableKeyPressed

    private void upcTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_upcTextFieldMouseClicked
        
        
        if ( !upcTextField.isEnabled()){

            if (!accessKey.checkInventory(200)){
                accessKey.showMessage("Create");
                return;
            }

            /* A new record has begun */
            dataOut [0] = new Integer (0);
            noteButton.setEnabled(false);
            picRef [0] = new Integer(0);
            
            clearFields();
            setFieldsEnabled(true);
            
            //Version 1.5
            serviceBox.setEnabled(true);
            
            saveButton.setEnabled(true);
            upcTextField.requestFocus();
            
            
        }
    }//GEN-LAST:event_upcTextFieldMouseClicked

    private void findTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_findTextFieldKeyPressed
        
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F9){
            
            boolean state = receiveModeBox.isSelected();
            
            if (state){
                
                receiveModeBox.setSelected(false);
                
            }else {
                
                receiveModeBox.setSelected(true);
                
            }
            
            findTextField.requestFocus();
            return;
            
        }
        
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            search();
        }
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_F11){
            togglePanels();
        }
    }//GEN-LAST:event_findTextFieldKeyPressed

    private void iTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iTableMouseClicked
    int mouseButton = evt.getButton();
    if (mouseButton == evt.BUTTON2 || mouseButton == evt.BUTTON3) return;    
    //Version 1.5
       if (selectMode){ 
        
           if (evt.getClickCount() == 2){
                       
            int row = iTable.rowAtPoint(new Point(evt.getX(), evt.getY()));
            
             if (iTable.getSelectedRow() > -1) {  
                
                returnValue = new int [1];
                returnValue[0] = (Integer) tm.getValueAt(row, 0);
                //DV.expose(DV.getRow(tm, row));
                //db = null;
        
                this.setVisible(false);
             }
            
           }
                   
       }
       
                    
            populateFields ();
            
            saveButton.setEnabled(true);
           
       
       
       
      
        
    }//GEN-LAST:event_iTableMouseClicked

    private void voidButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voidButtonActionPerformed
        
        returnValue = new int [] {-1};
        setVisible(false);
        
    }//GEN-LAST:event_voidButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

        if (!accessKey.checkInventory(500)){
          
            accessKey.showMessage("Delete");
            return;
        }
        
        if (iTable.getSelectedRow() > -1) {
            
        int a = JOptionPane.showConfirmDialog(this, "Delete Selected Record?","DELETE",  JOptionPane.YES_NO_OPTION);
          
            if (a == 0){
            
            /*Delete current inventory record. */
            Integer key = (Integer) tm.getValueAt(iTable.getSelectedRow(), 0) ;
            db.removeRecord("inventory",key);
            
            /*Delete pic ref */
            if (!picField.getText().trim().equals("")) {
                
                /*Search for image reference record with this inventory's key */
                java.util.ArrayList pr = db.search("imgref", 1,Integer.toString(key), false);
                
                if (pr != null) {
                                        
                    /*Get the actual record froma table model  */
                    TableModel pic_ref = db.createTableModel("imgref", pr, false);
                    
                    /*Access the first row,col to get the key of this imgref to delete it from the db  */
                    db.removeRecord("imgref", (Integer)pic_ref.getValueAt(0,0));
                    
                }
                
                
            }
            
            refreshTable ();
            clearFields();
            this.setFieldsEnabled(false);
            this.saveButton.setEnabled(false);
            
            
            }
         }
        //calculateTotalInventoryCost();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        
        saveButton.setEnabled(false);
               
        clearFields();
        serviceBox.setEnabled(false);
        setFieldsEnabled(false);
        dataOut = new Object [20];
        
    }//GEN-LAST:event_clearButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
            
        if (validateForm()) saveRecord();
       
    }//GEN-LAST:event_saveButtonActionPerformed
 
    private void saveRecord () {

        int z = (Integer)dataOut[0];
        if (z == 0){
            if (!accessKey.checkInventory(200)){
                accessKey.showMessage("Create");
                clearFields();
                return;
            }
        }

        if (z > 0){
            if (!accessKey.checkInventory(400)){
                accessKey.showMessage("Edit");
                clearFields();
                return;
            }
        }
        
        //call validateForm() before this method
        if (upcTextField.getText().trim().equals("") && nameTextField.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this,
                    "You must enter a UPC or Code. Code is displayed on invoices.",
                    "Need Reference Data",  JOptionPane.OK_OPTION);
                    return;
        }
        //make sure CODE and UPC are unique before adding a new record.
        
                dataOut [1] = new String ( upcTextField.getText().trim() );
                dataOut [2] = new String ( nameTextField.getText().trim());   //code
                dataOut [3] = new String ( descTextField.getText().trim() );
                dataOut [4] = new String ( sizeTextField.getText().trim() );
                dataOut [5] = new String ( weightTextField.getText().trim() );
        
                //Make sure the qty, cost & price fields get set to some number if none are entered
                if ( DV.validFloatString(qtyTextField.getText().trim())) {
                   dataOut [6] = new Float ( qtyTextField.getText().trim() ); 
             
             } else {
                    JOptionPane.showMessageDialog(this,"Make sure QTY is a number.","Form Problem!",  JOptionPane.OK_OPTION);
                 return;
             } 
        
             if ( DV.validFloatString(costTextField.getText().trim())) {
                    dataOut [7] = new Float ( Tools.round(Float.parseFloat(costTextField.getText().trim()) ));
             } else  {
                   JOptionPane.showMessageDialog(this,"Make sure COST is a number.","Form Problem!",  JOptionPane.OK_OPTION);
                    return;
                }

                if ( DV.validFloatString(priceTextField.getText().trim())) {
                   dataOut [8] = new Float ( Tools.round( Float.parseFloat(priceTextField.getText().trim()) ));
                } else {
                    JOptionPane.showMessageDialog(this,"Make sure PRICE is a number.","Form Problem!",  JOptionPane.OK_OPTION);
                   return;
                }

                dataOut [9] = new String ( catTextField.getText().trim() );
        
                dataOut [10] = new Integer (supp[0]);
                dataOut [11] = new Integer (supp[1]);
                dataOut [12] = new Integer (supp[2]);
                dataOut [13] = new Boolean (taxCheckBox.isSelected()); 
                dataOut [14] = new Boolean (tax2CheckBox.isSelected());
                dataOut [19] = new Boolean (partialBox.isSelected());
                dataOut [15] = new Boolean (availableCheckBox.isSelected());
                
                if ((Integer) dataOut [0] == 0) dataOut [16] = new Long (0);
                else dataOut [16] = new Long (lastSaleDate);
                
                if ((Integer) dataOut [0] == 0) dataOut [17] = new Long (new java.util.Date().getTime());
                else dataOut [17] = new Long (lastRecvDate);
                
                
                int v = (Integer) jSpinner1.getValue();  //reorder at
                
                dataOut[18] = new Integer (v);
                
                //System.out.println("EXPOSE for Inventory::saveRecord()");
                //DV.expose(dataOut);  //debug
                int currentKey = (Integer)dataOut[0];
                
                int ikey = db.saveRecord("inventory", dataOut, false);
                
                /* Need to update the table model to reflect the changes instead of a total refresh */
                if (currentKey > 0){
                int changeRow = iTable.getSelectedRow();

                int colCount = iTable.getModel().getColumnCount();

                for (int c = 0; c < colCount; c++){
                    iTable.getModel().setValueAt(dataOut[c], changeRow, c);
               }

                }else {

                    refreshTable();

                }
                /* Save picture path info */
                //picRef [0] = already set
                picRef [1] = new Integer(ikey);
                picRef [2] = new String (picField.getText().trim());
                picRef [3] = new Boolean (true);  //future use for building catalogs and web pages
                
                db.saveRecord("imgref", picRef, false);
                
                /* Cleanup */
                saveButton.setEnabled(false);                
                
                dataOut = new Object [20];
                picRef = new Object [4];
                
                
                /* Get description and save it for later */
                String last_desc = descTextField.getText().trim();
                                        
                clearFields();     // clean up   
                //refreshTable();
                setFieldsEnabled(false); 
                //calculateTotalInventoryCost();
                int the_row = DV.searchTable(iTable.getModel(), 3, last_desc);
                
                iTable.changeSelection(the_row,0,false,false);
                last_desc = null;  //help with memory
              
        this.normalizeCategoryList(catTextField.getText().trim());
        
        
    }
    
    private boolean validateForm () {
     
     
        if (DV.validFloatString(costTextField.getText().trim()));
        else {
            
  JOptionPane.showMessageDialog(this, "Make sure Cost is a valid number (0.00). ","Form Problem!",  JOptionPane.OK_OPTION);
            return false;
        }
        if (DV.validFloatString(priceTextField.getText().trim()));
        else {
            
  JOptionPane.showMessageDialog(this, "Make sure Price is a valid number (0.00). ","Form Problem!",  JOptionPane.OK_OPTION);
            return false;
        }
                      
       
        java.util.ArrayList al = null;
                   
        
        if (nameTextField.getText().trim().equalsIgnoreCase("return")){
            
            JOptionPane.showMessageDialog(this, "The word 'return' cannot be used for a Code!","Invalid Code",  JOptionPane.OK_OPTION);
                return false;
            
            
        }
            al = db.search("inventory", 3, descTextField.getText().trim(), false);
            
            if (al != null){
                
                int k = (Integer) al.get(0);
                                
                if (k != (Integer) dataOut[0]){
            
                JOptionPane.showMessageDialog(this, "A record with this DESC. already exsists.","No Duplicates",  JOptionPane.OK_OPTION);
                return false;
            
                }
            }
        
           
            /* Look for duplicate CODE */
            al = db.search("inventory", 2, nameTextField.getText().trim(), false);
            
            if (al != null){
            
                if (al.size() > 1) {
                    
                    JOptionPane.showMessageDialog(this, "Multiple records with this CODE already exsist. (2 Maximum)","No More Duplicates",  JOptionPane.OK_OPTION);
                    return false;
            
                    
                }
                
                int k = (Integer) al.get(0);
                                
                if (k != (Integer) dataOut[0]){
                                
                    int a = JOptionPane.showConfirmDialog(this, "A record with this CODE already exists, is this OK ?","WARNING",  JOptionPane.YES_NO_OPTION);
          
                    if (a == 0);
                    else {
            
                        return false;
                
                    }
                   
                }
                
            }
        
            /* Look for duplicate UPC */
            al = db.search("inventory", 1, upcTextField.getText().trim(), false);
            if (al != null){
            
                
                if (al.size() > 1) {
                    
                    JOptionPane.showMessageDialog(this, "Multiple records with this UPC already exsist. (2 Maximum)","No More Duplicates",  JOptionPane.OK_OPTION);
                    return false;
                                
                }
                
                int k = (Integer) al.get(0);
                                
                if (k != (Integer) dataOut[0]){
                    
                
                    int a = JOptionPane.showConfirmDialog(this, "A record with this UPC already exists, is this OK ?","WARNING",  JOptionPane.YES_NO_OPTION);
          
                    if (a == 0);
                    else {
            
                     return false;
                
                    }
                }
            }
            
              
        return true;
    }
    
    
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
        
        if (iTable.getSelectedRow() > -1) {  
        
            setReturnValue();
            //this.notifyAll();
            
        db = null;
        
        this.setVisible(false);
        
        }
        
    }//GEN-LAST:event_selectButtonActionPerformed

    private void notesPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_notesPaneMouseClicked

        if (evt.getClickCount() == 2){
            Point p = evt.getPoint();

            int tableRow = iTable.getSelectedRow();
            if (tableRow > 0) {

                this.populateFields();
                int k = (Integer)iTable.getModel().getValueAt(tableRow, 0);
                new InventoryNoteDialog(null,true, db, (Integer)dataOut[0], descTextField.getText());
                this.getNote(k);
            }


           }

    }//GEN-LAST:event_notesPaneMouseClicked

    private void priceTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_priceTextFieldFocusGained
        calculateMarkup();
        selectAll(evt);
    }//GEN-LAST:event_priceTextFieldFocusGained

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped

    }//GEN-LAST:event_formKeyTyped

    private void upcTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_upcTextFieldFocusGained
       selectAll(evt);
    }//GEN-LAST:event_upcTextFieldFocusGained

    private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
        selectAll(evt);
    }//GEN-LAST:event_nameTextFieldFocusGained

    private void sizeTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sizeTextFieldFocusGained
        selectAll(evt);
    }//GEN-LAST:event_sizeTextFieldFocusGained

    private void weightTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_weightTextFieldFocusGained
        selectAll(evt);
    }//GEN-LAST:event_weightTextFieldFocusGained

    private void costTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_costTextFieldFocusGained
        selectAll(evt);
    }//GEN-LAST:event_costTextFieldFocusGained

    private void catTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_catTextFieldFocusGained
        selectAll(evt);
    }//GEN-LAST:event_catTextFieldFocusGained

    private void searchFieldComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldComboActionPerformed
        findTextField.requestFocus();
    }//GEN-LAST:event_searchFieldComboActionPerformed

    private void selectAll(java.awt.event.FocusEvent evt){
        JTextField tf = (JTextField)evt.getSource();
        tf.selectAll();
    }

    private void setReturnValue () {
        
            int tmp [] = iTable.getSelectedRows(); 
            returnValue = new int [tmp.length];
            
            for (int i = 0; i < tmp.length; i++){
               
                if (debug) System.out.println("InventoryDialog:Selected Row " + tmp[i]);
                returnValue [i] = (Integer) tm.getValueAt(tmp[i], 0);
                //System.out.println(returnValue[i]);
                /* build inventoryDAO and store in an ArrayList */
                //store in application DAO list
                /* call the implemented method! which triggers action on the list of DAOs */
                
                /* Dispose this */
            }  
    }
    
    /* My private local variables and object references */

    private GlobalApplicationDaemon application;
    private DbEngine db;
    private int [] returnValue = new int [] {-1};
    private Object [] dataOut = new Object [20];
    private Object [] picRef = new Object [4];
    private long lastRecvDate;
    private long lastSaleDate;
    private boolean service = false;
    private java.awt.Image winIcon;
    
    //                     k s w a  t1 t2 s1s2s3c upc
    private int [] vals = {0,3,3,12,11,10,9,8,7,4,0,5,5,5,5}; //col view removal
    private java.awt.Frame parentWin;
    private int [] supp = new int [3];  //supplier keys
    private boolean selectMode;
    private TableModel tm;
    private Settings props;
    private String nl = System.getProperty("line.separator");
    
    
    /* NetBeans generated privates */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel assField;
    private javax.swing.JCheckBox availableCheckBox;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField catTextField;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField costTextField;
    private javax.swing.JLabel costTotalLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField descTextField;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JTabbedPane detailTabPane;
    private javax.swing.JTextField findTextField;
    private javax.swing.JButton groupButton;
    private javax.swing.JTextField helpBox;
    private javax.swing.JTable iTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton labelButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton noteButton;
    private javax.swing.JTextPane notesPane;
    private javax.swing.JScrollPane notesScrollPane;
    private javax.swing.JCheckBox partialBox;
    private javax.swing.JButton picButton;
    private javax.swing.JTextField picField;
    private javax.swing.JPanel picturesPanel;
    private javax.swing.JTextField priceTextField;
    private javax.swing.JTextField qtyTextField;
    private javax.swing.JButton receiveButton;
    private javax.swing.JCheckBox receiveModeBox;
    private javax.swing.JTextField recvdField;
    private javax.swing.JLabel retailTotalLabel;
    private javax.swing.JTextField saleField;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel savePanel;
    private javax.swing.JComboBox searchFieldCombo;
    private javax.swing.JButton selectButton;
    private javax.swing.JCheckBox serviceBox;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField supp1TextField;
    private javax.swing.JTextField supp2TextField;
    private javax.swing.JTextField supp3TextField;
    private javax.swing.JPanel tabPanel;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JCheckBox tax2CheckBox;
    private javax.swing.JCheckBox taxCheckBox;
    private javax.swing.JButton toggleButton;
    private javax.swing.JTextField upcTextField;
    private javax.swing.JButton viewButton;
    private javax.swing.JButton voidButton;
    private javax.swing.JTextField weightTextField;
    // End of variables declaration//GEN-END:variables
    
}
