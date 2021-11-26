/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package businessmanager.Inventory;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import models.Inventory;

/**
 *
 * @author SeanAnderson
 */
public class InventoryTableModel extends AbstractTableModel{

    private List<Inventory> inventory;
   
    public InventoryTableModel(List<Inventory> inventory) {
        this.inventory = inventory;
    }
    
    private String[] columns = { "Code", "Description", "Quantity", "Price", "Category"};
      
    @Override
    public int getRowCount() {
        if (inventory != null) {
            return inventory.size();
        } else {
            return 0;
        }        
    }

    @Override
    public int getColumnCount() {
        if (columns != null)
        {
            return columns.length;
        } else {
            return 0;
        }
    }
    
    @Override
    public String getColumnName(int columnIndex){
         return columns[columnIndex];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (inventory == null) {
            return;
        }
        var item = inventory.get(row);
        
        switch (col) {
            case 0: 
                item.setCode((String)value);
                break;
            case 1:
                item.setDescription((String)value);
                break;
            case 2:
                item.setQuantity((Double)value);
                break;
            case 3:
                item.setPrice((Double)value);
                break;
            case 4:
                item.setCategory((String)value);
                break;
           }
    }
    
    public void setValueAt(int rowIndex, Inventory value) {
        inventory.set(rowIndex, value);
    }
    
    public void setValueAt(Inventory value){
        inventory.add(value);
    }
    
    public Object getValueAt(int rowIndex) {
        if (inventory == null) {
            return new Object();
        }
        return inventory.get(rowIndex);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (inventory == null) {
            return new Object();
        }
        var item = inventory.get(rowIndex);
        
        switch (columnIndex) {
            case 0: 
                return item.getCode();
            case 1:
                return item.getDescription();
            case 2:
                return item.getQuantity();
            case 3:
                return item.getPrice();
            case 4:
                return item.getCategory();
           }
           return null;
        
    }
    
}
