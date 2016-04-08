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

public class Innovation implements Serializable
{
	//Represents the innovation type
	private InnovationType type;
	//Represents the neuron id of the neuron coming in to another neuron or link
	private int            fromNeuron;
	//Represent the neuron id of the neuron coming out of another neuron or link
	private int            toNeuron;
	//Represents the ID of the neuron if its a neuron else -1 for links
	private int            neuronID;
	//private int            innovationNum;

	private static final long serialVersionUID = 7260173936646208532L;

	public Innovation(InnovationType type, int from, int to, int id)
	{
		this.type = type;
		this.fromNeuron = from;
		this.toNeuron = to;
		this.neuronID = id;
		//this.innovationNum = num;
	}

	//fix this?
	//@Override
	public boolean equals(Innovation innovation)
	{
		return (this.type == innovation.type && this.fromNeuron == innovation.fromNeuron && this.toNeuron == innovation.toNeuron && this.neuronID == innovation.neuronID);
	}

	public boolean isEqual(InnovationType type, int fromNeuron, int toNeuron, int neuronID)
	{
		return (this.type == type && this.fromNeuron == fromNeuron && this.toNeuron == toNeuron && this.neuronID == neuronID);
	}

	public boolean isEqual(InnovationType type, int fromNeuron, int toNeuron)
	{
		return (this.type == type && this.fromNeuron == fromNeuron && this.toNeuron == toNeuron);
	}

	//Get whether the innovation is a neuron or a link
	public InnovationType getType() 
	{
		return this.type;
	}

	//Get synapse input neuronID, return -1 for neurons
	public int getFromNeuron() 
	{
		return this.fromNeuron;
	}

	//Get synapse output neuronID, return -1 for neurons
	public int getToNeuron()
	{
		return this.toNeuron;
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
	public void setFromNeuron(int neuron) 
	{
		this.fromNeuron = neuron;
	}

	//Get synapse output neuronID, return -1 for neurons
	public void setToNeuron(int neuron)
	{
		this.toNeuron = neuron;
	}

	//Get id of the neuron, will return -1 for links
	public void setNeuronID(int id)
	{
		this.neuronID = id;
	}
}