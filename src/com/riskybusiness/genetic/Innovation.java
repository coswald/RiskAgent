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

public class Innovation extends Object
{
	//Represents the innovation type
	private InnovationType type;
	//Represents the neuron id of the neuron coming in to another neuron or link
	private int            neuronIn;
	//Represent the neuron id of the neuron coming out of another neuron or link
	private int            neuronOut;
	//Represents the ID of the neuron if its a neuron else -1 for links
	private int            neuronID;
	//private int            innovationNum;

	public Innovation(InnovationType type, int in, int out, int id)
	{
		this.type = type;
		this.neuronIn = in;
		this.neuronOut = out;
		this.neuronID = id;
		//this.innovationNum = num;
	}

	//fix this?
	//@Override
	public boolean equals(Innovation innovation)
	{
		return (this.type == innovation.type && this.neuronIn == innovation.neuronIn && this.neuronOut == innovation.neuronOut && this.neuronID == innovation.neuronID);
	}

	//Get whether the innovation is a neuron or a link
	public InnovationType getType() 
	{
		return this.type;
	}

	//Get synapse input neuronID, return -1 for neurons
	public int getInNode() 
	{
		return this.neuronIn;
	}

	//Get synapse output neuronID, return -1 for neurons
	public int getOutNode()
	{
		return this.neuronOut;
	}

	//Get id of the neuron, will return -1 for links
	public int getNeuronID()
	{
		return this.neuronID;
	}

	public void setType(InnovationType type) 
	{
		this.type = type;
	}

	//Get synapse input neuronID, return -1 for neurons
	public void setInNode(int in) 
	{
		this.neuronIn = in;
	}

	//Get synapse output neuronID, return -1 for neurons
	public void setOutNode(int out)
	{
		this.neuronOut = out;
	}

	//Get id of the neuron, will return -1 for links
	public void setNeuronID(int id)
	{
		this.neuronID = id;
	}
}