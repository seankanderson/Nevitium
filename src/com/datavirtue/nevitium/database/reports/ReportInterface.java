/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datavirtue.nevitium.database.reports;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Data Virtue
 */
public interface ReportInterface {
    public void setDateRange(long start, long end);
    public void SetTitle(String t);
    public String getName();
    public void SetOutputFilename(String f);
    public void buildReport();
    public void printReport();
    public void pdfReport(String filename);
    public DefaultTableModel getPeriod();
    public String getSummary();
    public int[] ColsToDelete();
    public boolean ignoreDate();
    public int [] getColWidths();
}
