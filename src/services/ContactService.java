
package services;

import dao.ContactDao;
import java.sql.SQLException;
import models.Contact;

/**
 *
 * @author SeanAnderson
 */
public class ContactService extends BaseService<ContactDao, Contact> {
    
    public ContactService() {
        
    }
    
    

    @Override
    public ContactDao getDao() throws SQLException {
        return dao == null ? new ContactDao(connection) : dao;
    }
    
}
