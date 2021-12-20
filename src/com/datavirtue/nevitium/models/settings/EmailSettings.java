package com.datavirtue.nevitium.models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter
@Setter
public class EmailSettings {

    private String serverAddress;
    private String serverPort;
    private String returnAddress;
    private String serverUsername;
    private String serverPassword;
    private boolean useSSL;
}
