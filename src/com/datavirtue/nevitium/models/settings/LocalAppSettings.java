package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class LocalAppSettings {
    private String dataPath;
    private String connectionString;
    
    private String lastNevitiumUser;
    
    private String theme;
    
    private ScreenSettings screenSettings;
   
         
}
