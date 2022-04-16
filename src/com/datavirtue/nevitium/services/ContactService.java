
package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.ContactDao;
import java.sql.SQLException;
import java.util.List;
import com.datavirtue.nevitium.models.contacts.Contact;
import com.datavirtue.nevitium.models.contacts.ContactAddress;
import com.datavirtue.nevitium.models.contacts.ContactAddressInterface;
import com.google.inject.Inject;
import com.j256.ormlite.dao.DaoManager;
import java.text.MessageFormat;
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
    
    public String createGoogleMapLink(ContactAddressInterface address) {
        //https://www.google.com/maps/place/1711+Sanborn+Dr,+Cincinnati,+OH+45215
                
        return MessageFormat.format("https://www.google.com/maps/place/{0},+{1},+{2}+{3}", 
                address.getAddress1().replace(" ", "+"),  
                address.getCity().replace(" ", "+"),
                address.getState().replace(" ", "+"),
                address.getPostalCode().replace(" ", "+")
                );
    } 
    
    public int saveAddress(ContactAddress address) throws SQLException {
        //var addressService = new ContactAddressService();
        return addressService.save(address);
    }
    
    public String[] formatContactAddress(ContactAddressInterface contact) {
        String nl = System.getProperty("line.separator");
        String[] address = new String[5];

        String code = contact.getCountryCode() == null ? "US" : contact.getCountryCode();  

        address[0] = contact.getCompanyName() != null ? contact.getCompanyName() + nl : "";
        address[1] = contact.getContactName() != null ? contact.getContactName() + nl : "";
        address[2] = contact.getAddress1() != null ? contact.getAddress1() + nl : "";
        address[3] = contact.getAddress2() != null ? contact.getAddress2() + nl : "";

        if (code.equalsIgnoreCase("US")
                || code.equalsIgnoreCase("CA")
                || code.equalsIgnoreCase("AU")) {
            address[4] = contact.getCity()
                    + "  "
                    + contact.getState()
                    + "  "
                    + contact.getPostalCode()
                    + nl;
        }

        if (code.equalsIgnoreCase("GB")
                || code.equalsIgnoreCase("ZA")
                || code.equalsIgnoreCase("IN")
                || code.equalsIgnoreCase("PH")) {

            address[4] = contact.getCity()
                    + nl
                    + contact.getPostalCode()
                    + nl;

        }
       
        return address;
    }
    
    
    
    @Override
    public ContactDao getDao() throws SQLException {
        return DaoManager.createDao(connection, Contact.class);
    }
    
}
