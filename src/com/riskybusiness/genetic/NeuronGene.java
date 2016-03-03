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
import com.riskybusiness.neural.StepNeuron;
import com.riskybusiness.neural.SigmoidNeuron;

import java.io.Serializable;

public class NeuronGene implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    //Represents the ID of the Neuron
    private int     neuronID;
    //Represents the type of the Neuron, sigmoid or step
    private String  neuronType; 
    //Represents whether the neuron is recurrent
    private boolean neuronRecurrent;
    //Represents the weight of the neuron
    private float   neuronActivationResponse;
    //private int     innovationNum;

    //Create a neuron gene
    public NeuronGene(int id, String type, boolean recur, float activate)
    {
        neuronID = id;
        neuronType = type;
        neuronRecurrent = recur;
        neuronActivationResponse = activate;
    }

    //Get the id of the neuron
    public int getID()
    {
        return neuronID;
    }

    //Get the neuron type
    public String getNeuronType()
    {
        return neuronType;
    }

    //Set the neuron type
    public void setNeuronType(String type)
    {
        this.neuronType = type;
    }

    //Return whether the link is recurrent
    public boolean isRecurrent()
    {
        return neuronRecurrent;
    }
    
    //Set the recurrency of the neuron
    public void setRecurrency(boolean recur)
    {
        this.neuronRecurrent = recur;
    }
    
    //Get the activation response of the neuron
    public float getActivationResponse()
    {
        return neuronActivationResponse;
    }
    
    //Set the activation response of the neuron
    public void setActivationResponse(float activate)
    {
        this.neuronActivationResponse = activate;
    }
}