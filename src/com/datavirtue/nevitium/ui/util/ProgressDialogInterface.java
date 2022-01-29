/*
 * ProgressDialogInterface.java
 *
 * Created on June 29, 2007, 10:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.ui.util;

/**
 *
 * @author Data Virtue
 */
public interface ProgressDialogInterface {
    
public void setBarMax(int value);

public void updateBar(int value);

public void close();



}
