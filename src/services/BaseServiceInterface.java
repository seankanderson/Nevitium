package services;

import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public interface BaseServiceInterface<T> {
    public T getDao() throws SQLException;
}

