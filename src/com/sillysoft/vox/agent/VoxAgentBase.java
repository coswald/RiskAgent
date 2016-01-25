package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import com.sillysoft.vox.command.*;
import com.sillysoft.tools.*;
import java.util.Random;
import java.util.List;
import java.util.*;

//
//  VoxAgentBase.java
//  Copyright (c) 2002-2011 Sillysoft Games. 
//	http://sillysoft.net
//	lux@sillysoft.net
//
//	This source code is licensed free for non-profit purposes. 
//	For other uses please contact lux@sillysoft.net
//


/**
VoxAgentBase is an abstract agent class containing utility methods.
Sub-class it to gain easy access to them.
*/

abstract public class VoxAgentBase implements VoxAgent
{
// This agent's ownerCode:
protected int ID;

// We store some refs the world and to the country array
protected VoxWorld world;
protected Country[] countries;

// It is useful to have a random number generator for a couple of things
protected Random rand = new Random();

public void buyUnits(int money)
	{
	List bases = world.getCastleCountriesOwnedBy(ID);
	if (bases.size() == 0)
		{ 
		//.... we don't own any castles! we should build one or take one over
		buyCastle();
		return;
		}	
		
	buyUnits22(money, (Country) bases.get(rand.nextInt(bases.size())));	
	}


/** Spend half the money on Knights, then max out pawns */
public void buyHalfKnights(int money, Country baseCountry)
	{
	Unit pawn = new UnitPawn(world.getPlayer(ID));
	Unit knight = new UnitKnight(world.getPlayer(ID));
	
	int numKnight = (money/knight.getCost())/2;
	world.placeUnits(new UnitStack(knight, numKnight), baseCountry);
	
	money -= (numKnight*knight.getCost());
	int numInf = money/pawn.getCost();
	world.placeUnits(new UnitStack(pawn, numInf), baseCountry);
	}

/** Buy 2 men, then 2 knights - as much as we can */
public void buyUnits22(int money, Country baseCountry)
	{
	Unit pawn = new UnitPawn(world.getPlayer(ID));
	Unit knight = new UnitKnight(world.getPlayer(ID));
	int numInf = 0, numKnight = 0;
	while (money >= pawn.getCost())
		{
		if (money >= pawn.getCost())
			{
			numInf++;
			money -= pawn.getCost();
			}
		if (money >= pawn.getCost())
			{
			numInf++;
			money -= pawn.getCost();
			}
		if (money >= knight.getCost())
			{
			numKnight++;
			money -= knight.getCost();
			}
		if (money >= knight.getCost())
			{
			numKnight++;
			money -= knight.getCost();
			}
		}
		
	world.placeUnits(new UnitStack(pawn, numInf), baseCountry);
	world.placeUnits(new UnitStack(knight, numKnight), baseCountry);
	}

/** Buy 1 men, then 1 knight - as much as we can */
public void buyUnitsAlternating(int money, Country baseCountry)
	{
	Unit pawn = new UnitPawn(world.getPlayer(ID));
	Unit knight = new UnitKnight(world.getPlayer(ID));
	int numInf = 0, numKnight = 0;
	while (money >= pawn.getCost())
		{
		if (money >= pawn.getCost())
			{
			numInf++;
			money -= pawn.getCost();
			}
		if (money >= knight.getCost())
			{
			numKnight++;
			money -= knight.getCost();
			}
		}
		
	world.placeUnits(new UnitStack(pawn, numInf), baseCountry);
	world.placeUnits(new UnitStack(knight, numKnight), baseCountry);
	}

public void buyKnights(int money, Country baseCountry)
	{
	Unit knight = new UnitKnight(world.getPlayer(ID));
	world.placeUnits(new UnitStack(knight, money/knight.getCost()), baseCountry);
	}	
	
public void buyPawns(int money, Country baseCountry)
	{
	Unit pawn = new UnitPawn(world.getPlayer(ID));
	world.placeUnits(new UnitStack(pawn, money/pawn.getCost()), baseCountry);
	}	
	
/** Try to build a castle. */	
public boolean buyCastle()
	{
	Unit castle = new UnitCastle(world.getPlayer(ID));
	if (world.getPlayerMoney(ID) >= castle.getCost())
		{
		Country country = getBestCastleBuildCountry();
		if (country != null)
			{
			world.placeUnits(new UnitStack(castle, 1), country);
			return true;
			}
		}
	return false;
	}

/** Return a country that we own that it would make sense to build a castle on. */
public Country getBestCastleBuildCountry()
	{
	int bestValue = 0;
	Country bestCountry = null;
	for (int i = 0; i < countries.length; i++)
		{
		Country c = countries[i];
		if (c.getOwner().getID() == ID && c.getUnitStackGroup().getTotalUnitCount() > 4)
			{
			int value = c.getUnitStackGroup().getCost();
			value += c.getBonus();
			
			if (value > bestValue || (value == bestValue && rand.nextInt(10) > 5))
				{
				bestValue = value;
				bestCountry = c;
				}
			}
		}

	return bestCountry;
	}
	
		
			
					
// do nothing
public void declareMoves(Country[] countries)
	{
	this.countries = countries;
	}
	
/** Return the base with the least defenders. */
public Country getWeakestBase()
	{
	int lowestDefence = 1000000;
	Country weakestBase = null;
	List bases = world.getCastleCountriesOwnedBy(ID);
	for (int i = 0; i < bases.size(); i++)
		{
		Country c = (Country) bases.get(i);
		int defence = c.getUnitStackGroup().calculateDiceRollKills(false);
		if (defence < lowestDefence)
			{
			lowestDefence = defence;
			weakestBase = c;
			}
		}

	return weakestBase;
	}

/** Return the base with the most defenders. */
public Country getStrongestBase()
	{
	int lowestDefence = -100;
	Country weakestBase = null;
	List bases = world.getCastleCountriesOwnedBy(ID);
	for (int i = 0; i < bases.size(); i++)
		{
		Country c = (Country) bases.get(i);
		int defence = c.getUnitStackGroup().calculateDiceRollKills(false);
		if (defence > lowestDefence)
			{
			lowestDefence = defence;
			weakestBase = c;
			}
		}

	return weakestBase;
	}	
	
/** Return the base closest to the front lines. */
public Country getFrontBase()
	{
	int lowestDistance = 1000000;
	Country weakestBase = null;
	List bases = world.getCastleCountriesOwnedBy(ID);
	for (int i = 0; i < bases.size(); i++)
		{
		Country c = (Country) bases.get(i);
		int defence = distanceToEnemy(c);
		if (defence < lowestDistance)
			{
			lowestDistance = defence;
			weakestBase = c;
			}
		}

	return weakestBase;
	}


/** Return a base close to the front lines that isn't in danger this turn. */
public Country getFrontBaseSafe()
	{
	int lowestDistance = 1000000;
	Country weakestBase = null;
	List bases = world.getCastleCountriesOwnedBy(ID);
	for (int i = 0; i < bases.size(); i++)
		{
		Country c = (Country) bases.get(i);
		int defence = distanceToEnemy(c);
		if (defence < lowestDistance)
			{
			if (! countryIsInDanger(c))
				{
				lowestDistance = defence;
				weakestBase = c;
				}
			}
		}

	return weakestBase;
	}

/** Is the country in danger this turn? WARNING: only looks at distance-1 enemies right now! WARNING: will not consider one-way connections into this country! */
public boolean countryIsInDanger(Country c)	
	{
	int defend = c.getUnitStackGroup().calculateDiceRollKills(false);
	int attack = 0;
	

	List examine = new ArrayList();
	examine.addAll(c.getAdjoiningList());
	
	
	for (int i = 0; i < examine.size(); i++)
		{
		Country border = (Country) examine.get(i);
		if (! border.getTeam().equals(c.getTeam()))
			{
			attack += border.getUnitStackGroup().calculateDiceRollKills(true);
			}
		}
	
	return attack > defend;
	}
	
		
	
	
	
/** Return a random neighboring Country. */
public Country getRandomBorder(Country c)
	{
	List adjoiningList = c.getAdjoiningList();
	return (Country) adjoiningList.get(rand.nextInt(adjoiningList.size()));
	}

/** Return a random neighboring Country within range (max range 2). */
public Country getRandomBorder(Country c, int range)
	{
	List adjoiningList = c.getAdjoiningList();
	List examine = new ArrayList();
	examine.addAll(c.getAdjoiningList());
	
	for (int i = 0; i < adjoiningList.size(); i++)
		{
		Country border = (Country) adjoiningList.get(i);
		if (border.getTeam().equals(world.getPlayer(ID).getTeam()) || border.isEmpty())
			examine.addAll(border.getAdjoiningList());
		}
	
	return (Country) examine.get(rand.nextInt(examine.size()));
	}


public List getEnemyBorders(Country c)
	{
	List adjoiningList = c.getAdjoiningList();
	List matching = new ArrayList();
	for (int i = 0; i < adjoiningList.size(); i++)
		{
		Country border = (Country) adjoiningList.get(i);
		if (! border.getTeam().equals(c.getTeam()))
			matching.add(border);
		}
	
	return matching;
	}
	
	
/** Return a random Enemy neighboring Country. */
public Country getRandomEnemyBorder(Country c)
	{
	List adjoiningList = c.getAdjoiningList();
	List matching = new ArrayList();
	for (int i = 0; i < adjoiningList.size(); i++)
		{
		Country border = (Country) adjoiningList.get(i);
		if (! border.getTeam().equals(c.getTeam()))
			matching.add(border);
		}
	
	if (matching.size() == 0)
		{
		return null;
		}
		
	return (Country) matching.get(rand.nextInt(matching.size()));
	}


/** Calculate the shortest distance to an enemy country. Probably bad performance on big maps. */
public int distanceToEnemy(Country c)
	{
	return CountryPathFinder.distanceToEnemy(c);
	}


/** Calculate the direction to go from here to get to the closest enemy country. Probably bad performance on big maps. */
public Country directionToEnemy(Country c)
	{
//	SS.debug("directionToEnemy from "+c+":");

	Country enemy = getRandomEnemyBorder(c);
	if (enemy != null)
		return enemy;		
		
	int lowestPath = 1000000;
	Country lowestDirection = null;
	
	List adjoiningList = c.getAdjoiningList();
	for (int i = 0; i < adjoiningList.size(); i++)
		{
		Country border = (Country) adjoiningList.get(i);
			
		if (! border.getTeam().equals(c.getTeam()))
			return border;
		
		int borderDistance = distanceToEnemy(border);
//		SS.debug("    considering "+border+" at distance "+borderDistance);
		if (borderDistance < lowestPath)
			{
			lowestPath = borderDistance;
			lowestDirection = border;
			}
		}

	return lowestDirection;
	}

/** Will move all units towards enemies. If there are multiple equi-distant paths, it will split up the stack into multiple groups, each going along one of those paths. */
public void moveTowardsEnemySplittingUp(UnitStack units, Country from)
	{
	int lowestDistance = 1000000;
	List lowestDistanceBorders = new ArrayList();
	List borders = from.getAdjoiningList();
	
	// Build a list of all the borders that are lowest distance to the enemy.
	for (int b = 0; b < borders.size(); b++)
		{
		Country check = (Country) borders.get(b);
		int distance = distanceToEnemy(check);
		if (distance < lowestDistance)
			{
			lowestDistance = distance;
			lowestDistanceBorders = new ArrayList();
			lowestDistanceBorders.add(check);
			}
		else if (distance == lowestDistance)
			{
			lowestDistanceBorders.add(check);
			}
		}
						
	// divide armies into all the closest borders.
	int numberToMove = units.getCount();						
	for (int m = 0; m < lowestDistanceBorders.size(); m++)
		{
		world.moveUnit(units, from, (Country) lowestDistanceBorders.get(m), (int) Math.ceil(1f*numberToMove/lowestDistanceBorders.size()));
		}
	}	
	
public float getIncome(Country c)
	{
	return c.getBonus() + c.getContinentBonusPartial(world);
	}

/** Warning: Counts range 2 enemy pawns as danger at same level.	*/
public float getDanger(Country c)
	{
	float danger = 0;
	
	List examine = new ArrayList();
	examine.addAll(c.getAdjoiningList());
	int range1size = examine.size();
	
	for (int i = 0; i < examine.size(); i++)
		{
		Country border = (Country) examine.get(i);
		if (! border.getTeam().equals(c.getTeam()))
			{ 
			// it's an enemy
			danger += border.getUnitStackGroup().calculateDiceRollKills(true);
			}
			
		if (i < range1size && 
				(! border.getTeam().equals(c.getTeam()) || border.isEmpty()))
			{
			// add this country's borders to the examine list
			List nextBorders = border.getAdjoiningList();
			examine.addAll(nextBorders);
			}
		}
			
	SS.debug(	"getDanger of "+c+" = "+danger, 2);
	return danger;
	}


/** Return a valuable Enemy neighboring Country within range (max range 2 supported). */
public Country getMostValuableEnemyBorder(Country c, int range)
	{
	return CountryPathFinder.getMostValuableEnemyBorder(c, range, world);
	}


// Save references to our world
public void setPrefs( int newID, VoxWorld theworld )
	{
	ID = newID;		// this is how we distinguish what countries we own
	world = theworld;
	}

public String name()
	{
	return "VoxAgentBase";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "VoxAgentBase is an abstract agent class you can subclass.";
	}


/** fix? for reported problem of "fake" countries appearing in adjoiningLists. */	
public List getRealAdjoiningList(Object country, Country[] countryArrayReal)
	{
	List fakeList = ((Country)country).getAdjoiningList();
	List realList = new ArrayList();
	for (int j = 0; j < fakeList.size(); j++)
		{
		realList.add(countryArrayReal[((Country)fakeList.get(j)).getID()]);
		}
	return realList;
	}
	


// Oh boy. If this method ever gets called it is because we have won the game.
// Send back something witty to tell the user.
public String youWon()
	{ 
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {"You should extend the youWon() method!!!",
	"You should extend the youWon() method!!!",
	"You should extend the youWon() method!!!" };

	return answers[ rand.nextInt(answers.length) ];
	}

/** We get notified through this methos when certain things happen. We parse out all the different messages and do nothing with them. 

Not sure if this works yet ???
*/
public String message( String message, Object data )
	{

	if ("chat".equals(message))
		{
		String text = (String) data;
//		System.out.println(ID+" CHATLOG "+text);
		
		if (text.startsWith("/t"))
			{
			// Team chats
			}
			
		// Examples of sending chat and team-chat:	
//		if (text.indexOf("All players have finished") > -1)
//			{
//			world.sendChat("/t we're doing good");
//			world.sendChat("death to all enemies!");
//			}
		}
		
	else if ("roundIsEnding".equals(message))
		{
		// courtesy notification for teaming bots.
		// could finalize moves here.
		// don't take up lots of time here, humans expect the round to advance fast right now.
		
		// this technique is in testing stage. does it work?
		}
	
	else if ("endRoundMoves".equals(message))
		{
		// round is over. we get sent the list of all moves, to see who did what.
		List moveList = (List) data;
		for (int j = 0; j < moveList.size(); j++) {
			Command move = (Command) moveList.get(j);
			
			move.toCountry();
			move.getOwner();
			
			}
		}
		
	return null;
	}

}	// End of VoxAgentBase class
