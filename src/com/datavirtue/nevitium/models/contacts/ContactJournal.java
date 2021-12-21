package com.datavirtue.nevitium.models.contacts;

import com.datavirtue.nevitium.database.orm.ContactJournalDao;
import com.datavirtue.nevitium.models.BaseModel;
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
@DatabaseTable(tableName = "contact_journals", daoClass = ContactJournalDao.class)
public class ContactJournal extends BaseModel {    
    
    @DatabaseField(canBeNull = false)
    private Date date = new Date();
    
    @DatabaseField(columnDefinition = "CLOB(10K)")
    private String content;
    
    @DatabaseField(foreign=true,foreignAutoRefresh=true, canBeNull = false)
    private Contact contact;
        
}
