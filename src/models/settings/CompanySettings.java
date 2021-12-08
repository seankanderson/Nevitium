package models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 * Tuesday DEc 7th 2021
 * @author SeanAnderson
 */
@Getter @Setter
public class CompanySettings {
    private String companyName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String addressFormat = "US";
    private String phoneNumber;
    private String email;
    private String taxId;
    private boolean showTaxIdOnInvoice = false;
    private String invoiceLogo;
    private String invoiceLogoMimeType;
    private String mainScreenLogo;
    private String mainScreenLogoMimeType;
}
