package com.datavirtue.nevitium.database.orm;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.invoices.Invoice;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceDao extends BaseDaoImpl<Invoice, Object> implements InvoiceDaoInterface {
    public InvoiceDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Invoice.class);
    }
}
