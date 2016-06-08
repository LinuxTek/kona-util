/*
 * Copyright (C) 2014 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.util.Arrays;
import java.util.Random;

import org.apache.log4j.Logger;
/**
 * Random utilities
 *
 * @version 1.0
 * @since 1.0
 */

public class KRandomUtil {
    private static Logger logger = Logger.getLogger(KRandomUtil.class);

    /**
     * Returns an array of _count_ digits that range from minValue (inclusive)
     * to maxValue (inclusive).
     *
     * Used to generate a split pin.  For example, if a pin is 6
     * digits long and you wanted 3 random offsets to ask the user
     * to return, you would call:  
     *
     *            getRandomDigits(3, 5)
     *
     * In this case, the offsets would range from 0 to 5.
     */
    public static Integer[] getRandomDigits(int count, int minValue, int maxValue) {
        maxValue += 1;
        if (maxValue < minValue) throw new IllegalArgumentException("Min value cannot exceed max value");
        Random r = new Random();
        Integer[] digits = new Integer[count];
        int index = 0;
        LOOP: while (index < count)
        {
            int x = r.nextInt(maxValue);
            while (x<minValue) x = r.nextInt(maxValue);
            if (index > 0)
            {
                for (int i=0; i<index; i++)
                    if (digits[i] == x)
                        continue LOOP;
            }
            digits[index] = x;
            index++;
        }
        return digits;
    }

    /**
     * Return _count_ digits that are between 0 and _maxValue_ inclusive.
     */
    public static Integer[] getRandomDigits(int count, int maxValue) {
        return getRandomDigits(count, 0, maxValue);
    }

    /**
     * Return _count_ digits that are between 0 and 9 inclusive.
     */
    public static Integer[] getRandomDigits(int count) {
        return getRandomDigits(count, 0, 9);
    }

    /**
     * Returns the digits from getRandomDigits sorted in ascending
     * numerical order.
     */
    public static Integer[] getSortedRandomDigits(int count, int minValue, int maxValue) {
        Integer[] digits = getRandomDigits(count, minValue, maxValue);
        Arrays.sort(digits);
        return (digits);
    }
}
