package com.sillysoft.vox.agent;

import com.sillysoft.vox.unit.*;
import com.sillysoft.vox.*;
import com.sillysoft.tools.*;
import java.util.*;

//
//  Bubbles.java
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
	Bubbles splits up all his units evenly, sort of like a Communist bot.
	*/



public class Bubbles extends VoxAgentBase implements VoxAgent
{

public Bubbles()
	{
	rand = new Random();
	}

public void buyUnits(int money)
	{
	List bases = world.getCastleCountriesOwnedBy(ID);
	if (bases.size() == 0)
		{ 
		//.... we don't own any castles! we should build one or take one over
		buyCastle();
		return;
		}	
		
	Country baseCountry = getWeakestBase();
	
	buyUnitsAlternating(money, baseCountry);
	}

public void declareMoves(Country[] countries)
	{
	super.declareMoves(countries);
	
	buyUnits(world.getPlayerMoney(ID));
	
	SS.debug("Bubbles is ordering moves...........");
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
						// We found some units that we can move!
						// Divide them up onto all borders
						List matchingBorders = countries[i].getAdjoiningList();
						int toMove = ug.getCount();
						
							for (int b = 0; toMove > 0; b++)
								{
								Country moveTo = (Country) matchingBorders.get(b % matchingBorders.size());
								world.moveUnit(ug, countries[i], moveTo, 1);
								toMove--;
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
	return "Bubbles";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Bubbles doesn't stick together.";
	}




// Oh boy. If this method ever gets called it is because we have won the game.
// Send back something witty to tell the user.
public String youWon()
	{ 
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {"Bubbly Success!",
		//	"Your skull is squishy and mellon-like",
	};

	return answers[ rand.nextInt(answers.length) ];
	}

}	// End of Bubbles class
