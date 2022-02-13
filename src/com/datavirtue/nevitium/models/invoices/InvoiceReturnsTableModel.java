
package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.models.AbstractCollectionTableModel;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceReturnsTableModel extends AbstractCollectionTableModel<InvoiceItemReturn> {

    public InvoiceReturnsTableModel(List<InvoiceItemReturn> items) {
        this.items = items;
        this.columns = new String[]{"Quantity", "Code", "Description", "Unit", "Tax1", "Tax2", "Total",};
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);

        switch (col) {
            case 0:
                item.setQuantity((Double) value);
                break;
            case 1:
                item.setCode((String) value);
                break;
            case 2:
                item.setDescription((String) value);
                break;
            case 3:
                item.setCode((String) value);
                break;
            case 4:
                item.setItemCreditAmount((Double) value);
                break;
            case 5:
                item.setTaxable1((Boolean) value);
                break;
            case 6:
                item.setTaxable2((Boolean) value);
                break;
            case 7:
                
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
                return item.getQuantity();

            case 1:
                return item.getCode();

            case 2:
                return item.getDescription();

            case 3:
                return item.getCode();

            case 4:
                return item.getItemCreditAmount();

            case 5:
                return item.isTaxable1();

            case 6:
                return item.isTaxable2();

            case 7:
                return (item.getQuantity() * item.getItemCreditAmount()); // TODO: add taxable logic

        }
        return null;

    }


}
