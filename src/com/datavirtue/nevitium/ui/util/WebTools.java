/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.ui.util;
import java.util.regex.*;

/**
 *
 * @author Data Virtue
 */
public class WebTools {

    /** isEmailValid: Validate email address using Java reg ex.
    * This method checks if the input string is a valid email address.
    * @param email String. Email address to validate
    * @return boolean: true if email address is valid, false otherwise.
    */

    public static boolean isEmailValid(String email){
    boolean isValid = false;

   /*
   Email format: A valid email address will have following format:
           [\\w\\.-]+: Begins with word characters, (may include periods and hypens).
       @: It must have a '@' symbol after initial characters.
       ([\\w\\-]+\\.)+: '@' must follow by more alphanumeric characters (may include hypens.).
   This part must also have a "." to separate domain and subdomain names.
       [A-Z]{2,4}$ : Must end with two to four alaphabets.
   (This will allow domain names with 2, 3 and 4 characters e.g pa, com, net, wxyz)

   Examples: Following email addresses will pass validation
   abc@xyz.net; ab.c@tx.gov
   */

   //Initialize reg ex for email.
   String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
   CharSequence inputStr = email;
   //Make the comparison case-insensitive.
   Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
   Matcher matcher = pattern.matcher(inputStr);
   if(matcher.matches()){
   isValid = true;
   }
   return isValid;
   }



}
