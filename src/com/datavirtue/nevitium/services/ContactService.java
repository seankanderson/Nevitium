
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.ContactDao;
import java.sql.SQLException;
import java.util.List;
import com.datavirtue.nevitium.models.contacts.Contact;
import com.datavirtue.nevitium.models.contacts.ContactAddress;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import java.util.UUID;

/**
 *
 * @author SeanAnderson
 */
public class ContactService extends BaseService<ContactDao, Contact> {
    
    @Inject
    private ContactAddressService addressService;
    
    public ContactService() {
        
    }
    
    public Contact getContactById(UUID id) throws SQLException {
        var results = this.getDao().queryForEq("id", id);   
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
       
    public boolean newContactCandidateExists(String companyName, String email, String phoneNumber) throws SQLException {
        var result = this.getDao().queryBuilder().where().eq("company", companyName).or().eq("phone", phoneNumber).or().eq("email", email);
        var exists = result.query();
        return exists != null;
    }    
    
    public List<ContactAddress> getContactAddresses(Contact contact) throws SQLException {
        //var injector = DiService.getInjector();
        //var addressService = injector.getInstance(ContactAddressService.class);
        return addressService.getAddressesForContactId(contact.getId());
    }
    
    public int saveAddress(ContactAddress address) throws SQLException {
        //var addressService = new ContactAddressService();
        return addressService.save(address);
    }
    
    
    @Override
    public ContactDao getDao() throws SQLException {
        return DaoManager.createDao(connection, Contact.class);
    }
    
}
