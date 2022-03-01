
package com.datavirtue.nevitium.models.invoices;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class InvoiceTotals {
    
    double tax1 = 0.00;
    double tax2 = 0.00;
    double items = 0.00;
    double grand = 0.00;
    
}
