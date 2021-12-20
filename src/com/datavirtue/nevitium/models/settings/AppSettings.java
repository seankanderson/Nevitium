package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class AppSettings {
    private CompanySettings company;
    private DataSettings backups;
    private InternetSettings internet;
    private InventorySettings inventory;
    private InvoiceSettings invoice;
    private SecuritySettings security;
    private OutputSettings output;
    private SoftwareVersion softwareVersion = new SoftwareVersion("2","0","0", "2022-02-28"); 
    
}
