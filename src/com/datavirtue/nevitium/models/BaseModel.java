package com.datavirtue.nevitium.models;

import com.j256.ormlite.field.DatabaseField;
import java.util.UUID;

/**
 *
 * @author SeanAnderson
 */
public class BaseModel {

    @DatabaseField(generatedId = true)
    private UUID id; 
    
    /**
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }
    

}


