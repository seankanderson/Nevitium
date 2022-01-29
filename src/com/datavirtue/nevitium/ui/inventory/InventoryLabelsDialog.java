/*
 * InventoryLabelsDialog.java
 *
 * Created on January 27, 2007, 12:26 PM
 */
package com.datavirtue.nevitium.ui.inventory;

import com.datavirtue.nevitium.services.PdfLabelService;
import com.datavirtue.nevitium.services.util.DV;
import javax.swing.*;
import java.io.*;
//import org.pdfbox.PrintPDF;
//import org.pdfbox.pdmodel.PDDocument;
import java.awt.event.*;

/**
 *
 * @author Data Virtue
 */
public class InventoryLabelsDialog extends javax.swing.JDialog {

    /**
     * Creates new form InventoryLabelsDialog
     */
    public InventoryLabelsDialog(java.awt.Frame parent, boolean modal,
            javax.swing.table.TableModel tm, int[] selected_rows, String path) {
        super(parent, modal);
        initComponents();
        
        /* Close dialog on escape */
        ActionMap am = getRootPane().getActionMap();
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object windowCloseKey = new Object();
        KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action windowCloseAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        };
        im.put(windowCloseStroke, windowCloseKey);
        am.put(windowCloseKey, windowCloseAction);
        /**/

        this.tm = tm;
        this.selected_rows = selected_rows;

        countLabel.setText(selected_rows.length + " Row(s) selected.");

        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, null, 1);
        eachSpinner.setModel(model);

        model = new SpinnerNumberModel(0, 0, null, 1);;
        skipSpinner.setModel(model);

        labelPanel1.setFile("defs.txt");

        java.awt.Dimension dim = DV.computeCenter((java.awt.Window) this);
        this.setLocation(dim.width, dim.height);

        this.setVisible(true);

    }

    private void generateLabels() {

        float[] def = labelPanel1.getLabelDef();

        if (def != null) {  //begin action block

            String filename = "labels";

            boolean good = false;

            int no = 0;

            do {

                if (new File(filename + ".pdf").exists()) {

                    if (new File(filename + ".pdf").canWrite()) {

                        good = true;

                    } else {

                        no++;
                        filename = filename + no;

                    }

                } else {
                    good = true;
                }

            } while (!good);

            if (no > 20) {  //clean up old files

                for (int i = 1; i < no - 10; i++) {

                    if (new File("labels" + i + ".pdf").exists() && new File("labels" + i + ".pdf").canWrite()) {

                        File fx = new File("labels" + i + ".pdf"); // delete file
                        fx.delete();

                    }

                }

            }

            filename = filename + ".pdf";
            PdfLabelService pdf = new PdfLabelService(def, filename);

            if (centerRadio.isSelected()) {
                pdf.setAlignment(9, 1);  //center
            }
            if (leftRadio.isSelected()) {
                pdf.setAlignment(9, 0); //left   
            }
            if (midRadio.isSelected()) {
                pdf.setAlignment(5, 9); //middle
            }
            if (topRadio.isSelected()) {
                pdf.setAlignment(4, 9); //top
            }

            //pdf.setDebug(true);
            /* Setup PDFLabels object */
 /* Access tableModel and send data to the PDFLabels object */
            int row;
            int howmany = (Integer) eachSpinner.getValue();
            int skip = (Integer) skipSpinner.getValue();
            boolean warn = false;
            pdf.setStartLabel(skip + 1);

            StringBuilder sb = new StringBuilder();
            String tmp;

            for (int r = 0; r < selected_rows.length; r++) {

                row = selected_rows[r];  //the current row we are working on in each loop cycle

                for (int j = 0; j < howmany; j++) {  //how many of each label?

                    if (barcodeRadio.isSelected()) {  //barcode labels

                        /* Barcode section doesn't use the StringBuilder temp since it only acceses one field */
                        tmp = (String) tm.getValueAt(row, 2);
                        tmp = tmp.trim();

                        if (ucBox.isSelected()) {
                            tmp = tmp.toUpperCase();
                        }

                        /*Begin barcode type-check*/
                        if (C39Radio.isSelected()) {

                            if (!tmp.contains("=")) {

                                pdf.addCode39(tmp);  //code field

                            } else {

                                if (!warn) {
                                    javax.swing.JOptionPane.showMessageDialog(null, "Code 39 cannot contain =; use Code 128.");
                                }
                                warn = true;
                            }

                        }

                        if (C128Radio.isSelected()) {

                            pdf.addCode128(tmp); //code field

                        }
                        /*End barcode type-check*/

                    } else { //info label

                        if (upcBox.isSelected()) {
                            sb.append((String) tm.getValueAt(row, 1) + " ");
                        }
                        if (codeBox.isSelected()) {
                            sb.append("CODE: " + tm.getValueAt(row, 2).toString());
                        }
                        if (upcBox.isSelected() || codeBox.isSelected()) {
                            sb.append(nl);
                        }

                        if (descBox.isSelected()) {
                            sb.append((String) tm.getValueAt(row, 3) + nl);
                        }

                        if (sizeBox.isSelected()) {
                            sb.append("SIZE: " + (String) tm.getValueAt(row, 4) + "   ");
                        }
                        if (weightBox.isSelected()) {
                            sb.append("WEIGHT: " + (String) tm.getValueAt(row, 5));
                        }
                        if (sizeBox.isSelected() || weightBox.isSelected()) {
                            sb.append(nl);
                        }

                        if (costBox.isSelected()) {
                            sb.append("$" + DV.money((Float) tm.getValueAt(row, 7)) + "   ");
                        }
                        if (priceBox.isSelected()) {
                            sb.append("$" + DV.money((Float) tm.getValueAt(row, 8)));
                        }
                        if (costBox.isSelected() || priceBox.isSelected()) {
                            sb.append(nl);
                        }

                        if (catBox.isSelected()) {
                            sb.append((String) tm.getValueAt(row, 9));
                        }

                        /*Formatting steps*/
                        if (ucBox.isSelected()) {
                            tmp = sb.toString().toUpperCase();
                        } else {
                            tmp = sb.toString();
                        }

                        pdf.add(tmp);  //add the data to the Label Sheet Object

                    }

                    //System.out.println(sb.toString());
                    tmp = "";
                    sb = new StringBuilder();
                }

            }

            pdf.finnish();

            if (prnRadio.isSelected()) {

                //ReportFactory.windowsFastPrint(filename, props);

            } //end Fast print section

            if (!prnRadio.isSelected()) {  //print/view

                //Settings props = new Settings(workingPath + "settings.ini");  //this needs a static method to get one property

                //ReportFactory.veiwPDF(props.getProp("ACRO"), filename, props);

                //props = null;

            }

        } else {  //end action block

            javax.swing.JOptionPane.showMessageDialog(null, "Make sure the values entered into the Dimension fields are floating point (decimal) numbers.");

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        labelPanel1 = new com.datavirtue.nevitium.ui.LabelPanel();
        jPanel1 = new javax.swing.JPanel();
        goButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        barcodeRadio = new javax.swing.JRadioButton();
        infoRadio = new javax.swing.JRadioButton();
        upcBox = new javax.swing.JCheckBox();
        codeBox = new javax.swing.JCheckBox();
        descBox = new javax.swing.JCheckBox();
        sizeBox = new javax.swing.JCheckBox();
        weightBox = new javax.swing.JCheckBox();
        costBox = new javax.swing.JCheckBox();
        priceBox = new javax.swing.JCheckBox();
        catBox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        centerRadio = new javax.swing.JRadioButton();
        leftRadio = new javax.swing.JRadioButton();
        ucBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        eachSpinner = new javax.swing.JSpinner();
        skipSpinner = new javax.swing.JSpinner();
        midRadio = new javax.swing.JRadioButton();
        topRadio = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jRadioButton5 = new javax.swing.JRadioButton();
        prnRadio = new javax.swing.JRadioButton();
        C39Radio = new javax.swing.JRadioButton();
        C128Radio = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        countLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Inventory Labels");
        setResizable(false);

        labelPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        goButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Play.png"))); // NOI18N
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        buttonGroup1.add(barcodeRadio);
        barcodeRadio.setSelected(true);
        barcodeRadio.setText("Barcode");
        barcodeRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        barcodeRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        barcodeRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barcodeRadioActionPerformed(evt);
            }
        });

        buttonGroup1.add(infoRadio);
        infoRadio.setText("Info Labels");
        infoRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        infoRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        infoRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoRadioActionPerformed(evt);
            }
        });

        upcBox.setText("UPC");
        upcBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        upcBox.setEnabled(false);
        upcBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        codeBox.setSelected(true);
        codeBox.setText("Code");
        codeBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        codeBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        descBox.setText("Desc.");
        descBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        descBox.setEnabled(false);
        descBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        sizeBox.setText("Size");
        sizeBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sizeBox.setEnabled(false);
        sizeBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        weightBox.setText("Weight");
        weightBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        weightBox.setEnabled(false);
        weightBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        costBox.setText("Cost");
        costBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        costBox.setEnabled(false);
        costBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        priceBox.setText("Price");
        priceBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        priceBox.setEnabled(false);
        priceBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        catBox.setText("Category");
        catBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        catBox.setEnabled(false);
        catBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(costBox)
                    .add(sizeBox)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(barcodeRadio)
                            .add(upcBox)
                            .add(descBox))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(codeBox)
                            .add(infoRadio)
                            .add(weightBox)
                            .add(catBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(priceBox))))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(barcodeRadio)
                    .add(infoRadio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(upcBox)
                    .add(codeBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(descBox)
                    .add(catBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sizeBox)
                    .add(weightBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(costBox)
                    .add(priceBox))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        buttonGroup2.add(centerRadio);
        centerRadio.setSelected(true);
        centerRadio.setText("Center");
        centerRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        centerRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup2.add(leftRadio);
        leftRadio.setText("Left");
        leftRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        leftRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        ucBox.setSelected(true);
        ucBox.setText("UPPERCASE");
        ucBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ucBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel1.setText("How many each");

        jLabel2.setText("Skip");

        eachSpinner.setFont(new java.awt.Font("Courier", 0, 12));

        skipSpinner.setFont(new java.awt.Font("Courier", 0, 12));

        buttonGroup4.add(midRadio);
        midRadio.setSelected(true);
        midRadio.setText("Middle");
        midRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        midRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup4.add(topRadio);
        topRadio.setText("Top");
        topRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        topRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel3.setText("V-Align:");

        jLabel4.setText("H-Align:");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ucBox)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(eachSpinner)
                            .add(skipSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(centerRadio)
                            .add(midRadio))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(topRadio)
                            .add(leftRadio))))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(ucBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(eachSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(skipSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 16, Short.MAX_VALUE)
                        .add(jLabel4))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(midRadio)
                            .add(topRadio))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(centerRadio)
                            .add(leftRadio))))
                .addContainerGap())
        );

        buttonGroup5.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("View/Print");
        jRadioButton5.setToolTipText("Pdf Viewer");
        jRadioButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup5.add(prnRadio);
        prnRadio.setText("Fast Print");
        prnRadio.setToolTipText("Windows Default Printer");
        prnRadio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        prnRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup3.add(C39Radio);
        C39Radio.setSelected(true);
        C39Radio.setText("Code 3 of 9");
        C39Radio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        C39Radio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        C39Radio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                C39RadioActionPerformed(evt);
            }
        });

        buttonGroup3.add(C128Radio);
        C128Radio.setText("Code 128");
        C128Radio.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        C128Radio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        C128Radio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                C128RadioActionPerformed(evt);
            }
        });

        jLabel5.setText("Barcode Type:");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(C39Radio)
                            .add(C128Radio)))
                    .add(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jRadioButton5)
                            .add(prnRadio))))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(C39Radio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(C128Radio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 46, Short.MAX_VALUE)
                .add(prnRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton5)
                .addContainerGap())
        );

        countLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        countLabel.setText("1 Record Selected.");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(countLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(goButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(countLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 102, Short.MAX_VALUE)
                        .add(goButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, labelPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(labelPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void C39RadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_C39RadioActionPerformed

        ucBox.setSelected(true);
        ucBox.setEnabled(false);

    }//GEN-LAST:event_C39RadioActionPerformed

    private void C128RadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_C128RadioActionPerformed

        ucBox.setSelected(false);
        ucBox.setEnabled(false);


    }//GEN-LAST:event_C128RadioActionPerformed

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed

        generateLabels();

    }//GEN-LAST:event_goButtonActionPerformed

    private void barcodeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barcodeRadioActionPerformed

        upcBox.setEnabled(false);
        upcBox.setSelected(false);

        codeBox.setSelected(true);
        codeBox.setEnabled(false);

        sizeBox.setEnabled(false);
        sizeBox.setSelected(false);

        weightBox.setEnabled(false);
        weightBox.setSelected(false);

        costBox.setEnabled(false);
        costBox.setSelected(false);

        priceBox.setEnabled(false);
        priceBox.setSelected(false);

        catBox.setEnabled(false);
        catBox.setSelected(false);

        C128Radio.setEnabled(true);
        C39Radio.setEnabled(true);

        descBox.setSelected(false); //cleanup
        descBox.setEnabled(false);

        centerRadio.setSelected(true);

        ucBox.setSelected(true);
        ucBox.setEnabled(false);

    }//GEN-LAST:event_barcodeRadioActionPerformed

    private void infoRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoRadioActionPerformed

        codeBox.setEnabled(true);

        upcBox.setEnabled(true);

        sizeBox.setEnabled(true);
        weightBox.setEnabled(true);
        costBox.setEnabled(true);
        priceBox.setEnabled(true);
        catBox.setEnabled(true);

        C128Radio.setEnabled(false);
        C39Radio.setEnabled(false);

        descBox.setSelected(true);
        descBox.setEnabled(true);

        leftRadio.setSelected(true);

        //ucBox.setSelected(true);
        ucBox.setEnabled(true);

    }//GEN-LAST:event_infoRadioActionPerformed

    private javax.swing.table.TableModel tm;
    private int[] selected_rows;
    private String nl = System.getProperty("line.separator");

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton C128Radio;
    private javax.swing.JRadioButton C39Radio;
    private javax.swing.JRadioButton barcodeRadio;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JCheckBox catBox;
    private javax.swing.JRadioButton centerRadio;
    private javax.swing.JCheckBox codeBox;
    private javax.swing.JCheckBox costBox;
    private javax.swing.JLabel countLabel;
    private javax.swing.JCheckBox descBox;
    private javax.swing.JSpinner eachSpinner;
    private javax.swing.JButton goButton;
    private javax.swing.JRadioButton infoRadio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton5;
    private com.datavirtue.nevitium.ui.LabelPanel labelPanel1;
    private javax.swing.JRadioButton leftRadio;
    private javax.swing.JRadioButton midRadio;
    private javax.swing.JCheckBox priceBox;
    private javax.swing.JRadioButton prnRadio;
    private javax.swing.JCheckBox sizeBox;
    private javax.swing.JSpinner skipSpinner;
    private javax.swing.JRadioButton topRadio;
    private javax.swing.JCheckBox ucBox;
    private javax.swing.JCheckBox upcBox;
    private javax.swing.JCheckBox weightBox;
    // End of variables declaration//GEN-END:variables

}
