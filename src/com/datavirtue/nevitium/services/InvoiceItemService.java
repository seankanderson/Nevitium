
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.InvoiceItemDao;
import com.datavirtue.nevitium.models.inventory.Inventory;
import com.datavirtue.nevitium.models.invoices.Invoice;
import com.datavirtue.nevitium.models.invoices.InvoiceItem;
import com.datavirtue.nevitium.models.settings.AppSettings;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceItemService extends BaseService<InvoiceItemDao, InvoiceItem>{

    @Inject
    private AppSettingsService appSettingsService;
    private AppSettings appSettings;
        
    @Override
    public InvoiceItemDao getDao() throws SQLException {
        return DaoManager.createDao(connection, InvoiceItem.class);
    }
    
    public List<InvoiceItem> getItemsForInvoice(Invoice invoice) throws SQLException {
        return this.getDao().queryForEq("invoiceId", invoice.getId()); 
    }
    
    public List<InvoiceItem> getReturnsForInvoice(Invoice invoice) throws SQLException {
        var query =  this.getDao().queryBuilder().where().eq("invoiceId", invoice.getId()).and().eq("code", "RETURN"); 
        return query.query();
    }
    
    public List<InvoiceItem> getSaleItemsForInvoice(Invoice invoice) throws SQLException {
        var query = this.getDao().queryBuilder().where().eq("invoiceId", invoice.getId()).and().ne("code", "RETURN"); 
        return query.query();
    }
    
    public InvoiceItem mapInventoryToInvoiceItem(double qty, Invoice invoice, Inventory inventory) throws SQLException  {

        appSettingsService.setObjectType(AppSettings.class);
        appSettings = appSettingsService.getObject();
        
        var item = new InvoiceItem();
        item.setSourceInventoryId(inventory.getId());
        item.setCode(inventory.getCode());
        item.setCost(inventory.getCost());
        item.setDate(new Date());
        item.setDescription(inventory.getDescription());
        item.setInvoice(invoice);
        item.setQuantity(qty);
        item.setTaxable1(inventory.isTax1());
        item.setTaxable2(inventory.isTax2());
        item.setTaxable1Rate(appSettings.getInvoice().getTax1Rate());
        item.setTaxable2Rate(appSettings.getInvoice().getTax2Rate());
        item.setUnitPrice(inventory.getPrice());
        return item;
    }

}
