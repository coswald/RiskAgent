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
	//Represents the neuronID to add
	private int 					neuronID;
	//Represents the ID to print our a innovation database
	private static final long serialVersionUID = 141838380522290195L;


	public InnovationDB(int start)
	{
		//Set the first parameters
		innovationID = 0;
		neuronID = start;
	}

	public int getSize()
	{
		//Return the size of the innovation database
		return innovationDB.size();
	}

	public int curID()
	{
		//Return the current Id of the innovation database
		return innovationID;
	}

	public int getNext()
	{
		//Return the next neuron ID to be added
		return neuronID + 1;
	}

	public int getNeuronID(int index)
	{
		//Returns the neuron ID of the last neuron added
		return innovationDB.get(index - 1).getNeuronID();
	}

	public int innovationExists(InnovationType type, int from, int to, int id)
	{
		//See if the innovation datebase contains the toFind object
		for (int i = 0; i < innovationDB.size(); i++)
		{
			//If the innovation exists then return the innovation ID
			if (innovationDB.get(i).isEqual(type, from, to, id))
			{
				 return i;
			}
		}
		//If its not in the database return -1
		return -1;
	}

	public int innovationExists(InnovationType type, int from, int to)
	{
		//See if the innovation datebase contains the toFind object
		for (int i = 0; i < innovationDB.size(); i++)
		{
			//If the innovation exists then return the innovation ID
			if (innovationDB.get(i).isEqual(type, from, to))
			{
				 return i;
			}
		}
		//If its not in the database return -1
		return -1;
	}

	public int addInnovation(InnovationType type, int from, int to, int id)
	{
		//Determine the type of innovation and call the proper innovation exists function
		if (type == InnovationType.NEW_LINK)
		{
			int innovationCheck = innovationExists(type, from, to, id);

			//Check to see if innovation exists
			if (innovationCheck != -1)
			{
				//If it does return the innovation ID
				return innovationCheck;
			} 
			else 
			{
				//Add innovation
				innovationDB.add(innovationID, new Innovation(type, from, to, id));
				//Increment innovationID
				innovationID++; 
				return -1;
			}
		}
		else
		{
			int innovationCheck = innovationExists(type, from, to);

			//Check to see if innovation exists
			if (innovationCheck != -1)
			{
				//If it does return the innovation ID
				return innovationCheck;
			} 
			else 
			{
				//Add innovation
				innovationDB.add(innovationID, new Innovation(type, from, to, ++neuronID));
				//Increment innovationID and neuronID
				innovationID++; 
				return -1;
			}
		}
	}

	public int getInnovationID(int fromNeuron, int toNeuron)
	{
		//Loop through the innovation database and find the innovation and return its ID
		for (int i = 0; i < innovationDB.size(); i++)
		{
			//Check to see if the passed in link is in the Database
			if (innovationDB.get(i).isEqual(InnovationType.NEW_LINK, fromNeuron, toNeuron, -1))
			{
				return i;
			}
		}
		//If its not in the database return -1
		return -1;
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