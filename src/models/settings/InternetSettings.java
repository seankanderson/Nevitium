package models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class InternetSettings {
    private EmailSettings emailSettings;
    boolean showRemoteMessage = true;
}

