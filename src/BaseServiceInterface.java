
import com.j256.ormlite.jdbc.JdbcConnectionSource;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

/**
 *
 * @author SeanAnderson
 */
public interface BaseServiceInterface<T> {
    public T getDao(JdbcConnectionSource connection);
}
