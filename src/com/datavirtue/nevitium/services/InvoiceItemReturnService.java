


package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoiceItemReturnDao;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceItemReturn;
import com.datavirtue.nevitium.models.settings.AppSettings;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceItemReturnService extends BaseService<InvoiceItemReturnDao, InvoiceItemReturn>{

    @Inject
    private AppSettingsService appSettingsService;
    private AppSettings appSettings;
        
    @Override
    public InvoiceItemReturnDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoiceItemReturn.class);
    }
    
    public List<InvoiceItemReturn> getReturnsForInvoice(Invoice invoice) throws SQLException {
        var query =  this.getDao().queryBuilder().where().eq("invoiceId", invoice.getId()); 
        return query.query();
    }

}
