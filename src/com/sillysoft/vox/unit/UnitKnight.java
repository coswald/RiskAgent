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

public class UnitKnight extends UnitAbstract implements Unit
{


protected Player owner;

public UnitKnight(Player own)
	{
	owner = own;
	}
	
public int getAttack()
	{
	return 5;
	}

public int getDefend()
	{
	return 2;
	}

public int getMovement()
	{
	return 2;
	}

public int getType()	{	return 1;	}

public String toString()
	{
	return "Knight";
	}

public int getCost()
	{	return 5;	}
	
public String getImageFilename()
	{
	return "unit_knight.png";
	}

public Player getOwner()
	{
	return owner;
	}

public String getShortString()
	{ return "Knight"; }
}
