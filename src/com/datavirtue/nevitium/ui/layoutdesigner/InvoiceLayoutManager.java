/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * InvoiceLayoutManager.java
 *
 * Created on Aug 23, 2010, 12:51:35 AM
 */

package com.datavirtue.nevitium.ui.layoutdesigner;


import com.datavirtue.nevitium.services.util.DV;
import com.datavirtue.nevitium.services.util.ExtensionFileFilter;
import com.datavirtue.nevitium.ui.util.TreeUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.freehep.util.export.ExportDialog;


/**
 *
 * @author Sean Anderson
 *
 * create a DocElement class to hold any document element data for painting to a WYSIWYG panel
 *
 * Every element in the tree must have size and position elements/children
 * others include font, color, text
 */

public class InvoiceLayoutManager extends javax.swing.JFrame {
private DocumentLayout layout;
    /** Creates new form InvoiceLayoutManager */
    private Image winIcon;
    public InvoiceLayoutManager(String filename) {

        initComponents();
        String file_sep = System.getProperty("file.separator");
        Toolkit tools = Toolkit.getDefaultToolkit();
        Image winIcon = tools.getImage(getClass().getResource("/businessmanager/res/Orange.png"));
        this.setIconImage(winIcon);
        
        //System.out.println(working_path+filename);
        setPopup();
        this.setVisible(true);
//        if (!new File(working_path+filename).exists()){
//            JOptionPane.showMessageDialog(null, "The file "+working_path+filename+" was not found.");
//        }else {
//            layout = new DocumentLayout(working_path + filename);
//            this.setTitle("Form Builder "+ "["+working_path+filename+"]");
//            jTree.setModel(layout.getTreeModel());
//            preview(false);
//        }
        
        ImageIcon rootCell = new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Measure.png"));
        ImageIcon subCell = new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Drawing.png"));
        ImageIcon settingsCell = new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Gear.png"));
        ImageIcon tableCell = new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Table.png"));
        ImageIcon barcodeCell = new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-16/enabled/Barcode scanner1.png"));
        
        ImageIcon positionCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Compass.png"));
        ImageIcon sizeCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Resize image.png"));
        ImageIcon dataCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Data.png"));
        ImageIcon textCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Tag.png"));
        ImageIcon fontCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Text.png"));
        ImageIcon imageCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Image.png"));
        ImageIcon graphicsCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Figures.png"));
        ImageIcon includeCell = new javax.swing.ImageIcon(getClass()
                .getResource("/businessmanager/res/Aha-16/enabled/Apply.png"));
        
        
        jTree.setCellRenderer(new CustomTreeCellRenderer(rootCell, subCell, settingsCell,
                tableCell, barcodeCell, positionCell, sizeCell, dataCell, textCell, fontCell, imageCell,
                graphicsCell, includeCell));

    }

    
private void loadLayout(){

    JFileChooser jfc = new JFileChooser(current_file);
        jfc.setSelectedFile(current_file);
        jfc.setDialogTitle("Load XML Layout File (.xml)");
        /* Only allow the user to select a file (specifically .egd files, although they can choose All Files) */
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filter = new ExtensionFileFilter(".xml Files", new String[] { "xml"});
        jfc.setFileFilter(filter);
        jfc.setSelectedFile(new File("/layouts/"));
        /* the JFileChooser method returns an integer value denoting the status of the file dialog based on the user's choice */
        int status = jfc.showOpenDialog(this);
        /* Exit the method if the user hit cancel */
        if (status == JFileChooser.CANCEL_OPTION) return;

        File file = jfc.getSelectedFile();
        String f = file.getPath();
        /* Check to certify the user chose a .xml file */
        if (!f.toLowerCase().endsWith(".xml")){
            /* if ext is not valid, append .egd ext and check  */
            if (new File(f+".xml").exists()){
                file = new File(f+".xml");
                f = file.getPath();
            }else{
                JOptionPane.showMessageDialog(null,
                        "You must select a XML layout file (.xml).");
                return;
            }
        }
        layout = null;
        layout = new DocumentLayout(file.getAbsolutePath());
        layout.buildLayout(false);
        jTree.setModel(layout.getTreeModel());

}
private File current_file;

  private DefaultMutableTreeNode buildColumnNode(){

      DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode("column");
      DefaultMutableTreeNode tempNode;

      tempNode = new DefaultMutableTreeNode("width");
      tempNode.add(new DefaultMutableTreeNode("100"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("header");
      tempNode.add(new DefaultMutableTreeNode("Column"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("arc_width");
      tempNode.add(new DefaultMutableTreeNode("1"));
      columnNode.add(tempNode);
      tempNode = new DefaultMutableTreeNode("arc_height");
      tempNode.add(new DefaultMutableTreeNode("1"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("head_color");
      tempNode.add(new DefaultMutableTreeNode("0xFFFFFF"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("cell_color");
      tempNode.add(new DefaultMutableTreeNode("0xFFFFFF"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("alt_color");
      tempNode.add(new DefaultMutableTreeNode("0xFFFFFF"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("top_border");
      tempNode.add(new DefaultMutableTreeNode("1"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("bot_border");
      tempNode.add(new DefaultMutableTreeNode("1"));
      columnNode.add(tempNode);

      tempNode = new DefaultMutableTreeNode("rep_count");
      tempNode.add(new DefaultMutableTreeNode("1"));
      columnNode.add(tempNode);


      return columnNode;
  }
private JMenuItem deleteMenuItem;
private JMenuItem insertMenuItem;
private JMenuItem moveUpMenuItem;
private JMenuItem helpMenuItem;

private void setPopup(){
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        //System.out.println("Popup Item Selected: " + actionEvent.getActionCommand());
        Object node = jTree.getLastSelectedPathComponent();
        if ((node != null) && (node instanceof TreeNode)) {
            DefaultTreeModel model = (DefaultTreeModel)jTree.getModel();
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;

            //System.out.println("Node (data) Selected: "+treeNode.toString());
            
            /* Check for each menu item and invoke the proper method */
            /* you may need to check for node position relative to the root to filter commands better */

            /* Checks for Insert on table node */
            if (treeNode.toString().equalsIgnoreCase("table") && actionEvent.getActionCommand().equalsIgnoreCase("insert")){
                treeNode.add(buildColumnNode());
                model.reload(treeNode);
            }
            if (treeNode.toString().equalsIgnoreCase("column") && actionEvent.getActionCommand().equalsIgnoreCase("delete")){
                model.removeNodeFromParent(treeNode);
            }
            
            if (treeNode.getLevel() == 1 && actionEvent.getActionCommand().equalsIgnoreCase("insert")){
                treeNode.add(addNewDocumentElement());
                model.reload(treeNode);
            }
            if (treeNode.getLevel() == 2 && actionEvent.getActionCommand().equalsIgnoreCase("move up")){
      
                 TreePath tp = jTree.getSelectionPath();        // get path of selected node.
             // if no node selected, show error msg and return
             if(tp == null) {
                 JOptionPane.showMessageDialog(null, "No node selected", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
             }
             // Get the last component of the selected node path. This is cast to a DefaultMutableTreeNode
             DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)tp.getLastPathComponent();
             DefaultTreeModel dtm = (DefaultTreeModel)jTree.getModel();    // get the tree model
             //now get the index of the selected node in the DefaultTreeModel
             int index = dtm.getIndexOfChild(dtm.getRoot(), dmtn);
             // if selected node is first, return (can't move it up)
             if(index != 0) {
                 //dtm.insertNodeInto(dmtn, (DefaultMutableTreeNode)dtm.getRoot(), index-1);    // move the node
                 moveNode(-1);

             }
             else {
                 JOptionPane.showMessageDialog(null, "Selected node is first in tree.", "Error", JOptionPane.ERROR_MESSAGE);
             }
             // reload revalidates the look of the JTree on screen
             dtm.reload();
                
                //model.removeNodeFromParent(treeNode);
                //model.reload();
            }
            if (treeNode.getLevel() == 2 && actionEvent.getActionCommand().equalsIgnoreCase("delete")){

                int a = javax.swing.JOptionPane.showConfirmDialog(null, "Do you want to remove "+treeNode.toString(),"Remove Document Element",  JOptionPane.YES_NO_OPTION);
                if (a == 0){

                model.removeNodeFromParent(treeNode);
                //model.reload(treeNode);
                }                
            }
        }
      }
    };



    popup = new JPopupMenu();

     // Insert
    insertMenuItem = new JMenuItem("Insert");
    insertMenuItem.addActionListener(actionListener);
    popup.add(insertMenuItem);

    // Delete
    deleteMenuItem = new JMenuItem("Delete");
    deleteMenuItem.addActionListener(actionListener);
    popup.add(deleteMenuItem);

    // Delete
    moveUpMenuItem = new JMenuItem("Move Up");
    moveUpMenuItem.addActionListener(actionListener);
    popup.add(moveUpMenuItem);


    // Help
    helpMenuItem = new JMenuItem("Help");
    helpMenuItem.addActionListener(actionListener);
    popup.add(helpMenuItem);


}

private boolean moveNode(int step){
    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)jTree.getSelectionPath().getLastPathComponent();
    DefaultMutableTreeNode parentNode = null;
    if(selectedNode == null){
        JOptionPane.showMessageDialog(null,
                "No node selected", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    } else{
        parentNode = (DefaultMutableTreeNode)selectedNode.getParent();
        if(parentNode == null){
            return false;
        } else {
            DefaultTreeModel dtm = (DefaultTreeModel)jTree.getModel();
            int childCount = parentNode.getChildCount();
            int currentNodeIndex = parentNode.getIndex(selectedNode);
            int destIndex = currentNodeIndex + step;
            if(destIndex > childCount - 1 || destIndex < 0){
                return false;
            } else {
                System.out.println("Are we resetting the expansion state???");
                Enumeration tp = TreeUtil.saveExpansionState(jTree);
                //TreePath path = jTree.getPathForRow(2);
                dtm.insertNodeInto(selectedNode, parentNode, destIndex);
                TreeUtil.loadExpansionState(jTree, tp);
                //jTree.expandPath(path);
                
                //dtm.reload();
                
            }
        }
    }
    return false;
}

private DefaultMutableTreeNode addNewDocumentElement(){
    DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode("new element");
      DefaultMutableTreeNode topNode, subNode;

      topNode = new DefaultMutableTreeNode("position");
      
      subNode = new DefaultMutableTreeNode("X");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      
      subNode = new DefaultMutableTreeNode("Y");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);            
      elementNode.add(topNode);

      topNode = new DefaultMutableTreeNode("size");
      subNode = new DefaultMutableTreeNode("X");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("Y");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);            
      elementNode.add(topNode);
      
      topNode = new DefaultMutableTreeNode("data");
      subNode = new DefaultMutableTreeNode("X");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("Y");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);            
      elementNode.add(topNode);
      
      topNode = new DefaultMutableTreeNode("text");
      subNode = new DefaultMutableTreeNode("string");
      subNode.add(new DefaultMutableTreeNode("null"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("X");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("Y");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);            
      elementNode.add(topNode);
      
      topNode = new DefaultMutableTreeNode("font");
      subNode = new DefaultMutableTreeNode("family");
      subNode.add(new DefaultMutableTreeNode("tahoma"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("style");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("points");
      subNode.add(new DefaultMutableTreeNode("12"));
      topNode.add(subNode);            
      elementNode.add(topNode);

      topNode = new DefaultMutableTreeNode("image");
      subNode = new DefaultMutableTreeNode("null");
      elementNode.add(topNode);

      topNode = new DefaultMutableTreeNode("table");
      subNode = new DefaultMutableTreeNode("row_height");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("row_count");
      subNode.add(new DefaultMutableTreeNode("0"));
      topNode.add(subNode);
      elementNode.add(topNode);

      topNode = new DefaultMutableTreeNode("graphics");
      subNode = new DefaultMutableTreeNode("border");
      subNode.add(new DefaultMutableTreeNode("0.0"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("fg_color");
      subNode.add(new DefaultMutableTreeNode("0x000000"));
      topNode.add(subNode);
      subNode = new DefaultMutableTreeNode("bg_color");
      subNode.add(new DefaultMutableTreeNode("0xFFFFFF"));
      topNode.add(subNode);
      elementNode.add(topNode);

      topNode = new DefaultMutableTreeNode("include");
      subNode = new DefaultMutableTreeNode("true");
      elementNode.add(topNode);

      return elementNode;

}


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("checked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        treePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree();
        yField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        xField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        autoBox = new javax.swing.JCheckBox();
        jToolBar2 = new javax.swing.JToolBar();
        previewPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        loadButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        psCalcButton = new javax.swing.JButton();
        previewButton = new javax.swing.JButton();

        setTitle("Document Builder");
        setIconImage(winIcon);

        treePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTree.setEditable(true);
        jTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTreeMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTree);

        yField.setEditable(false);

        jLabel1.setText("Y");

        xField.setEditable(false);

        jLabel2.setText("X");

        autoBox.setSelected(true);
        autoBox.setText("Auto");

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        javax.swing.GroupLayout treePanelLayout = new javax.swing.GroupLayout(treePanel);
        treePanel.setLayout(treePanelLayout);
        treePanelLayout.setHorizontalGroup(
            treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, treePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, treePanelLayout.createSequentialGroup()
                        .addComponent(autoBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)))
                .addContainerGap())
        );
        treePanelLayout.setVerticalGroup(
            treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(treePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(treePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(yField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(xField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(autoBox)))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );

        previewPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                previewPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                previewPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                previewPanelMouseExited(evt);
            }
        });
        previewPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                previewPanelMouseMoved(evt);
            }
        });
        previewPanel.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        loadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Open file.png"))); // NOI18N
        loadButton.setToolTipText("Open Layout File");
        loadButton.setFocusable(false);
        loadButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(loadButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Floppy.png"))); // NOI18N
        saveButton.setToolTipText("Save Layout");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(saveButton);

        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Print.png"))); // NOI18N
        printButton.setToolTipText("Print");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(printButton);

        exportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Export text.png"))); // NOI18N
        exportButton.setToolTipText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(exportButton);

        psCalcButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Calculator.png"))); // NOI18N
        psCalcButton.setToolTipText("Postscript Calculator");
        psCalcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                psCalcButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(psCalcButton);

        previewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/Aha-24/enabled/Refresh v2.png"))); // NOI18N
        previewButton.setToolTipText("View - Refresh");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(previewButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(treePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(previewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addComponent(treePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        preview(false);
    }//GEN-LAST:event_previewButtonActionPerformed

    private PreviewPanel preview;
    private void preview(boolean save){

    //layout.setFile(current_file);
    ArrayList docElements = layout.buildLayout(save);
    
    docElements.add(this.highlight);
    
    jTree.setModel(layout.getTreeModel());
    if (docElements == null) return;
    //System.out.println("Made it past the docElements == null");
    preview = new PreviewPanel(docElements);
        /* Java translats to device points when printing? yes, lose precision */
        preview.setPreferredSize(new Dimension(612,792)); //paper size
        preview.setDoubleBuffered(false);
        previewPanel.removeAll();
        previewPanel.add(preview);
        this.validate();
}
    private void psCalcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_psCalcButtonActionPerformed
        new PointCalc(null, false).setVisible(true);
    }//GEN-LAST:event_psCalcButtonActionPerformed

    private void previewPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewPanelMouseEntered
        this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }//GEN-LAST:event_previewPanelMouseEntered

    private void previewPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewPanelMouseExited
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_previewPanelMouseExited

    private void previewPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewPanelMouseMoved
        if (autoBox.isSelected()){
            xField.setText(Integer.toString(evt.getX()));
            yField.setText(Integer.toString(evt.getY()));
        }
    }//GEN-LAST:event_previewPanelMouseMoved

    private void previewPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewPanelMouseClicked
        xField.setText(Integer.toString(evt.getX()));
        yField.setText(Integer.toString(evt.getY()));
    }//GEN-LAST:event_previewPanelMouseClicked

    private void jTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeMousePressed

        TreePath selPath = jTree.getPathForLocation(evt.getX(), evt.getY());
        if (selPath == null){
            return;
        }else{
            jTree.setSelectionPath(selPath);
        }
        if (evt.isPopupTrigger()) {

            doPopup(evt);
    }
    }//GEN-LAST:event_jTreeMousePressed

    private void doPopup(java.awt.event.MouseEvent evt){
        Object node = jTree.getLastSelectedPathComponent();
        if ((node != null) && (node instanceof TreeNode)) {
            
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;

          
            if (treeNode.getLevel() == 1){
                this.insertMenuItem.setEnabled(true);
                this.deleteMenuItem.setEnabled(false);
            }
            if (treeNode.getLevel() == 2){
               this.insertMenuItem.setEnabled(false);
               this.deleteMenuItem.setEnabled(true);
            }

            if (treeNode.toString().equalsIgnoreCase("table")){
                this.insertMenuItem.setEnabled(true);
                this.deleteMenuItem.setEnabled(false);
            }

            if (treeNode.toString().equalsIgnoreCase("column")){
               this.insertMenuItem.setEnabled(false);
                this.deleteMenuItem.setEnabled(true);
            }

            popup.show((Component)evt.getSource(), evt.getX(), evt.getY());
            
        }
            
    }

    private void jTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeMouseReleased
        TreePath selPath = jTree.getPathForLocation(evt.getX(), evt.getY());
        if (selPath == null){
            return;
        }else{
            jTree.setSelectionPath(selPath);
        }
        if (evt.isPopupTrigger()) {
            popup.show((Component)evt.getSource(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTreeMouseReleased

    private void jTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeMouseClicked
       if (jTree.getSelectionPath() == null || jTree.getSelectionPath().getParentPath() == null) return;
        if (jTree.getSelectionPath().getParentPath().getPathCount()==2){
                highlightPreview();
            }
       //jTree.getSelectionModel().
        

    }//GEN-LAST:event_jTreeMouseClicked

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        preview(false);
        ExportDialog export = new ExportDialog();
        
            export.showExportDialog( treePanel, "Export view as ...",preview,new Dimension(612, 792), "export" );

        /*Properties p = new Properties();
        p.setProperty("PageSize","A4");
        VectorGraphics g = null;
        String f = "output.pdf";
        try {
            g = new PDFGraphics2D(new File(f), new Dimension(612, 792));
        } catch (Exception ex) {
            Logger.getLogger(InvoiceLayoutManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        g.setProperties(p);
        g.startExport();
        preview.print(g);
        g.endExport();
        try {
            Desktop.getDesktop().open(new File(f));
        } catch (Exception ex) {
            //Logger.getLogger(InvoiceLayoutManager.class.getName()).log(Level.SEVERE, null, ex);
        }*/

    }//GEN-LAST:event_exportButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        preview.doPrint();
    }//GEN-LAST:event_printButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        preview(true);

    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed

       loadLayout();
       preview(false);
    }//GEN-LAST:event_loadButtonActionPerformed

    private void highlightPreview(){
    //System.out.println("hightlightPreviw CALLED");
             TreePath path = jTree.getSelectionPath();
             TreeNode node = (TreeNode)path.getLastPathComponent();
             float pos_x=0;
             float pos_y=0;
             float size_x=0;
             float size_y=0;
             highlight = new DocumentElement("highlight");
             
            for(int c = 0; c < node.getChildCount(); c++){

                TreeNode prop = node.getChildAt(c);

                if (prop.toString().equalsIgnoreCase("position")){
                    for(int p = 0; p < prop.getChildCount(); p++){
                        TreeNode pos = prop.getChildAt(p);
                        if (pos.toString().equalsIgnoreCase("x")){
                            TreeNode x = pos.getChildAt(0);
                            pos_x = DV.parseFloat(x.toString());
                            //System.out.println("pos_x: "+pos_x);

                        }
                        if (pos.toString().equalsIgnoreCase("y")){
                            TreeNode y = pos.getChildAt(0);
                            pos_y = DV.parseFloat(y.toString());
                            //System.out.println("pos_y: "+pos_y);
                        }
                    }

                    //System.out.println("found position");
                    
                }

                if (prop.toString().equalsIgnoreCase("size")){

                    for(int p = 0; p < prop.getChildCount(); p++){
                        TreeNode size = prop.getChildAt(p);
                        if (size.toString().equalsIgnoreCase("x")){
                            TreeNode x = size.getChildAt(0);
                            size_x = DV.parseFloat(x.toString());
                                //System.out.println("size_x: "+size_x);

                        }
                        if (size.toString().equalsIgnoreCase("y")){
                            TreeNode y = size.getChildAt(0);
                            size_y = DV.parseFloat(y.toString());
                            //System.out.println("size_y: "+size_y);
                        }
                    }

                    //System.out.println("found size");
                    
                }

        }
             
             highlight.setPosition(pos_x, pos_y);
             highlight. setSize(size_x, size_y);
             
             preview(false);

    }
 
    private DocumentElement highlight;

    private JPopupMenu popup;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoBox;
    private javax.swing.JButton exportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTree jTree;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton previewButton;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JButton printButton;
    private javax.swing.JButton psCalcButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel treePanel;
    private javax.swing.JTextField xField;
    private javax.swing.JTextField yField;
    // End of variables declaration//GEN-END:variables

}
 class PreviewPanel extends JPanel implements Printable {

    public PreviewPanel(ArrayList al) {
        super();
        elements = al;
        elements.trimToSize();
        //this.setSize(612, 792);
    }
    public int print(Graphics g, PageFormat pf, int pageIndex){

    if (pageIndex != 0) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D)g;
        //pf.setOrientation(PageFormat.PORTRAIT);
        //Paper paper = new Paper();
        //paper.setImageableArea(0, 0, 612, 792);
        //pf.setPaper(paper);
        //g2.translate(pf.getImageableX(), pf.getImageableY());
        paint(g2);
        return PAGE_EXISTS;
    }


    public void doPrint(){
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
        try {
            printJob.print();
        } catch (Exception prt) {
            System.err.println(prt.getMessage());
        }
        }
}
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        int elementCount = elements.size();
        //System.out.println("How many elements in the arraylist? "+elementCount);
        DocumentElement de;

        Graphics2D g2 = (Graphics2D)g;
        //g2.scale(1.04,1.04 );
        
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Float(0f,0f,612f,792f));

        for (int e = 0; e < elementCount; e++){
            de = (DocumentElement)elements.get(e);
            if (de == null) continue;

            /*Element label data */
            String text = de.getText();
            float [] text_mod = de.getTextPosition();
            String name = de.getName();

            /*Element size and position data */
            float size[] = de.getSize();
            float position[] = de.getPosition();

            if (name.equalsIgnoreCase("highlight")){
                //System.out.println("<<<<<<<<<<<<<<< Element Name: "+name);
                //BasicStroke str = (BasicStroke)g2.getStroke();
                //float pen_width = str.getLineWidth();
                //g2.setStroke(new BasicStroke(1));
                g2.setColor(new Color(0x55c4deff, true));//bluish
                g2.fill(new Rectangle2D.Float(position[0], position[1], size[0], size[1]));
                //g2.setColor(new Color(0x66ff1199));
                //g2.setStroke(new BasicStroke(pen_width));//change the stroke back
                //g2.drawRect(position[0], position[1], size[0], size[1]);
                //g2.setColor(de.getFgColor());
                continue;
            }


            /*Element Table data*/
            int row_count = de.getTable().getRow_count();
            float row_height = de.getTable().getRow_height();
            ArrayList tableColumns = de.getTable().getColumns();
            
            g2.setFont(new Font(de.getFont().getFamily(),
                    de.getFont().getStyle(),
                    (de.getFont().getSize())));

            /* Take care of background and rectangle border */
            g2.setColor(de.getBgColor());
            g2.fill(new RoundRectangle2D.Float(position[0], position[1], size[0], size[1], 1.0f,1.0f));//recangles are hardcoded still
            g2.setColor(de.getFgColor());
            if (de.getBorderThickness() > 0){
                BasicStroke str = (BasicStroke)g2.getStroke();
                float pen_width = str.getLineWidth();
                g2.setStroke(new BasicStroke(de.getBorderThickness()));
                g2.draw(new RoundRectangle2D.Float(position[0], position[1], size[0], size[1], 1,1));
                g2.setStroke(new BasicStroke(pen_width));
            }

            

            /* Table loop */
            tableColumns.trimToSize();
            float x_append = 0;
            for (int col = 0; col < tableColumns.size(); col++){
                TableColumn tc = (TableColumn)tableColumns.get(col);
                String header_title = tc.getHeader_title();
                
                float column_width = tc.getWidth(); //* 3.78 = device resolution
                float bottom_border = tc.getBot_border();
                float top_border = tc.getTop_border();
                String header_color = tc.getHead_color();
                String cell_color = tc.getCell_color();
                String even_color = tc.getAlt_color();
                int repeat_count = tc.getRep_count();
                float x_position = position[0];
                x_position += x_append; //make sure we start writing the col at the proper x
                float y_position = position[1];
                float original_x = x_position;
                float original_y = y_position;
                /* loop for repeating columns */
                for (int count = 0; count < repeat_count; count++){
                    //loop to build column repeating for row count plus one the header = 0
                    for(int row = 0; row < row_count; row++){//start with zero so we can process the header i nthis loop as well
                        /* process cell */
                        if (row % 2 != 0) g2.setColor(new Color(Integer.decode(cell_color)));
                        if (row == 0) g2.setColor(new Color(Integer.decode(header_color)));
                        if (row % 2 == 0 && row > 0){
                            g2.setColor(new Color(Integer.decode(even_color)));
                        }

                        //System.out.println((x_position+column_width));
                        g2.fill(new Rectangle2D.Float(x_position, y_position, column_width, row_height));
                        g2.setColor(de.getFgColor());
                        g2.draw(new Rectangle2D.Float(x_position, y_position, column_width, row_height));

                        if (row == 0 && header_title != null && !header_title.equalsIgnoreCase("null")){
                            //set font!!!!!!!!!! TODO
                            g2.drawString(header_title, x_position+2 , y_position + g2.getFontMetrics().getAscent());
                        }
                        y_position += row_height;//moove to next coordinates down the page
                        //System.out.println("DRAW-->y_position: "+y_position+"  repeat count"+count);
                    }
                    y_position = original_y;
                    x_position += column_width; //repeating across
                    x_append += column_width;//how much x realestate have we used
                    //System.out.println("DRAW-->X_position: "+x_position+"  repeat count"+count);
                }

            }


            if (de.getImage() != null){
                try {
                    g2.drawImage(de.getImage(),null, (int)position[0], (int)position[1]);
                    
                }catch(Exception excp){
                    
                }
            }


            if (text != null && !text.equalsIgnoreCase("null")){
                g2.setColor(de.getFgColor());//make sure color is set back to non-table values
                g2.drawString(text, text_mod[0] , text_mod[1]+g2.getFontMetrics().getAscent());
            }

            
        }

    }


private ArrayList elements;


}
