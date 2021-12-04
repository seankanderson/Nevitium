package businessmanager.Inventory;

import java.util.List;
import models.AbstractCollectionTableModel;
import models.Inventory;

/**
 * December 1st 2021
 * @author SeanAnderson
 */
public class InventoryTableModel extends AbstractCollectionTableModel<Inventory>{
     
    public InventoryTableModel(List<Inventory> inventory) {
        this.items = inventory;
        this.columns = new String[]{ "Code", "Description", "Quantity", "Price", "Category"};
    }
        
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);
        
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
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (items == null) {
            return new Object();
        }
        var item = items.get(rowIndex);
        
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
