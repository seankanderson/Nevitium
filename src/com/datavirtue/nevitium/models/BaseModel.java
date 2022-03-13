package com.datavirtue.nevitium.models;

import com.j256.ormlite.field.DatabaseField;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class BaseModel {

    @DatabaseField(generatedId = true)
    private UUID id; 
   
   
}


