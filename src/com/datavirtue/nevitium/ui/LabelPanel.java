/*
 * LabelPanel.java
 *
 * Created on January 25, 2007, 4:25 PM
 */

package com.datavirtue.nevitium.ui;
import java.io.*;
import javax.swing.DefaultListModel;


/**
 *
 * @author  Data Virtue
 */
public class LabelPanel extends javax.swing.JPanel {
    
    /** Creates new form LabelPanel */
    public LabelPanel() {
        initComponents();
    }
    
    public void setFile(String filename){
        
        this.filename = filename;
        
        int c = 0;
        
        DefaultListModel lm = new DefaultListModel();
        
        
        try {
            
            File file = new File (filename);
            FileInputStream in = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream( in, 4096 /* buffsize */ );  
            BufferedReader b = new BufferedReader(new InputStreamReader(bis));
              
            
            String tmp="";
            
            do {
                
                tmp = b.readLine();
                
                if (tmp == null) break;
                
                c++;
                
                 
            }while (tmp != null);
                        
            bis.close();
            
            defs = new String [c] [6];  //6 cols 
            
            in = new FileInputStream(file);
            bis = new BufferedInputStream( in, 4096 /* buffsize */ );  
            b = new BufferedReader(new InputStreamReader(bis));
            
            tmp = "";
            String[] result;
            int r= 0;
            
            
            do {
                
                tmp = b.readLine();
                if (tmp == null) break;
                
                result = tmp.split(",");
                
                for (int i = 0; i < result.length; i++){
                    
                    defs [r] [i] = result[i];
                    
                    //System.out.println(result[i]);
                    
                }
                
                r++;
                
                
                lm.addElement(result[0]);
                
                
            }while (true);
            
            
        }catch (Exception e) {
         
            System.out.println("error in LabelPanel");
            return;
            
        }
        
        jList1.setModel(lm);
        jList1.validate();
      
        
    }
    
    protected void deleteDef() {
        
        if (current_index > -1){
               
            String t = (String)jList1.getModel().getElementAt(current_index);
               
           if (t.startsWith("*")) return;
            
            String [] [] def_tmp = new String [defs.length - 1] [6];
           
           for (int r = 0; r < def_tmp.length; r++){
                
                for (int c = 0; c < 6; c++){
                   
                    if (r != current_index){
                       
                        if (r > current_index){
                            
                            def_tmp [r] [c] = defs [r-1] [c];
                            
                        }else {
                        def_tmp [r] [c] = defs [r] [c];
                        }
                    }
                                                            
                }
                
            }
            
           defs = def_tmp;
           
        }
        
        writeFile();
        
    }
    
    protected void writeFile() {
        
        try {
                
            File data = new File (filename);
            
            PrintWriter out = new PrintWriter(
                    new BufferedWriter( 
                     new FileWriter (data, false ) ) ); //overwrite the whole file
            
            //write text
            
            StringBuilder sb = new StringBuilder();
            
            for (int r = 0; r < defs.length; r++){
                
                for (int c = 0; c < 6; c++){
                    
                    sb.append(defs[r][c]);
                    
                    //System.out.println("write File: "+ defs[r][c]);
                    
                    if (c != 5) sb.append(',');
                    
                }
                
                out.println(sb.toString());
                sb = new StringBuilder();
                
            }
            
            out.flush();
            out.close();
            this.setFile(this.filename);
            
            return ;
            
        } catch (Exception e) {
                  
            return;
        }
        
        
        
    }
    
    protected void saveFile(){
        
        //System.out.println("Save Index :" + current_index);
        
        if (current_index > -1 && current_index < defs.length){
            
            defs [current_index] [0] = nameField.getText();
            defs [current_index] [1] = HField.getText();
            defs [current_index] [2] = WField.getText();
            defs [current_index] [3] = LRMField.getText();
            defs [current_index] [4] = TBMField.getText();
            defs [current_index] [5] = GapField.getText();
        
        }else {
            
           String [] [] def_tmp = new String [defs.length + 1] [6];
           
           for (int r = 0; r < defs.length; r++){
                
                for (int c = 0; c < 6; c++){
                   
                    def_tmp [r] [c] = defs [r] [c];
                                                            
                }
                
            }
            
           def_tmp [defs.length] [0] = nameField.getText();
           def_tmp [defs.length] [1] = HField.getText();
           def_tmp [defs.length] [2] = WField.getText();
           def_tmp [defs.length] [3] = LRMField.getText();
           def_tmp [defs.length] [4] = TBMField.getText();
           def_tmp [defs.length] [5] = GapField.getText();
           
           defs = def_tmp;
           
        }
        
        
        writeFile();
        
    }
    
       
    /** This method provides a label definition float array.  
        If the parse of the fields fails it returns null. */
    public float [] getLabelDef (){
        
        float h;
        float w;
        float tbm;
        float lrm;
        float gap;
        
        try {
            
            h = Float.parseFloat(HField.getText());
            w = Float.parseFloat(WField.getText());
            tbm = Float.parseFloat(TBMField.getText());
            lrm = Float.parseFloat(LRMField.getText());
            gap = Float.parseFloat(GapField.getText());
            
        }catch (Exception e) {
            
            return null;
            
        }
        
        return new float [] {h, w, lrm, tbm, gap};
        
    }
    
    protected void populateFields() {
        
         if (jList1.getSelectedIndex() > -1) {
            
            int i = jList1.getSelectedIndex();
             
           /* If label name starts with * then do not allow delete  */
            String t = (String)jList1.getModel().getElementAt(i);
               
           if (t.startsWith("*")) {
                
               deleteButton.setEnabled(false); 
               saveButton.setEnabled(false);
               
           }else {
                
                saveButton.setEnabled(true);
                deleteButton.setEnabled(true);
           }
            
            
            HField.setText(defs[i][1]);
            WField.setText(defs[i][2]);
            LRMField.setText(defs[i][3]);
            TBMField.setText(defs[i][4]);
            GapField.setText(defs[i][5]);
            nameField.setText(defs[i][0]);
            
            current_index = i;
            
            //System.out.println("Click Index :" + current_index);
            
        }
        
    }
    
   
    protected void clearFields() {
        
        HField.setText("");
        WField.setText("");
        LRMField.setText("");
        TBMField.setText("");
        GapField.setText("");
        nameField.setText("");
        current_index = defs.length;
        
        saveButton.setEnabled(true);
        
        
    }
    
    
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        HField = new javax.swing.JTextField();
        WField = new javax.swing.JTextField();
        TBMField = new javax.swing.JTextField();
        LRMField = new javax.swing.JTextField();
        GapField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        HField.setToolTipText("Also called Vertical Pitch when there is a gap below the label.");

        WField.setToolTipText("Actual Label Width");

        TBMField.setToolTipText("Top Margin");

        LRMField.setToolTipText("Side Margin");

        GapField.setToolTipText("Horizontal Pitch minus Width");

        jLabel3.setText("Height");

        jLabel4.setText("Width");

        jLabel5.setText("T & B Margin");

        jLabel6.setText("L & R Margin");

        jLabel7.setText("Gap");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel2.setText("Dimensions:");

        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nameFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nameFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nameFieldKeyTyped(evt);
            }
        });

        jLabel8.setText("Name");

        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jList1KeyReleased(evt);
            }
        });
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("New");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Inches");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel4)
                            .add(jLabel3)
                            .add(jLabel6)
                            .add(jLabel5)
                            .add(jLabel7)))
                    .add(jLabel2))
                .add(6, 6, 6)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel9)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, TBMField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, LRMField)
                    .add(HField)
                    .add(WField)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, GapField))
                .add(7, 7, 7)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nameField))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel8)
                            .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(saveButton)
                            .add(deleteButton)
                            .add(jButton1)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jLabel9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(HField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(WField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(LRMField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(TBMField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(GapField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/label_diagram.jpg"))); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 246, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jList1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyReleased
        
        populateFields();
        
    }//GEN-LAST:event_jList1KeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        clearFields();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void nameFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyPressed
        
              
    }//GEN-LAST:event_nameFieldKeyPressed

    private void nameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyReleased
        
        
        
    }//GEN-LAST:event_nameFieldKeyReleased

    private void nameFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameFieldKeyTyped
        
         
        
        
    }//GEN-LAST:event_nameFieldKeyTyped

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        
        deleteDef();
        
        
        
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        
       populateFields();
        
    }//GEN-LAST:event_jList1MouseClicked

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

       try {
            
            Float.parseFloat(HField.getText());
            Float.parseFloat(WField.getText());
            Float.parseFloat(TBMField.getText());
            Float.parseFloat(LRMField.getText());
            Float.parseFloat(GapField.getText());
            
        }catch (Exception e) {
                        
           javax.swing.JOptionPane.showMessageDialog(null, "Make sure the values entered into the Dimension fields are floating point (decimal) numbers.");
           return;
            
        }
        
        
        
        if (nameField.getText().contains(",") || nameField.getText().equals("")){
           
           javax.swing.JOptionPane.showMessageDialog(null, "You cannot have commas or a blank value in the name field.");
           return;
           
       }
        
        saveFile();        
        
    }//GEN-LAST:event_saveButtonActionPerformed
    
    protected String filename= "";
    protected String [] [] defs;  //the in-memory label def databse (table)
    protected int current_index = 0;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField GapField;
    private javax.swing.JTextField HField;
    private javax.swing.JTextField LRMField;
    private javax.swing.JTextField TBMField;
    private javax.swing.JTextField WField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
    
}
