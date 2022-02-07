package com.datavirtue.nevitium.services.util;

import com.datavirtue.nevitium.ui.util.Tools;

/**
 *
 * @author SeanAnderson
 */
public class CurrencyUtil {

    private static  double getHundredth(double decimal) {
        double hundredth = Tools.round(((decimal * .1f) % 1) * 10);
        return hundredth;
    }

    private static  double getDecimal(double amt) {
        double decimal = Tools.round((amt % 1) * 100);
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

}
