package com.datavirtue.nevitium.models.contacts;

import com.datavirtue.nevitium.models.contacts.Contact;
import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 * connship.sch
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "contact_addresses")
public class ContactAddress extends BaseModel {
     
    @DatabaseField(foreign=true,foreignAutoRefresh=true, canBeNull = false)
    private Contact contact;
    
    @DatabaseField
    private String company;
    @DatabaseField
    private String attention;
    @DatabaseField
    private String address1;
    @DatabaseField
    private String address2;
    @DatabaseField
    private String city;
    @DatabaseField
    private String state;
    @DatabaseField
    private String postalCode;
    @DatabaseField
    private String countryCode;
    @DatabaseField
    private String phone;
    @DatabaseField
    private String addressType;
    @DatabaseField
    private boolean defaultAddress;
        
}

