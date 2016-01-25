package com.sillysoft.vox;

//
//  CountryPathFinder.java
//
//  Created by Dustin Sacks on 1/8/05.
//  Copyright 2005 Sillysoft Games. All rights reserved.
//

import com.sillysoft.tools.*;
import java.util.*;

/** Utility class to calculate distances between a graph of Country objects. */
public class CountryPathFinder 
{


public static boolean canUnitReachLand(UnitStack us, Country from, Country to)
	{			
	// Do a breadth first search, remembering what we have seen so far.
	List seenCountries = new ArrayList();
	seenCountries.add(from);
	int distanceSoFar = 1;
	// The store of countries that we will look at their adjoining list
	List expandOn = new ArrayList();
	expandOn.add(from);
	List expandOnNextSet = new ArrayList();	// used to ease the distance calculations
	while (distanceSoFar <= us.getUnit().getMovement())
		{
		// When we are done with the current batch, increment the distance and look at the next bunch
		if (expandOn.size() == 0)
			{
			if (expandOnNextSet.size() == 0)
				return false;
			distanceSoFar++;
			expandOn = expandOnNextSet;
			expandOnNextSet = new ArrayList();
			}
		// Pull the next Country off the to-check list and run through its neighbors
		Country next = (Country) expandOn.remove(0);
		if (next.isEmpty() || next.getTeam().equals(us.getTeam()))
			{
			List adjoining = next.getAdjoiningList();
			for (int i = 0; i < adjoining.size(); i++)
				{
				Country check = (Country) adjoining.get(i);
				if (! seenCountries.contains(check))
					{
					if (check.equals(to))
						return distanceSoFar <= us.getUnit().getMovement();
					seenCountries.add(check);
					expandOnNextSet.add(check);
					}
				}
			}
		}
		
	return false;
	}
	




public static int distanceToEnemy(Country from)
	{			
	// Do a breadth first search, remembering what we have seen so far.
	List seenCountries = new ArrayList();
	seenCountries.add(from);
	int distanceSoFar = 0;
	// The store of countries that we will look at their adjoining list
	List expandOn = new ArrayList();
	expandOn.add(from);
	List expandOnNextSet = new ArrayList();	// used to ease the distance calculations
	while (true)
		{
		// Pull the next Country off the to-check list and run through its neighbors
		Country next = (Country) expandOn.remove(0);
		
		List adjoining = next.getAdjoiningList();
		for (int i = 0; i < adjoining.size(); i++)
			{
			Country check = (Country) adjoining.get(i);
			if (! seenCountries.contains(check))
				{
				if (! check.getTeam().equals(from.getTeam()))
					return distanceSoFar+1;
				seenCountries.add(check);
				expandOnNextSet.add(check);
				}
			}

		// When we are done with the current batch, increment the distance and look at the next bunch
		if (expandOn.size() == 0)
			{
			if (expandOnNextSet.size() == 0)
				{
				SS.debug("Failed to find any path for distanceToEnemy("+from);
				return 1000000;
				}
			distanceSoFar++;
			expandOn = expandOnNextSet;
			expandOnNextSet = new ArrayList();
			}		
		}
	}




public static Country getMostValuableEnemyBorder(Country from, int range, VoxWorld world)
	{			
	float choiceValue = -1000;
	Country choiceBorder = null;
	
	// Do a breadth first search, remembering what we have seen so far.
	List seenCountries = new ArrayList();
	seenCountries.add(from);
	int distanceSoFar = 1;
	// The store of countries that we will look at their adjoining list
	List expandOn = new ArrayList();
	expandOn.add(from);
	List expandOnNextSet = new ArrayList();	// used to ease the distance calculations
	while (true)
		{
		// Pull the next Country off the to-check list and run through its neighbors
		Country next = (Country) expandOn.remove(0);
		
		List adjoining = next.getAdjoiningList();
		for (int i = 0; i < adjoining.size(); i++)
			{
			Country check = (Country) adjoining.get(i);
			if (! seenCountries.contains(check))
				{
				seenCountries.add(check);
				
				if (! check.getTeam().equals(from.getTeam()))
					{
					if (check.getBonus() + check.getContinentBonusPartial(world) > choiceValue
					|| ((check.getBonus() + check.getContinentBonusPartial(world)) == choiceValue && SS.rand.nextInt(2) == 1))
						{	// ^ if it's equal as good, random chance change selection to it
						choiceValue = check.getBonus() + check.getContinentBonusPartial(world);
						choiceBorder = check;
						}	
					}

				if (check.isEmpty() || check.getTeam().equals(from.getTeam()))
					{
					expandOnNextSet.add(check);
					}				
				}
			}

		// When we are done with the current batch, increment the distance and look at the next bunch
		if (expandOn.size() == 0)
			{				
			if (expandOnNextSet.size() == 0)
				{
				SS.debug("Failed to find any path for distanceToEnemy("+from);
				return null;
				}
			distanceSoFar++;
			if (distanceSoFar > range)
				{
				return choiceBorder;
				}

			expandOn = expandOnNextSet;
			expandOnNextSet = new ArrayList();
			}		
		}
	}	
	/*
	
// Return a valuable Enemy neighboring Country within range (max range 2 supported). 
public Country getMostValuableEnemyBorder(Country c, int range)
	{
	float choiceValue = -1000;
	Country choiceBorder = null;
	
	List examine = new ArrayList();
	examine.addAll(c.getAdjoiningList());
	int range1size = examine.size();
	
	for (int i = 0; i < examine.size(); i++)
		{
		Country border = (Country) examine.get(i);
		if (! border.getTeam().equals(c.getTeam()))
			{ 
			// it's an enemy, a better choice?:
	//SS.debug(	"getMostValuableEnemyBorder of "+c+" in range "+range+" considering border "+	border);
			if (border.getBonus() + border.getContinentBonusPartial(world) > choiceValue
				|| ((border.getBonus() + border.getContinentBonusPartial(world)) == choiceValue && rand.nextInt(2) == 1))
				{	// ^ if it's equal as good, random chance change selection to it
				choiceValue = border.getBonus() + border.getContinentBonusPartial(world);
				choiceBorder = border;
				}			
			}
			
		if (range == 2 && i < range1size && 
				(border.getTeam().equals(c.getTeam()) || border.isEmpty()))
			{
			// add this country's borders to the examine list
			List nextBorders = border.getAdjoiningList();
			examine.addAll(nextBorders);
			}
		}
			
	SS.debug(	"getMostValuableEnemyBorder of "+c+" in range "+range+" = "+	choiceBorder);
	return choiceBorder;
	}
	*/



private Map distanceMapLand = new HashMap();	
private Map distanceMapAir = new HashMap();	

	
// Get the distance between these 2 countries, over land
public int distanceBetweenLand(Country from, Country to)
	{
	// Have we saved this distance check before?
	Integer savedDist = (Integer) distanceMapLand.get(from.getID()+"-"+to.getID());
	if (savedDist == null)
		{
		// No saved value. We must calculate it.
		savedDist = new Integer(calculateDistanceBetweenLand(from, to));
		distanceMapLand.put(from.getID()+"-"+to.getID(), savedDist);
		}
	
	return savedDist.intValue();
	}




	
	
private int calculateDistanceBetweenLand(Country from, Country to)
	{
	// Do a breadth first search, remembering what we have seen so far.
	List seenCountries = new ArrayList();
	seenCountries.add(from);
	int distanceSoFar = 1;
	// The store of countries that we will look at their adjoining list
	List expandOn = new ArrayList();
	expandOn.add(from);
	List expandOnNextSet = new ArrayList();	// used to ease the distance calculations
	while (true)
		{
		// When we are done with the current batch, increment the distance and look at the next bunch
		if (expandOn.size() == 0)
			{
			if (expandOnNextSet.size() == 0)
				return 1000000;
			distanceSoFar++;
			expandOn = expandOnNextSet;
			expandOnNextSet = new ArrayList();
			}
		// Pull the next Country off the to-check list and run through its neighbors
		Country next = (Country) expandOn.remove(0);
		List adjoining = next.getAdjoiningList();
		for (int i = 0; i < adjoining.size(); i++)
			{
			Country check = (Country) adjoining.get(i);
			if (! seenCountries.contains(check))
				{
				if (check.equals(to))
					return distanceSoFar;
				seenCountries.add(check);
				expandOnNextSet.add(check);
				}
			}
		}
	}

/*

public int distanceBetweenWater(Country from, Country to)
	{
	return calculateDistanceBetweenWater(from, to);
	}

// Get the distance between these 2 countries, through the air
public int distanceBetweenAir(Country from, Country to)
	{
	// Have we saved this distance check before?
	Integer savedDist = (Integer) distanceMapAir.get(from.getID()+"-"+to.getID());
	if (savedDist == null)
		{
		// No saved value. We must calculate it.
		savedDist = new Integer(calculateDistanceBetweenAir(from, to));
		distanceMapAir.put(from.getID()+"-"+to.getID(), savedDist);
		}
	
	return savedDist.intValue();
	}	
	
	
public static int calculateDistanceBetweenWater(Country from, Country to)
	{
	// Only consider Water Countries
	if (! from.isWater() || ! to.isWater())
		{
//		SS.debug("XXXXXX calculateDistanceBetweenWater got called on land countries");
		return 1000;
		}
		
	if (from.equals(to))
		return 0;
		
	// Do a breadth first search, remembering what we have seen so far.
	List seenCountries = new ArrayList();
	seenCountries.add(from);
	int distanceSoFar = 1;
	// The store of countries that we will look at their adjoining list
	List expandOn = new ArrayList();
	expandOn.add(from);
	List expandOnNextSet = new ArrayList();	// used to ease the distance calculations
	while (true)
		{
		// When we are done with the current batch, increment the distance and look at the next bunch
		if (expandOn.size() == 0)
			{
			if (expandOnNextSet.size() == 0)
				return 1000000;
			distanceSoFar++;
			expandOn = expandOnNextSet;
			expandOnNextSet = new ArrayList();
			}
		// Pull the next Country off the to-check list and run through its neighbors
		Country next = (Country) expandOn.remove(0);
		if (next.isWater())
			{
			List adjoining = next.getAdjoiningList();
			for (int i = 0; i < adjoining.size(); i++)
				{
				Country check = (Country) adjoining.get(i);
				if (! seenCountries.contains(check))
					{
					if (check.equals(to))
						return distanceSoFar;
					seenCountries.add(check);
					expandOnNextSet.add(check);
					}
				}
			}
		}
	}
			
private int calculateDistanceBetweenAir(Country from, Country to)
	{
	// Do a breadth first search, remembering what we have seen so far.
	List seenCountries = new ArrayList();
	seenCountries.add(from);
	int distanceSoFar = 1;
	// The store of countries that we will look at their adjoining list
	List expandOn = new ArrayList();
	expandOn.add(from);
	List expandOnNextSet = new ArrayList();	// used to ease the distance calculations
	while (true)
		{
		// When we are done with the current batch, increment the distance and look at the next bunch
		if (expandOn.size() == 0)
			{
			if (expandOnNextSet.size() == 0)
				return 1000000;
			distanceSoFar++;
			expandOn = expandOnNextSet;
			expandOnNextSet = new ArrayList();
			}
		// Pull the next Country off the to-check list and run through its neighbors
		Country next = (Country) expandOn.remove(0);
		List adjoining = next.getAdjoiningList();
		for (int i = 0; i < adjoining.size(); i++)
			{
			Country check = (Country) adjoining.get(i);
			if (! seenCountries.contains(check))
				{
				if (check.equals(to))
					return distanceSoFar;
				seenCountries.add(check);
				expandOnNextSet.add(check);
				}
			}		
		}
	}
	*/
}
