/**
 * Copyright (C) 2016  Coved Oswald, Kaleb Luse, and Weston Miller

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.riskybusiness;

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;
import com.sillysoft.lux.agent.LuxAgent;

public abstract class LuxAgentAdapter implements LuxAgent
{
    protected int ID;
    protected float version = 1.0f;
    protected String description = "An AI";
    protected Board board;
    protected Country[] countries;
    
    public LuxAgentAdapter()
    {
    }
    
    @Override
    public void setPrefs(int ID, Board board)
    {
	this.ID = ID;
	this.board = board;
	this.countries = board.getCountries();
    }
    
    public String name()
    {
	return this.getClass().getName();
    }
    
    public float version()
    {
	return this.version;
    }
    
    public String description()
    {
	return this.description;
    }
}
    
