/*
 * ReportModel.java
 *
 * Created on July 6, 2006, 7:27 PM
 *
 * * Copyright (c) Data Virtue 2006, 2007, 2008, 2009
 */
package com.datavirtue.nevitium.database.reports;
import com.datavirtue.nevitium.services.util.CurrencyUtil;
import com.datavirtue.nevitium.services.util.DV;
import javax.swing.table.*;
          import java.text.*;
     import java.util.*;
     
/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */
public class ReportModel {
    
    /** Creates a new instance of ReportModel */
    public ReportModel(TableModel tm) {
    
        data =  tm;
        total_rows = data.getRowCount();
        current_row = 0;
        
    }
    
    
    public ReportModel(TableModel tm, int sort_col) {
    
        data =  tm;
        total_rows = data.getRowCount();
        
        //somehow sort the table or build map
        
        current_row = 0;
        
    }
    
    public Object getRealValue (int col) {
        
        return data.getValueAt(current_row, col);
        
    }
    
    public String getValueAt (int col) {
        
        return convertToString( data.getValueAt(current_row, col) );
   }
        
   
    public boolean next () {
       
        
        //modify this to work with sort list
       if (!sorted){
           
           if (current_row < total_rows - 1) {
       
            
                current_row++;
                return true;
            
            } else return false;
       
       
       } else {
           
           return false;//make sorted getter for data
           
       }
       
   }
   
    /*public void addDataRow (Object [] obj) {
        
       data.addRow(obj);
        
        
    }
    
    
    public void addModel (TableModel new_model){
        
    if (new_model.getRowCount()> 0) {
        
        Object [] row = new Object [new_model.getColumnCount()];
        
        for (int r = 0; r < new_model.getRowCount(); r++){
            
            for (int c = 0; c < data.getColumnCount(); c++){
                
             row[c] = new_model.getValueAt(r,c);   
            }
           
            data.addRow(row); 
            
            
        }
        
    }
        
        
  }*/
    
    /**Do not parse numbers from this method, they are formatted! */
    public String convertToString (Object obj) {
        
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);


     int q = DV.whatIsIt(obj);
            
            switch (q) {
                
                case 1: return new String ( (String) obj );
                
                case 2: return Integer.toString( (Integer) obj );
                
                case 3: return CurrencyUtil.money ( (Float) obj ) ;
                
                case 4: if ((Boolean) obj) return "*"; else return " ";
                
                case 5: return (df.format(new Date((Long)obj)));
                
                case 0:  return "Unknown object!";
            }
        
        
        
        return "";   
    }
    
    public int getTotalRows () {
        
     return total_rows;   
               
    }
    
    
    private TableModel data;
    private int total_rows;
    private int current_row;
    private boolean sorted = false;
}
