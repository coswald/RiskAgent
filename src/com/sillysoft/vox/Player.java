package com.sillysoft.vox;

import com.sillysoft.tools.*;
import java.awt.Color;
import org.xml.sax.Attributes;


//
//  Player.java
//
//  Created by Dustin Sacks on 12/28/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//


/** Represents a Player in the game. */
public class Player implements XMLSerializable, IDable
{

private int ID;
private Team team;
private Color color;
private String name, agentType;
protected String controlledBy, controlledByPrevious;
protected String controlledByKey, controlledByKeyPrevious;

//private int money = 20;

//public boolean human = false;

public final static Player WATER = new Player("Water", Team.WATER, Color.BLUE);

protected boolean agentOverride = false;
protected boolean controlledBySaved = false;

protected String startingMoney;	// only for saved games

/** Used when reading off the network. */
public Player(String XML)
	{
	try
		{
//		this.human = Boolean.parseBoolean(XMLTool.extractAttribute("human", XML));
		
		this.ID = Integer.parseInt(XMLTool.extractAttribute("id", XML));		
//		this.money = Integer.parseInt(XMLTool.extractAttribute("money", XML));
		
		this.name = XMLTool.extractAttribute("name", XML);
		this.agentType = XMLTool.extractAttribute("agentType", XML);
		this.controlledBy = XMLTool.extractAttribute("controlledBy", XML);
		if ("null".equals(controlledBy))
			this.controlledBy = null;	
		
		this.color = GraphicsTool.colorFromString(XMLTool.extractAttribute("color", XML));
		
		this.team = new Team(XMLTool.extractAttribute("teamName", XML), XMLTool.extractAttribute("teamSymbol", XML));
		} 
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}

// When exporting for the end-of-game rankings, we add some extra info:
static protected boolean exportingWithKeys = false;

public String toStringXML()
	{
	//		money=\""+money+"\"
	
	return "<player id=\""+ID+"\" name=\""+name+"\" color=\""+GraphicsTool.stringFromColor(color)+"\" agentType=\""+agentType+"\" "+
			(controlledBy != null ? "controlledBy=\""+controlledBy+"\" ": (exportingWithKeys && controlledByPrevious != null ? "controlledBy=\""+controlledByPrevious+"\" ":""))+
			(exportingWithKeys && controlledByKey != null ? "cKey=\""+controlledByKey+"\" ":(exportingWithKeys && controlledByKeyPrevious != null ? "cKey=\""+controlledByKeyPrevious+"\" ":""))+
			"teamName=\""+team.getName()+"\" teamSymbol=\""+team.getSymbol()+"\" />";	// human=\""+human+"\"
	}	


public Player(String name, Team team)
	{
	this(name, team, Color.BLACK);
	}
	
public Player(String name, Team team, Color color)
	{
	this.ID = -1;
		
	this.team = team;
	this.color = color;
	this.name = name;
	}
	
public Player(Attributes att, Team team)
	{
	try
		{
		this.ID = Integer.parseInt(att.getValue("id"));
		} 
	catch (Exception e)
		{
		throw new RuntimeException("Player ID is not an integer ("+att.getValue("id")+")");
		}
		
	this.team = team;
	this.color = GraphicsTool.colorFromString(att.getValue("color"));
	this.name = att.getValue("name");

	agentType = att.getValue("agent");
	if (agentType != null)
		{
		agentOverride = true;
		if ("boring".equals(agentType))
			agentType = "Boring";
		SS.debug("Player "+name+" has over-ride agent type of "+att.getValue("agent"), 1);
		}
		
	controlledBy = att.getValue("controlledBy");
	if (controlledBy != null)
		{
		controlledBySaved = true;
		SS.debug("Player "+name+" has over-ride controlledBy of "+controlledBy, 1);
		}	
		
	startingMoney = att.getValue("money");
	if (startingMoney != null)
		{
		SS.debug("Player "+name+" has starting money of "+startingMoney, 1);
		}		
	}
	
public Team getTeam()
	{
	return team;
	}

public String getName()
	{
	return name;
	}

public String name()
	{
	return name;
	}
	
public String getBrain()
	{
	if (isHuman()) {
		return controlledBy;
		}
	return agentType;
	}

public String getAgentType()
	{
	if (isHuman())
		return "Human";
		
	return agentType;
	}

public void setAgentType(String type)
	{
	if (! agentOverride || "Human".equals(type))
		{
		agentType = type;
		}
	else {
		SS.debug("Disregard setAgentType("+type+") from override", 1);
		}
	}

/** Return the name of the human controlling this player, or the name of the bot type controlling it. */
public String getControllerName()
	{
//	(SS.isMacintosh || SS.isVista ? "\u265F" : " ")
	
	if (controlledBy != null)
		return controlledBy;
		
	return agentType;		
	}

public boolean isHuman()
	{
	return "Human".equalsIgnoreCase(agentType) || controlledBy != null;
	}
	
/*		
public void setMoney(int money)
	{
	this.money = money;
	}
	
	
public int getMoney()
	{
	return money;
	}
*/
	
public Color getColor()
	{
	return color;
	}

public int getID()
	{
	return ID;
	}
	
public String toString()
	{
	return "<Player "+name+" :: "+team+">";
	}
	
public boolean equals(Object other)
	{
	if (other instanceof Player)
		{
		Player c = (Player) other;
		return c.name.equals(name) && c.ID == ID;
		}
	return false;
	}

protected void setControlledBy(String controller)
        {
	controlledBy = controller;
	}

protected void setControlledByKey(String controllerKey)
        {
	controlledByKey = controllerKey;
	}
}
