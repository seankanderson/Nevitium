/*
 * PDFReport.java
 *
 * Created on November 26, 2006, 10:30 AM
 *
 * 
 */

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */

package com.datavirtue.nevitium.services;

import datavirtue.*;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
//import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfContentByte;
 
import com.lowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.net.MalformedURLException;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sean K Anderson - Data Virtue 2006
 */
public class PdfReportService extends PdfPageEventHelper{
    
    /** Creates a new instance of PDFReport */
    public PdfReportService(String filename, String orientation) {

            if (orientation.toLowerCase().equals("portrait")){
            document = new Document(portrait);
            W = 612f;  H = 792f;
        }
        if (orientation.toLowerCase().equals("landscape")){
            document = new Document(landscape);
            W = 792f;  H = 612f;
        }

            try {
                
                    
                writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            
            } catch (Exception ex) {
            
                
               ex.printStackTrace();
            
            }
            writer.setPageEvent(this);
            
            
            document.addAuthor("Data Virtue");
            document.addCreationDate();
            document.addSubject("Nevitium Report");
            document.open();
            
            cb = writer.getDirectContent();
            ct = new ColumnText(cb); 
            ct.setSimpleColumn(36, 36, W - 36, H - 36, 18, Element.ALIGN_LEFT);
            
        
    }
protected float W = 0f;
protected float H = 0f;

    /* Default portrait */
    public PdfReportService(String filename) {

        W = 612f;  H = 792f;
            try {


                writer = PdfWriter.getInstance(document, new FileOutputStream(filename));

            } catch (Exception ex) {


               ex.printStackTrace();

            }
            writer.setPageEvent(this);


            document.addAuthor("Data Virtue");
            document.addCreationDate();
            document.addSubject("Nevitium Report");
            document.open();

            cb = writer.getDirectContent();
            ct = new ColumnText(cb);
            ct.setSimpleColumn(36, 36, W - 36, H - 36, 18, Element.ALIGN_LEFT);


    }

    
    
        
    public void addImage (String fileURL) {
        try {
             
            Image img = Image.getInstance(fileURL) ;
           
            try {
                
                document.add (img);
                
               
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
            
        } catch (BadElementException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
       
        
    }
    
    public void addVLine (float inches) {
        
        float length = inches * 72;
        
        
        
        
    }
    
    public void addParagraph (Paragraph p, boolean keepTogether) {
        
        
        try {
            
            p.setKeepTogether(keepTogether);
            document.add(p);
            
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        
        
    }
    
    
    public void setHeader(String t){

        header = new Paragraph(t, new Font(doc_font, font_size, font_style) );

    }
    
    private int any = 0;
    public void addParagraph (String text, boolean keepTogether) {
        
        if (store_text) raw_text += text;
        
        
        try {
             
            //System.out.println (text);
            
            Paragraph p = new Paragraph(text, new Font(doc_font, font_size, font_style) );
                      
            //p.setKeepTogether(keepTogether);  
            
            ColumnText tst = ColumnText.duplicate(ct);
            
            
            tst.addElement(p);
            
            pos = ct.getYLine();
            
            //if (pos > 800) pos -= 116;
                //System.out.println (pos);
            
            
            
            
            status = tst.go(true);
            
           
            if (!ColumnText.hasMoreText(status)) {
                if (any == 0 ) ct.addElement(header);
                any++;
                ct.addElement(p);  //add the paragraph
                ct.setYLine(pos);  //tell the ct where to write on itself
                ct.go(false);       //write it
            }
            else {
                document.newPage();
                ct.addElement(header);
                ct.addElement(p);
                ct.setYLine(H - 36);
                ct.go();
            }


            
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        
     
        
    }
    
    
    public void newPage () {
        try {
            document.newPage();
        } catch (DocumentException ex) {
            System.out.println("Document exception around line 195 in PDFReport.java");
        }
            
       
    }
    
    public void finish () {
        
        document.close();
        //document = null;
    }
    
    
    
    public Chunk getChunk (String text, int size_adj){
        int fs = font_size;
        int fstyle = font_style;
        
        if (size_adj > 10 ) fstyle = Font.BOLD;
        else if (size_adj > 0 || 2 * size_adj < 0) fs += size_adj;
                
        return new Chunk (text, new Font (doc_font, fs, fstyle));
        
        
    }
    
    
    public Phrase getPhrase (String text){
        
        return new Phrase (0, text, new Font (doc_font, font_size, font_style) );
        
        
        
    }
    
    
    public String getRawText () {
        
        
        return raw_text;
        
    }
   
   public Font getDocFont (int si, int style) {
       
       if (style == 0) style = font_style;
       
       return new Font (doc_font, font_size + si, style);
       
   }
    
    public float getPageWidth () {
       
       Rectangle r = document.getPageSize();
       return r.width();
       
       
   }
    
   
   public void setFont (String font) {
        
        if (font.equalsIgnoreCase("roman")) doc_font = Font.TIMES_ROMAN;
        if (font.equalsIgnoreCase("courier")) doc_font = Font.COURIER;
        if (font.equalsIgnoreCase("helv")) doc_font = Font.HELVETICA;
        if (font.equalsIgnoreCase("zapf")) doc_font = Font.ZAPFDINGBATS;
        if (font.equalsIgnoreCase("symbol")) doc_font = Font.SYMBOL;
        
        
    }
    
    public void setFontStyle (String style) {
        
        if (style.equalsIgnoreCase("normal")) font_style = Font.NORMAL;
        if (style.equalsIgnoreCase("bold")) font_style = Font.BOLD;
        if (style.equalsIgnoreCase("italic")) font_style = Font.ITALIC;
        if (style.equalsIgnoreCase("underline")) font_style = Font.UNDERLINE;
        if (style.equalsIgnoreCase("strike")) font_style = Font.STRIKETHRU;
                
    }
    
    public void setFontSize (int a) {
        
        font_size = a;
        
        
    }
    
    
        
    /**
     * Use this to toggle text storage so that all the 
     * paragraphs passed in will not be compiled into one string
     */
    public void setStoreText (boolean s){
        
        store_text = s;
        
    }
    
    public void onEndPage(PdfWriter writer, Document document) {
        try {
            
            if (water_mark){
                
                writeWatermark();
                
            }
            
            
            Rectangle page = document.getPageSize();
            PdfPTable head = new PdfPTable(1);
            //for (int k = 1; k <= 6; ++k)
            
            PdfPCell headerCell = new PdfPCell (new Phrase (report_name , new Font (doc_font, font_size+2, font_style)));
                headerCell.setBorder(Rectangle.NO_BORDER);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                head.addCell(headerCell);
                
            head.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
            head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(),
                writer.getDirectContent());
            PdfPTable foot = new PdfPTable(1);
           // for (int k = 1; k <= 6; ++k)
                
            
            PdfPCell footerCell = new PdfPCell (new Phrase ("Page: " + document.getPageNumber(), new Font (doc_font, font_size+2, font_style)));
            footerCell.setBorder(Rectangle.NO_BORDER);
            
            footerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            foot.addCell(footerCell);
            
            foot.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
            foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(),
                writer.getDirectContent());
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
        
    public void writeWatermark (){
        
        try {
                
                Image image = Image.getInstance(watermark);
                image.setAbsolutePosition(160, 300);
                image.setRotationDegrees(45);
                
                
                BaseFont font = BaseFont.createFont(BaseFont.COURIER,
                BaseFont.WINANSI, BaseFont.EMBEDDED);
               
                
                PdfGState gstate = new PdfGState();
                gstate.setFillOpacity(0.1f);
                gstate.setStrokeOpacity(0.1f  );  
            
                PdfContentByte contentunder = writer.getDirectContentUnder();
                contentunder.saveState();
                contentunder.setGState(gstate);
                contentunder.addImage(image);
                        
                        /*,
                image.width() * 1, 0, 0, image.height() * 1, 120, 650);
                */
                         
                /*contentunder.setColorFill(Color.blue);
                contentunder.beginText();
                
                contentunder.setFontAndSize(font, 48);
                contentunder.showTextAligned(Element.ALIGN_CENTER,
                "P a g e  " + writer.getPageNumber(),
                document.getPageSize().width() / 2,
                document.getPageSize().height() / 2, 45);
                
                contentunder.endText();
                */
                 contentunder.restoreState();
                
                } catch (DocumentException e) {
                e.printStackTrace();
                }catch (IOException e) {
                e.printStackTrace();
                }
        
    }
    
    public void setWatermarkEnabled (boolean tf){
        
        water_mark = tf;
        
    }
    
    public void setWatermark (String file) {
        
        watermark = file;
        
    }
    
    public void setReportTitle (String title){
        
        report_name = title;
       
    }
    
    public void blastText () {
        
        
        System.out.println(raw_text);
        
    }    
     
    public int getPageNumber() {
        
       return document.getPageNumber();
        
    }



    protected Rectangle portrait = new Rectangle(612f, 792f);
    protected Rectangle landscape = new Rectangle(792f, 612f);
    protected Document document = new Document(portrait);

    
    
    protected ColumnText ct;
    protected PdfContentByte cb;
    protected PdfWriter writer;
    
    protected float pos;
    
    protected int status = ColumnText.START_COLUMN;


    protected String report_name = "";
    
    protected String raw_text = "";
    
    protected int doc_font = Font.COURIER;
    protected int font_size = 10;
    protected int font_style = Font.NORMAL;
    
    protected boolean store_text = false;
    protected boolean water_mark = true;
    
    protected String watermark = "";
    private String nl = System.getProperty("line.separator");
    protected Paragraph header  = new Paragraph(" ", new Font(doc_font, font_size, font_style));
    
    
}



