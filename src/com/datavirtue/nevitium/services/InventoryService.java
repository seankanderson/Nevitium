package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.ContactAddressDao;
import com.datavirtue.nevitium.database.orm.InventoryDao;
import com.datavirtue.nevitium.models.contacts.ContactJournal;
import datavirtue.DV;
import datavirtue.Settings;
import java.util.List;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.inventory.Inventory;
import com.j256.ormlite.dao.DaoManager;

/**
 *
 * @author SeanAnderson
 */
public class InventoryService extends BaseService<InventoryDao, Inventory> {

    public InventoryService() {

    }

    public List<Inventory> getAllInventoryByUpc(String upc) throws SQLException {
        return this.getDao().queryForEq("upc", upc);
    }

    public List<Inventory> getAllInventoryByCode(String code) throws SQLException {
        return this.getDao().queryForEq("code", code);
    }

    public List<Inventory> getAllInventoryByCategory(String category) throws SQLException {
        var result = this.getDao().queryBuilder().where().like("category", "%" + category + "%");
        return result.query();
    }

    public List<Inventory> getAllInventoryBySize(String size) throws SQLException {
        var result = this.getDao().queryBuilder().where().like("size", "%" + size + "%");
        return result.query();
    }

    public List<Inventory> getAllInventoryByWeight(String weight) throws SQLException {
        var result = this.getDao().queryBuilder().where().like("weight", "%" + weight + "%");
        return result.query();
    }

    public String[] getInventoryCategories() throws SQLException {
        var results = this.getDao().queryBuilder().distinct().selectColumns("category").query();
        return results.stream().map(p -> p.getCategory()).toArray(size -> new String[results.size()]);
    }

    public List<Inventory> getAllInventoryByDecription(String desc) throws SQLException {
        return this.getDao().queryForEq("description", desc);
    }

    public void deleteInventory(Inventory inventory) throws SQLException {
        this.getDao().delete(inventory);
//        TransactionManager.callInTransaction(connection, new Callable<Void>() {
//            public Void call() throws Exception {
//                // delete both objects but make sure that if either one fails, the transaction is rolled back
//                // and both objects are "restored" to the database
//                getDao().delete(inventory);
//                //barDao.delete(bar);
//                return null;
//            }
//        });
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

    @Override
    public InventoryDao getDao() throws SQLException {
        return DaoManager.createDao(connection, Inventory.class);
    }

}
