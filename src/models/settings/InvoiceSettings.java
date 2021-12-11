package models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class InvoiceSettings {
    private String tax1Name;
    private String tax2Name;
    private double tax1Rate;
    private double tax2Rate;
    private boolean showTax1;
    private boolean showTax2;
    
    private double interestRate;
    private int interestGracePeriodInDays = 30;
    private double cashRoundingRate = 0;  // .05 and .10 are valid values
    
    private String invoiceName;
    private String invoicePrefix;
    private String quoteName;
    private String quotePrefix;
    private String billToLabel = "Bill To:";
    
    private boolean inkSaver = false;
    private boolean processPaymentOnPosting = true;
    private String defaultBarcodeScanField = "upc";
    private boolean pointOfSaleMode = false;
    private double recieptPaperWidthInMm = 80;
    
    private String currencySymbol = "$";
    private boolean printZeros = false;
    
    private long nextInvoiceNumber;
    private long nextQuoteNumber;
    
    
}
