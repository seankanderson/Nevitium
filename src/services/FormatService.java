package services;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 *
 * @author SeanAnderson
 */
public class FormatService {

    public static Float parseFloat(String numberString) {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        try {
            Number number = format.parse(numberString);
            return number.floatValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Integer parseInteger(String numberString) {
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        try {
            Number number = format.parse(numberString);
            return number.intValue();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
