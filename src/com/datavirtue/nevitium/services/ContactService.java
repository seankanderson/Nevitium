
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.ContactDao;
import java.sql.SQLException;
import java.util.List;
import com.datavirtue.nevitium.models.contacts.Contact;
import java.util.UUID;

/**
 *
 * @author SeanAnderson
 */
public class ContactService extends BaseService<ContactDao, Contact> {
    
    public ContactService() {
        
    }
    
    public Contact getContactById(UUID id) throws SQLException {
        var results = this.getDao().queryForEq("id", id.toString());   
        if (results == null) {
            return null;
        }
        return results.get(0);
    }
    
    public List<Contact> getAllCustomers() throws SQLException {
        return this.getDao().queryForEq("isCustomer", true);     
    }
    
    public List<Contact> getAllVendors() throws SQLException {        
        return this.getDao().queryForEq("isVendor", true); 
    }
    
    public List<Contact> getUnpaidCustomers() {
        return null;        
    }
       
    @Override
    public ContactDao getDao() throws SQLException {
        return dao == null ? new ContactDao(connection) : dao;
    }
    
}
