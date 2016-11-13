/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package businessmanager.InvoiceQuote;


import RuntimeManagement.GlobalApplicationDaemon;
import businessmanager.Common.Tools;
import datavirtue.DV;
import datavirtue.DbEngine;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Data Virtue
 */
public class InvoiceItem {
private GlobalApplicationDaemon application;
public InvoiceItem(GlobalApplicationDaemon g, Object[] i){
    this.application = g;
    item = i;
    this.db = application.getDb();
    VAT = DV.parseBool(application.getProps().getProp("VAT"), false);
}
public InvoiceItem(Object[] i, GlobalApplicationDaemon application, boolean vat){

    item = i;
    VAT = DV.parseBool(application.getProps().getProp("VAT"), false);
    this.db = application.getDb();
    this.application = application;
}
private boolean VAT = false;
public static ArrayList getItemList(DefaultTableModel t, GlobalApplicationDaemon application, boolean vat){
    ArrayList al = new ArrayList(t.getRowCount());

    for (int r = 0; r < t.getRowCount(); r++){
        al.add(new InvoiceItem(DV.getTableRow(t, r), application, vat));
    }
    al.trimToSize();
    return al;
}

public static ArrayList getItemList(DefaultTableModel t, GlobalApplicationDaemon g){
    ArrayList al = new ArrayList(t.getRowCount());

    for (int r = 0; r < t.getRowCount(); r++){
        al.add(new InvoiceItem(g, DV.getTableRow(t, r)));
    }
    al.trimToSize();
    return al;
}

public int getKey(){

    return (Integer)item[0];
}
public int getInvoiceKey(){
    return (Integer)item[1];
}
public long getDate(){
    return (Long)item[2];
}
public float getQty(){
    return (Float)item[3];
}
public String getCode(){
    return (String)item[4];
}
public String getDesc(){
    return (String)item[5];
}
public float getUnit(){
    return Tools.round((Float)item[6]);
}
public boolean isTax1(){
    return (Boolean)item[7];
}
public boolean isTax2(){
    return (Boolean)item[8];
}
public float getCost(){
    return Tools.round((Float)item[9]);
}
public float getUnitTotal(){
    return Tools.round(getQty() * getUnit());
}
public float getCostTotal(){
    return Tools.round(getQty() * getCost());
}
/**This is the item total including tax, expensive method!
 Returns -1 if no DbEngine constructor was used.*/
/* CURRENTLY UNUSED 1.5.7 */
public float getItemTotal(){

    float total = getUnitTotal();
    float tax1 = 0.00f, tax2 = 0.00f;

    if (isTax1() || isTax2()){
        if (db == null) return -1.00f;
        Invoice inv = new Invoice(application, this.getInvoiceKey());
        tax1 = inv.getTax1Rate();
        tax2 = inv.getTax2Rate();
        inv = null;
    }

    //VAT ADDIN
    if(isTax1()) total += (total * tax1);
    if(isTax2()) total += (total * tax2);

    return Tools.round(total);
}

public float getTax1Total(){
    float t = 0.00f;
    float taxRate;
    if (this.isTax1()){
        if (db == null) return -1.00f;
        Invoice inv = new Invoice(application, this.getInvoiceKey());
        taxRate = inv.getTax1Rate();
        inv = null;
        if (VAT) {
            //System.out.println("'InvoiceItem: getTax1Total: VAT " + Tools.round(Invoice.extractGST(getUnitTotal(), taxRate)));
            return Tools.round(Invoice.extractGST(getUnitTotal(), taxRate));
        }else {
            return Tools.round(getUnitTotal() * taxRate);
        }
    }
    return t;
}

public float getTax2Total(){
    float t = 0.00f;
    float taxRate;
    if (this.isTax2()){
        if (db == null) return -1.00f;
        Invoice inv = new Invoice(application, this.getInvoiceKey());
        taxRate = inv.getTax2Rate();
        inv = null;
        return Tools.round((getUnitTotal() * taxRate));
    }
    return t;
}

public Object [] getItem(){
    return item;
}
private Object [] item = new Object[10];
private DbEngine db = null;
}
