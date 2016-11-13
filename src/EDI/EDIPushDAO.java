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
import datavirtue.TableSorter;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Data Virtue
 */
public class EDIPushDAO {
   
   /* Instance variables */ 
   private GlobalApplicationDaemon application;
   private DbEngine db;
   private DefaultTableModel emailTargets;
   private TableSorter folderTargets;
   private TableSorter ftpTargets;
   private Settings EDIconfig;
   private String key;
   private EDIResolver resolver;
   private boolean ready = false;
    public EDIPushDAO(GlobalApplicationDaemon application){
        
        this.application = application;
        db = application.getDb();
        EDIconfig = new Settings(application.getWorkingPath()+"EDI/edi.ini");
        ready = true;
        String ediReady = EDIconfig.getProp("EDI CONFIG");
        if (ediReady == null || !ediReady.equals("true")){
            javax.swing.JOptionPane.showMessageDialog(null, 
                        "The EDI settings have not been configured. You may experience problems.");
            ready = false;
        }
        
        if (ready){
            key = EDIconfig.getProp("PASSWORD");
            key = this.decrypt(key, "ass");//security through obscurity
            populateEmailSettings();
            populateFolderSettings();
            populateFtpSettings();
            this.getEDIEmailTargets();
        } 
    }
    
    private void populateEmailSettings(){
        emailAddress = EDIconfig.getProp("EMAIL ADDRESS");
        smtpServer = EDIconfig.getProp("SMTP SERVER");
        smtpPort = EDIconfig.getProp("SMTP PORT");
        smtpUser = EDIconfig.getProp("SMTP USER");
        smtpPassword = this.decrypt(EDIconfig.getProp("SMTP PW"), key);
        smtpSSL = DV.parseBool(EDIconfig.getProp("SMTP SSL"), false);
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
    
    private void populateFolderSettings(){
        
        sourceFolder = EDIconfig.getProp("SOURCE FOLDER");
        stationName = EDIconfig.getProp("STATION NAME");
        folderCrypto = DV.parseBool(EDIconfig.getProp("FOLDER CRYPTO"), true);
        folderActive = DV.parseBool(EDIconfig.getProp("FOLDER ACTIVE"), false);
        
    }
    private String sourceFolder, stationName ;
    private boolean folderCrypto, folderActive;
    
    private void populateFtpSettings(){
        ftpServer = EDIconfig.getProp("FTP SERVER");
        ftpPort = EDIconfig.getProp("FTP PORT");
        ftpUser = EDIconfig.getProp("FTP ACCT");
        ftpPassword = this.decrypt(EDIconfig.getProp("FTP PW"), key);
        ftpPath = EDIconfig.getProp("FTP PATH");
        ftpCrypto = DV.parseBool(EDIconfig.getProp("FTP CRYPTO"), true);
        ftpAttempt = DV.parseInt(EDIconfig.getProp("FTP ATTEMPT"));
        
    }
    private String ftpServer, ftpPort, ftpUser, ftpPassword, ftpPath;
    private boolean ftpCrypto;
    private int ftpAttempt;
    
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
    
    /* Email Data Access */
    public TableModel getEDIEmailTargets(){
        /* Get email targets */
        ArrayList al = db.searchFast("edifolder", 5, "true", false); //search email boolean
        if (al != null && al.size() > 0){
            emailTargets = (DefaultTableModel)db.createTableModelFast("edifolder", al, false);
            return emailTargets;
        }else {
            return new DefaultTableModel();
        }                
     
    }
    public TableModel updateEmailTarget(Object [] emailTarget){
        db.saveRecord("edifolder", emailTarget, false);
        return getEDIEmailTargets();        
    }
    public TableModel removeEmailTarget(int rec){
        db.removeRecord("edifolder", rec);
        return getEDIEmailTargets();
    }        
    
    
    public TableModel getEDIFolderTargets(){
        
        return new DefaultTableModel();
        
    }
    
    public TableModel getEDIFTPTargets(){
        
        return new DefaultTableModel();
    }
    
    private StringBuilder stream = new StringBuilder(); 
    private boolean anyEmailTargets = true;
    private boolean anyFolderTargets = false;
    private boolean anyFtpTargets = false;
    
    public String startStream(){
        if (!ready) return "NOT READY";
        stream=new StringBuilder(Long.toString(new Date().getTime())+"\r\n");
        return stream.toString();
    }
    public String addData(String table, Object [] data){
        if (!ready) return "NOT READY";
        /* Scan all targets and set a true if any condition */
        
        /* Check table against include list for each transfer method */
        
        stream.append(table);
        stream.append("\r\n");
        for (int i = 0; i < data.length; i++){
            stream.append('"');
            if (DV.isString(data[i])){
               stream.append((String)data[i]);
           }if (DV.isInteger(data[i])){
               stream.append(Integer.toString((Integer)data[i]));
            }if (DV.isFloat(data[i])){
               stream.append(Float.toString((Float)data[i]));
           }if (DV.isLong(data[i])){
               stream.append(Long.toString((Long)data[i]));
           }if (DV.isBoolean(data[i])){
               stream.append(Boolean.toString((Boolean)data[i]));
           }
            stream.append('"');
            if (i < data.length-1) stream.append(',');
        }
        stream.append("\r\n");
        return "DATA ADDED";
    }
    public void endStream(){
        if (!ready) return;
        buildOutfiles(getStream());
        
    }
    /* For Debug */
    public String getStream(){ 
        return stream.toString();
    }
        
    
    private String buildOutfiles(String data){
        /*build standard transfer text files and place in /EDI/out/ */
        /* Encrypt after building MD5, so if MD5 fails ask about encryption setting. */
        
        String dateCode = Long.toString(new Date().getTime());
                
        /* Generate checksum on data */
        String filename = Tools.buildMD5(data+this.sharedKey); //would it help to append the shared key! to the data and have the client do the same?
        
        /* name file with checksum.time? */
        filename = filename + '.' + dateCode;
        
        /* write data to file in target folder if any */
        DV.writeFile(application.getWorkingPath()+"EDI/out/email/"+filename, data, false);
        
        return "FILE WRITTEN: "+ filename;
    }
    
   
    
    public String sendEmail(){
        
        if (!ready) return "NOT READY";
        /* Auto-sync is not implemented */
        String statMessage = "";
        int errorCount = 0;
        Object [] target;
        boolean encryptData;
        String toAddress;
        String body="";
        String subject = "";
        String ediPath = application.getWorkingPath()+"EDI/out/email/";
        File f = new File(ediPath);
        String [] files = f.list();
        
        if (files == null || files.length < 1) return "EMAIL: NO OUTFILES";
        /* Should I keep a record of processed files and check each item a new one is processed? */
        /* <BEGIN> OUTFILE LOOP */ 
        for (int file = 0; file < files.length; file++){
            
            if (!new File(ediPath+files[file]).isFile()) continue;
            System.out.println("FILE NAME: "+files[file]);
                                    
            /* <BEGIN> EMAIL TARGET LOOP */
            for (int row = 0; row < emailTargets.getRowCount(); row++){
            
                target =  DV.getRow(emailTargets, row);
            
                if (!(((Boolean)target[7]))) continue; //if inactive, skip
            
                encryptData = (Boolean)target[8];
                toAddress = (String)target[1];
                if (this.emailAlreadySentToRecipient(files[file], toAddress)) continue;
                subject = files[file];
                body = DV.readFile(ediPath + files[file]);
                
                EDIEmail email = new EDIEmail();
                email.setFrom(this.emailAddress);
                email.setServer(this.smtpServer);
                email.setPort(this.smtpPort);
                email.setUsername(this.smtpUser);
                email.setPassword(this.smtpPassword);
                email.setRecipent(toAddress);
                email.setSubject(subject);
                email.setSSL(this.smtpSSL);//what about SSL error numbers
                email.setAuth(true);
                email.setText(body);
                
                int err = 0;
                int x = 0;
                while (x == 0){ //error handling loop for send procedure
                     err = email.sendEmail();
                     if (err == 7) {
                         int a = javax.swing.JOptionPane.showConfirmDialog(null, toAddress+" is not a valid address.","Retry?",  JOptionPane.YES_NO_OPTION);
                         if (a == JOptionPane.YES_OPTION){
                             continue;
                         }
                         logEDI("email",files[file], toAddress, false, new Date().getTime());//log email as NOT SENT
                         x = 1;
                     }
                     if (err == 4) {
                         int a = javax.swing.JOptionPane.showConfirmDialog(null, 
                                 "Please check the SMTP server name, user name, or password. Do you want to retry?","Retry?",  JOptionPane.YES_NO_OPTION);
                         if (a == JOptionPane.YES_OPTION){
                             continue;
                         }
                         logEDI("email",files[file], toAddress, false, new Date().getTime());//log email as NOT SENT
                         x = 1;
                     }
                     if (err == 2) {
                         int a = javax.swing.JOptionPane.showConfirmDialog(null, 
                                 "Internet/network connection problem, do you want to retry?","Firewall?",  JOptionPane.YES_NO_OPTION);
                         if (a == JOptionPane.YES_OPTION){
                             continue;
                         }
                         logEDI("email",files[file], toAddress, false, new Date().getTime());//log email as NOT SENT
                         x = 1;
                     }
                     if (err == 0){ //should Nevitium check the inbox to verify it was sent?
                         //delete out file
                         new File(files[file]).delete();
                         //log as sent
                         logEDI("email",files[file], toAddress, true, new Date().getTime());//log email as SENT
                         x=1; //leave loop
                         
                     }
                }
                email = null; //help with cleanup
            }/* <END> EMAIL TARGET LOOP */
        
        }/* <END> OUTFILE LOOP */
        
        
        return "SEND EMAIL";
    }
    
    private void logEDI(String type, String filename, String to, boolean sent, long time){
        Object [] log;
        if (type.equals("email")){
            
            ArrayList al = db.searchFast("ediemaillog", 1, filename, false);
            if (al != null){
                
                for (int i = 0; i < al.size(); i++){
                    log = db.getRecord("ediemaillog", (Long)al.get(i));
                    if (((String)log[2]).equals(to)){
                        log [2] = sent;
                        log [3] = time;
                        db.saveRecord("ediemaillog", log, false);
                    }                    
                }
            }else{
                log = new Object [] {0,filename, to, sent, time};
                db.saveRecord("ediemaillog", log, false);
            }
            
        }
        
    }
    
    private boolean emailAlreadySentToRecipient(String filename, String to){
        Object [] log;
        ArrayList al = db.searchFast("ediemaillog", 1, filename, false);
        if (al != null){
            al.trimToSize();
            for (int i = 0; i < al.size(); i++){
                log = db.getRecord("ediemaillog", (Long)al.get(i));
                if ( ((String)log[1]).equalsIgnoreCase(filename) && ((String)log[2]).equalsIgnoreCase(to)){
                     return (Boolean)log[3];
                }
            }
           
        }
        return false;
    }
    
}

