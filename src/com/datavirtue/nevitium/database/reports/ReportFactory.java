/*
 * ReportFactory.java
 *
 * Created on November 26, 2006, 11:52 PM
 *
 * * Copyright (c) Data Virtue 2006
 */

package com.datavirtue.nevitium.database.reports;
import com.datavirtue.nevitium.services.PdfReportService;

import com.datavirtue.nevitium.models.contacts.Contact;

import com.datavirtue.nevitium.ui.util.NewEmail;
import com.datavirtue.nevitium.ui.util.Tools;
import com.datavirtue.nevitium.models.invoices.old.PDFInvoice;


import java.util.*;
import java.text.DateFormat;
import com.lowagie.text.BadElementException;
import java.net.MalformedURLException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.table.*;

/**
 *
 * @author  Sean K Anderson - Data Virtue
 * @rights Copyright Data Virtue 2006, 2007 All Rights Reserved.
 */

public class ReportFactory {
    
    /** Creates a new instance of ReportFactory */
    public ReportFactory() {
        
    }
       
    
//    public static String generateOpenInvoiceReport (GlobalApplicationDaemon application ) {
//        
//        String nl = System.getProperty("line.separator");
//        
////generates a report of outstanding invoices
//        
//        //invoices, 
//        
//        //Contact information, invoice number, date opened, amount paid, amount owed
//        DbEngine dbsys = application.getDb();
//        Settings props = application.getProps();
//
//        ArrayList al = dbsys.search("invoice", 8, "false", false);
//        if (al == null) return "none";
//        javax.swing.table.DefaultTableModel temp_model = (javax.swing.table.DefaultTableModel) dbsys.createTableModel("invoice", al, null);
//        
//        //kill the voids
//        for (int i = 0; i < temp_model.getRowCount(); i++){
//            
//            if ((Boolean)temp_model.getValueAt(i,9) == true){  //if void = true
//                
//                temp_model.removeRow(i);//remove the void
//                i--;
//            }                        
//        }
//        
//        ReportModel rm = new ReportModel (temp_model);
//        
//        StringBuilder sb = new StringBuilder ();
//        String invoice_number = "";
//        
//        //these reference vars are for collecting and accessing payment detail
//        ArrayList pl;
//        ReportModel prm;
//        
//        int custKey;
//        
//        ReportDialog rd = new ReportDialog (null, true, props.getProp("REPORT FOLDER"), false, props.getProp("CO NAME")+" Open Invoice Report", props);
//        
//        
//        String date = DV.getShortDate().replace('/', '-');
//        String file = rd.getFile()+ "/" + date +"_UNPAID.pdf";
//                
//        if (!DV.isFileAccessible(file, "PDF file")) return "WARNING: "+file+ " was open by another program." +
//                "  Close your pdf reader (Adobe?) and try again."+nl;
//             
//        PdfReportService cust = new PdfReportService (file);
//        
//        cust.setWatermark(props.getProp("WATERMARK"));//TODO
//        cust.setWatermarkEnabled(Boolean.parseBoolean(props.getProp("PRINT WM")));
//        
//        if (rd.getState() == false) return "";
//                
//        //populate report settings with data from ReportDialog
//               
//        cust.setReportTitle(rd.getTitle()+" "+ DV.getFullDate());
//        String currency = props.getProp("SYM");
//        String phone="";
//        
//        
//        do  {   //process each invoice
//           
//            sb = new StringBuilder();  //use the scrap StringBuilder 
//            
//            
//            invoice_number =  rm.getValueAt( 1 );
//
//            custKey = Integer.valueOf(rm.getValueAt(11));
//
//            if (custKey > 0) {
//                Object [] customerRecord = dbsys.getRecord("conn", custKey);
//                if (customerRecord == null){
//                    phone = "NO CUSTOMER";
//                }else phone = (String)customerRecord[10];
//                customerRecord = null;
//            }
//            
//            if (phone.equals("")) phone = "<< No phone number on record. >>";
//            sb.append( nl + "- " +rm.getValueAt(1) + "  " + rm.getValueAt(2)+
//                    "  Invoice Total "+currency + rm.getValueAt(10) + nl +
//                    rm.getValueAt(3) + nl + phone + nl );
//            
//            /* Get an invoice object */
//            OldInvoice invoice = new OldInvoice(application, (Integer)rm.getRealValue(0));
//            float totalDue = invoice.getInvoiceDueNow();
//            // Add payment detail and balance due.
//            
//            if (invoice.getPaymentCount() > 0) {
//                sb.append("(Pmts/Chgs/Crdts): ");
//                prm = new ReportModel (invoice.getPayments());
//                int rows = invoice.getPaymentCount();
//                int count=0;
//            
//                do {
//                
//                    //get date, amount, balance
//                    sb.append(prm.getValueAt(2) + " +"+currency + prm.getValueAt(5)+
//                            " -"+currency+prm.getValueAt(6)+ " : ");
//                    
//                    if (count == rows-1) sb.append(" STILL DUE " +currency + Float.toString(totalDue)+
//                            nl );
//                
//                    count++; 
//                    
//                } while (prm.next()) ;
//                
//            }  else { 
//                
//                sb.append("N O   P A Y M E N T S" + "   TOTAL DUE " + currency + Float.toString(totalDue) + nl);
//                                
//            }  
//            
//            //insert line(s)
//            cust.addParagraph( sb.toString(), true );                        
//            
//        } while (rm.next());  //next record
//                
//        cust.finish();  //save report
//         
//        if ( rd.isView() ){
//            
//            return ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             
//        }
//        if (rd.getWinPrn()) {
//            
//            ReportFactory.windowsFastPrint(file, props);
//            
//            
//        }
//        return "";
//        
//        
//    }
//    
//    
//    public static String generateCustomerReport (DbEngine dbsys, Settings props, boolean vendors ) {
//       
//        String type="Customer List";
//        
//        if (vendors) type = "Supplier List"; 
//        
//        ReportDialog rd = new ReportDialog (null, true,
//                props.getProp("REPORT FOLDER"), false, props.getProp("CO NAME")+ ' ' + type, props);
//                
//        String currency = props.getProp("SYM");
//        
//        if (rd.getState() == false) return "";
//        
//        ArrayList al;
//        
//        if (!vendors) al = dbsys.search("conn", 15, "true", false);  //customers
//        else al = dbsys.search("conn", 16, "true", false); //vendors
//        
//        if (al == null) return "";
//        
//        TableSorter ts = (TableSorter) dbsys.createTableModel("conn", al, true);
//        
//        if (vendors){
//            
//            ts.setSortingStatus(1, 1);  //sort by company
//            //ts.setSortingStatus(3, 1);  //sort by last name
//           
//        }else {
//            
//            ts.setSortingStatus(3, 1);  //sort by last name
//            ts.setSortingStatus(1, 1);  //sort by company
//            
//        }
//        
//        ReportModel rm = new ReportModel (ts);  //VAR
//        
//        type =  "Customer_List.pdf";
//        if (vendors) type = "Supplier_List.pdf";
//        
//        String file = rd.getFile() + type;
//        
//        
//        if (!DV.isFileAccessible(file, "PDF file")) return "WARNING: "+type+" was open by another application";
//        
//        PdfReportService cust = new PdfReportService (file);
//        cust.setReportTitle(rd.getTitle());
//       
//        cust.setWatermark(props.getProp("WATERMARK"));
//        cust.setWatermarkEnabled(Boolean.parseBoolean(props.getProp("PRINT WM")));
//        
//        
//        StringBuilder sb = new StringBuilder ();
//        
//                
//        int [] fields ={3, 4, 5, 6, 9};
//       String nl = System.getProperty("line.separator");
//       
//                
//        do {
//           
//                sb = new StringBuilder();
//                sb.append(nl);          
//                sb.append ( rm.getValueAt(1) + nl );    //company
//                sb.append ( rm.getValueAt(2)+" " + rm.getValueAt(3) + nl  );  //first Last    
//                sb.append ( DV.addSpace(rm.getValueAt(4), 40, ' ') + " Suite: " + rm.getValueAt(5) + nl );    
//                sb.append ( rm.getValueAt(6) + ", " + rm.getValueAt(7) + "  "  + rm.getValueAt(8) + nl );    
//                sb.append ( DV.addSpace("Contact: " + rm.getValueAt(9), 30, ' ') + "  Phone: " + rm.getValueAt(10) + "  Fax: " + rm.getValueAt(11) + nl );    
//                sb.append ( "eMail: " + rm.getValueAt(12) + nl);
//                sb.append ( "Web: " + rm.getValueAt(13) + nl);
//                sb.append("Misc: " + rm.getValueAt(14)+nl);
//                sb.append(nl);
//                
//         
//         cust.addParagraph(sb.toString(), true);
//         
//         } while ( rm.next() ) ;
//               
//        cust.finish();
//        
//        if ( rd.isView() ){
//            
//            return ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             
//        }
//        if (rd.getWinPrn()) {
//            
//            ReportFactory.windowsFastPrint(file, props);
//            
//        }
//        return "";
//        
//    }
//    
//    
//    public static String generateInventoryStatusReport (DbEngine dbsys, Settings props) {
//        
//        
//        // list each inventory item 
//         // total wholesale and retail value of each in-stock items
//         //* calculate grand totals of instock wholesale and retail values
//         String currency = props.getProp("SYM");
//         
//        ReportDialog rd = new ReportDialog (null, true, props.getProp("REPORT FOLDER"), false, props.getProp("CO NAME")+ " Inventory Report", props);
//        
//        
//        if (rd.getState() == false) return "";
//        
//                
//        String nl = System.getProperty("line.separator");
//        
//        TableSorter ts = (TableSorter) dbsys.createTableModel("inventory", null);
//        
//        ts.setSortingStatus(9, 1);  //sort by category
//        ts.setSortingStatus(3, 1);  //sort by desc
//        
//        
//        ReportModel rm = new ReportModel (ts);  //VAR
//                
//               
//        String file = rd.getFile() + "InventoryReport_" + DV.getShortDate().replace('/','-')+".pdf";  //add date
//        
//        if (!DV.isFileAccessible(file, "PDF file")) return "WARNING: "+file+ " was open by another program." + "  Close your pdf reader (Adobe?) and try again."+nl;
//                
//        PdfReportService report = new PdfReportService (file);
//        
//        report.setReportTitle(rd.getTitle());
//        report.setWatermark(props.getProp("WATERMARK"));
//        report.setWatermarkEnabled(Boolean.parseBoolean(props.getProp("PRINT WM")));
//        
//        
//        Paragraph p;
//        StringBuilder sb;
//        
//                
//        //int [] fields ={3, 4, 5, 6, 9};
//        
//        double cost = 0.00;
//        double price = 0.00;
//        
//        double cost_total = 0.00;
//        double price_total = 0.00;
//        
//        double running_cost = 0.00;
//        double running_retail = 0.00;
//        float qty = 0;
//        float goods=0;
//        int cutoff = 1000;
//        
//        String avail = "";
//        
//        //get inventory list
//        //cycle list and build each entry with totals
//        //keep running totals update them into the footer
//        //launch view
//        String tax1name = props.getProp("TAX1NAME");
//        String tax2name = props.getProp("TAX2NAME");
//        
//        do {
//           
//                sb = new StringBuilder ();                
//                sb.append(nl);
//                sb.append(DV.addSpace(rm.getValueAt(3), 52,' ')+DV.addSpace("Size:"+rm.getValueAt(4), 17,' ') +"Wt:"+rm.getValueAt(5)+ nl );
//                
//                qty = Float.parseFloat(DV.deFormat(rm.getValueAt(6)));
//                cost = Float.parseFloat(DV.deFormat(rm.getValueAt(7)));
//                price = Float.parseFloat(DV.deFormat(rm.getValueAt(8)));
//                cutoff = Integer.parseInt(rm.getValueAt(18));
//                
//                if (qty > 0){
//                
//                    goods += qty;
//                             
//                    cost_total = cost * qty;
//                    price_total = price * qty;
//                                
//                    running_cost += cost_total;
//                    running_retail += price_total;
//                
//                }else {                                                      
//                    cost_total = 0.00f;
//                    price_total = 0.00f;
//                 
//                }
//                if (rm.getValueAt(15).equals("N")) avail = "Available - In Stock";
//                
//                if (rm.getValueAt(15).equals("N") && qty > 0 && cutoff >= qty) avail = "Low - Reorder!";
//                
//                if (rm.getValueAt(15).equals("Y") || qty < 1) avail = "Not Available";
//                
//                
//               sb.append(DV.addSpace("Qty On Hand: "+rm.getValueAt(6), 24 ,' ') + "Reorder @ "+ 
//                       DV.addSpace(rm.getValueAt(18), 18, ' ')+
//                        "Status: " + avail + nl +
//            
//                
//                DV.addSpace("Cost  " + currency +CurrencyUtil.money(cost), 16,' ') +
//                        "  Cost Total  " + currency +CurrencyUtil.money(cost_total)  + nl+
//                
//                DV.addSpace("Price " + currency +CurrencyUtil.money(price), 16,' ') +
//                        "  Price Total " + currency +CurrencyUtil.money(price_total) + nl +
//                
//                DV.addSpace("UPC: "+rm.getValueAt(1), 19,' ') + DV.addSpace("Code: "+rm.getValueAt(2), 20,' ')+
//                       DV.addSpace(tax1name +": "+rm.getValueAt(13), 8,' ') + tax2name + ": "+rm.getValueAt(14) + nl + "Category: " + rm.getValueAt(9)); 
//                
//                
//                report.addParagraph(sb.toString(), true);
//         
//         
//         } while ( rm.next() ) ;
//        
//          
//          sb = new StringBuilder();
//          
//          //add line to paragraph???
//          sb.append(nl+"_____________________________________________________" + nl);
//          sb.append("           Total Cost of Goods On Hand  " + currency +CurrencyUtil.money(running_cost)+nl);
//          sb.append("   Total Retail Value of Goods On Hand  " + currency +CurrencyUtil.money(running_retail)+nl);
//          sb.append("        Total Number of Goods In-Stock  "+Float.toString(goods)+nl);          
//          
//          report.addParagraph(sb.toString(), true);
//          
//        report.finish();
//        
//        if ( rd.isView() ){  //view print options
//            
//            return ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//                          
//        }
//        if (rd.getWinPrn()) {
//            
//            ReportFactory.windowsFastPrint(file, props);
//            
//        } 
//        
//        return "";
//    }
//    
//    
//public static String generateReorderReport (DbEngine dbsys, Settings props) {
//    
//        /* Get the date period and report title from the user. */
//        ReportDialog rd = new ReportDialog (null, true, props.getProp("REPORT FOLDER"), false, props.getProp("CO NAME")+ " Reorder Report", props);
//        
//        /* Get the currency symbol from the user settings  */
//        String currency = props.getProp("SYM");
//        
//        if (rd.getState() == false) return "";
//                        
//        String nl = System.getProperty("line.separator");
//        
//        /* Get a sortable table model from inventory */
//        TableSorter ts = (TableSorter) dbsys.createTableModel("inventory", null);
//        
//        ts.setSortingStatus(9, 1);  //Sort by category
//        ts.setSortingStatus(3, 1);  //Sort by description
//        
//        ReportModel rm = new ReportModel (ts);  //VAR
//               
//        String file = rd.getFile() + "ReorderReport_" + DV.getShortDate().replace('/','-')+".pdf";  //add date
//        
//        if (!DV.isFileAccessible(file, "PDF file")) return "WARNING: "+file+ " was open by another program." + "  Close your pdf reader (Adobe?) and try again."+nl;
//                
//        PdfReportService report = new PdfReportService (file);
//        
//        report.setReportTitle(rd.getTitle());
//        report.setWatermark(props.getProp("WATERMARK"));
//        report.setWatermarkEnabled(Boolean.parseBoolean(props.getProp("PRINT WM")));
//        
//        
//        Paragraph p;  //PDF Stuff from iText
//        StringBuilder sb;
//        
//        double cost = 0.00;
//        float qty = 0;
//        int cutoff = 1000;
//        int howmany = 0;
//        boolean service = false;
//
//        String avail = "REORDER";
//        String suppliers;
//        
//        Object [] conn = new Object [17];
//        
//        do {
//           
//                sb = new StringBuilder ();
//                qty = Float.parseFloat(rm.getValueAt(6));  //qty
//                cutoff = Integer.parseInt(rm.getValueAt(18)); //cutoff
//                cost = Float.parseFloat(rm.getValueAt(7));  //cost
//                service = rm.getValueAt(9).equals("Service");
//
//                if (qty <= cutoff && !service){
//                      howmany++;
//                      
//                    /* Get supplier info */
//                    
//                    for (int i=10; i < 13; i++){  //check each supplier
//                        
//                        if (DV.validIntString(rm.getValueAt(i)) && Integer.parseInt(rm.getValueAt(i)) > 0 ){
//                        
//                            conn = dbsys.getRecord("conn", Integer.parseInt(rm.getValueAt(i)));
//                            
//                            if (i == 10 || i == 11) sb.append(new String ((String)conn[1] + nl));
//                            else sb.append(new String ((String)conn[1]));
//                            
//                        }
//                        
//                    }
//                    
//                    suppliers = sb.toString();  //got the suppliers
//                    
//                    sb = new StringBuilder ();
//                    /* END */
//                                      
//                    sb.append(nl);          
//                                
//                    sb.append(DV.addSpace(rm.getValueAt(3), 52,' ')+DV.addSpace("Size:"+rm.getValueAt(4), 17,' ') +
//                            "Wt:"+rm.getValueAt(5)+ nl );
//                    
//                    sb.append(DV.addSpace("Qty On Hand: "+rm.getValueAt(6), 24 ,' ') + "Reorder @ "+ 
//                       DV.addSpace(rm.getValueAt(18), 18, ' ')+
//                        "Status: " + avail + nl +
//            
//                
//                    DV.addSpace("Cost  " + currency +CurrencyUtil.money(cost), 16,' ')+
//                    
//                    DV.addSpace("UPC: "+rm.getValueAt(1), 20,' ') + DV.addSpace("Code: "+rm.getValueAt(2), 23,' ') +
//                            "Cat: "+rm.getValueAt(9) + nl +
//                            "____________________________________________"+nl +
//                            suppliers );
//                
//                
//                    report.addParagraph(sb.toString(), true);
//         
//                }else continue;               
//                
//        }while ( rm.next() ) ;
//          
//        if (howmany < 1) {
//            javax.swing.JOptionPane.showMessageDialog(null, "No items in reorder status.");
//            return "";
//        }
//        report.finish();
//        
//        if ( rd.isView() ){  //view print options
//            
//            return ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             //DV.launchURL('"' + file + '"');
//             
//        }
//        if (rd.getWinPrn()) {
//            
//            ReportFactory.windowsFastPrint(file, props);
//        } 
//        
//        return "";
//}    
// 
// public static String veiwPDF (String acro, String file, Settings props) {
//     boolean desktop = DV.parseBool(props.getProp("DESKTOP SUPPORTED"), true);
//     if (Desktop.isDesktopSupported() && desktop){         
//         if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)){
//                try {
//                    File f = new File(file);
//                    Desktop.getDesktop().open(f);
//                    return "";
//                } catch (IOException ex) {
//                  javax.swing.JOptionPane.showMessageDialog(null,
//                          "There was a problem trying to view the pdf with the default viewer, Nevitium will now try to use the viewer found in Settings->Output. ");
//
//                }
//             
//         }
//     }
//
//     /* Old Slow Method of launching PDFs (Before JDK 6)*/
//    String pdfLocation = "";
//    String a="";
//    String nl = System.getProperty("line.separator");
//    String osName = System.getProperty("os.name").toLowerCase();
//    boolean mac = osName.contains("mac");
//    boolean unix = osName.contains("nix");
//    boolean linux = osName.contains("nux");
//    boolean windows = osName.contains("windows");
//
//    boolean debug = false;
//   
//    
//    if (debug) System.out.println("ReportFactory:viewPDF:osName="+osName);
//
//    try {
//        if(windows){
//            pdfLocation = '"' + acro + '"'+ " "+'"' + file.replace('/','\\')+'"';
//            if (debug) System.out.println(pdfLocation);
//            Runtime.getRuntime().exec(pdfLocation);
//        }
//        if (linux || unix || mac){
//            File wd = new File("/bin");
//            Process proc = null;
//
//            proc = Runtime.getRuntime().exec("bash", null, wd);
//
//            if (proc != null) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
//                out.println(acro + " " +'"'+ file + '"');
//                out.println("exit");
//                try {
//                    String line;
//                    while ((line = in.readLine()) != null) {
//                        System.out.println(line);
//                    }
//                    proc.waitFor();
//                    in.close();
//                    out.close();
//                    proc.destroy();
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//       
//    } catch (IOException ex) {
//        a = "error: There was a problem launching your PDF reader." + nl +
//        "Verify 'PDF Reader' in the Output tab. (File Menu --->Settings)";
//    }
//    return a;
// }   
//    
//  public static void windowsFastPrint (String file, Settings props){
//
//      boolean desktop = DV.parseBool(props.getProp("DESKTOP SUPPORTED"), true);
//     if (Desktop.isDesktopSupported() && desktop){
//         if (Desktop.getDesktop().isSupported(Desktop.Action.PRINT)){
//                try {
//                    Desktop.getDesktop().print(new File(file));
//                    return;
//                } catch (IOException ex) {
//                  //try the old manual method below
//                }
//
//         }
//     }else {
//          javax.swing.JOptionPane.showMessageDialog(null,
//                  "Your OS is not reporting a default application () for printing this type of file: "+file);
//     }
//
//
//      
//  } 
//   
//
//    public static boolean generateStatements (GlobalApplicationDaemon application, int key) {
//
//        DbEngine dbsys = application.getDb();
//        Settings props = application.getProps();
//
//
//        /* Get users ink saver option setting */       
//        boolean ink = Boolean.parseBoolean(props.getProp("INK SAVER"));
//        
//        float std_row_height=14f;  //row height to account for added rows to fill incomplete pages
//        
//        int headerwidths[] = { 12, 15, 40, 12, 12};  //header widths for invoice detail
//        
//         /* 
//            lt cyan  191, 236, 238
//            lt green 209, 254, 207
//            lt red   249, 176, 184  
//            lt yell  248, 253, 142
//            lt blue  198, 198, 253
//        */
//
//
//        String color = props.getProp("STCOLOR");
//
//        java.awt.Color rowColor = Tools.stringToColor(color);
//
//
//        String title = "Invoice Statement";        
//        ReportDialog rd =
//                new ReportDialog
//                (null, true,props.getProp("REPORT FOLDER"), false,
//                props.getProp("CO NAME")+ title, props, true, false);
//        
//        if (!rd.getState()) return false;
//                
//        
//        /* Construct Header */
//        /* Print payment details */
//        /* Print Summary */
//        
//        
//        PDFInvoice inv = null;
//        PdfPTable items= null;
//        
//        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//        
//        //for (int i = 0; i < keys.size(); i++){
//        
//            /* Get invoice and make sure it has payments */
//            //Object [] the_invoice = db.getRecord("invoice", (Integer) key);
//            OldInvoice invoice = new OldInvoice(application, (Integer) key);
//            //ArrayList al = db.search("payments", 1, (String) the_invoice[1], false);
//
//            //if (al == null) continue;
//            
//            String num = invoice.getInvoiceNumber();
//
//            String file = rd.getFile() + "Statement_"+ num + '_'+DV.getShortDate().replace('/', '-') + ".pdf";  //folder + invoice number + extension
//        
//            if (!DV.isFileAccessible(file, "PDF file")) return false;
//            
//            javax.swing.table.DefaultTableModel invoicePayments = invoice.getPayments();
//            
//            String nl = System.getProperty("line.separator");
//            String type = "S T A T E M E N T";
//                 
//                    
//        /*  START TABLE WORK  */
//        
//        String company = props.getProp("CO NAME")+ nl +
//                    props.getProp("CO ADDRESS")+ nl +
//                    props.getProp("CO CITY")+ nl +
//                    props.getProp("CO PHONE");
//
//        String currency = props.getProp("SYM");
//             
//        PdfPTable head = new PdfPTable (2);  //setup a two col header table
//        //head.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//        
//        PdfPTable foot = new PdfPTable(2); //Setup a three col footer table
//        //foot.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//        
//        try {
//            //insert font stuff here
//                       
//            String prop_font = props.getProp("FONT");
//            String prop_font_size = props.getProp("FONT SIZE");
//            String prop_font_style = props.getProp("FONT STYLE");
//            
//            int pts=14;  //default 
//            int sty = Font.NORMAL;
//            
//            try {
//                
//                pts = Integer.parseInt(prop_font_size);
//                
//            } catch (NumberFormatException ex) {
//               
//                pts = 14; //default
//                
//            }
//            
//           /* if (prop_font_style.equals("NORMAL"));
//            else sty = Font.BOLD;*/
//                        
//            Font font;
//                        
//            if (prop_font.equalsIgnoreCase("Times New Roman") || prop_font.equalsIgnoreCase("Roman")) {
//                font = new Font (Font.TIMES_ROMAN, pts, sty);
//                
//            }else if (prop_font.equalsIgnoreCase("Helvetica")){
//                font = new Font (Font.HELVETICA, pts, sty);
//                
//            }else if (prop_font.equalsIgnoreCase("Courier")) {
//                
//                font = new Font (Font.COURIER, pts, sty);
//                
//            }else {
//                
//               font = new Font (Font.HELVETICA, pts, sty);              
//                
//            }
//            
//            PdfPCell cell;
//        
//            /** 
//             *
//             * Build the header table
//             *
//             */
//            
//            java.io.File f = new java.io.File (props.getProp("LOGO"));
//            
//            if (f.exists()){
//                
//                cell = new PdfPCell (Image.getInstance(props.getProp("LOGO")), true);
//                
//            }else {
//                
//                cell = new PdfPCell (new Phrase(company, font));
//                
//            }
//            
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(100);
//            head.addCell(cell);
//            
//            font = new Font (Font.COURIER, 12, Font.NORMAL);
//            
//            cell = new PdfPCell (new Phrase(type, font));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//            cell.setFixedHeight(100);
//            head.addCell(cell);
//            String s = "FOR:";
//            
//            
//            cell = new PdfPCell (new Phrase(s + nl + invoice.getCustomer(), font));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(80);
//            cell.setVerticalAlignment(Rectangle.TOP);
//            
//            head.addCell(cell);
//            
//            cell = new PdfPCell (new Phrase(""));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(80);
//            head.addCell(cell);
//            
//            
//            PdfPTable dateline = new PdfPTable (3);
//            
//            dateline.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//            
//            int [] widths = new  int [] {33,33,33 };
//            dateline.setWidths(widths);
//            
//            font = new Font (Font.COURIER, 10, Font.NORMAL);  //get new font
//            
//            cell = new PdfPCell(new Phrase (DV.getShortDate(), font) );
//            cell.setBorder(Rectangle.BOX);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell);
//            
//            //Version 1.5
//            cell = new PdfPCell(new Phrase ("Inv Date:" + df.format(new Date(invoice.getDate())), font) );
//            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell); 
//            
//            cell = new PdfPCell(new Phrase ("Invoice # "+ num, font));
//            cell.setBorder(Rectangle.BOX);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell);
//            
//            dateline.setTotalWidth(com.lowagie.text.PageSize.LETTER.width());
//            
//            cell = new PdfPCell(dateline);  //put this tble in a cell
//            
//            cell.setBorder(Rectangle.BOX);
//            cell.setColspan(2);
//            
//            head.addCell(cell);  //magic method
//            
//                    
// /*  END HEADER TABLE */          
//            
//            /** 
//             *
//             * Build the Item header for the last row of the header table
//             *
//             */
//            
//            PdfPTable itemHeader = new PdfPTable (5);
//            
//                
//        itemHeader.setWidths(headerwidths);
//         
//        font = new Font (Font.TIMES_ROMAN, 10, Font.BOLD);
//        
//        cell = new PdfPCell (new Phrase("Date", font));
//        
//        cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Type", font));
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Memo", font));
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Amount " + currency, font));
//        
//        itemHeader.addCell(cell);
//                
//        itemHeader.addCell(new PdfPCell (new Phrase("Balance", font)));
//        
//        itemHeader.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//        
//        cell = new PdfPCell(itemHeader);
//        cell.setColspan(2);
//                
//        cell.setPaddingTop(5);      //space between itemheader and dateline
//        
//        cell.setBorder(Rectangle.NO_BORDER);
//        
//        head.addCell(cell);
//        
//        inv = new PDFInvoice (file, head);
//
//        //TODO: get watermark pref from settings
//        inv.setWatermarkEnabled(false);  //set watermark for statements
//        
// /*  END ITEM HEADER TABLE */   
//        
//        
//        
//        /** 
//             *
//             *  build the summary table
//             *
//             */
//        int ftheaderwidths[] = { 70, 30};
//         font = new Font (Font.COURIER, 10, Font.NORMAL);
//        
//       
//        foot.setWidths(ftheaderwidths);
//         
//         cell = new PdfPCell (new Phrase("Notes:", font));  //this stays
//         foot.addCell(cell);
//                  
//         /*
//          * check paidCheckBox
//          * if paid get last payment and balance due  
//          * if not config default vals
//          *
//          *
//          */
//         float invoice_total = invoice.getInvoiceTotal();
//         float interest_total = 0.00f;
//         float total_payments = 0.00f;
//         float total_refunds = 0.00f;
//         float total_returns = 0.00f;
//         float balance_due = invoice.getTotalDue();
//
//         //Calculate interest for this report and add to the total due
//         // Change Invoice to do this
//         String paymentType;
//         float debitAmount;
//         float creditAmount;
//         
//         
//         /* Loop payments and figure totals for the above floats */
//        for (int x = 0; x < invoicePayments.getRowCount(); x++){
//             
//             paymentType = (String) invoicePayments.getValueAt(x, 3);
//             debitAmount = (Float) invoicePayments.getValueAt(x, 5);
//             creditAmount = (Float)invoicePayments.getValueAt(x, 6);
//             
//             if (paymentType.equalsIgnoreCase("Refund")) total_refunds += debitAmount;  //change refunds to negative for the statement
//             
//             if ( paymentType.equalsIgnoreCase("Cash")  ||
//                     paymentType.equalsIgnoreCase("Check")  ||
//                     paymentType.equalsIgnoreCase("CC")) total_payments += creditAmount;
//             
//             if (paymentType.equalsIgnoreCase("Interest")) interest_total += debitAmount;  //change interest to positive
//             
//             if (paymentType.equalsIgnoreCase("Return")) total_returns += creditAmount;  //change returns to negative
//             
//             
//        }
//         
//         PdfPTable totals = new PdfPTable(2);
//                  
//         totals.addCell(new Phrase("Invoice", font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(invoice_total), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //invoice grand total
//         
//         
//         totals.addCell(new Phrase("Interest", font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(interest_total), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  // total intetrest charges
//         
//         
//         totals.addCell(new Phrase("Total", font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(invoice_total + interest_total), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //total of invoice total and interest
//         
//         
//         totals.addCell(new Phrase("Returns", font));//other
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(total_returns), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //
//         
//         
//         totals.addCell(new Phrase("Payments", font));
//                  
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(total_payments), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //total payments
//         
//         
//         totals.addCell(new Phrase("Refunds", font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(total_refunds), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //total refunds
//                  
//         String bd = "BAL DUE  " + currency;
//         //if (bal_due < 0) bd = "CREDIT";
//         
//         totals.addCell(new Phrase(bd, font));   
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(balance_due), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //current balance due
//                  
//         totals.setTotalWidth(130);
//         
//         foot.addCell(totals);
//         
//         inv.addTagline(props.getProp("CO NAME") + " / " + props.getProp("CO ADDRESS") + " / " + 
//                 props.getProp("CO CITY") + " / " + props.getProp("CO PHONE"));
//         
//         //cell = new PdfPCell (new Phrase(tagline, inv.getDocFont(-2,0)));
//         //cell.setColspan(2);
//         
//         //foot.addCell(cell);   
//         cell = new PdfPCell (new Phrase("Page: ", inv.getDocFont(-4,0)));
//         cell.setColspan(2);
//         
//         foot.addCell(cell);   
//         
//         inv.setFooter(foot);  //magic method
//                  
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (BadElementException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//         catch (DocumentException ex) {
//                ex.printStackTrace();
//            }
//           
// /*  END SUMMARY TABLE */                       
//            
//        
//            /**
//             * Build actual Items table
//             *
//             */
//           
//            ReportModel rm = new ReportModel(invoicePayments);
//            
//            
//            
//            int cols = invoicePayments.getColumnCount();
//            int rows = invoicePayments.getRowCount();
//                      
//            items = new PdfPTable (5);
//            
//        try {
//            
//            //items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//            items.setWidths(headerwidths);
//            
//        } catch (DocumentException ex) {
//            ex.printStackTrace();
//        }
//            
//            Font f = new Font (Font.COURIER, 10, Font.NORMAL);
//            PdfPCell cell = new PdfPCell ( new Phrase ( "", f) );
//            int a = 1;
//            
//            do {
//                
//            for (int c = 2; c < cols; c++) {   //start grabbing data after invoice and inventory keys
//                
//
//                /* these if clauses effectivly skip either the debit or credit col as output
//                 based on their value being zero
//                 */
//                if (c == 5){
//                    
//                    
//                    float debit=0.00f;
//                    try{
//                        debit = (Float)rm.getRealValue(c);
//                    }catch(ClassCastException e){
//                        
//                    }
//                    
//                    if (debit > 0) {
//                        cell = new PdfPCell ( new Phrase (rm.getValueAt(c), f) );
//
//                    }else continue;
//
//                }
//                if (c == 6){
//                    
//                    float credit=0.00f;
//                    try{
//                        credit = (Float)rm.getRealValue(c);
//                    }catch(ClassCastException e){
//                        
//                    }
//
//                    if (credit > 0) {
//                        cell = new PdfPCell (new Phrase("-"+rm.getValueAt(c), f) );
//                    }else continue;
//
//                }
//                if (c != 5 && c != 6)cell = new PdfPCell ( new Phrase ( rm.getValueAt(c), f) );
//                
//                cell.setBorder(Rectangle.NO_BORDER);
//                
//                if (c == cols-2 || c == cols -1) {
//
//                        cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//                        
//                   } 
//             
//                if ((a % 2) != 0 ) {
//                    
//                    if (!ink) cell.setBackgroundColor(rowColor);
//                    //cell.setGrayFill(.80f);
//                }
//                
//                items.addCell(cell);                
//            }         
//         a++;      
//                  
//         } while ( rm.next() ) ;
//         
//            items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//            
// /*  END ITEMS TABLE */ 
//            /**
//             *
//             *  Make the invoice pretty
//             *  COSMETICS
//             *  (Skipped if 1 page or more)
//             *
//             */
//            
//            float pages = inv.howManyPages(items);  //how many pages will it take to write invoice
//                                   
//            
//            
//            if (pages < 1) {   //if less than a full page fill it out
//             
//                pages = pages % 1.0f;
//             // head.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);
//             // foot.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);
//              
//            float hf = inv.getHeaderSize() + inv.getFooterSize();
//            
//            float bs = inv.getBodySize() - 10 ;  //fudge
//                       
//            float remain = bs - items.getTotalHeight();  //how much space is left after the table
//            
//            //System.out.println(remain);
//            
//            float row_height = std_row_height;    //standard row height??  15points
//                      
//            float z =  remain / row_height;  //how many Standard rows can be written to fill (no remainder) 
//             
//            boolean odd = true;
//
//            if (items.getRows().size() % 2 == 0)  odd = false;
//            for (int l = 1; l < z; l++){                    
//                for (int x = 2; x < cols-1; x++) {   //simulate the data loop                
//                    cell = new PdfPCell ( new Phrase ( " ", f) );  //place ' ' instead of data
//                                
//                    cell.setBorder(Rectangle.NO_BORDER);                       
//                    cell.setFixedHeight(row_height);
//                            
//                    if (!odd ) {
//                     if (!ink) cell.setBackgroundColor(rowColor);
//                    //cell.setGrayFill(.80f);
//                    }                
//                    items.addCell(cell);                            
//                }
//            
//                if (odd) odd = false;
//                else  odd = true;
//                
//            }
//            
//            }
// 
///*  END COSMETICS */               
//            
//            /*  END INVOICE TABLES  */
//            
//        
//        
//            
//            inv.setBody(items);   //add the items table to the PDFInvoice        
//            
//            inv.build(); 
//      
//            //}/* End huge for-loop */
//            
//            inv.finish();
//             
//            if ( rd.isView() ){
//            
//                ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             
//            }
//             
//            if (rd.getWinPrn()) {
//            
//                 ReportFactory.windowsFastPrint(file, props);
//            
//             }
//
//            if (rd.isEmail()){
//                String addr = invoice.getEmail();
//                if (Tools.verifyEmailAddress(addr)){
//                NewEmail email = new NewEmail();
//                email.setAttachment(file);
//                email.setRecipent(addr);
//                email.setText("Statement for invoice " +invoice.getInvoiceNumber()+" From "+props.getProp("CO NAME"));
//                email.setSubject("Statement for invoice " +invoice.getInvoiceNumber()+" From "+props.getProp("CO NAME"));
//                email.setFrom(props.getProp("EMAIL ADDRESS"));
//                email.setServer(props.getProp("EMAIL SERVER"));
//                email.setPort(props.getProp("EMAIL PORT"));
//                email.setUsername(props.getProp("EMAIL USER"));
//                email.setPassword(props.getProp("EMAIL PWD"));
//                email.setSSL(DV.parseBool(props.getProp("SSL"), false));
//                email.sendEmail();
//                }
//                
//            }
//        
//        
//        return true;
//        
//    }
//
//  
// public static boolean generateCustomerStatement (GlobalApplicationDaemon application, Contact contact) {
//        DbEngine db = application.getDb();
//        Settings props = application.getProps();
//
//        /* Get users ink saver option setting */       
//        boolean ink = Boolean.parseBoolean(props.getProp("INK SAVER"));
//        
//        float std_row_height=14f;  //row height to account for added rows to fill incomplete pages
//        
//        int headerwidths[] = { 12, 15, 28, 12, 12, 12};  //header widths for invoice detail
//        
//         /* 
//            lt cyan  191, 236, 238
//            lt green 209, 254, 207
//            lt red   249, 176, 184  
//            lt yell  248, 253, 142
//            lt blue  198, 198, 253
//        */
//        
//        java.awt.Color rowColor = new java.awt.Color(198, 198, 253);
//         
//        /* Report Title */           
//        String title = "Customer Invoice History";        
//
//        if (contact == null) title = "Misc Invoice History";
//
//        ReportDialog rd = new ReportDialog (null, true,props.getProp("REPORT FOLDER"), false, title, props);
//        
//        if (!rd.getState()) return false;
//                
//        ReportModel payments = null;
//        
//        /* Report Title */ 
//        title = rd.getTitle();
//        
//        /* Construct Header */
//        /* Print payment details */
//        /* Print Summary */
//        
//        
//        PDFInvoice inv = null;
//        PdfPTable items= null;
//        
//        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//                   
//            /* PDF output filename */
//            String file = rd.getFile() + "Customer History "+DV.getShortDate().replace('/', '-') + ".pdf";  
//        
//            if (!DV.isFileAccessible(file, "PDF file")) return false;
//            
//           /* New Line */
//            String nl = System.getProperty("line.separator");
//            
//           
//        /*  START TABLE WORK  */
//        
//        String company = props.getProp("CO NAME")+ nl +
//                    props.getProp("CO ADDRESS")+ nl +
//                    props.getProp("CO CITY")+ nl +
//                    props.getProp("CO PHONE");
//          
//        String statement = " BAL DUE";
//            
//        String currency = props.getProp("SYM");
//      
//        PdfPTable head = new PdfPTable (2);  //setup a two col header table
//       
//        PdfPTable foot = new PdfPTable(2); //Setup a three col footer table
//        
//        /* these locations need defined outside the try */
//        PdfPCell cell;
//        int cols = 7;
//        Font font;
//        
//        try {
//            
//            //insert font stuff here
//                       
//            String prop_font = props.getProp("FONT");
//            String prop_font_size = props.getProp("FONT SIZE");
//            String prop_font_style = props.getProp("FONT STYLE");
//            
//            int pts=14;  //default 
//            int sty = Font.NORMAL;
//            
//            try {
//                
//                pts = Integer.parseInt(prop_font_size);
//                
//            } catch (NumberFormatException ex) {
//               
//                pts = 14; //default
//                
//            }
//            
//            if (prop_font_style.equals("NORMAL"));
//            else sty = Font.BOLD;
//                        
//            
//                        
//            if (prop_font.equals("ROMAN")) font = new Font (Font.TIMES_ROMAN, pts, sty);
//            else font = new Font (Font.COURIER, pts, sty);
//                        
//            
//        
//            /** 
//             *
//             * Build the header table
//             *
//             */
//            
//            java.io.File logoFile = new java.io.File (props.getProp("LOGO"));
//            
//            if (logoFile.exists()){
//                
//                cell = new PdfPCell (Image.getInstance(props.getProp("LOGO")), true);
//                
//            }else {
//                
//                cell = new PdfPCell (new Phrase(company, font));
//                
//            }
//            
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(100);
//            head.addCell(cell);
//            
//            font = new Font (Font.COURIER, 12, Font.NORMAL);
//            
//            cell = new PdfPCell (new Phrase(title, font));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//            cell.setFixedHeight(100);
//            head.addCell(cell);
//            
//            /* Build customer address */
//
//            String str = "S A L E";
//
//            if (contact != null){
//                
//                String [] addr = Tools.formatAddress(contact);
//                str = addr[0] + addr[1] + addr[2] + addr[3] + addr[4];                
//            }
//
//            //cell = new PdfPCell (new Phrase(s + nl + (String) the_invoice[3], font));
//            
//            cell = new PdfPCell (new Phrase(str, font));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(80);
//            cell.setVerticalAlignment(Rectangle.TOP);
//            
//            head.addCell(cell);
//            
//            cell = new PdfPCell (new Phrase(""));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(80);
//            head.addCell(cell);
//            
//            /* Build the date line of the header */
//            PdfPTable dateline = new PdfPTable (3);
//            
//            
//            dateline.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//            
//            /* Set width of each cell in percent of the total width */
//            int [] widths = new  int [] {33,33,33 };
//            dateline.setWidths(widths);
//            
//            /* Get new font */
//            font = new Font (Font.COURIER, 10, Font.NORMAL);  
//            
//            /* Date Header Cell 1  */
//            cell = new PdfPCell(new Phrase ("", font) );
//            cell.setBorder(Rectangle.BOX);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell);
//            
//            /* Date Header Cell 2  */
//            cell = new PdfPCell(new Phrase ("", font) );
//            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell); 
//            
//            /* Date Header Cell 3  */
//            cell = new PdfPCell(new Phrase ("Report Date: " + df.format(new Date()), font));
//            cell.setBorder(Rectangle.BOX);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell);
//            
//            dateline.setTotalWidth(com.lowagie.text.PageSize.LETTER.width());
//            
//            cell = new PdfPCell(dateline);  //put this dateline table in a cell
//            
//            cell.setBorder(Rectangle.BOX);
//            cell.setColspan(2);
//            
//            head.addCell(cell);  
//            
//                    
// /*  END HEADER TABLE */          
//            
//            /** 
//             *
//             * Build the Item header for the last row of the header table
//             *
//             */
//            
//            PdfPTable itemHeader = new PdfPTable (6);
//            
//                
//        itemHeader.setWidths(headerwidths);
//         
//        font = new Font (Font.TIMES_ROMAN, 10, Font.BOLD);
//        
//        cell = new PdfPCell (new Phrase("Date", font));
//        
//        cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Type", font));
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Memo", font));
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Debit " + currency, font));
//        
//        itemHeader.addCell(cell);
//                
//        cell = new PdfPCell (new Phrase("Credit " + currency, font));
//        
//        itemHeader.addCell(cell);
//
//        itemHeader.addCell(new PdfPCell (new Phrase("Balance", font)));
//        
//        itemHeader.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//        
//        cell = new PdfPCell(itemHeader);
//        cell.setColspan(2);
//                
//        cell.setPaddingTop(5);      //space between itemheader and dateline
//        
//        cell.setBorder(Rectangle.NO_BORDER);
//        
//        head.addCell(cell);
//        
//        inv = new PDFInvoice (file, head);
//        
//        inv.setWatermarkEnabled(false);  //set watermark for statements
//        
// /*  END ITEM HEADER TABLE */   
//        
//        
//            /**
//             * Build actual Items table
//             *
//             */
//            StringBuilder sb;
//            
//          
//            Font f = new Font (Font.COURIER, 10, Font.NORMAL);
//            //PdfPCell cell;
//            int itemsRow = 1;
//            
//            /* This whole section was moved to before the summary table */
//            
//            float invoicesTotal = 0.00f;
//            float interestTotal = 0.00f;
//            float totalPayments = 0.00f;
//            float totalTax1 = 0.00f;
//            float totalTax2 = 0.00f;
//            float totalRefunds = 0.00f;
//            float totalReturns = 0.00f;
//            
//            /* Print debug messages */
//            boolean debug = false;
//            
//            
//            /* Gather all invoices for this customer into a List then table */
//            
//            ArrayList invoiceList = null; //db.search("invoice", 11, Integer.toString(custKey) , false);
//                       
//            
//            if (invoiceList == null){
//                
//                javax.swing.JOptionPane.showMessageDialog(null, "No invoices for this customer.");
//                return false;  //No invoices, Get out
//                
//            }
//            
//            DefaultTableModel customerInvoices = 
//                    (javax.swing.table.DefaultTableModel)db.createTableModel("invoice", invoiceList, false);
//
//            int customer_rows = customerInvoices.getRowCount();
//            boolean vd;
//            for (int r=0; r < customer_rows; r++){
//                vd = (Boolean)customerInvoices.getValueAt(r, 9);
//                if (vd) {
//                    customerInvoices.removeRow(r);
//                    customer_rows = customer_rows-1;
//                    r--;
//                }
//
//            }
//
//            if (customerInvoices.getRowCount() < 1){
//                javax.swing.JOptionPane.showMessageDialog(null, "No invoices for this customer.");
//                return false;  //No invoices, Get out
//            }
//
//            /* Setup payments list and table for repeated use later on */
//            ArrayList paymentList;
//            DefaultTableModel invoicePayments;
//            
//            /* Get number of invoices for this customer and exit if none. */
//            int numInvoices = customerInvoices.getRowCount();
//            
//            String invoiceNumber;
//            int invoiceKey;
//            String invoiceDate;
//            String paymentType = "Inv";
//            String invoiceBalance;
//            float invBalance;
//            
//            ReportModel rm;
//            items = new PdfPTable (6);
//            try {
//            
//            //items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//            /*Constucts the table so that each added cell is the proper size*/
//            items.setWidths(headerwidths);  
//            
//        } catch (DocumentException ex) {
//            ex.printStackTrace();
//        }
//            //payments = new ReportModel();
//            
//         for (int i=0; i < numInvoices; i++){  //invoice loop based on customerInvoices
//                
//                invoiceNumber = (String)customerInvoices.getValueAt(i,1);
//                invoiceKey = (Integer)customerInvoices.getValueAt(i, 0);
//                invoiceDate = df.format(new Date((Long)customerInvoices.getValueAt(i, 2)));
//                paymentType = "Invoice";
//                paymentType = props.getProp("INVOICE NAME");
//                invBalance = (Float)customerInvoices.getValueAt(i, 10);
//                invoiceBalance = CurrencyUtil.money(invBalance);
//                
//                /* Keep running total of invoice balances */
//                invoicesTotal += invBalance;
//                if (debug) System.out.println(" " + invoicesTotal);
//                                               
//                /* List invoice on statement */
//                
//                /* Invoice Date */
//                cell = new PdfPCell ( new Phrase ( invoiceDate, f) );
//                if ((itemsRow % 2) != 0 ){
//                if (!ink) cell.setBackgroundColor(rowColor);
//                }
//                items.addCell(cell);
//                
//                
//                /*Invoice type */
//                cell = new PdfPCell ( new Phrase ( paymentType, f) );
//                if ((itemsRow % 2) != 0 ){
//                if (!ink) cell.setBackgroundColor(rowColor);
//                }
//                items.addCell(cell);
//                
//                /*Invoice number */
//                cell = new PdfPCell ( new Phrase ( invoiceNumber, f) );
//                if ((itemsRow % 2) != 0 ){
//                if (!ink) cell.setBackgroundColor(rowColor);
//                }
//                items.addCell(cell);
//                
//                /*Debit */
//                cell = new PdfPCell ( new Phrase ( "", f) );
//                if ((itemsRow % 2) != 0 ){
//                if (!ink) cell.setBackgroundColor(rowColor);
//                }
//                items.addCell(cell);
//
//                /*Credit */
//                cell = new PdfPCell ( new Phrase ( "", f) );
//                if ((itemsRow % 2) != 0 ){
//                if (!ink) cell.setBackgroundColor(rowColor);
//                }
//                items.addCell(cell);
//
//                /*Invoice balance */
//                cell = new PdfPCell ( new Phrase ( invoiceBalance, f) );
//                if ((itemsRow % 2) != 0 ){
//                if (!ink) cell.setBackgroundColor(rowColor);
//                }
//                cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//                items.addCell(cell);
//                
//                //itemsRow++;
//                OldInvoice theInvoice = new OldInvoice(application, invoiceKey);
//                /* Use paymentList & table now */
//                //paymentList = db.search("payments", 1, invoiceNumber, false);
//                //paymentList = theInvoice.getPayments();
//                //if (paymentList == null) continue;
//                
//                /* Check for null and add invoice data to report and continue */
//                //invoicePayments = (javax.swing.table.DefaultTableModel) db.createTableModel("payments", paymentList , false);
//                invoicePayments = theInvoice.getPayments();
//
//                int numPayments = invoicePayments.getRowCount();
//                if (numPayments < 1) continue;
//
//                /*  */
//                rm = new ReportModel(invoicePayments);
//                
//                cols = invoicePayments.getColumnCount();
//                
//                        
//                if (debug) System.out.println("Invoice Payments list size: "+cols);
//
//                totalTax1 += theInvoice.getTax1Total();
//                totalTax2 += theInvoice.getTax2Total();
//
//                /* Loop through payments and add the data to the items table */
//               
//                do {
//            
//                /* this happens inside the payments loop */
//                    /* this loop crosses over each data row in the payments 
//                     table for this invoice and grabs the needed data */
//                    for (int c = 2; c < cols; c++) {   //start grabbing data after invoice and inventory keys
//                
//                        /* Acces the report model to get the data */
//                        cell = new PdfPCell ( new Phrase ( rm.getValueAt(c), f) );
//                                                              
//                        cell.setBorder(Rectangle.NO_BORDER);
//                
//                        if (c== cols-3 || c == cols-2 || c == cols -1) {
//
//                                cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//                        
//                        } 
//                         
//                        /* Build totals for payments, refunds and returns here. */ 
//                        
//
//                        /* Check Type and process accordingly */
//                        if (c == 3) { 
//                            
//                            float debit = (Float)rm.getRealValue(5);
//                            float credit = (Float)rm.getRealValue(6);
//                            
//                            paymentType = rm.getValueAt(c);
//                            
//                            if (paymentType.equalsIgnoreCase("Return")){
//                                
//                                totalReturns += credit;
//                                
//                            }else if (paymentType.equalsIgnoreCase("Refund")) {
//                                
//                                    totalRefunds += debit;
//                                
//                            }else if (paymentType.equalsIgnoreCase("Interest")) {
//
//                                    interestTotal += debit;
//
//                            }else {
//                                
//                                    totalPayments += credit;
//                                
//                            }
//                            
//                            
//                        }//end type check
//                                       
//
//                            items.addCell(cell);
//                
//                    }//End col data grab loop
//                
//                    
//                    
//         } while (rm.next());
//                
//         itemsRow++;         
//                  
//         }//end invoices loop
//            
//         
//            items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//           
//            
//            /* END ITEM TABLE */
//        
//        /** 
//             *
//             *  build the summary table
//             *
//             */
//        int ftheaderwidths[] = { 70, 30};
//         font = new Font (Font.COURIER, 10, Font.NORMAL);
//        
//       
//        foot.setWidths(ftheaderwidths);
//         
//         cell = new PdfPCell (new Phrase("Notes:", font));  //this stays
//         foot.addCell(cell);
//                  
//         /*
//          * check paidCheckBox
//          * if paid get last payment and balance due  
//          * if not config default vals
//          *
//          *
//          */
//         
//         float balanceDue = (invoicesTotal - (totalPayments+totalReturns)) + totalRefunds;
//         String t;
//         float val;
//         
//         PdfPTable totals = new PdfPTable(2);
//                  
//         totals.addCell(new Phrase("Invoices", font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(invoicesTotal), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //invoice grand total
//                  
//         totals.addCell(new Phrase("Interest", font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(interestTotal), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  // total intetrest charges
//                  
//         totals.addCell(new Phrase("Credits", font));//other
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(totalPayments + totalReturns), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);         
//
//         totals.addCell(new Phrase("Refunds", font));
//
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(totalRefunds), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //total of invoice total and interest
//
//         String t1Name = props.getProp("TAX1NAME");
//
//         if (t1Name.equalsIgnoreCase("GST")) t1Name = "GST Content";
//         if (t1Name.equalsIgnoreCase("VAT")) t1Name = "VAT Content";
//
//         totals.addCell(new Phrase(t1Name, font));
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(totalTax1), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //total payments
//
//         String t2Name = props.getProp("TAX2NAME");
//         totals.addCell(new Phrase(t2Name, font));
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(totalTax2), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //total refunds
//        
//         totals.addCell(new Phrase("BAL DUE", font));   
//         
//         cell = new PdfPCell (new Phrase(CurrencyUtil.money(balanceDue), font));
//         cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//         totals.addCell(cell);  //current balance due
//                  
//         totals.setTotalWidth(130);
//         
//         foot.addCell(totals);
//         
//         inv.addTagline(props.getProp("CO NAME") + " / " + props.getProp("CO ADDRESS") + " / " + 
//                 props.getProp("CO CITY") + " / " + props.getProp("CO PHONE"));
//         
//                  
//         
//         cell = new PdfPCell (new Phrase("Page: ", inv.getDocFont(-4,0)));
//         cell.setColspan(2);
//         
//         foot.addCell(cell);   
//         
//         inv.setFooter(foot);  //magic method
//                  
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (BadElementException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//         catch (DocumentException ex) {
//                ex.printStackTrace();
//            }
//           
// /*  END SUMMARY TABLE */                       
//                        
//            font = new Font (Font.COURIER, 10, Font.NORMAL);
//            
//            /**
//             *
//             *  Make the invoice pretty
//             *  COSMETICS
//             *  (Skipped if 1 page or more)
//             *
//             */
//            
//            float pages = inv.howManyPages(items);  //how many pages will it take to write invoice
//                                   
//            
//            
//            if (pages < 1) {   //if less than a full page fill it out
//             
//                pages = pages % 1.0f;
//             // head.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);
//             // foot.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);
//              
//            float hf = inv.getHeaderSize() + inv.getFooterSize();
//            
//            float bs = inv.getBodySize() - 10 ;  //fudge
//                       
//            float remain = bs - items.getTotalHeight();  //how much space is left after the table
//            
//            //System.out.println(remain);
//            
//            float row_height = std_row_height;    //standard row height??  15points
//                      
//            float z =  remain / row_height;  //how many Standard rows can be written to fill (no remainder) 
//             
//            boolean odd = true;
//            if (items.getRows().size() % 2 == 0)  odd = false;
//            int d = 0; //added to compensate for misc report 01-20-2011
//            if (contact == null) d = 0;
//            /* cycle the amount of empty rows needed to fill page */
//            for (int l = 1; l < z; l++){
//                    
//                for (int x = 0; x < cols-2+d; x++) {   //simulate the data loop
//                
//                    cell = new PdfPCell ( new Phrase ( " ", font) );  //place ' ' instead of data
//                                
//                    cell.setBorder(Rectangle.NO_BORDER);
//                       
//                    cell.setFixedHeight(row_height);                       
//                   
//                    if (!odd ) {                   
//                    
//                     if (!ink) cell.setBackgroundColor(rowColor);
//                    //cell.setGrayFill(.80f);
//                                                      
//                    }                
//                    items.addCell(cell);                            
//                }            
//                if (odd) odd = false;
//                else  odd = true;
//                
//            }
//            
//            }
// 
///*  END COSMETICS */               
//            
//            /*  END INVOICE TABLES  */
//            
//            inv.setBody(items);   //add the items table to the PDFInvoice        
//            
//            inv.build(); 
//      
//            //}/* End huge for-loop */
//            
//            inv.finish();
//             
//            if ( rd.isView() ){
//            
//                ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             
//            }
//             
//            if (rd.getWinPrn()) {
//            
//                 ReportFactory.windowsFastPrint(file, props);
//            
//             }
//        
//        
//        return true;
//        
//    }
//    
//   
//
//    
//public static boolean generateWorkOrder(Settings props){
//    
//        
//        String currency = props.getProp("SYM");
//        
//        
//        float std_row_height=14f;  //row height to account for added rows to fill incomplete pages
//        int headerwidths[] = { 5, 18, 50, 11, 2, 2, 12};  //header widths for invoice detail
//        String nl = System.getProperty("line.separator");
//                    
//        ReportDialog rd = new ReportDialog (null, true, props.getProp("INVOICE FOLDER"), false, "Blank Work Order Printing", props);
//        if (!rd.getState()){
//            
//            javax.swing.JOptionPane.showMessageDialog(null,"PDF was NOT generated. Action canceled by user.");
//            
//            return false;
//        }
//         String type = "WORK ORDER"; //?????
//         
//        String file = rd.getFile()+"WorkOrder.pdf";
//
//        boolean debug = true;
//        if (debug) System.out.println(file);
//
//        if (!DV.isFileAccessible(file, "PDF file")) return false;
//        
//        
//        /*  START TABLE WORK  */
//        
//        String company = props.getProp("CO NAME")+ nl +
//                    props.getProp("CO OTHER")+ nl +
//                    props.getProp("CO ADDRESS")+ nl +
//                    props.getProp("CO CITY")+ nl +
//                    props.getProp("CO PHONE");
//          
//        String statement = " BAL DUE";
//        
//        PdfPTable head = new PdfPTable (2);  //setup a two col header table
//        //head.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//        
//        PdfPTable foot = new PdfPTable(2); //Setup a three col footer table
//        //foot.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//                
//        PDFInvoice inv = null;
//        
//        try {
//            
//            //company info font
//            String prop_font = props.getProp("FONT");
//            String prop_font_size = props.getProp("FONT SIZE");
//            String prop_font_style = props.getProp("FONT STYLE");
//            
//            int pts=14;  //default 
//            int sty = Font.NORMAL;
//            
//            try {
//                
//                pts = Integer.parseInt(prop_font_size);
//                
//            } catch (NumberFormatException ex) {
//               
//                pts = 14; //default
//                
//            }
//            
//      
//            Font font;
//                        
//            if (prop_font.equalsIgnoreCase("Times New Roman") || prop_font.equalsIgnoreCase("Roman")) {
//                font = new Font (Font.TIMES_ROMAN, pts, sty);
//                
//            }else if (prop_font.equalsIgnoreCase("Helvetica")){
//                font = new Font (Font.HELVETICA, pts, sty);
//                
//            }else if (prop_font.equalsIgnoreCase("Courier")) {
//                
//                font = new Font (Font.COURIER, pts, sty);
//                
//            }else {
//                
//               font = new Font (Font.HELVETICA, pts, sty);              
//                
//            }
//                        
//            PdfPCell cell;
//        
//            /** 
//             *
//             * Build the header table
//             *
//             */
//            
//            File f = new File (props.getProp("LOGO"));
//            
//            if (f.exists()){
//                
//                cell = new PdfPCell (Image.getInstance(props.getProp("LOGO")), true);
//                
//            }else {
//                
//                cell = new PdfPCell (new Phrase(company, font));
//                
//            }
//            
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(100);
//            head.addCell(cell);
//            
//            font = new Font (Font.COURIER, 12, Font.NORMAL);
//            
//            cell = new PdfPCell (new Phrase(type, font));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
//            cell.setFixedHeight(100);
//            head.addCell(cell);
//            String s = "FOR:";
//           
//            
//            cell = new PdfPCell (new Phrase("CUSTOMER: ", font));
//            cell.setBorder(Rectangle.BOX);
//            cell.setFixedHeight(80);
//            cell.setVerticalAlignment(Rectangle.TOP);
//            
//            head.addCell(cell);
//            
//            cell = new PdfPCell (new Phrase(""));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setFixedHeight(80);
//            head.addCell(cell);
//            
//            
//            PdfPTable dateline = new PdfPTable (3);
//            
//            dateline.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//            
//            int [] widths = new  int [] {33,33,33 };
//            dateline.setWidths(widths);
//            
//            font = new Font (Font.COURIER, 12, Font.NORMAL);  //get new font
//            
//            cell = new PdfPCell(new Phrase ("DATE: ", font) );
//            cell.setBorder(Rectangle.BOX);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell);
//            
//            dateline.addCell(""); 
//            
//            cell = new PdfPCell(new Phrase ("AGENT:", font)); //invoice number
//            cell.setBorder(Rectangle.BOX);
//            cell.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
//            cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//            cell.setPaddingBottom(3);
//            dateline.addCell(cell);
//            
//            dateline.setTotalWidth(com.lowagie.text.PageSize.LETTER.width());
//            
//            cell = new PdfPCell(dateline);  //put this tble in a cell
//            
//            cell.setBorder(Rectangle.BOX);
//            cell.setColspan(2);
//            
//            head.addCell(cell);  //magic method
//            
//                    
// /*  END HEADER TABLE */          
//            
//            /** 
//             *
//             * Build the Item header for the last row of the header table
//             *
//             */
//            
//            PdfPTable itemHeader = new PdfPTable (7);
//           
//            String tax1name = props.getProp("TAX1NAME");
//        String tax2name = props.getProp("TAX2NAME");
//           
//        String t1 = tax1name.substring(0,1);
//        String t2 = tax2name.substring(0,1);
//        
//        itemHeader.setWidths(headerwidths);
//         
//        font = new Font (Font.TIMES_ROMAN, 10, Font.BOLD);
//        
//        cell = new PdfPCell (new Phrase("Qty", font));
//        
//        cell.setVerticalAlignment(Rectangle.ALIGN_TOP);
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Code", font));
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Description", font));
//        
//        itemHeader.addCell(cell);
//        
//        cell = new PdfPCell (new Phrase("Unit " + currency, font));
//        
//        itemHeader.addCell(cell);
//        
//        itemHeader.addCell(new PdfPCell (new Phrase(t1, font)));
//        itemHeader.addCell(new PdfPCell (new Phrase(t2, font)));
//        itemHeader.addCell(new PdfPCell (new Phrase("Total " + currency, font)));
//        
//        itemHeader.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//        
//        cell = new PdfPCell(itemHeader);
//        cell.setColspan(2);
//                
//        cell.setPaddingTop(5);      //space between itemheader and dateline
//        
//        cell.setBorder(Rectangle.NO_BORDER);
//        
//        head.addCell(cell);
//        
//        inv = new PDFInvoice (file, head);
//        inv.setWatermarkEnabled(false);
//        
// /*  END ITEM HEADER TABLE */   
//        
//        
//        
//        /** 
//             *
//             *  build the summary table
//             *
//             */
//        int ftheaderwidths[] = { 75, 25};
//         font = new Font (Font.COURIER, 10, Font.NORMAL);
//        
//       
//        foot.setWidths(ftheaderwidths);
//         
//         cell = new PdfPCell (new Phrase("NOTES:", font));  //this stays
//         foot.addCell(cell);
//                  
//         /*
//          * check paidCheckBox
//          * if paid get last payment and balance due  
//          * if not config default vals
//          *
//          *
//          */
//         //float payment=0.00f;
//         //float balance_due = Float.parseFloat(grandTotalField.getText() );
//         
//         PdfPTable totals = new PdfPTable(2);
//         
//         totals.addCell(new Phrase("Sub-Total", font));
//         totals.addCell(new Phrase("", font));
//         
//         totals.addCell(new Phrase(tax1name, font));
//         totals.addCell(new Phrase("", font));
//         
//         totals.addCell(new Phrase(tax2name, font));
//         totals.addCell(new Phrase("", font));
//         
//         //totals.addCell(new Phrase(" ", font));//other
//         //totals.addCell(new Phrase(shippingField.getText(), font));  //
//         
//         totals.addCell(new Phrase("TOTAL", font));
//         totals.addCell(new Phrase("", font));
//         
//         totals.addCell(new Phrase("Payment", font));
//                  
//         totals.addCell(new Phrase("", font));
//                  
//         totals.addCell(new Phrase("BAL DUE  " + currency, font));
//         totals.addCell(new Phrase("", font));
//                  
//         totals.setTotalWidth(130);
//         
//         foot.addCell(totals);
//         
//         inv.addTagline(props.getProp("CO NAME") + " / " + props.getProp("CO ADDRESS") + " / " + 
//                 props.getProp("CO CITY") + " / " + props.getProp("CO PHONE"));
//         
//         
//         cell = new PdfPCell (new Phrase("Page: ", inv.getDocFont(-4,0)));
//         cell.setColspan(2);
//         
//         foot.addCell(cell);   
//         
//         inv.setFooter(foot);  //magic method
//                  
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (BadElementException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//         catch (DocumentException ex) {
//                ex.printStackTrace();
//            }
//           
// /*  END SUMMARY TABLE */                       
//            
//        
//            /**
//             * Build actual Items table
//             *
//             */
//            StringBuilder sb;
//                                  
//            PdfPTable items = new PdfPTable (7);
//            
//        try {
//            
//            //items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//            items.setWidths(headerwidths);
//            
//        } catch (DocumentException ex) {
//            ex.printStackTrace();
//        }
//            
//            Font f = new Font (Font.COURIER, 10, Font.NORMAL);
//            PdfPCell cell;
//            int a = 1;
//            
//           
//            items.setTotalWidth(com.lowagie.text.PageSize.LETTER.width()-72);
//            
// /*  END ITEMS TABLE */              
//            
//            
//            /**
//             *
//             *  Make the invoice pretty
//             *  COSMETICS
//             *  (Skipped if 1 page or more)
//             *
//             */
//            
//            float pages = 0;// inv.howManyPages(items);  //how many pages will it take to write invoice
//   
//            
//            if (pages < 1) {   //if less than a full page fill it out
//             
//                pages = pages % 1.0f;
//             // head.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);
//             // foot.setTotalWidth(com.lowagie.text.PageSize.LETTER.width() - 72);
//              
//            float hf = inv.getHeaderSize() + inv.getFooterSize();
//            
//            float bs = inv.getBodySize() - 10 ;  //fudge
//                       
//            float remain = bs - items.getTotalHeight();  //how much space is left after the table
//            
//            //System.out.println(remain);
//            
//            float row_height = std_row_height;    //standard row height??  15points
//                      
//            float z =  remain / row_height;  //how many Standard rows can be written to fill (no remainder) 
//             
//            boolean odd = true;
//            if (items.getRows().size() % 2 == 0)  odd = false;
//            
//            int cols = 7;
//            
//            for (int i = 1; i < z; i++){
//                    
//                for (int x = 2; x < cols-1; x++) {   //simulate the data loop
//                
//                    cell = new PdfPCell ( new Phrase ( " ", f) );  //place ' ' instead of data
//                                
//                    cell.setBorder(Rectangle.BOX);
//                       
//                    cell.setFixedHeight(row_height);
//                    
//                   /* if (!odd ) {
//                    
//                    cell.setGrayFill(.80f);
//                          
//                           
//                    }*/
//                
//                    items.addCell(cell);
//                            
//                }
//            
//                if (odd) odd = false;
//                else  odd = true;
//                
//            }
//            
//            }
// 
///*  END COSMETICS */               
//            
//            /*  END INVOICE TABLES  */
//            
//            inv.setBody(items);   //add the items table to the PDFInvoice        
//            
//            inv.build(); 
//      
//            inv.finish();
//            
//            String stat;
//             
//            if ( rd.isView() ){
//            
//                stat  = ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             
//            }
//             
//            if (rd.getWinPrn()) {
//            
//                 ReportFactory.windowsFastPrint(file, props);
//                 if (rd.isDuplicate()) ReportFactory.windowsFastPrint(file, props);
//            
//             }
//    
//    return true;
//    
//}   
//    
//  
//  public static String generatePhoneList (DbEngine dbsys, Settings props, boolean customers, int font_size) {
//      
//       /* My data collections */ 
//      ArrayList al;
//      TableSorter ts;
//      StringBuilder sb;
//      String nl = System.getProperty("line.separator");
//      Paragraph p;
//      DbEngine db = dbsys;
//      ReportModel rm;
//      
//     
//      /* Get the appropriate records (rows) */
//      if (!customers) al = db.search("conn", 16, "true", false); //vendors
//      else al = db.search("conn", 15, "true", false);  //customers
//      
//      /* Make sure there are records to work with if so get a tablesorter (model) */
//      if (al == null) return "";
//      else ts = (TableSorter) db.createTableModel("conn", al, true);
//      
//      /* Sort differently depending on customer or vendor */
//      if (!customers) ts.setSortingStatus(1, 1);  //company sort
//      else {
//          
//          ts.setSortingStatus(3, 1);  //last name sort
//          ts.setSortingStatus(1, 1);  //company
//          
//      }
//      
//      
//      rm = new ReportModel(ts);    
//      /*
//        
//        Company Name........................Contact..........Phone
//      
//       
//       */
//       String type;
//       
//       if (!customers) type = "Supplier ";
//       else type = "Client ";
//        
//               
//        ReportDialog rd = new ReportDialog (null, true,
//                props.getProp("REPORT FOLDER"), false, props.getProp("CO NAME")+' '+ type + "Phone List " + DV.getShortDate(), props);
//                
//        if (rd.getState() == false) return "";
//                      
//        String file = rd.getFile() + type.trim() + "PhoneList_" + DV.getShortDate().replace('/','-')+".pdf";  //add date
//        
//        if (!DV.isFileAccessible(file, "PDF file")) return "WARNING: "+file+ " was open by another program." + "  Close your pdf reader (Adobe?) and try again."+nl;
//                
//        PdfReportService report = new PdfReportService (file);
//        
//        report.setReportTitle(rd.getTitle());
//        report.setWatermark(props.getProp("WATERMARK"));
//        report.setWatermarkEnabled(Boolean.parseBoolean(props.getProp("PRINT WM")));
//        
//                
//        
//        do {
//            
//            sb = new StringBuilder();
//            
//            if (1==1) {
//                
//                sb.append(nl + DV.addSpace(rm.getValueAt(1), 36, '.') +
//                        rm.getValueAt(2)+ ' ' + rm.getValueAt(3) + nl +
//                        "Contact: " + DV.addSpace(rm.getValueAt(9), 21, '.') +
//                        "Phone: " + DV.addSpace(rm.getValueAt(10), 15, '.') + " Fax: "+rm.getValueAt(11)+nl);
//            }
//            
//          /*  if (customers) {
//                
//                sb.append(DV.addSpace(rm.getValueAt(1), 36, '.') +
//                        DV.addSpace(DV.chop(rm.getValueAt(2)+' '+
//                        rm.getValueAt(3), 30), 31, '.') +
//                        rm.getValueAt(10) + nl);
//            }*/
//            
//            p = new Paragraph (sb.toString().toUpperCase(), new Font (Font.COURIER, font_size, Font.NORMAL));
//            
//            report.addParagraph(p, false);
//            
//            
//        }while (rm.next());
//        
//        
//
//      report.finish();
//      
//        if ( rd.isView() ){
//            
//        ReportFactory.veiwPDF(props.getProp("ACROEXE"), file, props);
//             
//        }
//             
//        if (rd.getWinPrn()) {
//            
//            ReportFactory.windowsFastPrint(file, props);
//            
//        }
//      
//       return "";
//        
//    }
//  

}
