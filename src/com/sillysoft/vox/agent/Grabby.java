package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import com.sillysoft.tools.*;
import java.util.*;

//
//  Grabby.java
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
Grabby uses knights to attack high-value countries. 
*/

public class Grabby extends VoxAgentBase implements VoxAgent
{

public void declareMoves(Country[] countries)
	{
	super.declareMoves(countries);
	
	SS.debug("Grabby "+ID+" "+world.getPlayer(ID).name()+" ordering moves...........");
	
	List bases = world.getCastleCountriesOwnedBy(ID);
	if (bases.size() == 0)
		{ 
		// we don't own any castles! we should build one or take one over
		buyCastle();
		}	
	else 
		{
		Country base = getFrontBaseSafe();
		if (base != null)
			buyUnitsAlternating(world.getPlayerMoney(ID), base);
		else 
			{
			// we have no safe bases?
			// do all-in pawns on the best chance?
			buyPawns(world.getPlayerMoney(ID), getStrongestBase());
			}

		}
	
	for (int i = 0; i < countries.length; i++)
		{	
		if (countries[i].getUnitStackGroup().getTotalUnitCount() > 0 
			&& countries[i].getTeam().equals(world.getTeam(ID)))
			{
			// loop through the units here looking for ones we own to move
			// loop backwards so we don't move out all of 1 unit and skip an index
			UnitStackGroup usg = countries[i].getUnitStackGroup();
			for (int j = usg.size()-1; j > -1; j--)
				{
				UnitStack units = usg.get(j);
				if (units.getOwner().getID() == ID && units.getMovement() > 0)
					{	
					// We found some units that we own
					if (units.getUnitAttack() > 3)
						{  // KNIGHTS
						// Try to attack valuable enemy lands
						Country moveTo = getMostValuableEnemyBorder(countries[i], units.getMovement());
						if (moveTo != null)
							{
							SS.debug("Grabby Knight attack from "+countries[i]+" to most valuable enemy: "+moveTo, 1);
							world.moveUnit(units, countries[i], moveTo, units.getCount());
							}
						else 
							{
							// We can't reach any enemy country. Move them towards the front-lines
							Country dir1 = directionToEnemy(countries[i]);
							Country dir2 = directionToEnemy(dir1);
							if (dir2.equals(countries[i]))
								dir2 = dir1;
							SS.debug("Knight move-towards-enemy from "+countries[i]+" to : "+dir2);
							world.moveUnit(units, countries[i], dir2, units.getCount());
							}
						}
					else 
						{	// PAWN units
						int danger = (int) getDanger(countries[i]);
						int countryValue = (int) getIncome(countries[i]);
						if (countries[i].hasCastle())
							countryValue += 5;
							
						int numberToMove = units.getCount();	// move them all
						if (countryValue > 3 && danger > .6)
							{	// keep some for defence
							numberToMove = units.getCount() - (danger + countryValue);
							numberToMove = Math.max(0, numberToMove);
							}
							
						if (getRandomEnemyBorder(countries[i]) == null)
							{
							// we have no enemy borders. move out towards enemy borders
							moveTowardsEnemySplittingUp(units, countries[i]);
							}
						else {
							// there are enemy borders, evenly move pawns to them
							List enemies = getEnemyBorders(countries[i]);
							for (int e = 0; e < enemies.size(); e++) 
								{
								world.moveUnit(units, countries[i], (Country) enemies.get(e), numberToMove/enemies.size());
								}
							}
						}
					}
				}
			}
		}
	}



public String name()
	{
	return "Grabby";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Grabby is a demo bot";
	}


public String youWon()
	{ 
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {"Gimme Gimme Got It All!",
	"Luck had nothing to do with it",
	"Trust The Computer. The Computer is your friend.",
	"The only legitimate use of a computer is to play games. \n          - Eugene Jarvis",
	"The most likely way for the world to be destroyed, most experts agree, is by accident.\n          - Nathaniel Borenstein",
	"Computers are useless. They can only give you answers. \n          - Pablo Picasso",
	"Don't trust a computer you can't throw out a window. \n          - Steve Wozniak",
	"My tanks were filled with gasoline and wars. I was a lead soldier. I marched against the smoke of the city....And the world closed its doors--anvils and hammers against the sleeping men--doors of the heart--cities everywhere--and litte lead soldiers. \n          - Giannina Braschi",
	"War is not a pathology that, with proper hygiene and treatment, can be wholly prevented.\n          - Philip Bobbitt",
	"War is a continuation of politics by other means. \n          - Carl von Clausewitz ",
	"All free men remember that in the final choice a soldier's pack is not so heavy a burden as a prisoner's chains. \n          - Dwight D. Eisenhower",
	"If you wish for peace, understand war. \n          - B. H. Liddell Hart",
	"Four things greater than all things are; \nWomen and Horses and Power and War. \n          - Rudyard Kipling",
	"War will not end until all of the violent people are killed. \n          - Roger Langbecker",
	"Men grow tired of sleep, love, singing and dancing sooner than war. \n          - Homer",
	"An army of sheep led by a lion would defeat an army of lions led by a sheep. ",
	"Great empires are not maintained by timidity. \n          - Tacitus",
	"EAT MY ROBOT DUST" 
	};

	return answers[ rand.nextInt(answers.length) ];
	}

}	// End of AK class
