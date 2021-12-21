package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.database.orm.InvoiceDao;
import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "invoices", daoClass = InvoiceDao.class)
public class Invoice extends BaseModel{

    @DatabaseField
    private String invoiceNumber;
    @DatabaseField
    private Date invoiceDate;
    @DatabaseField
    private String customer;
    @DatabaseField
    private UUID customerId;
    @DatabaseField
    private boolean voided;
    @DatabaseField
    private boolean paid;
    @DatabaseField
    private String message;
    @DatabaseField
    private double shippingFee;
    @DatabaseField
    private boolean isQuote;
    
    @ForeignCollectionField(eager = true)
    private Collection<InvoiceItem> items;
    
    
}
