/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import org.apache.log4j.Logger;

/**
 * System utilities
 *
 * @version 1.0
 * @since 1.0
 */

public class KSystemUtil {
    private static Logger logger = Logger.getLogger(KSystemUtil.class);

    public static void sleep(Long ms) { 
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
    }
}
