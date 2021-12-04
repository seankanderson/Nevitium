/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

/**
 *
 * @author SeanAnderson
 */
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
    
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
}
