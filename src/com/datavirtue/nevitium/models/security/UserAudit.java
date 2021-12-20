package com.datavirtue.nevitium.models.security;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an atomic action (user posting invoice or applying a payment to an invoice) 
 * if an invoice is posted: log the invoice
 * if a payment is applied: log the payment object (which should also contain the invoice and at least the reference to it)
 * prefrerence should be given to storing entire object graphs (showing the data at the point in time of the action) 
 *  
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "audit")
public class UserAudit {
    @DatabaseField(canBeNull = false)
    private Date dateTime;
    @DatabaseField(canBeNull = false)
    private String username;
    @DatabaseField
    private String actionType;
    @DatabaseField
    private String actionDescription;
    @DatabaseField
    private String className;
    @DatabaseField(columnDefinition = "CLOB(10K)")   
    private String businessObjectJson;
}
