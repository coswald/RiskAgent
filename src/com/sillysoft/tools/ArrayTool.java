package com.sillysoft.tools;

import java.util.List;
import java.util.Random;
import java.util.Vector;

//
//  ArrayTool.java
//  Lux
//
//  Created by Dustin Sacks on Tue Jun 15 2004.
//  Copyright (c) 2002-2011 Sillysoft Games. All rights reserved.
//

public class ArrayTool
{

/** Return the index of the given key inside the array or -1 if the array does not contain the key. */
public static int arrayIndex(String[] array, String key)
	{
	for (int i = 0; i < array.length; i++)
		if (array[i].equals(key))
			return i;

	return -1;
	}

/** Does this array contain the given key? */
public static boolean arrayContains(String[] array, String key)
	{
	for (int i = 0; i < array.length; i++)
		if (array[i].equals(key))
			return true;

	return false;
	}

/** Does this array contain the given key? */
public static boolean arrayContains(int[] array, int key)
	{
	for (int i = 0; i < array.length; i++)
		if (array[i] == key)
			return true;

	return false;
	}

public static String toString(List v)
	{
	StringBuffer sb = new StringBuffer("<List size:");
	sb.append(v.size());
	sb.append("\n\t");
    for(int i = 0; i < v.size(); i++)
		{
		sb.append("\n\t");
		sb.append(v.get(i));
		}
	sb.append(" >");
	
	return sb.toString();
	}
	

public static String toString(String[] strArray)
	{
	StringBuffer sb = new StringBuffer("<String array size:");
	sb.append(strArray.length);
    for(int i = 0; i < strArray.length; i++)
		{
		sb.append("\n\t");
		sb.append(strArray[i]);
		}
	sb.append(" >");
	
	return sb.toString();
	}	


/** Shuffle the order of the elements in the List. Return the same List instance with the shuffled order. */
public static Vector randomize(Vector v)
    {
	Random random = new Random();
	return randomize(v, random);
    }
	
/** Shuffle the order of the elements in the List. Return the same List instance with the shuffled order. */	
public static Vector randomize(Vector v, Random random)
    {
    for(int i = 0; i < v.size() - 1; i++)
        {
        int fromIndex = (int)(random.nextFloat() * (float)(v.size() - i)) + i;
		if(fromIndex != i)
            {
			Object tempObject = v.get(i);
			v.setElementAt(v.get(fromIndex), i);
			v.setElementAt(tempObject, fromIndex);
            }
        }

	return v;
    }

public static Vector removeDuplicates(Vector v)
	{
    for(int i = 0; i < v.size(); i++)
		{
		Object item = v.get(i);
		for(int j = i+1; j < v.size(); j++)
			{
			if (item.equals(v.get(j)))
				{
				v.remove(j);
				j--;
				}
			}
		}
	return v;
	}
	
public static int sum(int[] array)
	{
	int result = 0;
	for (int i = 0; i < array.length; i++)
		result += array[i];
			
	return result;
	}

}
