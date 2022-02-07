
package com.datavirtue.nevitium.models.invoices;

import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author SeanAnderson
 */
public class PaymentTypeComboModel extends AbstractListModel implements ComboBoxModel{

    private List<InvoicePaymentType> paymentTypes;
    public PaymentTypeComboModel(List<InvoicePaymentType> pTypes) {
        this.paymentTypes = pTypes;
    }
    
    private String selection;
    
    @Override
    public Object getElementAt(int index) {
        return paymentTypes.get(index).getName();
    }
    
    public InvoicePaymentType getType(int index) {
        return paymentTypes.get(index);
    }

    @Override
    public int getSize() {
        return this.paymentTypes.size();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selection = (String)anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selection;
    }
    
           
    
   
}
