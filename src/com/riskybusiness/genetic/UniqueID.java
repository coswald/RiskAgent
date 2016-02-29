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

public class UniqueID
{
	private int genomeID = 0;
	private int linkID   = 0;
	private int neuronID = 0;

	//Increment the genome ID and then return it
	public int getNextGenomeID()
	{
		return ++genomeID;
	}

	//Increment the link ID and then return it
	public int getNextLinkID()
	{
		return ++linkID;
	}

	//Increment the neuron ID and then return it
	public int getNextNeuronID()
	{
		return ++neuronID;
	}

	//Return the genome ID
	public int getCurGenomeID()
	{
		return genomeID;
	}

	//Return the link ID
	public int getCurLinkID()
	{
		return linkID;
	}

	//Return the neuron ID
	public int getCurNeuronID()
	{
		return neuronID;
	}
}