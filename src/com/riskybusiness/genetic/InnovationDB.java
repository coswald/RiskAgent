/* 
 * Copyright (C) 2016  Coved Oswald, Kaleb Luse, and Weston Miller
 *
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

package com.riskybusiness.genetic;

import java.io.Serializable;
import java.util.ArrayList;

public class InnovationDB implements Serializable
{

	//Represents an array of innovations
	private ArrayList<Innovation> 	innovationDB = new ArrayList<Innovation>();
	//Represents the innovationto find in the database
	private Innovation 				toFind;
	//Represetns the innovationID
	private int 					innovationID;

	private static final long serialVersionUID = 141838380522290195L;


	public InnovationDB()
	{
		innovationID = 0;
	}

	public int getSize()
	{
		return innovationDB.size();
	}

	public int getInnovationID(int fromNeuron, int toNeuron)
	{
		for (int i = 0; i < innovationDB.size(); i++)
		{
			if (innovationDB.get(i).isEqual(InnovationType.NEW_LINK, fromNeuron, toNeuron, -1))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	Fix these two function
	**///Also the id may not be necassary
	public boolean innovationExists(InnovationType type, int from, int to, int id)
	{
		boolean exists = false;

		//See if the innovation datebase contains the toFind object
		for (int i = 0; i < innovationDB.size(); i++)
		{
			if (innovationDB.get(i).isEqual(type, from, to, id))
			{
				exists = true;
			}
		}

		return exists;
	}

	public int addInnovation(InnovationType type, int to, int from, int id)
	{
		//Check to see if innovation exists
		if (innovationExists(type, from, to, id))
		{
			return -1;
		} 
		else 
		{
			//Add innovation
			innovationDB.add(innovationID, new Innovation(type, from, to, id));
			//Increment innovationID
			innovationID++; 
			return 0;
		}
	}

	public int curID()
	{
		return innovationID;
	}

	@Override
	public String toString()
	{
		String toReturn = "";
		toReturn = "Printing Database...\n";
		for (int i = 0; i < innovationDB.size(); i++)
		{
			Innovation toPrint = innovationDB.get(i);
			toReturn += "Type: " + toPrint.getType() + " From Neuron: " + toPrint.getFromNeuron() + " To Neuron: " + toPrint.getToNeuron() + " ID: " + toPrint.getNeuronID() + "\n";
		}	

		return toReturn;
	}
}