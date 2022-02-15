package com.datavirtue.nevitium.models.inventory;

import com.datavirtue.nevitium.database.orm.InventoryDao;
import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "inventory", daoClass = InventoryDao.class)
public class Inventory extends BaseModel{    
    @DatabaseField
    private String upc;
    @DatabaseField
    private String code;
    @DatabaseField(canBeNull = false)
    private String description;
    @DatabaseField
    private String size;
    @DatabaseField
    private String weight;
    @DatabaseField
    private Double quantity;
    @DatabaseField
    private Double cost;
    @DatabaseField
    private Double price;    
    @DatabaseField
    private String category;    
    @DatabaseField
    private boolean tax1;
    @DatabaseField
    private boolean tax2;
    @DatabaseField
    private boolean available;
    @DatabaseField
    private Date lastSale = new Date();
    @DatabaseField
    private Date lastReceived = new Date();
    @DatabaseField
    private int reorderCutoff;
    @DatabaseField
    private boolean partialSaleAllowed;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] image;
}
