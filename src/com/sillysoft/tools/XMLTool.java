package com.sillysoft.tools;

import java.util.List;

//
//  XMLTool.java
//  Lux
//
//  Created by Dustin Sacks on Mon May 19 2003.
//  Copyright (c) 2002-2011 Sillysoft Games. All rights reserved.
//

public class XMLTool 
{

/** Extract the the value of the first <key> tag. Return null if the key is not found. */
public static String extract(String key, String contents)
	{
	return extract(key, contents, 0);
	}

/** Extract the the value inside the n'th <key> tag. Return null if the key is not found. */
public static String extract(String key, String contents, int number)
	{
	// count to the 'number'th place:
	int fromPlace = 0;
	for (int i = 0; i < number; i++)
		{
		fromPlace = contents.indexOf("<"+key+">", fromPlace)+1;
		}
	int start = contents.indexOf("<"+key+">", fromPlace);
	int end = contents.indexOf("</"+key+">", start);

	if (start == -1 || end == -1)
		return null;

	start += key.length()+2;
	return contents.substring(start, end);
	}

/** Count the number of times a <key> tag open and close pair appears in the contents. */
public static int count(String key, String contents)
	{
	int fromPlace = 0, count = 0;
	do
		{
		fromPlace = contents.indexOf("<"+key+">", fromPlace)+1;
		count++;
		}
	while (fromPlace != 0);	// the +1 will turn a -1 retun into a zero

	count--;	// becuase we do the loop once for sure, even if the tag doesn't exist;
	return count;
	}

/** Count the number of times a <key /> tag appears in the contents. */
public static int countSolo(String key, String contents)
	{
	int fromPlace = 0, count = 0;
	do
		{
		fromPlace = contents.indexOf("<"+key+" ", fromPlace)+1;
		count++;
		}
	while (fromPlace != 0);	// the +1 will turn a -1 retun into a zero

	count--;	// becuase we do the loop once for sure, even if the tag doesn't exist;
	return count;
	}

/** Extract the the value of the first <key> tag and parse it as an integer. Return 0 if the key is not found or badly formed. */
public static int extractInt(String key, String contents)
	{
	String stringValue = extract(key, contents, 0);

	if (stringValue == null)
		return 0;

	try
		{
		return Integer.parseInt(stringValue);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
		
	return 0;
	}
	
/** Pull out the value of an attribute in the XML content (should be a single node). 
EG get attribute from: <blah attribute="123" />	 */
public static String extractAttribute(String key, String contents)
	{
	if (contents == null)
		throw new NullPointerException("XMLTool.extractAttribute("+key+") called with a null contents");
		
	// count to the 'number'th place:
	int start = contents.indexOf(" "+key+"=\"");
	if (start == -1)
		return null;
	start +=key.length()+3;
	
	int end = contents.indexOf("\"", start);

	if (start == -1 || end == -1)
		return null;

	return contents.substring(start, end);
	}	

public static void main(String args)
	{
	String test = "<CardSequence code=\"3.55\" nextValue=\"6\" counter=\"-2\" peaked=\"false\">";
	SS.debug(XMLTool.extractAttribute("code", test));
	SS.debug(XMLTool.extractAttribute("nextValue", test));
	SS.debug(XMLTool.extractAttribute("counter", test));
	SS.debug(XMLTool.extractAttribute("peaked", test));
	}

/** Extract the the value inside the n'th <key foo="bar" /> tag. Return null if the key is not found. */
public static String extractContained(String key, String contents, int number)
	{
	// count to the 'number'th place:
	int fromPlace = 0;
	for (int i = 0; i < number; i++)
		{
		fromPlace = contents.indexOf("<"+key+" ", fromPlace)+1;
		}
	int start = contents.indexOf("<"+key+" ", fromPlace);
	int end = contents.indexOf("/>", start);

	if (start == -1 || end == -1)
		return null;

	return contents.substring(start, end+2);
	}

/** 
Sample input contents:
<team name="BBBBB">
	<player id="0" name="B" color="1/0/0" />
	<player id="1" name="A2" color="1/0.6/0" />
</team>
<team name="AAAAA">
	<player id="2" name="A" color="0.8/0.8/0.8" />
</team>
<team name="Human">
	<player id="3" name="Gui Player" color="0.2/0.2/0.8" />
</team>
<team name="CCCCC">
	<player id="4" name="C" color="0/1/0" />
	<player id="5" name="C" color="0/0.5/0" />
</team>

Sample output:
extractFull("team", contents, 0) = <team name="BBBBB">
	<player id="0" name="B" color="1/0/0" />
	<player id="1" name="A2" color="1/0.6/0" />
</team>
*/
public static String extractFull(String key, String contents, int number)
	{
	// count to the 'number'th place:
	int fromPlace = 0;
	for (int i = 0; i < number; i++)
		{
		fromPlace = contents.indexOf("<"+key+" ", fromPlace)+1;
		}
	int start = contents.indexOf("<"+key+" ", fromPlace);
	int end = contents.indexOf("</"+key+">", start);

	if (start == -1 || end == -1)
		return null;

	return contents.substring(start, end+3+key.length());
	}	
	

/** Returns the XML serialization of a list of XMLSerializable objects.	*/
public static String getListXML(List list, String itemName)
	{
	StringBuffer buf = new StringBuffer("<"+itemName+"list>");
	for (int i = 0; i < list.size(); i++)
		{
		XMLSerializable ob = (XMLSerializable) list.get(i);
		buf.append("<"+itemName+">");
		buf.append(ob.toStringXML());
		buf.append("</"+itemName+">");
		}
	buf.append("</"+itemName+"list>");
	return buf.toString();
	}
	
}
