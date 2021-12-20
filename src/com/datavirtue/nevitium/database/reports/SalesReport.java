/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.database.reports;


import com.datavirtue.nevitium.services.PdfReportService;
import RuntimeManagement.GlobalApplicationDaemon;
import com.datavirtue.nevitium.models.invoices.old.OldInvoice;
import com.datavirtue.nevitium.models.invoices.old.OldInvoiceItem;

import datavirtue.DV;
import datavirtue.DbEngine;
import datavirtue.LinePrinter;
import datavirtue.Settings;
import java.awt.Font;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Data Virtue
 */
public class SalesReport implements ReportInterface {
private GlobalApplicationDaemon application;
private boolean VAT = false;
public SalesReport(GlobalApplicationDaemon application, boolean vat){
    this.application = application;
    db = application.getDb();
    props = application.getProps();
    this.VAT = vat;
}

public boolean ignoreDate(){
    return false;
}

public void setDateRange(long start, long end){
    startDate = start;
    endDate = end;
}
public void SetTitle(String t){
    title = t;
    if (theReport != null){
        theReport.setReportTitle(title);
    }
}
public void SetOutputFilename(String f){
    filename = f;
    theReport = new PdfReportService(f);
}

private boolean checkIndexStatus(ArrayList al, int index){
    if (al == null) return false;

    al.trimToSize();
    if (al.size() < 1) return false;

    int x= 0;
    for (int i=0; i < al.size(); i++){
        x = (Integer)al.get(i);
        if (x == index) return true; //already used
    }
    return false;//not used yet
}

private TableModel combineItems(DefaultTableModel oldTable){

    DefaultTableModel newTable = new DefaultTableModel(0,8){
             public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            }
         };
    
         String t1 = "Tax1";
         String t2 = "Tax2";
         /*t1 = props.getProp("TAX1NAME");
         t2 = props.getProp("TAX2NAME");*/
         
         if (props.getProp("TAX1NAME").length() > 0) t1 = props.getProp("TAX1NAME");
         if (props.getProp("TAX2NAME").length() > 0) t2 = props.getProp("TAX2NAME");
         
         Object [] header = new Object[]{"Date","Code","Qty","Desc","Price","Cost",t1,t2} ;
    newTable.setColumnIdentifiers(header);

    ArrayList done = new ArrayList();
    String desc = "";
    String innerDesc = "";
    boolean already = false;
    //Object [] oldRow;
    Object [] newRow;

    /* Loops to combine items */
    for (int oldIdx = 0; oldIdx < oldTable.getRowCount(); oldIdx++){
        if (checkIndexStatus(done, oldIdx)) continue;

        //oldRow = DV.getTableRow(oldTable, oldIdx);//get the row we are checking
        newRow = DV.getTableRow(oldTable, oldIdx);

        desc = (String)oldTable.getValueAt(oldIdx, 2);//get desc from row of "old" daily

        for (int r = 0; r < oldTable.getRowCount(); r++){

            if (r == oldIdx) continue;  //dont use the one we are on!
            
            innerDesc = (String)oldTable.getValueAt(r, 2);//get desc
            already = this.checkIndexStatus(done, r);

            if (innerDesc.equalsIgnoreCase(desc) && !already){

                /* combine */

                try {
                    /* add appropriate cols */
                    float base = (Float) newRow[1];
                    float add = (Float) oldTable.getValueAt(r, 1);
                    newRow[1] = base += add;//qty

                    base = (Float) newRow[3];
                    add = (Float) oldTable.getValueAt(r, 3);
                    newRow[3] = base += add;//price

                    base = (Float) newRow[4];
                    add = (Float) oldTable.getValueAt(r, 4);
                    newRow[4] = base += add;//cost

                    base = (Float) newRow[5];
                    add = (Float) oldTable.getValueAt(r, 5);
                    newRow[5] = base += add;//tax1

                    base = (Float) newRow[6];
                    add = (Float) oldTable.getValueAt(r, 6);
                    newRow[6] = base += add;//tax2

                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                    "Error in SalesReport:combineItems:data mismatch");
                    return new DefaultTableModel();
                }


                /* record row index in done */
                done.add(new Integer(r));
            }
        }
        newTable.addRow(newRow);

    }

    return newTable;
}

private void appendDailyToPeriod(boolean insertTotals){

    if (insertTotals){

        Object [] item = new Object [7];
        Float [] totals = totalCols(daily);

        try {
            if (daily.getRowCount() < 1) return;
            item[0] = new Long((Long) daily.getValueAt(daily.getRowCount() - 1, 0));
        } catch (Exception e) {

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error in SalesReport:appendDailyToPeriod:Long");
                    return;
        }
        
        item[1] = new Float(0);
       
        item[2] = new String(dateformat.format(
                new Date((Long)daily.getValueAt(
                daily.getRowCount()-1, 0)))+ " TOTALS -->>");
        item[3] = new Float(totals[0]);
        item[4] = new Float(totals[1]);
        item[5] = new Float(totals[2]);
        item[6] = new Float(totals[3]);
        daily.addRow(item);

    }

    Object [] row;
    for (int d = 0; d < daily.getRowCount(); d++){
        
        row = DV.getTableRow(daily, d);
        period.addRow(row);
        
    }
    this.addToDailies(daily);


}
private Float [] totalCols(DefaultTableModel tm){
    float price=0.00f, cost=0.00f, t1=0.00f, t2=0.00f;
    String code = "";

    try {
        for (int r = 0; r < tm.getRowCount(); r++) {

            code = (String) tm.getValueAt(r, 2);
            if (code.contains("TOTALS")) {
                continue;

            }
            price += (Float) tm.getValueAt(r, 3);
            cost += (Float) tm.getValueAt(r, 4);
            t1 += (Float) tm.getValueAt(r, 5);
            t2 += (Float) tm.getValueAt(r, 6);

        }

    } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                    "Error in SalesReport:totalCols:data mismatch");
                    return new Float[] {0f, 0f, 0f, 0f};
                }


    return new Float[] {price, cost, t1, t2};
}

public int[] ColsToDelete(){
    int [] vals = {1};
    return vals;
}

public int [] getColWidths(){

    return new int []{80,50,300};

}

private void appendPeriodTotals(){

    Object [] item = new Object [7];
        Float [] totals = totalCols(period);

        item[0] = new Long(0);
        //item[1] = new String ("Z TOTALS");
        item[1] = new Float(0);

        item[2] = new String("PERIOD TOTALS ---->>");
        item[3] = new Float(totals[0]);
        item[4] = new Float(totals[1]);
        item[5] = new Float(totals[2]);
        item[6] = new Float(totals[3]);
        period.addRow(item);

        summary = "Total Margin "+DV.money((totals[0] - totals[1])) +
                "   Total Taxes Collected " + DV.money((totals[2] + totals[3]));
        
}

private void appendItemToDaily(ArrayList itemList){
    Object [] item = new Object [7];
    OldInvoiceItem ii;
    /* Report Table:
     Long date, Float quantity, String desc, Float total, Float cost,
     Float tax1, Float tax2*/
    for (int idx = 0; idx < itemList.size(); idx++){

        ii = (OldInvoiceItem)itemList.get(idx);
        item[0] = ii.getDate();
        //item[1] = ii.getCode();
        item[1] = ii.getQty();
        item[2] = ii.getDesc();
        item[3] = ii.getUnitTotal();
        if (ii.getUnitTotal() == 0.0) continue;
        periodSales += ii.getUnitTotal();

        item[4] = ii.getCostTotal();
        periodCost += ii.getCostTotal();

        item[5] = ii.getTax1Total();
        //System.out.println("Sales Report: item[5]: "+item[5]);
        item[6] = ii.getTax2Total();

        daily.addRow(item);
        
    }

}
private String tax1 = "Tax1";
private String tax2 = "Tax2";

public void buildReport(){
    periodSales = 0.00f;
    periodCost = 0.00f;
    periodTax1 = 0.00f;
    periodTax2 = 0.00f;

    period = new DefaultTableModel(0, 7){
             public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            }
         };

         
         /*t1 = props.getProp("TAX1NAME");
         t2 = props.getProp("TAX2NAME");*/

         if (props.getProp("TAX1NAME").length() > 0) tax1 = props.getProp("TAX1NAME");
         if (props.getProp("TAX2NAME").length() > 0) tax2 = props.getProp("TAX2NAME");
         
    Object [] header = new Object[]{"Date","Qty","Desc","Sales","Cost",tax1,tax2} ;
    period.setColumnIdentifiers(header);
    
    ArrayList invoiceItems;
    /* Get all of the invoices for each day */
    for (long day = startDate; day <= endDate; day += oneDay){ //start day loop

        daily = new DefaultTableModel(0, 7){
             public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            }
         }; //invitems columns (move to day loop)
        
        daily.setColumnIdentifiers(header);
        invoices = new ArrayList();
        invoices.trimToSize();

        ArrayList temp = new ArrayList();
        temp.trimToSize();

        temp = db.nSearch("invoice", 2, day, day, false);
        if (temp == null || temp.size() < 1) continue;

        /* Remove the voids */

         for (int i = 0; i < temp.size(); i++){
            invoice = new OldInvoice(application, (Integer)temp.get(i));            

            if (!invoice.isVoid()){
                
                invoices.add(temp.get(i));

            }
         }
        if (invoices == null || invoices.size() < 1) continue;

        /* Loop through invoices */
        for (int i = 0; i < invoices.size(); i++){
            invoice = new OldInvoice(application, (Integer)invoices.get(i));
            /*right now, if a day has only voided invoices it causes an error */
            if (invoice.isVoid()){                
                continue;
            }
            /* Get invitems */            
            invoiceItems = invoice.getInvoiceItemList();            
            appendItemToDaily(invoiceItems);
        }

        daily = (DefaultTableModel)combineItems(daily);//clean up daily list
        this.appendDailyToPeriod(true);
        
    }

    this.appendPeriodTotals();


    /* Pump the period into a PDFReport */
}

private void addToDailies(DefaultTableModel tm){
    dailies.trimToSize();

    dailies.add(DV.copyTableModel(tm));
}


public void printReport(){
    if (period.getRowCount() < 1) return;
    
    LinePrinter lp = new LinePrinter(new Font("courier", Font.PLAIN, 10), true);
    lp.setOrientation("Landscape");
    int [] fieldSizes = {10, 10, 40, 14, 14, 12, 12};
    Object value;
    Class objId;
    StringBuilder sb = new StringBuilder();
    

    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    String nl = System.getProperty("line.separator");
    lp.addLine(this.getName() + "  "+ df.format(new Date(startDate)) + " To " +
            df.format(new Date(endDate)));
    lp.newLine();
    String chunk = "";

    sb.append(DV.addSpace("DATE", 10, ' '));
    sb.append(DV.addSpace("#SOLD", 10, ' '));
    sb.append(DV.addSpace("ITEM NAME", 40, ' '));
    sb.append(DV.addSpace("SALES", 14, ' '));
    sb.append(DV.addSpace("COST", 14, ' '));
    sb.append(DV.addSpace(tax1.toUpperCase(), 12, ' '));
    sb.append(DV.addSpace(tax2.toUpperCase(), 12, ' '));
    lp.addLine(sb.toString());
    lp.addLine(DV.addSpace("-", 110, '-'));
    sb = new StringBuilder();

    for (int r = 0; r < period.getRowCount(); r++){

        for (int c = 0; c < period.getColumnCount(); c++){

            //System.out.println("Print Report: period col count "+period.getColumnCount());
            value = period.getValueAt(r, c);
            objId = DV.idObject(value);

            if (objId.equals(String.class)){
                chunk = DV.addSpace((String)value, fieldSizes[c], ' ');
            }
            if (objId.equals(Float.class)){
                if (c != 1) {
                    chunk = DV.addSpace(DV.money((Float)value), fieldSizes[c], ' ');
                }else chunk = DV.addSpace(Float.toString((Float)value), fieldSizes[c], ' ');
            }
            if (objId.equals(Integer.class)){
                chunk = DV.addSpace(Integer.toString((Integer)value), fieldSizes[c], ' ');
            }
            if (objId.equals(Boolean.class)){
                chunk = DV.addSpace(Boolean.toString((Boolean)value), fieldSizes[c], ' ');
            }
            if (objId.equals(Date.class)){
                chunk = DV.addSpace(df.format(new Date((Long)value)), fieldSizes[c], ' ');
            }

            sb.append(chunk);


        }
        lp.addLine(sb.toString());
        //lp.newLine();
        sb = new StringBuilder();
    }

    lp.newLine();
    lp.addLine(DV.addSpace("=== Summary ",110, '='));
    lp.addLine("Total Margin: "+DV.money(periodSales - periodCost));



    lp.formFeed();
    lp.go();


}
public void pdfReport(String f){
    PdfReportService pdf = new PdfReportService(f, "landscape");
    ReportModel rm = new ReportModel(period);
    String t = "";

    pdf.setReportTitle(title);

    pdf.setWatermarkEnabled(false);
    pdf.setWatermark(f);
    pdf.setFont("Courier");
    pdf.setFontSize(10);
    
    
    int [] fieldSizes = {10, 10, 40, 14, 14, 12, 12};
    Object value;
    Class objId;
    StringBuilder sb = new StringBuilder();

    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    String nl = System.getProperty("line.separator");

    sb.append(DV.addSpace("DATE", 10, ' '));
    sb.append(DV.addSpace("#SOLD", 10, ' '));
    sb.append(DV.addSpace("ITEM NAME", 40, ' '));
    sb.append(DV.addSpace("SALES", 14, ' '));
    sb.append(DV.addSpace("COST", 14, ' '));
    sb.append(DV.addSpace(tax1.toUpperCase(), 12, ' '));
    sb.append(DV.addSpace(tax2.toUpperCase(), 12, ' '));
    
    pdf.setHeader(this.getName() + "  "+ df.format(new Date(startDate)) + " To " +
            df.format(new Date(endDate)) + nl +
            sb.toString()+ nl +
            DV.addSpace("-", 110, '-'));
   
    String chunk = "";

    sb = new StringBuilder();

    for (int r = 0; r < period.getRowCount(); r++){

        for (int c = 0; c < period.getColumnCount(); c++){

            //System.out.println("Print Report: period col count "+period.getColumnCount());
            value = period.getValueAt(r, c);
            objId = DV.idObject(value);

            if (objId.equals(String.class)){
                chunk = DV.addSpace((String)value, fieldSizes[c], ' ');
            }
            if (objId.equals(Float.class)){
                if (c != 1) {
                    chunk = DV.addSpace(DV.money((Float)value), fieldSizes[c], ' ');
                }else chunk = DV.addSpace(Float.toString((Float)value), fieldSizes[c], ' ');
            }
            if (objId.equals(Integer.class)){
                chunk = DV.addSpace(Integer.toString((Integer)value), fieldSizes[c], ' ');
            }
            if (objId.equals(Boolean.class)){
                chunk = DV.addSpace(Boolean.toString((Boolean)value), fieldSizes[c], ' ');
            }
            if (objId.equals(Date.class)){
                chunk = DV.addSpace(df.format(new Date((Long)value)), fieldSizes[c], ' ');
            }

            sb.append(chunk);


        }
        pdf.addParagraph(sb.toString(), false);
        
        sb = new StringBuilder();
    }

    sb.append(nl+
            DV.addSpace("=== Summary ",110, '=')+ nl +
            "Total Margin: "+DV.money(periodSales - periodCost));

    pdf.addParagraph(sb.toString(), true);
    pdf.finish();

}

public DefaultTableModel getPeriod(){
    return period;
}
public String getName(){
    return title;
}
public String getSummary(){
    return summary;
}
private StringBuilder sb;
private DbEngine db;
private String nl = System.getProperty("line.separator");
private long startDate = 0, endDate = 0;
private String title = "Sales Report";
private String filename="C:/SalesReport.pdf";
private DateFormat dateformat = DateFormat.getDateInstance(DateFormat.SHORT);
private DefaultTableModel daily;
private DefaultTableModel period;
private ArrayList dailies = new ArrayList();
private PdfReportService theReport;
private ArrayList invoices;
private OldInvoice invoice;
private long oneDay = (60 * 60 * 1000L);
private String summary="";

/* Report Detail */
private float periodSales = 0.00f;
private float periodCost = 0.00f;
private float periodTax1 = 0.00f;
private float periodTax2 = 0.00f;
private Settings props;

}
