/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.database.reports;


import com.datavirtue.nevitium.services.PdfReportService;

import com.datavirtue.nevitium.models.invoices.old.InvoicePayment;


import java.awt.Font;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Data Virtue
 */
public class PurchaseHistoryReport implements ReportInterface {

public PurchaseHistoryReport(){

    
}

//public void setDateRange(long start, long end){
//    startDate = start;
//    endDate = end;
//}
//public boolean ignoreDate(){
//    return true;
//}
//public void SetTitle(String t){
//    title = t;
//    if (theReport != null){
//        theReport.setReportTitle(title);
//    }
//}
//public void SetOutputFilename(String f){
//    filename = f;
//    theReport = new PdfReportService(f);
//}
//
//public void setCustomer(int custKey){
//    customerKey = custKey;
//}
//private int customerKey = 0;
//private String customer;
//public void buildReport(){
//
//    
//    period = new DefaultTableModel(0, 6){
//             public Class getColumnClass(int column) {
//                return DV.idObject(this.getValueAt(0,column));
//            }
//
//             boolean[] canEdit = new boolean [] {
//        false, false, false, false, false, false, false
//    };
//
//    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        return canEdit [columnIndex];
//    }
//
//         };
//    Object [] header = new Object[]{"Date","InvNum","Code","Product","Qty","Price"} ;
//    period.setColumnIdentifiers(header);
//
//    ArrayList invoices;
//    /* Get all of the payments for each day */
//   
//        DefaultTableModel temp;
//
//        Object [] cust = db.getRecord("conn", customerKey);
//        String tmp = (String)cust[1] + " : "+(String)cust[10];
//        if (tmp.trim().equals("")){
//            tmp = (String)cust[2]+" "+(String)cust[3]+" : "+(String)cust[10];
//        }
//        customer = tmp;
//
//        invoices = db.nSearch("invoice", 11, customerKey, customerKey, false);
//
//        if (invoices == null || invoices.size() < 1) return;
//
//        temp = (DefaultTableModel)db.createTableModel("invoice", invoices, false);
//        OldInvoiceItem ii;
//        OldInvoice inv;
//        ArrayList invoiceItems;
//        
//        Object [] newRow;
//        InvoicePayment ip;
//
//        for (int r = 0; r < temp.getRowCount(); r++){
//            
//           inv = new OldInvoice(application, (Integer)temp.getValueAt(r, 0));
//
//           newRow = new Object[6];
//           /* Loop and build row object  */
//           if (inv.isVoid()) continue;
//           invoiceItems = inv.getInvoiceItemList();
//
//           for(int i = 0; i < invoiceItems.size(); i++){
//
//               ii = (OldInvoiceItem)invoiceItems.get(i);
//
//               newRow[0] = ii.getDate(); //date
//               newRow[1] = new String((String)inv.getInvoiceNumber());
//               
//               newRow[2] = ii.getCode();//code
//               newRow[3] = ii.getDesc();//desc
//               newRow[4] = ii.getQty();//qty
//               newRow[5] = ii.getUnitTotal();//price
//               float t = ii.getUnitTotal();
//
//               if (t != 0.0) {
//                   period.addRow(newRow);
//               }else {
//                   continue;
//               }
//
//               /* search payments for a return */
//               int pc = inv.getPaymentCount();
//               if (pc < 2) continue;
//               for (int c = 0; c < pc; c++){
//
//                   ip = new InvoicePayment(inv.getPayment(c));
//                   if(ip.getType().equals("Return")){
//
//                       if(ip.getMemo().equals(ii.getDesc())){
//
//                           newRow[0] = ii.getDate(); //date
//                           newRow[1] = new String((String)inv.getInvoiceNumber());
//                           newRow[2] = new String(ip.getType());//code
//                           newRow[3] = ii.getDesc();//desc
//                           newRow[4] = new Float(0.00f);//qty
//                           newRow[5] = ip.getCredit();//price
//                           period.addRow(newRow);
//                       }
//
//                   }
//
//               }
//
//           }
//        
//        }
//
//        summary = "Total Sales: "+DV.money(totalCols(period));
//      
//        
//}
//
///* Modify this to work only on the period and to provide return, refund summary
// contrasted with total recorded revenue.*/
//private float totalCols(DefaultTableModel tm){
//    float sales=0.00f;
//    
//
//    for (int r = 0; r < tm.getRowCount(); r++){
//
//        String code = (String)tm.getValueAt(r, 2);
//
//        if (code.equals("Return")){
//            sales -= (Float)tm.getValueAt(r, 5);
//        }else{
//            sales += (Float)tm.getValueAt(r, 5);
//        }
//
//    }
//
//    return sales;
//}
//
//
//public DefaultTableModel getPeriod(){
//
//    return period;
//}
//
//public void printReport(){
//    if (period.getRowCount() < 1) return;
//
//    LinePrinter lp = new LinePrinter(new Font("courier", Font.PLAIN, 10), true);
//    lp.setOrientation("Landscape");
//    int [] fieldSizes = {12, 10, 18, 52, 12,14};
//    Object value;
//    Class objId;
//    StringBuilder sb = new StringBuilder();
//    
//
//    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//
//    String nl = System.getProperty("line.separator");
//    lp.addLine(this.getName());
//    lp.addLine(customer);
//    lp.newLine();
//    String chunk = "";
//    sb.append(DV.addSpace("Date", 12, ' '));
//    sb.append(DV.addSpace("InvNum", 10, ' '));
//    sb.append(DV.addSpace("Code", 18, ' '));
//    sb.append(DV.addSpace("Product", 52, ' '));
//    sb.append(DV.addSpace("Qty", 12, ' '));
//    sb.append(DV.addSpace("Price", 14, ' '));
//
//    lp.addLine(sb.toString());
//    lp.addLine(DV.addSpace("-", 110, '-'));
//    sb = new StringBuilder();
//
//    for (int r = 0; r < period.getRowCount(); r++){
//
//        for (int c = 0; c < period.getColumnCount(); c++){
//
//            //System.out.println("Print Report: period col count "+period.getColumnCount());
//            value = period.getValueAt(r, c);
//            objId = DV.idObject(value);
//
//            if (objId.equals(String.class)){
//                chunk = DV.addSpace((String)value, fieldSizes[c], ' ');
//            }
//            if (objId.equals(Float.class)){
//                if (c != 1) {
//                    chunk = DV.addSpace(DV.money((Float)value), fieldSizes[c], ' ');
//                }else chunk = DV.addSpace(Float.toString((Float)value), fieldSizes[c], ' ');
//            }
//            if (objId.equals(Integer.class)){
//                chunk = DV.addSpace(Integer.toString((Integer)value), fieldSizes[c], ' ');
//            }
//            if (objId.equals(Boolean.class)){
//                chunk = DV.addSpace(Boolean.toString((Boolean)value), fieldSizes[c], ' ');
//            }
//            if (objId.equals(Date.class)){
//                chunk = DV.addSpace(df.format(new Date((Long)value)), fieldSizes[c], ' ');
//            }
//
//            sb.append(chunk);
//
//        }
//        lp.addLine(sb.toString());
//        
//        sb = new StringBuilder();
//    }
//
//    lp.newLine();
//    lp.addLine(DV.addSpace("=== Summary ",110, '='));
//    lp.addLine(summary);
//    char c = '_';
//
//    
//    lp.formFeed();
//    lp.go();
//
//
//    
//}
//public void pdfReport(String f){
//    PdfReportService pdf = new PdfReportService(f, "landscape");
//    ReportModel rm = new ReportModel(period);
//    
//    pdf.setReportTitle(title);
//
//    pdf.setWatermarkEnabled(false);
//    pdf.setWatermark(f);
//    pdf.setFont("Courier");
//    pdf.setFontSize(10);
//
//    int [] fieldSizes = {12, 10, 18, 52, 12,14};
//    Object value;
//    Class objId;
//    StringBuilder sb = new StringBuilder();
//
//    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//
//    String nl = System.getProperty("line.separator");
//
//    String chunk = "";
//
//    sb.append(DV.addSpace("Date", 12, ' '));
//    sb.append(DV.addSpace("InvNum", 10, ' '));
//    sb.append(DV.addSpace("Code", 18, ' '));
//    sb.append(DV.addSpace("Product", 52, ' '));
//    sb.append(DV.addSpace("Qty", 12, ' '));
//    sb.append(DV.addSpace("Price", 14, ' '));
//
//    pdf.setHeader(customer + nl +
//            sb.toString() + nl +
//            DV.addSpace("-", 110, '-'));
//
//    sb = new StringBuilder();
//
//    for (int r = 0; r < period.getRowCount(); r++){
//
//        for (int c = 0; c < period.getColumnCount(); c++){
//
//            value = period.getValueAt(r, c);
//            objId = DV.idObject(value);
//
//            if (objId.equals(String.class)){
//                chunk = DV.addSpace((String)value, fieldSizes[c], ' ');
//            }
//            if (objId.equals(Float.class)){
//                if (c != 1) {
//                    chunk = DV.addSpace(DV.money((Float)value), fieldSizes[c], ' ');
//                }else chunk = DV.addSpace(Float.toString((Float)value), fieldSizes[c], ' ');
//            }
//            if (objId.equals(Integer.class)){
//                chunk = DV.addSpace(Integer.toString((Integer)value), fieldSizes[c], ' ');
//            }
//            if (objId.equals(Boolean.class)){
//                chunk = DV.addSpace(Boolean.toString((Boolean)value), fieldSizes[c], ' ');
//            }
//            if (objId.equals(Date.class)){
//                chunk = DV.addSpace(df.format(new Date((Long)value)), fieldSizes[c], ' ');
//            }
//
//            sb.append(chunk);
//
//        }
//       pdf.addParagraph(sb.toString(), false);
//
//        sb = new StringBuilder();
//    }
//
//    pdf.addParagraph(DV.addSpace("=== Summary ",110, '=') + nl + summary, true);
//    
//    pdf.finish();
//    
//}
//
//public String getName(){
//    return title;
//}
//public String getSummary(){
//    return summary;
//}
//public int[] ColsToDelete(){
//    return null;
//}
//public int [] getColWidths(){
//
//    return new int []{80,50,100,300};
//
//}
//private DbEngine db;
//private String filename="C:/RevenueReport.pdf";
//private PdfReportService theReport;
//private String title="Purchase History Report";
//
//private DefaultTableModel daily;
//private DefaultTableModel period;
//ArrayList dailies = new ArrayList();
//private DateFormat dateformat = DateFormat.getDateInstance(DateFormat.SHORT);
//private long startDate = 0;
//private long endDate = 0;
//private long oneDay = (60 * 60 * 1000L);
//private String summary="";
//

    @Override
    public void setDateRange(long start, long end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetTitle(String t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void SetOutputFilename(String f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void buildReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pdfReport(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DefaultTableModel getPeriod() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSummary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] ColsToDelete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ignoreDate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] getColWidths() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
