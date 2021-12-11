package services;

import com.google.gson.Gson;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.UpdateBuilder;
import dao.AppSettingsDao;
import java.sql.SQLException;
import models.AppConfig;
import models.settings.AppSettings;

/**
 *
 * @author SeanAnderson
 */
public class AppSettingsService extends BaseService<AppSettingsDao, AppConfig> {

    private AppSettings settings;

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
                ? this.getSaved(AppSettings.class)
                : settings;

//        settings.setBackups(this.getSaved(DataSettings.class));
//        settings.setInternet(this.getSaved(InternetSettings.class));
//        settings.setInventory(this.getSaved(InventorySettings.class));
//        settings.setInvoice(this.getSaved(InvoiceSettings.class));
//        settings.setSecurity(this.getSaved(SecuritySettings.class));
//        settings.setSoftwareVersion(this.getSaved(SoftwareVersion.class));
    }

    public void saveSettings() throws SQLException {
        var keyValuePair = new AppConfig();
        keyValuePair.setKey(AppSettings.class.getSimpleName());
        var gson = new Gson();
        keyValuePair.setValue(gson.toJson(settings));

        if (doSettingsExist()) {

            UpdateBuilder<AppConfig, Object> ub = this.getDao().updateBuilder();
            ub.updateColumnValue("value", keyValuePair.getValue());
            ub.where().eq("key", keyValuePair.getKey());            
            int updated = ub.update();
            if (updated != 1) {
                throw new SQLException(keyValuePair.getKey() + " was not updated " + updated);
            }
        } else {
            this.getDao().create(keyValuePair);
        }
    }

    @Override
    public AppSettingsDao getDao() throws SQLException {
        return dao == null ? new AppSettingsDao(connection) : dao;
    }

    private <T> T getSaved(Class type) throws SQLException {
        var results = this.getDao().queryForEq("key", type.getSimpleName());
        if (results != null && !results.isEmpty()) {
            var json = results.get(0).getValue();
            var gson = new Gson();
            return (T) gson.fromJson(json, type);
        } else {
            return null;
        }
    }

}
