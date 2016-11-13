/*
 * Quote.java
 *
 * Created on January 7, 2009, 2:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 */

package businessmanager.InvoiceQuote;
import businessmanager.Common.Tools;
import businessmanager.*;
import javax.swing.table.*;
import datavirtue.*;
import java.util.*;

/**
 *
 * @author Sean K Anderson - Data Virtue 2009
 */
public class Quote {
    
    /** Creates a new instance of Invoice */
    public Quote(DbEngine dbe) {
        db = dbe;
        /* If you dont specify the amount of columns 
         the table model will not accept data! */
        items = new DefaultTableModel(0,10);
    }
    /* this constructor populates the invoice */
    public Quote(DbEngine dbe, int key){
        db = dbe;
        int st = this.populateQuote(key);
        if (debug) System.out.println("Populate quote status in constructor: "+st);
    }
    
    /* Return boolean to show tables were set  NOT IN USE! */
    public boolean setDbTables(String quote, String qitems){
        if (!tablesSet){
        quoteTable = quote;
        itemsTable = qitems;
        tablesSet = true;
        return true;
        }
        return false;
    }
    /* This is the items as needed for the invoice screen */
    public DefaultTableModel getItems() {        
        return items;   
    }    
    public Object [] getItem(int row){        
        return DV.getRow((DefaultTableModel)items, row);
    }    
    public int getItemCount(){        
        return items.getRowCount();        
    }       
    /* spews graphics */
    public boolean checkQuoteNumber(String number, boolean newQuote){
        
        int len = number.length();
        
        if (len > 8) {
            
            javax.swing.JOptionPane.showMessageDialog(null, "Eight character maximum for the quote number.");
            return false;
        }
        if (len < 1) {
            
            javax.swing.JOptionPane.showMessageDialog(null, "You need to enter a quote number.");
            return false;
            
        }        
        
        if (newQuote){
            ArrayList al = db.search("quote", 1, number, false);
            if (al == null) {
                return true;
            }else {
                javax.swing.JOptionPane.showMessageDialog
                    (null, "Quote number " + number + " already exsists.");
                return false;
            }
        }else return true;
        
    }
    
    /* Check and set the invoice number */
    public boolean setQuoteNumber(String num, boolean newQuote) {
        boolean good = false;

        good = this.checkQuoteNumber(num, newQuote);

        if (good){
            quoteNumber = num;
            return true;
        }else return false;
                
    }
    public String getQuoteNumber() {
        return quoteNumber;
    }    
    public void setDate(long d) {
       quoteDate = d;
    }
    public long getDate() {
        return quoteDate;
    }    
    public void setCustomer(String c){
        customer = c;        
    }
    public String getCustomer() {
        return customer;        
    }
    
    public String getShipToAddress() {
        return shipToAddress;        
    }
    
    public void setShipToAddress(String a) {
        shipToAddress = a;
    }
    /* Returns the invoice's initial total not counting returns or payments */
    /* This method also sets the taxtotals */
    private float calculateQuoteTotal(){
                
        float qty_TMP;
        float unit_TMP;
        float item_TMP;
        boolean taxable_TMP;
        float tax1_TMP;
        float tax2_TMP;
        tax1Total=0.0f;
        tax2Total=0.0f;
        total = 0.0f;
        itemTotal = 0.00f;
        
        int rows = items.getRowCount();
        
        if (rows < 1) return 0.00f;
        
        for (int r=0; r < rows; r++){
            tax1_TMP = 0.0f;
            tax2_TMP = 0.0f;
            
            /* Get unit totals  */
            if (debug) System.out.println("Quote: calculateTotal() itemRow--> "+r);
            qty_TMP = (Float)items.getValueAt(r,2);
            unit_TMP = (Float)items.getValueAt(r,5);
            item_TMP = qty_TMP * unit_TMP;
            itemTotal += item_TMP;
            
            /* Get tax totals overall  */
            taxable_TMP = (Boolean)items.getValueAt(r,6);
            if (taxable_TMP) {
                tax1_TMP = item_TMP * tax1Rate;
                tax1Total += tax1_TMP;
            }
            
            taxable_TMP = (Boolean)items.getValueAt(r,7);
            if (taxable_TMP) {
                tax2_TMP = item_TMP * tax2Rate;
                tax2Total += tax2_TMP;
            }
            total += (item_TMP + tax1_TMP + tax2_TMP);
            
        }

        total = Tools.round(total);
        tax1Total = Tools.round(tax1Total);
        tax2Total = Tools.round(tax2Total);

        if (debug) System.out.println("calculateQuoteTotal method ret: "+total);
        return total; 
        
    }
    
    public float getItemTotal() {
        return itemTotal;        
    }
       
    public float getQuoteTotal() {
        /*Calc total at each request  */
        this.calculateQuoteTotal();
        return total;        
    }

        
    public void setShipping(float s) {        
        shipping = s;        
    }

    public float getShippingCharge() {
        return shipping;
    }
    
    public void setCustKey(int k) {        
        custKey = k;        
    }

    public int getShipToKey(){
        return shipToKey;
    }
    
    public int getCustKey() {
        return custKey;        
    }

    public void setTaxes(float t1, float t2){        
        tax1Rate = t1;
        tax2Rate = t2;        
    }

    public float getTax1Rate() {        
        return tax1Rate;
    }

    public float getTax2Rate(){        
       return tax2Rate; 
    }
    
    public float getTax1Total() {
        this.calculateQuoteTotal();
        return tax1Total;
    }
    public float getTax2Total(){
       this.calculateQuoteTotal();
       return tax2Total; 
    }    
    
    public void setVoid(boolean v){        
        voide = v;
    }

    public boolean isVoid() {
        return voide;
    }
        
    public boolean isPosted() {
        return posted;
    }

    public boolean removeItem(int itemKey){
        
        return db.removeRecord("qitems", itemKey);
    }


        /* Store this quote */
    public int saveQuote(){

        if (debug) System.out.println("Entering Quote saveQuote() <>");
        //if (quoteKey > 0) return -1;  //populated, will not save new
        /* Check all items before saving? */

        invoiceData [0] = new Integer(quoteKey);
        invoiceData [1] = new String(quoteNumber);
        invoiceData [2] = new Long(quoteDate);
        invoiceData [3] = new String (customer) ;
        invoiceData [4] = new Float(shipping);
        invoiceData [5] = new String(message) ;
        invoiceData [6] = new Float(tax1Rate) ;
        invoiceData [7] = new Float(tax2Rate) ;
        invoiceData [8] = new Boolean(paid) ;
        invoiceData [9] = new Boolean(voide) ;
        invoiceData [10] = new Float(this.calculateQuoteTotal())  ;
        invoiceData [11] = new Integer(custKey) ;
        debug = true;
        if (debug) {
            System.out.println("Saving quote:");
            DV.expose(invoiceData);
        }

        int k = db.saveRecord("quote", invoiceData, false);
        quoteKey = k;

        /* Before saving first delete all the saved items? */

        /* Save items */
        Object[] item = new Object[10];
        int rows = items.getRowCount();

        /* Run throu the InvoiceDialog table model and translate to 
         regular qitems table (database)*/
        for (int r=0; r < rows; r++)  {

            item[0] = items.getValueAt(r,0);  //TODO: Make sure addItems sets the key
            item[1] = new Integer (quoteKey); //invoice reference
            item[2] = new Long (quoteDate);
            item[3] = items.getValueAt(r, 2);  //qty
            item[4] = items.getValueAt(r, 3);  //code
            item[5] = items.getValueAt(r, 4);  //desc
            item[6] = items.getValueAt(r, 5);  //unit
            item[7] = items.getValueAt(r, 6);  //tax bool
            item[8] = items.getValueAt(r, 7);  //taxbool
            item[9] = items.getValueAt(r, 9);  //cost
            if (debug) DV.expose(item);
            db.saveRecord("qitems", item, false);

        }

        /* Save Shipto  */
        Object [] shipto = new Object [4];
        shipto[0] = new Integer (shipToKey);
        shipto[1] = new Integer (quoteKey);
        shipto[2] = shipToAddress;
        shipto[3] = new Integer (0);//unused data field
        db.saveRecord("qshipto", shipto, false);

        posted = true;
        populated = true;
        return quoteKey; //saved as key
    }

   
   /* If qty is sufficiant and item is marked avail returns inventory qty */ 
    public float checkInventory(int key, float qty){
        Object [] inventory = db.getRecord("inventory", key);
        if (debug){
            System.out.println("Quote: checkInventory -->>");
            DV.expose(inventory);                   
        }
        if (inventory == null) return 0;
        float availQty = (Float)inventory[6];
        boolean unavailable = (Boolean)inventory[15];
                
        if (qty <= availQty && unavailable==false) return availQty;
        if (unavailable==true) return -1;
        if (qty > availQty) return -2;      
        return -3;
    }
    /* Returns an inventory record or null  */
    public Object[] getInventory(int key) {
        Object [] inventory = db.getRecord("inventory", key);
        return inventory;
    }
    
    /*This method accepts an inventory record and converts it to the invoice tablemodel format */
    public int addItem(Object inventory[], float needed){
        //
        Object[] tableItem = new Object[10];
        
        tableItem[0] = new Integer(0);//new item
        tableItem[1] = new Long(new Date().getTime());//date is actually taken from the posted invoice
        tableItem[2] = new Float(needed);//req qty
        tableItem[3] = new String((String)inventory[2]);//code
        tableItem[4] = new String((String)inventory[3]);//desc
        tableItem[5] = new Float((Float)inventory[8]);//price
        tableItem[6] = new Boolean((Boolean)inventory[13]);//tax 1
        tableItem[7] = new Boolean((Boolean)inventory[14]);//tax 2
        float _unitTotal = (Float)tableItem[2]*(Float)tableItem[5];
        
        tableItem[8] = new Float(Tools.round(_unitTotal)); //unit totl
        tableItem[9] = new Float((Float)inventory[7]);//cost
              
        items.addRow(tableItem);
        this.calculateQuoteTotal();
        return items.getRowCount();        
        
    }
    public void setItemModel(DefaultTableModel tm){
        items = tm;
    }
    
    public boolean removeItems(int [] rows){
        if (posted) return false;
        
        int itemCount = items.getRowCount();
        if (rows.length > itemCount) return false;
        
        int removed = 0;
        
        if (rows != null && rows.length > 0) {
            
            for (int r = 0; r < rows.length; r++){
                items.removeRow(rows[r] - removed);
                removed++;                
            }                      
        }else return false;
        return true;
    }    
    
    
    /* Populate quote object  */
    /* Remember, invoice-quote items are stored differently than they are
       displayed and handled on screen. */
    public int populateQuote(int key){
        if (debug) System.out.println("Populating quote with key "+key);
        invoiceData = db.getRecord(quoteTable, key);
        if (invoiceData==null) {
            populated = false;
            posted = false;
            return 0;
        }
        quoteKey = (Integer) invoiceData[0];
        quoteNumber = (String) invoiceData[1];
        quoteDate = (Long) invoiceData[2];
        customer = (String) invoiceData[3];
        shipping = (Float) invoiceData[4];
        message = (String) invoiceData[5];
        tax1Rate = (Float) invoiceData[6];
        tax2Rate = (Float) invoiceData[7];
        paid = (Boolean) invoiceData[8];
        voide = (Boolean) false;
        total = (Float) invoiceData[10];
        custKey = (Integer) invoiceData[11];
        
        if (custKey > 0){
            Object [] tmp = db.getRecord("conn", custKey);
            custEmail = (String) tmp[12];
            tmp = null;
        }
        

        if (debug) {
            System.out.println("Quote: populate() quoteData-->");
            DV.expose(invoiceData);

        }

        /* Populate items table from invitems */
        ArrayList al;
        al = db.search(itemsTable, 1, Integer.toString(quoteKey), false);
        if (al==null) return -1;

        if (debug) System.out.println("Getting items for invoice #items "+al.size());
        invItems = (DefaultTableModel)db.createTableModel("qitems", al, false);
        

        for (int r = 0; r < invItems.getRowCount(); r++){

            //this converts the item rows into the proper format for the invoice table screen
            

            Object [] item = new Object [10];
            item[0] = new Integer((Integer)invItems.getValueAt(r, 0));//key
            item[1] = new Long((Long)invItems.getValueAt(r, 2));//date
            item[2] = new Float((Float)invItems.getValueAt(r, 3));//qty
            item[3] = new String((String)invItems.getValueAt(r, 4));//code
            item[4] = new String((String)invItems.getValueAt(r, 5));//desc
            item[5] = new Float((Float)invItems.getValueAt(r, 6));//unit price
            item[6] = new Boolean((Boolean)invItems.getValueAt(r, 7));//t1
            item[7] = new Boolean((Boolean)invItems.getValueAt(r, 8));//t2
            
            float _unitTotal = (Float)item[2] * (Float)item[5];
            item[8] = new Float(Tools.round(_unitTotal));//build unit total
            item[9] = new Float((Float)invItems.getValueAt(r, 9));

            if (debug) {
                System.out.println("Quote: populate() invoiceItem-->");
                DV.expose(item);            
            }       
            items.addRow(item);  
            if (debug) System.out.println("Quote: populate() itemsCount--> "+items.getRowCount() );
            if (debug) {
                System.out.println("Quote: populate() getItemRow-->");
                Object [] test = DV.getRow(items, 0);
                DV.expose(test);
            } 
        }

         
      
        /* Try to get shipto data */
        al = db.search("qshipto", 1, Integer.toString(quoteKey), false);
        if (al == null){
            shipToKey = 0;
            shipToAddress = "";
        }else {
            shipToKey = (Integer)al.get(0); 
        }
        /* If found populate shipTo string */
        if (shipToKey > 0) {
            Object [] rec = db.getRecord("qshipto", shipToKey);
            shipToAddress = (String)rec[2];
        }       
        
        populated = true;
        total = this.calculateQuoteTotal();

        if (debug) System.out.println("Calculated  quote Total $"+total);
        
        posted = true;

        return quoteKey;
    }
           
    public String getMessage(){
        
        return message;
        
    }
    public void setMessage(String m){        
        message = m;        
    }
    
    public boolean isPopulated() {
        return populated;        
    }

    public String getEmail(){
        if (custEmail.isEmpty() && custKey > 0){
            
            Object [] tmp = db.getRecord("conn", custKey);
            custEmail = (String) tmp[12];
            tmp = null;
        }
        return custEmail;
    }
    public int getQuoteKey(){
        return quoteKey;
    }
    /* Private Object Data */
    private DefaultTableModel items = new DefaultTableModel(0,10);
    private DefaultTableModel invItems;
    private float itemTotal=0.00f;    
    private int shipToKey=0;
    private String shipToAddress="";
    private int custKey;
    //private int lastRecordedQuoteNumber=0;
    private int quoteKey=0;
    private String quoteNumber;
    private long quoteDate=0;
    private String customer;
    private String message;
    private boolean paid;
    private boolean voide;
    private float tax1Rate;
    private float tax1Total;
    private float tax2Rate;
    private float tax2Total;
    private float total;
    private float shipping;
    private String custEmail="";

    /* Database Management  */
    private DbEngine db;
    /* gets and sets also maintain these Object Arrays */
    private Object [] invoiceData = new Object [12];
  
    
    private String quoteTable="quote";
    private String itemsTable="qitems";
    private boolean tablesSet=false;
    private boolean populated=false;
    private boolean posted = false;
    private String nl = System.getProperty("line.separator");
    private boolean debug = false;
    
}
