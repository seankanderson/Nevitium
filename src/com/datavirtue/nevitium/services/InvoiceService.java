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
import java.util.Calendar;
import java.util.Date;
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
        var dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
        var dateString = dateFormat.format(now);
        return prefix + dateString.substring(2, dateString.length() - 2);
    }

    public double getTax1Total(Invoice invoice) {
        if (invoice.getItems() == null) {
            return 0.00;
        }
        double taxTotal = 0;
        for (var item : invoice.getItems()) {
            taxTotal += getItemTax1Total(item);
        }
        return taxTotal;
    }

    public double getTax2Total(Invoice invoice) {
        if (invoice.getItems() == null) {
            return 0.00;
        }
        double taxTotal = 0;
        for (var item : invoice.getItems()) {
            taxTotal += getItemTax2Total(item);
        }
        return taxTotal;
    }

    public double getSubtotal(Invoice invoice) {
        if (invoice.getItems() == null) {
            return 0.00;
        }
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
        return item.isTaxable2() && item.getTaxable2Rate() > 0 ? (item.getQuantity() * item.getUnitPrice()) * item.getTaxable2Rate() : 0;
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
     * @return if a refund payemnt has been issued it is returned so that the calling method can prompt the user to complete the payment
     * @throws SQLException
     * @throws PartialQuantityException
     * @throws InvoiceItemAlreadyReturnedException
     * @throws InvoiceVoidedException
     */
    public InvoicePayment returnInvoiceItem(InvoiceItem invoiceItem, double proposedRetQty, double credit, Date returnDate)
            throws SQLException,
            PartialQuantityException,
            InvoiceItemAlreadyReturnedException,
            InvoiceVoidedException {
        var invoice = invoiceItem.getInvoice();

        if (invoice.isVoided()) {
            throw new InvoiceVoidedException("");
        }

        Inventory sourceInventory = null;
        if (invoiceItem.getSourceInventoryId() != null) {
            sourceInventory = inventoryService.getInventoryById(invoiceItem.getSourceInventoryId());
        }

        if (Tools.isDecimal(credit) && !invoiceItem.isPartialSaleAllowed()) {
            throw new PartialQuantityException("Partial quantity return was attempted on an item that does not support partial quantity sales: " + invoiceItem.getDescription());
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

            if (invoiceDue < 0) { // TODO: make a setting "minimum dollar amount for refund payment" to skip this if the amount is too low to trigger a refund payment
                // create refund payment
                var invoiceRefund = new InvoicePayment();
                invoiceRefund.setCredit((invoiceDue * -1)); // convert negative amount to absolute value
                invoiceRefund.setInvoice(invoice);
                invoiceRefund.setMemo("Return caused overpayment");
                invoiceRefund.setPaymentActivityDate(new Date());
                invoiceRefund.setPaymentEffectiveDate(new Date()); // we are paying the customer today
                invoiceRefund.setType("REFUND");
                
                //k m67 867y nmhuj, nmb8invoice.getPaymentActivity().add(invoiceRefund);                
                //this.invoicePaymentService.save(invoiceRefund);
                return invoiceRefund;
            }

        }

        return null;
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
    
    public void deletePayment(InvoicePayment payment) throws SQLException {        
        invoicePaymentService.delete(payment);
    }

}
