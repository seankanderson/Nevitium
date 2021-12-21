package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoiceDao;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.j256.ormlite.dao.DaoManager;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceService extends BaseService<InvoiceDao, Invoice> {

    @Override
    public InvoiceDao getDao() throws SQLException {
        return DaoManager.createDao(connection, Invoice.class);
    }
    
}
