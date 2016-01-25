package com.sillysoft.vox;

//
//  CountriesManager.java
//  Sillysoft Games
//

import java.util.*;

public interface CountriesManager
{
	
public List getCountries();

public List getContinents();

public Player getPlayer(int ID);

public VoxWorld getWorld();

}
