
package services;

import dao.ContactDao;
import java.sql.SQLException;
import java.util.List;
import models.Contact;

/**
 *
 * @author SeanAnderson
 */
public class ContactService extends BaseService<ContactDao, Contact> {
    
    public ContactService() {
        
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
