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

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;

import java.io.Serializable;

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis class contains the metadata
 * used to construct a {@code Synapse}. This class also contains
 * data used to help form and mantain generations within a
 * population.</p>
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.neural.Synapse
 */
public class LinkGene implements Serializable
{
    
    private static final long serialVersionUID = -6113158899915565377L;
    
    //Represents the ID of the link
    private int     linkID;
    //Represents the ID of the neuron the link/synapse is coming from
    private int     fromNeuron;
    //Represents the ID of the neuron the link/synapse is going to
    private int     toNeuron;
    //Represents the weight of the link
    private double  linkWeight; //Not necassary at the moment?
    //Represents if the link is enabled
    private boolean linkEnabled;
    //Represents the innovation ID
    private int     innovationID;
    
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates a
	 * {@code LinkGene} with the given ID and
	 * other metadata.</p>
	 * @param myID The ID for this gene.
	 * @param fNeuron The neuron sending data.
	 * @param tNeuron The neuron receiving the data.
	 * @param weight The weight on this link.
	 * @param enabled Tells whether or not this link
	 * 			works or not.
	 */
    public LinkGene(int myID, int fNeuron, int tNeuron, int id, double weight, boolean enabled)
    {
        linkID        = myID;
        fromNeuron    = fNeuron;
        toNeuron      = tNeuron;
        linkWeight    = weight;
        innovationID  = id;
        linkEnabled   = enabled;
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates a blank
	 * {@code LinkGene}. This states an invalid
	 * sending and receiving neuron, with a weight
	 * of zero that is dissabled.</p>
	 * @see com.riskybusiness.genetic.LinkGene#LinkGene(int, int, int, int, double, boolean)
	 */
	public LinkGene()
    {
        fromNeuron = -1;
        toNeuron = -1;
        linkWeight = 0.0;
        linkEnabled = false;
        innovationID = 0;
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the ID
	 * of the link.</p>
	 * @return The ID of the link
	 */
	public int getID()
    {
        return linkID;
    }

    //Get the ID of the link
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the ID
	 * corresponding to the {@code Innovation}
	 * representing this link.</p>
	 * @return The innovation ID associated with
	 * 			this link.
	 */
	public int getInnovationID()
    {
        return innovationID;
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns whether
	 * the link is enabled or not.</p>
	 * @returns true if the link is enabled, false
	 * 			otherwise.
	 */
    public boolean getEnabled()
    {
        return linkEnabled;
    }
       
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the
	 * weight of the link.</p>
	 * @return The weight of this link.
	 */
	public double getWeight()
    {
        return linkWeight;
    }
    
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the
	 * ID of the receiving neuron. This is the
	 * neuron the link is sending the data to.</p>
	 * @return The neuron the link goes to.
	 */
	public int getToNeuron()
    {
        return toNeuron;
    }
    
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the
	 * ID of the sending neuron. This is the
	 * neuron the link is sending the data to.</p>
	 * @return The neuron the link comes from.
	 */
	public int getFromNeuron()
    {
        return fromNeuron;
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges whether
	 * or not the link is enabled.</p>
	 * @param enabled The new enabled state.
	 */
	public void setLink(boolean enabled)
    {
        this.linkEnabled = enabled;
    }
    
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspSets the weight
	 * of the link.</p>
	 * @param weight The new weight to change to.
	 */
	public void setWeight(double weight)
    {
        this.linkWeight = weight;
    }
    
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the
	 * sending neuron of this link.</p>
	 * @param neuron The new sending neuron.
	 */
	public void setFromNeuron(int neuron)
    {
        this.fromNeuron = neuron;
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the
	 * receiving neuron of this link.</p>
	 * @param neuron The new receiving neuron.
	 */
    public void setToNeuron(int neuron)
    {
        this.toNeuron = neuron;
    }
	
	/**
	 * @inheritDoc
	 */
    @Override
    public String toString()
    {
        //The string to return
        String toReturn = "";

        //Add all the information to the string
        toReturn += "This link comes from neuron: " + fromNeuron + " and goes to neuron: " + toNeuron + "\n";

        toReturn += "This link has a weight of " + linkWeight + " and is " + linkEnabled;

        //Return the string
        return toReturn;
    }
}
