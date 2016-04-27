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

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspAn {@code InnovationDB} is a data
 * structure that allows for the storing of {@code Innovation}s that
 * describe change in a population of {@code Genome}s. These
 * {@code Innovation}s could later be used by the {@code Genome} to
 * test past changes in order to make better changes in the future,
 * ensuring the same changes aren't being made.</p>
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.genetic.Innovation
 */
public class InnovationDB implements Serializable
{

	//Represents an array of innovations
	private ArrayList<Innovation> 	innovationDB = new ArrayList<Innovation>();
	//Represetns the innovationID
	private int 					innovationID;
	private int						neuronID = 0;
	//Represents the ID to print our a innovation database
	private static final long serialVersionUID = 141838380522290195L;

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis will initialize an {@code InnovationDB}
	 * with the appropriate starting index for the database.</p>
	 */
	public InnovationDB()
	{
		innovationID = 0;
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the size of this database. This
	 * is representative of the amount of {@code Innovation}s present
	 * within the database, stored in an {@code ArrayList}.</p>
	 * @return The size of this database.
	 */
	public int getSize()
	{
		//Return the size of the innovation database
		return innovationDB.size();
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the current accessing index of
	 * the database.</p>
	 * @return The index that is used to access the database.
	 */
	public int curID()
	{
		//Return the current Id of the innovation database
		return innovationID;
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChecks whether or not the Innovation in
	 * question exists within the database. This will return the index at
	 * which the innovation does exist, or -1 if the innovation is not
	 * found within the dabase. This is used for added {@code NeuronGene}s.</p>
	 * @param type The type of the {@code Innovation} to find.
	 * @param from The from neuron of the {@code Innovation}.
	 * @param to The to neuron of the {@code Innovation}.
	 * @param id The neuron ID of the {@code Innovation}.
	 * @return The index of the {@code Innovation} to find, -1 if it is
	 * 			not in the database.
	 * @see com.riskybusiness.genetic.Innovation#isEqual(InnovationType, int, int, int)
	 */
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
	
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChecks whether or not the Innovation in
	 * question exists within the database. This will return the index at
	 * which the innovation does exist, or -1 if the innovation is not
	 * found within the dabase. This is used for added {@code LinkGene}s.</p>
	 * @param type The type of the {@code Innovation} to find.
	 * @param from The from neuron of the {@code Innovation}.
	 * @param to The to neuron of the {@code Innovation}.
	 * @return The index of the {@code Innovation} to find, -1 if it is
	 * 			not in the database.
	 * @see com.riskybusiness.genetic.Innovation#isEqual(InnovationType, int, int)
	 */
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
	
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspAdds an {@ocde Innovation} to the
	 * {@code InnovationDB}.</p>
	 * @param type The type of the {@code Innovation}.
	 * @param from The from neuron of the {@code Innovation}.
	 * @param to The to neuron of the {@code Innovation}.
	 * @param id The neuron ID of the {@code Innovation}. If a {@code LinkGene}
	 * 			is to be added, then -1 is to be passed in.
	 * @return The index of the {@code Innovation} if it exists in the database,
	 * 			-1 if it was not in the database and has been 
	 * 			successfully added.
	 */
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
	
	/**
	 * 
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspFinds the index at which the given 
	 * {@code Innovation} exists. this will return a -1 if it is not
	 * in the database.</p>
	 * @param fromNeuron The sending neuron data used to find the
	 * 			{@code Innovation}.
	 * @param toNeuron The receiving neuron data used to find the
	 * 			{@code Innovation}.
	 * @return The index at which the innovation exists, or -1 if it does
	 * 			not exists within the database.
	 */
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
	
	/**
	 * @inheritDoc
	 */
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
