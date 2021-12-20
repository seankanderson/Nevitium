package com.datavirtue.nevitium.database.orm;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.contacts.ContactAddress;

/**
 *
 * @author SeanAnderson
 */
public class ContactAddressDao extends BaseDaoImpl<ContactAddress, Object> implements ContactAddressDaoInterface {
    public ContactAddressDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ContactAddress.class);
    }
    
}
