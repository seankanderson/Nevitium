/*
 * CheckStub.java
 *
 * Created on July 13, 2007, 1:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.datavirtue.nevitium.models.checks;

/**
 *
 * @author Data Virtue
 */
public class CheckStub {

    private final String payee;
    private final String date;
    private final String number;
    private final String amount;
    private final String memo;
    private final String[] address;

    /**
     * Creates a new instance of CheckStub
     */
    public CheckStub(String Payee, String[] address, String theDate, String checkNumber, String theAmount, String theMemo) {

        payee = Payee;
        this.address = address;

        date = theDate;
        number = checkNumber;
        amount = theAmount;
        memo = theMemo;

    }

    public String getStreet() {

        return address == null ? null : address[2];

    }

    public String getAddr2() {
        return address == null ? null : address[3];
    }

    public String getRegion() {

        return address == null ? null : address[4];

    }

    public String getCity() {
        return address == null ? null : address[5];
    }

    public String getPayee() {

        return payee;

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
