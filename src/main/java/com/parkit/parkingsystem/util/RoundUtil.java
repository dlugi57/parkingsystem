package com.parkit.parkingsystem.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Util to make some number manipulations
 */
public class RoundUtil {
    /**
     * Round double number to get selected places after comma separator
     *
     * @param value  number to do the round manipulation
     * @param places of the numbers after comma separator
     * @return number with right numbers after comma separator
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
