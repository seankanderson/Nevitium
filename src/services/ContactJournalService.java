package services;

import dao.ContactJournalDao;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import models.ContactJournal;

/**
 *
 * @author SeanAnderson
 */
public class ContactJournalService extends BaseService<ContactJournalDao, ContactJournal> {
    
    public ContactJournalService() {
        
    }    
    
    public List<ContactJournal> getJournalsForContact(UUID contactId) throws SQLException  {
        return this.getDao().queryForEq("contact_id", contactId);
    }
    
    @Override
    public ContactJournalDao getDao() throws SQLException {
        return dao == null ? new ContactJournalDao(connection) : dao;
    }
    
}
