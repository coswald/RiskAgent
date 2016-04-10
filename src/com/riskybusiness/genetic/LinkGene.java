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
    
    public LinkGene()
    {
        fromNeuron = -1;
        toNeuron = -1;
        linkWeight = 0.0;
        linkEnabled = false;
        innovationID = 0;
    }

    // //Create a LinkGene
    // public LinkGene(int myID, int fNeuron, int tNeuron, int id, double weight)
    // {
    //     linkID        = myID;
    //     fromNeuron    = fNeuron;
    //     toNeuron      = tNeuron;
    //     linkWeight    = weight;
    //     linkEnabled   = true;
    //     innovationID  = id;
    // }
    
    //Create a LinkGene
    public LinkGene(int myID, int fNeuron, int tNeuron, int id, double weight, boolean enabled)
    {
        linkID        = myID;
        fromNeuron    = fNeuron;
        toNeuron      = tNeuron;
        linkWeight    = weight;
        innovationID  = id;
        linkEnabled   = enabled;
    }

    //Get the ID of the link
    public int getID()
    {
        return linkID;
    }

    //Get the ID of the link
    public int getInnovationID()
    {
        return innovationID;
    }

    //Return whether the link is enabled
    public boolean getEnabled()
    {
        return linkEnabled;
    }
       
    //Get the weight of the link
    public double getWeight()
    {
        return linkWeight;
    }
    
    //Get the neuron the link goes to
    public int getToNeuron()
    {
        return toNeuron;
    }
    
    //Get the neuron the link comes from
    public int getFromNeuron()
    {
        return fromNeuron;
    }

    //Set the link to be enabled or disabled
    public void setLink(boolean enabled)
    {
        this.linkEnabled = enabled;
    }
    
    //Set the weight of the link
    public void setWeight(double weight)
    {
        this.linkWeight = weight;
    }
    
    //Set the from Neuron ID
    public void setFromNeuron(int neuron)
    {
        this.fromNeuron = neuron;
    }

    //Set the to Neuron ID
    public void setToNeuron(int neuron)
    {
        this.toNeuron = neuron;
    }

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
