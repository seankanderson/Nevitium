package com.datavirtue.nevitium.models.inventory;

import com.datavirtue.nevitium.models.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "inventory_item_notes")
public class InventoryItemNote extends BaseModel{
    
    @DatabaseField
    private UUID inventoryItemId;
    @DatabaseField
    private Date date = new Date();
    @DatabaseField
    private String note;
    
}
