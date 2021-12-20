/*
 * Invoice.java
 *
 * Created on January 7, 2009, 2:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 */

package com.datavirtue.nevitium.models.invoices.old;
import RuntimeManagement.GlobalApplicationDaemon;

import com.datavirtue.nevitium.ui.util.Tools;
import javax.swing.table.*;
import datavirtue.*;
import java.util.*;


/**
 *
 * @author Sean K Anderson - Data Virtue 2009
 */
public class OldInvoice {
    private GlobalApplicationDaemon application;

    /** Creates a new instance of Invoice */
    public OldInvoice(GlobalApplicationDaemon application) {
        db = application.getDb();
        VAT = DV.parseBool(application.getProps().getProp("VAT"), false);
        /* If you dont specify the amount of columns 
         the table model will not accept data! */
        items = new DefaultTableModel(0,10);
    }
    /* this constructor populates the invoice */
    public OldInvoice(GlobalApplicationDaemon application, int key){
        this.application = application;
        db = application.getDb();
        VAT = DV.parseBool(application.getProps().getProp("VAT"), false);

        int st = this.populateInvoice(key);        
        if (debug) System.out.println("Populate inv status in constructor: "+st);
    }
    
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
    public boolean checkInvoiceNumber(String number){
        
        int len = number.length();
        
        if (len > 8) {
            
            javax.swing.JOptionPane.showMessageDialog(null, "Eight character maximum for the invoice number.");
            return false;
        }
        if (len < 1) {
            
            javax.swing.JOptionPane.showMessageDialog(null, "You need to enter an invoice number.");
            return false;
            
        }        
        
        ArrayList al = db.search("invoice", 1, number, false);
        if (al == null) {            
            return true;            
        }else {
            javax.swing.JOptionPane.showMessageDialog
                    (null, "Invoice number " + number + " already exsists.");
            return false;            
        }
        
    }
    
    /* Check and set the invoice number */
    public boolean setInvoiceNumber(String num) {      
        boolean good = false;
        good = this.checkInvoiceNumber(num);
        if (good){
            invoiceNumber = num;
            return true;
        }else return false;
                
    }
    public String getInvoiceNumber() {
        return invoiceNumber;
    }    
    public void setDate(long d) {
       invoiceDate = d;        
    }
    public long getDate() {
        return invoiceDate;
    }    
    /* Auxilary Invoice Information or Project Details */
    private long invoiceDueDate=0;
    public long getDueDate(){
        return invoiceDueDate;
    }
    private String purchaseOrderNumber="";
    public String getPurchaseOrder(){
        return purchaseOrderNumber;
    }
    private String projectName="";
    public String getProjectName(){
        return projectName;
    }
    private long startDate=0;
    public long getStartDate(){
        return startDate;
    }
    private long endDate=0;
    public long getEndDate(){
        return endDate;
    }
    private String projectNote="";
    public String getProjectNote(){
        return projectNote;
    }
    
    public void setDueDate(long d){
        invoiceDueDate = d;
    }
    public void setPurchaseOrder(String p){
        purchaseOrderNumber = p;
    }
    public void setProjectName(String p){
        projectName = p;
    }
    public void setStartDate(long d){
        startDate = d;
    }
    public void setEndDate(long d){
        endDate = d;
    }
    public void setProjectNote(String n){
        projectNote = n;
    }
    /* END PROJECT INFO */
    
      public String getCustomer() {
        return customer;        
    }

  
    /* This is the key for a regular Connections addresses  */
    public int getShippingKey(){
        return shippingKey;
    }
    public void setShippingKey(int key){
        shippingKey = key;
    }

    public String getShipToAddress() {
        return shipToAddress;        
    }
    public void setShipToAddress(String a) {
        shipToAddress = a;
    }

    public long getLastPaymentDate(long todaysDate){

        long returnValue = this.getDate();
        InvoicePayment ip;
        long temp = 0;

        int rows = payments.getRowCount();
        if (rows < 1) {
            return this.getDate();
        }

        for (int r = 0; r < rows; r++){
            ip = new InvoicePayment(getPayment(r));
            temp = todaysDate - ip.getDate();
            
            if (temp < (todaysDate - returnValue)){
                
                if (ip.getCredit() > 0){
                    
                    if (ip.getType().equals("Cash") ||
                            ip.getType().equals("CC") ||
                            ip.getType().equals("Check") ||
                            ip.getType().equals("Prepaid")){
                    
                        returnValue = ip.getDate();
                    }

                }

            }


        }

        return this.getDate();  //no payments

    }

    public static float extractGST(float amount, float tax_rate){
        //VAT ADDIN
        return amount - (amount * (100/(100+(tax_rate*100)) ) );
    }
    /* Returns the invoice's initial total not counting returns or payments */
    /* This method also sets the taxtotals */
    private float calculateInvoiceTotal(){
                
        float qty_TMP;
        float unit_TMP;
        float item_TMP;
        
        float tax1_TMP;
        float tax2_TMP;
        boolean taxable_TMP;

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
            if (debug) System.out.println("Invoice: calculateTotal() itemRow--> "+r);
            qty_TMP = (Float)items.getValueAt(r,2);
            unit_TMP = (Float)items.getValueAt(r,5);
            
            item_TMP = qty_TMP * unit_TMP;
            itemTotal += item_TMP;
            
            /* Get tax totals overall  */
            taxable_TMP = (Boolean)items.getValueAt(r,6);

            if (taxable_TMP) {
                //VAT ADDIN
                if (VAT){
                    tax1Total += extractGST(item_TMP, this.tax1Rate);
                }else {
                    tax1_TMP = item_TMP * tax1Rate;
                    tax1Total += tax1_TMP;
                }
            }
            
            taxable_TMP = (Boolean)items.getValueAt(r,7);

            if (taxable_TMP) {
                tax2_TMP = item_TMP * tax2Rate;
                tax2Total += tax2_TMP;
            }
            //VAT ADDIN
            if (VAT){
                total += (item_TMP + tax2_TMP);
            }else {
                total += (item_TMP + tax1_TMP+ tax2_TMP);
            }
            total = Tools.round(total);
        }
        if (debug) System.out.println("calculateInvoiceTotal method ret: " + total);

        tax1Total = Tools.round(tax1Total);
        tax2Total = Tools.round(tax2Total);

        return total;
        
    }
    
    public float getItemTotal() {
        this.calculateInvoiceTotal();
        return itemTotal;        
    }
    
    /* Use only after populating */
    private float calculateInvoiceDue() {
        dueNow = 0.00f;
        
        if (!populated){
            if (debug) System.out.println("Invoice not Populated");
            return 0.00f;
        }
        
        if (payments==null){
            if (debug) System.out.println("Invoice Payments null");
            dueNow = total;
            return dueNow;
        }
        if (payments.getRowCount() < 1){
            dueNow = total;
            return dueNow;
        }
        
        /* TODO: May need revisions */
        /* Interest, returns, payments(debits & credits) */
        float debits;
        float credits;
        
        debits = Tools.totalFloatColumn(payments, 5);
        credits = Tools.totalFloatColumn(payments, 6);
        //float returns = calculateInvoiceReturns();
                
        dueNow = total + (debits - credits);
        dueNow = Tools.round(dueNow);

        //dueNow = (total + debits) - credits;
        if (debug) System.out.println("calculateInvoiceDueNow: "+dueNow);
        
        return dueNow;
    }

    public float getTotalPayments() {
        float debits;
        float credits;
        float t;

        debits = Tools.totalFloatColumn(payments, 5);
        credits = Tools.totalFloatColumn(payments, 6);
        //float returns = calculateInvoiceReturns();
        t = debits - credits;
        return Tools.round(t);
    }
    
    public float getTotalDue() {
        
        this.calculateInvoiceTotal();
        this.calculateInvoiceDue();
        return dueNow;
        
    }
    public float calculateInvoiceReturns() {
        
        if (returns==null) return 0.00f;
        
        float totalReturns = 0.00f;
        float qty_TMP;
        float unit_TMP;
        int rows = returns.getRowCount();
        for (int r=0; r < rows; r++){
            
            qty_TMP = (Float)returns.getValueAt(r,3);
            unit_TMP = (Float)returns.getValueAt(r,6);
            totalReturns += unit_TMP;            
        }        
        return Tools.round(totalReturns);
    }
    
    public float getInvoiceTotal() {        
        /*Calc total at each request  */
        this.calculateInvoiceTotal();
        return total;        
    }

    public float getInvoiceDueNow() {
        this.calculateInvoiceDue();
        return dueNow;
    }    
    public void setCustomer(String s){
        customer = s;
    }
    public void setShipping(float s) {        
        shipping = s;        
    }

    public float getShippingCharge() {
        return shipping;
    }
    
    public void setCustKey(int k) {        
        custKey = k;  
        Object [] tmp = db.getRecord("conn", custKey);
        custEmail = (String) tmp[12];
        tmp = null;
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
        this.calculateInvoiceTotal();
        return tax1Total;
    }
    public float getTax2Total(){
       this.calculateInvoiceTotal(); 
       return tax2Total; 
    }    
    
    public void setVoid(boolean v){        
        voide = v;
    }

    public boolean isVoid() {
        return voide;
    }

    public void setPaid(boolean p){
        paid = p;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isPosted() {
        return posted;
    }
    
    /* Store this invoice */
    public int postInvoice(){
        if (debug) System.out.println("Entering Invoice postInvoice() <>");
        if (invoiceKey > 0) return -1;
        /* Check all items before saving? */
      
        invoiceData [0] = new Integer(invoiceKey);
        invoiceData [1] = new String(invoiceNumber);
        invoiceData [2] = new Long(invoiceDate);
        invoiceData [3] = new String (customer) ;
        invoiceData [4] = new Float(shipping);
        invoiceData [5] = new String(message) ;
        invoiceData [6] = new Float(tax1Rate) ;
        invoiceData [7] = new Float(tax2Rate) ;
        invoiceData [8] = new Boolean(paid) ;
        invoiceData [9] = new Boolean(voide) ;
        invoiceData [10] = new Float(this.calculateInvoiceTotal())  ;
        invoiceData [11] = new Integer(custKey) ;
        if (debug) {
            System.out.println("Posting invoice:");
            DV.expose(invoiceData);
        }
        int k = db.saveRecord("invoice", invoiceData, false);
        invoiceKey = k;
        
        /* Save items */
        /* items is from the InvoiceDialog table model
         translated to the qitems table */
        Object[] item = new Object[10];
        int rows = items.getRowCount();
        
        for (int r=0; r < rows; r++)  {
                
            item[0] = new Integer(0);  //all recored items will be appended to .db
            item[1] = new Integer (invoiceKey); //invoice reference 
            item[2] = new Long (invoiceDate);  
            item[3] = items.getValueAt(r, 2);  //qty
            item[4] = items.getValueAt(r, 3);  //code
            item[5] = items.getValueAt(r, 4);  //desc
            item[6] = items.getValueAt(r, 5);  //unit
            item[7] = items.getValueAt(r, 6);  //tax bool
            item[8] = items.getValueAt(r, 7);  //taxbool
            item[9] = items.getValueAt(r, 9);  //cost
            if (debug) DV.expose(item);
            db.saveRecord("invitems", item, false);
            //PCODE: Get key from item save and use in storing/mapping of long note info
                
        }
         
        /* Save Shipto */
        Object [] shipto = new Object [4];        
        shipto[0] = new Integer (0);
        shipto[1] = new Integer (invoiceKey);
        shipto[2] = shipToAddress;
        shipto[3] = new Integer (0);//unused data field
        db.saveRecord("shipto", shipto, false);        
        
        /* Save invoice auxilary info */
        
        Object [] invaux = new Object [8];
        invaux[0] = new Integer(0);
        invaux[1] = invoiceKey;
        if (invoiceDueDate == 0) invoiceDueDate = invoiceDate;
        invaux[2] = invoiceDueDate;
        invaux[3] = purchaseOrderNumber;
        invaux[4] = projectName;
        invaux[5] = startDate;
        invaux[6] = endDate;
        invaux[7] = projectNote;
        db.saveRecord("invaux", invaux, false);
        
        posted = true;
        populated = true;
        
        return invoiceKey; //saved as key   
    }

        /* Store this invoice */
    public int saveQuote(){

        if (debug) System.out.println("Entering Invoice saveQuote() <>");
        if (invoiceKey > 0) return -1;  //populated, will not save new
        /* Check all items before saving? */

        invoiceData [0] = new Integer(invoiceKey);
        invoiceData [1] = new String(invoiceNumber);
        invoiceData [2] = new Long(invoiceDate);
        invoiceData [3] = new String (customer) ;
        invoiceData [4] = new Float(shipping);
        invoiceData [5] = new String(message) ;
        invoiceData [6] = new Float(tax1Rate) ;
        invoiceData [7] = new Float(tax2Rate) ;
        invoiceData [8] = new Boolean(paid) ;
        invoiceData [9] = new Boolean(voide) ;
        invoiceData [10] = new Float(this.calculateInvoiceTotal())  ;
        invoiceData [11] = new Integer(custKey) ;

        if (debug) {
            System.out.println("Saving quote:");
            DV.expose(invoiceData);
        }

        int k = db.saveRecord("invoice", invoiceData, false);
        invoiceKey = k;

        /* Save items */
        Object[] item = new Object[10];
        int rows = items.getRowCount();

        for (int r=0; r < rows; r++)  {

            item[0] = new Integer(0);  //all recored items will be appended to .db
            item[1] = new Integer (invoiceKey); //invoice reference
            item[2] = new Long (invoiceDate);
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
        shipto[0] = new Integer (0);
        shipto[1] = new Integer (invoiceKey);
        shipto[2] = shipToAddress;
        shipto[3] = new Integer (0);//unused data field
        db.saveRecord("qshipto", shipto, false);

        posted = true;
        populated = true;
        return invoiceKey; //saved as key
    }

    /* This method can take a 'returns' or 'invItems' table model and
     counts the quantity of the specific item sold or returned based on
     the description.  Used to calculate how many of an item have
     already been returned to determin if any more can be returned. */
    private float countTotalItemQty(String desc, String code, TableModel tm) {
        
        if (tm==null) return 0.00f;
        int rows = tm.getRowCount();
        if (rows < 1) return 0.00f;
        
        float qty=0;  //the amount sold/returned on the invoice depending upon the tm
        String desc_TMP, code_TMP;  //temps to hold desc and code from tm

        int occurances = 0; //debug data

        for (int r=0; r<rows; r++){
           
             desc_TMP = (String)tm.getValueAt(r,5); //description col
             code_TMP = (String)tm.getValueAt(r,4); //code column
             
            if (desc_TMP.equals(desc) && code_TMP.equals(code)){
                occurances++;//debug data
                qty += (Float)tm.getValueAt(r,3);//qty col
            }
        }

        if (debug) System.out.println("countTotalItemQty - "+desc + " Occurances:"+occurances);
        return qty;
    }
   /* If qty is sufficiant and item is marked avail returns inventory qty */ 
    public float checkInventory(int key, float qty){
        Object [] inventory = db.getRecord("inventory", key);
        if (debug){
            System.out.println("Invoice: checkInventory -->>");
            DV.expose(inventory);                   
        }
        if (inventory == null) return 0;
        float availQty = (Float)inventory[6];
        boolean unavailable = (Boolean)inventory[15];
        boolean partialAllowed = (Boolean)inventory[19];

        if (Tools.isDecimal(qty) && !partialAllowed) return -3;

        if (qty <= availQty && unavailable==false) return availQty;
        if (unavailable==true) return -1;
        if (qty > availQty) return -2;
        return -4;
    }
    /* Returns an inventory record or null  */
    public Object[] getInventory(int key) {
        Object [] inventory = db.getRecord("inventory", key);
        return inventory;
    }
    
    /* this method takes an inventory row */
    /* Only calling Post will save these added items */
    public int addItem(Object inventory[], float needed, boolean cust_tax1, boolean cust_tax2, boolean addCatData, int replaceRow, int insert ){
        if (posted) return -1;
        int insertedAt = -1;
        int alreadyRow = -1;
        /*If the user supplies a different upc or code for two iitems that
          have the same description, this search and combination algorithm fails.*/
        //search desc
        alreadyRow = DV.searchTable(items, 4, (String) inventory[3]);
        //don't check for existing items when replacing rows/items
        if (replaceRow > -1) alreadyRow = -1;

        if (alreadyRow < 0){
        
            Object[] tableItem = new Object[10];
        
            tableItem[0] = new Integer(0);//new item
            tableItem[1] = new Long(new Date().getTime());//filler - date is actually taken from the posted invoice
            tableItem[2] = new Float(needed);//req qty
            tableItem[3] = (String)inventory[2];//code
            tableItem[4] = (String)inventory[3];//desc
            tableItem[5] = new Float((Float)inventory[8]);//price
            //VAT ADDIN
            if (VAT) { //VAT is assuming tax1 and not tax2 is used for GST or wahtever

                if (VAT && cust_tax1 && ((Boolean)inventory[13])){ //check for taxable status of item/customer
                    tableItem[5] = new Float(((Float)tableItem[5]) + (((Float)tableItem[5]) * this.tax1Rate));
                }
            }

            /* If the customer is taxable assign tax status from inventory
             Otherwise if the customer is not taxable the tax status is false*/
            boolean t1 = true, t2 = true;
        
            if (cust_tax1 == true){
                t1 = (Boolean)inventory[13];
            }else t1 = false;
        
            if (cust_tax2 == true){
                t2 = (Boolean)inventory[14];
            }else t2 = false;

            tableItem[6] = t1;//tax 1
            tableItem[7] = t2;//tax 2
            tableItem[8] = new Float((Float)tableItem[2]*(Float)tableItem[5]); //unit totl
            tableItem[9] = new Float((Float)inventory[7]);//cost
            //DV.expose(tableItem);
            //replace Routine for editing misc items
            if (replaceRow > -1) {
                //make sure the replace row is not greater than those available, if so just add
                if (replaceRow >= items.getRowCount()){
                    items.addRow(tableItem);   
                    insertedAt = items.getRowCount()-1;
                }else {
                    items.removeRow(replaceRow);
                    items.insertRow(replaceRow, tableItem);
                    insertedAt = replaceRow;
                }
            }else {
                if (insert > -1 && replaceRow==-1) {
                    items.insertRow(insert, tableItem);
                    insertedAt = insert;
                }else {
                    items.addRow(tableItem);
                    insertedAt = items.getRowCount()-1;
                }
            }
            
            //PCODE: store long note data in items (must build to fit the InvoiceFDialog tablemodel)
            //when populating the Invoice object (this) the long notes are inserted back into the 
            
            
            //are we adding inventory category data below the item automatically?
            if (addCatData){
                
                tableItem[0] = new Integer(0);//new item
                tableItem[1] = new Long(new Date().getTime());//date is actually taken from the posted invoice
                tableItem[2] = new Float(0);//req qty
                tableItem[3] = "MISC";//code
                tableItem[4] = (String)inventory[9];//desc
                tableItem[5] = new Float(0.00f);//price
                tableItem[6] = false;
                tableItem[7] = false;
                tableItem[8] = new Float(0.00f); //unit totl
                tableItem[9] = new Float(0.00f);//cost
                items.addRow(tableItem);
            }

        }else {
            //WTF?! Bug Watch - Am I considering partial quantities?!
            if (needed > 0){ //make sure we're not combining item notes!
                float qtySold = (Float)items.getValueAt(alreadyRow, 2); //get qty currently sold
                items.setValueAt(new Float(qtySold+needed), alreadyRow, 2);  //increase currently sold 
            }
        }

        this.calculateInvoiceTotal();
        return insertedAt;
        
    }
    public void setInvoiceDialogModel(DefaultTableModel tm){
        items = tm;
    }
    public void setInvItems(DefaultTableModel tm){
        invItems = tm;
    }
    public boolean removeItem(int r) {
        if (posted) return false;

        int rows = items.getRowCount();
        if (r > rows-1) return false;
        items.removeRow(r);
        return true;
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
    
    public int returnItem(int itemRow, float proposedRetQty, float credit, long date){
        
        if (!populated) return 0;
        if (items == null) return -1;
        if (!posted) return -3;
        
        /* Unique data to process invoice items */
        String itemDescription = (String)invItems.getValueAt(itemRow,5);
        
        boolean partial = Tools.isDecimal(proposedRetQty);
        
        ArrayList al = db.search("inventory", 3, itemDescription, false);
        if (al != null){
        Object [] i = db.getRecord("inventory", (Integer)al.get(0));
        boolean partialAllowed = (Boolean)i[19];
        if (partial && !partialAllowed){
            return -4;
        }
        }


        String itemCode = (String)invItems.getValueAt(itemRow,4);
        
        /* Search the invoice for the same item and 
           calculate how many were sold*/
        float itemTotalQty = countTotalItemQty(itemDescription, itemCode, invItems);
        if (debug) System.out.println("returnItem itemTotalQty "+itemTotalQty);
        
        /* Search returns for same item, if found
           calculate how many have been returned*/
        float returnedTotalQty = countTotalItemQty(itemDescription, itemCode, returns);
        if (debug) System.out.println("returnItem returnedTotalQty "+returnedTotalQty);

        /* Subtract returned from sold and check against proposed return qty
           if the amount is less than or equal to the remaining qty process return*/
        float diff = itemTotalQty - returnedTotalQty;
        if (debug) System.out.println("returnItem itemQty minus previous returns "+diff);
        
        if (diff < proposedRetQty) {
           /* Item already returned */
            javax.swing.JOptionPane.showMessageDialog(null,
                    "You tried to return more items than had been sold.");
            return -2;
        }
        
        /* Process: */
        Object [] returnItem = DV.getRow(invItems, itemRow);
        DV.expose(returnItem);
        returnItem[0] = new Integer(0);//where the hell was this?
        returnItem[2] = new Long(date); //date of return
        returnItem[3] = new Float(proposedRetQty);
        returnItem[6] = new Float(credit);  //this is supplied by the user
                                            //used to calculate sales report
        db.saveRecord("returns", returnItem, false);
        returnItem=null;

        /* Now the return is processed, nothing has yet been done to
         assure the invoice is normalized.  i.e. make sure dueNow
         shows the proper amount after the return has been factored in.*/

        /* Record a credit for the return amount in payments */
        Object [] payment = new Object[7];

        payment [0] = new Integer(0);
        payment [1] = new String(invoiceNumber);
        payment [2] = new Long(date);
        payment [3] = new String("Return");  // treated as a credit but marked return
        payment [4] = new String(itemDescription);
        payment [5] = new Float(0.00f);
        payment [6] = new Float(credit);
        dueNow = this.calculateInvoiceDue() - credit;
        //payment [7] = new Float(dueNow);

        int paymentKey = db.saveRecord("payments", payment, false);
        /* A payment has been recorded crediting the customer for the amount
         refunded on the return.*/

        if (dueNow <= 0.00f) paid = true;

        /*Now I have to check to see if the customer has 'overpaid'.
         i.e. Has a negative balance on the invoice now that a return has been
         credited.*/
        if (dueNow < 0){
            float debit;
            payment [0] = new Integer(0);
            payment [1] = new String(invoiceNumber);
            payment [2] = new Long(date);
            payment [3] = new String("Refund");
            payment [4] = new String(itemDescription);
            debit = (dueNow * -1);
            payment [5] = new Float(debit);
            payment [6] = new Float(0.00f);
            dueNow = dueNow + debit;
            //payment [7] = new Float(dueNow);
            db.saveRecord("payments", payment, false);

            //this.paid = true;
            this.saveInvoice();  //save invoice to mark 'paid' status
            return paymentKey;
        }
        this.saveInvoice();
        return paymentKey;//payment (credit) key
    }    
      
    public int getPaymentCount() {        
        return payments.getRowCount();        
    }

    public Object[] getPayment(int row) {        
        return DV.getRow(payments, row);        
    }

    public int recordPayment(Object[] payment){
        if (debug){
            System.out.println("Trying to record payment --->>");
            DV.expose(payment);
        }
        int k = db.saveRecord("payments", payment, false);
        this.retrievePayments();
        return k;
    }

    public int saveInvoice(){
        if (!posted) return -1;
        
        invoiceData [0] = new Integer(invoiceKey);
        invoiceData [1] = new String(invoiceNumber);
        invoiceData [2] = new Long(invoiceDate);
        invoiceData [3] = new String (customer) ;
        invoiceData [4] = new Float(shipping);
        invoiceData [5] = new String(message) ;
        invoiceData [6] = new Float(tax1Rate) ;
        invoiceData [7] = new Float(tax2Rate) ;
        invoiceData [8] = new Boolean(paid) ;
        invoiceData [9] = new Boolean(voide) ;
        invoiceData [10] = new Float(this.calculateInvoiceTotal())  ;
        invoiceData [11] = new Integer(custKey) ;
        int k = db.saveRecord("invoice", invoiceData, false);
        invoiceKey = k;
        this.populateInvoice(invoiceKey);
        return k;
    }
    /* Populate invoice object  */
    public int populateInvoice(int key){

        //currently after a return the items table is getting populated again.

        if (debug) System.out.println("Populating invoice with key "+key);
        invoiceData = db.getRecord(invoiceTable, key);
        if (invoiceData==null) {
            populated = false;
            posted = false;
            return 0;
        }
        invoiceKey = (Integer) invoiceData[0];
        invoiceNumber = (String) invoiceData[1];
        invoiceDate = (Long) invoiceData[2];
        customer = (String) invoiceData[3];
        shipping = (Float) invoiceData[4];
        message = (String) invoiceData[5];
        tax1Rate = (Float) invoiceData[6];
        tax2Rate = (Float) invoiceData[7];
        paid = (Boolean) invoiceData[8];
        voide = (Boolean) invoiceData[9];
        total = (Float) invoiceData[10];
        custKey = (Integer) invoiceData[11];
        
        

        if (debug) {
            System.out.println("Invoice: populate() invoiceData-->");
            DV.expose(invoiceData);

        }

        if (custKey > 0){
            Object [] tmp = db.getRecord("conn", custKey);
            custEmail = (String) tmp[12];
            tmp = null;
        }

        /* Populate items table from invitems database */
        ArrayList al;
        //************************************items = new DefaultTableModel();
        al = db.search("invitems", 1, Integer.toString(invoiceKey), false);
        if (al==null) return -1;

        if (al != null) invItems = (DefaultTableModel)db.createTableModel("invitems", al, false);
        

        /* Convert invItems to InvoiceDialog items */
        if (items.getRowCount() == 0){
        for (int r = 0; r < invItems.getRowCount(); r++){

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
            item[9] = new Float((Float)invItems.getValueAt(r, 9));//cost

            if (debug) {
                System.out.println("Invoice: populate() invoiceItem-->");
                DV.expose(item);            
            }       
            items.addRow(item); 
            
            //PCODE: Add long-note here if available
            // it must e built to fit the tablemodel designed for the items
            
            if (debug) System.out.println("Invoice: populate() itemsCount--> "+items.getRowCount() );
            if (debug) {
                System.out.println("Invoice: populate() getItemRow-->");
                Object [] test = DV.getRow(items, 0);
                DV.expose(test);
            } 
        }
        }
        //TODO: Must distill this table properly
        
        al = db.searchFast("returns", 1, Integer.toString(invoiceKey), false);
        if (al==null){
            returns = new DefaultTableModel();
        }else {
            returns = (DefaultTableModel)db.createTableModelFast("returns", al, false);
        }        
        
        
        /* Try to get shipto data */
        al = db.search("shipto", 1, Integer.toString(invoiceKey), false);
        if (al == null){
            shipToKey = 0;
            shipToAddress = "";
        }else {
            shipToKey = (Integer)al.get(0); 
        }
        /* If found populate shipTo string */
        if (shipToKey > 0) {
            Object [] rec = db.getRecord("shipto", shipToKey);
            shipToAddress = (String)rec[2];
        }       
        
        populated = true;
        
        /* WHY WAS THIS COMMENTED OUT! */
        //total = this.calculateInvoiceTotal();

        retrievePayments();

        if (debug) System.out.println("Calculated Total $"+total);
        dueNow = this.calculateInvoiceDue();

        if (debug) System.out.println("Calculated invoice due $"+dueNow);
        posted = true;

        //PCODE: get aux  - uses populated invoice key
        loadInvoiceAux();

        return invoiceKey;
    }
    
    private void loadInvoiceAux(){
     /*   ArrayList al = db.nSearch("invaux", 1, this.invoiceKey, this.invoiceKey, false);
        if (al != null){
            invoiceAuxData = db.getRecord("invaux", (Long)al.get(0));
        }else {
            return;
        }*/
        
        //remember to set invoice key in invoiceAuxData for new invoices or quotes
    }
    
    private void setInvoiceAux(InvoiceAux ia){
        invoiceAuxData = ia;
    }
    
    public DefaultTableModel getInvItems() {
        return invItems;
    }
    public ArrayList getInvoiceItemList(){

   /*     ArrayList al = new ArrayList(invItems.getRowCount());

    for (int r = 0; r < invItems.getRowCount(); r++){
        al.add(new InvoiceItem(application, DV.getTableRow(invItems, r)));
    }
    al.trimToSize();
    return al;*/

        return OldInvoiceItem.getItemList(invItems, application);
    }
    public DefaultTableModel getReturns() {
        return returns;
    }
    public int getReturnCount() {
        if (returns == null) return 0;
        return returns.getRowCount();
    }
    public DefaultTableModel getPayments() {        
        return payments;        
    }
    private void retrievePayments(){
        ArrayList al;
        TableSorter temp;
        payments=null;
        al = db.search("payments",1, invoiceNumber, false);
        if (al==null){
            payments = new DefaultTableModel();
        }else {        
             temp = (TableSorter)db.createTableModel("payments", al, true);

             temp.setSortingStatus(2, 1);  //sort by Date

             Object [] header = new Object[]{"KEY","INUMBER","Date","Type","Memo","Debit","Credit","Balance"};
             payments = new DefaultTableModel(header,0){
             public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            }
         };

             Object [] newPmt = new Object [8];
             total = this.calculateInvoiceTotal();
             float runDue = total;
             int rows = temp.getRowCount();
             int cols = temp.getColumnCount();

             /* run through and assign payment values to the new ledger view
                while adding the balance column*/
             for (int r = 0; r < rows; r++){
                
                for (int c=0; c < cols; c++){
                    newPmt[c] = temp.getValueAt(r, c);
                }
                runDue += Tools.round((Float)newPmt[5]); //add debit
                runDue -= Tools.round((Float)newPmt[6]); //subtract credit
                newPmt[7] = new Float(DV.flattenZero(runDue));
                payments.addRow(newPmt);
             }
            if (debug) System.out.println("Getting payments #"+payments.getRowCount());
            
        } 
    }

    public int getInvoiceKey(){
        return this.invoiceKey;
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
    public void setVAT(boolean vat){
        VAT = vat;
    }
    
    
    /* Private Object Data */
    private DefaultTableModel items = new DefaultTableModel(0,10);
    private DefaultTableModel invItems;
    private float itemTotal=0.00f;
    private DefaultTableModel returns; //??
    private DefaultTableModel payments;
    private int shippingKey = 0;
    private int shipToKey=0;
    private String shipToAddress="";
    private int custKey;
    private int invoiceKey=0;
    private String invoiceNumber;
    private long invoiceDate=0;
    private String customer;
    private String custEmail="";
    private String message;
    private boolean paid;
    private float tax1Rate;
    private float tax1Total;
    private float tax2Rate;
    private float tax2Total;
    private boolean VAT;
    private float total;
    private float dueNow;
    private boolean voide;
    private float shipping;
    //private InvoiceAux invaux;
    
    /* Database Management  */
    private DbEngine db;
    /* gets and sets also maintain these Object Arrays */
    private Object [] invoiceData = new Object [12];
    private InvoiceAux invoiceAuxData;
    
    
    private String invoiceTable="invoice";
    private boolean populated=false;
    private boolean posted = false;
    private boolean debug = false;
    
}
