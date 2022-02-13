

package com.datavirtue.nevitium.database.orm;

import com.datavirtue.nevitium.models.invoices.InvoiceItemReturn;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceItemReturnDao extends BaseDaoImpl<InvoiceItemReturn, Object> implements InvoiceItemReturnDaoInterface{
    public InvoiceItemReturnDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, InvoiceItemReturn.class);
        
    }
}
