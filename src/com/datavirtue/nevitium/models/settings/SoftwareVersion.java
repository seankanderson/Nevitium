package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class SoftwareVersion {
   
    public SoftwareVersion(String major, String minor, String sub, String release) {
        this.major = major;
        this.minor = minor;
        this.subversion = sub;
        this.relaseDate = release;
    }
    
    private String major;
    private String minor;
    private String subversion;
    
    private String relaseDate;
    
    public String getVersionString() {
        return major + "." + minor + "." + subversion;
    }
}
