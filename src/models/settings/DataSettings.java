package models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class DataSettings {
   private String dataPath;
   private String primaryBackupPath;
   private String secondaryBackupPath;
}
