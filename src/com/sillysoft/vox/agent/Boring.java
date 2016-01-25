package com.sillysoft.vox.agent;

import com.sillysoft.vox.unit.*;
import com.sillysoft.vox.*;
import com.sillysoft.tools.*;

//
//  Boring.java
//  Lux
//
//  Copyright (c) 2002-2011 Sillysoft Games. 
//	http://sillysoft.net
//	lux@sillysoft.net
//
//	This source code is licensed free for non-profit purposes. 
//	For other uses please contact lux@sillysoft.net
//


public class Boring implements VoxAgent
{

public Boring()
	{}

public int pickCountry()
	{
	return 42;
	}
	
public void declareMoves(Country[] countries)
	{}

public void setPrefs( int newID, VoxWorld theworld )
	{}

public String name()
	{
	return "Boring";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Boring is an AI that does nothing.";
	}

public String youWon()
	{ 
	return "This should never happen!";
	}

public String message( String message, Object data )
	{
	return null;
	}

}
