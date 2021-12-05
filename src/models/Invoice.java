package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author SeanAnderson
 */
@DatabaseTable(tableName = "invoices")
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
    
    @ForeignCollectionField(eager = false)
    private Collection<InvoiceItem> items = new ArrayList();
    
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(boolean voided) {
        this.voided = voided;
    }

     public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Collection<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(Collection<InvoiceItem> items) {
        this.items = items;
    }
    
    
}
