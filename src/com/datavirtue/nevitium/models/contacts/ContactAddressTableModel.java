package com.datavirtue.nevitium.models.contacts;

import com.datavirtue.nevitium.models.AbstractCollectionTableModel;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class ContactAddressTableModel extends AbstractCollectionTableModel<ContactAddress> {

    public ContactAddressTableModel(List<ContactAddress> addresses) {
        this.items = addresses;
        this.columns = new String[]{"Attention", "Street", "City", "State", "Country"};
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);

        switch (col) {
            case 0:
                item.setAttention((String) value);
                break;
            case 1:
                item.setAddress1((String) value);
                break;
            case 2:
                item.setCity((String) value);
                break;
            case 3:
                item.setState((String) value);
                break;
            case 4:
                item.setCountryCode((String) value);
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
                return item.getAttention();
            case 1:
                return item.getAddress1();
            case 2:
                return item.getCity();
            case 3:
                return item.getState();
            case 4:
                return item.getCountryCode();
        }
        return null;

    }

}
