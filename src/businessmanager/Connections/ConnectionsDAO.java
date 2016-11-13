/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package businessmanager.Connections;
//import EDI.EDIPushDAO;
import RuntimeManagement.GlobalApplicationDaemon;
import datavirtue.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.*;
/**
 *
 * @author Data Virtue
 */


public class ConnectionsDAO {
private boolean debug = false;
private GlobalApplicationDaemon application;
    /* Initialize new Connection record */
    public ConnectionsDAO(DbEngine dbe, GlobalApplicationDaemon g) {

        db = dbe;
        application = g;
        populateDAO(0);
    }

    /* Initialize with exsisting record */
    public ConnectionsDAO(DbEngine dbe, GlobalApplicationDaemon g, int key){
        db = dbe;
        application=g;
        populateDAO(key);
    }

    public void populateDAO(int key){
        if (key == 0){            
            conn[0] = new Integer(0);
        }else {
            conn = db.getRecord("conn", key);
            retrieveShipToTable(key);
        }
    }

    public TableModel getMyConnectionsTable(JTable table){
        return db.createTableModel ("conn",table);
    }

    public TableModel getCustomerTable(JTable table){

        java.util.ArrayList al = db.search ("conn", 15, "true", false);

            if (al == null) return new DefaultTableModel();

            return db.createTableModel ("conn",al,table);
    }

    public TableModel getVendorTable(JTable table){
        java.util.ArrayList al = db.search ("conn", 16, "true", false);

            if (al == null) return new DefaultTableModel();

            return db.createTableModel ("conn",al,table);
        
    }

    
    public TableModel getUnpaidTable(JTable table){
        ArrayList<Integer> customers = new ArrayList();
        ArrayList<Integer> al = db.search("invoice", 8, "false", false);
                
        if (al != null){
                                  
            DefaultTableModel tm = (DefaultTableModel)db.createTableModel("invoice", al, false);
            
            int k = 0; boolean vd = false;
            for (int r = 0; r < tm.getRowCount(); r++){
                k = (Integer)tm.getValueAt(r, 11);
                if (k > 0) {
                    if (!DV.arrayListContains(customers, k)){//check for voids - skip them
                        vd = (Boolean)tm.getValueAt(r, 9);
                        if (!vd) customers.add(k); //add  customer if they have unpaid and are not voided
                    } //store customer keys in arraylist
                }else {
                    //unpaid SALE!!
                }   
            }
            customers.trimToSize();
        }else {
            return new DefaultTableModel();
        }
        
        if (customers != null && customers.size() > 0) {
            return db.createTableModel ("conn", customers, table);
        }
        return new DefaultTableModel();
    }
    
    private void retrieveShipToTable(int key){
        
        ArrayList al = db.nSearch("connship", 1, key, key, false);
        if (al != null){            
            shipToTable = db.createTableModel("connship", al, false);
            if (debug) System.out.println("CDAO retreiveTable row count "+shipToTable.getRowCount());

            for (int x = 0; x < shipToTable.getRowCount(); x++){

                shiptos.add(new ShipToDAO(db, (Integer)shipToTable.getValueAt(x, 0)));

            }

        }else {            
            shipToTable = null;
            shiptos = new ArrayList();
        }        
    }

    public int saveRecord(){

        int x = db.saveRecord("conn", conn, false);
        
        /* Perform EDI */
        /*EDIPushDAO dao = new EDIPushDAO(application);
        dao.startStream();
        dao.addData("conn", conn);
        dao.endStream();
        dao.sendEmail();*/
                
        return x;
        
        
    }

    public boolean deleteRecord(int key){

        if (checkInventory(key)) {

            return db.removeRecord("conn",key);

        } else {
            JOptionPane.showMessageDialog(null, "This record cannot be deleted, it is referenced in INVENTORY!");
            return false;
        }

    }

    private boolean checkInventory (int key) {

        //scan inventory field 10,11,12 for key
        //return false if found anywhere
       for (int f = 10; f < 13; f++) {

            if (db.search("inventory", f, Integer.toString(key), false) != null) return false;

       }
       return true;
   }
    public ArrayList search(int search_column, String search_text){

        return db.search("conn", search_column,search_text, true);

    }
    public TableModel getSearchResultTable(ArrayList keys, boolean sorted){

        return db.createTableModel("conn", keys, sorted);
    }

    /* the table parameter allows this to be used for quotes and invoices */
    public TableModel getInvoiceTableModel(String table, int key){
        
       /* Build the My Connections Invoice Table Model */
       DefaultTableModel im = new DefaultTableModel (new Object [] {"_", "Date", "Pd"}, 0){

           Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Boolean.class};

            boolean[] canEdit = new boolean [] {false, false, false};

           /* public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }*/

            //Version 1.5
            public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            };


            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            };



       };//end custom TableModel

       if (key == 0) return im;

       TableModel t;
      
       /* Search for invoices attached to this customer, the keys are stored in al */
       java.util.ArrayList al = db.search(table, 11, Integer.toString(key), false);

      
       /* If none are found set the blank custom TableModel as the model for the invoice table */
       /*  clean up and exit*/
       if (al == null) {
           return im;
       }

       /* Some entries were found so now we get a list of those invoices belonging to this contact */
       t = db.createTableModel(table, al, false);

       boolean vd;
       
       int rows = t.getRowCount();
       
       if (rows < 1) return im;
       /* Populate our custom invoice list (TableModel) for this customer */
       for (int r = 0; r < rows; r++){
           
           vd = (Boolean)t.getValueAt(r, 9);
           if (!vd){
                im.addRow(new Object [] {(Integer) t.getValueAt(r,0), (Long)t.getValueAt(r,2), (Boolean)t.getValueAt(r,8)} );
           }

       }
       return im;

    }

/*     Getters         */

    public int getKey() {

        return (Integer)conn[0];
    }
public DefaultTableModel getShipToTable() {

    this.retrieveShipToTable((Integer)conn[0]);
    return (DefaultTableModel)shipToTable;
}
    public String getCompany() {

        return (String)conn[1];
    }
    public String getFirstName() {

        return (String)conn[2];
    }
    public String getLastName() {

        return (String)conn[3];
    }
    public String getStreet() {

        return (String)conn[4];
    }
    public String getAddr2() {

        return (String)conn[5];
    }
    public String getCity() {

        return (String)conn[6];
    }
    public String getState() {

        return (String)conn[7];
    }
    public String getPostCode() {

        return (String)conn[8];
    }
    public String getContact() {

        return (String)conn[9];
    }
    public String getPhone() {

        return (String)conn[10];
    }
    public String getFax() {

        return (String)conn[11];
    }
    public String getEmail() {

        return (String)conn[12];
    }
    public String getWWW() {

        return (String)conn[13];
    }
    public String getMisc() {

        return (String)conn[14];
    }
    public boolean isCustomer() {

        return (Boolean)conn[15];
    }
    public boolean isSupplier() {

        return (Boolean)conn[16];
    }
    public String getAlphaCountryCode() {

        return (String)conn[17];
    }
    public boolean isTax1() {

        return (Boolean)conn[18];
    }
    public boolean isTax2() {

       return (Boolean)conn[19];
    }

    public ArrayList getShiptoList() {
        if (shiptos != null) {
            shiptos.trimToSize();
            return shiptos;
        }else {
            return null;

        }
        

    }

    /*    Setters     */




    public void setCompany(String s){
        conn[1] = new String(s);

    }
    public void setFirstName(String s){
        conn[2] = new String(s);

    }
    public void setLastName(String s){
        conn[3] = new String(s);

    }
    public void setStreet(String s){
        conn[4] = new String(s);

    }
    public void setAddr2(String s){
        conn[5] = new String(s);

    }
    public void setCity(String s){
        conn[6] = new String(s);

    }
    public void setState(String s){
        conn[7] = new String(s);

    }
    public void setPostCode(String s){
        conn[8] = new String(s);

    }
    public void setContact(String s){
        conn[9] = new String(s);

    }
    public void setPhone(String s){
        conn[10] = new String(s);

    }
    public void setFax(String s){
        conn[11] = new String(s);

    }
    public void setEmail(String s){
        conn[12] = new String(s);

    }
    public void setWWW(String s){
        conn[13] = new String(s);

    }
    public void setMisc(String s){
        conn[14] = new String(s);

    }
    public void setCustomer(boolean b){
        conn[15] = new Boolean(b);

    }
    public void setSupplier(boolean b){
        conn[16] = new Boolean(b);

    }
    public void setAlphaCountryCode(String s){
        conn[17] = new String(s);

    }
    public void setTax1(boolean b){
        conn[18] = new Boolean(b);

    }
    public void setTax2(boolean b){
        conn[19] = new Boolean(b);

    }

private DbEngine db;
private Object [] conn = new Object[20];
private Object [] connShip = null;
private ArrayList shiptos = new ArrayList();
private TableModel shipToTable = null;

}
