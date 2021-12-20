package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter
@Setter
public class DataSettings {

    private String primaryBackupPath;
    private String secondaryBackupPath;
    private boolean disableBackupFeatures = true;
}
