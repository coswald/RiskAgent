package com.sillysoft.vox;

import com.sillysoft.vox.agent.*;
import com.sillysoft.tools.*;
import java.util.*;

//
//  VoxWorld.java
//
//	This is a stub file of VoxWorld, so you can compile against it.
//

/** VoxWorld is where agents send their commands to. It also has some convenience methods. */
public class VoxWorld
{

	
public List getCountries()
	{
	return null;
	}
	
/** Take these units away from the group it was in and add it to a command storing the move.
Return true if there are more units left in the group, false otherwise.	*/
public boolean moveUnit(UnitStack ug, Country from, Country to, int numberOfUnits)
	{
	return false;
	}
	
public boolean placeUnits(UnitStack us, Country c)
	{	
	return false;
	}
	
public Country[] getCountriesArray()
	{
	return null;
	}
	
public Team getTeam(int playerID)
	{
	return null;
	}

public int getNumberOfPlayers()
	{ return 0; }

public Player getPlayer(int playerID)
	{
	return null;
	}
	
public int getPlayerMoney(Player player)
	{
	return 0;
	}

public int getPlayerMoney(int playerID)
	{
	return 0;
	}

public boolean unitCanReach(UnitStack units, Country from, Country to)
	{
	return false;
	}
	
public Country countryWithID(int ID)
	{
	return null;
	}
	
public List getCastleCountriesOwnedBy(int ID)
	{
	return null;
	}
	
public List getBuyableUnitsForPlayer(Player player)	
	{
	return null;
	}


/** Return the USG containing all the units that will defend this Country on this turn. */	
public UnitStackGroup getDefendersUnitStackGroup(Country c)
	{
	return null;
	}
	
/** Return the UnitStackGroup containing all the units from Player attacking this Country. Your bot will only be able to see attacks he makes, so calling this with another Player will always return an empty list. */
public UnitStackGroup getAttackerUnitStackGroup(Country c, Player player)
	{
	return null;
	}

public int getPlayerID()
	{
	return 0;
	}
	

public int getContinentSize(int continentID)
	{
	return 0;
	}		

public Continent getContinent(int continentID)
	{
	return null;
	}
	
public int getPlayerIncome(int playerID)	
	{
	return 0;
	}

public int getPlayerLandCount(int playerID)	
	{
	return 0;
	}	
	
public int getPlayerArmyCount(int playerID)	
	{
	return 0;
	}		
	
public void sendChat(String chat)
	{
	return;
	}
}