/*
 * Copyright (C) 2011 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Collection of utilities to help work with classes.
 */

public class KClassUtil {
	private static Logger logger = Logger.getLogger(KClassUtil.class);

	// print out all (local) attributes of an object. inherited attributes
	// are not included.
	public static String toString(Object obj) {
		if (obj == null) {
			return (null);
		}

		Class<?> c = obj.getClass();
		return (toString(c, obj));
	}

	public static String toString(Class<?> c, Object obj) {
		if (c == null || obj == null) {
			return (null);
		}

        logger.debug("toString: class: " + c);
        
		StringBuffer s = new StringBuffer();

		// make sure obj is an instanceof c
		Class<?> c1 = obj.getClass();
		if (!c.isInstance(obj)) {
			logger.warn("Object not instance of Class: " + "object: " + c1
					+ "\n" + "class: " + c);
			return (null);
		}

		Field[] fields = c.getDeclaredFields();

		for (Field f : fields) {
			try {
				// setAccessible to true to allow reading of private members
				f.setAccessible(true);
				Object value = (f.get(obj));
				s.append(f.getName());
				s.append(": ");
				if (value != null)
					s.append(value.toString());
				else
					s.append("null");
				s.append("\n");
			} catch (Exception e) {
				logger.warn("Unable to access field: " + f.getName() + "\n"
						+ e.getMessage());
			}
		}

		return (s.toString());
	}



	public static String toJson(Object obj) {
        if (obj == null) return null;
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //mapper.enableDefaultTyping(); // preserve types when serializing
        
        // TODO: not sure if these two features are desirable
        // http://stackoverflow.com/questions/18096589/json-jsonmappingexception-while-try-to-deserialize-object-with-null-values
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        
        try {
			mapper.writeValue(sw, obj);
		} catch (IOException e) {
            logger.error(e);
            return null;
		}
        return sw.toString();
	}
    
	public static String serializeJson(Object obj) {
        return toJson(obj);
	}
    
	public static <T> T deserializeJson(String json, Class<T> clazz) {
        return fromJson(json, clazz);
	}
    
	public static <T> T deserializeJson(String json, TypeReference<T> typeRef) {
        return fromJson(json, typeRef);
	}
    
	public static <T> T fromJson(String json, Class<T> clazz) {
        logger.debug("fromJson: " + json);
        
		ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS , true);
        
        T t = null;
	     try {
	    	 t  = mapper.readValue(json, clazz);
			} catch (IOException e) {
	            logger.error(e);
	            return null;
			}
         return t;
	}
    
	public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        logger.debug("fromJson: " + json);
		ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        T t = null;
	     try {
	    	 t  = mapper.readValue(json, typeRef);
			} catch (IOException e) {
	            logger.error(e);
	            return null;
			}
         return t;
	}
    
	public static Map<String,Object> toMap(Object o) {
       return toMap(o, false, true); 
	}
    
	public static Map<String,Object> toMap(Object o, boolean includeClassName) {
        return toMap(o, includeClassName, true);
        
	}
    
	public static Map<String,Object> toMap(Object o, boolean includeClassName, boolean includePrivate) {
		Map<String,Object> map = new HashMap<String,Object>();
        
        if (includeClassName) {
        	map.put("class", getClassName(o, true));
        }
		Field[] fields = o.getClass().getDeclaredFields();
		for (Field f : fields) {
			try {
				// setAccessible to true to allow reading of private members
				f.setAccessible(includePrivate);
				Object value = (f.get(o));
                map.put(f.getName(), value);
			} catch (Exception e) {
				logger.warn("Unable to access field: " + f.getName() + "\n"
						+ e.getMessage());
			}
		}
        return map;
	}
    

    
	public static String getClassName(Object o, boolean autoboxPrimitives) {
        Class<? extends Object> clazz = o.getClass();
        String className = clazz.getName();
        if (clazz.isPrimitive() && autoboxPrimitives) {
            String name = className;
            switch (name) {
            case "void":
                className = Void.class.getName();
                break;
            case "byte":
                className = Byte.class.getName();
                break;
            case "char":
                className = Character.class.getName();
                break;
            case "int":
                className = Integer.class.getName();
                break;
            case "boolean":
                className = Boolean.class.getName();
                break;
            case "short":
                className = Short.class.getName();
                break;
            case "long":
                className = Long.class.getName();
                break;
            case "float":
                className = Float.class.getName();
                break;
            case "double":
                className = Double.class.getName();
                break;
            default:
                throw new IllegalArgumentException(
                		"Invalid primitive class: " + name);
            	
            }
            
        }
        return className; 
	}
    

	public static String toXML(String rootName, Object obj) {
		Class<?> c = obj.getClass();
		return (toXML(rootName, c, obj, false));
	}

	public static String toXML(String rootName, Class<?> c, Object obj,
			Boolean includeInheritedFields) {
		String s = null;

		Element root = new Element(rootName);
		Document doc = new Document(root);

		doc = toXML(doc, c, obj, includeInheritedFields);

		try {
			StringWriter sw = new StringWriter();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, sw);
			s = sw.toString();
		} catch (IOException e) {
			logger.error(e);
		}

		return (s);
	}

	/*
	 * private static String getSimpleName(Class c) { String s = c.getName();
	 * String[] p = s.split("\\.");
	 * 
	 * logger.debug("class string: " + s + "\n string parts: " + p +
	 * "\n parts length: " + p.length);
	 * 
	 * return (p[p.length - 1]); }
	 */

	public static Document toXML(Document doc, Object obj) {
		Class<?> c = obj.getClass();
		return (toXML(doc, c, obj, false));
	}

	public static Document toXML(Document doc, Object obj, Boolean fields) {
		Class<?> c = obj.getClass();
		return (toXML(doc, c, obj, fields));
	}

	private static void fetchFields(Map<String, String> map, Class<?> c,
			Object obj) {
		Field[] fields = c.getDeclaredFields();

		for (Field f : fields) {
			try {
				f.setAccessible(true);

				String name = f.getName();
				if (map.containsKey(name))
					continue;

				// setAccessible to true to allow reading of private members
				String s = null;
				Object value = (f.get(obj));
				if (value != null)
					s = value.toString();
				map.put(name, s);
			} catch (Exception e) {
				logger.warn("Unable to access field: " + f.getName() + "\n"
						+ e.getMessage());
			}
		}
	}

	public static Document toXML(Document doc, Class<?> c, Object obj,
			Boolean includeInheritedFields) {
		// make sure obj is an instanceof c
		Class<?> c1 = obj.getClass();
		if (!c.isInstance(obj)) {
			logger.warn("Object not instance of Class: " + "object: " + c1
					+ "\n" + "class: " + c);
			return (null);
		}

		Element root = doc.getRootElement();
		Element thisClass = new Element(c.getSimpleName());
		root.addContent(thisClass);

		Map<String, String> fieldMap = new HashMap<String, String>();

		fetchFields(fieldMap, c, obj);
		if (includeInheritedFields) {
			Class<?> superClass = c.getSuperclass();
			while (!superClass.equals(java.lang.Object.class)) {
				fetchFields(fieldMap, superClass, obj);
				superClass = superClass.getSuperclass();
			}
		}

		Set<String> keys = fieldMap.keySet();

		for (String key : keys) {
			Object value = fieldMap.get(key);
			Element e = new Element(key);

			if (value != null)
				e.addContent(value.toString());
			else
				e.addContent("null");

			thisClass.addContent(e);
		}

		return (doc);
	}

	@SuppressWarnings("rawtypes")
	public static Method getMethod(Class<?> clazz, String name, Class[] types,
			Object[] args) {
		Method method = null;

		// sanity check
		if (types != null && args != null && types.length != args.length) {
			throw new IllegalArgumentException(
					"Number of types do not match number of args");
		}

		// Method[] methods = clazz.getDeclaredMethods();
		Method[] methods = clazz.getMethods();

		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equalsIgnoreCase(name)) {
				Integer argCount = args.length;
				if (methods[i].getParameterTypes().length == argCount
						&& Modifier.isPublic(methods[i].getModifiers())) {

					// if types is null, return first public method found
					if (types == null) {
						logger.debug("no types: found method");
						method = methods[i];
						break;
					}

					// start off assuming a match
					boolean typesMatch = true;

					Class[] paramTypes = methods[i].getParameterTypes();
					for (int j = 0; j < paramTypes.length; j++) {
						if (!paramTypes[j].equals(types[j])) {
							typesMatch = false;
						}
					}

					if (typesMatch) {
						logger.debug("typesMatch: found method");
						method = methods[i];
						break;
					}
				}
			}
		}

		if (method == null) {
			logger.info("No method matched: " + name);
		}

		return (method);
	}

	public static Method getMethod(Class<?> clazz, String name, Object... args) {
		return (getMethod(clazz, name, null, args));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T valueOf(Class<T> clazz, String value) {
		T result = null;

		Class[] types = { String.class };
		Object[] args = { value };

		Class<?> c = clazz;
		logger.debug("clazz name: " + clazz.getName());
        
        // since java.lang.String does not have a valueOf(String) method
		// let's check for it and simply return the value.
		if (clazz.getName().equals("java.lang.String")) {
			result = (T) c.cast(value);
            logger.debug("valueOf String is value itself: " + result);
            return result;
		}
        
		if (clazz.getName().equals("java.util.Date")) {
			Long time = Long.valueOf(value);
			result = (T) new Date(time);
            logger.debug("valueOf Date value: " + result);
            return result;
		}
		
		if (clazz.getName().equals("java.util.List")) {
			logger.debug("valueOf: have list: " + value);
			List<Object> list = KStringUtil.toList(value);
			logger.debug("valueOf: converted to object: " + KClassUtil.toString(list));
			result = (T) list;
            return result;
		}

		// Normally this method should be called for Objects but it's
		// possible to pass in a primitive. When a primitive is passed
		// in, then the Object equivalent needs to be used instead.
		if (clazz.isPrimitive()) {
			String className = clazz.getName().toLowerCase();
			if (className.contains("boolean")) {
				logger.debug("valueOf(): forcing class to java.lang.Boolean");
				c = Boolean.class;
			} else if (className.contains("long")) {
				logger.debug("valueOf(): forcing class to java.lang.Long");
				c = Long.class;
			} else if (className.contains("int")) {
				logger.debug("valueOf(): forcing class to java.lang.Integer");
				c = Integer.class;
			} else if (className.contains("byte")) {
				logger.debug("valueOf(): forcing class to java.lang.Byte");
				c = Byte.class;
			} else if (className.contains("short")) {
				logger.debug("valueOf(): forcing class to java.lang.Short");
				c = Short.class;
			} else if (className.contains("float")) {
				logger.debug("valueOf(): forcing class to java.lang.Float");
				c = Float.class;
			} else if (className.contains("double")) {
				logger.debug("valueOf(): forcing class to java.lang.Double");
				c = Double.class;
			} else if (className.contains("char")) {
				logger.debug("valueOf(): forcing class to java.lang.Char");
				c = Character.class;
			}
		}

		Method m = getMethod(c, "valueOf", types, args);

		if (m == null) {
			logger.debug("valueOf not found for: " + clazz.getName());
			return (null);
		}

		logger.debug("found valueOf method for: " + clazz.getName());

		try {
			// result = clazz.cast(m.invoke(null, value));
			result = (T) c.cast(m.invoke(null, value));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return (result);
	}
    
	
	/** Read the object from Base64 string. */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeObject(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return (T) o;
	}

	/** Write the object to a Base64 string. */
	public static String serializeObject(Serializable o) throws IOException {
        if (o == null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
    
	/** Read the object from Base64 string. */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeObjectFromByteArray(byte [] data) throws IOException, ClassNotFoundException {
        if (data == null) return null;
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return (T) o;
	}

	/** Write the object to a Base64 string. */
	public static byte[] serializeObjectAsByteArray(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return baos.toByteArray();
	}

    public static List<Map<String,Object>> beanListToMapList(List<?> beanList) {
        if (beanList == null) return null;
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for (Object o : beanList) {
            list.add(beanToMap(o));
        }
        return list;
    }
    

	public static Map<String,Object> beanToMap(Object o) {
		try {
			Map<String, Object> objectAsMap = new HashMap<String, Object>();
			BeanInfo info = Introspector.getBeanInfo(o.getClass());
			//logger.debug("beanToMap: converting class: " + o.getClass().getName());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				Method reader = pd.getReadMethod();
				if (!pd.getName().equals("class") && reader != null) {
                    //logger.debug("beanToMap: fetching value for property: " + pd.getName());
					objectAsMap.put(pd.getName(),reader.invoke(o));
				}
			}
			return objectAsMap;
		} catch(Throwable e) {
			throw new IllegalStateException("Cannot convert object to Map<String,Object>");
		}
	}
}
