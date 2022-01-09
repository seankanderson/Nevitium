
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoicePaymentDao;
import com.datavirtue.nevitium.models.invoices.InvoicePayment;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoicePaymentService extends BaseService<InvoicePaymentDao, InvoicePayment> {

    @Override
    public InvoicePaymentDao getDao() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
