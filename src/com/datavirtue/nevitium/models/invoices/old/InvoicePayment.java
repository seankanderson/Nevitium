/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.models.invoices.old;

import com.datavirtue.nevitium.ui.util.Tools;

/**
 *
 * @author Data Virtue
 */
public class InvoicePayment {

public InvoicePayment(Object [] p){
    payment = p;
}

public int getKey(){

    return (Integer)payment[0];
}
public String getInvoiceNumber(){
    return (String)payment[1];
}
public long getDate(){
    return (Long)payment[2];
}
public String getType(){
    return (String)payment[3];
}
public String getMemo(){
    return (String)payment[4];
}
public double getDebit(){
    return Tools.round((Float)payment[5]);
}
public double getCredit(){
    return Tools.round((Float)payment[6]);
}
public boolean isCredit(){
   if (getCredit() > 0.00f) return true;
   return false;
}
public Object[] getUserData(){

    Object [] pmt = new Object[6];

    pmt[0] = new String(getInvoiceNumber());
    pmt[1] = new Long(getDate());
    pmt[2] = new String(getType());
    pmt[3] = new String(getMemo());
    pmt[4] = new Float(getDebit());
    pmt[5] = new Float(getCredit());

    return pmt;
}

    private Object [] payment;


}
