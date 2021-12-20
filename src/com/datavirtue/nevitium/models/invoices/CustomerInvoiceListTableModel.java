package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.models.AbstractCollectionTableModel;
import java.util.Date;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class CustomerInvoiceListTableModel extends AbstractCollectionTableModel<Invoice> {

    public CustomerInvoiceListTableModel(List<Invoice> invoices) {
        this.items = invoices;
        this.columns = new String[]{"Invoice Date", "Paid"};
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);

        switch (col) {
            case 0:
                item.setInvoiceDate((Date) value);
                ;
                break;
            case 1:
                item.setPaid((Boolean) value);
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
                return item.getInvoiceDate();
            case 1:
                return item.isPaid();

        }
        return null;

    }

}
