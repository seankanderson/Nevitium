package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import models.AppConfig;
import models.Contact;

/**
 *
 * @author SeanAnderson
 */
public class AppSettingsDao extends BaseDaoImpl<AppConfig, Object> implements AppSettingsDaoInterface{
    public AppSettingsDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, AppConfig.class);
    }
}
