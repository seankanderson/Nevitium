package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoicePaymentDao;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.datavirtue.nevitium.models.invoices.InvoicePaymentType;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class InvoicePaymentService extends BaseService<InvoicePaymentDao, InvoicePayment> {

    @Inject
    private InvoicePaymentTypeService typeService;

    public InvoicePaymentService() {

    }

    @Override
    public InvoicePaymentDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoicePayment.class);
    }

    public List<InvoicePayment> getAllPaymentsForInvoice(Invoice invoice) throws SQLException {
        return this.getDao().queryForEq("invoice_id", invoice.getId());
    }

    public List<InvoicePayment> getAllDebitsForInvoice(Invoice invoice) throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("invoice_id", invoice.getId()).and().gt("debit", 0);
        return result.query();
    }
    
    public List<InvoicePayment> getAllCreditsForInvoice(Invoice invoice) throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("invoice_id", invoice.getId()).and().gt("credit", 0);
        return result.query();
    }

    public List<InvoicePaymentType> getPaymentTypes() throws SQLException {
        return typeService.getAll();
    }

}
