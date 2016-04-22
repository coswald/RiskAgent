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

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis class represents a single
 * {@code Innovation} to be entered into the {@code InnovationDB}.
 * An {@code Innovation} is any addition to the network that
 * can be described in a database setting to be used for NEAT.
 * A single {@code Innovation} consists of an {@code InnovationType},
 * and different values depending on whether or not this type
 * is adding link or a neuron. If a neuron is to be added,
 * then the neuron is put inbetween two neurons on a link,
 * making the "sending" neuron the left neuron, and the
 * "receiving" neuron the right neuron, while the neuron's id
 * is set to the added neurons ID. However, if a link is to be
 * added, the sending and receiving neurons are what you think
 * they would be for a link, but the neuron ID is set to -1.</p>
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Weston Miller
 * @ldm 22 April 2016
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.genetic.InnovationType
 */
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
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates an {@code Innovation}
	 * that uses the given parameters to describe a change in
	 * the network.</p>
	 * @param type Describes the type of {@code Innovation}.
	 * @param from Describes the neuron that sends information.
	 * @param to Describes the neuron that recieves information.
	 * @param id The id of the neuron to be added, -1 if no
	 * 			addition of a neuron is taking
	 * 			place.
	 * @see com.riskybusiness.genetic.Innovation
	 */
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
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChecks to see if this
	 * {@code Innovation} is equal to another neuron addition
	 * {@code Innovation}.</p>
	 * @param type The type to compare to this ones.
	 * @param fromNeuron The fromNeuron to compare to this ones.
	 * @param toNeuron The toNeuron to compare to this ones.
	 * @param neuronID The neuronID to comapre to this ones.
	 * @return Whether or not these two {@code Innovations} are equal.
	 */
	public boolean isEqual(InnovationType type, int fromNeuron, int toNeuron, int neuronID)
	{
		return (this.type == type && this.fromNeuron == fromNeuron && this.toNeuron == toNeuron && this.neuronID == neuronID);
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChecks to see if this
	 * {@code Innovation} is equal to another link addition
	 * {@code Innovation}.</p>
	 * @param type The type to compare to this ones.
	 * @param fromNeuron The fromNeuron to compare to this ones.
	 * @param toNeuron The toNeuron to compare to this ones.
	 * @return Whether or not these two {@code Innovations} are equal.
	 */
	public boolean isEqual(InnovationType type, int fromNeuron, int toNeuron)
	{
		return (this.type == type && this.fromNeuron == fromNeuron && this.toNeuron == toNeuron);
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the
	 * {@code InnovationType} of this {@code Innovation}.
	 * @return The {@code InnovationType} of this class.
	 * @see com.riskybusiness.genetic.InnovationType
	 */
	public InnovationType getType() 
	{
		return this.type;
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the sending
	 * neuron's identification integer.</p>
	 * @return The id of the sending neuron.
	 */
	public int getFromNeuron() 
	{
		return this.fromNeuron;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the receiving
	 * neuron's identification integer.</p>
	 * @return The id of the receiving neuron.
	 */
	public int getToNeuron()
	{
		return this.toNeuron;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the new
	 * neuron's identification integer.</p>
	 * @return The id of the new neuron, -1 if this
	 * 			is a link {@code Innovation}.
	 */
	public int getNeuronID()
	{
		return this.neuronID;
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the type
	 * of {@code Innovation} this is.</p>
	 * @param type The new, different {@code InnovationType}.
	 */
	public void setType(InnovationType type) 
	{
		this.type = type;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the sending
	 * neuron's id.</p>
	 * @param neuron The new neuron ID.
	 */
	public void setFromNeuron(int neuron) 
	{
		this.fromNeuron = neuron;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the receiving
	 * neuron's id.</p>
	 * @param neuron The new neuron ID.
	 */
	public void setToNeuron(int neuron)
	{
		this.toNeuron = neuron;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the neuron's
	 * id.</p>
	 * @param id The new neuron ID.
	 */
	public void setNeuronID(int id)
	{
		this.neuronID = id;
	}
}
