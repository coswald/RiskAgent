package com.sillysoft.vox;


//
//  Team.java
//
//  Created by Dustin Sacks on 12/28/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

import org.xml.sax.Attributes;

/** Represents a Team in the game. */
public class Team 
{

public final static Team WATER = new Team("Water");

private String teamName, symbol;

public Team(String name)
	{
	teamName = name;
	}
	
public Team(String name, String symbol2)
	{
	teamName = name;
	symbol = symbol2;
	}

public Team(Attributes att)
	{
	teamName = att.getValue("name");
	symbol = att.getValue("symbol");
	}
	
public String getName()
	{
	return teamName;
	}
	
public String getSymbol()
	{
	if (symbol == null)
		return "\u2251";
		
	return symbol;
	}
	
	
public String toString()
	{
	return teamName;
	}

public boolean equals(Object other)
	{
	if (other instanceof Team)
		{
		Team c = (Team) other;
		return c.teamName.equals(teamName);
		}
	return false;
	}

}
