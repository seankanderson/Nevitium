/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package businessmanager.InvoiceQuote;

/**
 *
 * @author dataVirtue
 */
public class InvoiceAux {
private int invoice_key=0;
private String tax_id = "";
private String license = "";
private String PO_number="";
private String project_number="";
private long due_date = 0;
private String custom1="";
private String custom2="";

public InvoiceAux(int invoiceKey){
    //populate aux info from invoice key
}

    public String getPO_number() {
        return PO_number;
    }

    public void setPO_number(String PO_number) {
        this.PO_number = PO_number;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public long getDue_date() {
        return due_date;
    }

    public void setDue_date(long due_date) {
        this.due_date = due_date;
    }

    public int getInvoice_key() {
        return invoice_key;
    }

    public void setInvoice_key(int invoice_key) {
        this.invoice_key = invoice_key;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getProject_number() {
        return project_number;
    }

    public void setProject_number(String project_number) {
        this.project_number = project_number;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }









}
