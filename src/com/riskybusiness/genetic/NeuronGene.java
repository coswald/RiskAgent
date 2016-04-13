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
<<<<<<< HEAD
<<<<<<< HEAD
    private double  neuronActivationResponse;
    //Represents the bias weight for the neuron
    private double  biasWeight;
    //Represents the list of links that connect to the neuron
    private ArrayList<Integer> incomingLinks = new ArrayList<Integer>();
    //Represents the list of links that leave this neuron
    private ArrayList<Integer> outgoingLinks   = new ArrayList<Integer>();
=======
    private double   neuronActivationResponse;
>>>>>>> parent of a9da4e3... Genome v8.1
=======
    private double   neuronActivationResponse;
>>>>>>> parent of a9da4e3... Genome v8.1

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

        //Create a neuron gene
    public NeuronGene(int id, String type, String layerType, double activate, int layer, ArrayList<Integer> incoming, ArrayList<Integer> outgoing)
    {
        neuronID = id;
        neuronType = type;
        neuronLayerType = layerType;
        neuronLayer = layer;
        neuronActivationResponse = activate;
        for (int i = 0; i < incoming.size(); i++)
        {
            incomingLinks.add(new Integer(incoming.get(i)));
        }
        for (int i = 0; i < outgoing.size(); i++)
        {
            outgoingLinks.add(new Integer(outgoing.get(i)));
        }
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

<<<<<<< HEAD
<<<<<<< HEAD
    public double getBiasWeight()
    {
        return this.biasWeight;
    }

    public void setBiasWeight(double weight)
    {
        this.biasWeight = weight;
    }

    public ArrayList<Integer> getIncomingLinks()
    {
        return this.incomingLinks;
    }

    public ArrayList<Integer> getOutgoingLinks()
    {
        return this.outgoingLinks;
    }

    public void setIncomingLinks(ArrayList<Integer> incoming)
    {
        this.incomingLinks.clear();
        for(int i = 0; i < incoming.size(); i++)
        {
            this.incomingLinks.add(new Integer(incoming.get(i)));
        }
    }

    public void setOutgoingLinks(ArrayList<Integer> outgoing)
    {
        this.outgoingLinks.clear();
        for(int i = 0; i < outgoing.size(); i++)
        {
            this.outgoingLinks.add(new Integer(outgoing.get(i)));
        }
    }

    public void addIncomingLink(int incomingLink)
    {
        this.incomingLinks.add(new Integer(incomingLink));
    }

    public void addOutgoingLink(int outgoingLink)
    {
        this.outgoingLinks.add(new Integer(outgoingLink));
    }

    //Increment the layer by one
    public void pushLayer()
    {
        this.neuronLayer += 1;
    }

=======
>>>>>>> parent of a9da4e3... Genome v8.1
=======
>>>>>>> parent of a9da4e3... Genome v8.1
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