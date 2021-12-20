package com.datavirtue.nevitium.models.contacts.old;

import java.util.List;
import com.datavirtue.nevitium.models.AbstractCollectionTableModel;
import com.datavirtue.nevitium.models.contacts.Contact;

/**
 
 * 2021-12-01
 * @author SeanAnderson
 */
public class ContactsTableModel extends AbstractCollectionTableModel<Contact> {

    public ContactsTableModel(List<Contact> contacts) {
        this.items = contacts;
        this.columns = new String[]{ "Company", "First Name", "Last Name", "Phone", "Country"};
    }
        
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);
        
        switch (col) {
            case 0: 
                item.setCompany((String)value);
                break;
            case 1:
                item.setFirstName((String)value);
                break;
            case 2:
                item.setLastName((String)value);
                break;
            case 3:
                item.setPhone((String)value);
                break;
            case 4:
                item.setCountryCode((String)value);
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
                return item.getCompany();
            case 1:
                return item.getFirstName();
            case 2:
                return item.getLastName();
            case 3:
                return item.getContact();
            case 4:
                return item.getCountryCode();
           }
           return null;
        
    }
    
    
}
