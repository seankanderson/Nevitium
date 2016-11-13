/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package businessmanager.Connections;
import datavirtue.*;
import java.util.*;
/**
 *
 * @author Data Virtue
 */
public class ShipToDAO {

public ShipToDAO(DbEngine dbe) {
    db = dbe;

    addr[0] = new Integer(0);

}

public ShipToDAO(DbEngine dbe, int key){
    this.key = key;
    db = dbe;
    addr = db.getRecord("connship", key);
    
}
/*   Getters  */

    public Object [] getRecord(){

        return addr;
    }

    public int getKey(){
        return (Integer)addr[0];
    }

    public int getParentKey() {

        return (Integer)addr[1];
    }
    public String getCompany() {

        return (String)addr[2];
    }
    public String getName() {

        return (String)addr[3];
    }
    public String getStreet() {

        return (String)addr[4];
    }
    public String getAddr2() {

        return (String)addr[5];
    }
    public String getCity() {

        return (String)addr[6];
    }
    public String getState() {

        return (String)addr[7];
    }
    public String getPostCode() {

        return (String)addr[8];
    }

    public String getAlphaCountryCode(){
        return (String)addr[9];
    }

    public String getPhone() {

        return (String)addr[10];
    }

    public boolean isDefault(){
        return (Boolean)addr[11];
    }

   /* Setters  */
    public void setParentKey(int i){

        addr[1] = new Integer(i);
    }

    public void setCompany(String s){
        addr[2] = new String(s);

    }
    public void setName(String s){
        addr[3] = new String(s);

    }
    public void setStreet(String s){
        addr[4] = new String(s);

    }
    public void setAddr2(String s){
        addr[5] = new String(s);

    }
    public void setCity(String s){
        addr[6] = new String(s);

    }
    public void setState(String s){
        addr[7] = new String(s);

    }
    public void setPostCode(String s){
        addr[8] = new String(s);

    }

    public void setCountryAlphaCode(String s){
        addr[9] = new String(s);
    }

    public void setPhone(String s){
        addr[10] = new String(s);

    }

    public void setDefault(boolean b){
        addr[11] = new Boolean(b);
    }

    public void save(){

        db.saveRecord("connship", addr, false);
    }


private DbEngine db;
private int key = 0;
private Object [] addr = new Object[12];

}
