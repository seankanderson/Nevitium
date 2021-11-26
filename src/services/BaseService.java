/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import dao.InventoryDao;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class BaseService {
    public JdbcConnectionSource getDatabaseConnection() {
        JdbcConnectionSource conn;
        try {
             conn = DatabaseService.getConnection();
             return conn;
        }catch(SQLException e){
            System.out.println(e.getSQLState());
        }
        return null;
    }
}
