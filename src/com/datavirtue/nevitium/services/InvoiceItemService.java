
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoiceItemDao;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceItemService extends BaseService<InvoiceItemDao, InvoiceItem>{

    @Override
    public InvoiceItemDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoiceItem.class);
    }
    
    public List<InvoiceItem> getItemsForInvoice(Invoice invoice) throws SQLException {
        return this.getDao().queryForEq("invoiceId", invoice.getId()); 
    }

}
