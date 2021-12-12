package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "users")
public class User extends BaseModel {

    @DatabaseField
    private String userName;
    @DatabaseField
    private String key;
    @DatabaseField
    private boolean master;
    @DatabaseField
    private long inventory;
    @DatabaseField
    private long contacts;
    @DatabaseField
    private long invoices;
    @DatabaseField
    private long invoiceManager;
    @DatabaseField
    private long reports;
    @DatabaseField
    private long checks;
    @DatabaseField
    private long exports;
    @DatabaseField
    private long settings;
    
    
}
