package com.sillysoft.vox;

//
//  UnitPack.java
//
//  Created by Dustin Sacks on 1/7/05.
//  Copyright 2005 Sillysoft Games. All rights reserved.
//

import com.sillysoft.vox.unit.*;
import com.sillysoft.tools.SS;
import org.xml.sax.SAXException;
import java.util.Hashtable;

/** UnitPack is a factory class for creating Units. */
public class UnitPack 
{

/** Will not throw exceptions, may return null. */
public static Unit createUnitSafe(String unitType, Player owner)
	{
	try
		{
		return createUnit(unitType, owner);
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
		
	return null;
	}

public static Unit createUnit(String unitType, Player owner) throws SAXException
	{
	
	if ("pawn".equalsIgnoreCase(unitType) || "infantry".equalsIgnoreCase(unitType))
		return new UnitPawn(owner);
		
	else if ("knight".equalsIgnoreCase(unitType) || "tank".equalsIgnoreCase(unitType))
		return new UnitKnight(owner);
		
	else if ("flag".equalsIgnoreCase(unitType))
		return new UnitFlag(owner);
		
	else if ("castle".equalsIgnoreCase(unitType) || "base".equalsIgnoreCase(unitType) || "factory".equalsIgnoreCase(unitType))
		return new UnitCastle(owner);		
		
/*		

	else if ("Artillery".equalsIgnoreCase(unitType))
		return new UnitArtillery(owner);
		
		
	else if ("jet".equalsIgnoreCase(unitType))
		return new UnitJet(owner);
	else if ("bomber".equalsIgnoreCase(unitType))
		return new UnitJet(owner);
	else if ("transport-plane".equalsIgnoreCase(unitType))
		return new UnitTransportPlane(owner);
		
	else if ("sub".equalsIgnoreCase(unitType))
		return new UnitSub(owner);
	else if ("Transport".equalsIgnoreCase(unitType))
		return new UnitTransportBoat(owner);
	else if ("cruiser".equalsIgnoreCase(unitType))
		return new UnitCruiser(owner);
	else if ("carrier".equalsIgnoreCase(unitType))
		return new UnitCarrier(owner);


	else if ("fort".equalsIgnoreCase(unitType))
		return new UnitBase(owner);
	else if ("missile".equalsIgnoreCase(unitType))
		return new UnitMissile(owner);
	else if ("Nuke".equalsIgnoreCase(unitType))
		return new UnitNuke(owner);
		
	else if ("fast Tank".equalsIgnoreCase(unitType))
		return new UnitTankFast(owner);
	else if ("fast infantry".equalsIgnoreCase(unitType))
		return new UnitPawnFast(owner);
*/		
	
	SS.debug("UnitPack Unknown unit type: "+unitType);
	return null;
//	throw new SAXException("Unknown unit type: "+unitType);
	}
}
