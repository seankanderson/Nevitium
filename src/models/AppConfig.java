package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "app_config")
public class AppConfig extends BaseModel {
    
    
    private UUID id;
    @DatabaseField(canBeNull = false, unique = true)
    private String key;
    @DatabaseField(columnDefinition = "CLOB(10K)")
    private String value;
}
