/*
 * Reports.java
 *
 * Created on August 12, 2006, 11:07 AM
 *
 * Use this object to create text file reports
 * This class counts lines and pages, places the headers and footers, and makes other adjustments
 * All you have to do is add text with the appendLine () method and set certain values
 *
 * 
 ** Copyright (c) Data Virtue 2006
 *
 */

package com.datavirtue.nevitium.database.reports;


import com.datavirtue.nevitium.services.util.DV;
import java.io.*;

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */

public class Report {
    private boolean debug = false;
    /** Creates a new instance of Reports */
    public Report(int width) {
       
        report_width = width; 
        
    }
    
    
    public Report() {
        
        
    }
    
    public boolean setFile (String path_filename) {
        
        try {
                
            File data = new File (path_filename);
            
            out = new PrintWriter(
                    new BufferedWriter( 
                     new FileWriter (data ) ) );
                
        } catch (Exception e) {e.printStackTrace();}

        return true;
        
    }
 
    
    public boolean appendLine (String report_line) {
        
        
        try {
        
        
            if (lines == 0 ) {  //top of page
                
                                    
                     printHeader();
                      //lines is 2                  
                
            }
            
            int nl = DV.howManyNewLines(report_line) ;
            //if (nl == 1) nl = 0;  
            
                            
        if (lines + nl + footspace > page_length) {  //if towards the end of a page
                                
                    printFooter();
                  /*lines has bee set to zero
                   *
                   */
               
                page_number++;
                
                
                //print another header
                if (do_header) {
                    
                    printHeader();
                    
                    
                }
                out.println(report_line);
                lines += DV.howManyNewLines(report_line);
                lines++;
        }else {
        
        //System.out.println(lines);
            out.println(report_line);
        lines++;
        lines += nl;  //add to report length
        }
            
        return true;
    
        } catch (Exception e) {e.printStackTrace();}
    
     return false;
    }
 
    //setting the header turns on header printing
    public void setHeader (String h) {
    
        header = h;
        do_header = true;
    
    }
    

    private void printHeader () {
      
        
        
        
        
        try {
            
            out.println(header);
            lines += DV.howManyNewLines(header);
            
        }catch (Exception e) {e.printStackTrace();}
    
    }
    
    
    
    private void printFooter () {
        
        try { 
            
            if (debug) System.out.println("Lines before Footer "+lines);
            
            int left = (page_length - lines) - (footspace+1);  //-1 ?????
            if (debug) System.out.println("Lines till footer  " + (page_length - lines-footspace-2));
            for (int i = 0; i < left; i++){
                
                out.println(i);
                
            }
            
            if (do_footer) out.println( rightJustify(footer + " Pg: " + page_number, ' ') );
            lines = 0; 
            
            
       } catch (Exception e) {e.printStackTrace();} 
        
        
    }
    
    
    //setting the footer turns on footer printing
    public void setFooter (String f) {
    
        footer = f;
        do_footer = true;
    }

    //inserts a separator text line built with the specified character (char)
    public boolean sepLine (char c) {
    
        sb = new StringBuilder ();
    
        for (int i = 0; i < report_width; i++) {
  
             sb.append(c);
          
        }
    
        sb.trimToSize();
    
        boolean op = appendLine ( sb.toString() );
        
        if (op) return true;
        return false;
    
    }

    public String rightJustify (String s, char c) {
        
        StringBuilder sb = new StringBuilder ();
        int los = s.length();
        
        for (int i = 0; i < report_width - los; i++){
            sb.append(c);
        }
        
        sb.append(s);
        
        return sb.toString();
    
    
    }

    public String center (String s, char c) {
    
        StringBuilder sb = new StringBuilder ();
        int los = s.length();
        int spaces = (report_width - los) / 2;
        if (debug) System.out.println(spaces);
        for (int i = 0; i < spaces; i++){
            sb.append(c);
        }
        
        sb.append(s);
        
        return sb.toString();
    
    
    
    }


    public boolean finish () {
        
            printFooter();
            out.flush();
            out.close();
            return true;
    
       
        
    }
    
    
    
    private boolean close() {
    
        try {
        
            out.flush();
            out.close();
            return true;
    
        } catch (Exception e) {e.printStackTrace();}
    
        return false;
    
    }
    
    
    public void setPageWidth (int w) {
        
     report_width = w;   
        
    }
    
    public int getPageWidth () {
        
        return report_width;
        
    }
    
    
   public void setPageLength (int len) {
       
        page_length = len;   
       
   }
    
    public int getPageLength () {
       
    return page_length;   
       
   }
   
   public void flagHeader (boolean b) {
       
       do_header = b;
       
   }
   
   public void flagFooter (boolean b) {
       
       do_footer = b;
       footspace = 0;
       
   }
       
   public int getLines () {
       
    return lines;   
       
   }
   
   
private StringBuilder sb;
private PrintWriter out;

private int report_width = 80;
private int lines = 0;
private int page_length = 66;

private int page_number = 1;
private String header = "";
private String footer = "";
private int header_size = 1;
private int footspace = 1;

private boolean do_header = true;
private boolean do_footer = true;
private String newLine = System.getProperty("line.separator");

}
