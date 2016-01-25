package com.sillysoft.vox;

import com.sillysoft.tools.*;
import com.sillysoft.vox.unit.*;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.*;
import org.xml.sax.Attributes;

//
//  Continent.java
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//


/** Data structure for a Continent in the game. */
public class Continent implements XMLSerializable
{
private int ID;
private String name;
private int bonus;				// the bonus given for owning this Continent

// Visual elements
private Point labelLocation;
private Color color;

public boolean flashing;


public String toStringXML()
	{
	return "<continent id=\""+ID+"\" name=\""+name+"\" bonus=\""+bonus+"\" color=\""+GraphicsTool.stringFromColor(color)+"\" labelLocation=\""+GraphicsTool.stringFromPoint(labelLocation)+"\" />";
	}		
			

protected Continent()
	{
	}

/** Used when reading in from a maploader. */
protected Continent(Attributes att)
	{
//	name = att.getValue("name");
	try
		{
		if (att.getValue("id") != null)
			this.ID = Integer.parseInt(att.getValue("id"));
		else
			this.ID = -1;	// must get set later
		}
	catch (Exception e)
		{
		e.printStackTrace();
		throw new RuntimeException("Continent ID '"+att.getValue("id")+"' or bonus value is not an integer.");
		}
	}

/** Used when reading off the network. */
protected Continent(String XML)
	{
	try
		{
		this.ID = Integer.parseInt(XMLTool.extractAttribute("id", XML));		
		this.bonus = Integer.parseInt(XMLTool.extractAttribute("bonus", XML));
		
		this.name = XMLTool.extractAttribute("name", XML);
		this.labelLocation = GraphicsTool.pointFromString(XMLTool.extractAttribute("labelLocation", XML));
		this.color = GraphicsTool.colorFromString(XMLTool.extractAttribute("color", XML));
		
		if (color == null)
			SS.debug("null color cont");
		} 
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}

protected void setID(int ID)
	{
	this.ID = ID;
	}
	
protected void setID(String ID)
	{
	try
		{
		this.ID = Integer.parseInt(ID);
		}
	catch (Exception e)
		{
		throw new RuntimeException(this+" Bad continent ID '"+ID+"' is not an integer.");
		}
	}

protected void setLabelLocation(String value)
	{
	this.labelLocation = GraphicsTool.pointFromString(value);
	}
		
protected void setColor(String value)
	{
	this.color = GraphicsTool.colorFromString(value);
	}

protected void setBonus(String b)
	{
	try
		{
		bonus = Integer.parseInt(b);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
		
public int getID()
	{
	return ID;
	}

public Point getLabelLocation()
	{
	return labelLocation;
	}	
	
public String toString()
	{
	return "<Continent: "+name+" bonus:"+bonus+">";
	}
	
protected void setName(String name)
	{
	this.name = name;
	}

public String getName()
	{
	return name;
	}

public int getBonus()
	{
	return bonus;
	}

public Color getColor()
	{
	return color;
	}

protected Object clone()
	{
	try
		{
		Continent clone = new Continent();
		clone.ID = this.ID;
		clone.name = this.name;
		clone.bonus = this.bonus;
		clone.flashing = this.flashing;
/*
		clone.adjoiningCountries = new Vector();
		for (int i = 0; i < adjoiningCountries.size(); i++)
			{
			Country c = (Country)adjoiningCountries.get(i);
			clone.adjoiningCountries.add(c);
			}

		clone.voxPoints = this.voxPoints;
		clone.unitStacks = (UnitStackGroup) unitStacks.clone();
		clone.usedArea = this.usedArea;
		clone.owner = this.owner;
		clone.isWater = this.isWater;

		// Visual elements
		clone.shape = this.shape;
		*/
		return clone;
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	return null;
	}

public boolean equals(Object other)
	{
	if (other instanceof Continent)
		{
		Continent c = (Continent) other;
		return c.name.equals(name) && c.ID == ID;
		}
	return false;
	}
	
protected void setFlashing(boolean flash)
	{
	this.flashing = flash;
	}

public Team getTeamOwner(List countries)
	{
	Team ownerTeam = null;
	for (int i = 0; i < countries.size(); i++)
		{
		Country c = (Country) countries.get(i);
		if (c.getContinentID() == ID)
			{
			if (ownerTeam == null)
				ownerTeam = c.getTeam();
			else if (! ownerTeam.equals( c.getTeam()))
				{
				return null;
				}
			}
		}
	return ownerTeam;
	}


private Point labelLocationOriginal;
public void resize(double resizePercent)
	{
	if (labelLocationOriginal == null)
		{
		if (labelLocation == null)
			return;
		
		// save the original labelLocation:
		labelLocationOriginal = new Point(labelLocation.x, labelLocation.y);
		}
			
	labelLocation.setLocation(labelLocationOriginal.x*resizePercent, labelLocationOriginal.y*resizePercent);
	}

}