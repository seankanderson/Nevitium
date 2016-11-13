/*
 * Tools.java
 *
 * Created on January 4, 2009, 11:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package businessmanager.Common;
import datavirtue.DV;
import gui.PlayWave;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;
import org.apache.commons.codec.binary.Hex;



/**
 *
 * @author Data Virtue
 */
public class Tools {
    
   public static float totalFloatColumn(TableModel tm, int column) {
      
       if (tm == null) return 0.00f;
       int rowCount = tm.getRowCount();
              
       if (rowCount < 1) return 0.0f;
       
       float colTotal=0.00f;
       
       for (int row=0; row < rowCount; row++){
           
           colTotal += (Float)tm.getValueAt(row, column);
           
       }
       
      return Tools.round(colTotal);
      
   }

   public static String buildMD5(String data){
        MessageDigest messageDigest;
        try{
            messageDigest = MessageDigest.getInstance("MD5");
        }catch(java.security.NoSuchAlgorithmException e){
            javax.swing.JOptionPane.showMessageDialog(null, "There was a problem creating the MD5 digest.");
            return "FAILED MD5: "+data;
        }
        messageDigest.reset();
        messageDigest.update(data.getBytes(Charset.forName("UTF8")));
        byte[] resultByte = messageDigest.digest();
        return Hex.encodeHexString(resultByte);
    }
   
   public static final int YSCR = 10;

   public static void playSound(URL url){
       if (url==null) return;
       String f="";
        if (url != null) f = url.getPath();
        //System.out.println("Sound File:  "+f);
        new PlayWave(f).start();

    }


   public static boolean isDecimal(float f){
       Float flt = new Float(f);
       int sig = flt.intValue();
       //System.out.println("Mantissa??   :"+sig);
       if (f > sig) return true;

       return false;
   }

public static float round(float n){

     float v = (float) (Math.round(n*100.00f)/100.00f);
     v = DV.flattenZero(v);
     return v;
}
   public static String colorToString(Color c){

       String color = Integer.toString(c.getRed()) +","+
               Integer.toString(c.getGreen())+","+
               Integer.toString(c.getBlue());
       return color;
   }
   public static Color stringToColor(String c){

       int red, green, blue;

       String [] rgb = Tools.fromComma(c);
       /* on parse error return light light grey */
       if (rgb == null){
           new Color(232,231,231);
       }
       red = Integer.valueOf(rgb[0]);
       green = Integer.valueOf(rgb[1]);
       blue = Integer.valueOf(rgb[2]);

        /*
            lt cyan  191, 236, 238
            lt green 209, 254, 207
            lt red   249, 176, 184
            lt yell  248, 253, 142
            lt blue  198, 198, 253
        */
       Color color = new Color(red,green,blue);

       return color;
   }

   public static Dimension parseDimension(String s){

       String [] dim = Tools.fromComma(s);
    
       if (dim == null || dim.length < 2 || dim[0]==null) {
           return null;
       }
       return new Dimension(Integer.valueOf(dim[0]), Integer.valueOf(dim[1]));
   }
   public static Point parsePoint(String s){
    
       String [] dim = Tools.fromComma(s);
    
       if (dim == null || dim.length < 2 || dim[0]==null) {
           return null;
       }
       return new Point(Integer.valueOf(dim[0]), Integer.valueOf(dim[1]));
   }
   public static String [] fromComma(String csv){

       char [] line = csv.toCharArray();
       String [] stringRecord = new String [3];

       StringBuilder temp = new StringBuilder (line.length);

       /* This loop grabs each comma separated value */
       int index = 0;
       try {
           for (int i = 0; i < stringRecord.length; i++) {

               do {

                   if (index >= line.length) return stringRecord;

                   if (line[index] == ',') {
                       break;

                   }
                   if (line[index] != '"') {
                       temp.append(line[index]);  //this took a while to straighten out
                       if (index == line.length) break;
                   }
                   index++;


               } while (index < line.length && line[index] != ',');


               temp.trimToSize();
               stringRecord[i] = temp.toString();
               //System.out.println("Tools.fromComma: "+stringRecord[i]);
               temp.delete(0, temp.length());
               index++;

           }
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }
       return stringRecord;

   }

   public static String [] formatAddress(Object [] custRecord){

       String nl = System.getProperty("line.separator");
       String [] address = new String [5];
       try{
       
       String code = (String) custRecord[17];  //country code

        address[0] = (String) custRecord[1]+ nl;  //company
        address[1] = (String) custRecord[2] +" "+ (String) custRecord[3]+nl; //name
        address[2] = (String) custRecord[4]+ nl; //street
        address[3] = (String) custRecord[5] + nl; //address 2

        if (code.equalsIgnoreCase("US") ||
                code.equalsIgnoreCase("CA") ||
                code.equalsIgnoreCase("AU"))
            address[4] = (String) custRecord[6]+ "  " +
                    (String) custRecord[7] + "  " + (String) custRecord[8]+nl;

        if (code.equalsIgnoreCase("GB") ||
                code.equalsIgnoreCase("ZA")||code.equalsIgnoreCase("IN") || code.equalsIgnoreCase("PH")){

            address[4] = (String) custRecord[6] + nl +
                    (String) custRecord[8]+nl;

        }

       }catch(Exception e){

           javax.swing.JOptionPane.showMessageDialog(null,
                   "The formatAddress method most likely encountered an unexpected data type.  Restart the application and contact software@datavirtue.com if it repeats.");
           return null;
       }
        /* Phone removed from cust info in version 1.5 */
        //address[5] = (String) custRecord[10];

        /* Loop through and capitalize? */

       return address;

   }

   public static void exportTable(TableModel tm, String filename, boolean header){
    Object value;
    Class objId;
    StringBuilder sb = new StringBuilder();
    int lastCol = tm.getColumnCount() - 1;
    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
    String nl = System.getProperty("line.separator");
    DV.writeFile(filename, header + nl, false);

    if (header){
        for (int c = 0; c < tm.getColumnCount(); c++){

            sb.append("\"" + tm.getColumnName(c) + "\"");
            if (c != lastCol) sb.append(",");

        }
        DV.writeFile(filename, sb.toString() + nl, false);
    }
    sb = new StringBuilder();
    
    for (int r = 0; r < tm.getRowCount(); r++){

        for (int c = 0; c < tm.getColumnCount(); c++){

            value = tm.getValueAt(r, c);
            objId = DV.idObject(value);

            if (objId.equals(String.class)){
                sb.append("\"" + (String)value + "\"");
            }
            if (objId.equals(Float.class)){
                sb.append("\"" + Float.toString((Float)value) + "\"");
            }
            if (objId.equals(Integer.class)){
                sb.append("\"" + Integer.toString((Integer)value) + "\"");
            }
            if (objId.equals(Boolean.class)){
                sb.append("\"" + Boolean.toString((Boolean)value) + "\"");
            }
            if (objId.equals(Date.class)){
                sb.append("\"" + df.format(new Date((Long)value)) + "\"");
            }

            if (c != lastCol) sb.append(",");

            


        }
        DV.writeFile(filename, sb.toString()+nl, true);
        sb = new StringBuilder();
    }


}

   public static boolean verifyEmailAddress(String email){

       if (email.isEmpty()){
           javax.swing.JOptionPane.showMessageDialog(null, "There is no email address for this invoice/quote: "+
                   '('+email+')');
                   return false;
       }

       if (!WebTools.isEmailValid(email)){
           javax.swing.JOptionPane.showMessageDialog(null, "The contact does not have a valid email address: "+
                   '('+email+')');
                   return false;
       }
       return true;

   }


   public static boolean verifyElevatedPermissions(String f){

       File file = new File(f);
       String nl = System.getProperty("line.separator");

       /* Test is skipped if the file doesn't exsist */
       if (file.exists() && !file.canWrite()) {
         javax.swing.JOptionPane.showMessageDialog(null, "It appears you do not have permission to write certain data files owned by Nevitium." + nl +
                 "Nevitium needs elevated permissions to work properly."+ nl+
                 "Shut down Nevitium & Contact technical support.");
                 return false;
       }

       return true;
   }

public static String getBoolString(boolean val){
        if (val) return "true";
        return "false";
    }
public static boolean getStringBool(String val){

        if (val.equalsIgnoreCase("true")) return true;
        return false;

    }
public static int getStringInt(String val, int def){

    try {
        return Integer.parseInt(val);
    }catch (Exception e){
        return def;
    }

}

public static boolean isFolderEmpty(String folder){


    File f = new File(folder);

        if (!f.exists()) return false;

        java.io.File [] files = f.listFiles();

        /* TODO: check for permissions here as well */

        if (files.length < 1 && f.isDirectory()){
            return true;
        }
        return false;

}

}