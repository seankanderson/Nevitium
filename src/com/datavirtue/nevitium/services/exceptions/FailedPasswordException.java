

package com.datavirtue.nevitium.services.exceptions;

/**
 *
 * @author SeanAnderson
 */
public class FailedPasswordException extends Exception {

    @Override 
    public String getMessage() {
        return "Failed password attemp.";
    }
}
