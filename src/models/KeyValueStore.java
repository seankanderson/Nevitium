package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
@DatabaseTable(tableName = "key_value_store")
public class KeyValueStore extends BaseModel {
    
    @DatabaseField(canBeNull = false, unique = true)
    private String key;
    @DatabaseField(columnDefinition = "CLOB(10K)")
    private String value;
}
