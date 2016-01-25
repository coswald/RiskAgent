package com.sillysoft.vox.command;

import com.sillysoft.vox.*;
import com.sillysoft.tools.*;
import java.util.*;

abstract public class Command implements XMLSerializable
{
	
/** Gives the origin country of the move, or will return null if this is a build command. */
abstract public int fromCountry();

/** Gives the destination country of the command. */	
abstract public int toCountry();	

/** The team that is controlling these units. */
abstract public Team getTeam();
abstract public Player getOwner();

/** Does this command involve a battle in the given country?	*/
abstract public boolean battleInCountry(Country c);

/** Get the type of unit this command is made up of.	*/
abstract public Unit getUnit();

abstract public int getUnitCount();	

abstract public String toStringXML();

}