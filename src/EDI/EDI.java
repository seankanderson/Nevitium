/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package EDI;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Data Virtue
 */
public class EDI {
    
    public static void sendEmail(String address, String server, String port, String user, String pw, boolean SSL, String [] data ){
    
        //data[0] = from
        //data[1] = to
        //data[2] = subject: checksum of body
        //data[n] = body
        
    }
    
    public static void getEmails(String server, String port, String user, String pw, boolean SSL, String INpath){
        
        //get email list
        //cycle through list and build each format into an Object to be stored
        // 
        //loop through and build a formatted String [] passing to buildEDIFile(String [], path)
        //check for problems
        //POP delete after successful download
        
    }
    
    public static void buildEDIFile(String [] data, String path){
        
        //line 1: UUID
        //line 2: TIME
        //line n: <table name>
        //line n: <CSV data>
        //...
        //String filename = EDI.generateChecksum(content);
        //name and write file with path+checksum_name.edi
        
        
                
        //not to be performed in this method --V
        //when writing to a network share, write file, read file content and compare to remote content?, if bad then delete and rebuild, try three times
        
    }
    
    public static String encryptData(String data, String key, String crypto){
        return "";
    }
    
    public static String decryptData(String data, String key, String crypto){
        return "";
    }

    /**Uses MD5 for speed */
    public static String generateChecksum(String data){
        return "";
    }
    
    public static String getSHA1(String s){
        byte[] bytesOfMessage;
        try {
             bytesOfMessage= s.getBytes("UTF-8");
        }catch (java.io.UnsupportedEncodingException e){
            return s;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
            
        }catch(java.security.NoSuchAlgorithmException e){
            return s;
        }
        byte[] thedigest = md.digest(bytesOfMessage);

        return new String(thedigest);
    }
    
}
