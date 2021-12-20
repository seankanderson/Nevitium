package com.datavirtue.nevitium.database.orm;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.KeyValueStore;

/**
 *
 * @author SeanAnderson
 */
public class CheckSettingsDao extends BaseDaoImpl<KeyValueStore, Object> implements CheckSettingsDaoInterface {
    public CheckSettingsDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, KeyValueStore.class);
    }
}