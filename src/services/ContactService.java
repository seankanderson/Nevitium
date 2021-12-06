
package services;

import dao.ContactDao;
import java.sql.SQLException;
import java.util.List;
import models.Contact;
import static models.ModelConstants.CUSTOMER;
import static models.ModelConstants.VENDOR;

/**
 *
 * @author SeanAnderson
 */
public class ContactService extends BaseService<ContactDao, Contact> {
    
    public ContactService() {
        
    }
    
    public List<Contact> getAllCustomers() throws SQLException {
        return this.getDao().queryForEq(CUSTOMER, true);     
    }
    
    public List<Contact> getAllVendors() throws SQLException {        
        return this.getDao().queryForEq(VENDOR, true); 
    }
    
    public List<Contact> getUnpaidCustomers() {
        return null;        
    }
       
    @Override
    public ContactDao getDao() throws SQLException {
        return dao == null ? new ContactDao(connection) : dao;
    }
    
}
