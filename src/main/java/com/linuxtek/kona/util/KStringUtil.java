/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.io.StringWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Collection of utilities to help work with strings.
 * 
 * @version 1.0
 * @since 1.0
 */

public class KStringUtil {
	private static Logger logger = Logger.getLogger(KStringUtil.class);
    
	private static ObjectMapper mapper = new ObjectMapper(); 
    static {
    	mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }


    public static String createFullName(String firstName, String lastName) {
        return createFullName(firstName, lastName, null, null);
        
    }
    
    public static String createFullName(String firstName, String lastName,
    		String middleName, String suffix) {
    	if (firstName == null && lastName == null && middleName == null) return null;
    	
        String fullName = "";
        
        if (firstName != null) {
            fullName += firstName;
        }
        
        if (middleName != null) {
        	fullName += " " + middleName;
        }
        
        if (lastName != null) {
        	fullName += " " + lastName;
        }
        
        if (suffix != null) {
        	fullName += " " + suffix;
        }
        
        return fullName.trim();
    }
    
	public static String join(Collection<Object> s, String delimiter) {
        logger.debug("join(Collection<Object>) called");
		StringBuffer buffer = new StringBuffer();
		Iterator<Object> iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next().toString());
			while (iter.hasNext()) {
				buffer.append(delimiter);
				buffer.append(iter.next().toString());
			}
		}
		return buffer.toString();
	}
    
	public static String join(List<String> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next());
			while (iter.hasNext()) {
				buffer.append(delimiter);
				buffer.append(iter.next());
			}
		}
		return buffer.toString();
	}

	public static List<String> split(String text, String delimiter) {
	    String[] parts = text.split(delimiter);

	    List<String> list = new ArrayList<String>();

	    for (String part : parts) {
	        list.add(part);
	    }

	    return list;
	}



	public static String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static String join(Object[] s, String delimiter) {
        logger.debug("join(Object[]) called");
		return (join(Arrays.asList(s), delimiter));
	}
    
	/*
	public static String encryptSHA1(String raw) {
		String encrypted = null;
		try {
			encrypted = KEncryptUtil.SHA1(raw);
		} catch (Exception e) {
			logger.error(e);
			throw new IllegalArgumentException("Error encrypting value: " + raw);
		}
		return (encrypted);
	}
	*/

	/**
	 * Return a number as hex value with specified char count.
	 */
	public static String toHex(Long value, int charCount) {
		String s = Long.toHexString(value).toUpperCase();

		while (s.length() > charCount) {
			s = s.substring(1);
			//throw new IllegalStateException("Generated Number larger than "
            //			+ "charCount:\nnumber: " + s + "\ncharCount: " + charCount);
		}

		if (s.length() < charCount) {
			int diff = charCount - s.length();
			for (int i = 0; i < diff; i++)
				s = "0" + s;
		}

		return (s);
	}

	public static String toHex(byte[] data) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;

			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));

				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static boolean isWordInCommaList(String word, String commaList) {
		return (isWordInCommaList(word, commaList, Locale.getDefault()));
	}

	public static boolean isWordInCommaList(String word, String commaList,
			Locale locale) {
		String list[] = commaList.split(",");

		boolean found = false;
		for (String w : list) {
			if (found)
				break;
			w = w.trim().toLowerCase(locale);
			word = word.trim().toLowerCase(locale);

			if (word.equals(w)) {
				found = true;
				break;
			}
		}
		return (found);
	}

	public static String toCommaList(Collection<?> items) {
		if (items == null || items.size() == 0)
			return (null);

		String itemList = "";

		Iterator<?> it = items.iterator();
		while (it.hasNext()) {
			String item = it.next().toString();
			itemList += item + ",";
		}

		itemList = itemList.substring(0, itemList.length() - 1);
		return (itemList);
	}

	public static String interpolate(String s) {
		return (interpolate(s, System.getProperties()));
	}

	public static String interpolate(String s, Properties props) {
		if (s == null)
			return (null);

		StringBuffer sb = new StringBuffer();

		Pattern p = Pattern.compile("\\$\\{.+\\}");
		Matcher m = p.matcher(s);
		while (m.find()) {
			String param = m.group();
			logger.debug("found param: " + param);

			param = param.substring(2, param.length() - 1);
			String value = props.getProperty(param);
			logger.debug("value of [" + param + "]: " + value);

			if (value != null)
				m.appendReplacement(sb, value);
		}
		m.appendTail(sb);
		return (sb.toString());
	}

	// take a String that represents an arry of ints [1,2,3]
	// (with or without []) and return a List of Integers.
	public static List<Integer> toIntList(String arr) {
		// logger.debug("arr: " + arr);

		if (arr.startsWith("["))
			arr = arr.substring(1);

		if (arr.endsWith("]"))
			arr = arr.substring(0, arr.length() - 1);

		// logger.debug("arr: " + arr);

		String list[] = arr.split(",");
		List<Integer> intList = new LinkedList<Integer>();
		for (String s : list) {
			try {
				intList.add(Integer.parseInt(s));
			} catch (NumberFormatException e) {
				logger.error("invalid number in list: " + s);
			}
		}

		return (intList);
	}

	public static Long toLong(Object s) {
		Long x = null;
		if (s == null) {
			return (null);
		}
		try {
			x = new Long(s.toString());
		} catch (NumberFormatException e) {
			logger.error(e);
		}

		return (x);
	}

	public static Integer toInteger(Object s) {
		Integer x = null;
		if (s == null) {
			return (null);
		}
		try {
			x = new Integer(s.toString());
		} catch (NumberFormatException e) {
			logger.error(e);
		}

		return (x);
	}

	public static Boolean toBoolean(Object s) {
		if (s == null) {
			return (null);
		}
		return (new Boolean(s.toString()));
	}

	public static String pluralize(String s) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.pluralize(s));
	}

	public static String singularize(String s) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.singularize(s));
	}

	public static String lowerCamelCase(String s, char... delimiterChars) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.lowerCamelCase(s, delimiterChars));
	}

	public static String upperCamelCase(String s, char... delimiterChars) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.upperCamelCase(s, delimiterChars));
	}

	public static String camelCase(String s, boolean uppercaseFirstLetter,
			char... delimiterChars) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.camelCase(s, uppercaseFirstLetter, delimiterChars));
	}

	public static String underscore(String s, char... delimiterChars) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.underscore(s, delimiterChars));
	}

	public static String capitalize(String s) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.capitalize(s));
	}

	public static String humanize(String s, String... removableTokens) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.humanize(s, removableTokens));
	}

	public static String titleCase(String s, String... removableTokens) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.titleCase(s, removableTokens));
	}

	public static String ordinalize(int number) {
		KInflector inflector = KInflector.getInstance();
		return (inflector.ordinalize(number));
	}

	public static String chop(String s) {
		if (s == null) {
			return (null);
		}

		if (s.length() == 1) {
			return (new String());
		}

		s = s.substring(0, (s.length() - 1));
		return (s);
	}

	
	// FIXME
	public static String encodeBase64(String s) {
		return null;
	}

	// FIXME
	public static String decodeBase64(String s) {
		return null;
	}

	public static String abbreviate(String s, int length) {
		if (s == null)
			return null;
		if (s.length() <= length)
			return s;
		s = s.substring(0, length - 5);
		if (s.endsWith(" ")) {
			s += "...";
		} else {
			s += " ...";
		}
		return s;
	}

	/*
	 * converts to -------- --------------------------- & &amp; < &lt; > &gt;
	 * \r,\n,\r\n1space <BR>&nbsp; \r,\n,\r\n <BR> \t &nbsp;&nbsp;&nbsp;&nbsp; 2
	 * spaces 1space
	 */
	/*
	public static String toHtml(String s) {
		if (s == null)
			return "";

		// first replace all &
		String html = s.replaceAll("&", "&amp;");
		html = html.replaceAll("<", "&lt;");
		html = html.replaceAll(">", "&gt;");

		// match any 2 spaces
		html = html.replaceAll("\u0020{2}", "&nbsp;&nbsp;");

		html = html.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		html = html.replaceAll("\r ", "<br/>&nbsp;");
		html = html.replaceAll("\n ", "<br/>&nbsp;");
		html = html.replaceAll("\r\n ", "<br/>&nbsp;");

		html = html.replaceAll("\r", "<br/>");
		html = html.replaceAll("\n", "<br/>");
		html = html.replaceAll("\r\n", "<br/>");

		return html;
	}
	*/
	
	
	// http://stackoverflow.com/questions/5134959/convert-plain-text-to-html-text-in-java
	public static String toHtml(String s) {
	    if (s == null) {
	        return null;
	    }

	    StringBuilder builder = new StringBuilder();

	    boolean previousWasASpace = false;

	    for (char c : s.toCharArray()) {
	        if (c == ' ') {
	            if (previousWasASpace) {
	                builder.append("&nbsp;");
	                previousWasASpace = false;
	                continue;
	            }

	            previousWasASpace = true;
	        } else {
	            previousWasASpace = false;
	        }

	        switch (c) {
	            case '<':
	                builder.append("&lt;");
	                break;
	            case '>':
	                builder.append("&gt;");
	                break;
	            case '&':
	                builder.append("&amp;");
	                break;
	            case '"':
	                builder.append("&quot;");
	                break;
	            case '\n':
	                builder.append("<br>");
	                break;
	                // We need Tab support here, because we print StackTraces as HTML
	            case '\t':
	                builder.append("&nbsp; &nbsp; &nbsp;  &nbsp;");
	                break;
	            default:
	                builder.append(c);

	        }
	    }

	    String converted = builder.toString();

	    String str = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";

	    Pattern patt = Pattern.compile(str);

	    Matcher matcher = patt.matcher(converted);

	    converted = matcher.replaceAll("<a href=\"$1\">$1</a>");

	    return converted;
	}


	@SuppressWarnings("rawtypes")
	public static String toJson(List list) {
        if (list == null) return null;

        StringWriter sw = new StringWriter();

        try {
			mapper.writeValue(sw, list);
		} catch (Exception e) {
            logger.error(e);
            return null;
		}

        return sw.toString();
	}
    
    /*
	@SuppressWarnings("rawtypes")
	public static String toJson(Map map) {
        if (map == null) return null;
        StringWriter sw = new StringWriter();
        try {
			mapper.writeValue(sw, map);
		} catch (Exception e) {
            logger.error(e);
            return null;
		}
        return sw.toString();
	}
    */
    
	public static String toJson(Object o) {
        if (o == null) return null;
		logger.debug("toJson called for object: " + KClassUtil.toString(o));
        return KClassUtil.toJson(o);
	}
    

    /*
	public String toJson2 (Object message) {
		if (message == null) return null;
		try {
			return mapper.writeValueAsString(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;
		}
	}
    */

	public static Map<String,Object> toMap(String json) {
        if (json == null) return null;
        
		try {
            logger.debug("toMap called for json string: " + json);
            return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
            logger.error(e);
            return null;
		}
	}
    
	// NOTE: this converts all values to String
	// FIXME: parseComplexJSON needs logic to determine the correct type to return
	public static Map<String,Object> toMap2(String json) {
		if (json == null) return null;
		return parseComplexJSON(json); 
	}
    
	public static Object toObject(String json) {
        if (json == null) return null;
		try {
            logger.debug("toObject called for json string: " + json);
			return mapper.readValue(json, Object.class);
		} catch (Exception e) {
            logger.error(e);
            return null;
		}
	}
    
	@SuppressWarnings("rawtypes")
	public static List toList(String json) {
        if (json == null) return null;

		try {
            logger.debug("toList called for json string: " + json);
			return mapper.readValue(json, List.class);
		} catch (Exception e) {
            logger.error(e);
            return null;
		}
	}

    
    // Normally s.replaceAll(regex, repl) is good enough but when s
	// contains backslashes or dollar signs, the result can be unexpected.
	// This method first suppresses the special meaning of the those characters
	// then does the replace.
	public static String quotedReplaceAll(String str, String regex, String repl) {
        str = Matcher.quoteReplacement(str);
		String s = Pattern.compile(regex).matcher(str).replaceAll(repl);
        return s;
	}
    
	private static Map<String, Object> parseComplexJSON(String jsonstr) {
        Map<String, Object> respdata = new HashMap<String, Object>();
        
        JsonFactory jfac = new JsonFactory();
        
        try {
            JsonParser jParser = jfac.createParser(jsonstr);
            
            while (jParser.nextToken() != null) {
                if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
                    respdata.put("result", readJSONArray(jParser));
                } else if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    return readJSONObject(jParser);
                } else {
                    respdata.put(jParser.getCurrentName(), jParser.getText());
                }
            }
            jParser.close();
        } catch (Exception ex) {
            logger.error(ex);
        }
        return respdata;
    }
    private static Map<String, Object> readJSONObject(JsonParser jParser) {
        Map<String, Object> jsonobject = new HashMap<String, Object>();
        
        int jsoncounter = 1;
        
        if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
            try {
                while (jParser.nextToken() != JsonToken.END_OBJECT) {
                    if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                        Map<String, Object> subjsonobj = readJSONObject(jParser);
                        
                        if (jParser.getCurrentName() != null && !jParser.getCurrentName().trim().isEmpty()) {
                            jsonobject.put(jParser.getCurrentName(), subjsonobj);
                        } else {
                            jsonobject.put(jsoncounter + "", subjsonobj);
                            jsoncounter++;
                        }
                    } else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
                        List<Object> subjsonarray = readJSONArray(jParser);
                        
                        if (jParser.getCurrentName() != null && !jParser.getCurrentName().trim().isEmpty()) {
                            jsonobject.put(jParser.getCurrentName(), subjsonarray);
                        } else {
                            jsonobject.put(jsoncounter + "", subjsonarray);
                            jsoncounter++;
                        }
                    } else {
                        jsonobject.put(jParser.getCurrentName(), jParser.getText());
                    }
                }
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        
        return jsonobject;
    }
    
    private static List<Object> readJSONArray(JsonParser jParser) {
        List<Object> jsonarray = new ArrayList<Object>();
        if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
            try {
                while (jParser.nextToken() != JsonToken.END_ARRAY) {
                    if (jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                        Map<String, Object> subjsonobj = readJSONObject(jParser);
                        jsonarray.add(subjsonobj);
                    } else if (jParser.getCurrentToken() == JsonToken.START_ARRAY) {
                        List<Object> subjsonarray = readJSONArray(jParser);
                        jsonarray.add(subjsonarray);
                    } else {
                        jsonarray.add(jParser.getText());
                    }
                }
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        return jsonarray;
    }
}
