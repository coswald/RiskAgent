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
    
    private static final long serialVersionUID = -4444998043311781107L;
    
    //Represents the ID of the Neuron
    private int     neuronID;
    //Represents the type of the Neuron, sigmoid or step
    private String  neuronType;
    //Represents whether the neuron is a hidden neuron, input, or output
    private String  neuronLayerType;
    //Represents the layer the neuron exists in
    private int     neuronLayer;
    //Represents the weight of the neuron
    private double  neuronActivationResponse;
    //Represents the bias weight for the neuron
    private double  biasWeight;

    //Create a blank neuron gene
    public NeuronGene()
    {
        neuronID = -1;
        neuronType = "SIGMOID";
        neuronLayerType ="Hidden";
        neuronLayer = 1;
        neuronActivationResponse = 0.0;
    }

    //Create a neuron gene
    public NeuronGene(int id, String type, String layerType, double activate, int layer)
    {
        neuronID = id;
        neuronType = type;
        neuronLayerType = layerType;
        neuronLayer = layer;
        neuronActivationResponse = activate;
    }

    //Get the id of the neuron
    public int getID()
    {
        return this.neuronID;
    }

    public void setID(int id)
    {
        neuronID = id;
    }

    //Get the neuron type
    public String getNeuronType()
    {
        return this.neuronType;
    }

    //Set the neuron type
    public void setNeuronType(String type)
    {
        this.neuronType = type;
    }    
    
    //Get the activation response of the neuron
    public double getActivationResponse()
    {
        return this.neuronActivationResponse;
    }
    
    //Set the activation response of the neuron
    public void setActivationResponse(double activate)
    {
        this.neuronActivationResponse = activate;
    }

    //Get the layer of the neuron
    public int getNeuronLayer()
    {
        return this.neuronLayer;
    }

    //Set the layer of the neuron
    public void setNeuronLayer(int layer)
    {
        this.neuronLayer = layer;
    }

    //Increment the layer by one
    public void pushLayer()
    {
        this.neuronLayer += 1;
    }

    //Gets the type of layer this neuron is (Input, Hidden, or Output)
    public String getLayerType()
    {
        return this.neuronLayerType;
    }

    //Sets the layer type of the neuron
    public void setLayerType(String layerType)
    {
        neuronLayerType = layerType;
    }

    public double getBiasWeight()
    {
        return this.biasWeight;
    }

    public void setBiasWeight(double weight)
    {
        this.biasWeight = weight;
    }

    @Override
    public String toString()
    {
        //The string to return
        String toReturn = "";

        //Add all the information to the string
        toReturn += "This neuron is a " + neuronType + " neuron. The neuron exists in the " + neuronLayerType + " layer.\n";

        toReturn += "The neuron has an activation response of " + neuronActivationResponse + " and is in layer " + neuronLayer + "\n";

        //Return the string
        return toReturn;
    }
}