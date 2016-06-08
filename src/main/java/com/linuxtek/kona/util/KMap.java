/*
 * Copyright (C) 2014 LinuxTek, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class KMap<K extends Comparable<? super K>,V> extends LinkedHashMap<K,V>
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(KMap.class);

	private boolean convertNullToEmptyString = false;
	private boolean isSorted = false;

	public KMap() 
	{
		super();
	}

	public KMap(boolean convertNullToEmptyString)
	{
		this();
		this.convertNullToEmptyString = convertNullToEmptyString;
	}


	public KMap(int initialCapacity) 
	{
		super(initialCapacity);
	}

	public KMap(int initialCapacity, float loadFactor) 
	{
		super(initialCapacity, loadFactor);
	}

	public KMap(int initialCapacity, float loadFactor, boolean accessOrder) 
	{
		super(initialCapacity, loadFactor, accessOrder);
	}

	public KMap(Map<K,V> m) 
	{
		super(m);
	}

	public static <K extends Comparable<? super K>,V> KMap<K,V> getInstance()
	{
		return (new KMap<K,V>());
	}

	public static <K extends Comparable<? super K>,V> KMap<K,V> getInstance(Map<K,V> m)
	{
		return (new KMap<K,V>(m));
	}

	public boolean isSorted()
	{
		return (isSorted);
	}

	public V get(K key, V defaultValue)
	{
		V value = super.get(key);
        if (value == null) {
        	value = defaultValue;
        }
		return value;
	}

	public String getString(K key)
	{
		V value = get(key);
		return (value.toString());
	}


	public K getKey(V value)
	{
		if (!containsValue(value))
			return (null);

		Iterator<K> it = keys();
		while (it.hasNext())
		{
			K key = it.next();
			if (value.equals(get(key)))
				return (key);
		}

		return (null);
	}

	public K getKey(V value, K defaultKey)
	{
		K key =  getKey(value);

		if (key == null)
            key = defaultKey;

		return key;
	}

	public Iterator<V> iterator()
	{
		return (values().iterator());
	}

	public Iterator<K> keys() {
		return (keySet().iterator());
	}

	// returns keys sorted by "natural ordering"
	public Iterator<K> sortedKeys()
	{
		List<K> list = new ArrayList<K>(keySet());
		Collections.sort(list);
		return (list.iterator());
	}

	// returns keys sorted by using the comparator
	public Iterator<K> sortedKeys(Comparator<K> c)
	{
		List<K> list = new ArrayList<K>(keySet());
		Collections.sort(list, c);
		return (list.iterator());
	}
    
	// returns keys sorted by values
    // assumes 1:1
	public Iterator<K> keysSortedByValue(Comparator<V> c)
	{
		List<V> list = new ArrayList<V>(values());
		List<K> keys = new ArrayList<K>(list.size());
		Collections.sort(list, c);
        for (V v : list) {
        	K key = getKeyByValue(this, v);
            keys.add(key);
        }
        
		return keys.iterator();
	}
    
	public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
	    for (Map.Entry<K, V> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
    
	public static <K, V> Set<K> getKeysByValue(Map<K, V> map, V value) {
	    Set<K> keys = new HashSet<K>();
	    for (Map.Entry<K, V> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            keys.add(entry.getKey());
	        }
	    }
	    return keys;
	}

	public KMap<K,V> sort()
	{
		KMap<K,V> map = new KMap<K,V>();

		Iterator<K> it = sortedKeys();
		while (it.hasNext())
		{
			K key = it.next();
			V value = get(key);
			map.put(key, value);
		}

		map.isSorted = true;
		return (map);
	}

	public KMap<K,V> sort(Comparator<K> c)
	{
		KMap<K,V> map = new KMap<K,V>();

		Iterator<K> it = sortedKeys(c);
		while (it.hasNext())
		{
			K key = it.next();
			V value = get(key);
			map.put(key, value);
		}

		map.isSorted = true;
		return (map);
	}

	// returns keys sorted by "natural ordering"
	public KMap<K,V> reverse()
   {
		KMap<K,V> map = new KMap<K,V>();

		List<K> list = new ArrayList<K>(keySet());
		Collections.reverse(list);

		Iterator<K> it = list.iterator();
		while (it.hasNext())
		{
			K key = it.next();
			V value = get(key);
			map.put(key, value);
		}

      return (map);
   }

	public String toCommaList()
	{
		return (toCommaList(this.values()));
	}

	public static <V> String toCommaList(Collection<V> items)
	{
		if (items == null || items.size() == 0)
			return (null);

		String itemList = "";

		Iterator<V> it = items.iterator();
		while (it.hasNext()) {
			String item = it.next().toString();
			itemList += item + ",";
		}

		itemList = itemList.substring(0, itemList.length()-1);
		return (itemList);
	}

	public String toString()
	{
		String s = "---- Begin KMap Listing ----\n";
		Iterator<K> it = sortedKeys();
		while (it.hasNext())
		{
			K key = it.next();
			V value = get(key);	
			s += "[" + key + "," + value + "]\n";
		}
		s += "---- End KMap Listing ----\n";

		return (s);
	}
}
