/*
 * AccessDialog.java
 *
 * Created on July 27, 2007, 10:40 PM
 */

package businessmanager;
import RuntimeManagement.KeyCard;

import datavirtue.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author  Data Virtue
 */
public class AccessDialog extends javax.swing.JDialog {
    
    /** Creates new form AccessDialog */
    public AccessDialog(java.awt.Frame parent, boolean modal, DbEngine db, String path) {
        super(parent, modal);
        initComponents();
    
        workingPath = path;
        pathField.setText(workingPath);
        
        this.db = db;
        checkUsers();
        
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        
        this.setLocation(dim.width, dim.height);
        
        passField.requestFocus();
        
        if (secure) this.setVisible(true);
                
    }
    private String workingPath="";
    
    private KeyCard keycard;
    private DbEngine db;
    private boolean secure = false;
    
    private void checkUsers() {
        
        ArrayList al;
        Object [] rec;
        
        al = db.search("users", 1, "master", false);
        
        if (al != null){
           
            rec = db.getRecord("users", (Integer)al.get(0));
            
            String pk = (String)rec[2];
            
            if (pk.equals("")){
                
                secure = false;

                allowed = true;
                return;
            }
            else{
                
                secure = true;
                return;
            }
            
                        
        }else {
            
            /* Setup first master user */
            
            int a = JOptionPane.showConfirmDialog(null,
                    "The security database does not have a 'Master' user." + nl +
                    "Would you like to create a new Master user?"+nl+nl+
                    "It is recommended that you create a Master user."+nl+
                    "Security will not be enabled though until you set a" +nl+
                    "password for the Master user."+nl+
                    "File--> Settings--> Security --> [Manage Users]",
                    "Security",  JOptionPane.YES_NO_OPTION);
            if (a == 0){

                Object [] user = new Object [12];
                user[0] = new Integer(0);
                user[1] = new String("Master");
                user[2] = new String("");//password
                user[3] = new Boolean(true);//master
                user[4] = new Long(300);
                user[5] = new Long(300);
                user[6] = new Long(500);
                user[7] = new Long(300);
                user[8] = new Long(100);
                user[9] = new Long(100);
                user[10] = new Long(100);
                user[11] = new Long(100);
                db.saveRecord("users", user, false);

            }else {
                /* Exit the system?? */
                //System.exit(-6);
                secure = false;
                return;
            }


            
        }
        
    }
    
    private void getUser(String user, char[] password) {
        
        ArrayList al; 
        String decrypted=null;
        
        al = db.search("users", 1, user, false);
        if (al != null){
            
            /* Get user record and access setttings. */
            Object [] rec = db.getRecord("users", (Integer)al.get(0));
            
            String cipher = (String)rec[2];
            boolean master = (Boolean)rec[3];

            try {
                decrypted = PBE.decrypt(password, cipher);
                //System.out.println(decrypted);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (decrypted != null){
                
                if (decrypted.equals(new String(password))){
                    
                    secure = true;
                    allowed = true;
                    keycard = new KeyCard(rec);
                    this.setVisible(false);
                    
                }else {
                    
                    secure = true;
                    allowed = false;
                    keycard = null;
                    this.setVisible(false);
                    
                }
                
            }else {
                
                allowed = false;
                secure = true;
                keycard = null;
                this.setVisible(false);
                
            } 
            
        }else {
            
            javax.swing.JOptionPane.showMessageDialog(null, "User: "+user+" was not found.");
            secure = true;
            this.setVisible(false);
        }
        
    }
    
    public boolean isSecure() {
        
        return secure;
        
    }
    
    private boolean allowed = false;
    
    public boolean isAllowed() {
        
        return allowed;
        
    }
        
    public boolean isCanceled(){
        return canceled;
    }
    public KeyCard getKeyCard() {
        
        return keycard;
                
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        userField = new javax.swing.JTextField();
        passField = new javax.swing.JPasswordField();
        tryButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        pathField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nevitium");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/onebit_25.gif"))); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("User Name");

        jLabel2.setText("Password");

        userField.setText("master");
        userField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                userFieldFocusGained(evt);
            }
        });

        passField.setNextFocusableComponent(tryButton);
        passField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passFieldKeyPressed(evt);
            }
        });

        tryButton.setText("Try");
        tryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tryButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Nevitium Access Panel");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                                .add(tryButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 103, Short.MAX_VALUE)
                                .add(cancelButton))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, passField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .add(userField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .add(11, 11, 11)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(userField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(passField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(tryButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pathField.setEditable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pathField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pathField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_userFieldFocusGained
        
        userField.selectAll();
        
        
    }//GEN-LAST:event_userFieldFocusGained

    private void passFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passFieldKeyPressed
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
           
            
               go();
           
        }
        
    }//GEN-LAST:event_passFieldKeyPressed
private boolean canceled = false;
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        
        secure = true;
        allowed = false;
        canceled = true;
        this.setVisible(false);
        
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void tryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tryButtonActionPerformed
        
        go();
        
        
    }//GEN-LAST:event_tryButtonActionPerformed
    
   private void go () {
       
       getUser(userField.getText().trim(), passField.getPassword());
       
   }
    private String nl = System.getProperty("line.separator");
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField passField;
    private javax.swing.JTextField pathField;
    private javax.swing.JButton tryButton;
    private javax.swing.JTextField userField;
    // End of variables declaration//GEN-END:variables
    
}
