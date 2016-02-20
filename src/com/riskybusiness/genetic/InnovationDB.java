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

public class InnovationDB implements Serializable
{

	private ArrayList<Innovation> innovationDB;
	private Innovation toFind;
	private int innovationID = 0;

	private boolean innovationExists(InnovationType type, int in, int out, int id)
	{
		//Instantiate the inputs into an innovation to be compared throughout the database
		toFind = new Innovation(type, in, out, id);
		//See if the innovation datebase contains the toFind object
		return (innovationDB.contains(toFind) == -1);

	}

	public int addInnovation(InnovationType type, int in, int out, int id)
	{
		//Check to see if innovation exists
		if innovationExists(type, in, out, id)
		{
			return -1;
		} 
		else 
		{
			//Add innovation
			innovationDB.add(innovationID, new Innovation(type, in, out, id));
			//Increment innovationID
			innovationID++; 
		}
	}
}