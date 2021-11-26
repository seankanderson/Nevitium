/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import dao.InventoryDao;
import datavirtue.DV;
import datavirtue.Settings;
import java.util.List;
import java.sql.SQLException;
import models.Inventory;

/**
 *
 * @author SeanAnderson
 */
public class InventoryService extends BaseService{
    
    @Inject
    @Named("DatabaseConnection") 
    private JdbcConnectionSource connection;
    private InventoryDao inventoryDao;
    public InventoryService() {
        
    }
    
    public List<Inventory> getAllInventory() throws SQLException {        
        return this.getInventoryDao().queryForAll();
    }
    
    public void save(Inventory inventory) throws SQLException {
        if (inventory.getId() == null){
            this.getInventoryDao().create(inventory);
            System.out.println("INSERTED: " + inventory.getId());
        }else {
            this.getInventoryDao().update(inventory);
        }     
    }
    
    public List<Inventory> getAllInventoryByUpc(String upc) throws SQLException {
        return this.getInventoryDao().queryForEq("upc", upc);
    }
    
    
    
    private InventoryDao getInventoryDao() throws SQLException {          
        return inventoryDao == null ? new InventoryDao(connection) : inventoryDao;
    }
    
    
    public double calculateMarkup(double cost, Settings props) {
        float points;
        if (DV.validFloatString(props.getProp("MARKUP"))) {
            points = Float.parseFloat(props.getProp("MARKUP"));
        } else {
            return 0.00;
        }
        return cost * points;
    }
    
    
}
