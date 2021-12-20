package com.datavirtue.nevitium.models.contacts;

import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "contacts")
public class Contact extends BaseModel {    
    @DatabaseField
    private String company;
    @DatabaseField(canBeNull = false)
    private String firstName;
    @DatabaseField
    private String lastName;
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
    private String contact;
    @DatabaseField
    private String countryCode;
    @DatabaseField
    private String phone;
    @DatabaseField
    private String fax;    
    @DatabaseField
    private String email;
    @DatabaseField
    private String webLink;
    @DatabaseField    
    private String notes;
    @DatabaseField
    private boolean isCustomer;
    @DatabaseField
    private boolean isVendor;    
    @DatabaseField
    private boolean taxable1;
    @DatabaseField
    private boolean taxable2;
    @ForeignCollectionField(eager = false)
    private Collection<ContactAddress> addresses = new ArrayList();
    @ForeignCollectionField(eager = false)
    private Collection<ContactJournal> journals = new ArrayList();
        
}
