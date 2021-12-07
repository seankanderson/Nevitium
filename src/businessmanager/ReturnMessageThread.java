/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package businessmanager;

import datavirtue.DVNET;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Data Virtue
 */
public class ReturnMessageThread extends Thread {

    public ReturnMessageThread(String url, JTextField tf, JLabel inetStat){
        URL = url;
        textField = tf;
        internetStatus = inetStat;
    }

    public void run(){
        if (debug) System.out.println("Running Message Thread");
        String message="";
        message = DVNET.HTTPGetFile(URL, "Please visit datavirtue.com for updates and support.", false);
        textField.setText(message);
        
            if (textField.getText().contains("No Internet")){
                internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Disconnect.png")));
            }else{
                internetStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Connect.png")));
            }
        if (debug) System.out.println("Ending Message Thread");
    }

   private JTextField textField;
   private String URL="";
   private JLabel internetStatus;
   boolean debug = false;
}
