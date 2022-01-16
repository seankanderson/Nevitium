
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoicePaymentDao;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoicePaymentService extends BaseService<InvoicePaymentDao, InvoicePayment> {

    @Override
    public InvoicePaymentDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoicePayment.class);
    }

}
