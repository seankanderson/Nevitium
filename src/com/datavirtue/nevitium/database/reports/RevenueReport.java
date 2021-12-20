/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.database.reports;

import com.datavirtue.nevitium.services.PdfReportService;
import com.datavirtue.nevitium.ui.util.Tools;
import com.datavirtue.nevitium.models.invoices.old.InvoicePayment;

import datavirtue.DV;
import datavirtue.DbEngine;
import datavirtue.LinePrinter;
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
public class RevenueReport implements ReportInterface {

public RevenueReport(DbEngine dbe){

    db = dbe;

}
public int [] getColWidths(){
    
    return new int []{50,80,100,300};
    
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

public void buildReport(){

    interestTotal = 0.00f;  //Interest  -- already in debit
    creditTotal = 0.00f;
    debitTotal = 0.00f;
    refundTotal = 0.00f; //Refund -- already in credits
    feeTotal = 0.00f; //Fee -- already in debit
    returnTotal = 0.00f; //Return  --  Already in credits
    custCreditTotal = 0.00f; //A credit not counted as revenue, gift Card etc..
    prepaidTotal = 0.00f; //credit not counted as revenue

    period = new DefaultTableModel(0, 6){
             public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            }

             boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false
    };

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
    }

         };
    Object [] header = new Object[]{"InvNum","Date","Type","Memo","Debit","Credit"} ;
    period.setColumnIdentifiers(header);

    ArrayList payments;
    /* Get all of the payments for each day */
    for (long day = startDate; day <= endDate; day += oneDay){ //start day loop
        DefaultTableModel temp;

        daily = new DefaultTableModel(0, 6){
             public Class getColumnClass(int column) {
                return DV.idObject(this.getValueAt(0,column));
            }

             boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false
    };

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
    }

         }; 
        
        daily.setColumnIdentifiers(header);

        payments = db.nSearch("payments", 2, day, day, false);

        if (payments == null || payments.size() < 1) continue;


        temp = (DefaultTableModel)db.createTableModel("payments", payments, false);
        InvoicePayment ip;
        String type;

        for (int r = 0; r < temp.getRowCount(); r++){
            
           ip = new InvoicePayment(DV.getTableRow(temp, r));
           type = ip.getType();

           if (type.equals("Interest")) interestTotal += ip.getDebit();
           if (type.equals("Return")) returnTotal += ip.getCredit();
           if (type.equals("Fee")) feeTotal += ip.getDebit();
           if (type.equals("Refund")) refundTotal += ip.getDebit();
           if (type.equals("Credit")) custCreditTotal += ip.getCredit();
           if (type.equals("Prepaid")) prepaidTotal += ip.getCredit();
           if (ip.getDebit() > 0) debitTotal += ip.getDebit();
           if (ip.getCredit() > 0) creditTotal += ip.getCredit();

           daily.addRow(ip.getUserData());
                      
        }

        //this.removeReturns(daily);

        this.appendDailyToPeriod(true);
        
    }//end day loop

    
    summary = "Credits:" + DV.money(creditTotal) +
            "  Debits:" + DV.money(debitTotal) +
            "  Customer Credits:" + DV.money(custCreditTotal)+
            "  Prepaid Credits:" + DV.money(prepaidTotal)+
            "  Returns:"+ DV.money(returnTotal) +
            "  Refunds:"+ DV.money(refundTotal)+
            "  Fees:" + DV.money(feeTotal) +
            "  Interest:" + DV.money(interestTotal)+
            "   Actual Revenue:"+ 
            DV.money(creditTotal - (returnTotal+refundTotal+custCreditTotal+prepaidTotal));
}


private float interestTotal = 0.00f;  //Interest  -- already in debit
private float creditTotal = 0.00f;
private float debitTotal = 0.00f;
private float refundTotal = 0.00f; //Refund -- already in credits
private float feeTotal = 0.00f; //Fee -- already in debit
private float returnTotal = 0.00f; //Return  --  Already in credits
private float custCreditTotal = 0.00f; //A credit not counted as revenue, gift Card etc..
private float prepaidTotal = 0.00f;

/* This method removes Returns so they are not counted as revenue. */
/*private void removeReturns(DefaultTableModel tm){

    String type = "";

    for (int r = 0; r < tm.getRowCount(); r++){

        type = (String)tm.getValueAt(r, 2);//get payment type
        if (type.equals("Return") || type.equals("Refund")){
            tm.removeRow(r);
            r--;
        }
    }

}*/

private void appendDailyToPeriod(boolean insertTotals){

    if (insertTotals){

        Object [] item = new Object [7];
        Float [] totals = totalCols(daily);

        item[0] = new String("");
        item[1] = new Long((Long)daily.getValueAt(daily.getRowCount()-1, 1));

        item[2] = new String(dateformat.format(
                new Date((Long)daily.getValueAt(
                daily.getRowCount()-1, 1)))+ " TOTALS -->>");
                
        item[3] = new String("");
        item[4] = new Float(totals[0]);
        item[5] = new Float(totals[1]);
        
        daily.addRow(item);

    }

    Object [] row;
    for (int d = 0; d < daily.getRowCount(); d++){

        row = DV.getTableRow(daily, d);
        period.addRow(row);

    }
    this.addToDailies(daily);


}

/* Modify this to work only on the period and to provide return, refund summary
 contrasted with total recorded revenue.*/
private Float [] totalCols(DefaultTableModel tm){
    float debit=0.00f, credit=0.00f;
    String code = "";

    for (int r = 0; r < tm.getRowCount(); r++){

        code = (String)tm.getValueAt(r, 2);
        if (code.contains("TOTALS -->>")) continue;

        debit += (Float)tm.getValueAt(r, 4);
        credit += (Float)tm.getValueAt(r, 5);
    }
    return new Float[] {debit, credit};
}

private void addToDailies(DefaultTableModel tm){
    dailies.trimToSize();
    dailies.add(DV.copyTableModel(tm));
}

public DefaultTableModel getPeriod(){

    return period;
}

public void printReport(){
    if (period.getRowCount() < 1) return;

    LinePrinter lp = new LinePrinter(new Font("courier", Font.PLAIN, 10), true);
    lp.setOrientation("Landscape");
    int [] fieldSizes = {10, 10, 20, 32, 12,12};
    Object value;
    Class objId;
    StringBuilder sb = new StringBuilder();
    

    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    String nl = System.getProperty("line.separator");
    lp.addLine(this.getName() + "  "+ df.format(new Date(startDate)) + " To " +
            df.format(new Date(endDate)));
    lp.newLine();
    String chunk = "";

    sb.append(DV.addSpace("Inv #", 10, ' '));
    sb.append(DV.addSpace("Date", 10, ' '));
    sb.append(DV.addSpace("Type", 20, ' '));
    sb.append(DV.addSpace("Memo", 32, ' '));
    sb.append(DV.addSpace("Debit", 12, ' '));
    sb.append(DV.addSpace("Credit", 12, ' '));

    lp.addLine(sb.toString());
    lp.addLine(DV.addSpace("-", 98, '-'));
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
        
        sb = new StringBuilder();
    }

    lp.newLine();
    lp.addLine(DV.addSpace("=== Summary ",98, '='));
    lp.addLine("Signs (+/-) show how the entry types affect the invoice.");
    char c = '_';

    chunk = DV.addSpace("CREDITS(-) ", 84, c) + DV.money(creditTotal);
    lp.addLine(chunk);
    
    chunk = DV.addSpace("Customer Credits(-) ", 84, c) + DV.money(custCreditTotal);
    lp.addLine(chunk);

    chunk = DV.addSpace("Prepaid Credits(-) ", 84, c) + DV.money(prepaidTotal);
    lp.addLine(chunk);

    chunk = DV.addSpace("Returns(-) ", 84, c)+ DV.money(returnTotal);
    lp.addLine(chunk);

    chunk = DV.addSpace("Non-Revenue Credits ", 84, c)+ DV.money(returnTotal+custCreditTotal);
    lp.addLine(chunk);
    
    chunk = DV.addSpace("DEBITS(+) ", 72, c) + DV.money(debitTotal);
    lp.addLine(chunk);

    
    chunk = DV.addSpace("Refunds(+) ", 72, c)+ DV.money(refundTotal);
    lp.addLine(chunk);

    chunk = DV.addSpace("Fees(+) ", 72, c) + DV.money(feeTotal);
    lp.addLine(chunk);
    
    chunk = DV.addSpace("Interest(+) ", 72, c) + DV.money(interestTotal);
    lp.addLine(chunk);
    
    chunk = DV.addSpace("Actual Revenue ", 84, c) +
            DV.money(creditTotal - (returnTotal + refundTotal + custCreditTotal+prepaidTotal));
    lp.addLine(chunk);
    
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


    int [] fieldSizes = {10, 10, 20, 32, 14,14};
    Object value;
    Class objId;
    StringBuilder sb = new StringBuilder();

    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

    String nl = System.getProperty("line.separator");

    String chunk = "";

    sb.append(DV.addSpace("Inv #", 10, ' '));
    sb.append(DV.addSpace("Date", 10, ' '));
    sb.append(DV.addSpace("Type", 20, ' '));
    sb.append(DV.addSpace("Memo", 32, ' '));
    sb.append(DV.addSpace("Debit", 14, ' '));
    sb.append(DV.addSpace("Credit", 14, ' '));


    pdf.setHeader(this.getName() + "  "+ df.format(new Date(startDate)) + " To " +
            df.format(new Date(endDate)) + nl +
            sb.toString() + nl +
            DV.addSpace("-", 110, '-'));


    sb = new StringBuilder();

    for (int r = 0; r < period.getRowCount(); r++){

        for (int c = 0; c < period.getColumnCount(); c++){

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

    char c = '_';

    sb.append(DV.addSpace("=== Summary ",110, '=')+ nl +
    "Signs (+/-) show how the entry types affect the invoice."+ nl +
    DV.addSpace("CREDITS(-) ", 84, c) + DV.money(creditTotal) + nl +
    DV.addSpace("Customer Credits(-) ", 84, c) + DV.money(custCreditTotal)+ nl +
    DV.addSpace("Prepaid Credits(-) ", 84, c) + DV.money(prepaidTotal)+ nl +
    DV.addSpace("Returns(-) ", 84, c)+ DV.money(returnTotal) + nl +
    DV.addSpace("Non-Revenue Credits ", 84, c)+ DV.money(returnTotal+custCreditTotal)+ nl +
    DV.addSpace("DEBITS(+) ", 72, c) + DV.money(debitTotal)+ nl +
    DV.addSpace("Refunds(+) ", 72, c)+ DV.money(refundTotal)+ nl +
    DV.addSpace("Fees(+) ", 72, c) + DV.money(feeTotal)+ nl +
    DV.addSpace("Interest(+) ", 72, c) + DV.money(interestTotal)+ nl +
    DV.addSpace("Actual Revenue ", 84, c) +
            DV.money(creditTotal - (returnTotal + refundTotal + custCreditTotal+prepaidTotal)));

    pdf.addParagraph(sb.toString(), true);
    pdf.finish();
    
}

public String getName(){
    return "Revenue Report";
}
public String getSummary(){
    return summary;
}
public int[] ColsToDelete(){
    return null;
}

private DbEngine db;
private String filename="C:/RevenueReport.pdf";
private PdfReportService theReport;
private String title="Revenue Report";
private StringBuilder sb;
private DefaultTableModel daily;
private DefaultTableModel period;
ArrayList dailies = new ArrayList();
private DateFormat dateformat = DateFormat.getDateInstance(DateFormat.SHORT);
private long startDate = 0;
private long endDate = 0;
private long oneDay = (60 * 60 * 1000L);
private String summary="";


}
