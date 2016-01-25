package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import com.sillysoft.tools.*;
import java.util.*;

//
//  Angry.java
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
Angry attacks lots.
*/


public class Angry extends VoxAgentBase implements VoxAgent
{

public Angry()
	{
	rand = new Random();
	}

public void declareMoves(Country[] countries)
	{
	super.declareMoves(countries);
	SS.debug("Angry is ordering moves...........");
	
	buyUnits(world.getPlayerMoney(ID));
	
	for (int i = 0; i < countries.length; i++)
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
					// only move 90% of the time		
					if (rand.nextInt(100) < 90)
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
	}
	
public String name()
	{
	return "Angry";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Angry is an AI that likes to attack.";
	}

// Oh boy. If this method ever gets called it is because we have won the game.
// Send back something witty to tell the user.
public String youWon()
	{ 
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {"Muh-Ha-Ha-Ha\nAngry now very happy!",
	"Your skull is squishy and mellon-like",
	"ME STILL ANGRY!!!" };

	return answers[ rand.nextInt(answers.length) ];
	}

}	// End of Angry class
