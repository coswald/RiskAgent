package com.sillysoft.tools;

//
//  SS.java
//  Lux
//
//  This class contains methods of use to all Sillysoft applications.
//
//  Copyright (c) 2002-2011 Sillysoft Games. All rights reserved.
//

import java.util.Random;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;


public class SS 
{

public static final boolean isMacintosh = System.getProperty("mrj.version") != null || System.getProperty("os.name").toLowerCase().indexOf("mac os x") != -1;
public static final boolean isMacintosh104 = isMacintosh && System.getProperty("os.version").indexOf("10.4") != -1;
public static final boolean isMacintosh105 = isMacintosh && System.getProperty("os.version").indexOf("10.5") != -1;
public static final boolean isMacintosh106 = isMacintosh && System.getProperty("os.version").indexOf("10.6") != -1;
public static final boolean isWindows = (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1);
public static final boolean isXP =      (System.getProperty("os.name").toLowerCase().indexOf("windows xp") != -1);
public static final boolean isVista =   (System.getProperty("os.name").toLowerCase().indexOf("vista") != -1) ||
										(System.getProperty("os.name").toLowerCase().indexOf("windows 7") != -1) ||
										(System.getProperty("os.name").toLowerCase().indexOf("windows 8") != -1) ||
										(System.getProperty("os.name").toLowerCase().indexOf("windows 10") != -1);


public static final Random rand = new Random();

public static int showDebugLevel = 10;	// 0 for no debugging, 10 for everything

public static void beep()
	{
	Toolkit.getDefaultToolkit().beep();
	}

/** 0 for no debugging, 10 for everything. */
public static void setShowDebugLevel(int level)
	{
    showDebugLevel = level;
	}

/** Print debug test with importance level */
public static void debug(Object text, int level)
	{
	if (showDebugLevel >= level)
		debug(text);
	}
    
public static void debug(Object text)
	{
	System.out.println(text);
	System.out.flush();
	}
	
final static public String monthNames[] = 
	{
	"January", 
	"February", 
	"March", 
	"April",
	"May", 
	"June", 
	"July", 
	"August",
	"September", 
	"October",
	"Novemeber", 
	"December"	};
	
/** Return the stack trace of the Throwable object in a String. */	
public static String getStackTrace(Throwable t)
	{
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw, true);
	t.printStackTrace(pw);
	pw.flush();
	sw.flush();
	return sw.toString();
	}	

}
