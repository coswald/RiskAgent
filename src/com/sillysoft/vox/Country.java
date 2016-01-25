package com.sillysoft.vox;

import com.sillysoft.tools.*;
import java.util.*;
import java.awt.Rectangle;
import java.awt.Point;
import org.xml.sax.Attributes;

/** Data structure for a Country in the game. */
public class Country implements XMLSerializable, IDable
{

public String toStringXML()
	{
	return null;
	}	
	
public Country()
	{
	}

public Country(Attributes att)
	{
	}

public Rectangle getBounds()
	{
	return null;
	}

public int getContinentID()
	{
	return 0;
	}

public Point getCenter()
	{
	return null;
	}

public int getID()
	{
	return 0;
	}

	
public Player getOwner()
	{	
	return null;	
	}
	
public Team getTeam()
	{
	return null;
	}

/** Returns true if the country has 0 units in it. */
public boolean isEmpty()
	{
	return false;
	}
	
/** Returns the List of countries that can be reached from this one. */	
public List getAdjoiningList()
	{
	return null;
	}


public boolean contains(Point p)
	{
	return false;
	}
	

/** This will return the UnitStack that matches the type and player of the given one.
 (UGs can be created and destroyed during moves and stuff.) */
public UnitStack getUnitStackMatching(UnitStack matcher)
	{
	return null;
	}

		
	
public UnitStackGroup getUnitStackGroup()
	{
	return null;
	}
	

	
public String toString()
	{
	return "<Country ID: name>";
	}

public String toStringWithUnits()
	{
	return null;
	}
	
/** Has any Castle? */	
public boolean hasCastle()
	{
	return false;
	}

/** Has a Castle owned by the given player? */
public boolean hasCastle(Player owner)
	{
	return false;
	}
	
public String getName()
	{
	return null;
	}

/* The income bonus this country gives. */
public int getBonus()
	{
	return 0;
	}

/* The income bonus of this countries Continent devided by the continent's size. */
public float getContinentBonusPartial(VoxWorld world)
	{
	return 0.5f;
	}



public boolean equals(Object other)
	{
	return false;
	}
	
	
public Point getLabelLocation()
	{
	return null;
	}


}