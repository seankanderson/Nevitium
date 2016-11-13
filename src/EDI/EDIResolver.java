/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EDI;

import RuntimeManagement.GlobalApplicationDaemon;
import businessmanager.Common.Tools;
import datavirtue.DV;
import datavirtue.DbEngine;
import datavirtue.Settings;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Data Virtue
 */
public class EDIResolver {
    Settings EDIconfig;
    String key;
    GlobalApplicationDaemon application;
    DbEngine db;
    public EDIResolver(GlobalApplicationDaemon application){
        this.application = application;
        EDIconfig = new Settings(application.getWorkingPath()+"EDI/edi.ini");
        db = application.getDb();
        this.populateEmailSettings();
    }
    
    private void populateEmailSettings(){
        
        key = EDIconfig.getProp("PASSWORD");
        key = this.decrypt(key, "ass");//security through obscurity
        emailAddress = EDIconfig.getProp("EMAIL ADDRESS");
        popServer = EDIconfig.getProp("POP SERVER");
        popPort = EDIconfig.getProp("POP PORT");
        popUser = EDIconfig.getProp("POP USER");
        popSSL = DV.parseBool(EDIconfig.getProp("POP SSL"), false);
        popPassword = this.decrypt(EDIconfig.getProp("POP PW"), key);
        sharedKey = this.decrypt(EDIconfig.getProp("SHARED KEY"), key);
        emailCrypto = DV.parseBool(EDIconfig.getProp("EMAIL CRYPTO"), true); //display crypto notification
        emailTries = DV.parseInt(EDIconfig.getProp("EMAIL ATTEMPT"));
        
    }
    private String emailAddress, smtpServer, smtpPort, smtpUser, 
            smtpPassword, popServer, popPort, popUser, popPassword, sharedKey;
    private boolean emailCrypto, smtpSSL, popSSL;
    private int emailTries;
    
    
    public void resolveEmail(){
        
        int emailSuccess = this.getEmails();
        
        /* Need to determine if we resolve before sending or vise-versa */
        
        if (emailSuccess > 0) this.resolveInfiles();
        
    }    
    
    private int getEmails(){
       
        // Create empty properties
        Properties props = new Properties();
        int number_recorded = 0;
        // Get session
        Session session = Session.getDefaultInstance(props, null);

        // Get the store
        Store store=null;
        try {
            store = session.getStore("pop3");
            store.connect(this.popServer, this.popUser, this.popPassword);

        }catch(javax.mail.AuthenticationFailedException e){
            //login failed: check EDI email settings 
            //return false
        }catch(javax.mail.MessagingException e){
            //there was a problem connecting to the server (network - firewall)
            //try again?
            return -1;
        }
            
        // Get folder
        Folder folder;
        try {        
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);//we are going to delete
        }catch(javax.mail.MessagingException e){
            //could not gain access to the inbox (try again?)
            return -1;
        }
        
        // Get directory
        //enclose try block in while(true)?
        try {
            Message message[] = folder.getMessages();
        
            //check for email and get a list of available messages
            //check the list against the email EDIreceive log
            //if already processe, delete email from server and current list cache
            //process each email in the list building an in-file (same as an out-file)
            //perform checksum on data+sharedkey and compare to that in the subject
            //if the checksum is bad, warn the user that the sender may have the wrong key or the data is corrupted in transit
            //delete each from server after verified correct, ecord in edireceive as recorded
            //warn user about the specific email and leav on server?
            //process each in-file against the database           
                        
            for (int i=0, n=message.length; i<n; i++) {
                System.out.println(i + ": " + message[i].getFrom()[0] 
                + "\t" + message[i].getSubject());
                
                //check edireceive, if recorded - delete and continue
                //if not recorded check attempts (make a decision)
                //split checksum and compare with payload, if correct: build in-file, record in edireceive OR otherwise delete
                
                //if the message subject size is too small we have junk which needs purged
                if (message[i].getSubject().length() < 46) {
                    message[i].setFlag(Flag.DELETED, true);
                    
                    continue;    //jump to next message                
                }
                
                ArrayList al = db.searchFast("edireceivelog", 1, message[i].getSubject(), false);
                DefaultTableModel receiveLog;
                Object [] logEntry;
                int logKey = 0;
                
                if (al != null) {
                    receiveLog = (DefaultTableModel) db.createTableModelFast("edireceivelog", al, false);
                    if (receiveLog.getRowCount() > 0){
                        logEntry = DV.getTableRow(receiveLog, 0);  //get first entry
                        logKey = (Integer)logEntry[0]; //store the key
                        //is message recorded? (in-file built?)
                        if (((Boolean)logEntry[2])){
                            //if already recorded delete mesage from inbox
                            message[i].setFlag(Flag.DELETED, true);
                            break;
                        }else{//message not recorded (in-file not built)
                            if (((Integer)logEntry[3]) > 0){ //how many attempts have been made?
                                //what do we do if attempted before?
                                if (((Integer)logEntry[3]) > 2){
                                    //if tried three times? do what? make count limit based on EDI attempt setting
                                }
                            }
                        }   
                    }
                   
                }else {
                    //create new log entry before proceeding
                    logKey = db.saveRecord("edireceivelog", new Object [] {new Integer(0), message[1].getSubject(),
                        false, new Integer(0), new Date().getTime()}, false);
                    
                }
                    //build in-file, verify, and update entry to reflect status (recorded, attempts, time)
                    Object content=null;
                    try {
                        content = message[i].getContent();
                    }catch(Exception e){
                        //retry a couple of times and warn user
                        //disconnected?
                    }
                    String body = "";
                    
                    if (content == null) {
                        //bail out
                    }
                    
                    if (content instanceof String){
                        body = (String)content;
                    }else {
                        //mark and delete - update log
                    }
                    
                    DV.writeFile(application.getWorkingPath()+"EDI/in/email/"+message[i].getSubject(), body, false);
                    String email_md5 = message[i].getSubject().substring(0, 31) ;
                    
                    String inFileContent = DV.readFile(application.getWorkingPath()+"EDI/in/email/"+message[i].getSubject());
                    
                    String inFileMd5 = Tools.buildMD5(inFileContent+sharedKey);
                    
                    if (email_md5.equals(inFileMd5)){
                        //everyting checks out and we can end this madness! shew!
                        db.saveRecord("edireceivelog", new Object [] {new Integer(logKey), message[1].getSubject(),
                        true, new Integer(0), new Date().getTime()}, false);
                        message[i].setFlag(Flag.DELETED, true);
                        number_recorded++;
                    }else{
                        //build a fail message and return to sender?
                        message[i].setFlag(Flag.DELETED, true);
                    }
                    
                    
            }//end message loop
            
        }catch(javax.mail.MessagingException e) {
            //there was a problem encountered while trying to retrieve the messages
            //try again?
            return -1;
        }
        
        // Close connection 
        try {
            folder.close(true); //expunge delete flags
            store.close();
        }catch(javax.mail.MessagingException e){
            //problem deleting the email messages or closing the message store
            //this situation will not cause problems because every message is logged
            //try again?  in order to try again we will need to enclose this mess in a loop
            return -1;
        }
        
        return number_recorded;
    }
    
    private void resolveInfiles(){
        
        //set loop to check each in-file
        //if negative key, perform match check and add next record as replacement
        
        //my connections
        /*  
         check databse for like data, 
         when sending data, if changing: start the first csv line with -1 key and the data before it was changed
         when resolving, check for an exact or near exact match of the before data, if found plug in the change line
         otherwise add the change line as new  (Create a merge tool in My Connections?)
           
         
         */
        
        
    }
    
    
    private String decrypt(String t, String key){
        try {
            String k = EDIPBE.decrypt(key.toCharArray(), t);
            if (k != null) return k;
        }catch(Exception e){
            javax.swing.JOptionPane.showMessageDialog(null, 
            "There was a problem when trying to decrypt EDI settings.");
            return "";
        }
        return "";
    }
    
}
