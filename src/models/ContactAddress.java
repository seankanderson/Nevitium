/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;

/**
 * connship.sch
 * @author SeanAnderson
 */
@DatabaseTable(tableName = "contact_addresses")
public class ContactAddress extends BaseModel {
    
//    @DatabaseField(canBeNull = false)
//    private UUID contactId;
    
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
    
//    public UUID getContactId() {
//        return contactId;
//    }
//
//    public void setContactId(UUID contactId) {
//        this.contactId = contactId;
//    }
    
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

     public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    
    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
    
}

