/*
 * CheckPrintSettingsDialog.java
 *
 * Created on July 13, 2007, 1:10 PM
 */

package businessmanager.checkMod;
import datavirtue.*;
import javax.swing.*;

/**
 *
 * @author  Data Virtue
 */
public class CheckPrintSettingsDialog extends javax.swing.JDialog {
    
    private int date_x;
    private int date_y;
    private int payee_x;
    private int payee_y;
    private int amount_x;
    private int amount_y;
    private int spell_x;
    private int spell_y;
    private int memo_x;
    private int memo_y;
    private int sig_x;
    private int sig_y;
    
    private boolean printSig = true;
    
    /** Creates new form CheckPrintSettingsDialog */
    public CheckPrintSettingsDialog(java.awt.Frame parent, boolean modal, String path) {
        super(parent, modal);
        initComponents();
        workingPath = path;
        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);
        
        docFontSpin.getModel().setValue(new Integer(12));
        toFontSpin.getModel().setValue(new Integer(12));
        
        checkSettings();
        this.setVisible(true);
        
    }
    private String workingPath = "";
    
    private void checkSettings() {
        
        
        
        if (!new java.io.File(workingPath + "checks.ini").exists()){
        
        
        //plug in defaults
        Settings set = new Settings(workingPath + "checks.ini");
        set.setProp("DATEX", "490");
        set.setProp("DATEY", "65");
        set.setProp("PAYEEX", "65");
        set.setProp("PAYEEY", "90");
        set.setProp("AMTX", "495");
        set.setProp("AMTY", "105");
        set.setProp("SPELLX", "26");
        set.setProp("SPELLY", "142");
        set.setProp("MEMOX", "51");
        set.setProp("MEMOY", "206");
        set.setProp("SIGX", "380");
        set.setProp("SIGY", "145");
        set.setProp("DEF", "true");
        
        set.setProp("DOCFONT", "helvetica");
        set.setProp("DOCPS", "12");
        
        set.setProp("TOFONT", "helvetica");
        set.setProp("TOPS", "12");
        
        
        
        set = null;
        
        }
    
    Settings set = new Settings(workingPath + "checks.ini");
        
        //load settings
    dateX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
    dateX.getModel().setValue(conv(set.getProp("DATEX")));
    dateY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
    dateY.getModel().setValue(conv(set.getProp("DATEY")));
    
    amtX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
    amtX.getModel().setValue(conv(set.getProp("AMTX")));    
    amtY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
    amtY.getModel().setValue(conv(set.getProp("AMTY")));
    
    payX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
    payX.getModel().setValue(conv(set.getProp("PAYEEX")));
    payY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
    payY.getModel().setValue(conv(set.getProp("PAYEEY")));
    
    spellX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
    spellX.getModel().setValue(conv(set.getProp("SPELLX")));
    spellY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
    spellY.getModel().setValue(conv(set.getProp("SPELLY")));
    
    memoX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
    memoX.getModel().setValue(conv(set.getProp("MEMOX")));
    memoY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
    memoY.getModel().setValue(conv(set.getProp("MEMOY")));
    
    sigX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
    sigX.getModel().setValue(conv(set.getProp("SIGX")));
    sigY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
    sigY.getModel().setValue(conv(set.getProp("SIGY")));
    
    
    defaultBox.setSelected(Boolean.parseBoolean(set.getProp("DEF")));
    
    printBox.setSelected(Boolean.parseBoolean(set.getProp("PRINTSIG")));
    
    imagePath = set.getProp("SIGPATH");
        
    this.setImage(imagePath);
    
    changeSpinners();
    
    toFontSpin.getModel().setValue(new Integer(conv(set.getProp("TOPS"),12)));
    docFontSpin.getModel().setValue(new Integer(conv(set.getProp("DOCPS"),12)));
    
    String docFont = set.getProp("DOCFONT");
    String toFont = set.getProp("TOFONT");
    
    if (toFont != null && !toFont.trim().equals("")) jComboBox1.getModel().setSelectedItem(toFont);
    if (docFont != null && !docFont.trim().equals("")) jComboBox2.getModel().setSelectedItem(docFont);
       
    
    
    }
    
    private void saveSettings() {
        
        Settings props = new Settings(workingPath + "checks.ini");
        
        props.setProp("DATEX", Integer.toString((Integer)dateX.getModel().getValue()));
        props.setProp("DATEY", Integer.toString((Integer)dateY.getModel().getValue()));
        
        props.setProp("PAYEEX", Integer.toString((Integer)payX.getModel().getValue()));
        props.setProp("PAYEEY", Integer.toString((Integer)payY.getModel().getValue()));
        
        props.setProp("AMTX", Integer.toString((Integer)amtX.getModel().getValue()));
        props.setProp("AMTY", Integer.toString((Integer)amtY.getModel().getValue()));
        
        props.setProp("SPELLX", Integer.toString((Integer)spellX.getModel().getValue()));
        props.setProp("SPELLY", Integer.toString((Integer)spellY.getModel().getValue()));
        
        props.setProp("MEMOX", Integer.toString((Integer)memoX.getModel().getValue()));
        props.setProp("MEMOY", Integer.toString((Integer)memoY.getModel().getValue()));
        
        props.setProp("SIGX", Integer.toString((Integer)sigX.getModel().getValue()));
        props.setProp("SIGY", Integer.toString((Integer)sigY.getModel().getValue()));
        
        props.setProp("DEF", Boolean.toString(defaultBox.isSelected()));
        
        props.setProp("TOFONT", (String)jComboBox1.getModel().getSelectedItem());
        props.setProp("DOCFONT", (String)jComboBox2.getModel().getSelectedItem());
        
        props.setProp("TOPS", Integer.toString((Integer)toFontSpin.getModel().getValue()));
        props.setProp("DOCPS", Integer.toString((Integer)docFontSpin.getModel().getValue()));
        
        props.setProp("SIGPATH", imagePath);
        props.setProp("PRINTSIG", String.valueOf(printBox.isSelected()));
        
        
        
        
    }
    
    
    public static String conv (int i){        
        
        String s;
        s = Integer.toString(i);
        return s;
        
        
    }
    
   public static int conv(String s){
        
        int a = 0;
        try {
            
            a = Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
        
        return a;
    }
    
   public static int conv(String s, int def){
        
        int a = 0;
        try {
            
            a = Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            return def;
        }
        
        return a;
    }
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        defaultBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        sigY = new javax.swing.JSpinner();
        sigX = new javax.swing.JSpinner();
        memoY = new javax.swing.JSpinner();
        memoX = new javax.swing.JSpinner();
        spellY = new javax.swing.JSpinner();
        spellX = new javax.swing.JSpinner();
        amtY = new javax.swing.JSpinner();
        amtX = new javax.swing.JSpinner();
        payY = new javax.swing.JSpinner();
        payX = new javax.swing.JSpinner();
        dateX = new javax.swing.JSpinner();
        dateY = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        toFontSpin = new javax.swing.JSpinner();
        docFontSpin = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        applyButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        previewLabel = new javax.swing.JLabel();
        printBox = new javax.swing.JCheckBox();
        warnLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Check Layout");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        defaultBox.setText("Ignore These");
        defaultBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        defaultBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        defaultBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Date Y");

        jLabel1.setText("Date X");

        jLabel3.setText("Payee X");

        jLabel4.setText("Payee Y");

        jLabel5.setText("Amount X");

        jLabel6.setText("Amount Y");

        jLabel7.setText("Spelling X");

        jLabel8.setText("Spelling Y");

        jLabel11.setText("Signature X");

        jLabel12.setText("Signature Y");

        jLabel10.setText("Memo Y");

        jLabel9.setText("Memo X");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(defaultBox)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                                                                    .add(jLabel2)
                                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                                                .add(jPanel1Layout.createSequentialGroup()
                                                                    .add(jLabel1)
                                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                                            .add(jPanel1Layout.createSequentialGroup()
                                                                .add(jLabel3)
                                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                                        .add(jPanel1Layout.createSequentialGroup()
                                                            .add(jLabel4)
                                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                                    .add(jPanel1Layout.createSequentialGroup()
                                                        .add(jLabel5)
                                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                                .add(jPanel1Layout.createSequentialGroup()
                                                    .add(jLabel6)
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                            .add(jPanel1Layout.createSequentialGroup()
                                                .add(jLabel7)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                        .add(jPanel1Layout.createSequentialGroup()
                                            .add(jLabel8)
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                    .add(jPanel1Layout.createSequentialGroup()
                                        .add(jLabel11)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .add(jPanel1Layout.createSequentialGroup()
                                    .add(jLabel12)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel10)
                                    .add(jLabel9))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, sigY)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, sigX)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, memoY)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, memoX)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, spellY)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, spellX)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, amtY)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, amtX)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, payY)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, payX)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, dateX)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, dateY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(dateX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(dateY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(payX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(payY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(amtX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(amtY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(spellX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(spellY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(memoX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(memoY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(sigX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(sigY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultBox)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Helvetica", "Roman", "Courier" }));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Helvetica", "Roman", "Courier" }));

        jLabel13.setText("Payee Font");

        jLabel14.setText("Check Font");

        toFontSpin.setToolTipText("Font Size");

        docFontSpin.setToolTipText("Font Size");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(jLabel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jComboBox1, 0, 128, Short.MAX_VALUE)
                            .add(jComboBox2, 0, 128, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(docFontSpin)
                            .add(toFontSpin, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(toFontSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(docFontSpin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        applyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Floppy.png"))); // NOI18N
        applyButton.setText("Save/Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        browseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Find in folder.png"))); // NOI18N
        browseButton.setText("Browse");
        browseButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Signature Image (GIF or JPG)");

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        printBox.setSelected(true);
        printBox.setText("Print Signature");
        printBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        printBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        printBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        warnLabel.setFont(new java.awt.Font("Tahoma", 0, 10));
        warnLabel.setForeground(new java.awt.Color(204, 0, 0));
        warnLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, warnLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, previewLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(applyButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(browseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, printBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel15)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(previewLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(browseButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(printBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(warnLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(45, 45, 45)
                .add(applyButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String imagePath=".";
    
    
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        
        JFileChooser fileChooser = DV.getFileChooser(imagePath);
            
        java.io.File curFile = fileChooser.getSelectedFile();
        if (curFile == null) return;
        if (curFile != null )imagePath = curFile.toString();
        if (!curFile.toString().contains(".gif")  && !curFile.toString().contains(".jpg")) imagePath = "";
        
        setImage(imagePath);
        
        
    }//GEN-LAST:event_browseButtonActionPerformed

    private void setImage(String imagePath){
        
        boolean tooTall = false;
        
        previewLabel.setIcon(new ImageIcon(imagePath));
        if (previewLabel.getIcon().getIconHeight() > 75) {
            warnLabel.setText("Signature may be too tall!");
            tooTall = true;
            
        }else warnLabel.setText("");
        
        if (previewLabel.getIcon().getIconWidth() > 220) {
            
            warnLabel.setText("Signature may be too wide!");
            if (tooTall) warnLabel.setText("Signature image is too tall & wide!");
            
        }else{
                        
            if (!tooTall) warnLabel.setText("");
            
        }
        
        this.pack(); 
        
    }
    
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        
        saveSettings();
        this.dispose();
        
    }//GEN-LAST:event_applyButtonActionPerformed

    private void changeSpinners() {
        
        
        boolean status;
        
        if (defaultBox.isSelected()) status = false;
        else status = true;
        
        dateY.setEnabled(status);
        dateX.setEnabled(status);
        
        payX.setEnabled(status);
        payY.setEnabled(status);
        
        amtX.setEnabled(status);
        amtY.setEnabled(status);
        
        spellY.setEnabled(status);
        spellX.setEnabled(status);
        
        memoY.setEnabled(status);
        memoX.setEnabled(status);
        
        sigY.setEnabled(status);
        sigX.setEnabled(status);
        
    }
    
    
    private void defaultBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultBoxActionPerformed

        changeSpinners();
        
    }//GEN-LAST:event_defaultBoxActionPerformed
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner amtX;
    private javax.swing.JSpinner amtY;
    private javax.swing.JButton applyButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JSpinner dateX;
    private javax.swing.JSpinner dateY;
    private javax.swing.JCheckBox defaultBox;
    private javax.swing.JSpinner docFontSpin;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSpinner memoX;
    private javax.swing.JSpinner memoY;
    private javax.swing.JSpinner payX;
    private javax.swing.JSpinner payY;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JCheckBox printBox;
    private javax.swing.JSpinner sigX;
    private javax.swing.JSpinner sigY;
    private javax.swing.JSpinner spellX;
    private javax.swing.JSpinner spellY;
    private javax.swing.JSpinner toFontSpin;
    private javax.swing.JLabel warnLabel;
    // End of variables declaration//GEN-END:variables
    
}
