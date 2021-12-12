/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import java.util.List;
import models.BaseModel;
import models.Contact;

/**
 *
 * @author SeanAnderson
 * @param <T1> database entity-typed dao implementation
 * @param <T2> database model entity type
 */
public abstract class BaseService<T1 extends BaseDaoImpl<T2, Object>, T2 extends BaseModel> implements BaseServiceInterface<T1> {
    
    @Inject
    @Named("DatabaseConnection")
    protected JdbcConnectionSource connection;
    protected T1 dao;
    
    protected JdbcConnectionSource getDatabaseConnection() {
        JdbcConnectionSource conn;
        try {
             conn = DatabaseService.getConnection();
             return conn;
        }catch(SQLException e){
            System.out.println(e.getSQLState());
        }
        return null;
    }    
    
    public List<T2> getAll() throws SQLException {
        return this.getDao().queryForAll();
    }

    public void save(T2 item) throws SQLException {
        if (item.getId() == null) {
            this.getDao().create(item);
        } else {
            this.getDao().update(item);
        }
    }
    
    public int delete(T2 item) throws SQLException {
        if (item.getId() != null) {
            return this.getDao().delete(item);
        }else {
            return 0;
        }
    }  
    
    public List<T2> searchField(String fieldName, String forValue) throws SQLException {
        return this.getDao().queryForEq(fieldName, forValue);
    }
}
