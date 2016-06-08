/*
 * Copyright (C) 2013 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.math.BigDecimal;

/**
 * Math utilities
 *
 * @version 1.0
 * @since 1.0
 */

public class KMathUtil {
    //private static Logger logger = Logger.getLogger(KMathUtil.class);

    public static BigDecimal divide(BigDecimal bd1, BigDecimal bd2) {
        if (bd1.compareTo(new BigDecimal(0)) == 0)
            return (new BigDecimal(0));
            
        if (bd2.compareTo(new BigDecimal(0)) == 0)
            return (null);
            
        BigDecimal result = bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP);
        return (result);
    }   
}
