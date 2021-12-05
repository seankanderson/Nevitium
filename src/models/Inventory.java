/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

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
@DatabaseTable(tableName = "inventory")
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
    
}
