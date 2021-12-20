

package com.datavirtue.nevitium.database.orm;

import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceItemDao extends BaseDaoImpl<InvoiceItem, Object> implements InvoiceItemDaoInterface{
    public InvoiceItemDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, InvoiceItem.class);
        
    }
}
