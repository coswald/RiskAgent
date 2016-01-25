package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import com.sillysoft.tools.*;
import java.util.*;

//
//  Holdem.java
//  Lux
//
//  Copyright (c) 2002-2011 Sillysoft Games. 
//	http://sillysoft.net
//	lux@sillysoft.net
//
//	This source code is licensed free for non-profit purposes. 
//	For other uses please contact lux@sillysoft.net
//


/** 
Holdem tries to protect his lands.
*/

public class Holdem extends VoxAgentBase implements VoxAgent
{

public void declareMoves(Country[] countries)
	{
	super.declareMoves(countries);
	SS.debug("Holdem "+ID+" "+world.getPlayer(ID).name()+" ordering moves...........");
	
	List bases = world.getCastleCountriesOwnedBy(ID);
	if (bases.size() == 0)
		{ 
		//.... we don't own any castles! we should build one or take one over
		buyCastle();
		}	
	else 
		{
		buyPawns(world.getPlayerMoney(ID), getWeakestBase());
		}
	
	for (int i = 0; i < countries.length; i++)
		{		
		if (countries[i].getUnitStackGroup().getTotalUnitCount() > 0 && countries[i].getTeam().equals(world.getTeam(ID)))
			{	
			Country ourCountry = countries[i];
			
//			SS.debug("CountryPathFinder.distanceToEnemy("+ourCountry+") = "+CountryPathFinder.distanceToEnemy(ourCountry));
//			SS.debug("VoxAgentBase.distanceToEnemy("+ourCountry+") = "+distanceToEnemy(ourCountry));
//			if (true) continue;
			
			// loop through the units here looking for ones we own to move
			UnitStackGroup usg = ourCountry.getUnitStackGroup();
			for (int j = usg.size()-1; j > -1; j--)
				{
				UnitStack units = usg.get(j);
				if (units.getOwner().getID() == ID && units.getMovement() > 0)
					{	
					// We found some units that we own
					if (units.getUnitAttack() > 3)
						{
						// knights
						// Try to attack valuable enemy lands
						Country moveTo = getMostValuableEnemyBorder(ourCountry, units.getMovement());
						if (moveTo != null)
							{
							world.moveUnit(units, ourCountry, moveTo, units.getCount());
							}
						else 
							{
							// We can't reach any enemy country. Move them towards the front-lines
							Country dir1 = directionToEnemy(ourCountry);
							Country dir2 = directionToEnemy(dir1);
							if (dir2.equals(ourCountry))
								dir2 = dir1;
							world.moveUnit(units, ourCountry, dir2, units.getCount());
							}
						}
					else 
						{
						// pawn units
						int danger = (int) getDanger(ourCountry);
						int countryValue = (int) getIncome(ourCountry);
						if (ourCountry.hasCastle())
							countryValue += 10;
							
						int numberToMove = units.getCount();	// load them all
						if (countryValue > 3 && danger > .6)
							{	// keep some for defence
							numberToMove = units.getCount() - ((danger + countryValue + countryValue));
							numberToMove = Math.max(0, numberToMove);
							}
							
						// Divide up amongst all front-line countries - where we own or can win
						List borders = ourCountry.getAdjoiningList();
						// Build a list of friendly countries that deserve some defence:
						List friendly = new ArrayList();
						
						boolean madeAttack = false;	// attack once per country max
						for (int b = 0; b < borders.size(); b++)
							{
							Country check = (Country) borders.get(b);
							if (! check.getTeam().equals(ourCountry.getTeam()))		
								{	// enemy country, can we win?
								if (! madeAttack && numberToMove > check.getUnitStackGroup().getTotalUnitCount())
									{
									int moveHere = check.getUnitStackGroup().getTotalUnitCount() + 2 + rand.nextInt(5);
									world.moveUnit(units, ourCountry, check, moveHere);
									numberToMove -= moveHere;
									madeAttack = true;
									}
								}
							else if (distanceToEnemy(check) < 3)
								{
								friendly.add(check);
								}
							}
						
						if (friendly.size() == 0)
							{
							// no neighbors in need of defence, move towards frontlines
							world.moveUnit(units, ourCountry, directionToEnemy(ourCountry), numberToMove);
							}
						
						// distribute among friendlies wanting defence
						for (int m = 0; m < friendly.size(); m++)
							{
							int move = numberToMove/friendly.size();
							if (move > 0)
								world.moveUnit(units, ourCountry, (Country) friendly.get(m), move);
							}
						}
					}
				}
			}
		}
	}


public String name()
	{
	return "Holdem";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Holdem tries to hold his countries";
	}


public String youWon()
	{ 
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {"You've got to know when to hold 'em,\nknow when to fold 'em,\nknow when to walk out,\nand know when to run.",
		//	"You got to know when to hold 'em",
		//	"You got to know when to hold 'em" 
	};

	return answers[ rand.nextInt(answers.length) ];
	}

}	// End of AK class
