package services;

import com.google.gson.Gson;
import dao.AppSettingsDao;
import java.sql.SQLException;
import models.AppSettings;
import models.settings.InventorySettings;
import models.settings.InvoiceSettings;

/**
 *
 * @author SeanAnderson
 */
public class AppSettingsService extends BaseService<AppSettingsDao, AppSettings> {
    
    public InvoiceSettings getInvoiceSettings() throws SQLException{
        return this.getSettings(InvoiceSettings.class);
    }
    
    public InventorySettings getInvnetorySettings() throws SQLException{
        return this.getSettings(InventorySettings.class);
    }
    
    @Override
    public AppSettingsDao getDao() throws SQLException {
        return dao == null ? new AppSettingsDao(connection) : dao;
    }
    
    private <T> T getSettings(Class type) throws SQLException {
        var results = this.getDao().queryForEq("key", type.getSimpleName());
         if (results != null){ 
            var json = results.get(0).getValue();
            var gson = new Gson();
            return (T)gson.fromJson(json, type);
        }else {
            return null;
        }        
    }
    
}
