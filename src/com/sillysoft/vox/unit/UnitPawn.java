package com.sillysoft.vox.unit;

import com.sillysoft.vox.*;

import com.sillysoft.tools.*;

//
//  UnitPawn.java
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

import java.awt.Image;

public class UnitPawn extends UnitAbstract implements Unit
{

protected Player owner;

public UnitPawn(Player own)
	{	owner = own;	}
	
public String toString()
	{	return "Pawn";	}

public int getCost()
	{	return 3;	}

public int getAttack()
	{	return 1;	}

public int getDefend()
	{	return 3;	}

public int getMovement()
	{	return 1;	}

public int getType()	{	return 2;	}


public String getShortString()
	{ return "Pawn"; }

	
public String getImageFilename()
	{
	return "unit_pawn.png";
	}

public Player getOwner()
	{
	return owner;
	}
	
}
