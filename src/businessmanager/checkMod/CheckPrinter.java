/*
 * CheckPrinter.java
 *
 * Created on July 13, 2007, 1:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package businessmanager.checkMod;
import datavirtue.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.text.AttributedString;
import java.util.Vector;
import java.awt.image.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.print.Paper;
import javax.swing.*;
import java.awt.Toolkit;

/**
 *
 * @author Data Virtue
 */
public class CheckPrinter {
    
    private boolean signature;
    private boolean printNumber;
    
    private PageFormat pgFormat = new PageFormat();

    private Book book = new Book();

    private Paper p;

    private boolean printAddress = false;
    private boolean prompt = true;
    
    private int W;
	private int H;
    
        public boolean canPrintSig() {
            
            return signature;
            
        }
        
    /** Creates a new instance of CheckPrinter */
    public CheckPrinter(boolean printSignature, boolean printCheckNumber, boolean printAddress, boolean prompt) {
    
        signature = printSignature;
        printNumber = printCheckNumber;
        
        this.prompt = prompt;
      
        this.printAddress = printAddress;
        
        p = new Paper();

        p.setSize(W = 612, H = 792);  //8.5" x 11"

	//p.setImageableArea(20, 20, W-20 ,H-20);  //half inch margins

        p.setImageableArea(0, 0, W ,H);  //no margins
        
        pgFormat.setPaper(p);

        
    }
    
    private java.awt.Font docFont = new Font("helvetica", Font.PLAIN, 12); //thisis the font for number, amount and date
    private java.awt.Font toFont = new Font("helvetica", Font.PLAIN, 10); // this is the font for the payee and amount spelling
    
    public void setDocFont (java.awt.Font f) {
        
        if (f != null) docFont = f;
        
    }
    public void setPayeeFont (java.awt.Font f) {
        
        if (f != null) toFont = f;
        
    }
    
    private int date_x = 490 ;
    private int date_y = 65;
    
    public void setDateDim(int x, int y){
        
        date_x = x;
        date_y = y;
        
    }
    public Dimension getDateDim() {
        
        return new Dimension(date_y, date_x);
    }

    private int num_x = 500;
    private int num_y = 36;
    
    public void setNumberDim(int x, int y){
        
        num_x = x;
        num_y = y;
        
    }
    public Dimension getNumberDim() {
        
        return new Dimension(num_y, num_x);
    }
    
    private int payTo_x = 65;
    private int payTo_y = 90;
    
    public void setPayToDim(int x, int y){
        
        payTo_x = x;
        payTo_y = y;
        
    }
    public Dimension getPayToDim() {
        
        return new Dimension(payTo_y, payTo_x);
    }
    
    private int amount_x = 495;
    private int amount_y = 105;//heap
    
    public void setAmountDim(int x, int y){//stack
        
        amount_x = x;
        amount_y = y;//stack to heap
                
    }
    public Dimension getAmountDim() {
        
        return new Dimension(amount_y, amount_x);
    }
    
    private int spell_x = 26;
    private int spell_y = 142;
    
    public void setSpellDim(int x, int y){
        
        spell_x = x;
        spell_y = y;
        
    }
    public Dimension getSpellDim() {
        
        return new Dimension(spell_y, spell_x);
    }
    
    private int memo_x = 51;
    private int memo_y = 206;
    
    public void setMemoDim(int x, int y){
        
        memo_x = x;
        memo_y = y;
                
    }
    public Dimension getMemoDim() {
        
        return new Dimension(memo_y, memo_x);
    }
    
    private int signature_x = 380;
    private int signature_y = 145;
    private int stub_y;
    
    
    public void setSigDim(int x, int y){
        
        signature_x = x;
        signature_y = y;
        
    }
    
    public Dimension getSigDim() {
        
        return new Dimension(signature_y, signature_x);
    }
    
    public void setStubDim(int y){
        
        stub_y = y;
        
    }
    
    public Dimension getStubDim() {
        
        return new Dimension(stub_y, 20);
    }
    
   
    private String imagePath = "";
    
    public String getSignatureImage() {
        
        return imagePath;
        
    }
    
    public void setSignatureImage(String path) {
        
        imagePath = path;
        
    }
    
    private java.util.ArrayList checkStubs;
    
    public void add(CheckStub chk){
        
        //checkStubs.add(chk);
        book.append(new CheckPrinterPage(chk, docFont, toFont, this, printAddress, printNumber, signature), pgFormat);
        
    }
    
    public void go(){
        
           /* Print the Book */
        PrinterJob printJob = PrinterJob.getPrinterJob();

        printJob.setPageable(book);  //contains all pgFormats

        
        boolean doJob = true;

        if (prompt){

            doJob = printJob.printDialog();

        }

        if (doJob) {

            try {


                printJob.print();

             }catch (Exception PrintException) {

                PrintException.printStackTrace();

             }

        }

        
    }
    
        
    
}


class CheckPrinterPage implements Printable {  

    /** Creates a new instance of DymoLabel */
    public CheckPrinterPage(CheckStub stub, Font docFont, Font toFont, 
            CheckPrinter chkprn, boolean printAddress, boolean chkNum, 
            boolean signature) {

        this.docFont = docFont;
        this.toFont = toFont;
        this.cp = chkprn;
        chk = stub;
        this.chkNum = chkNum;
        this.signature = signature;
        this.printAddress = printAddress;
        
    }

    private CheckPrinter cp;
    private CheckStub chk;
    private Font docFont;
    private Font toFont;

    private boolean printAddress;
    private boolean chkNum;
    private boolean signature;
    
    public int print(Graphics g, PageFormat pageFormat, int page) {

        EnglishDecimalFormat f = new EnglishDecimalFormat();
        
      //--- Create the Graphics2D object
      Graphics2D g2d = (Graphics2D) g;
      
      //1 centimeter = 28.3464567 PostScript points  OUCH!
            
      //--- Set the default drawing color to black
      g2d.setPaint(Color.black);

      //Font font = new Font("helvetica", Font.PLAIN, 12);

      g2d.setFont(docFont);

      g2d.setClip(null);
      
      //g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
     
      g2d.drawString (chk.getDate(), cp.getDateDim().height , cp.getDateDim().width);

      if (chkNum) g2d.drawString (chk.getNumber(), cp.getNumberDim().height , cp.getNumberDim().width);
           
      g2d.drawString (chk.getAmount(), cp.getAmountDim().height , cp.getAmountDim().width);
       
      g2d.drawString (f.convertDollars(Float.parseFloat(chk.getAmount())), cp.getSpellDim().height , cp.getSpellDim().width);
      
      g2d.drawString(chk.getMemo(), cp.getMemoDim().height, cp.getMemoDim().width);
      
      //payee stub
      g2d.drawString(chk.getMemo()+DV.addSpace("", 40, ' ')+chk.getAmount(), 36,324);
 
      
      
      //keeper stub
      g2d.drawString(chk.getMemo()+DV.addSpace("", 40, ' ')+chk.getAmount(), 36,685);
      
      g2d.drawString (chk.getPayee(), 36 , 580);  //expand for address
      
      g2d.setFont(toFont);

      /* Setup for possible address printing */
      FontRenderContext fr = g2d.getFontRenderContext();
      LineMetrics lm = toFont.getLineMetrics( "HijK", fr );

      int fh = (int)lm.getHeight();

      int y = cp.getPayToDim().width;
      if (chk.getAddr2().length() > 0) y = y - (fh / 2);
      if (chk.getRegion().length() > 0) y = y - (fh / 2);

      g2d.drawString (chk.getPayee(), cp.getPayToDim().height , y);
         
      if (printAddress){
          
            y += fh;
            g2d.drawString (chk.getStreet(),
                    cp.getPayToDim().height , y);

            
            y+=fh;

            if (!chk.getAddr2().equals("")){
                g2d.drawString (chk.getAddr2(),
                        cp.getPayToDim().height, y);
                y+=fh; //+2
            }
            
            if(!chk.getRegion().equals("")){
                g2d.drawString (chk.getRegion(),
                        cp.getPayToDim().height, y);
                y += fh; //+3
            }

            if(!chk.getCity().equals("")){
                g2d.drawString (chk.getCity(),
                        cp.getPayToDim().height, y);
                y+=fh;
            }

      }
      
      /* Print image  */
      
      if (cp.canPrintSig()) {
          
          String path = cp.getSignatureImage();
          Toolkit tools = Toolkit.getDefaultToolkit();
          
        
          g2d.drawImage(tools.getImage(path),cp.getSigDim().height, cp.getSigDim().width,null);
          
      }
      
      return (PAGE_EXISTS);
    }
}







