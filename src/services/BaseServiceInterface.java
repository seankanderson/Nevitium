/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public interface BaseServiceInterface<T> {
    public T getDao() throws SQLException;
}

