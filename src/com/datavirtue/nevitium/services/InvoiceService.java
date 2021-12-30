package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoiceDao;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.j256.ormlite.dao.DaoManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceService extends BaseService<InvoiceDao, Invoice> {

    @Override
    public InvoiceDao getDao() throws SQLException {
        return DaoManager.createDao(connection, Invoice.class);
    }
    
    
    public String getNewInvoiceNumber(String prefix) {
        var now = Calendar.getInstance().getTime();
        var dateFormat = new SimpleDateFormat("yyyyMMddHHmmssff");
        var dateString = dateFormat.format(now);
        return prefix + dateString.substring(2, dateString.length() - 2);
    } 
    
    public double getTax1Total(Invoice invoice) {
        double taxTotal=0;
        for(var item : invoice.getItems()) {
            taxTotal += getItemTax1Total(item);
        }
        return taxTotal;
    }
    
    public double getTax2Total(Invoice invoice) {
        double taxTotal=0;
        for(var item : invoice.getItems()) {
            taxTotal += getItemTax1Total(item);
        }
        return taxTotal;
    }
    
    public double getSubtotal(Invoice invoice){
        double itemTotal=0;
        for(var item : invoice.getItems()) {
            itemTotal += getItemSubTotal(item);
        }
        return itemTotal;
    }
    
    public double getItemSubTotal(InvoiceItem item) {
        return item.getQuantity() > 0 && item.getUnitPrice() > 0 ? (item.getQuantity() * item.getUnitPrice()) : 0;
    }
    
    public double getItemTax1Total(InvoiceItem item){
        return item.isTaxable1() && item.getTaxable1Rate() > 0 ? (item.getQuantity() * item.getUnitPrice()) * item.getTaxable1Rate() : 0;
    }
    
    public double getItemTax2Total(InvoiceItem item){
        return item.isTaxable1() && item.getTaxable2Rate() > 0 ? (item.getQuantity() * item.getUnitPrice()) * item.getTaxable2Rate() : 0;
    }
        
 }
