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
    
    private int     fromNeuron;
    private int     toNeuron;
    private double  linkWeight;
    private boolean linkEnabled;
    private boolean linkRecurrent;
    private int     innovationID;
    
    //Create a LinkGene
    public LinkGene(int fNeuron, int tNeuron, int id, double weight, boolean recur)
    {
        fromNeuron = fNeuron;
        toNeuron = tNeuron;
        linkWeight = weight;
        linkEnabled = true;
        linkRecurrent = recur;
        innovationID = id;
        //I have no clue what to do with the innovationID. -K
        //Neither do I! -C
    }
    
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
    
    //Possibly combines these two?
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
    
    public void setFromNeuron(int neuron)
    {
        this.fromNeuron = neuron;
    }

    public void setToNeuron(int neuron)
    {
        this.toNeuron = neuron;
    }
    
    //Have it return a Synapse -C
    public Synapse createSynapse()
    {
        //com.riskybusiness.neural.Synapse(//Not sure if this is correct but I think it is unique so should work for an id
                                         //this.innovationID, this.fromNeuron, this.toNeuron);
        /*
         * It was correct! Just syntatical issues.
         * The only other thing that I can see issue with is decoding the from neuron and to neuron from ints into actual
         * Neurons that need to be present for the formation of a synapse. I think what will have to happen is the NeuronGenes
         * will form first, and then a LinkGene will be formed using the neurons made from the NeuronGenes. I don't know how
         * we'll do this, but I don't see a huge problem, just little phinicky things.
         */
        return new Synapse(this.innovationID, new com.riskybusiness.neural.SigmoidNeuron(this.fromNeuron), new com.riskybusiness.neural.SigmoidNeuron(this.toNeuron)); //make neurons with inputs for now.
        
    }

}
