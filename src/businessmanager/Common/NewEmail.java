

package businessmanager.Common;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import java.util.Properties;
import gui.PleaseWait;


/**
 *
 * @author Data Virtue 2010
 */
public class NewEmail {


    public NewEmail(){

    }

    public void appendText(String t){
        body.append(t);
    }
    public void setText(String t){
        body = null;
        body = new StringBuilder();
        body.append(t);
    }

    public void setAttachment(String f){
        attachment = f;
    }
    public void setRecipent(String t){
        toAddress = t;
    }
    public void setFrom(String t){
        from = t;
    }
    public void setSubject(String t){
        subject = t;
    }
    public void setServer(String t){
        smtpServer = t;
    }
    public void setPort(String p){
        smtpPort = p;
    }
    public void setUsername(String t){
        userName = t;
    }
    public void setPassword(String t){
        password = t;
    }
    public void setAuth(boolean b){
        auth = b;
    }

   public void setSSL(boolean s){
       SSL = s;
   }

    public int sendEmail(){

        /* TODO: Check email address, if empty: ask for one*/
        /* if not empty, check against REG EXP and ask for a new one. */
        if (SSL){
            return sendSSL();

        }
        
        PleaseWait wait = new PleaseWait(null, false, "Sending Email to "+toAddress);
        wait.setVisible(true);
        wait.paint(wait.getGraphics());

            if (debug){

                System.out.println("Server: "+smtpServer);
                System.out.println("User Name: "+userName);
                System.out.println("Password: "+password);
                System.out.println("From: "+from);
                System.out.println("To: "+toAddress);
                System.out.println("Subject:  "+subject);
                System.out.println("Body: "+body.toString());
                System.out.println("Attachment: "+attachment);
            }
        
          Properties props = new Properties();
          Session mailSession;

          
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.host", smtpServer);
            props.setProperty("mail.user", userName);
            props.setProperty("mail.password", password.trim());
            props.setProperty("mail.smtp.port", smtpPort);
            props.setProperty("mail.smtp.auth", Boolean.toString(auth));
            props.setProperty("mail.smtp.use8bit", "true");
       
            if (auth){
              Authenticator authenticator = new SMTPAuthenticator(userName, password);
			mailSession = Session.getInstance(props, authenticator);

          }else {
            mailSession = Session.getDefaultInstance(props, null);
          }
                  
          
          mailSession.setDebug(debug);

          try {
          Transport transport = mailSession.getTransport();          

          MimeMessage message = new MimeMessage(mailSession);          

          try{

              message.setFrom(new InternetAddress(from));
              
          /* Email Subject */
              message.setSubject(subject);
          if (debug) System.out.println("subject set");
          message.setText(body.toString());

          /* Attach file */
          if (attachment.length() > 1){
            MimeBodyPart file = new MimeBodyPart();
            MimeBodyPart text = new MimeBodyPart();
            Multipart mp = new MimeMultipart();
            
            
            FileDataSource fds = new FileDataSource(attachment);
            file.setDataHandler(new DataHandler(fds));
            file.setFileName(fds.getName());

            text.setText(body.toString());

            mp.addBodyPart(text);
            mp.addBodyPart(file);
            message.setContent(mp);

          }

          message.saveChanges();
          
          try {
          /* Set the address for the recipient */
              message.addRecipient(Message.RecipientType.TO,
              new InternetAddress(toAddress));
          }catch(AddressException e){
              wait.dispose();
              javax.swing.JOptionPane.showMessageDialog(null, "Receipient Address Problem!  Email was not sent to "+toAddress);
              return 7;
          }
          try{
          if (debug) System.out.println("Attempting to connect...");
          transport.connect(smtpServer, userName, password);
          }catch(MessagingException e){
            wait.dispose();
            javax.swing.JOptionPane.showMessageDialog(null, "Messaging Error: Verify server & user credentials in Settings.");
            return 4;
          }
          if (debug) System.out.println("Connected, sending Msg...");
          transport.sendMessage(message, message.getAllRecipients());
          if (debug) System.out.println("Message Sent!");
          transport.close();

          }catch(MessagingException e){
            wait.dispose();
            javax.swing.JOptionPane.showMessageDialog(null, "Messaging Error: Possible network/internet problems.");
            return 2;
          }

          }catch(NoSuchProviderException e){
            wait.dispose();
            javax.swing.JOptionPane.showMessageDialog(null, "Provider Exception");
            return 1;
          }
       
          wait.dispose();
        return 0;
    }


    public int sendSSL(){
        Session mailSession;
        PleaseWait wait = new PleaseWait(null, false, "Sending Email to "+toAddress+"  (SSL)");
        wait.setVisible(true);
        wait.paint(wait.getGraphics());

        Properties props = new Properties();
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.socketFactory.port", smtpPort);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", smtpPort);

		mailSession = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName,password);
				}
			});

          mailSession.setDebug(debug);

          try {
          //Transport transport = mailSession.getTransport();

          MimeMessage message = new MimeMessage(mailSession);

          try{

              message.setFrom(new InternetAddress(from));

          /* Email Subject */
              message.setSubject(subject);
          if (debug) System.out.println("subject set");
          message.setText(body.toString());

          /* Attach file */
          if (attachment.length() > 1){
            MimeBodyPart file = new MimeBodyPart();
            MimeBodyPart text = new MimeBodyPart();
            Multipart mp = new MimeMultipart();

            FileDataSource fds = new FileDataSource(attachment);
            file.setDataHandler(new DataHandler(fds));
            file.setFileName(fds.getName());

            text.setText(body.toString());

            mp.addBodyPart(text);
            mp.addBodyPart(file);
            message.setContent(mp);

          }

          message.saveChanges();

          /* Set the address for the recipient */
              message.addRecipient(Message.RecipientType.TO,
              new InternetAddress(toAddress));
          }catch(AddressException e){
              wait.dispose();
              javax.swing.JOptionPane.showMessageDialog(null, "Receipient Address Problem!  Email was not sent.");
              return 7;
          }
         /* try{
          if (debug) System.out.println("Attempting to connect...");
          Transport.connect(smtpServer, userName, password);
          }catch(MessagingException e){
            wait.dispose();
            javax.swing.JOptionPane.showMessageDialog(null, "Messaging Error: Verify server & user credentials in Settings.");
            return 4;
          }
          if (debug) System.out.println("Connected, sending Msg...");
          transport.sendMessage(message, message.getAllRecipients());
          if (debug) System.out.println("Message Sent!");
          transport.close();*/
          Transport.send(message);
          }catch(MessagingException e){
            wait.dispose();
            javax.swing.JOptionPane.showMessageDialog(null, "Messaging Error: Possible network/internet problems.");
            return 2;
          }

          wait.dispose();
        return 0;

    }

    

private boolean debug = true;
private String from = "";
private String toAddress = "";
private String subject = "";
private StringBuilder body = new StringBuilder();
private String attachment = "";
private String smtpServer = "";
private String smtpPort = "25";
private String userName = "";
private String password="";
private boolean auth = true;
private boolean SSL = false;

}

class SMTPAuthenticator extends Authenticator {

		String username;
		String password;

		public SMTPAuthenticator(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication(
				String username, String password) {
			return new PasswordAuthentication(username, password);
		}
	}