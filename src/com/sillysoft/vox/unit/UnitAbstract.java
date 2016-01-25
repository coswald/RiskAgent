package com.sillysoft.vox.unit;

import com.sillysoft.vox.*;
import com.sillysoft.tools.*;

//
//  an Abstract Unit class
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

import java.awt.Image;

public abstract class UnitAbstract implements Unit
{


private Player owner;
private static Image image;
	
public int getAttack()
	{
	return 0;
	}

public int getDefend()
	{
	return 8;
	}

public int getMovement()
	{
	return 0;
	}

public int getAttackMovement()
	{
	return getMovement();
	}

public abstract int getType();

public abstract String toString();

public abstract int getCost();

public int getNukeLevel()
	{ return 0; }
public boolean expiresAfterAttack()
	{ return false; }
	
public boolean isFort()
	{ return false; }
public boolean isCastle()
	{ return false; }
public boolean isSub()		
{ return false; }
public boolean isFlag()		
{ return false; }
public boolean isWater()
	{ return false; }
public int transportCapacity()
	{ return 0; }
public int transportCapacityAir()
	{ return 0; }
public int transportCapacityMissiles()
	{ return 0; }	
public int transportWeight()
	{ return 1; }
public boolean isAir()
	{ return false; }
public boolean canShoreBombard()
	{ return false; }
	

public String getImageFilename()
	{
	return "abstract-unit.png";
	}

public Player getOwner()
	{
	return owner;
	}
	
	//private static IntegerHashtable savedWidth = new IntegerHashtable(), savedHeight = new IntegerHashtable();
public int getWidth()
	{	
		return 50;
		/*
	int savedWidthInt = savedWidth.getInt(getImageFilename());
	if (savedWidthInt == 0)
		{		
		Image i = GraphicsTool.getManagedImage(getImageFilename());
		savedWidthInt = i.getWidth(null);
		int savedHeightInt = i.getHeight(null);
		savedWidth.putInt(getImageFilename(), savedWidthInt);
		savedHeight.putInt(getImageFilename(), savedHeightInt);
		GraphicsTool.releaseManagedImage(getImageFilename());
		SS.debug(getImageFilename()+" sized at "+savedWidthInt+","+savedHeightInt);
		}
	return savedWidthInt;
		 */
	}
public int getHeight()
	{	
		return 50;
		/*
	int savedHeightInt = savedHeight.getInt(getImageFilename());
	if (savedHeightInt == 0)
		{
		getWidth();
		}
	return savedHeight.getInt(getImageFilename());
		 */
	}
	
public String getShortString()
	{ return toString(); }
public String getSpecialString()
	{ return ""; }
	
public int getNumberOffsetX()
	{	return 20;	}	// 21, 25
	
public int getNumberOffsetY()
	{	return 25;	}	// 10
}
