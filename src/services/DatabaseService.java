package services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import models.KeyValueStore;
import models.Contact;
import models.ContactAddress;
import models.ContactJournal;
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
    public static void createTables(boolean dropTablesFirst) throws SQLException {
        
        getConnection();
        
        if (dropTablesFirst) {
            TableUtils.dropTable(connectionSource, Inventory.class, true);
            TableUtils.dropTable(connectionSource, InventoryItemNote.class, true);
            TableUtils.dropTable(connectionSource, Contact.class, true);
            TableUtils.dropTable(connectionSource, ContactAddress.class, true);
            TableUtils.dropTable(connectionSource, Invoice.class, true);
            TableUtils.dropTable(connectionSource, InvoiceItem.class, true);
            TableUtils.dropTable(connectionSource, InvoiceMessages.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            //TableUtils.dropTable(connectionSource, AppConfig.class, true);
        }
        
        TableUtils.createTableIfNotExists(getConnection(), Inventory.class); 
        TableUtils.clearTable(connectionSource, Inventory.class);
        TableUtils.createTableIfNotExists(getConnection(), InventoryItemNote.class); 
        TableUtils.clearTable(connectionSource, InventoryItemNote.class);
        TableUtils.createTableIfNotExists(getConnection(), Contact.class); 
        TableUtils.clearTable(connectionSource, Contact.class);
        TableUtils.createTableIfNotExists(getConnection(), ContactJournal.class); 
        TableUtils.clearTable(connectionSource, ContactJournal.class);
        TableUtils.createTableIfNotExists(getConnection(), ContactAddress.class); 
        TableUtils.clearTable(connectionSource, ContactAddress.class);
        TableUtils.createTableIfNotExists(getConnection(), Invoice.class); 
        TableUtils.clearTable(connectionSource, Invoice.class);
        TableUtils.createTableIfNotExists(getConnection(), InvoiceItem.class); 
        TableUtils.clearTable(connectionSource, InvoiceItem.class);    
        TableUtils.createTableIfNotExists(getConnection(), InvoiceMessages.class); 
        TableUtils.clearTable(connectionSource, InvoiceMessages.class);
        TableUtils.createTableIfNotExists(getConnection(), User.class); 
        TableUtils.clearTable(connectionSource, User.class);   
        TableUtils.createTableIfNotExists(getConnection(), KeyValueStore.class); 
        //TableUtils.clearTable(connectionSource, AppConfig.class);    
    }
}
