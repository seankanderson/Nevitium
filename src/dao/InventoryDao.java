/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.util.List;
import models.Inventory;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */

public class InventoryDao extends BaseDaoImpl<Inventory, Long> implements InventoryDaoInterface{
    
    public InventoryDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Inventory.class);
    }
    @Override
    public List<Inventory> findByUpc(String upc) throws SQLException{
        return super.queryForEq("upc", upc);
    }
}
