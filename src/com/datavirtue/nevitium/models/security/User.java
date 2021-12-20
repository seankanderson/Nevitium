package com.datavirtue.nevitium.models.security;

import com.datavirtue.nevitium.models.BaseModel;
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

    @DatabaseField(canBeNull = false, unique = true)
    private String userName;
    @DatabaseField
    private String email;    
    @DatabaseField
    private String phone;
    @DatabaseField
    private String password="";
    @DatabaseField
    private boolean admin = false;
    @DatabaseField
    private int inventory=300;
    @DatabaseField
    private int contacts=300;
    @DatabaseField
    private int invoices=300;
    @DatabaseField
    private int invoiceManager=300;
    @DatabaseField
    private int reports=100;
    @DatabaseField
    private int checks=100;
    @DatabaseField
    private int exports=100;
    @DatabaseField
    private int settings=100;
    
    
}
