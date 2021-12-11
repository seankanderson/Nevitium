package models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class InventorySettings {
    private double defaultProductMarkupFactor = 2.7;
    private String weightUnit = "lbs"; // kgs
    private boolean ignoreQuantityWarnings = false;
    private boolean addCategoryLineToInvoiceItems = false;
    private String defaultInventorySearchField = "upc";
    
}
