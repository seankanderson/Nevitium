

package com.datavirtue.nevitium.database.orm;

import com.datavirtue.nevitium.models.invoices.InvoicePaymentType;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoicePaymentTypeDao extends BaseDaoImpl<InvoicePaymentType, Object> implements InvoicePaymentTypeDaoInterface{
    public InvoicePaymentTypeDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, InvoicePaymentType.class);
        
    }
}
