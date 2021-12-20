package com.datavirtue.nevitium.services;

import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public interface BaseServiceInterface<T> {
    T getDao() throws SQLException;
}

