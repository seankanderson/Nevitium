/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.ui.layoutdesigner;

import com.datavirtue.nevitium.services.util.DV;
import com.datavirtue.nevitium.services.util.ExtensionFileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author dataVirtue
 */
public class DocumentLayout {
    private ArrayList document_element_list;


    /* This class takes a xml file and parses it and builds the DocumentElement from which
     requests can be made for any of the known elements for data positions. All of the elements
     can also be */
    public DocumentLayout(String filename){
        
            populateTree(new File(filename));
            this.buildLayout(false);
    }    

    public DocumentElement getElement(String element_name){
        int element_count = document_element_list.size();
        DocumentElement anElement;
        for (int e = 0; e < element_count; e++){
            anElement = (DocumentElement)document_element_list.get(e);
            if (anElement.getName().equalsIgnoreCase(element_name)){
                return anElement;
            }
        }
        return null;
    }

    public void setElementList(ArrayList e){
        document_element_list = e;
    }
    public DefaultTreeModel getTreeModel(){
        return defaultTreeModel;
    }
    public ArrayList getElements(){
        return this.document_element_list;
    }
    
    public float [] getPageSize(){
        return page_size;
    }
    
    public boolean getOrientation(){
        return orientation;
    }
    
    private float [] page_size;
    boolean orientation = true;
    
    private File current_file;
    DefaultTreeModel defaultTreeModel;

private void populateTree(File xml){
    File file = xml;
    current_file = xml;

    DefaultMutableTreeNode base = new DefaultMutableTreeNode("XML Document: "
        + file.getAbsolutePath());

    defaultTreeModel = new DefaultTreeModel(base);

    try{

        buildTree(defaultTreeModel, base, file);
    }catch(Exception e){
        e.printStackTrace();
    }
    //jTree.setModel(defaultTreeModel);
    //jTree.setEditable(true);
}

private void buildTree(DefaultTreeModel treeModel, DefaultMutableTreeNode current, File file)
      throws XMLStreamException, FileNotFoundException {
    FileInputStream inputStream = new FileInputStream(file);
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

    //addStartDocumentNodes(reader, current);

    parseRestOfDocument(reader, current);
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

        /* Check for children, if none then create node and add a blank node?? */

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
          DefaultMutableTreeNode data = new DefaultMutableTreeNode(reader.getText());

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

  public void setFile(File f){
      current_file = f;
  }

  /* this method distills the xml layout file and builds neat little DocumentElements
   which you can search through and query to build a graphical document. */
public ArrayList buildLayout(boolean save){
    /*
     Add Progress bar?  When save is true I am not getting a refresh! the arraylist being returned is not updated?
     */
    String nl = System.getProperty("line.separator");
    String f = current_file.getAbsolutePath();

    if (save){
        JFileChooser jfc = new JFileChooser(current_file);
        jfc.setSelectedFile(current_file);
        jfc.setDialogTitle("Save XML Layout File (.xml)");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter filter = new ExtensionFileFilter(".xml Files", new String[] { "xml" });
        jfc.setFileFilter(filter);
        int status = jfc.showSaveDialog(null);

        if (status == JFileChooser.CANCEL_OPTION) return null ;

        //String f = current_file.getAbsolutePath();
        File file = jfc.getSelectedFile();
        f = file.getAbsolutePath();
        f = file.getPath();
        if (!f.endsWith(".xml")){
            file = new File(file.getPath()+".xml");

        }
        current_file = file;
        //f = current_file.getAbsolutePath();

        //System.out.println(f);

        DV.writeFile(f, "<?xml version="+'"'+"1.0"+'"'+" ?>"+nl, false);
        DV.writeFile(f,"<InvoiceLayout>"+nl , true);
    }
    // Get Root Node from the model ...
    TreeNode rootNode  = (TreeNode)this.defaultTreeModel.getRoot();
    // ... and its path in the tree from the tree.
    
    this.document_element_list = new ArrayList(15);
    // ROOT LOOP
    for(int r=0; r<rootNode.getChildCount(); r++) {//only has one child
        //cycle through the first nodes off the root
        //logo, barcode, billto, shipto, etc...
        TreeNode docRoot = rootNode.getChildAt(r);
        //System.out.println(element.toString());
        //we crate a new DocElement with the name of the node "logo", etc...

        DocumentElement de = new DocumentElement("default");//un-initialized
        for (int s = 0; s <docRoot.getChildCount(); s++ ){
            TreeNode section = docRoot.getChildAt(s);
            de = new DocumentElement(section.toString());

        //now we scan for various expected child nodes
        for(int e=0; e < section.getChildCount(); e++) {
            //position, size, etc..
            DefaultMutableTreeNode property = (DefaultMutableTreeNode)section.getChildAt(e);
            //System.out.println(property.toString());

            
            
            //TEst for "page setup" node and populae DocumentLayout pages size and orientation
            //Begin Page Settings
            if(property.toString().equalsIgnoreCase("paperSize")) {
                float x=0; float y=0;

                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode leaf = property.getChildAt(l);

                    for (int a=0; a < leaf.getChildCount(); a++){
                        TreeNode attribute = leaf.getChildAt(a);

                        if (leaf.toString().equalsIgnoreCase("x")){

                            x = (float)DV.parseDouble(attribute.toString());

                        }
                        if (leaf.toString().equalsIgnoreCase("y")){

                            y = (float)DV.parseDouble(attribute.toString());
                        }
                    }

                } de.setPaperSize(new float[]{x,y});
                    this.page_size = new float[]{x,y};
                //continue;
            }//end position element loop
            
            if(property.toString().equalsIgnoreCase("portrait")) {
                String t="null";
                boolean portrait = true;

                for(int l=0; l < property.getChildCount(); l++) {
                    DefaultMutableTreeNode leaf = (DefaultMutableTreeNode)property.getChildAt(l);
                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                            t = "true";
                        }else {
                            t = leaf.toString();
                            
                            if (t.equalsIgnoreCase("true") || t.equalsIgnoreCase("yes")) portrait = true;
                            if (t.equalsIgnoreCase("false") || t.equalsIgnoreCase("no")) portrait = false;

                        }

                } de.setPortraitOrientation(portrait);
                    this.orientation = portrait;
                //continue;
            }
            //End Page Settings 
            
            

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("position")) {
                float x=0; float y=0;

                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode leaf = property.getChildAt(l);

                    for (int a=0; a < leaf.getChildCount(); a++){
                        TreeNode attribute = leaf.getChildAt(a);

                        if (leaf.toString().equalsIgnoreCase("x")){

                            x = (float)DV.parseDouble(attribute.toString());

                        }
                        if (leaf.toString().equalsIgnoreCase("y")){

                            y = (float)DV.parseDouble(attribute.toString());
                        }
                    }

                } de.setPosition(x, y);
            }//end position element loop

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("size")) {
                float x=0; float y=0;
                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode leaf = property.getChildAt(l);

                    for (int a=0; a < leaf.getChildCount(); a++){
                        TreeNode attribute = leaf.getChildAt(a);

                        if (leaf.toString().equalsIgnoreCase("x")){
                            x = (float)DV.parseDouble(attribute.toString());
                        }
                        if (leaf.toString().equalsIgnoreCase("y")){
                            y = (float)DV.parseDouble(attribute.toString());
                        }
                    }

                }de.setSize(x, y);
            }//end size element loop

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("data")) {
                float x=0; float y=0;
                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode leaf = property.getChildAt(l);

                    for (int a=0; a < leaf.getChildCount(); a++){
                        TreeNode attribute = leaf.getChildAt(a);

                        if (leaf.toString().equalsIgnoreCase("x")){
                            x = (float)DV.parseDouble(attribute.toString());
                        }
                        if (leaf.toString().equalsIgnoreCase("y")){
                            y = (float)DV.parseDouble(attribute.toString());
                        }
                    }

                }de.setDataPosition(x, y);
            }//end size element loop

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("font")) {
                String family = "tahoma"; int style = 0; int size = 12;
                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode leaf = property.getChildAt(l);

                    for (int a=0; a < leaf.getChildCount(); a++){
                        TreeNode attribute = leaf.getChildAt(a);

                        if (leaf.toString().equalsIgnoreCase("family")){
                            family = attribute.toString();
                        }
                        if (leaf.toString().equalsIgnoreCase("style")){
                            style = DV.parseInt(attribute.toString());
                            if (style < 0) style = 0;
                        }
                        if (leaf.toString().equalsIgnoreCase("points")){
                            size = DV.parseInt(attribute.toString());
                            if (size < 1) size = 12;
                        }
                    }

                }de.setFont(family, style, size);
            }//end font element loop


             /* test for each element such as position, size, font, table, text*/
            if(property.toString().toLowerCase().contains("table")) {
                //System.out.println("WE ARE IN THE TABLE BLOCK");
                //loop through table elements
                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode te = property.getChildAt(l);//get the elements of the table

                    //loop through table element children if any

                        //System.out.println("Table Element: "+te.toString());

                        if (te.toString().toLowerCase().contains("column")){
                            TableColumn column = new TableColumn();
                            //System.out.println("WE HIT THE COLUMN LOOPS");
                            //loop through column children
                            column.setColumnName(te.toString());//assign the name of the column to the document element

                            for (int col = 0; col < te.getChildCount();col++){
                                TreeNode ce = te.getChildAt(col);//column element - ce


                                if (ce.toString().equalsIgnoreCase("width")){
                                    //loop through width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setWidth(10);
                                        }else {
                                            column.setWidth((float)DV.parseDouble(leaf.toString()));

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }

                                if (ce.toString().equalsIgnoreCase("header")){
                                    //loop through header's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setHeader_title("null");
                                        }else {
                                            column.setHeader_title(leaf.toString());

                                        }

                                    }//end header col element child loop (Should be single iteration)

                                }

                                if (ce.toString().equalsIgnoreCase("arc_width")){
                                    //loop through arc width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setArc_width(0);
                                        }else {
                                            column.setArc_width((float)DV.parseDouble(leaf.toString()));

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }//end if

                                if (ce.toString().equalsIgnoreCase("arc_height")){
                                    //loop through arc width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setArc_height(0);
                                        }else {
                                            column.setArc_height((float)DV.parseDouble(leaf.toString()));

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }//end if


                                if (ce.toString().equalsIgnoreCase("head_color")){
                                    //loop through arc width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setHead_color("0xFFFFFF");
                                        }else {
                                            column.setHead_color(leaf.toString());

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }//end if

                                if (ce.toString().equalsIgnoreCase("cell_color")){
                                    //loop through arc width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setCell_color("0xFFFFFF");
                                        }else {
                                            column.setCell_color(leaf.toString());

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }//end if

                                if (ce.toString().equalsIgnoreCase("alt_color")){
                                    //loop through arc width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setAlt_color("0xFFFFFF");
                                        }else {
                                            column.setAlt_color(leaf.toString());

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }//end if

                                if (ce.toString().equalsIgnoreCase("rep_count")){
                                    //loop through arc width's children (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                            column.setRep_count(1);
                                        }else {
                                            column.setRep_count(DV.parseInt(leaf.toString()));

                                        }

                                    }//end width col element child loop (Should be single iteration)

                                }//end if

                                if (ce.toString().equalsIgnoreCase("visible")){
                                    //loop through visible's (only one)
                                    for(int w=0; w < ce.getChildCount(); w++) {
                                        TreeNode leaf = ce.getChildAt(w);
                                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null ||
                                                leaf.toString().equals("1") || leaf.toString().contains("t")
                                                || leaf.toString().equalsIgnoreCase("true")){
                                            column.setVisible(true);
                                        }else {
                                            column.setVisible(false);
                                        }
                                    }//end width col element child loop (Should be single iteration)

                                }//end if

                            }//end column child loop
                            de.getTable().addColumn(column);

                        }//end if column loops

                        if (te.toString().equalsIgnoreCase("row_height")){
                            for(int w=0; w < te.getChildCount(); w++) {
                                TreeNode leaf = te.getChildAt(w);
                                if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                    de.getTable().setRow_height(20);
                                }else {
                                    de.getTable().setRow_height((float)DV.parseDouble(leaf.toString()));
                                }
                            }//end width col element child loop (Should be single iteration)


                        }

                        if (te.toString().equalsIgnoreCase("row_count")){
                            for(int w=0; w < te.getChildCount(); w++) {
                                TreeNode leaf = te.getChildAt(w);
                                if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                                    de.getTable().setRow_count(11);
                                }else {
                                    de.getTable().setRow_count(DV.parseInt(leaf.toString()));
                                }
                            }//end width col element child loop (Should be single iteration)
                        }

                }//end table element enumeration loop

                /* set element props */

            }//end table check (if statment)

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("graphics")) {
                float border = 2; String fg_color = "0x000000"; String bg_color = "0xFFFFFF"; int opacity = 50;
                for(int l=0; l < property.getChildCount(); l++) {
                    TreeNode leaf = property.getChildAt(l);

                    for (int a=0; a < leaf.getChildCount(); a++){
                        TreeNode attribute = leaf.getChildAt(a);

                        if (leaf.toString().equalsIgnoreCase("border")){
                            border = (float)DV.parseDouble(attribute.toString());
                        }
                        if (leaf.toString().equalsIgnoreCase("fg_color")){

                            fg_color = attribute.toString();
                            //if not hex color value insert default
                        }
                        if (leaf.toString().equalsIgnoreCase("bg_color")){
                            bg_color = attribute.toString();

                            //if not hex color value insert default
                        }
                        if (leaf.toString().equalsIgnoreCase("opacity")){
                            opacity = DV.parseInt(attribute.toString());
                            if (opacity > 100 || opacity < 1) opacity = 50;
                        }
                    }//end attribute loop
                }//end leaf enumeration loop


                de.setBorderThickness(border);
                de.setColors(fg_color, bg_color);
                de.setOpacity(opacity);

            }//end graphics element loop

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("image")) {
                String t="null";

                for(int l=0; l < property.getChildCount(); l++) {
                    DefaultMutableTreeNode leaf = (DefaultMutableTreeNode)property.getChildAt(l);
                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                            t = "null";
                        }else t = leaf.toString();

                } de.setImage(t);
            }//end image element loop

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("include")) {
                String t="null";
                boolean include = true;

                for(int l=0; l < property.getChildCount(); l++) {
                    DefaultMutableTreeNode leaf = (DefaultMutableTreeNode)property.getChildAt(l);
                        if (leaf.toString().trim().length() < 1 || leaf.toString()==null){
                            t = "true";
                        }else {
                            t = leaf.toString();
                            
                            if (t.equalsIgnoreCase("true") || t.equalsIgnoreCase("yes")) include = true;
                            if (t.equalsIgnoreCase("false") || t.equalsIgnoreCase("no")) include = false;

                        }

                } de.setInclude(include);
            }//end include element loop

            /* test for each element such as position, size, font*/
            if(property.toString().equalsIgnoreCase("text")) {
                String t="null";
                float [] text_position = new float[2];

                for(int l=0; l < property.getChildCount(); l++) {
                    DefaultMutableTreeNode leaf = (DefaultMutableTreeNode)property.getChildAt(l);
                        TreeNode attribute;
                        for (int L = 0; L < leaf.getChildCount(); L++){
                            attribute = leaf.getChildAt(L);
                            if (leaf.toString().equalsIgnoreCase("string")){
                                if (attribute.toString().trim().length() < 1 || attribute.toString()==null){
                                    t = "null";
                                }else t = attribute.toString();
                            }
                            if (leaf.toString().equalsIgnoreCase("x")){
                                text_position[0] = (float)(float)DV.parseDouble(attribute.toString());
                            }
                            if (leaf.toString().equalsIgnoreCase("y")){
                                text_position[1] = (float)(float)DV.parseDouble(attribute.toString());
                            }

                        }

                } de.setText(t); de.setTextXY(text_position[0], text_position[1]);
            }//end text element loop


        }//end element property enumeration loop  - position, size, font, text, table... (properties (children) of each section)
        if (save) DV.writeFile(f,de.printElement(false), true);
        document_element_list.add(de);
        //System.out.println("DocumentLayout element"+de.getName());
        }//end section enumeration loop  - logo, docName, etc... (these fall under the main doc tag)

        if (save) DV.writeFile(f, "</InvoiceLayout>"+nl, true);
    }//end doc tag enumeration (only one!)

    //if (!save) elementList.add(highlight);
    //if (save) this.populateTree(current_file);

    return this.document_element_list; //returns the list of elements for InvoicePrintPanel and any other methods
}



}
