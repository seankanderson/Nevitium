/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;

/**
 *
 * @author SeanAnderson
 */
@DatabaseTable(tableName = "contact_phones")
public class ContactPhone extends BaseModel {
    
    @DatabaseField(foreign=true,foreignAutoRefresh=true, canBeNull = false)
    private Contact contact;
    
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
