/*
 * CheckPrintSettingsDialog.java
 *
 * Created on July 13, 2007, 1:10 PM
 */
package com.datavirtue.nevitium.ui.checks;

import com.google.inject.Injector;
import datavirtue.*;
import com.datavirtue.nevitium.services.DiService;
import java.awt.Point;
import javax.swing.*;
import com.datavirtue.nevitium.models.settings.CheckSettings;
import com.datavirtue.nevitium.services.CheckSettingsService;
import com.datavirtue.nevitium.services.ExceptionService;
import java.sql.SQLException;

/**
 *
 * @author Data Virtue
 */
public class CheckPrintSettingsDialog extends javax.swing.JDialog {

    private CheckSettingsService checkSettings;

    /**
     * Creates new form CheckPrintSettingsDialog
     */
    public CheckPrintSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Injector injector = DiService.getInjector();
        checkSettings = injector.getInstance(CheckSettingsService.class);
        checkSettings.setObjectType(CheckSettings.class);

        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        checkFontSizeSpinner.getModel().setValue(12);
        payeeFontSizeSpinner.getModel().setValue(12);

        loadCheckSettings();
        this.setVisible(true);

    }

    private void loadCheckSettings() {

        checkSettings.setObjectType(CheckSettings.class);
        CheckSettings settings = new CheckSettings();
        try {
            if (!checkSettings.doExist()) {
                checkSettings.set(new CheckSettings());
            }
            settings = checkSettings.getObject();
        } catch (SQLException e) {
            ExceptionService.showErrorDialog(this, e, "Error loading check settings");
        }

        dateX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
        dateX.getModel().setValue(settings.getCheckDate().x);
        dateY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
        dateY.getModel().setValue(settings.getCheckDate().y);

        amtX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
        amtX.getModel().setValue(settings.getAmount().x);
        amtY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
        amtY.getModel().setValue(settings.getAmount().y);

        payX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
        payX.getModel().setValue(settings.getPayee().x);
        payY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
        payY.getModel().setValue(settings.getPayee().y);

        spellX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
        spellX.getModel().setValue(settings.getSpelling().x);
        spellY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
        spellY.getModel().setValue(settings.getSpelling().y);

        memoX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
        memoX.getModel().setValue(settings.getMemo().x);
        memoY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
        memoY.getModel().setValue(settings.getMemo().y);

        sigX.setModel(new SpinnerNumberModel(36, 10, 600, 1));
        sigX.getModel().setValue(settings.getSignature().x);
        sigY.setModel(new SpinnerNumberModel(36, 10, 780, 1));
        sigY.getModel().setValue(settings.getSignature().y);

        useDefaultCheckSettingsBox.setSelected(settings.isUseDefaultCheckSettings());

        printSignatureBox.setSelected(settings.isPrintSignatureOnChecks());

        changeSpinners();

        payeeFontSizeSpinner.getModel().setValue(settings.getPayeeFont().getFontSize() < 8 ? 12 : settings.getPayeeFont().getFontSize());
        checkFontSizeSpinner.getModel().setValue(settings.getCheckFont().getFontSize() < 8 ? 12 : settings.getCheckFont().getFontSize());

        String checkFont = settings.getCheckFont().getFontName();
        String payeeFont = settings.getPayeeFont().getFontName();

        if (payeeFont != null && !payeeFont.trim().equals("")) {
            payeeFontCombo.getModel().setSelectedItem(payeeFont);
        }
        if (checkFont != null && !checkFont.trim().equals("")) {
            checkFontCombo.getModel().setSelectedItem(checkFont);
        }

    }

    private void saveSettings() {
        try {
            var check = checkSettings.getObject();
            check.setAmount(getPointForSpinners(this.amtX, this.amtY));
            check.setBase64EncodedImage(null);
            check.setCheckDate(getPointForSpinners(this.amtX, this.amtY));
            check.setMemo(getPointForSpinners(this.memoX, this.memoY));
            check.setPayee(getPointForSpinners(this.payX, this.payY));
            check.setSignature(getPointForSpinners(this.sigX, this.sigY));
            check.setSpelling(getPointForSpinners(this.spellX, this.spellY));
            check.setPrintSignatureOnChecks(this.printSignatureBox.isSelected());
            check.setUseDefaultCheckSettings(this.useDefaultCheckSettingsBox.isSelected());
            checkSettings.save();
        } catch (SQLException e) {
            ExceptionService.showErrorDialog(this, e, "Error saving check settings");
        }
    }

    private Point getPointForSpinners(JSpinner xspinner, JSpinner yspinner) {
        return new Point((int) xspinner.getModel().getValue(), (int) yspinner.getModel().getValue());
    }

    public static int conv(String s, int def) {

        int a = 0;
        try {

            a = Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            return def;
        }
        return a;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        useDefaultCheckSettingsBox = new javax.swing.JCheckBox();
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
        checkFontCombo = new javax.swing.JComboBox();
        payeeFontCombo = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        payeeFontSizeSpinner = new javax.swing.JSpinner();
        checkFontSizeSpinner = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        applyButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        previewLabel = new javax.swing.JLabel();
        printSignatureBox = new javax.swing.JCheckBox();
        warnLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Check Layout");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        useDefaultCheckSettingsBox.setText("Use Default");
        useDefaultCheckSettingsBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useDefaultCheckSettingsBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        useDefaultCheckSettingsBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useDefaultCheckSettingsBoxActionPerformed(evt);
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
                    .add(useDefaultCheckSettingsBox)
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
                                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2)
                                                                .add(jLabel1))
                                                            .add(jLabel3))
                                                        .add(jLabel4))
                                                    .add(jLabel5))
                                                .add(jLabel6))
                                            .add(jLabel7))
                                        .add(jLabel8))
                                    .add(jLabel11))
                                .add(jLabel12))
                            .add(jLabel10)
                            .add(jLabel9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
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
                .add(useDefaultCheckSettingsBox)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        checkFontCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Helvetica", "Roman", "Courier" }));

        payeeFontCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Helvetica", "Roman", "Courier" }));

        jLabel13.setText("Payee Font");

        jLabel14.setText("Check Font");

        payeeFontSizeSpinner.setToolTipText("Font Size");

        checkFontSizeSpinner.setToolTipText("Font Size");

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
                            .add(payeeFontCombo, 0, 128, Short.MAX_VALUE)
                            .add(checkFontCombo, 0, 128, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(checkFontSizeSpinner)
                            .add(payeeFontSizeSpinner))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(payeeFontCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(payeeFontSizeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(checkFontCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(checkFontSizeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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

        printSignatureBox.setSelected(true);
        printSignatureBox.setText("Print Signature");
        printSignatureBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        printSignatureBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        printSignatureBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        warnLabel.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
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
                    .add(org.jdesktop.layout.GroupLayout.LEADING, printSignatureBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
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
                .add(printSignatureBox)
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

    private String imagePath = ".";


    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        JFileChooser fileChooser = DV.getFileChooser(imagePath);

        java.io.File curFile = fileChooser.getSelectedFile();
        if (curFile == null) {
            return;
        }
        if (curFile != null) {
            imagePath = curFile.toString();
        }
        if (!curFile.toString().contains(".gif") && !curFile.toString().contains(".jpg")) {
            imagePath = "";
        }

        setImage(imagePath);


    }//GEN-LAST:event_browseButtonActionPerformed

    private void setImage(String imagePath) {

        boolean tooTall = false;

        previewLabel.setIcon(new ImageIcon(imagePath));
        if (previewLabel.getIcon().getIconHeight() > 75) {
            warnLabel.setText("Signature may be too tall!");
            tooTall = true;

        } else {
            warnLabel.setText("");
        }

        if (previewLabel.getIcon().getIconWidth() > 220) {

            warnLabel.setText("Signature may be too wide!");
            if (tooTall) {
                warnLabel.setText("Signature image is too tall & wide!");
            }

        } else {

            if (!tooTall) {
                warnLabel.setText("");
            }

        }

        this.pack();

    }

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed

        saveSettings();
        this.dispose();

    }//GEN-LAST:event_applyButtonActionPerformed

    private void changeSpinners() {

        boolean status;

        if (useDefaultCheckSettingsBox.isSelected()) {
            status = false;
        } else {
            status = true;
        }

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


    private void useDefaultCheckSettingsBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useDefaultCheckSettingsBoxActionPerformed

        changeSpinners();

    }//GEN-LAST:event_useDefaultCheckSettingsBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner amtX;
    private javax.swing.JSpinner amtY;
    private javax.swing.JButton applyButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JComboBox checkFontCombo;
    private javax.swing.JSpinner checkFontSizeSpinner;
    private javax.swing.JSpinner dateX;
    private javax.swing.JSpinner dateY;
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
    private javax.swing.JComboBox payeeFontCombo;
    private javax.swing.JSpinner payeeFontSizeSpinner;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JCheckBox printSignatureBox;
    private javax.swing.JSpinner sigX;
    private javax.swing.JSpinner sigY;
    private javax.swing.JSpinner spellX;
    private javax.swing.JSpinner spellY;
    private javax.swing.JCheckBox useDefaultCheckSettingsBox;
    private javax.swing.JLabel warnLabel;
    // End of variables declaration//GEN-END:variables

}
