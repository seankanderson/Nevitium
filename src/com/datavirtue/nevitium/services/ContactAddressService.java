package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.ContactAddressDao;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.contacts.ContactAddress;

/**
 *
 * @author SeanAnderson
 */
public class ContactAddressService extends BaseService<ContactAddressDao, ContactAddress>  {

    @Override
    public ContactAddressDao getDao() throws SQLException {
        return dao == null ? new ContactAddressDao(connection) : dao;
    }
    
    
    
    
    
}
