package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.database.orm.InvoiceCustomerInfoDao;
import com.datavirtue.nevitium.models.BaseModel;
import com.datavirtue.nevitium.models.contacts.ContactAddressInterface;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "invoice_customer_info", daoClass = InvoiceCustomerInfoDao.class)
public class InvoiceCustomerInfo extends BaseModel implements ContactAddressInterface {
     
    @DatabaseField(foreign=true,foreignAutoRefresh=true, canBeNull = false)
    private Invoice invoice;
    
    @DatabaseField
    private String companyName;
    @DatabaseField
    private String contactName;
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
    private String phoneNumber;
    @DatabaseField
    private String emailAddress;
    @DatabaseField
    private String taxId;     
}
