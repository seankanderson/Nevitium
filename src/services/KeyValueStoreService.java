package services;

import com.google.gson.Gson;
import dao.KeyValueStoreDao;
import java.sql.SQLException;
import models.KeyValueStore;

/**
 * This service concerns itself with extracting a single key-value pair from a table with two relevent columns: key and value. 
 * The key is named after the generic type represented by the generic type parameter T. 
 * Once retrieved the text from the value column is assumed to be JSON and is deserialized to an object of type T.
 * @author SeanAnderson
 * @param <T> the type of the object that is stored as JSON in the KeyValueStore value column. The JSON is deserialized into an instance of this type.
 * 
 */
public class KeyValueStoreService<T> extends BaseService<KeyValueStoreDao, KeyValueStore> {

    private T thing;
    private Class<T> thingType;
    public void setObjectType(Class<T> cl) {
        thingType = cl;
    }
    
    private KeyValueStore keyValue = new KeyValueStore();
    
    private String getKeyName() {
        return thingType.getSimpleName();
    }
       
    public void set(T aThing) throws SQLException {
        thing = aThing;
    }

    public boolean doExist() throws SQLException {
        var results = this.getDao()
                .queryBuilder()
                .where()
                .eq("key", getKeyName()).countOf();

        return results > 0;
    }

    public T getObject() throws SQLException {
        return thing == null
                ? this.getSavedObject(thingType)
                : thing;
    }

    public void save() throws SQLException {
       
        keyValue.setKey(getKeyName());
        keyValue.setValue(new Gson().toJson(thing));
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
    public KeyValueStoreDao getDao() throws SQLException {
        return dao == null ? new KeyValueStoreDao(connection) : dao;
    }

    private T getSavedObject(Class<T> clazz) throws SQLException {
        
        var results = this.getDao()
                .queryForEq("key", getKeyName());
        
        if (results != null && !results.isEmpty()) {
            keyValue = (KeyValueStore)results.get(0);
            var json = keyValue.getValue();
            this.thing = new Gson().fromJson(json, clazz);
            return thing;            
        } else {
            return null;
        }
    }

}
