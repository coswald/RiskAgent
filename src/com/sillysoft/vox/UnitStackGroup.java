package com.sillysoft.vox;

//
//  UnitStackGroup.java
//
//  Created by Dustin Sacks on 1/2/05.
//  Copyright 2005 Sillysoft Games. All rights reserved.
//

import java.util.*;
import com.sillysoft.tools.*;

/** UnitStackGroup is a group of UnitStacks, each of which may have different owners or types. */
public class UnitStackGroup
{

private List list;

public UnitStackGroup()
	{
	list = new Vector();
	}
	
public UnitStackGroup(UnitStack ug)
	{
	list = new Vector();
	list.add(ug);
	}
	
public UnitStackGroup(List list)
	{
	this.list = list;
	}	
	
public UnitStackGroup(String XML, CountriesManager master)
	{
	list = new ArrayList();
	String item = XMLTool.extract("vunits", XML);
	for (int h = 1; item != null; h++)
		{
		XMLSerializable object = new UnitStack(item, master);
		list.add(object);
		item = XMLTool.extract("vunits", XML, h);
		}
	}
	
	

	 
public UnitStack get(int i)
	{
	if (i > list.size()) {
		SS.debug("UnitStackGroup.get with an index too high. USG contains: "+this);
		}
	return (UnitStack) list.get(i);
	}
	
public UnitStack remove(int i)
	{
	if (list.size() == 0)
		{
		SS.debug("UnitStackGroup.remove(int i) called while EMPTY - "+i);
		return null;
		}
		
	return (UnitStack) list.remove(i);
	}
	
public boolean remove(UnitStack ug)
	{
	return list.remove(ug);
	}
	
public void removeEmpties()
	{
	for (int i = 0; i < size(); i++)
		if (get(i).getCount() == 0)
			{
			remove(i);
			i--;
			}
	}

public String toStringXML()
	{
	return XMLTool.getListXML(list, "vunits");
/*
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < size(); i++)
		{
		buf.apend(get(i).toStringXML());
		}
		*/
	}	
	
// Merge any UnitStacks with the same unit and owner
public void consolidateUnits()
	{
	for (int i = 0; i < size(); i++)
		{
		for (int j = 0; j < size(); j++)
			{
			if (i != j)
				{
				UnitStack us1 = get(i);
				UnitStack us2 = get(j);
				
				if ((us1.getOwner().equals(us2.getOwner())) && (us1.getUnit().getType() == us2.getUnit().getType()))
					{
					// merge them
					us1.setCount(us1.getCount() + us2.getCount());
					us1.addCarry(us2.getCarriedUnits());
					list.remove(us2);
					
					// start again in case there are more:
					consolidateUnits();
					return;
					}
				}		
			}
		}
	}

// Castles at the front, then knights, then pawns
public void orderUnits()
	{
	boolean changeMade = true;
	while(changeMade)
		{
		changeMade = false;
		for (int i = 0; i < size()-1; i++)
			{
			if (get(i).getUnit().getType() > get(i+1).getUnit().getType())
				{
				UnitStack move = get(i);
				remove(move);
				list.add(i+1, move);
				changeMade = true;
				}
			}
		}
	
	}
	
	
public int size()
	{
	return list.size();
	}
	
public int getTotalUnitCount()
	{
	int result = 0;
	for (int i = 0; i < list.size(); i++)
		{
		result += get(i).getCount();
		}
	return result;
	}
	
public boolean contains(UnitStack ug)
	{
	return list.contains(ug);
	}
	
/** If the UG is in this list then move it to the back and return true. Return false if the UG is not in this list. */	
public boolean pushToBack(UnitStack ug)
	{
	if (list.remove(ug))
		{
		list.add(ug);
		return true;
		}
	return false;
	}

/** If the UG is in this list then move it to the back and return true. Return false if the UG is not in this list. */	
public boolean pushToFront(UnitStack ug)
	{
	if (list.remove(ug))
		{
		list.add(0, ug);
		return true;
		}
	return false;
	}
		
// This method will add the given UnitStack to the list.
// The UnitStack added will NOT be a reference to the UG.
public void add(UnitStack ug)
	{
	if (ug == null || ug.getCount() == 0)
		return;
		
	// If we have a group with the same unit and owner then merge them
	// Maybe restrict sameness to air units and landing spots ??? XXXX
	for (int i = 0; i < list.size(); i++)
		{
		if (ug.equals(list.get(i)))	// problem here!! i think
			{
			UnitStack group = (UnitStack) list.get(i);
			group.setCount(group.getCount() + ug.getCount());
//			SS.debug("USG Added "+ug.getCount()+" units to "+group);
			
			group.addCarry(ug.getCarriedUnits());
			return;
			}
		}
		
	// OK, so the list does not have a matching unit group. add it
//	SS.debug("Added a new UnitStack to a list: "+ug);
	UnitStack ugnew = new UnitStack(ug.getUnit(), ug.getCount(), ug.getOriginalCountryID(), ug.getCarriedUnits(), ug.getCarriedUnitsAir(), ug.getCarriedUnitsMissiles());
	ugnew.setDrawPoint(ug.getDrawPoint());
	ugnew.setLandingCountry(ug.getLandingCountryID());
	list.add(ugnew);
	}

// Copy the contents of the given UGL into this one.
// The underlying UGs will be new ones, not references.
public void add(UnitStackGroup other)
	{
	if (other == null)
		return;
		
	for (int i = 0; i < other.size(); i++)
		{
		this.add(other.get(i));
		}	
	}

public Team getTeam()
	{
	return getDominantOwner().getTeam();
	}

// Return the player who owns the most units in this UGL	
public Player getDominantOwner()
	{
	// Build a map with a count of each owner
	Map countMap = new HashMap();
	for (int i = 0; i < size(); i++)
		{
		Player owner = get(i).getOwner();
		Integer count = (Integer) countMap.get(owner);
		if (count == null)
			{
			countMap.put(owner, new Integer(get(i).getCount()));
			}
		else
			{
			countMap.put(owner, new Integer(get(i).getCount()+count.intValue()));
			}
		}
		
	// Now go through the map and find the biggest one
	int biggestCount = 0;
	Player result = null;
	Iterator keys = countMap.keySet().iterator();
	while (keys.hasNext())
		{
		Object key = keys.next();
		if (((Integer)countMap.get(key)).intValue() > biggestCount)
			{
			biggestCount = ((Integer)countMap.get(key)).intValue();
			result = (Player) key;
			}
		}
		
	return result;
	}

public void setOriginalCountry(Country orig)
	{
	for (int i = 0; i < size(); i++)
		get(i).setOriginalCountry(orig);
	}

public boolean containsLoadedTransport()
	{
	for (int i = 0; i < size(); i++)
		if (get(i).getUnit().transportCapacity() > 0 && get(i).getCarriedUnitCount() > 0)
			return true;
	
	return false;
	}

// Return the total weight that units in this list take up. */	
public int getTransportWeight()
	{
	int weight = 0;
	for (int i = 0; i < size(); i++)
		weight += (get(i).getUnit().transportWeight() * get(i).getCount());
		
	return weight;
	}

// Remove the specified weight and return it in a new UGL
public UnitStackGroup takeWeight(int takeWeight)
	{
	UnitStackGroup result = new UnitStackGroup();
	for (int i = 0; i < size(); i++)
		{
		UnitStack group = get(i);
		// Take as many of this group as we can fit
		int takeUnits = takeWeight/(group.getUnit().transportWeight());
		takeUnits = Math.min(takeUnits, group.getCount());
		group.setCount(group.getCount() - takeUnits);
		result.add(new UnitStack(group.getUnit(), takeUnits, group.getOriginalCountryID()));
		takeWeight -= (group.getUnit().transportWeight() * takeUnits);
		if (takeWeight == 0)
			return result;
		}
	return result;
	}

// Roll the dice for this list and return the number of kills
public int calculateDiceRollKills(boolean attacking)
	{
	int result = 0;
	for (int i = 0; i < size(); i++)
		result += get(i).calculateDiceRollKills(attacking);
	return result;
	}

public void killUnits(int numberToKill)
	{
	if (numberToKill == 0)
		return;
		
	if (numberToKill > getTotalUnitCount())
		{
//		SS.debug("killing more units then we have XXXX");
//		Thread.dumpStack();
		
		numberToKill = getTotalUnitCount();
		if (numberToKill == 0)
			return;
		}
		
	// Find the least cost unit:
	int leastCost = 1000000;
	UnitStack leastCostGroup = null;
	for (int i = 0; i < size(); i++)
		{
		if (get(i).getUnit().getCost() < leastCost)
			{
			leastCost = get(i).getUnit().getCost();
			leastCostGroup = get(i);
			}
		}
		
	// Kill them:
	if (leastCostGroup == null)
		{
		SS.debug("leastCostGroup == null in "+this);
		return;
		}
		
	leastCostGroup.removeOne();
	remove(leastCostGroup);		// remove then re-add so the next killed unit will come from a different stack (if equal one exists)
	add(leastCostGroup);
	removeEmpties();
	
	if (numberToKill == 1)
		return;	// we just did it
	
	// otherwise we continue to kill more:
	killUnits(numberToKill-1);
	}

public String toString()
	{
	String result = "<UnitStackGroup: ";
	for (int i = 0; i < size(); i++)
		{
		result = result + get(i);
		}
	result = result +">";
	return result;
	}


/** Remove any units that expire after battles and return them. */
public UnitStackGroup removeExpireAfterAttackUnits()
	{
	UnitStackGroup result = new UnitStackGroup();
	for (int i = 0; i < size(); i++)
		{
		UnitStack us = get(i);
		if (us.getUnit().expiresAfterAttack())
			{
			result.add(remove(i));
			i--;
			}
		}
	return result;
	}
	
/** Remove any air units that cannot invade and return them. */
public UnitStackGroup removeNonLandableAirUnits()
	{
	UnitStackGroup result = new UnitStackGroup();
	for (int i = 0; i < size(); i++)
		{
		UnitStack us = get(i);
		if (us.getUnit().isAir())
			{
			result.add(remove(i));
			i--;
			}
		}
	return result;
	}

protected Object clone()
	{
	UnitStackGroup result = new UnitStackGroup();
	for (int i = 0; i < size(); i++)
		{
		UnitStack us = get(i);
		result.add((UnitStack)us.clone());
		}
	return result;
	}
	
public int getCost()
	{
	int result = 0;
	for (int i = 0; i < size(); i++)
		{
		result += (get(i).getUnit().getCost() * get(i).getCount());
		}
	return result;
	}
	
public int getNukeLevel()
	{
	int result = 0;
	for (int i = 0; i < size(); i++)
		{
		UnitStack us = get(i);
		result = Math.max(result, us.getUnit().getNukeLevel());
		}
	return result;
	}
	
public void nukeAtLevel(int percent)
	{
	for (int i = 0; i < size(); i++)
		{
		UnitStack us = get(i);
		int dead = (int) (us.getCount() * (percent/100.0f));
		us.setCount(us.getCount() - dead);
		}
	removeEmpties();
	}


public void setLandingCountry(Country orig)
	{
	for (int i = 0; i < size(); i++)
		{
		get(i).setLandingCountry(orig);
		}
	}

public boolean isOnlyAirplanes()
	{
	for (int i = 0; i < size(); i++)
		{
		if (! get(i).isAir() || get(i).expiresAfterAttack())
			return false;
		}
	return true;
	}

public boolean hasCastle()
	{
	for (int i = 0; i < size(); i++)
		{
		if (get(i).getUnit().isCastle())
			return true;
		}
	return false;
	}

public UnitStack getCastle()
	{
	for (int i = 0; i < size(); i++)
		{
		if (get(i).getUnit().isCastle())
			return get(i);
		}
	return null;
	}
	
}
