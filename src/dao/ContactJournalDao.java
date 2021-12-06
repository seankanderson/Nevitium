package dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import models.ContactJournal;

/**
 *
 * @author SeanAnderson
 */
public class ContactJournalDao extends BaseDaoImpl<ContactJournal, Object> implements ContactJournalDaoInterface {
    
    public ContactJournalDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ContactJournal.class);
    }
    
    
}
