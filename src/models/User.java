/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author SeanAnderson
 */
@DatabaseTable(tableName = "users")
public class User extends BaseModel {

    @DatabaseField
    private String userName;
    @DatabaseField
    private String key;
    @DatabaseField
    private boolean master;
    @DatabaseField
    private long inventory;
    @DatabaseField
    private long contacts;
    @DatabaseField
    private long invoices;
    @DatabaseField
    private long invoiceManager;
    @DatabaseField
    private long reports;
    @DatabaseField
    private long checks;
    @DatabaseField
    private long exports;
    @DatabaseField
    private long settings;
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public long getInventory() {
        return inventory;
    }

    public void setInventory(long inventory) {
        this.inventory = inventory;
    }

    public long getContacts() {
        return contacts;
    }

    public void setContacts(long contacts) {
        this.contacts = contacts;
    }

    public long getInvoices() {
        return invoices;
    }

    public void setInvoices(long invoices) {
        this.invoices = invoices;
    }

    public long getInvoiceManager() {
        return invoiceManager;
    }

    public void setInvoiceManager(long invoiceManager) {
        this.invoiceManager = invoiceManager;
    }

    public long getReports() {
        return reports;
    }

    public void setReports(long reports) {
        this.reports = reports;
    }

    public long getChecks() {
        return checks;
    }

    public void setChecks(long checks) {
        this.checks = checks;
    }

    public long getExports() {
        return exports;
    }

    public void setExports(long exports) {
        this.exports = exports;
    }

    public long getSettings() {
        return settings;
    }

    public void setSettings(long settings) {
        this.settings = settings;
    }
    
    
}
