package com.sillysoft.tools;

//
//  StringTool.java
//  Lux
//
//  Created by Dustin Sacks on Sat May 22 2004.
//  Copyright (c) 2002-2011 Sillysoft Games. All rights reserved.
//

import java.util.StringTokenizer;
import java.util.List;
import java.io.*;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class StringTool 
{

/** Count the number of lines in the String. */
public static int countLines(String text)
	{
	if (text == null)
		return 0;

	int lines = 1;
	for (int i = 0; i < text.length(); i++)
		{
		i = text.indexOf('\n', i);
		if (i != -1)
			lines++;
		else
			break;
		}
	return lines;
	}

/** Takes a string and returns a string by removing any periods at the end (e.g. "Preferences..." returns "Preferences"). */
public static String stripTrailingDots(String inputString)
	{
	while (inputString.endsWith("."))
		inputString = inputString.substring(0, inputString.length()-1);

	return inputString;
	}

/** Takes a string and returns a string by removing any numbers at the end (e.g. "Preferences1245" returns "Preferences"). */
public static String stripTrailingNumbers(String inputString)
	{
	while (inputString.endsWith("0") || inputString.endsWith("1") || inputString.endsWith("2") || inputString.endsWith("3") || inputString.endsWith("4") || inputString.endsWith("5") || inputString.endsWith("6") || inputString.endsWith("7") || inputString.endsWith("8") || inputString.endsWith("9"))
		inputString = inputString.substring(0, inputString.length()-1);

	return inputString;
	}

/** Return the numbers at the end of a string (e.g. "Preferences1245" returns "1245"). */
public static int captureTrailingNumbers(String inputString)
	{
	String output = "";
	
	while (inputString.endsWith("0") || inputString.endsWith("1") || inputString.endsWith("2") || inputString.endsWith("3") || inputString.endsWith("4") || inputString.endsWith("5") || inputString.endsWith("6") || inputString.endsWith("7") || inputString.endsWith("8") || inputString.endsWith("9"))
		{
		output = inputString.charAt(inputString.length()-1) + output;
		inputString = inputString.substring(0, inputString.length()-1);
		}

	if ("".equals(output))
		throw new NumberFormatException("captureTrailingNumbers no trailing numbers found: "+inputString);
		
	return Integer.parseInt(output);
	}
	
	
/** Removes the last char from the string. */
public static String stripLastChar(String inputString)
	{
	return inputString.substring(0, inputString.length()-1);
	}

/** Removes the last word and its preceding space from the string. */
public static String stripLastWord(String inputString)
	{
	int spacePos = inputString.lastIndexOf(' ');
	if (spacePos == -1)
		return inputString;
	if (spacePos == 0)
		return "";
		
	return inputString.substring(0, spacePos);
	}

/** Removes the last period and everything after it. i.e. 192.168.1.101 becomes 192.168.1 or filename.ext becomes filename */
public static String stripLastDotWord(String inputString)
	{
	int dotPos = inputString.lastIndexOf('.');
	if (dotPos == -1)
		return inputString;
	if (dotPos == 0)
		return "";
		
	return inputString.substring(0, dotPos);
	}
	
/** Strip the '.gz' suffix off of a string (if it is present). */
public static String stripGzip(String inputString)
	{
	if (inputString.endsWith(".gz"))
		return inputString.substring(0, inputString.length()-3);
	else
		return inputString;
	}

/** Takes a string and returns a string by removing all the spaces (e.g. "Play a Game" returns "PlayaGame"). */
public static String stripSpaces(String inputString)
	{
	if (inputString.indexOf(' ') != -1)
		{
		// it has spaces
		StringBuffer sb = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(inputString, " ");
		while (tok.hasMoreTokens())
			{
			sb.append(tok.nextToken());
			}
		return sb.toString();
		}
	else
		{
		return inputString;
		}
	}

/** Return the directory portion of the path. */
public static String getDirectoryFromPath(String path)
	{
	return path.substring(0, path.lastIndexOf(File.separator));
	}

/** return the filename portion of the path. */
public static String getFilenameFromPath(String path)
	{
	// Do 2 tests to test for the file seperators for all platforms
	path = path.substring(path.lastIndexOf('/')+1);
	path = path.substring(path.lastIndexOf('\\')+1);
	return path;
	}

/** Turn all spaces into the HTML encoded %20. */
public static String encodeSpaces(String s)
	{
	return s.replaceAll(" ", "%20");
	}


/** Add line breaks at word boundaries to get not more than 'lineSize' characters per line. */
public static String wrapLines(Object message, int lineSize)
	{
	return wrapLines(message.toString(), lineSize);
	}

/** Add line breaks at word boundaries to get not more than 'lineSize' characters per line. */
public static String wrapLines(String message, int lineSize)
	{
	return wrapLines(message, lineSize, false);
	}

/** Add line breaks at word boundaries to get not more than 'lineSize' characters per line. The input must be plain text, the output can be plain text (the defaullt - wrapped with \n) or an HTML wrapped version (wrapped with <br> and between <html></html>). The HTML ouput will be suitable for placing in a JLabel. */
public static String wrapLines(String message, int lineSize, boolean htmlResult)
	{
	try
		{
		StringBuffer result = new StringBuffer();
		if (htmlResult)
			result.append("<html>");
		String line;
		int pos = 0;
		boolean done = false;
		while (! done)
			{
			// Get the string up to the next linebreak
			int breakPos = message.indexOf('\n', pos);
			if (breakPos == -1) // we are on the last line
				{
				line = message.substring(pos);
				done = true;
				}
			else
				line = message.substring(pos, breakPos);
			pos += line.length()+1;

			// Go through the line adding words till we need a break
			int lineLengthSoFar = 0;
			StringTokenizer tok = new StringTokenizer(line, " ");
			while (tok.hasMoreTokens())
				{
				String word = tok.nextToken();
				if (lineLengthSoFar + word.length() > lineSize)
					{
					if (lineLengthSoFar != 0)
						result.append(htmlResult ? "<br>" : "\n");
					result.append(word);
					result.append(" ");
					lineLengthSoFar = word.length()+1;
					}
				else
					{
					result.append(word);
					result.append(" ");
					lineLengthSoFar += word.length()+1;
					}
				}
			if (! done)
				result.append(htmlResult ? "<br>" : "\n");
			}

		if (htmlResult)
			result.append("</html>");

		return result.toString();
		}
	catch (Throwable t)
		{
		return message;
		}
	}

/** Return the common starting chars of the 2 strings in a case in-sensitive manner. Example: getCommonStart("onTop", "onto me") will return "onTo" or "onto". If the strings have different starting characters then "" will be returned. **/
public static String getCommonStart(String s1, String s2)
	{
	int positionCount = 0;
	while (positionCount < s1.length() && positionCount < s2.length())
		{
		if (Character.toLowerCase(s1.charAt(positionCount)) == Character.toLowerCase(s2.charAt(positionCount)))
			positionCount++;
		else
			return s1.substring(0, positionCount);
		}

	return s1.substring(0, positionCount);
	}
	
public static String getCommonStart(List stringList)
	{
	if (stringList.size() == 0)
		return "";
	else if (stringList.size() == 1)
		return (String) stringList.get(0);
	else if (stringList.size() == 2)
		return getCommonStart((String) stringList.get(0), (String) stringList.get(1));
		
	// At least 3 strings gets us here:	
	String commonStart = getCommonStart((String) stringList.get(0), (String) stringList.get(1));
	for (int i = 2; i < stringList.size(); i++)
		{
		commonStart = getCommonStart(commonStart, (String) stringList.get(i));
		}
	return commonStart;
	}

	/*
public static void main(String[] args)
	{
	System.out.println(getCommonStart("oneFlew", "only"));
	System.out.println(getCommonStart("oneFlew", "oneflew"));
	System.out.println(getCommonStart("oneFlew", "blah"));
	System.out.println(getCommonStart("oneFlew", "oh my!"));
	System.out.println(getCommonStart("onlyFlew", "only"));
	System.out.println(getCommonStart("only", "onlyFlew"));
	System.out.println(getCommonStart("longnameguy", "longnameguy23"));
	
	

SS.debug(Translator.translateTokens("foobar2 :hasjoined:"));
SS.debug(Translator.translateTokens("foobar2 :hasjoined: foobar2 :hasjoined:"));
SS.debug(Translator.translateTokens(":hasjoined: foobar2"));
SS.debug(Translator.translateTokens(":hasjoined: foobar2 :hasjoined:"));
SS.debug(Translator.translateTokens("here: now you :hasjoined: again!!!!"));

	
	System.out.println(toString(tokenize("this is some content", " ")));
	System.out.println(toString(tokenize("nospaces", " ")));
//	System.out.println(toString(tokenize("", " ")));
	System.out.println(toString(tokenize("this time we split on all the e's", "e")));
	}	
	*/
	
/** Take a String content and return the tokens created by divinding it by the given delimiter. Analygous to PHP's explode() function. */
public static String[] tokenize(String contents, String delim)
	{
	StringTokenizer tok = new StringTokenizer(contents, delim);
	int tokenCount = tok.countTokens();
	if (tokenCount == 0)
		{
		return null;
		}
	String[] result = new String[tokenCount];
	for (int i = 0; i < tokenCount; i++) 
		{
		result[i] = tok.nextToken();
		}
	return result;
	}
	

/** Return the input with all underscored replaced with spaces. */
static public String underscoreToSpace(String input)
	{
	int underscorePos = input.indexOf('_');
	if (underscorePos < 0)
		return input;
	
	return StringTool.underscoreToSpace(input.substring(0, underscorePos)+" "+input.substring(underscorePos+1));
	}
	
/** Return the serializable form of this object in a String. On errors return "".*/	
public static String serialize(Serializable obj)
	{
	try
		{
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
		oos.writeObject(obj);
		oos.close();		
		bytesOut.close();
		return bytesOut.toString("UTF-8");
		}
	catch (Exception e)
		{
		SS.debug(e+" while serializing an obj: "+obj);
		}
	return "";
	}
	
public static byte[] serializeToBytes(Serializable obj)
	{
	try
		{
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
		oos.writeObject(obj);
		oos.close();		
		bytesOut.close();
		return bytesOut.toByteArray();
		}
	catch (Exception e)
		{
		SS.debug(e+" while serializing an obj: "+obj);
		}
	return null;
	}

public static Object deserializeBytes(byte[] byteBuffer)
	{
	try
		{
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(byteBuffer);
		ObjectInputStream ois = new ObjectInputStream(bytesIn);
		Object obj = ois.readObject();
		ois.close();
		bytesIn.close();
		return obj;
		}
	catch (Exception e)
		{
		SS.debug(e+" while deserializing bytes: "+byteBuffer);
		}
	return null;
	}

public static Object deserialize(String objString)
	{
	try
		{
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(objString.getBytes("UTF-8"));
		ObjectInputStream ois = new ObjectInputStream(bytesIn);
		Object obj = ois.readObject();
		ois.close();
		bytesIn.close();
		return obj;
		}
	catch (Exception e)
		{
		SS.debug(e+" while deserializing a string: "+objString);
		}
	return null;
	}



static public boolean sameSubnetIP	(String ip1, String ip2)
	{
	if (ip1 == null || ip2 == null)
		return false;
	
	SS.debug("subnetCheck: "+ip1+" / "+ip2);
	if (StringTool.stripLastDotWord(ip1).equals(StringTool.stripLastDotWord(ip2)))
        return true;
        
    return (isLocalIP(ip1) && isLocalIP(ip2));
	}
    
static public boolean isLocalIP(String ip)
    {
    if (ip.equals("0") || ip.equals("127.0.0.1"))
        return true;
    if (ip.startsWith("10."))
        return true;
    if (ip.startsWith("172."))   // 172.16.0.0 - 172.31.255.255
        {
        try {
            int node2 = Integer.parseInt(ip.substring(4,6));
            String dot2 = ip.substring(6,7);
            if (node2 >= 16 && node2 <= 31 && dot2.equals("."))
                return true;
            } catch (Exception e) {}
        }
    if (ip.startsWith("192.168."))
        return true;
    
    return false;
    }
    
	
static public String fileToString(String path)
	{
	StringBuffer inputBuffer = new StringBuffer();
	try
		{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

		String temp;
		while ((temp = in.readLine()) != null )
			{
			inputBuffer.append(temp);
			inputBuffer.append("\n");
			}

		in.close();
		} 
	catch (Exception e) { e.printStackTrace(); }

	return inputBuffer.toString();
	}


static public String timeFormat(long millis)
	{
	return timeFormat(millis, 6);
	}
	
// Same, but enclose some values :inside: net trans code
static public String timeFormatNetTrans(long millis, int showSecondsUnderTheseMinutes)
    {
	int days = (int) millis/(60000*60*24);
	int hours = (int) (millis % (60000*60*24)) / (60000*60);
	int minutes = (int) (millis % (60000*60)) / 60000;
	int seconds = (int) (millis % (60000)) / 1000;
	
	String result = "";
	if (days > 0)
		{
		if (days == 1)
			result = days+" :day:, ";
		else
			result = days+" :days:, ";
		}
		
	if (hours > 0)
		{
		if (hours == 1)
			result = result + hours+" :hour:, ";
		else
			result = result + hours+" :hours:, ";
		}
	
	if (minutes > 0)
		{
		if (minutes == 1)
			result = result + minutes+" :minute:";
		else
			result = result + minutes+" :minutes:";
		}
	
	if (days == 0 && hours == 0 && minutes < showSecondsUnderTheseMinutes)
		{ // only show seconds for small times
		result = (minutes > 0 ? result + ", " : "") + seconds+" :seconds:";
		}
		
	return result;
	}
    
static public String timeFormat(long millis, int showSecondsUnderTheseMinutes)
	{
	int days = (int) millis/(60000*60*24);
	int hours = (int) (millis % (60000*60*24)) / (60000*60);
	int minutes = (int) (millis % (60000*60)) / 60000;
	int seconds = (int) (millis % (60000)) / 1000;
	
	String result = "";
	if (days > 0)
		{
		if (days == 1)
			result = days+" "+Translator.getString("day")+", ";
		else
			result = days+" "+Translator.getString("days")+", ";
		}
		
	if (hours > 0)
		{
		if (hours == 1)
			result = result + hours+" "+Translator.getString("hour")+", ";
		else
			result = result + hours+" "+Translator.getString("hours")+", ";
		}
	
	if (minutes > 0)
		{
		if (minutes == 1)
			result = result + minutes+" "+Translator.getString("minute");
		else
			result = result + minutes+" "+Translator.getString("minutes");
		}
	
	if (days == 0 && hours == 0 && minutes < showSecondsUnderTheseMinutes)
		{ // only show seconds for small times
		result = (minutes > 0 ? result + ", " : "") + seconds+" "+Translator.getString("seconds");
		}
		
	return result;
	}
	
	/*
	
	((now.getTime()-gameStart)/60000) + " "+Translator.getString("minutes")+
	
	
						if (boss.ops.turnTimerLength < 60*60)
						{
						float minutes = ((boss.ops.turnTimerLength)/60.0f);
						
						DecimalFormat format = new DecimalFormat("##0.#");
						String timeString = format.format(minutes);
						request.append(timeString);
						if ("1".equals(timeString))
							request.append(URLEncoder.encode(" minute"));
						else
							request.append(URLEncoder.encode(" minutes"));
						}
					else {
						float hours = ((boss.ops.turnTimerLength) / 3600.0f);
						
						DecimalFormat format = new DecimalFormat("##0.#");
						String timeString = format.format(hours);
						request.append(timeString);
						if ("1".equals(timeString))
							request.append(URLEncoder.encode(" hour"));
						else
							request.append(URLEncoder.encode(" hours"));
						}




	return "foo";
	}
	*/
	
/** Return as "s" except if the number is 1.	*/
public static String plural(int i)
	{
	if (i == 1)
		return "";
		
	return "s";
	}	
	
/* Replace & with &amp;		*/
public static String xmlSafe(String input)
	{
	if (input == null)
		return null;
		
	return input.replaceAll("&", "&amp;");
	}

/* Replace &amp; with &		*/
public static String xmlSafeReverse(String input)
	{
	if (input == null)
		return null;
		
	return input.replaceAll("&amp;", "&");
	}
	
							
public static int getNumerator(String fraction)
	{
	int pos = fraction.indexOf("/");
	if (pos == -1)
		return 0;	// no fraction found
	
	return Integer.parseInt( fraction.substring(0, pos).trim() );
	}

public static int getDenominator(String fraction)
	{
	int pos = fraction.indexOf("/");
	String afterSlash = fraction.substring(pos+1).trim();

	pos = afterSlash.indexOf(" ");
	if (pos != -1)
		afterSlash = afterSlash.substring(0, pos);
	
	return Integer.parseInt(afterSlash);
	
	}
	
/** Given lines:
A
B
C
D

returns:
AC
BD			*/
public static String alignSongSheet(String filePath)
	{
	String input = fileToString(filePath);
	StringBuffer sb = new StringBuffer();
	// break up into lines:
	StringTokenizer tok = new StringTokenizer(input, "\n");
	while (tok.hasMoreTokens())
		{
		String A = tok.nextToken();
		if (! tok.hasMoreTokens())
			{
			sb.append(A);
			break;
			}
		String B = tok.nextToken();
		if (! tok.hasMoreTokens())
			{
			sb.append(A);
			sb.append(B);
			break;
			}
		String C = tok.nextToken();
		if (! tok.hasMoreTokens())
			{
			sb.append(A);
			sb.append(B);
			sb.append(C);
			break;
			}
		String D = tok.nextToken();
		
		int startLength = Math.max(A.length(), B.length());
		sb.append(A);
		sb.append("  ");
		while (A.length() < startLength) {
			sb.append(" ");
			startLength--;
			}
		sb.append(C);
		sb.append("\n");
		sb.append(B);
		sb.append("  ");
		while (B.length() < startLength) {
			sb.append(" ");
			startLength--;
			}
		sb.append(D);
		sb.append("\n");
		}
		
	return sb.toString();
	}
}
