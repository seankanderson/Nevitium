package models;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author SeanAnderson
 */
public abstract class AbstractCollectionTableModel<T> extends AbstractTableModel{

    protected String[] columns;
    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    protected List<T> items; 
    public void setCollection(List<T> collection) {
        items = collection;
    }
    public List<T> getCollection() {
        return items;
    }    
            
    @Override
    public int getRowCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return columns != null ? columns.length : 0;
    }
    
    @Override
    public String getColumnName(int columnIndex){
         return columns[columnIndex];
    }
    
    public abstract void setValueAt(Object value, int row, int col);
    
    public void setValueAt(int rowIndex, T value) {
        items.set(rowIndex, value);
    }
    
    public void setValueAt(T value){
        items.add(value);
    }
    
    public Object getValueAt(int rowIndex) {
        if (items == null) {
            return null;
        }
        return items.get(rowIndex);
    }  
}
