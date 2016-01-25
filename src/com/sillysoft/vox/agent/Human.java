package com.sillysoft.vox.agent;

import com.sillysoft.vox.unit.*;
import com.sillysoft.vox.*;
import com.sillysoft.tools.*;

//
//  Human.java
//  Lux
//
//  Copyright (c) 2002-2011 Sillysoft Games. 
//	http://sillysoft.net
//	lux@sillysoft.net
//
//	This source code is licensed free for non-profit purposes. 
//	For other uses please contact lux@sillysoft.net
//

import java.util.Random;
import java.util.List;
import java.util.*;

public class Human implements VoxAgent
{
// This agent's ownerCode:
protected int ID;

// We store some refs the world and to the country array
protected VoxWorld world;
protected Country[] countries;

// It is useful to have a random number generator for a couple of things
protected Random rand;

public Human()
	{
	rand = new Random();
	}

public int pickCountry()
	{
	return 42;
	}
	
public void declareMoves(Country[] countries)
	{	
	}

public UnitStackGroup buyUnits(int money)
	{
	return null;
	}

// Save references to 
public void setPrefs( int newID, VoxWorld theworld )
	{
	ID = newID;		// this is how we distinguish what countries we own

	world = theworld;
	}

public String name()
	{
	return "VoxHuman";
	}

public float version()
	{
	return 1.0f;
	}

public String description()
	{
	return "Vox human agent.";
	}


	
static public String youWonStatic()
	{
	// For variety we store a bunch of answers and pick one at random to return.
	String[] answers = new String[] {
		"Rock Out With Your Vox Out"
		
		,"Welcome to Voxtopia"
		
		,"All your Vox are Belong to Us!" 
		
		,"The Vox is Mightier than the Sword"
		
		,"A soldier will fight long and hard \nfor a bit of colored ribbon.         \n          - Napoleon Bonaparte"
			
		,"All men are brothers, like the seas throughout the world; So why do winds and waves clash so fiercely everywhere?          - Emperor Hirohito"
				
		,"All they that take the sword, \nshall perish with the sword.         \n          - Matthew, The Bible"
				
		,"Darkness cannot drive out darkness; \nonly light can do that. \nHate cannot drive out hate; \nonly love can do that.         \n          - Martin Luther King, Jr."
			
		,"God is not on the side of the big battalions, \nbut on the side of those who shoot best.         \n          - Voltaire"
		
		,"I know not with what weapons World War 3 will be fought, but World War 4 will be fought with sticks and stones.         \n          - Albert Einstein"
		
		,"In war there is no prize for the runner-up.        \n  - General Omar Bradley"
			
		,"It is fatal to enter any war without the will to win it.          - General Douglas MacArthur"
		
		,"Only the dead have seen the end of war         \n - Plato"
		
		,"Mankind must put an end to war before war puts an end to mankind.          - John F. Kennedy"
		
		,"The object of war is not to die for your country but to make the other bastard die for his.         \n          - George S. Patton, Jr."
		
		,"His words were softer than oil, yet were they drawn swords.          - Psalms, The Bible"
		
		,"War does not determine who is right, \n only who is left.          - Bertrand Russell"
		
		,"War is too serious a matter to entrust to military men.          - Georges Clemenceau"
		
		,"We make war that we may live in peace \n          - Aristotle"
		
		,"When engaged in combat, the vanquishing of thine enemy can be the warrior's only concern.\n          - Kill Bill, Volume 1"
		
		,"When the enemy advances, withdraw; \nwhen he stops, harass; \nwhen he tires, strike; \nwhen he retreats, pursue.         \n          - Mao Zedong"
		
		,"As long as war is regarded as wicked, it will always have its fascination. When it is looked upon as vulgar, it will cease to be popular.\n          - Oscar Wilde"
		,"War is sweet to those who have never experienced it.\n          - Pindar"
		,"In war, truth is the first casualty. \n          - Aeschylus"
		,"In peace, sons bury their fathers; in war, fathers bury their sons. \n          - Herodotus"
		,"Who was the first that forged the deadly blade? Of rugged steel his savage soul was made. \n          - Tibullus"
		
		,"Veni, vidi, vici"
		,"All your base are belong to us \n- Zero Wing"
		,"I'm in your castle, killin your doodz..."
		
		,"They speak of my barbaric ways and plunder, but never consider the many mouths I have to feed"

,"Success is the result of perfection, hard work, learning from failure, loyalty, and persistence. \n - Colin Powell"

,"From time to time, the tree of liberty must be watered with the blood of tyrants and patriots.\n - Thomas Jefferson"

,"In war there is no substitute for victory. \n- General Douglas MacArthur"

,"wars come and go but soldiers are forever. \n- tupac"

,"If only we could all just get along"

,"Always remember to pillage BEFORE you burn."

,"You can no more win a war than you can win an earthquake"

,"If I say its safe to surf this beach Captain, then its safe to surf this beach. \nYou either SURF or you FIGHT!"

,"When you have to kill a man it costs nothing to be polite. - Winston Churchill"

,"This ain't Hollywood. No kissing in the sunset."

,"May the Vox be with you, always."

,"No fate but what we make."

,"The Vox is Strong in This One."

,"Witness the True Power of the Vox !"

,"It is entirely seemly, for a young man killed in battle, to lie mangled by the lux spear. In his death, all things appear fair."

,"It is from their foes, not their friends, that voxers learn the lessons of building high walls."

,"Think Outside the Vox"

,"Vox Out With Your Cossack Out"

,"The object of war is not to die for your country but to make the other bastard die for his. - General Patton"

,"To the Voxer Go the Spoils"

,"All for Vox, and Vox for All"

		};
	
	return answers[ SS.rand.nextInt(answers.length) ];
	}

	
public String youWon()
	{ 
	return "non-static human youWon ?!";
	}

/** We get notified through this methos when certain things happen. Angry parses out all the different messages and does nothing with them. */
public String message( String message, Object data )
	{
	return null;
	}

}
