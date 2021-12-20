/*
 * InvoiceModel.java
 *
 * Created on July 8, 2006, 11:05 PM
 *
 * * Copyright (c) Data Virtue 2006
 */

package com.datavirtue.nevitium.models.invoices.old;
import java.io.*;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;


/**
 *
 * FORMAT OF LIST
 *----------------------
 * [DATA] 
 * Date
 * Number
 * Customer
 * Shipping
 * Message
 * Tax1
 * Tax2
 * Paid
 * Void
 *---------------------
 * [Item/Table]
 *---------------------
 *
 * 
 */

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007, 2008 All Rights Reserved.
 */
public class InvoiceModel implements Serializable {
    
    /** Creates a new instance of InvoiceModel */
    public InvoiceModel() {
    
    }
    
    public void addItems (javax.swing.table.TableModel tm) {
        
        //In use
        
        int cols = tm.getColumnCount();
        int rows = tm.getRowCount();
        
        setItemCount(rows, cols);
        
        for (int row = 0; row < rows; row++){
            
            for (int col =0; col < cols; col++) {
                   
                 //addItemElement( tm.getValueAt(row, col), row, col );
                  items.setValueAt( tm.getValueAt(row,col), row, col );  
             }
            
            
        }
                        
    }
    
    public void addItemElement (Object obj, int r, int c) {
        
        items.setValueAt(obj, r, c);
        
        
    }
    
    public Object [] getItem (int row) {
        
        int cols = items.getColumnCount();
        Object [] temp = new Object [cols];
        
        for (int col =0; col < cols; col++) {
            
            temp [col] = items.getValueAt(row, col);
            
        }
        
        return temp;
    }
    
    public void setItemCount(int r, int c) {
        
     items.setRowCount(r);
     items.setColumnCount(c);
    }
    
    public int getItemCount() {
     
        return items.getRowCount();
        
    }
    
           
    public void addData (Object d) {
       
        data.addElement(d);
        
    }
    
    public Object getData (int index) {
        
        return data.getElementAt(index);
        
    }
    
    public void clearModel () {
        items = null; data = null;
        items = new DefaultTableModel ();
        data = new DefaultListModel ();
    }
  
    public void save (String filename) {
        
        try {
             
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(this);
            out.close();
        
        } catch (Exception e) { e.printStackTrace(); } 
        
    }
    
    private DefaultTableModel items = new DefaultTableModel ();
    private DefaultListModel data = new DefaultListModel ();
    
}
