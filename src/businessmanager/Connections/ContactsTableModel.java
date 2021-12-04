package businessmanager.Connections;

import java.util.List;
import models.AbstractCollectionTableModel;
import models.Contact;
import models.Inventory;

/**
 * everybody learned that they want to work from home and hang out with thier co-workers in person, all at the same time 
 * I guess these are things running through zuck's mind right before he leaps out of bed to declare his love for the metaverse 
 * the last thing we need is another mark zuckerberg accidental learning experience, move fast and break everything 
 * as every good oligarchy knows: flowery language and lofty goals (paid for by a tax on Satan) and distractions are the poetry one uses to fuck-over large groups of people
 * if we are going to allow people to move fast and break things, everything: the body politic sayeth: 
 * "Hold my beer...I would like to try a three day work week with full salary and no-hassle healthcare--for free. Throw in college and prescription drugs too...and maybe Amazon Prime?" 
 * You built it, now we are coming.
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
