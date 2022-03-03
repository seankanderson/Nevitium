
package com.datavirtue.nevitium.models.invoices;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson 
 */
@Getter @Setter
public class InvoiceTotals {
    
    public double taxable1Subtotal = 0.00; // how much of the subtotal is taxable - subtract discounts from this total
    public double taxable2Subtotal = 0.00;  
    public double taxableRate1 = 0.00;
    public double taxableRate2 = 0.00;
    public double tax1Total = 0.00; // tax rate multiplied by the taxableSubtotal
    public double tax2Total = 0.00; 
    public double pretaxDiscounts = 0.00;
    public double subTotal = 0.00; // subtotal without regard to taxes
    public double getGrandTotal() {
        return tax1Total + tax2Total + (subTotal + pretaxDiscounts);
    }
    
}
