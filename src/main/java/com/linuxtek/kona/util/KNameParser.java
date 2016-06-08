/*
 * Copyright (C) 2014 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;


/**
 * Parse a name into first, middle, last.
 *
 * http://stackoverflow.com/questions/1330103/java-name-parse-library
 * 
 * @version 1.0
 * @since 1.0
 */

public class KNameParser {
	private static Logger logger = Logger.getLogger(KNameParser.class);
    
    private String firstName = "";
    private String lastName = "";
    private String middleName = "";
    private boolean capitalizeFully = false;
    private List<String> middleNames = new ArrayList<String>();
    private List<String> titlesBefore = new ArrayList<String>();
    private List<String> titlesAfter = new ArrayList<String>();
    private String[] prefixes = { "dr", "mr", "ms", "atty", "prof", "miss", "mrs" };
    private String[] suffixes = { "jr", "sr", "ii", "iii", "iv", "v", "vi", "esq", "2nd", "3rd", "jd", "phd",
            "md", "cpa" };

    public static KNameParser parse(String name, boolean capitalizeFully) {
        return new KNameParser(name, capitalizeFully);
    }
    
    public KNameParser() {
    }

    public KNameParser(String name) {
        parseName(name);
    }
    
    public KNameParser(String name, boolean capitalizeFully) {
        this.capitalizeFully = capitalizeFully;
        parseName(name);
    }
    
    /** Convert each name to be made up of a titlecase character and then a series of lowercase characters. */
    public void setCapitalizeFully(boolean capitalizeFully) {
        this.capitalizeFully = capitalizeFully;
    }

    private void reset() {
    	capitalizeFully = false;
        firstName = lastName = middleName = "";
        middleNames = new ArrayList<String>();
        titlesBefore = new ArrayList<String>();
        titlesAfter = new ArrayList<String>();
    }

    private boolean isOneOf(String checkStr, String[] titles) {
        for (String title : titles) {
            if (checkStr.toLowerCase().startsWith(title))
                return true;
        }
        return false;
    }

    public void parseName(String name) {
        logger.debug("parseName called for: " + name);
        if (StringUtils.isBlank(name))
            return;
        this.reset();
        String[] words = name.split(" ");
        boolean isFirstName = false;

        for (String word : words) {
            if (StringUtils.isBlank(word))
                continue;
            if (word.charAt(word.length() - 1) == '.') {
                if (!isFirstName && !this.isOneOf(word, prefixes)) {
                    firstName = word;
                    isFirstName = true;
                } else if (isFirstName) {
                    middleNames.add(word);
                } else {
                    titlesBefore.add(word);
                }
            } else {
                if (word.endsWith(","))
                    word = StringUtils.chop(word);
                if (isFirstName == false) {
                    firstName = word;
                    isFirstName = true;
                } else {
                    middleNames.add(word);
                }
            }
        }
        
        if (middleNames.size() > 0) {
            boolean stop = false;
            List<String> toRemove = new ArrayList<String>();
            for (int i = middleNames.size() - 1; i >= 0 && !stop; i--) {
                String str = middleNames.get(i);
                if (this.isOneOf(str, suffixes)) {
                    titlesAfter.add(str);
                } else {
                    lastName = str;
                    stop = true;
                }
                toRemove.add(str);
            }
            
            if (StringUtils.isBlank(lastName) && titlesAfter.size() > 0) {
                lastName = titlesAfter.get(titlesAfter.size() - 1);
                titlesAfter.remove(titlesAfter.size() - 1);
            }
            
            for (String s : toRemove) {
                middleNames.remove(s);
            }
        }
        
        for (String middle : middleNames) {
            middleName += (middle + " ");
        }
        middleName = StringUtils.chop(middleName);
        
        if (capitalizeFully) {
        	firstName = WordUtils.capitalizeFully(firstName);
        	middleName = WordUtils.capitalizeFully(middleName);
        	lastName = WordUtils.capitalizeFully(lastName);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        /*
        if (StringUtils.isBlank(this.middleName)) {
            for (String name : middleNames) {
                middleName += (name + " ");
            }
            middleName = StringUtils.chop(middleName);
        }
        */
        return middleName;
    }

    public List<String> getTitlesBefore() {
        return titlesBefore;
    }

    public List<String> getTitlesAfter() {
        return titlesAfter;
    }
}
