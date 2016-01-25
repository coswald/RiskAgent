package com.sillysoft.tools;

//
//  SSHashtable.java
//  lux
//
//  Created by Dustin Sacks on 2/17/06.
//  Copyright (c) 2002-2011 Sillysoft Games. All rights reserved.
//

import java.util.*;

/** 
A hashtable that stores integers for it's values.
**/

public class IntegerHashtable extends HashMap
{

private HashMap backing = new HashMap();

public void putInt(Object key, int value)
	{
	backing.put(key, new Integer(value));
	}
	
	
public void increment(Object key)
	{
	putInt(key, getInt(key)+1);
	}
	
	
public int getInt(Object key)
	{
	Object o = backing.get(key);
	if (o == null)
		return 0;

	return ((Integer)o).intValue();
	}

public List getSortedKeys()
	{
	// sorted keys output  thanks to T. GUIRADO for the tip!
    Vector v = new Vector(backing.keySet());
    Collections.sort(v, 
		new Comparator() 
			{
			public int compare(Object obj, Object key) 
				{
				if (getInt(obj) == getInt(key))
					return 0;
					
				return getInt(obj) - getInt(key);
				}
			});
	return v;
	}
}
