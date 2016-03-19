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
    
    private static final long serialVersionUID = 1L;
    
    //Represents the ID of the neuron the link/synapse is coming from
    private int     fromNeuron;
    //Represents the ID of the neuron the link/synapse is going to
    private int     toNeuron;
    //Represents the weight of the link
    private double  linkWeight; //Not necassary at the moment?
    //Represents if the link is enabled
    private boolean linkEnabled;
    //Represents if the link is recurrent
    private boolean linkRecurrent; //Not necassary right now but would be very useful
    //Represents the innovation ID
    private int     innovationID; //Still not sure what this does?
    
    //Create a LinkGene
    public LinkGene(int fNeuron, int tNeuron, int id, double weight, boolean recur)
    {
        fromNeuron    = fNeuron;
        toNeuron      = tNeuron;
        linkWeight    = weight;
        linkEnabled   = true;
        linkRecurrent = recur;
        innovationID  = id;
    }

    //Create a LinkGene
    //public LinkGene(int fNeuron, int tNeuron, int id)
    //{
    //    this.LinkGene(fNeuron, tNeuron, id, .1, false);
    //}
    
    //Get the ID of the link
    public int getID()
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

    //
    public boolean getRecurrency()
    {
        return linkRecurrent;
    }

    //Set the link to be enabled or disabled
    public void setLink(boolean enabled)
    {
        this.linkEnabled = enabled;
    }
    
    //Set the link to be either recurrent or not
    public void setRecurrency(boolean recur)
    {
        this.linkRecurrent = recur;
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
}
