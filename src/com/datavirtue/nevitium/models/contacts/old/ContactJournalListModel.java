package com.datavirtue.nevitium.models.contacts.old;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import com.datavirtue.nevitium.models.contacts.ContactJournal;

/**
 *
 * @author SeanAnderson
 */
public class ContactJournalListModel extends AbstractListModel {
    
    List<ContactJournal> journals = new ArrayList();
    
    public ContactJournalListModel(List<ContactJournal> entries) {
        this.setJournals(entries);
    }   
    public ContactJournalListModel() {
        
    }
    public final void setJournals(List<ContactJournal> entries) {
        this.journals = entries != null ? entries : new ArrayList();
    }   
            
    public ContactJournal getJournalAt(int index) {
            return journals.get(index);
    }

    @Override
    public int getSize() {
        return journals.size();
    }

    @Override
    public Object getElementAt(int index) {
        return journals.get(index).getDate().toString();
    }
    
   
}
