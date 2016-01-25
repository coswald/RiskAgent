package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import com.sillysoft.tools.*;
import java.util.*;

//
//  Quirky.java
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
	Quirky attacks randomly.
*/


public class Quirky extends VoxAgentBase implements VoxAgent
{

public Quirky()
	{
	rand = new Random();
	}

public void declareMoves(Country[] countries)
	{
	super.declareMoves(countries);
	SS.debug("Quirky is ordering moves...........");
	
	Country base = getWeakestBase();
	if (base == null)
		buyCastle();
	else
		buyUnitsAlternating(world.getPlayerMoney(ID), base);
	
	for (int i = 0; i < countries.length; i++)
		{
		try
			{
			if (countries[i].getUnitStackGroup().getTotalUnitCount() > 0 && countries[i].getTeam().equals(world.getTeam(ID)))
				{	
				// loop through the units here looking for ones we own to move
				UnitStackGroup usg = countries[i].getUnitStackGroup();
				for (int unit = 0; unit < usg.size(); unit++)
					{
					UnitStack ug = usg.get(unit);
					if (ug.getOwner().getID() == ID && ug.getUnit().getMovement() > 0)
						{	
						// only move 50% of the time		
						if (rand.nextInt(100) < 50)
							{
							Country moveTo = getRandomEnemyBorder(countries[i]);	
							if (moveTo == null)
								moveTo = getRandomBorder(countries[i]);	
							
							world.moveUnit(ug, countries[i], moveTo, ug.getCount());
							}
						}
					}
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			SS.debug("\n\n");
			}
		}
	}
	

public String name()
	{
	return "Quirky";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Quirky ~~~";
	}

// Oh boy. If this method ever gets called it is because we have won the game.
// Send back something witty to tell the user.
public String youWon()
	{ 
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {"squee ~~~",
	"squee ~~~",
	"~~ Quirk !!!" };

	return answers[ rand.nextInt(answers.length) ];
	}

}	// End of Quirky class
