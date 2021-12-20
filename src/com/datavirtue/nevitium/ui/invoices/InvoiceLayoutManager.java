/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * InvoiceLayoutManager.java
 *
 * Created on Aug 23, 2010, 12:51:35 AM
 */

package com.datavirtue.nevitium.ui.invoices;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Sean Anderson
 */
public class InvoiceLayoutManager extends javax.swing.JFrame {

    /** Creates new form InvoiceLayoutManager */
    public InvoiceLayoutManager() {
        initComponents();
        populateTree();

        panel = new JPanel()
        {
            public void paintComponent(Graphics g)
            {
                super.paintComponent(g);

                final Graphics g2 = getGraphics();

                System.out.println( "Are the Graphics objec equal? " + (g == g2));

                g.drawString("This works", 0, 50);

                g2.drawString("This doesn't work", 0, 100);

            }

            public Graphics getGraphics()
            {
            	System.out.println("getGraphics");
            	return super.getGraphics();
            }
        };//end JPanel

        panel.setPreferredSize(new Dimension(600, 800));
        panel.setDoubleBuffered(false);
        panel.repaint();
        this.setVisible(true);
        
    }

private void populateTree(){
File file = new File("invoiceLayout.xml");
      DefaultTreeModel defaultTreeModel;

    DefaultMutableTreeNode base = new DefaultMutableTreeNode("XML Document: "
        + file.getAbsolutePath());

    defaultTreeModel = new DefaultTreeModel(base);
    
    try{

    buildTree(defaultTreeModel, base, file);
    }catch(Exception e){
        e.printStackTrace();
    }
    jTree.setModel(defaultTreeModel);
    jTree.setEditable(true);
}

public void buildTree(DefaultTreeModel treeModel, DefaultMutableTreeNode current, File file)
      throws XMLStreamException, FileNotFoundException {
    FileInputStream inputStream = new FileInputStream(file);
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

    //addStartDocumentNodes(reader, current);

    parseRestOfDocument(reader, current);
  }

  private void addStartDocumentNodes(XMLStreamReader reader, DefaultMutableTreeNode current) {
    DefaultMutableTreeNode version = new DefaultMutableTreeNode(reader.getVersion());
    current.add(version);

    DefaultMutableTreeNode standalone = new DefaultMutableTreeNode(reader.isStandalone());
    current.add(standalone);

    DefaultMutableTreeNode standaloneSet = new DefaultMutableTreeNode(reader.standaloneSet());
    current.add(standaloneSet);

    DefaultMutableTreeNode encoding = new DefaultMutableTreeNode(reader.getEncoding());
    current.add(encoding);

    DefaultMutableTreeNode declaredEncoding = new DefaultMutableTreeNode(reader.getCharacterEncodingScheme());
    current.add(declaredEncoding);
  }

  private void parseRestOfDocument(XMLStreamReader reader, DefaultMutableTreeNode current)
      throws XMLStreamException {

    while (reader.hasNext()) {
      int type = reader.next();
      switch (type) {
      case XMLStreamConstants.START_ELEMENT:

        DefaultMutableTreeNode element = new DefaultMutableTreeNode(reader.getLocalName());
        current.add(element);
        current = element;

        if (reader.getNamespaceURI() != null) {
          String prefix = reader.getPrefix();
          if (prefix == null) {
            prefix = "[None]";
          }
          DefaultMutableTreeNode namespace = new DefaultMutableTreeNode("prefix = '"
              + prefix + "', URI = '" + reader.getNamespaceURI() + "'");
          current.add(namespace);
        }

        if (reader.getAttributeCount() > 0) {
          for (int i = 0; i < reader.getAttributeCount(); i++) {
            DefaultMutableTreeNode attribute = new DefaultMutableTreeNode("Attribute (name = '"
                + reader.getAttributeLocalName(i) + "', value = '" + reader.getAttributeValue(i)
                + "')");
            String attURI = reader.getAttributeNamespace(i);
            if (attURI != null) {
              String attPrefix = reader.getAttributePrefix(i);
              if (attPrefix == null || attPrefix.equals("")) {
                attPrefix = "[None]";
              }
              DefaultMutableTreeNode attNamespace = new DefaultMutableTreeNode(
                  "prefix=" + attPrefix + ",URI=" + attURI);
              attribute.add(attNamespace);
            }
            current.add(attribute);
          }
        }

        break;
      case XMLStreamConstants.END_ELEMENT:
        current = (DefaultMutableTreeNode) current.getParent();
        break;
      case XMLStreamConstants.CHARACTERS:
        if (!reader.isWhiteSpace()) {
          DefaultMutableTreeNode data = new DefaultMutableTreeNode("CD:"
              + reader.getText());
          
          current.add(data);
        }
        break;
      case XMLStreamConstants.DTD:
        DefaultMutableTreeNode dtd = new DefaultMutableTreeNode("DTD:" + reader.getText());
        current.add(dtd);
        break;
      case XMLStreamConstants.SPACE:
        break;
      case XMLStreamConstants.COMMENT:
        DefaultMutableTreeNode comment = new DefaultMutableTreeNode(reader.getText());
        current.add(comment);
        break;
      default:
        //System.out.println(type);
      }
    }
  }



private void saveLayout(){


}
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree();
        saveButton = new java.awt.Button();
        loadButton = new java.awt.Button();
        panel = new javax.swing.JPanel();

        setTitle("Document Builder");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setViewportView(jTree);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );

        saveButton.setLabel("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        loadButton.setLabel("Load");

        panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 387, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 521, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loadButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InvoiceLayoutManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTree;
    private java.awt.Button loadButton;
    private javax.swing.JPanel panel;
    private java.awt.Button saveButton;
    // End of variables declaration//GEN-END:variables

}
 class paintPanel extends JPanel {

        public void paintComponent(Graphics g) {
            //call super paintComponent
            super.paintComponent(g);

            //draw on this
            BufferedImage image = new BufferedImage(612, 792, BufferedImage.TYPE_INT_RGB);

            //get the image Graphics object
            Graphics2D imageG = image.createGraphics();

            //draw something
            imageG.fillRect(0, 0, 100, 100);

                //dispose the Graphics object when you're finished.
            imageG.dispose();


            //cast to Graphics2D
            Graphics2D tempg = (Graphics2D) g;

            //Draw BufferedImage
            tempg.drawImage(image, 0, 0, null);

        }
}
