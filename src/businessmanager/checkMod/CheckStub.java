/*
 * CheckStub.java
 *
 * Created on July 13, 2007, 1:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package businessmanager.checkMod;

/**
 *
 * @author Data Virtue
 */
public class CheckStub {
    private String payee;
    private String date;
    private String number;
    private String amount;
    private String memo;
    private String [] address;
    
    
    /** Creates a new instance of CheckStub */
    public CheckStub(String Payee, String [] address, String theDate, String checkNumber, String theAmount, String theMemo) {
    
    payee = Payee;
    this.address = address;
    
    date = theDate;
    number = checkNumber;
    amount = theAmount;
    memo = theMemo;
    
    }
    
    
    public String getStreet() {
        
        return address[2];
        
    }
    public String getAddr2(){
        return address[3];
    }
    
    public String getRegion() {
        
        return address[4];
        
    }
    public String getCity(){
        return address[5];
    }
    
    public String getPayee() {
        
        return address[1];
        
    }
    
    public String getDate() {
        
        return date;
        
    }
    
    public String getNumber() {
        
        return number;
        
    }
    
    public String getAmount() {
        
        return amount;
        
    }
    
    public String getMemo() {
        
        return memo;
        
    }
}
