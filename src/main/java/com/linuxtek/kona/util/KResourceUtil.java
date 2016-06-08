/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Collection of utilities to help work with resources
 */

public class KResourceUtil {
	private static Logger logger = Logger.getLogger(KResourceUtil.class);

	public static InputStream getResourceAsStream(String file) {
        return getResourceAsStream(null, file);
        
	}
    
	public static InputStream getResourceAsStream(ClassLoader cl, String file) {
        logger.debug("getResourceAsStream called for: " + file);
        
		InputStream in = null;
        boolean useSystemClassLoader = false;
        
		if (cl == null) {
			//Class<KResourceUtil> clazz = KResourceUtil.class;
			//cl = clazz.getClassLoader();
            
			cl = Thread.currentThread().getContextClassLoader();
            useSystemClassLoader = true;
		}
            
		in = cl.getResourceAsStream(file);

		if (in == null && useSystemClassLoader) {
			cl = ClassLoader.getSystemClassLoader();
			in = cl.getResourceAsStream(file);
		}
		return (in);
	}
    
    
	public static String getResourceAsString(String file) throws IOException {
        return getResourceAsString(file, null);
	}
    
	public static String getResourceAsString(String file, String encoding) throws IOException {
        if (encoding == null) {
        	encoding = "UTF-8";
        }
        InputStream in = getResourceAsStream(file);
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, encoding);
		return writer.toString();
	}
}
