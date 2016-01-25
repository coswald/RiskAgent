package com.sillysoft.vox.unit;

import com.sillysoft.vox.*;
import com.sillysoft.tools.*;

//
//  Tank.java
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

import java.awt.Image;

public class UnitCastle extends UnitAbstract implements Unit
{


private Player owner;

public UnitCastle(Player own)
	{
	owner = own;
	}
	
public int getAttack()
	{
	return 0;
	}

public int getDefend()
	{
	return 10;
	}

public int getMovement()
	{
	return 0;
	}
	
public int getType()	
	{	
	return 0;	}


public String toString()
	{
	return "Castle";
	}

public int getCost()
	{	return 80;	}

public boolean isCastle()
	{ return true; }
	
public String getImageFilename()
	{
	return "unit_castle.png";
	}

public Player getOwner()
	{
	return owner;
	}
	

}
