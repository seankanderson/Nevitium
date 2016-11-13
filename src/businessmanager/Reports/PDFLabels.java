/*
 * PDFLabels.java
 *
 * Created on January 14, 2007, 12:31 PM
 *
   This class handles the gory details of formatting, calculating and building
   a table for the specified lable sheet dimensions, placing data in the table
   and actually generating a pdf file.  Creates one table based on 
   the constructor and allows you call various add() methods which places data 
 * in a cell, formats the cell based on certain class fields and adds the cell to the table. 
   
 * Currently only supports 8.5" x 11" Avery TM style label sheets in portrait orientation.
 *  
 *
 *
   DEFAULT LABEL:
    Page Layout fields in inches

    Default is 1" High by 2.6" Wide - Three across Label Sheet

    protected float H = 1.0f * 72;  //height
    protected float W = 2.6f * 72;  //width
    protected float LRM = .17f * 72;  //Left Right Margin
    protected float TBM = .50f * 72;  //Top Bottom Margin
    protected float GAP = .17f * 72;  // Gap between labels
    
    new float [] = {1.0f, 2.6f, .17f, .50f, .17f} ;  <-- sample constructor value

    Default Font:
    new Font (Font.COURIER, 10, Font.NORMAL);

    Default Label Alignment: Vertical = MIDDLE ~ Horizontal = LEFT

 *
  CONSTRUCTOR EXAMPLES:
 *                                                                                            //AVERY Part No.
 PDFLabels pl = new PDFLabels (new float [] = {1.0f, 2.6f, .17f, .50f, .17f},"c:\\labels.pdf"); //5160 || 8160
        PDFLabels pl = new PDFLabels (1.33f, 4.0f, .16f, .83f, .17f,"c:\\labels.pdf");  //5162 
        PDFLabels pl = new PDFLabels (1.0f, 4.0f, .16f, .50f, .17f,"c:\\labels.pdf");  //5161
        PDFLabels pl = new PDFLabels (1.5f, 4.0f, .13f, .25f, .13f,"c:\\labels.pdf");  //5159
        PDFLabels pl = new PDFLabels (.75f, 2.25f, .38f, .63f, .5f,"c:\\labels.pdf");  //3261R
 
 
 *   iText required, visit:  <a href="http://www.lowagie.com/iText/">iText Website </a>
 *
 */

/**
 *
 * @author Sean K Anderson - 
 *
 * 
 *
 * Use how you wish. Pirate away.  AARRRH!!
 *
 * Send an email to 'software@datavirtue.com' or link to 'http://www.datavirtue.com' if this class helps you.
 * Send an email if you need help.  01-17-2007
 *
 *iText required, visit:  <a href="http://www.lowagie.com/iText/">iText Website </a>
 *
 */

package businessmanager.Reports;

//import com.lowagie.text.BadElementException;
import java.util.StringTokenizer;
import com.lowagie.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
//import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
//import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
//import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
//import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
//import com.lowagie.text.pdf.BaseFont;
//import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfContentByte;

import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.BarcodeEAN;
import com.lowagie.text.pdf.BarcodePostnet;
import com.lowagie.text.pdf.Barcode128;

import com.lowagie.text.pdf.ColumnText;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.awt.Color;


public class PDFLabels {

    /** Simple constructor uses the DEFAULT LABEL 2.6"W by 1.0"H three across.
     Creates a new instance of PDFLabels.
     */
    public PDFLabels (String file) {

        /* this constructor is good to use the default labels */
        this.PDF = file;
        init();
        checkStatus();

    }

    /** Provide custom label dimensions in a float array.  
        <b>new float [] = {1.0f, 2.6f, .17f, .50f, .17f} </b>
        Values are in inches.
        Creates a new instance of PDFLabels.
     */
    public PDFLabels (float [] gaps, String filename){
        
        /*    H         W       LRM       TBM      GAP     */
        this(gaps[0], gaps[1], gaps[2], gaps[3], gaps[4], filename);
        
        
    }
    
        
    /** Custom label dimensions - in inches.
        Creates a new instance of PDFLabels.
     */
    public PDFLabels(float H, float W, float LRM, float TBM, float GAP, String file) {

        /* Multiply by 72 to convert inches to points */
        this.H = H * 72;
        this.W = W * 72;
        this.LRM = LRM * 72;
        this.TBM = TBM * 72;
        this.GAP = GAP * 72;

        this.PDF = file;

        init();

        checkStatus();

    }
    
    public static void main(String[] args) {

        
        String nl = System.getProperty("line.separator");
        
	     PDFLabels pl = new PDFLabels (1.0f, 2.6f, .16f, .50f,.17f,"labels.pdf"); //5160 || 8160
             
             pl.setAlignment(9,1); //sets the defalut alignment properties for the cells 
             
             String text = "Sean Anderson" + nl + "11851 Golden Hill Drive" + nl + "Hillsboro, Ohio 45133";
             
	        for (int r = 0; r < 60; r++){  //two pages of labels

	           pl.appendPostnet(text, "45140-8778");  //regular label with postnet barcode

	        }
                pl.finnish();

	    }

    
    protected void init (){


        try {


                writer = PdfWriter.getInstance(document, new FileOutputStream(PDF));

            } catch (Exception ex) {

               STATUS_GOOD = false;

                ex.printStackTrace();

            }

            document.addAuthor("Data Virtue");  //change this or create fields with get/set to change this 
            document.addCreationDate();         //these statements must be called or set before calling open()
            document.addSubject("Nevitium Labels - datavirtue.com");
            document.open();

            cb = writer.getDirectContent();  //direct writer for rendering the table manually - NOT IN USE

            ct = new ColumnText(cb); //column text to render table easily  - IN USE
            ct.setSimpleColumn(0, TBM, PageSize.LETTER.width(), PageSize.LETTER.height() - TBM*2, 0, Element.ALIGN_MIDDLE);

            ACROSS = ( PageSize.LETTER.width() - (LRM * 2) ) / W;  //how many labels across based on supplied dims
            ACROSS -= ACROSS % 1; // trim the fat

            DOWN = ( PageSize.LETTER.height() - (TBM * 2) ) / H; //how many labels up/down based on supplied dims
            DOWN -= DOWN % 1;  //trim the fat


            COLS = new float [(int)ACROSS + 1 + ((int)ACROSS)];

            float cols = 0;  //total size of all COLS

            table = new PdfPTable ( COLS.length );//table with gaps and margins included

            /* kill the border on default cells */
            if (!DEBUG) table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            for (int i = 0; i < COLS.length; i++){

                //if (DEBUG) System.out.println("COLS WIDTH BL :"+cols);

                if (i == 0 || i == COLS.length-1) {

                    COLS[i] = LRM;
                    cols += LRM;
                    continue;
                }


                /* You do not reach this statement if i == 1 */
                if ((i+1) % 2 != 0 && i != COLS.length-1){  //if odd its a gap

                    COLS[i] = GAP;
                    cols += GAP;
                    continue;

                }else {

                    COLS[i] = W;
                    cols += W;
                    continue;
                }

            }

             if (DEBUG) System.out.println("COLS TOTAL WIDTH  :"+cols);

            try {


                table.setTotalWidth(cols);  //tried removing this, not good
                table.setWidths(COLS);
                table.setLockedWidth(true);
                
                table.getDefaultCell().setPaddingBottom(0.0f);
                table.getDefaultCell().setPaddingTop(0.0f);

            } catch (DocumentException ex) {

                STATUS_GOOD = false;

            }

        /* Last command for init() */
        STATUS_GOOD = true;


    }

    /**
     Called by all other add() methods to place a PdfPCell into the label table.
     This method also sets the height, padding, border and alignment of the PdfPCell.
     */
    public void add (PdfPCell cell){

        /* adds the actual cell to the table  */
        /* Every add() calls this method */


        cell.setFixedHeight(H);

        cell.setNoWrap(NO_CELL_WRAP);

        cell.setPaddingLeft(CELL_LEFT_PADDING);

        cell.setVerticalAlignment(VALIGNMENT);
        cell.setHorizontalAlignment(HALIGNMENT);

        if (!DEBUG) cell.setBorder(Rectangle.NO_BORDER);  //show table borders in DEBUG mode

        how_many++;  //total amount of labels
        counter++;  // 'int counter' keeps track of how many times
                    // this method is called within a range of X (X = ACROSS)

        table.addCell("");
        table.addCell(cell);
        if (counter == ACROSS) table.addCell("");  //at the end

        if (counter == ACROSS) counter = 0;  //if at the end reset the counter


    }

    public void add (PdfPTable nt){

        /* adds the actual cell to the table  */
        /* Every add() calls this method */

        //nt.setSpacingBefore(0.0f);
        //nt.setSpacingAfter(0.0f);
        
        how_many++;  //total amount of labels
        counter++;  // 'int counter' keeps track of how many times
                    // this method is called within a range of X (X = ACROSS)

        table.addCell("");
        table.addCell(nt);
        if (counter == ACROSS) table.addCell("");  //at the end

        if (counter == ACROSS) counter = 0;  //if at the end reset the counter


    }
    
    /**

    Each call to this method places the string into a proper PdfPCell object and adds it to the table.


     */
    public void add (String s){

        if (DEBUG) System.out.println("add(Cell) Counter :" + counter);
        if (DEBUG) System.out.println(s + nl+"----------------------");

        /* Build cell and call add(cell); */
        PdfPCell cell = new PdfPCell();



        cell = new PdfPCell (new Phrase(s, DOC_FONT));


        add(cell);


    }
    /** Calls add(String s) for each element */
    public void add (String [] sa){

        /* repeatedly call add(String) */
        for (int r = 0; r < sa.length; r++){

           add( sa[r] );

        }

    }

    /**
       Provide an ArrayList of Strings only.

     */
    public void add (java.util.ArrayList al){

        /* repeatedly call add(String) */

        al.trimToSize();

        for (int r = 0; r < al.size(); r++){

            try {

                add( (String) al.get(r) );

            } catch (Exception e) {

                STATUS_GOOD=false;

            }

        }


    }

    /** Barcode method to add a EAN UPCA barcode exclusivly (by itself) to a label. */
    public void addEAN12 (String UPCA){

        /* adds the barcode exclusivly to the label */
        /* Calls add(cell) */
        
        BarcodeEAN code = new BarcodeEAN();
        code.setCodeType(Barcode.UPCA);  //regular 12 digit
        code.setCode(UPCA);
        add(new PdfPCell (code.createImageWithBarcode(cb, null,null)) );
        
    }

    /** Standard 13 digit barcode seen on retail items; especially newer items. */
    public void addEAN13 (String EAN) {
        
       /* adds the barcode exclusivly to the label */
        /* Calls add(cell) */
        /* Regular 13 digit */
        BarcodeEAN code = new BarcodeEAN();
        code.setCode(EAN);
        add(new PdfPCell (code.createImageWithBarcode(cb, null,null)) ); 
        
    }
    
    /** EAN/UCC-14 digit number barcode. Your 14 digit code gets the AI '(01)' prefixed.  */
    public void addEAN14 (String EAN) {
        
       /* adds the barcode exclusivly to the label */
        /* Calls add(cell) */
        /* Regular 13 digit */
        Barcode128 code = new Barcode128();
        code.setCodeType(Barcode.CODE128_UCC);
        
        code.setCode("(01)" + EAN);
        
        add(new PdfPCell (code.createImageWithBarcode(cb, null,null)) ); 
        
    }
    
    /** Plain Code 128 barcode */
    public void addCode128 (String s){
        
        Barcode128 code = new Barcode128();
        code.setCode(s);
        add(new PdfPCell (code.createImageWithBarcode(cb, null,null)) ); 
        
    }
    
    /** Convenience method provides the FNC char(s) to markup/demarc Code 128 fields. */
    public char getFNCx (int f){
        
        if (f == 1) return Barcode128.FNC1;
        if (f == 2) return Barcode128.FNC2;
        if (f == 3) return Barcode128.FNC3;
        if (f == 4) return Barcode128.FNC4;
        
        return Barcode128.FNC1;  //default
        
    }
    
    /** RAW Code 128 barcode, specify a Title and code */
    public void addCode128Raw (String title, String cde){
        
        Barcode128 code = new Barcode128();
        code.setCode(cde + "\uffff" + title);
        code.setCodeType(Barcode.CODE128_RAW);
        add (new PdfPCell (code.createImageWithBarcode(cb, null,null)) ); 
       
    }
    
    
    /** Barcode method to add a Code 3 of 9 barcode exclusivly (by itself) to a label. */
    public void addCode39(String s){

         /* adds the barcode exclusivly to the label */
        /* Calls add(cell) */
        
        Barcode39 code39 = new Barcode39();
              
        code39.setCode( s.toUpperCase() );  //Only uppercase (base 3 of 9)
              //insert other modification methods for barcode here:    
        add(new PdfPCell (code39.createImageWithBarcode(cb, null,null)) );
        
        
    }

    /** Generates a postnet barcode and adds it to the label set but it first tries to remove any hyphen it detects.
        Most will want to use appendPostnet(String label, String post) instead.
     
     */
    public void addPostnet (String post){
        
        if (post.length() < 5 || post.length() > 11) return;
        
        post = removeHyphen(post);
        
        BarcodePostnet pnbc = new BarcodePostnet();
        
        pnbc.setCode(post);
        
        add(new PdfPCell (pnbc.createImageWithBarcode(cb, null,null)) );
        
        
    }
    
    protected String removeHyphen (String s){
        
        if (s.contains("-")){ 
            
            int idx = s.indexOf("-");
           
            try {
                
                /* Strip hyphen */
                
                return s.substring(0, idx) + s.substring(idx+1);
                
            } catch (Exception e) {
                
                return s;
                
            }
            
        }        
        
        return s;
        
    }
    
          
    /** Takes a regular label and slaps a US postal barcode at the bottom.
        If you provide a crap code it will skip the barcode
     */
    public void appendPostnet (String label, String post){
        
        post = removeHyphen(post);
        
        /* 
         *Construct 1 col table
         *add cell with a fixed height of H * .85
         *add cell with a fixed height of H * .15
         *label in first cell
         *zip in the secind cell
         *add cell
         **/
        
        PdfPTable t = new PdfPTable (1);
                  //t.setSpacingBefore(0.0f);
                  //t.setSpacingAfter(0.0f);
        PdfPCell cell = new PdfPCell (new Phrase(label, DOC_FONT));
        cell.setFixedHeight(H * .85f);
        
        this.formatCell(cell);
                        
        t.addCell(cell);
                       
        if (post.length() > 4) {
            
            BarcodePostnet pnbc = new BarcodePostnet();
        
            pnbc.setCode(post);
                 
            cell = new PdfPCell(pnbc.createImageWithBarcode(cb, null,null));
            cell.setFixedHeight(H * .15f);
            //cell.setVerticalAlignment(Element.ALIGN_TOP);
            this.formatCell(cell);
                      
            
            t.addCell(cell);
        }
        
         t.setTotalWidth(W);
         
        add(t);
        
    }
   
    /** Set the fixedHieght before calling this method.*/
    protected void formatCell (PdfPCell cell){
        
        //cell.setFixedHeight(H);

        cell.setNoWrap(NO_CELL_WRAP);

        cell.setPaddingLeft(CELL_LEFT_PADDING);

        cell.setVerticalAlignment(VALIGNMENT);
        cell.setHorizontalAlignment(HALIGNMENT);

        if (!DEBUG) cell.setBorder(Rectangle.NO_BORDER);  //show table borders in DEBUG mode

        
        
    }
    
    
   /** The label to start from on the sheet. Set this property before generating labels.
       This method immediatly adds x amount of blank labels to the table.
    */
   public void setStartLabel (int start) {

       if (start > 1){
           
       
       start_label = start;

       for (int i = 1; i < start; i++){
           
           this.add(" ");
           
       }
       
       }
       
   }

   /** Use iText FontFactory or provide your own font for the labels.
       Default: new Font (Font.COURIER, 10, Font.NORMAL)

    */
   public void setFont (Font f){

       DOC_FONT = f;

   }

   /** Lets you access the amount of labels across on a page ?? */
   public float getAcrossCount() {

        return ACROSS;

    }
    /** Lets you access the amount of labels up/down on a page ?? */
    public float getDownCount() {

        return DOWN;

    }

    private void checkStatus () {

       if (DEBUG){

                System.out.println("ACROSS :"+ACROSS);
                System.out.println("DOWN :"+DOWN);
                System.out.println("COLS LENGTH :"+COLS.length);
               
            }
       
        if (!STATUS_GOOD) {

            System.out.println( "PDFLabels object is corrupt!" );

        }

    }

    /** Toggles DEBUG which prints various information to the console/terminal.   */
    public void setDebug (boolean tf) {

        DEBUG = tf;
        
    }

    protected boolean writeLabels () {

       
             try {  //this could actually be put in finnish()


             document.add(table);


             } catch (DocumentException ex) {

                 STATUS_GOOD = false;

             }
        return true;
    }


    /**
      Call finnish() when you are done adding labels.
      Catch the boolean return value to make sure everything went as planned.
     */
    public boolean finnish () {

        /* Fill out the last cells to complete the incomplete rows */
        if (counter < ACROSS){
            
        for (int i = counter; i < ACROSS ; i++){
            
            table.addCell("");
            table.addCell("");
            counter++;
                    
        }    
            
            table.addCell("");  //last cell
            
        }
        
        
        STATUS_GOOD = writeLabels();

        if (DEBUG) System.out.println("LABELS ADDED :" + (how_many - start_label) );

        document.close();

        return STATUS_GOOD;
    }

    /** 
     * Changes alignment values for the cells added to the table.
     *You can call this once or before each add(?).
     * A value of 9 leaves the ALIGNMENT unchanged
         ALIGN_BASELINE 7  
         ALIGN_BOTTOM 6  
         ALIGN_CENTER 1  
         ALIGN_JUSTIFIED 3  
         ALIGN_JUSTIFIED_ALL 8  
         ALIGN_LEFT 0     *DEFAULT H*  
         ALIGN_MIDDLE 5   *DEFAULT V*  
         ALIGN_RIGHT 2  
         ALIGN_TOP 4  
         ALIGN_UNDEFINED -1   
   
     */
    public void setAlignment (int vertical, int horizontal) {

        /* if 9 don't change */
        if (horizontal != 9) HALIGNMENT = horizontal;
        if (vertical != 9) VALIGNMENT = vertical;

     }

    
    /** Default behavior is to NOT wrap text ~ setNoWrap(true) */
    public void setNoWrap (boolean tf){
                
        NO_CELL_WRAP = tf;
        
    }
    
    /**  Any left padding the labels may need ~ default is 8.0f */
    public void setCellLeftPad (float padding){
        
        
        CELL_LEFT_PADDING = padding;
        
    }
    
    
    protected void newPage () {
        try {
            document.newPage();
        } catch (DocumentException ex) {
            System.out.println("Document exception around line 759 in PDFLabels.java");
        }

        

    }

    /* Page Layout fields - inches */
    /** Label height defaults to 1" High each */
    protected float H = 1.0f * 72;
    /** Label width defaults to 2.6" Wide each  */
    protected float W = 2.6f * 72;
    /** LEFT and RIGHT margins Default: .17f each */
    protected float LRM = .17f * 72;  //The PdfPCells are left-padded by add(PdfPCell cell)  to 8.0f points
    /** TOP and BOTTOM margins Default: .50f each */
    protected float TBM = .50f * 72;
    /** The gap in between the labels, default is .17f each */
    protected float GAP = .17f * 72;

    /** The default font: new Font (Font.COURIER, 10, Font.NORMAL) */
    protected Font DOC_FONT = new Font (Font.COURIER, 10, Font.NORMAL);

    /* These fields are set by init() */
    protected int start_label = 0;
    protected float ACROSS = 0;
    protected float DOWN = 0;
    protected int how_many = 0;
    protected int ROWS = 0;
    

    /** Status values */ 
    protected boolean STATUS_GOOD = false;
    protected boolean DEBUG = false;


    /** The PDF filename.  */
    protected String PDF = "Labels.pdf";


    /* GLOBAL SCRAP */
    protected int counter = 0;
    protected float [] COLS ;
    protected String nl = System.getProperty("line.separator");


    /*  PDF iText Stuff  */
    protected Document document = new Document(PageSize.LETTER);  //potrait
    protected ColumnText ct;
    protected PdfContentByte cb;
    protected PdfWriter writer;


    /** Single PdfPTable to hold and format the labels.  Each call to add() adds a PdfPCell. 
        You need to make sure you specify JVM options to provide adequate memory for large tables.
        Check JVM tuning on Sun website.
     */
    protected PdfPTable table;

    /** Stores vertical alignment value for PdfPCells, see: setAlignment(int vertical, int horizontal) */
    protected int VALIGNMENT = Element.ALIGN_MIDDLE;
    /** Stores horizontal alignment value for PdfPCells, see: setAlignment(int vertical, int horizontal) */
    protected int HALIGNMENT = Element.ALIGN_LEFT;
    
    protected boolean NO_CELL_WRAP = true;
    protected float CELL_LEFT_PADDING = 8.0f;
    

}
