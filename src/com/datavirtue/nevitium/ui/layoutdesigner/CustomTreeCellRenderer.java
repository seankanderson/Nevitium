/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datavirtue.nevitium.ui.layoutdesigner;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author sean
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    Icon subIcon;
    Icon pageSettingsIcon;
    Icon rootLevelIcon; 
    Icon tableNodeIcon;
    Icon barcodeNodeIcon, positionIcon, sizeIcon, dataIcon, textIcon, fontIcon, imageIcon, graphicsIcon, incIcon  ;
    ImageIcon defaultIcon = new javax.swing.ImageIcon(getClass()
            .getResource("/businessmanager/res/Aha-16/enabled/Object.png"));
    ImageIcon leafIcon = new javax.swing.ImageIcon(getClass()
            .getResource("/businessmanager/res/Aha-16/enabled/Go.png"));
    ImageIcon rootIcon = new javax.swing.ImageIcon(getClass()
            .getResource("/businessmanager/res/Aha-16/enabled/Units.png"));
    
    public CustomTreeCellRenderer(Icon rootLevel, Icon subCell, 
            Icon settingsIcon, Icon tableIcon, Icon barcodeIcon,
            Icon positionIcon, Icon sizeIcon, Icon dataIcon, Icon textIcon, Icon fontIcon,
            Icon imageIcon, Icon graphicsIcon, Icon incIcon) {
        this.rootLevelIcon = rootLevel;
        this.subIcon = subCell;
        this.pageSettingsIcon = settingsIcon;
        this.tableNodeIcon = tableIcon;
        this.barcodeNodeIcon = barcodeIcon;
        this.positionIcon = positionIcon;
        this.sizeIcon = sizeIcon;
        this.dataIcon = dataIcon;
        this.textIcon = textIcon;
        this.fontIcon = fontIcon;
        this.imageIcon = imageIcon;
        this.graphicsIcon = graphicsIcon;
        this.incIcon = incIcon;
        
    }

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        
        if (isNodeType(value,"pagesettings", 2)){
            setIcon(pageSettingsIcon);
            //setToolTipText("Page Dimensions");
            return this;
        }else if (isRootLevel(value)){
            setIcon(this.rootLevelIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value,"table", -1)){
            setIcon(this.tableNodeIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "barcode", 2)){
            setIcon(this.barcodeNodeIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "position", 3)){ //begin sub nodes 3
            setIcon(this.positionIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "size", 3)){
            setIcon(this.sizeIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "data", 3)){
            setIcon(this.dataIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "text", 3)){
            setIcon(this.textIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "font", 3)){
            setIcon(this.fontIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "image", 3)){
            setIcon(this.imageIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "graphics", 3)){
            setIcon(this.graphicsIcon);
            //setToolTipText("Document Component");
            return this;
        }else if (isNodeType(value, "include", 3)){
            setIcon(this.incIcon);
            //setToolTipText("Document Component");
            return this;
        }
        
        
        //DEFAULT TOP
        if (isSubCell(value, 2)) {
            setIcon(subIcon);
            //setToolTipText("Document Component");
            return this;
        }
        
        //DEFAULT five deep
        if (isLeaf(value)) {
            setIcon(this.leafIcon);
            setToolTipText("Double-Click to edit");
            return this;
        }
        
        if (isRoot(value)){
            setIcon(rootIcon);
            //setToolTipText("Document Component");
            return this;
        }
        
        //DEFAULT ALL
        if (isNodeType(value, "", -1)){
            setIcon(defaultIcon);
            //setToolTipText("Document Component");
            return this;
        }
        
        
        setToolTipText(null);
        return this;
    }

    protected boolean isRootLevel(Object value){
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        int nodeLevel = node.getLevel();
        if (nodeLevel==1) return true; //our particular root level
        
        return false;
    }
  protected boolean isLeaf(Object value){
      DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        if (node.isLeaf()) return true;
        return false;
  }
  protected boolean isRoot(Object value){
      DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        if (node.isRoot()) return true;
        return false;
  }
    protected boolean isSubCell(Object value, int depth) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        int nodeLevel = node.getLevel();
        if (nodeLevel==depth) return true;
        
        return false;
    }
    protected boolean isNodeType(Object value, String type, int depth){
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        int nodeLevel = node.getLevel();
        Object nodeTitle = node.getUserObject();
        String title="";
        if (nodeTitle instanceof String) title = (String)nodeTitle;
        if (type.equals("") && depth==-1) return true; //any node specification
        if (depth == -1){
            if (title.toLowerCase().contains(type)) return true;
        }else {
            if (title.toLowerCase().contains(type) &&  nodeLevel==depth) return true;
        }
        return false;
    }
    
}
