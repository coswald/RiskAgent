package com.sillysoft.vox.unit;

import com.sillysoft.vox.*;

import com.sillysoft.tools.*;

//
//  Knight.java
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

import java.awt.Image;

public class UnitFlag extends UnitAbstract implements Unit
{


protected Player owner;

public UnitFlag(Player own)
	{
	owner = own;
	}
	
public int getAttack()
	{
	return 0;
	}

public int getDefend()
	{
	return 20;
	}

public int getMovement()
	{
	return 0;
	}

public int getType()	{	return 0;	}

public String toString()
	{
	return "Flag";
	}

public int getCost()
	{	return 5;	}
	
public String getImageFilename()
	{
	return "unit_flag.png";
	}

public Player getOwner()
	{
	return owner;
	}

public boolean isFlag()
{ return true; }

public String getShortString()
	{ return "Flag"; }
}
