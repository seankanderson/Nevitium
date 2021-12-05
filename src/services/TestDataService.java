/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import businessmanager.ControlCenter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import di.GuiceBindingModule;
import models.Contact;
import models.Inventory;
import java.sql.SQLException;

/**
 *
 * @author SeanAnderson
 */
public class TestDataService {
    
    public static void populateTestData() throws SQLException {
        
        Injector injector = Guice.createInjector(new GuiceBindingModule());
                
        InventoryService inventoryService = injector.getInstance(InventoryService.class);
        ContactService contactService = injector.getInstance(ContactService.class);
        
        boolean dropTables = true;
        DatabaseService.createTables(dropTables);
        
        var contact = new Contact();
        
        contact.setCompany("Data Virtue");
        contact.setFirstName("Sean");
        contact.setLastName("Anderson");
        contact.setAddress1("1711 Sanborn Dr");
        contact.setAddress2("");
        contact.setCity("Cincinnati");
        contact.setState("OH");
        contact.setPostalCode("45215");
        contact.setContact("Sean Anderson");
        contact.setPhone("937-509-8797");
        contact.setFax("");
        contact.setEmail("sean.anderson@datavirtue.com");
        contact.setWebLink("https://www.datavirtue.com");
        contact.setNotes("Nevitium developer");
        contact.setCustomer(true);
        contact.setVendor(true);
        contact.setCountryCode("US");
        contact.setTaxable1(true);
        contact.setTaxable2(false);
        contactService.save(contact);        
        
        var inventory = new Inventory();
        inventory.setDescription("Battlestar Galactica: Miniseries");
        inventory.setCode("025192792823");
        inventory.setQuantity(10.00);
        inventory.setCost(7.69);
        inventory.setPrice(14.99);
        inventory.setCategory("DVD - SciFi");          
        inventoryService.save(inventory);

        inventory = new Inventory();
        inventory.setDescription("Star Wars");
        inventory.setCode("045892797824");
        inventory.setQuantity(10.00);
        inventory.setCost(8.69);
        inventory.setPrice(17.99);
        inventory.setCategory("DVD - SciFi");          
        inventoryService.save(inventory);
        
        inventory = new Inventory();
        inventory.setDescription("Gaming Keyboard");
        inventory.setCode("015692731899");
        inventory.setQuantity(10.00);
        inventory.setCost(58.76);
        inventory.setPrice(199.99);
        inventory.setCategory("USB - GAMING");          
        inventoryService.save(inventory);
        
        
        
    }
    
}
