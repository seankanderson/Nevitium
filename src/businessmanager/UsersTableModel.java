package businessmanager;

import java.util.List;
import models.AbstractCollectionTableModel;
import models.User;

/**
 *
 * @author SeanAnderson
 */
public class UsersTableModel extends AbstractCollectionTableModel<User> {

    public UsersTableModel(List<User> users) {
        this.items = users;
        this.columns = new String[]{ "User Name", "Admin"};
    }
        
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);
        
        switch (col) {
            case 0: 
                item.setUserName((String)value);
                break;
            case 1:
                item.setAdmin((Boolean)value);
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
                return item.getUserName();
            case 1:
                return item.isAdmin();
            
           }
           return null;
        
    }
    
    
}
