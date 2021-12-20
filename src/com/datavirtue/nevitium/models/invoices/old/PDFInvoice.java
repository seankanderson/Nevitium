/*
 * PDFInvoice.java
 *
 * Created on November 29, 2006, 12:08 PM
 *
 * 
 */
/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */

package com.datavirtue.nevitium.models.invoices.old;

import com.datavirtue.nevitium.services.PdfReportService;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfContentByte;
 

/**
 *
 * @author Sean K Anderson - Data Virtue 2006
 */
public class PDFInvoice extends PdfReportService {
    /** Creates a new instance of PDFReport */
    
    public PDFInvoice ( String file, PdfPTable header) {
        super (file);
        
        //System.out.println( header.getTotalHeight());
        
        if (header != null){ 
            
            ct.setSimpleColumn(36, 36, PageSize.LETTER.width() - 36, PageSize.LETTER.height() - header.getTotalHeight()-16, 18, Element.ALIGN_LEFT);
            this.setHeader(header);
            
        }
    }
        
    
    
    public PdfPCell getCell (String text){
        
        return new PdfPCell ();
        
        
    }
    
    
    public void setHeader (PdfPTable pdft){
        
        head = pdft;
        ct.setSimpleColumn(36, 36, PageSize.LETTER.width() - 36, PageSize.LETTER.height() - head.getTotalHeight()-16, 18, Element.ALIGN_LEFT);
        head.setTotalWidth(PageSize.LETTER.width() - 72);
    }
    
    public void setBody (PdfPTable b){ 
        
        b.setTotalWidth(PageSize.LETTER.width()-72);
        body = b;
        
        
    }
   
    
    public float getHeaderSize () {
        
        return head.getTotalHeight() + 16;
        
        
    }
    
    public float getFooterSize () {
        
        return foot.getTotalHeight() + 16;
        
        
    }
    
   public float getBodySize () {
        
        return com.lowagie.text.PageSize.LETTER.height() - (getHeaderSize() + getFooterSize());
        
        
    }
    
    
    public void writeTable (PdfPTable b){
                   
        //float hf = head.getTotalHeight() + foot.getTotalHeight()+32;  //space taken by header footer
        float bs = this.getBodySize();  //calc the size available for the body
          bs -= 10f;  //fudge
          
        java.util.ArrayList al = b.getRows();
        al.trimToSize();
        
        int rows = al.size();
        float chunk_size = 0f;
        int end_row = 0;
        int last_row = rows - 1;
        
        //System.out.println(rows);
        
        int start_row = 0;
        
        for (int n = start_row; n <= last_row; n++ ){
            
            if (b.getRowHeight(n) + chunk_size <= bs){
                
                chunk_size += b.getRowHeight(n);
                end_row = n;
                
            }      
            
        }
        boolean cycled = false;
        
        do {
            
            if (end_row == last_row  && cycled) end_row++;
            
            b.writeSelectedRows(start_row,  end_row, 36, PageSize.LETTER.height()-head.getTotalHeight()-16,
            writer.getDirectContent());
            
            if (end_row == last_row  || end_row > last_row ) break;
            
            
            start_row  = end_row;
            
            chunk_size = 0f;
            cycled = true;
            try {
                document.newPage();
            } catch (DocumentException ex) {
                System.out.println("Document exception around line 160 in PDFInvoice.java");
            }
                
            
            
            
            for (int n = start_row ; n <= last_row; n++ ){
            
                if (b.getRowHeight(n) + chunk_size <= bs){
                
                    chunk_size += b.getRowHeight(n);
                    end_row = n;
                
                }
                   
                //end_row++;
            }
            
        }while (true);
        
        
    }
    
    
    public void setFooter (PdfPTable pdft){
        
        foot = pdft;
        foot.setTotalWidth(PageSize.LETTER.width() - 72);
    }
    
    public PdfContentByte getContentByte() {
        
        return writer.getDirectContent();
        
    }
    public float howManyPages (PdfPTable pdfpt) {
        
        
        //pdfpt.setTotalWidth(PageSize.LETTER.width()-72);
        float th = pdfpt.getTotalHeight();
        //System.out.println("Body Size "+ th);
        
        float hf = head.getTotalHeight()+foot.getTotalHeight()+32;
        float bs = PageSize.LETTER.height() - hf;
        
        //System.out.println(bs / th);
        
        return th / bs;
        
    }
    
    public void build () {
            
            pages = this.howManyPages(body);
                        
            //float t = 1 - a;
            ///System.out.println("Remainder of pages % 1.0 = " + a);
        
            //if (a != 0  ) pages += a;
            if (pages <= 1) pages = 1;
            if (pages > 1){
             
                float a = pages % 1.0f;
                float r = 1 - a;
                pages += r;
                
            }
            
            //System.out.println("PAGES: "+pages);
            
        
        writeTable (body);
        
        
    }
   
    public void addTagline (String t) {
        
        tag = t;
        
        
    }
    
    
    
    /**
     *
     *
     *
     *
     **/
    
    
    
    public void onEndPage(PdfWriter writer, Document doc) {
        try {
            
            if (water_mark){
                
                writeWatermark();
                
            }
            
            Rectangle page = doc.getPageSize();
            
             PdfPTable h = new PdfPTable(head);   
             
            
            
            //System.out.println(h.getTotalHeight());
            
            
            
            h.writeSelectedRows(0, -1, 36, PageSize.LETTER.height()-16 ,
             writer.getDirectContent());
           
             //ct.setYLine(842 - 200);
             
            //page.height() - 36 + h.getTotalHeight()
            //ct.setYLine(page.height() - (h.getTotalHeight() + 36));
            //document.add(head);
            //PdfPTable foot = new PdfPTable(1);
           // for (int k = 1; k <= 6; ++k)
                
           /* if (!summary_written){
            PdfPCell footerCell = new PdfPCell (new Phrase ("Page: " + document.getPageNumber()+ "  Continued...", new Font (doc_font, font_size+2, font_style)));
            footerCell.setBorder(Rectangle.BOX);
            
            footerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            foot.addCell(footerCell);
            
            }else foot = summary;
            
            
            foot.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
            **/
            
            foot.deleteLastRow();
            
            PdfPCell cell = new PdfPCell (new Phrase(tag, getDocFont(-4,0)));
            
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            //cell.setColspan(2);
            
            cell.setGrayFill(.8f);
            
            foot.addCell(cell);//add company info
            
            cell = new PdfPCell (new Phrase("Page: " + writer.getPageNumber()+ " of " + (int)pages, getDocFont(0,0)));
            
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            //cell.setColspan(2);
            
            cell.setGrayFill(.8f);
            
            foot.addCell(cell);
            
            foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin()+foot.getTotalHeight(),
                writer.getDirectContent());
            
            
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        
        
    }
   
   private float pages;
   private String tag = " ";
   private PdfPTable head;
   private PdfPTable body;
   private PdfPTable foot;
   private boolean summary_written = false;
   
   
}
