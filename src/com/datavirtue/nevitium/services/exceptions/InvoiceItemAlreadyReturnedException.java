

package com.datavirtue.nevitium.services.exceptions;

/**
 *
 * @author SeanAnderson
 */
public class InvoiceItemAlreadyReturnedException extends Exception {

     public InvoiceItemAlreadyReturnedException(String message, Throwable error) {
            super(message, error);
        }
        
        public InvoiceItemAlreadyReturnedException(String message) {
            super(message);
        }
}
