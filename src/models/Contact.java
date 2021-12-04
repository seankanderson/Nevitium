/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author SeanAnderson
 */
@DatabaseTable(tableName = "contacts")
public class Contact extends BaseModel {    
    @DatabaseField
    private String company;
    @DatabaseField(canBeNull = false)
    private String firstName;
    @DatabaseField
    private String lastName;
    @DatabaseField
    private String contact;    
    @DatabaseField
    private String phone;    
    @DatabaseField    
    private String notes;
    @DatabaseField
    private boolean customer;
    @DatabaseField
    private boolean vendor;
    @DatabaseField
    private String countryCode;
    @DatabaseField
    private boolean taxable1;
    @DatabaseField
    private boolean taxable2;  
    
    @ForeignCollectionField(eager = false)
    private Collection<ContactAddress> addresses = new ArrayList();
    
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
 
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
     public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isCustomer() {
        return customer;
    }

    public void setCustomer(boolean customer) {
        this.customer = customer;
    }

    public boolean isVendor() {
        return vendor;
    }

    public void setVendor(boolean vendor) {
        this.vendor = vendor;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isTaxable1() {
        return taxable1;
    }

    public void setTaxable1(boolean taxable1) {
        this.taxable1 = taxable1;
    }

    public boolean isTaxable2() {
        return taxable2;
    }

    public void setTaxable2(boolean taxable2) {
        this.taxable2 = taxable2;
    }

    public Collection<ContactAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(Collection<ContactAddress> addresses) {
        this.addresses = addresses;
    }
    
}
