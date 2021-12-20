/*
 * CheckPrinter.java
 *
 * Created on July 13, 2007, 1:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.services;
import com.datavirtue.nevitium.models.checks.CheckStub;
import datavirtue.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.awt.font.*;
import java.awt.print.Paper;
import java.awt.Toolkit;
import com.datavirtue.nevitium.services.FormatService;

/**
 *
 * @author Data Virtue
 */
public class CheckPrinterService {
    
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
    public CheckPrinterService(boolean printSignature, boolean printCheckNumber, boolean printAddress, boolean prompt) {
    
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
    
    
        
    
}


class CheckPrinterPage implements Printable {  

    public CheckPrinterPage(
            CheckStub stub,  // get payee and amount info here  (and address)
            Font docFont, 
            Font toFont, 
            CheckPrinterService chkprn, // get settings from here
            boolean printAddress, 
            boolean chkNum, 
            boolean signature) {

        this.docFont = docFont;
        this.toFont = toFont;
        this.checkPrinter = chkprn;
        this.check = stub;
        this.chkNum = chkNum;
        this.signature = signature;
        this.printAddress = printAddress;
        
    }

    private CheckPrinterService checkPrinter;
    private CheckStub check;
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
     
      g2d.drawString (check.getDate(), checkPrinter.getDateDim().height , checkPrinter.getDateDim().width);

      if (chkNum) g2d.drawString (check.getNumber(), checkPrinter.getNumberDim().height , checkPrinter.getNumberDim().width);
           
      g2d.drawString (check.getAmount(), checkPrinter.getAmountDim().height , checkPrinter.getAmountDim().width);
       
      var amount = FormatService.parseFloat(check.getAmount());
      g2d.drawString (f.convertDollars(amount), checkPrinter.getSpellDim().height , checkPrinter.getSpellDim().width);
      
      g2d.drawString(check.getMemo(), checkPrinter.getMemoDim().height, checkPrinter.getMemoDim().width);
      
      //payee stub
      g2d.drawString(check.getMemo()+DV.addSpace("", 40, ' ')+check.getAmount(), 36,324);
 
      //keeper stub
      g2d.drawString(check.getMemo()+DV.addSpace("", 40, ' ')+check.getAmount(), 36,685);
      
      g2d.drawString (check.getPayee(), 36 , 580);  //expand for address
      
      g2d.setFont(toFont);

      /* Setup for possible address printing */
      FontRenderContext fr = g2d.getFontRenderContext();
      LineMetrics lm = toFont.getLineMetrics( "HijK", fr );

      int fh = (int)lm.getHeight();

      int y = checkPrinter.getPayToDim().width;
      if (check.getAddr2() != null && check.getAddr2().length() > 0) y = y - (fh / 2);
      if (check.getRegion() != null && check.getRegion().length() > 0) y = y - (fh / 2);

      g2d.drawString (check.getPayee(), checkPrinter.getPayToDim().height , y);
         
      if (check != null && printAddress){
          
            y += fh;
            g2d.drawString (check.getStreet(),
                    checkPrinter.getPayToDim().height , y);

            
            y+=fh;

            if (!check.getAddr2().equals("")){
                g2d.drawString (check.getAddr2(),
                        checkPrinter.getPayToDim().height, y);
                y+=fh; //+2
            }
            
            if(!check.getRegion().equals("")){
                g2d.drawString (check.getRegion(),
                        checkPrinter.getPayToDim().height, y);
                y += fh; //+3
            }

            if(!check.getCity().equals("")){
                g2d.drawString (check.getCity(),
                        checkPrinter.getPayToDim().height, y);
                y+=fh;
            }

      }
      
      /* Print image  */
      
      if (checkPrinter.canPrintSig()) {
          
          String path = checkPrinter.getSignatureImage();
          Toolkit tools = Toolkit.getDefaultToolkit();
          
        
          g2d.drawImage(tools.getImage(path),checkPrinter.getSigDim().height, checkPrinter.getSigDim().width,null);
          
      }
      
      return (PAGE_EXISTS);
    }
    
    private String formatTo;
}







