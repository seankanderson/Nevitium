package com.datavirtue.nevitium.database.orm;

import com.datavirtue.nevitium.models.invoices.old.InvoicePayment;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoicePaymentDao extends BaseDaoImpl<InvoicePayment, Object> implements InvoicePaymentDaoInterface{
    public InvoicePaymentDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, InvoicePayment.class);
        
    }
}
