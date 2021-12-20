package com.datavirtue.nevitium.database.orm;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.datavirtue.nevitium.models.inventory.Inventory;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */

public class InventoryDao extends BaseDaoImpl<Inventory, Object> implements InventoryDaoInterface{
    
    public InventoryDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Inventory.class);
    }
    
}
