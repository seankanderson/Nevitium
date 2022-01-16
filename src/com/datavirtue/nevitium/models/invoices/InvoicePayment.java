
package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.database.orm.InvoicePaymentDao;
import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "invoice_payments", daoClass = InvoicePaymentDao.class)
public class InvoicePayment extends BaseModel {

    @DatabaseField(foreign=true,foreignAutoRefresh=true)
    private Invoice invoice;    
    
    @DatabaseField
    private Date paymentEffectiveDate = new Date();    
    
    @DatabaseField
    private Date paymentActivityDate = new Date();    
    
    @DatabaseField
    private String type;
    
    @DatabaseField
    private String memo;
    
    @DatabaseField
    private double debit;
    @DatabaseField
    private double credit;
    
    @DatabaseField
    private String paymentSystem;
    
    @DatabaseField
    private String paymentSystemReference;
    
    
    
}
