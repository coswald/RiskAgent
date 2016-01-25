package com.sillysoft.vox;

import com.sillysoft.tools.*;

//
//  Unit.java
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

import java.awt.Image;

public interface Unit
{
public int getCost();

public int getAttack();

public int getDefend();

public int getMovement();

// Some units can have a different movement value for friendly vs attacking moves
public int getAttackMovement();

public int getType();

public Player getOwner();

// Is this a water unit
public boolean isWater();

public boolean isFort();
public boolean isCastle();
public boolean isFlag();
public boolean isSub();

// Some water units can attack the shore during amphibious attacks
public boolean canShoreBombard();

// How much transport can this unit hold
public int transportCapacity();
public int transportCapacityAir();
public int transportCapacityMissiles();

// How much transport space does this unit take up
public int transportWeight();

public boolean isAir();

public boolean expiresAfterAttack();

public int getNukeLevel();

public int getWidth();
public int getHeight();

// For positioning the # of units on top of the image
public int getNumberOffsetX();
public int getNumberOffsetY();

public String getImageFilename();

public String getShortString();

public String getSpecialString();
}
