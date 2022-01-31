
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoicePaymentDao;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class InvoicePaymentService extends BaseService<InvoicePaymentDao, InvoicePayment> {

    @Override
    public InvoicePaymentDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoicePayment.class);
    }
    
    public List<InvoicePayment> getAllPaymentsForInvoice(Invoice invoice) throws SQLException {
        return this.getDao().queryForEq("invoice_id", invoice.getId());
    }

}
