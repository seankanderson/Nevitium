

package com.datavirtue.nevitium.services.exceptions;

/**
 *
 * @author SeanAnderson
 */
public class DuplicateUserNameException extends Exception {

    @Override 
    public String getMessage() {
        return "More than one user record exists with this username. Contact technical support.";
    }
    
}
