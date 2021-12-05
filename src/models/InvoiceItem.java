/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

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
@DatabaseTable(tableName = "invoice_items")
public class InvoiceItem extends BaseModel {

    @DatabaseField(foreign=true,foreignAutoRefresh=true)
    public Invoice invoice;    
    
    @DatabaseField
    private Date date = new Date();    
    @DatabaseField
    private double quantity;
    @DatabaseField
    private String code;
    @DatabaseField
    private String description;
    @DatabaseField
    private double unitPrice;
    @DatabaseField
    private boolean taxable1;
    @DatabaseField
    private boolean taxable2;
    @DatabaseField
    private double cost;
    
}
