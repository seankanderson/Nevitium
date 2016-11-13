/*
 * RemoteMessage.java
 *
 * Created on 05 December 2008, 13:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package businessmanager;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPMessageCollector;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FTPConnectMode;

import datavirtue.*;

/**
 *
 * @author Data Virtue
 */
public class RemoteMessage {
    
    /** Creates a new instance of RemoteMessage */
    public RemoteMessage() {
        
          host = "www.datavirtue.com";
          user = "updater";
          password = "updateme";
          file = "nevstat.txt";
          dir = "";
          
          FTPClient ftp = null;

            ftp = new FTPClient();
            try {
                ftp.setRemoteHost(host);
            }catch (Exception e) { message = "Bad message host reported. "; }
            
            FTPMessageCollector listener = new FTPMessageCollector();
            ftp.setMessageListener(listener);
            
            try {
                ftp.connect();
            }catch (Exception e) { message = "Message Connection Failed. "; }
                        
             try {
                ftp.login(user, password);
            }catch (Exception e) { message = "Error logging in message account. ";  }
            
            
            try {
            
            ftp.setConnectMode(FTPConnectMode.PASV);
            ftp.setType(FTPTransferType.ASCII);

            ftp.get(file, dir + file);

            message = DV.readFile(file);

            ftp.quit();
            }catch(Exception e){
                
              message = "Error retrieving message. ";  
                
            }
        
    }
    
    public String getMessage(){
       
        return message;
        
    }
    
    String message;
    String host;
    String user;
    String password;
    String file;
    String dir;
    int error = 0;
    
}
