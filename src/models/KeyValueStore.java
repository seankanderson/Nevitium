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
public class KeyValueStore extends BaseModel {
    
    @DatabaseField(canBeNull = false, unique = true)
    private String key;
    @DatabaseField(columnDefinition = "CLOB(10K)")
    private String value;
}
