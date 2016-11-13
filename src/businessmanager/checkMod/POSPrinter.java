package businessmanager.checkMod;


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

/**
 *
 
 */

public class POSPrinter {

    /* instance vars */

    private PageFormat pgFormat = new PageFormat();
private boolean debug = false;

    private Book book = new Book();

	private Paper p;


    private boolean prompt = true;

	private int lineSize = 0;
	private int Y_space = 0;

	private java.util.ArrayList lines = new java.util.ArrayList();

	private int W;
	private int H;
	private int usedLines = 0;
	private int availableLines=0;
	private  Font font;
    private float inches;

    /* Create a new instance of LinePrinter */

    public POSPrinter(Font fnt, boolean prompt, float width) {

		this.setFont(fnt);


        this.prompt = prompt;
        inches = width;
		W = (int)(inches * 72);

    }


	private void setupPaper() {



		/* calc linesAvailable and other metrics */
				/* get BufferedImage to create a grphics so we can do font calcs before turning the printable loose */
						BufferedImage bimage = new BufferedImage(W-4, 100, BufferedImage.TYPE_INT_RGB);

						Graphics2D g2d = bimage.createGraphics();

						g2d.setFont(font);

						FontRenderContext fr = g2d.getFontRenderContext();
						LineMetrics lm = font.getLineMetrics( "HijK", fr );

						lineSize = (int)lm.getHeight();


						//done with g2d
						g2d = null;


		Y_space = lineSize * usedLines;

		H = Y_space;
		p = new Paper();

		        p.setSize(W, H);

		        if (debug) System.out.println("Y_space "+H );

				p.setImageableArea(2, 2, W-2 ,H-2);  //2 point margin (1/7th of an inch)

		        pgFormat.setPaper(p);



	}


    /** Adds the text Strings to the page.
    If a line doesn't fit the page a new page is created and "printing" continues on the next page. */
    public void addLines (String [] text) {


		for (int i = 0; i < text.length; i++){

			lines.add(text[i]);
			usedLines++;

		}


    }


    /** Adds a line of text to the page.
    If a line goes over the page a new page is created and "printing" continues on the next page.
    You can create more methods to accept Vectors/Genrics and what not.
    Real new lines inside the string are ignored when rendering.
    This can be chaged by modifying this method to scan the string for the amount of newlines and then calling the newLine() method for each one.*/
    public void addLine (String line) {


		lines.add(line);
		usedLines++;



    }


	/**Adds a "new line" (blank line). */
	public void newLine() {

		addLine(" ");


	}
	/** Manually starts a new page. */
    public void formFeed() {

		setupPaper();

		book.append(new LinePrinterPage(lines, font), pgFormat);

		lines = new java.util.ArrayList();

		usedLines = 0;

	}




	/** The whole document will be rendered in this font until forFeed(Font f) is called at least.  */
    private void setFont(Font f){

		font = f;

	}


/** Call formFeed() before calling go()*/
    public void go () {

		setupPaper();

				book.append(new LinePrinterPage(lines, font), pgFormat);


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


    public static void main(String[] args) {



        POSPrinter dp = new POSPrinter(new Font("Concielian", Font.PLAIN, 10), true, 3.0f);  //3 inch paper

        dp.addLines(new String [] {"", "Sean Anderson", "Data Virtue", "5244 Leininger-Haigh Road", "Hillsboro, OH  45133"});

        for (int i = 0; i < 10; i++) {

			dp.newLine();  //Trailing space

		}


        dp.go();





	}

}//END LinePrinter class


/** When printing the various strings for the page new lines within the string are ignored  */
class LinePrinterPage implements Printable {  //this can access the vars in

    /** Creates a new instance of LinePrinterPage */
    public LinePrinterPage(java.util.ArrayList lines, Font f) {

        this.lines = lines;
        font = f;


    }

    private java.util.ArrayList lines;
	private Font font;

    public int print(Graphics g, PageFormat pageFormat, int page) {

		if (page > 1) {
		            return Printable.NO_SUCH_PAGE;  //?
		        }


      Graphics2D g2d = (Graphics2D) g;


      g2d.setPaint(Color.black);


      g2d.setFont(font);


      g2d.setClip(null);


	FontRenderContext fr = g2d.getFontRenderContext();
	LineMetrics lm = font.getLineMetrics( "HijK", fr );


          double X = pageFormat.getImageableX();


			double fh = (int)lm.getHeight();

          double Y = pageFormat.getImageableY();



      for (int i = 0; i < lines.size(); i++){



		/* real new lines inside text strings are ignored */

          g2d.drawString ((String)lines.get(i), (float) X , (float) Y);

          Y += fh;   // keeping track of how much was written

      }

      return (PAGE_EXISTS);
    }


  }
