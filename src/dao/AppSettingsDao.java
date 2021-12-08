package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import models.AppSettings;
import models.Contact;

/**
 *
 * @author SeanAnderson
 */
public class AppSettingsDao extends BaseDaoImpl<AppSettings, Object> implements AppSettingsDaoInterface{
    public AppSettingsDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, AppSettings.class);
    }
}
