package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;

//
//  VoxAgent.java
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
The VoxAgent interface acts as a bridge between agents and the game. <BR>
Implement all of the methods and Vox will call them at the specified times.
*/

public interface VoxAgent 
{

/**
At the start of the game your agent will be constructed and then the setPrefs() method will be called. It will tell you your ownerCode as well as give you a reference to the VoxWorld object for this game. You should store this information, as you will need it later.		*/
public void setPrefs( int ID, VoxWorld world );


/** Each turn this method is called for the agent to send his moves to the VoxWorld. This is the method where most of your bot smarts will go. Look inside Grabby or another example bot to see how to interact with the game.	*/
public void declareMoves(Country[] countries);


/** This is the name of your agent. It will identify you in the info window and record books.	*/
public String name();


/** The version of your agent. It is used by the plugin manager to notify the user when new versions are made available.	*/
public float version();


/** A description of your agent.	*/
public String description();


/** If your agent wins the game then this method will be called.		<BR>
Whatever you return will be displayed in big letters across the screen.
<P>
If you think that you will win a lot please provide many different answers for variety.	*/
public String youWon();


/** This method may be used in the future to send notifications to the VoxAgent.	*/ 
public String message( String message, Object data );


}	// that's the end of the VoxAgent interface