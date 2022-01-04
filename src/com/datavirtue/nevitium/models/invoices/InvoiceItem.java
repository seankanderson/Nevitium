package com.datavirtue.nevitium.models.invoices;

import com.datavirtue.nevitium.database.orm.InvoiceItemDao;
import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "invoice_items", daoClass = InvoiceItemDao.class)
public class InvoiceItem extends BaseModel {

    @DatabaseField(foreign=true,foreignAutoRefresh=true)
    private Invoice invoice;    
    
    @DatabaseField
    private UUID sourceInventoryId;
    
    @DatabaseField
    private Date date = new Date();    
    @DatabaseField
    private double quantity;
    @DatabaseField
    private String code;
    @DatabaseField
    private String description;
    @DatabaseField
    private String weight;
    @DatabaseField
    private double unitPrice;
    @DatabaseField
    private boolean taxable1;
    @DatabaseField
    private double taxable1Rate;
    @DatabaseField
    private boolean taxable2;
    @DatabaseField
    private double taxable2Rate;
    @DatabaseField
    private double cost;
    
}
