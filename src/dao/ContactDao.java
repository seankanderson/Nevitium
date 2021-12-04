/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.List;
import models.Contact;

/**
 *
 * @author SeanAnderson
 */
public class ContactDao extends BaseDaoImpl<Contact, Object> implements ContactDaoInterface {
    
    public ContactDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Contact.class);
    }
    
    
}
