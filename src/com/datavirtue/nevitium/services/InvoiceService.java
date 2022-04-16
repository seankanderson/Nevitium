package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.services.exceptions.PartialQuantityException;
import com.datavirtue.nevitium.database.orm.InvoiceDao;
import com.datavirtue.nevitium.models.contacts.Contact;
import com.datavirtue.nevitium.models.contacts.ContactAddress;
import com.datavirtue.nevitium.models.inventory.Inventory;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceCustomerInfo;
import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.datavirtue.nevitium.models.invoices.InvoiceItemReturn;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.datavirtue.nevitium.models.invoices.InvoiceTotals;
import com.datavirtue.nevitium.services.exceptions.InvoiceItemAlreadyReturnedException;
import com.datavirtue.nevitium.services.exceptions.InvoiceVoidedException;
import com.datavirtue.nevitium.services.util.CurrencyUtil;
import com.datavirtue.nevitium.ui.util.Tools;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.misc.TransactionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private InvoiceCustomerInfoService customerInfoService;
    @Inject
    private InvoiceItemReturnService invoiceItemReturnService;
    @Inject
    private InvoicePaymentService invoicePaymentService;
    @Inject
    private InvoicePaymentTypeService paymentTypeService;
    @Inject
    private AppSettingsService appSettingsService;

    @Override
    public InvoiceDao getDao() throws SQLException {
        return DaoManager.createDao(connection, Invoice.class);
    }

    public List<Invoice> getAllInvoices() throws SQLException {
        return this.getDao().queryForAll();
    }

    public List<Invoice> getAllQuotes() throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("isQuote", true).and().eq("voided", false);
        return result.query();
    }

    public List<Invoice> getUnpaidInvoices() throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("paid", false).and().eq("voided", false).and().eq("isQuote", false);
        return result.query();
    }

    public List<Invoice> getPaidInvoices() throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("paid", true).and().eq("voided", false).and().eq("isQuote", false);
        return result.query();
    }

    public List<InvoiceItemReturn> getReturnsForInvoice(Invoice invoice) throws SQLException {
        return invoiceItemReturnService.getReturnsForInvoice(invoice);
    }

    public List<Invoice> getVoidInvoices() throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("voided", true).and().eq("isQuote", false);;
        return result.query();
    }

    public String getNewInvoiceNumber(String prefix) {
        var now = Calendar.getInstance().getTime();
        var dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
        var dateString = dateFormat.format(now);
        return prefix + dateString.substring(2, dateString.length() - 2);
    }

    public InvoiceCustomerInfo mapContactToInvoiceCustomer(Contact contact) {

        var invoiceCustomer = new InvoiceCustomerInfo();

        invoiceCustomer.setCompanyName(contact.getCompanyName());
        invoiceCustomer.setContactName(contact.getContactName());
        invoiceCustomer.setAddress1(contact.getAddress1());
        invoiceCustomer.setAddress2(contact.getAddress2());
        invoiceCustomer.setCity(contact.getCity());
        invoiceCustomer.setState(contact.getState());
        invoiceCustomer.setPostalCode(contact.getPostalCode());
        invoiceCustomer.setCountryCode(contact.getCountryCode());
        invoiceCustomer.setTaxId(contact.getTaxId());
        invoiceCustomer.setPhoneNumber(contact.getPhone());

        return invoiceCustomer;
    }

    public InvoiceCustomerInfo mapContactAddressToInvoiceShipTo(ContactAddress contactAddress) {

        var invoiceCustomer = new InvoiceCustomerInfo();

        invoiceCustomer.setCompanyName(contactAddress.getCompanyName());
        invoiceCustomer.setContactName(contactAddress.getContactName());
        invoiceCustomer.setAddress1(contactAddress.getAddress1());
        invoiceCustomer.setAddress2(contactAddress.getAddress2());
        invoiceCustomer.setCity(contactAddress.getCity());
        invoiceCustomer.setState(contactAddress.getState());
        invoiceCustomer.setPostalCode(contactAddress.getPostalCode());
        invoiceCustomer.setCountryCode(contactAddress.getCountryCode());
        invoiceCustomer.setPhoneNumber(contactAddress.getPhone());

        return invoiceCustomer;
    }

    // https://www.calculator.net/sales-tax-calculator.html?beforetax=199.99&taxrate=7.0&finalprice=&x=51&y=20
    public static double getDiscountTotalForItem(InvoiceItem item) {
        var allItems = item.getInvoice().getItems();

//            var discountItems = allItems
//                    .stream()
//                    .filter(x -> x.isDiscount() && x.getRelatedInvoiceItem() == item)
//                    .collect(Collectors.toList());
        var discountItems = new ArrayList<InvoiceItem>();

        for (var invoiceItem : allItems) { // the lambda expression failed to accomplish the below -- smells like an order-of-operations problem
            if (invoiceItem.isDiscount()) {
                if (invoiceItem.getRelatedInvoiceItem() != null) {
                    if (invoiceItem.getRelatedInvoiceItem().getId() == null) {
                        if (invoiceItem.getRelatedInvoiceItem() == item) {
                            discountItems.add(invoiceItem);
                        }
                    } else {
                        if (invoiceItem.getRelatedInvoiceItem().getId().equals(item.getId())) {
                            discountItems.add(invoiceItem);
                        }
                    }
                }
            }
        }

        var itemDiscount = discountItems.stream().mapToDouble(x -> x.getUnitPrice()).sum();
        return itemDiscount;
    }

    public static InvoiceTotals calculateInvoiceTotals(Invoice invoice) {
        var totals = new InvoiceTotals();

        if (invoice.getItems() == null) {
            return totals;
        }

        for (var item : invoice.getItems()) {
            if (item.isDiscount()) {
                continue;
            }

            // full invoice discount for negative misc (non-inventory) item
            if (item.getSourceInventoryId() == null && item.getUnitPrice() < 0) {
                totals.pretaxDiscounts += item.getUnitPrice();
                continue;
            }

            var itemTotal = item.getItemSubtotal();

            totals.pretaxDiscounts += getDiscountTotalForItem(item);
            totals.subTotal += itemTotal;

            if (item.isTaxable1()) {
                totals.taxable1Subtotal += itemTotal;
                totals.taxableRate1 = item.getTaxable1Rate();
            }

            if (item.isTaxable2()) {
                totals.taxable2Subtotal += itemTotal;
                totals.taxableRate2 = item.getTaxable2Rate();
            }

        }
        totals.taxable1Subtotal += totals.pretaxDiscounts;
        totals.taxable2Subtotal += totals.pretaxDiscounts;
        totals.taxable1Subtotal = totals.taxable1Subtotal < 0 ? 0.00 : totals.taxable1Subtotal;
        totals.taxable2Subtotal = totals.taxable2Subtotal < 0 ? 0.00 : totals.taxable2Subtotal;
        totals.subTotal = CurrencyUtil.round(totals.subTotal);
        totals.pretaxDiscounts = CurrencyUtil.round(totals.pretaxDiscounts);
        totals.taxable1Subtotal = CurrencyUtil.round(totals.taxable1Subtotal);
        totals.taxable2Subtotal = CurrencyUtil.round(totals.taxable2Subtotal);
        totals.tax1Total = CurrencyUtil.round(totals.taxable1Subtotal * totals.taxableRate1);
        totals.tax2Total = CurrencyUtil.round(totals.taxable2Subtotal * totals.taxableRate2);

        return totals;
    }

    public double calculateTotalCredits(Invoice invoice) throws SQLException {
        var payments = invoicePaymentService.getAllCreditsForInvoice(invoice);
        var credits = payments.stream().mapToDouble(x -> x.getCredit()).sum();
        return CurrencyUtil.round(credits);
    }

    public double calculateTotalDebits(Invoice invoice) throws SQLException {
        var payments = invoicePaymentService.getAllDebitsForInvoice(invoice);
        var debits = payments.stream().mapToDouble(x -> x.getDebit()).sum();
        return CurrencyUtil.round(debits);
    }

    public double calculateInvoiceAmountDue(Invoice invoice) throws SQLException {
        var totals = calculateInvoiceTotals(invoice);
        var debits = calculateTotalDebits(invoice);
        var credits = calculateTotalCredits(invoice);
        return CurrencyUtil.round((totals.getGrandTotal() + debits) - credits);
    }

    public double calculateNumberSold(InvoiceItem item) {
        var items = item.getInvoice().getItems();

        if (item.getSourceInventoryId() != null) {
            var itemsSold = items.stream()
                    .filter(f -> f.getSourceInventoryId().equals(item.getSourceInventoryId()))
                    .mapToDouble(x -> x.getQuantity()).sum();
            return itemsSold;
        } else {
            var itemsSold = items.stream()
                    .filter(f -> f.getDescription().equalsIgnoreCase(item.getDescription()))
                    .mapToDouble(x -> x.getQuantity()).sum();
            return itemsSold;
        }
    }

    public double calculateNumberReturned(InvoiceItem item) {
        var returns = item.getInvoice().getReturns();

        if (item.getSourceInventoryId() != null) {
            var itemsReturned = returns.stream()
                    .filter(f -> f.getSourceInventoryId().equals(item.getSourceInventoryId()))
                    .mapToDouble(x -> x.getQuantity()).sum();
            return itemsReturned;
        } else {
            var itemsReturned = returns.stream()
                    .filter(f -> f.getDescription().equalsIgnoreCase(item.getDescription()))
                    .mapToDouble(x -> x.getQuantity()).sum();
            return itemsReturned;
        }
    }

    public List<InvoicePayment> getPaymentsForInvoice(Invoice invoice) throws SQLException {

        List<InvoicePayment> payments = this.invoicePaymentService.getAllPaymentsForInvoice(invoice);

        Collections.sort(payments, new Comparator<InvoicePayment>() {
            public int compare(InvoicePayment o1, InvoicePayment o2) {
                return o2.getPaymentActivityDate().compareTo(o1.getPaymentActivityDate());
            }
        });

        return payments;
    }

    public void postInvoice(Invoice invoice) throws SQLException {

        var returnValue = TransactionManager.callInTransaction(this.connection,
                new Callable<Void>() {
            public Void call() throws SQLException {
                
                save(invoice);
                
                if (invoice.getBillTo() != null) {
                    invoice.getBillTo().setInvoice(invoice);
                    customerInfoService.save(invoice.getBillTo());
                }
                
                if (invoice.getShiptTo() != null) {
                    invoice.getShiptTo().setInvoice(invoice);
                    customerInfoService.save(invoice.getShiptTo());
                }
                
                save(invoice);
                
                // make sure invoice items reference the invoice
                for (var item : invoice.getItems()) {
                    item.setInvoice(invoice);
                    invoiceItemService.save(item);
                }

                if (!invoice.isQuote() && !invoice.isVoided()) {
                    for (var item : invoice.getItems()) { // reduce inventory quantities
                        if (item.getSourceInventoryId() == null) {
                            continue;
                        }
                        var inventory = inventoryService.getInventoryById(item.getSourceInventoryId());
                        inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                        inventoryService.save(inventory);
                    }
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
     * @return if a refund payemnt has been issued it is returned so that the
     * calling method can prompt the user to complete the payment
     * @throws SQLException
     * @throws PartialQuantityException
     * @throws InvoiceItemAlreadyReturnedException
     * @throws InvoiceVoidedException
     */
    public InvoicePayment returnInvoiceItem(InvoiceItem invoiceItem, InvoiceItemReturn itemReturn)
            throws SQLException,
            PartialQuantityException,
            InvoiceItemAlreadyReturnedException,
            InvoiceVoidedException {
        var invoice = invoiceItem.getInvoice();

        double proposedRetQty = itemReturn.getQuantity();
        double credit = itemReturn.getReturnCreditAmount();
        Date returnDate = itemReturn.getDate();

        if (invoice.isVoided()) {
            throw new InvoiceVoidedException("");
        }

        Inventory sourceInventory = null;
        if (invoiceItem.getSourceInventoryId() != null) {
            sourceInventory = inventoryService.getInventoryById(invoiceItem.getSourceInventoryId());
        }

        if (Tools.isDecimal(proposedRetQty) && !invoiceItem.isPartialSaleAllowed()) {
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

        invoice.getReturns().add(itemReturn);
        this.invoiceItemReturnService.save(itemReturn);

        var refundForItemReturn = new InvoicePayment();

        refundForItemReturn.setCredit(credit);
        refundForItemReturn.setPaymentActivityDate(new Date());
        refundForItemReturn.setPaymentEffectiveDate(returnDate);
        refundForItemReturn.setMemo("RETURN: " + invoiceItem.getDescription());
        refundForItemReturn.setInvoice(invoice);
        var paymentType = paymentTypeService.getTypeByName("Credit");
        refundForItemReturn.setPaymentType(paymentType);

        invoice.getPaymentActivity().add(refundForItemReturn);
        this.invoicePaymentService.save(refundForItemReturn);

        var invoiceDue = this.calculateInvoiceAmountDue(invoice);

        if (invoiceDue <= 0) {
            invoice.setPaid(true);

            if (invoiceDue < 0) { // TODO: make a setting "minimum dollar amount for refund payment" to skip this if the amount is too low to trigger a refund payment
                // create refund payment
                var invoiceRefund = new InvoicePayment();
                invoiceRefund.setDebit((invoiceDue * -1)); // convert negative amount to absolute value
                invoiceRefund.setInvoice(invoice);
                invoiceRefund.setMemo("Refund for overpayment after return");
                invoiceRefund.setPaymentActivityDate(new Date());
                invoiceRefund.setPaymentEffectiveDate(new Date()); // we are paying the customer today
                var refundPaymentType = paymentTypeService.getTypeByName("Refund");
                invoiceRefund.setPaymentType(refundPaymentType);
                return invoiceRefund;
            }

        }

        return null;
    }

    public void deletePayment(InvoicePayment payment) throws SQLException {
        invoicePaymentService.delete(payment);
    }

}
