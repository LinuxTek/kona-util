/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.log4j.Logger;

public class KDateUtil 
{
    private static Logger logger = Logger.getLogger(KDateUtil.class);

    // 2015-01-06T03:51:57+00:00
    // 2015-01-06T03:51:57Z
    public static Date parseISO8601(String timestamp) {
    	Calendar cal = javax.xml.bind.DatatypeConverter.parseDateTime(timestamp);
        return cal.getTime();
    }
    
	public static Date getDate() 
	{
        return (new Date());
	}

	/**
	 * month ranges from 1 to 12 (not 0 to 11)
	 */
	public static Date getDate(int year, int month, int date) 
	{
		return getDate(year, month, date, 0, 0, 0);
	}

	/**
	 * month ranges from 1 to 12 (not 0 to 11)
	 */
	public static Date getDate(int year, int month, int date, 
                                   int hrs, int min) 
	{
		return getDate(year, month, date, hrs, min, 0);
	}


	/**
	 * month ranges from 1 to 12 (not 0 to 11)
	 */
    private static Calendar getCalendar()
    {
        return (getCalendar(null, null));
    }

    private static Calendar getCalendar(TimeZone timeZone,
                                        Locale locale)
    {
        if (timeZone == null)
		    timeZone = TimeZone.getDefault();

        if (locale == null)
		    locale = Locale.getDefault();

		Calendar c = new GregorianCalendar(timeZone, locale);
        return (c);
    }

    private static DateFormat getDateFormat(Locale locale)
    {
        if (locale == null)
		    locale = Locale.getDefault();

		return (DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
											   DateFormat.MEDIUM, locale));
    }

	public static Date getDate(int year, int month, int date, 
                                   int hrs, int min, int sec) 
 	{

		if (month < 1 || month > 12)
			throw new IllegalArgumentException("Valid range of month is 1-12");

        Calendar c = getCalendar();
        c.clear(Calendar.MILLISECOND);
		c.set(year, month-1, date, hrs, min, sec);
        return (c.getTime());
	}

	public static Date getDate(long time) 
	{
		Date date = new Date(time);
        return (date);
	}

	public static Date parse(String s)
	{
        return (parse(s, (Locale) null));
    }

	public static Date parse(String s, Locale locale)
	{
		Date d = getDateFormat(locale).parse(s, new ParsePosition(0));
        return (d);
	}

    public static Date parse(String s, String format)
    {
        Date d = null;

        try
        {
            DateFormat formatter = new SimpleDateFormat(format);
            d = (Date)formatter.parse(s);
        }
        catch (ParseException e)
        {
            logger.error(e);
        }

        return (d);
    }

    public static class JsonDateConverter extends DateTimeConverter {
        private String jsonFormat = null;
        public JsonDateConverter() {
        	super();
        }
        
        public JsonDateConverter(String jsonFormat) {
        	this.jsonFormat = jsonFormat;
        }
        
		@Override
		protected Class<Date> getDefaultType() {
			return Date.class;
		}
        
        @Override @SuppressWarnings("rawtypes")
		public Object convert(Class type, Object value) {
        	logger.debug("JsonDateConverter: trying to parse to Date: " + value);
            
            if (value == null) return null;
            if (value instanceof Date) return value;
            
            try {
            	Long time = Long.valueOf(value.toString());
            	return new Date(time);
            } catch(NumberFormatException e) { }
            
			try {
				return parseJson(value.toString(), jsonFormat);
			} catch (ParseException e) {
                logger.error(e);
                return null;
			}
        }
    }
    
    public static Date parseJson(String input) throws java.text.ParseException {
    	//return parseJson(input, null);
    	
        try {
        	Long time = Long.valueOf(input);
        	return new Date(time);
        } catch(NumberFormatException e) { }
        
		try {
			return parseJson(input, null);
		} catch (ParseException e) {
            logger.error(e);
            return null;
		}
    }
    
    // parse a json date string
    public static Date parseJson(String input, String format) throws java.text.ParseException {
        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        if (format == null) {
        	format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        
        //this is zero time so we need to add that TZ indicator for 
        if ( input.endsWith( "Z" ) ) {
            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;
        
            String s0 = input.substring( 0, input.length() - inset );
            String s1 = input.substring( input.length() - inset, input.length() );

            input = s0 + "GMT" + s1;
        }
        
        return df.parse( input );
    }
    
	public static int getDayOfMonth(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.DAY_OF_MONTH));
	}

	public static int getDayOfWeek(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.DAY_OF_WEEK));
	}

    // Sunday=1 .. Saturday=7
    // return Date value with hours/min/sec/ms set to 0
	public static Date getDateForCurrentWeekDay(int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		cal.set(Calendar.DAY_OF_WEEK, day);
		return cal.getTime();
	}
    
    // Sunday=1 .. Saturday=7
	public static Date getDateForCurrentWeekDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, day);
		return cal.getTime();
	}

	
	/** Hour: 1 - 12 */
	public static int getHour(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.HOUR));
	}

	/** Hour: 0 - 24 */
	public static int getHourOfDay(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.HOUR_OF_DAY));
	}

	/** Hour: 0 - 24 */
	public static int get24Hour(Date d)
	{
		return (getHourOfDay(d));
	}

	public static int getMinutes(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.MINUTE));
	}

	/**
	 * Jan = 1 ... Dec = 12
	 */
	public static int getMonth(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.MONTH) + 1);
	}

	public static int getSeconds(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.SECOND));
	}


	public static long getTime() 
	{
        Date d = new Date();
		return (d.getTime());
	}

	public static long getTime(Date d) 
	{
		return (d.getTime());
	}

	public static int getTimeZoneOffset(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.ZONE_OFFSET) + 
					c.get(Calendar.DST_OFFSET));
	}

	public static int getYear(Date d) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		return (c.get(Calendar.YEAR));
	}

	public static Date setDayOfMonth(Date d, int date) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH, date);
        return (c.getTime());
	}
	
	public static Date setHour(Date d, int hour) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.HOUR, hour);
        return (c.getTime());
	}

	public static Date setHourOfDay(Date d, int hours) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, hours);
        return (c.getTime());
	}

	public static Date set24Hour(Date d, int hour24)
	{
		return (setHourOfDay(d, hour24));
	}

	public static Date setMinutes(Date d, int minutes) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.MINUTE, minutes);
        return (c.getTime());
	}

	/**
	 * month ranges from 1 to 12
	 */
	public static Date setMonth(Date d, int month) 
	{
		if (month < 1 || month > 12)
			throw new IllegalArgumentException("Valid range of month is 1-12");

        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.MONTH, month-1);
        return (c.getTime());
	}

	public static Date setSeconds(Date d, int seconds) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.SECOND, seconds);
        return (c.getTime());
	}

	public static Date setTime(Date d, long time) 
	{
		d.setTime(time);
        return (d);
	}
		
	public static Date setYear(Date d, int year) 
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.set(Calendar.YEAR, year);
        return (c.getTime());
	}


    // FIXME: assumes dates are less than 1970 (Epoch Time)
    // use Joda Time to get more accurate results
	public static int diffSecs(Date date1, Date date2)
	{

		if (date1 == null || date2 == null)
			throw new NullPointerException("Date object is null");

        // DateTime d1 = new DateTime(date1);
        // DateTime d2 = new DateTime(date2);

		long s1 = date1.getTime();
		long s2 = date2.getTime();

		if (s1 < s2)
		{
			long tmp = s2;
			s2 = s1;
			s1 = tmp;
		}
			
		long diff = s1 - s2;

		int seconds = (int) ((diff/1000));
		return (seconds);
	}

	public static int diffMins(Date date1, Date date2)
	{
		int seconds = diffSecs(date1, date2);
		int minutes = (int) (seconds/60);
		return (minutes);
	}

	public static int diffHours(Date date1, Date date2)
	{
		int minutes = diffMins(date1, date2);
		int hours = (int) (minutes/60);
		return (hours);
	}

	public static int diffDays(Date date1, Date date2)
	{
		int hours = diffHours(date1, date2);
		int days  = (int) (hours/24);
		return (days);
	}

	public static Date addSecs(Date d, int secs)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.SECOND, secs);
        return (c.getTime());
	}

	public static Date addMins(Date d, int minutes)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.MINUTE, minutes);
        return (c.getTime());
	}

	public static Date addHours(Date d, int hours)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.HOUR, hours);
        return (c.getTime());
	}

	public static Date addDays(Date d, int days)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.DATE, days);
        return (c.getTime());
	}

	public static Date addWeeks(Date d, int weeks)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.WEEK_OF_MONTH, weeks);
        return (c.getTime());
	}

	public static Date addMonths(Date d, int months)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.MONTH, months);
        return (c.getTime());
	}

	public static Date addYears(Date d, int years)
	{
        Calendar c = getCalendar();
        c.setTime(d);
		c.add(Calendar.YEAR, years);
        return (c.getTime());
	}

	public static String toSQLDate(Date d)
	{
        /*
		String sql = getYear() + "-";

		if (getMonth() < 10)
			sql += "0";
		sql += getMonth() + "-";

		if (getDate() < 10)
			sql += "0";
		sql += getDate();

		return (sql);
        */
        return (format(d, "yyyy-MM-dd"));
	}


	public static String format(Date d, String format) {
        return (format(d, format, null, null));
    }
    
	public static String format(Date d, String format, String timeZone) {
        TimeZone tz = null;
        if (timeZone != null) {
        	tz = TimeZone.getTimeZone(timeZone);
        }
        return (format(d, format, null, tz));
    }

	public static String format(Date d, String format, Locale locale)  {
        return format(d, format, locale, null);
	}
    
	public static String format(Date d, String format, Locale locale, TimeZone tz) {
        if (locale == null)
            locale = Locale.getDefault();
        
        try
        {
            /*
            logger.debug("format() called: " +
                        "\n\tDate: " + d +
                        "\n\tformat: " + format +
                        "\n\tlocale: " + locale);
            */

            SimpleDateFormat df = new SimpleDateFormat(format, locale);
            if (tz != null) {
                df.setTimeZone(tz);
            }
            String s = df.format(d);
		    return (s);
        }
        catch (Throwable t)
        {
            StringWriter s = new StringWriter();
            t.printStackTrace(new PrintWriter(s));
            logger.error("format() error:\n" + s, t);
            return (d.toString());
        }
	}

    // return date string "EEE, dd MMM yyyy HH:mm:ss zzz" format in GMT
    // date is assumed to be local timezone.
    public static String formatHttp(Date d)
    {
        try
        {
            String format = "EEE, dd MMM yyyy HH:mm:ss zzz";
            Locale locale = Locale.getDefault();

            SimpleDateFormat df = new SimpleDateFormat(format, locale);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            String s = df.format(d);
            return (s);
        }
        catch (Throwable t)
        {
            StringWriter s = new StringWriter();
            t.printStackTrace(new PrintWriter(s));
            logger.error("format() error:\n" + s, t);
            return (d.toString());
        }
    }
}	
