package com.datavirtue.nevitium.services.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author SeanAnderson
 */
public class CurrencyUtil {

    private static  double getHundredth(double decimal) {
        double hundredth = CurrencyUtil.round(((decimal * .1f) % 1) * 10);
        return hundredth;
    }

    private static  double getDecimal(double amt) {
        double decimal = CurrencyUtil.round((amt % 1) * 100);
        decimal = decimal - (decimal % 1);
        return decimal;
    }

    private static  double roundToNearest5th(double amt) {
        double hundredth = getHundredth(getDecimal(amt));
        if (hundredth <= 3) {
            return (amt -= (hundredth * .01)); //rounded down to nearest 5th
        }
        if (hundredth > 3) {
            return (amt += (.05 - (hundredth * .01))); //rounded up to nearest 5th
        }
        return amt;
    }

    private static  double roundToNearest10th(double amt) {
        double hundredth = getHundredth(getDecimal(amt));
        if (hundredth <= 5) {
            return (amt -= (hundredth * .01)); //rounded down to nearest 10th
        }
        if (hundredth > 5) {
            return (amt += (.10 - (hundredth * .01))); //rounded up to nearest 10th
        }
        return amt;
    }
    
    public static double round(double value) {
        BigDecimal result = new BigDecimal(Double.toString(value)).setScale(2,  RoundingMode.HALF_UP);
        return result.doubleValue();
    }
    
    public static String money (double money) {
        //Locale locale = Locale.getDefault();
        money = round(money);
        NumberFormat formatter = new DecimalFormat("#,###,##0.00");        
        return formatter.format(money);
    }
    
    public static double parseToDouble(String value) {
        var parsedValue = new BigDecimal(value.trim()).doubleValue();
        return round(parsedValue);
    }

}
