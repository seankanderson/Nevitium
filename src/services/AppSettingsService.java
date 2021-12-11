package services;

import com.google.gson.Gson;
import com.j256.ormlite.stmt.UpdateBuilder;
import dao.AppSettingsDao;
import java.sql.SQLException;
import models.KeyValueStore;
import models.BaseModel;
import models.settings.AppSettings;

/**
 *
 * @author SeanAnderson
 */
public class AppSettingsService extends BaseService<AppSettingsDao, KeyValueStore> {

    private AppSettings settings;
    private KeyValueStore keyValue = new KeyValueStore();
    public void setAppSettings(AppSettings appSettings) throws SQLException {
        settings = appSettings;
        this.saveSettings();
    }

    public boolean doSettingsExist() throws SQLException {
        var results = this.getDao()
                .queryBuilder()
                .where()
                .eq("key", AppSettings.class.getSimpleName()).countOf();

        return results > 0;
    }

    public AppSettings getSettings() throws SQLException {

        return settings == null
                ? this.getSaved()
                : settings;

//        settings.setBackups(this.getSaved(DataSettings.class));
//        settings.setInternet(this.getSaved(InternetSettings.class));
//        settings.setInventory(this.getSaved(InventorySettings.class));
//        settings.setInvoice(this.getSaved(InvoiceSettings.class));
//        settings.setSecurity(this.getSaved(SecuritySettings.class));
//        settings.setSoftwareVersion(this.getSaved(SoftwareVersion.class));
    }

    public void saveSettings() throws SQLException {
        
        var gson = new Gson();
        
        keyValue.setKey(AppSettings.class.getSimpleName());
        keyValue.setValue(gson.toJson(settings));
        if (keyValue.getId() != null) {
            int updated = this.getDao().update(keyValue);
            if (updated != 1) {
                throw new SQLException(keyValue.getKey() + " was not updated " + updated);
            }
        } else {
            this.getDao().create(keyValue);
        }
    }

    @Override
    public AppSettingsDao getDao() throws SQLException {
        return dao == null ? new AppSettingsDao(connection) : dao;
    }

    private AppSettings getSaved() throws SQLException {
        
        var results = this.getDao()
                .queryForEq("key", AppSettings.class.getSimpleName());
        
        if (results != null && !results.isEmpty()) {
            keyValue = results.get(0);
            var json = keyValue.getValue();
            this.settings = new Gson().fromJson(json, AppSettings.class);
            return settings;            
        } else {
            return null;
        }
    }

}
