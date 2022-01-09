package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.services.exceptions.PartialQuantityException;
import com.datavirtue.nevitium.database.orm.InvoiceDao;
import com.datavirtue.nevitium.models.inventory.Inventory;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.datavirtue.nevitium.services.exceptions.InvoiceItemAlreadyReturnedException;
import com.datavirtue.nevitium.services.exceptions.InvoiceVoidedException;
import com.datavirtue.nevitium.ui.util.Tools;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.TransactionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceService extends BaseService<InvoiceDao, Invoice> {

    @Inject
    private InventoryService inventoryService;
    @Inject
    private InvoiceItemService invoiceItemService;
    @Inject
    private InvoicePaymentService invoicePaymentService;
    @Inject
    private AppSettingsService appSettingsService;

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
        double taxTotal = 0;
        for (var item : invoice.getItems()) {
            taxTotal += getItemTax1Total(item);
        }
        return taxTotal;
    }

    public double getTax2Total(Invoice invoice) {
        double taxTotal = 0;
        for (var item : invoice.getItems()) {
            taxTotal += getItemTax1Total(item);
        }
        return taxTotal;
    }

    public double getSubtotal(Invoice invoice) {
        double itemTotal = 0;
        for (var item : invoice.getItems()) {
            itemTotal += getItemSubTotal(item);
        }
        return itemTotal;
    }

    public double getItemSubTotal(InvoiceItem item) {
        return item.getQuantity() > 0 && item.getUnitPrice() > 0 ? (item.getQuantity() * item.getUnitPrice()) : 0;
    }

    public double getItemTax1Total(InvoiceItem item) {
        return item.isTaxable1() && item.getTaxable1Rate() > 0 ? (item.getQuantity() * item.getUnitPrice()) * item.getTaxable1Rate() : 0;
    }

    public double getItemTax2Total(InvoiceItem item) {
        return item.isTaxable1() && item.getTaxable2Rate() > 0 ? (item.getQuantity() * item.getUnitPrice()) * item.getTaxable2Rate() : 0;
    }

    public void postInvoice(Invoice invoice) throws SQLException {

        var returnValue = TransactionManager.callInTransaction(this.connection,
                new Callable<Void>() {
            public Void call() throws SQLException {

                // save invoice
                save(invoice);

                // make sure invoice items reference the invoice
                for (var item : invoice.getItems()) {
                    item.setInvoice(invoice);
                    invoiceItemService.save(item);
                }

                // reduce inventory quantities
                for (var item : invoice.getItems()) {
                    if (item.getSourceInventoryId() == null) {
                        continue;
                    }
                    var inventory = inventoryService.getInventoryById(item.getSourceInventoryId());
                    inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                    inventoryService.save(inventory);
                }
                return null;
            }
        });

    }

    /**
     * 
     * @param invoiceItem InvoiceItem should be populated with Invoice reference
     * @param proposedRetQty
     * @param credit
     * @param date
     * @return the invoice item return
     * @throws SQLException 
     * @throws PartialQuantityException
     * @throws InvoiceItemAlreadyReturnedException
     * @throws InvoiceVoidedException 
     */
    public InvoiceItem returnInvoiceItem(InvoiceItem invoiceItem, double proposedRetQty, double credit, Date returnDate) 
            throws SQLException, 
            PartialQuantityException,
            InvoiceItemAlreadyReturnedException,
            InvoiceVoidedException
    {
        var invoice = invoiceItem.getInvoice();

        if (invoice.isVoided()) {
            throw new InvoiceVoidedException("");
        }

        Inventory sourceInventory = null;
        if (invoiceItem.getSourceInventoryId() != null) {
            sourceInventory = inventoryService.getInventoryById(invoiceItem.getSourceInventoryId());
        }

        var settings = appSettingsService.getObject();

        if (Tools.isDecimal(credit) && !invoiceItem.isPartialSaleAllowed()) {
            throw new PartialQuantityException("Partial quantity return was attempted on an item that does not support partial quantities: " + invoiceItem.getDescription());
        }        
        
        /* Search the invoice for the same item and 
           calculate how many were sold*/        
        var numberSold = this.calculateNumberSold(invoiceItem);
        
        /* Search returns for same item, if found
           calculate how many have been returned*/
        var numberReturned = this.calculateNumberReturned(invoiceItem);

        /* Subtract returned from sold and check against proposed return qty
           if the amount is less than or equal to the remaining qty process return*/
        var difference = numberSold - numberReturned;
                
        if (difference < proposedRetQty) {
            throw new InvoiceItemAlreadyReturnedException("The invoice item has already been returned or the number you wanted to return was more than the number available to return: " + invoiceItem.getDescription());
        }
        
        var itemReturn = new InvoiceItem();
        
        itemReturn.setDescription(invoiceItem.getDescription());
        itemReturn.setQuantity(proposedRetQty);
        itemReturn.setInvoice(invoiceItem.getInvoice());
        itemReturn.setUnitPrice(credit);
        itemReturn.setCode("RETURN");
        itemReturn.setSourceInventoryId(invoiceItem.getSourceInventoryId());
        itemReturn.setDate(returnDate);
        
        invoice.getReturns().add(itemReturn); 
        
        this.invoiceItemService.save(itemReturn);
        
        var refundForItemReturn = new InvoicePayment();
        
        refundForItemReturn.setCredit(credit);
        refundForItemReturn.setPaymentActivityDate(new Date());
        refundForItemReturn.setPaymentEffectiveDate(returnDate);
        refundForItemReturn.setMemo("RETURN REFUND");
        refundForItemReturn.setInvoice(invoice);
        refundForItemReturn.setType("");
        
        invoice.getPaymentActivity().add(refundForItemReturn);
        
        this.invoicePaymentService.save(refundForItemReturn);
        
        var invoiceDue = this.calculateInvoiceAmountDue(invoice);
        
        if (invoiceDue <= 0) {
            invoice.setPaid(true);
            
            if (invoiceDue < 0) {
                // create refund payment
            }
            
        }
        
        return itemReturn;        
    }
    
    public double calculateNumberSold(InvoiceItem item) {
        return 0.00;
    }
    
    public double calculateNumberReturned(InvoiceItem item) {
        return 0.00;
    }
    
    public double calculateInvoiceAmountDue(Invoice invoice) {
        return 0.00;
    }

    public List<InvoiceItem> getReturnsForInvoice(Invoice invoice) {
        return new ArrayList();
    }
    
    public List<InvoiceItem> getSaleItemsForInvoice(Invoice invoice) {
        return new ArrayList();
    }
    
}
