/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.ui.layoutdesigner;

import com.datavirtue.nevitium.ui.util.PrintPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DateFormat;
import java.util.ArrayList;
import javax.swing.JPanel;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.output.OutputException;

/**
 *
 * @author dataVirtue
 */
 public class InvoicePrintPanel extends PrintPanel implements Printable {

private ArrayList elements;
private int start, end;
private DocumentLayout docLayout;
private float [] page_size;
private double viewScale=1.0;

public InvoicePrintPanel(DocumentLayout dl, int start, int end, double scale) {
        super();
        elements = dl.getElements();
        docLayout = dl;
        //System.out.println("Invoice Print Panel Constructor # of elements: "+elements.size());
        elements.trimToSize();
        
        this.viewScale = scale;
        this.start = start;
        this.end = end;
        
    }

public float [] getPageSize(){
    return page_size;
}
public void setDocumentScale(double s){
    viewScale = s;
}
public double getScale(){
    return viewScale;
}
public int print(Graphics g, PageFormat pf, int pageIndex){

        //if (pageIndex != 0) return Printable.NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D)g;
        paint(g2);
        return (PAGE_EXISTS);
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
    private void setGraphicsFont(Graphics2D g2d, DocumentElement de){        
                    g2d.setFont(new Font(de.getFont().getFamily(),
                    de.getFont().getStyle(),
                    (de.getFont().getSize())));            
        
    }
    
    public void paint(Graphics g){
//        super.paintComponent(g);
//        int elementCount = elements.size();
//        //System.out.println("Invoice Print Panel # of elements: "+elementCount);
//        float [] size, position;
//        float opacity = 100;
//        //System.out.println("How many elements in the arraylist? "+elementCount);
//        DocumentElement de;
//
//        Graphics2D g2 = (Graphics2D)g;
//        
//        g2.scale(viewScale, viewScale);
//                        
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, // Anti-alias the text only optimized for LCD
//        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//        
//        g2.setColor(Color.WHITE);
//        g2.fill(new Rectangle2D.Float(0f,0f, docLayout.getElement("pageSettings").getPaperSize()[0],
//                docLayout.getElement("pageSettings").getPaperSize()[1]));
//        //var inistialization for main loop
//        String text;
//        float [] text_pos;
//        int row_count;
//        float row_height;
//        float pen_width;
//        float x_append;
//        float column_width;
//        float bottom_border;
//        float top_border;
//        String header_color;
//        String cell_color;
//        String even_color;
//        int repeat_count;
//        float x_position;
//        float y_position;
//        float original_y;
//        TableColumn tc;
//        String header_title;
//        float y_jump;
//        
//        String element_name="";
//        boolean print_barcode_label = true;
//        /* This loop detects and processes each element */
//        for (int e = 0; e < elementCount; e++){
//            de = (DocumentElement)elements.get(e);
//            if (de == null) continue;
//            if (!de.isInclude()) continue;
//            element_name =  de.getName();
//            
//            //set Font
//            this.setGraphicsFont(g2, de);
//            
//            /*Element label data */
//            text = de.getText();
//            text_pos = de.getTextPosition();
//                        
//            //Build barcode and exit -- Should I exit at this point?
//            if (element_name.toLowerCase().contains("barcode")){
//                
//                if (text.toLowerCase().contains("%no label%")){
//                        print_barcode_label = false;
//                    }            
//                if (text.toLowerCase().contains("%invoice number%")){
//                    text = invoice.getInvoiceNumber();
//                }
//                
//                try {
//                     
//                    Barcode barcode=BarcodeFactory.create3of9(text, false);
//                    if (element_name.toLowerCase().contains("128")){
//                        barcode = BarcodeFactory.createCode128(text);
//                    }
//                    if (element_name.toLowerCase().contains("USPS")){
//                        barcode = BarcodeFactory.createUSPS(text);
//                    }
//                    if (element_name.toLowerCase().contains("3of9")){
//                        barcode = BarcodeFactory.createCode39(text, false);
//                    }
//                    if (print_barcode_label==false){
//                        barcode.setLabel(" ");
//                    }
//                    barcode.setBarWidth(1);                    
//                    barcode.draw(g2, (int)text_pos[0], (int)text_pos[1]);
//                }catch(BarcodeException excep){
//                    continue;
//                }catch(OutputException oexc){
//                    continue;
//                }
//                continue;
//            }
//            
//            
//            /*Element size and position data */
//            size = de.getSize();
//            position = de.getPosition();
//            opacity = (de.getOpacity() * .01f);
//
//            /*Element Table data*/
//            row_count = de.getTable().getRow_count();
//            row_height = de.getTable().getRow_height();
//            ArrayList tableColumns = de.getTable().getColumns();
//
//                       
//            g2.setPaint(de.getBgColor());
//            g2.fill(new RoundRectangle2D.Float(position[0], position[1], size[0], size[1], 1.0f,1.0f));//recangles are hardcoded still
//            g2.setColor(de.getFgColor());
//            if (de.getBorderThickness() > 0){
//                BasicStroke str = (BasicStroke)g2.getStroke();
//                pen_width = str.getLineWidth();
//                g2.setStroke(new BasicStroke(de.getBorderThickness()));
//                g2.draw(new RoundRectangle2D.Float(position[0], position[1], size[0], size[1], 1,1));
//                g2.setStroke(new BasicStroke(pen_width));
//            }
//
//            /* Table loop */
//            tableColumns.trimToSize();
//            x_append = 0;
//            for (int col = 0; col < tableColumns.size(); col++){
//                tc = (TableColumn)tableColumns.get(col);
//                header_title = tc.getHeader_title();
//
//                column_width = tc.getWidth(); 
//                bottom_border = tc.getBot_border();
//                top_border = tc.getTop_border();
//                header_color = tc.getHead_color();
//                cell_color = tc.getCell_color();
//                even_color = tc.getAlt_color();
//                repeat_count = tc.getRep_count();
//                x_position = position[0];
//                x_position += x_append; //make sure we start writing the col at the proper x
//                y_position = position[1];
//                original_y = y_position;
//                
//                /* loop for repeating columns */
//                for (int count = 0; count < repeat_count; count++){
//                    //loop to build column repeating for row count plus one the header = 0
//                    for(int row = 0; row < row_count; row++){//start with zero so we can process the header i nthis loop as well
//                        /* process cell */
//                        if (row % 2 != 0) g2.setColor(new Color(Integer.decode(cell_color)));
//                        if (row == 0) g2.setColor(new Color(Integer.decode(header_color)));
//                        if (row % 2 == 0 && row > 0){
//                            g2.setColor(new Color(Integer.decode(even_color)));
//                        }
//                        
//                        g2.fill(new Rectangle2D.Float(x_position, y_position, column_width, row_height));
//                        g2.setColor(de.getFgColor());
//                        /* Manually draw table cell based on top-bottom border settings */
//                        //draw left border
//                        g2.draw(new Line2D.Float(x_position, y_position, x_position, y_position+row_height));
//                        //draw right border
//                        g2.draw(new Line2D.Float(x_position+column_width, y_position, x_position+column_width, y_position+row_height));
//                        //draw top border
//                        if (top_border > 0) g2.draw(new Line2D.Float(x_position, y_position, x_position+column_width, y_position));
//                        //draw bottom border
//                        if (bottom_border > 0) g2.draw(new Line2D.Float(x_position, y_position+row_height, x_position+column_width, y_position+row_height));
//                        
//                        if (row == 0 && header_title != null && !header_title.equalsIgnoreCase("null")){
//                            
//                            g2.drawString(header_title, x_position+2 , y_position + g2.getFontMetrics().getAscent());
//                        }
//                        y_position += row_height;//moove to next coordinates down the page
//                        
//                    }
//                    y_position = original_y;
//                    x_position += column_width; //repeating across
//                    x_append += column_width;//how much x realestate have we used?
//                    
//                }
//
//            }//END TABLE LOOP
//
//            if (de.getImage() != null){
//                
//                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//                
//                try {
//                    
//                    g2.drawImage(de.getImage(), (int)position[0], (int)position[1], (int)(position[0]+size[0]), (int)(position[1]+size[1]), null);
//                    
//                }catch(Exception ex){
//                    //ex.printStackTrace();
//                }
//            }
//            if (text != null && !text.equalsIgnoreCase("null")){
//                g2.setColor(de.getFgColor());//make sure color is set back to non-table values
//                g2.drawString(text, text_pos[0] , text_pos[1]+g2.getFontMetrics().getAscent()); //add font ascent and draw
//            }
//        }
//
//        
//       /*  Begin: Invoice Items */
//        Object [] currentItem;
//        DocumentElementTable tbl = (DocumentElementTable)docLayout.getElement("itemTable").getTable();
//        DocumentElement tbl_de = (DocumentElement)docLayout.getElement("itemTable");
//        this.setGraphicsFont(g2, tbl_de);
//        y_jump = tbl.getRow_height();
//        ArrayList cols = tbl.getColumns();
//        cols.trimToSize();
//        float [] data_pos = tbl_de.getDataPosition();
//        float x_temp = data_pos[0];
//        float y_temp;
//        
//        String data = "x";
//        if (end > invoice.getItemCount()) end = invoice.getItemCount();
//
//        for (int r = start; r <=end; r++){
//            
//                currentItem = invoice.getItem(r);
//
//            for (int c = 0; c < cols.size(); c++){
//                tc = (TableColumn)cols.get(c);
//
//                tc.getColumnName();
//                //if name=? then
//                if (tc.getColumnName().equals("qtyColumn")) data = Float.toString((Float)currentItem[2]);
//                if (tc.getColumnName().equals("codeColumn")) data = (String)currentItem[3];
//                if (tc.getColumnName().equals("descColumn")) data = (String)currentItem[4];
//                if (tc.getColumnName().equals("unitColumn")) data = DV.money((Float)currentItem[5]);
//                if (tc.getColumnName().equals("t1Column")){
//                    data = " ";
//                    if ((Boolean)currentItem[6]) data = "*";
//                }
//                if (tc.getColumnName().equals("t2Column")){
//                    data = " ";
//                    if ((Boolean)currentItem[7]) data = "*";
//                }
//                if (tc.getColumnName().equals("totalColumn")) data = DV.money(((Float)currentItem[2] * (Float)currentItem[5]));
//                //if not GST and taxable, add tax to totals
//
//                if (tc.isVisible()){
//                    g2.drawString(data, x_temp, data_pos[1]);//get item data - why is ascent calced?
//                }
//                x_temp += tc.getWidth();
//
//            }data_pos[1] += y_jump;
//            x_temp = data_pos[0];
//       }/* End: Invoice Items */
//
//
//        
//        /* Begin: Bill To */
//        ArrayList str;
//        de = docLayout.getElement("billTo");
//        data_pos = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        str = DV.getLines(invoice.getCustomer());
//        x_temp = data_pos[0];
//        y_temp = data_pos[1];
//        FontMetrics metrics = g2.getFontMetrics(g2.getFont());
//        
//        // get the height of a line of text in this font and render context
//        y_jump = metrics.getHeight() + metrics.getDescent();
//
//        for (int i = 0 ; i < str.size(); i++){
//            if (((String)str.get(i)).equals("")) continue;
//            g2.drawString((String)str.get(i), x_temp, y_temp + g2.getFontMetrics().getAscent());
//            y_temp+=y_jump;
//        }
//        /* End: Bill To */
//
//        /* Begin: Ship To */
//        de = docLayout.getElement("shipTo");
//        data_pos = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        str = DV.getLines(invoice.getShipToAddress());
//        x_temp = data_pos[0];
//        y_temp = data_pos[1];
//        //FontMetrics metrics = g2.getFontMetrics(g2.getFont());
//
//        // get the height of a line of text in this font and render context
//        y_jump = metrics.getHeight() + metrics.getDescent();
//
//        for (int i = 0 ; i < str.size(); i++){
//            if (((String)str.get(i)).equals("")) continue;
//            g2.drawString((String)str.get(i), x_temp, y_temp);
//            y_temp+=y_jump;
//        }
//        /* End: Bill To */
//
//               
//        /* print date */
//        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//        de = docLayout.getElement("date");      
//        data_pos = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = data_pos[0];
//        y_temp = data_pos[1];
//        y_temp+= y_jump;
//        g2.drawString(df.format(invoice.getDate()), x_temp, y_temp);
//        
//        /* print invoice number */
//        de = docLayout.getElement("docNumber");
//        data_pos = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = data_pos[0];
//        y_temp = data_pos[1];
//        y_temp+= y_jump;
//        g2.drawString(invoice.getInvoiceNumber(), x_temp, y_temp);
//        
//        /* print message */
//        data_pos = docLayout.getElement("messageBox").getDataPosition();
//        x_temp = data_pos[0];
//        y_temp = data_pos[1];
//        size = docLayout.getElement("messageBox").getSize();
//        position = docLayout.getElement("messageBox").getPosition();
//        int margin = (((int)x_temp)-((int)position[0]));
//        
//        this.drawText(g2, invoice.getMessage(), g2.getFont(), (int)x_temp, (int)y_temp, (((int)size[0])-margin));
//        /* print summary -- I switched to using position variable????? */
//        
//        de = docLayout.getElement("subtotal");
//        position = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = justifyText(position[0], DV.money(invoice.getItemTotal()), metrics);
//        g2.drawString(DV.money(invoice.getItemTotal()), x_temp, position[1]);
//        
//        
//        de = docLayout.getElement("tax1");
//        position = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = justifyText(position[0], DV.money(invoice.getTax1Total()), metrics);
//        g2.drawString(DV.money(invoice.getTax1Total()), x_temp, position[1]);
//        
//        de = docLayout.getElement("tax2");
//        position = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = justifyText(position[0], DV.money(invoice.getTax2Total()), metrics);
//        g2.drawString(DV.money(invoice.getTax2Total()), x_temp, position[1]);
//        
//        de = docLayout.getElement("total");
//        position = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = justifyText(position[0], DV.money(invoice.getInvoiceTotal()), metrics);
//        g2.drawString(DV.money(invoice.getInvoiceTotal()), x_temp, position[1]);
//        
//        de = docLayout.getElement("payment");
//        position = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = justifyText(position[0], DV.money(invoice.getTotalPayments()), metrics);
//        g2.drawString(DV.money(invoice.getTotalPayments()), x_temp, position[1]);
//        
//        de = docLayout.getElement("balanceDue");
//        position = de.getDataPosition();
//        this.setGraphicsFont(g2, de);
//        x_temp = justifyText(position[0], DV.money(invoice.getInvoiceDueNow()), metrics);
//        g2.drawString(DV.money(invoice.getInvoiceDueNow()), x_temp, position[1]);
//                        
        /* print tag address */
        /* print page of page*/
      
        //After the doc is built add this to printableBook.

    }
 
    private float justifyText(float x_max, String t, FontMetrics metrics){
        
        return x_max - metrics.stringWidth(t);
    }

    /* Custom word wrapping method from: http://tech.javayogi.com/blogs/blog4.php */
    private void drawText(Graphics2D g, String text, Font font, int x, int y, int width) {
        // prepare graphics
        
        FontRenderContext frc = g.getFontRenderContext();

        // prepare font calculations
        AttributedString styledText = new AttributedString(text);
        styledText.addAttribute(TextAttribute.FONT, font);
        AttributedCharacterIterator styledTextIterator = styledText.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(styledTextIterator, frc);

        // draw
        while (measurer.getPosition() < text.length()) {
            TextLayout textLayout = measurer.nextLayout(width);
            y += textLayout.getAscent();
            textLayout.draw(g, x, y);
            y += textLayout.getDescent() + textLayout.getLeading();
        }
    }

}
