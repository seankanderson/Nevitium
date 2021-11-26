/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import models.Inventory;

/**
 *
 * @author SeanAnderson
 */
public class DatabaseService {
    private static JdbcConnectionSource connectionSource;
    public static JdbcConnectionSource getConnection()throws SQLException {
        if (connectionSource == null) {
            connectionSource = new JdbcConnectionSource("jdbc:h2:mem:nevitium");
        }
        return connectionSource;
    }
    public static void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(getConnection(), Inventory.class); 
    }
}
