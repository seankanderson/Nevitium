package com.datavirtue.nevitium.services;

import com.google.inject.Injector;
import com.datavirtue.nevitium.models.contacts.Contact;
import com.datavirtue.nevitium.models.contacts.ContactAddress;
import com.datavirtue.nevitium.models.inventory.Inventory;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.datavirtue.nevitium.models.invoices.InvoicePaymentType;
import com.datavirtue.nevitium.ui.util.Tools;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.BackingStoreException;

/**
 *
 * @author SeanAnderson
 */
public class TestDataService {

    public static void populateTestData() throws SQLException, BackingStoreException {

        Injector injector = DiService.getInjector();

        var inventoryService = injector.getInstance(InventoryService.class);
        var invoiceService = injector.getInstance(InvoiceService.class);
        var invoiceItemService = injector.getInstance(InvoiceItemService.class);
        var invoicePaymentService = injector.getInstance(InvoicePaymentService.class);
        var contactService = injector.getInstance(ContactService.class);
        var userService = injector.getInstance(UserService.class);
        var invoicePaymentTypeService = injector.getInstance(InvoicePaymentTypeService.class);

        boolean dropTables = true;
        DatabaseService.createTables(dropTables);

//        var user = new User();
//        
//        user.setAdmin(true);
//        user.setUserName("admin");
//        userService.save(user);
//        
        var cardType = new InvoicePaymentType();
        cardType.setName("Card");
        cardType.setDescription("Credit or debit card payment toward an invoice.");
        cardType.setInvoiceCredit(true);
        cardType.setInvoiceDebit(false);
        invoicePaymentTypeService.save(cardType);

        
        var cashType = new InvoicePaymentType();
        cashType.setName("Cash");
        cashType.setDescription("Physical cash payment toward an invoice.");
        cashType.setInvoiceCredit(true);
        cashType.setInvoiceDebit(false);
        invoicePaymentTypeService.save(cashType);
        
        
        var checkType = new InvoicePaymentType();
        checkType.setName("Check");
        checkType.setDescription("Paper check payment toward an invoice.");
        checkType.setInvoiceCredit(true);
        checkType.setInvoiceDebit(false);
        invoicePaymentTypeService.save(checkType);
        
        var eftType = new InvoicePaymentType();
        eftType.setName("ACH/EFT");
        eftType.setDescription("Electronic direct bank draft payment toward an invoice. ");
        eftType.setInvoiceCredit(true);
        eftType.setInvoiceDebit(false);
        invoicePaymentTypeService.save(eftType);
        
        var prepaidType = new InvoicePaymentType();
        prepaidType.setName("Prepaid account");
        prepaidType.setDescription("In-house prepaid account deduction used as payment against the invoice. ");
        prepaidType.setInvoiceCredit(true);
        prepaidType.setInvoiceDebit(false);
        invoicePaymentTypeService.save(prepaidType);
        
        var creditType = new InvoicePaymentType();
        creditType.setName("Credit");
        creditType.setDescription("General customer service credit used as a payment toward the invoice.");
        creditType.setInvoiceCredit(true);
        creditType.setInvoiceDebit(false);
        invoicePaymentTypeService.save(creditType);
        
        var refundType = new InvoicePaymentType();
        refundType.setName("Refund");
        refundType.setDescription("Refund for overpayment of the invoice.");
        refundType.setInvoiceCredit(false);
        refundType.setInvoiceDebit(true);
        invoicePaymentTypeService.save(refundType);
        
        var feeType = new InvoicePaymentType();
        feeType.setName("Fee");
        feeType.setDescription("Fee added to the invoice balance due.");
        feeType.setInvoiceCredit(false);
        feeType.setInvoiceDebit(true);
        invoicePaymentTypeService.save(feeType);
        
        var contact = new Contact();

        contact.setCompany("Data Virtue");
        contact.setFirstName("Sean");
        contact.setLastName("Anderson");
        contact.setAddress1("1711 Sanborn Dr");
        contact.setAddress2("");
        contact.setCity("Cincinnati");
        contact.setState("OH");
        contact.setPostalCode("45215");
        contact.setContact("Sean Anderson");
        contact.setPhone("937-509-8797");
        contact.setFax("");
        contact.setEmail("sean.anderson@datavirtue.com");
        contact.setWebLink("https://www.datavirtue.com");
        contact.setNotes("Nevitium developer");
        contact.setCustomer(true);
        contact.setVendor(true);
        contact.setCountryCode("US");
        contact.setTaxable1(true);
        contact.setTaxable2(false);
        contactService.save(contact);

        var contactAddress = new ContactAddress();
        contactAddress.setAttention("Sean Anderson");
        contactAddress.setCompany("Nucleus Mobile");
        contactAddress.setAddress1("5244 Leninger Haigh Rd");
        contactAddress.setContact(contact);
        contactAddress.setCity("Hillsboro");
        contactAddress.setState("OH");
        contactService.saveAddress(contactAddress);

        var inventory = new Inventory();
        inventory.setDescription("Battlestar Galactica: Miniseries");
        inventory.setCode("025192792823");
        inventory.setQuantity(10.00);
        inventory.setCost(7.69);
        inventory.setPrice(14.99);
        inventory.setTax1(true);
        inventory.setCategory("DVD - SciFi");
        inventoryService.save(inventory);

        var invoice = new Invoice();
        var billTo = Tools.formatAddress(contact);
        invoice.setCustomer(Tools.arrayToString(billTo));
        var items = new ArrayList();
        var item = invoiceItemService.mapInventoryToInvoiceItem(2, invoice, inventory);
        item.setTaxable1(true);
        item.setTaxable1Rate(0.07);
        items.add(item);
        invoice.setInvoiceDate(new Date());
        invoice.setItems(items);
        invoice.setInvoiceNumber(invoiceService.getNewInvoiceNumber("Q"));
        invoice.setQuote(true);
        invoiceService.postInvoice(invoice);

        inventory = new Inventory();
        inventory.setDescription("Star Wars");
        inventory.setCode("045892797824");
        inventory.setQuantity(10.00);
        inventory.setCost(8.69);
        inventory.setPrice(17.99);
        inventory.setTax1(true);
        inventory.setCategory("DVD - SciFi");
        inventoryService.save(inventory);

        inventory = new Inventory();
        inventory.setDescription("Gaming Keyboard");
        inventory.setCode("015692731899");
        inventory.setQuantity(10.00);
        inventory.setCost(58.76);
        inventory.setPrice(199.99);
        inventory.setTax1(true);
        inventory.setCategory("USB - GAMING");
        inventoryService.save(inventory);

        invoice = new Invoice();
        billTo = Tools.formatAddress(contact);
        invoice.setCustomer(Tools.arrayToString(billTo));
        items = new ArrayList();
        item = invoiceItemService.mapInventoryToInvoiceItem(1, invoice, inventory);
        item.setTaxable1(true);
        item.setTaxable1Rate(0.07);
        items.add(item);
        invoice.setInvoiceDate(new Date());
        invoice.setItems(items);
        invoice.setInvoiceNumber(invoiceService.getNewInvoiceNumber("I"));
        invoice.setQuote(false);
        invoiceService.postInvoice(invoice);
        
        var payment = new InvoicePayment();
        payment.setPaymentEffectiveDate(new Date());
        payment.setCredit(100.00);
        payment.setInvoice(invoice);
        payment.setMemo("Automated test payment");
        payment.setPaymentType(cardType);
        invoicePaymentService.save(payment);

    }

}
