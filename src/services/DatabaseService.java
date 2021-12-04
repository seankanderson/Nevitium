/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import models.Contact;
import models.ContactAddress;
import models.ContactPhone;
import models.Inventory;
import models.InventoryItemNote;
import models.Invoice;
import models.InvoiceItem;
import models.InvoiceMessages;
import models.User;

/**
 *
 * @author SeanAnderson
 */
public class DatabaseService {
    private static JdbcConnectionSource connectionSource;
    
    public static JdbcConnectionSource getConnection()throws SQLException {
        if (connectionSource == null) {
            // jdbc:h2:tcp://localhost:9400/~/nevitium
            // jdbc:h2:~/nevitium;AUTO_SERVER=TRUE
            connectionSource = new JdbcConnectionSource("jdbc:h2:~/nevitium;AUTO_SERVER=TRUE");
            //Server.createWebServer("-web","-webAllowOthers", "-tcp","-tcpAllowOthers" );
        }
        return connectionSource;
    }
    public static void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(getConnection(), Inventory.class); 
        TableUtils.clearTable(connectionSource, Inventory.class);
        TableUtils.createTableIfNotExists(getConnection(), InventoryItemNote.class); 
        TableUtils.clearTable(connectionSource, InventoryItemNote.class);
        TableUtils.createTableIfNotExists(getConnection(), Contact.class); 
        TableUtils.clearTable(connectionSource, Contact.class);
        TableUtils.createTableIfNotExists(getConnection(), ContactAddress.class); 
        TableUtils.clearTable(connectionSource, ContactAddress.class);
        TableUtils.createTableIfNotExists(getConnection(), ContactPhone.class); 
        TableUtils.clearTable(connectionSource, ContactPhone.class);
        TableUtils.createTableIfNotExists(getConnection(), Invoice.class); 
        TableUtils.clearTable(connectionSource, Invoice.class);
        TableUtils.createTableIfNotExists(getConnection(), InvoiceItem.class); 
        TableUtils.clearTable(connectionSource, InvoiceItem.class);    
        TableUtils.createTableIfNotExists(getConnection(), InvoiceMessages.class); 
        TableUtils.clearTable(connectionSource, InvoiceMessages.class);
        TableUtils.createTableIfNotExists(getConnection(), User.class); 
        TableUtils.clearTable(connectionSource, User.class);    
    }
}
