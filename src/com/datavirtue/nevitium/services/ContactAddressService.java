package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.ContactAddressDao;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.contacts.ContactAddress;
import com.j256.ormlite.dao.DaoManager;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author SeanAnderson
 */
public class ContactAddressService extends BaseService<ContactAddressDao, ContactAddress>  {

    public ContactAddressService() {
        
    }
    
    @Override
    public ContactAddressDao getDao() throws SQLException {
        return DaoManager.createDao(connection, ContactAddress.class);
    }
    
    
    public List<ContactAddress> getAddressesForContactId(UUID contactId) throws SQLException {
        return this.getDao().queryForEq("contact_id", contactId);
    }
       
    
}
