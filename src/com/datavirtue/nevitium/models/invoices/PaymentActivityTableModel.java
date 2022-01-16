

package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.models.AbstractCollectionTableModel;
import java.util.Date;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class PaymentActivityTableModel extends AbstractCollectionTableModel<InvoicePayment> {

    public PaymentActivityTableModel(List<InvoicePayment> activity) {
        this.items = activity;
        this.columns = new String[]{"Process Date", "Effective Date", "Memo", "Debit", "Credit", "Type", "Payment System",};
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (items == null) {
            return;
        }
        var item = items.get(row);

        switch (col) {
            case 0:
                item.setPaymentActivityDate((Date) value);
                break;
            case 1:
                item.setPaymentEffectiveDate((Date) value);
                break;
            case 2:
                item.setMemo((String) value);
                break;
            case 3:
                item.setDebit((Double) value);
                break;
            case 4:
                item.setCredit((Double) value);
                break;
            case 5:
                item.setType((String) value);
                break;
            case 6:
                item.setPaymentSystem((String) value);
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
                return item.getPaymentActivityDate();

            case 1:
                return item.getPaymentEffectiveDate();

            case 2:
                return item.getMemo();

            case 3:
                return item.getDebit();

            case 4:
                return item.getCredit();

            case 5:
                return item.getType();

            case 6:
                return item.getPaymentSystem();

        }
        return null;

    }


}
