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

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis class represents a {@code Neuron}
 * as it appears inside of a genetic algorithm context. A
 * {@code NeuronGene} consists of the metadata used to form many
 * different kinds of Neurons. This will allow another class to take
 * advantage of this metadata in order to form an actual
 * {@code Neuron}.</p>
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.neural.Neuron
 */
public class NeuronGene implements Serializable
{
    //Represents the ID to print out a innovation database
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
    private double   neuronActivationResponse;
    //Represents the bias weight for the neuron
    private double  biasWeight;

    //Create a blank neuron gene
    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates a "blank" {@code NeuronGene}.
     * This "blank" version consists of an invalid neuron id (-1), is a
     * {@code SigmoidNeuron}, in the hidden layer (with the layer id
     * equal to 1), and no activation. This neuron will never fire.</p>
     */
    public NeuronGene()
    {
        neuronID = -1;
        neuronType = "SIGMOID";
        neuronLayerType ="Hidden";
        neuronLayer = 1;
        neuronActivationResponse = 0.0;
    }

    
    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates a {@ocde NeuronGene}. This
     * gene has the correct metadata sent to it from the parameters
     * given.</p>
     * @param id The id of this neuron, used for sorting and innovation
     *              information.
     * @param type The type of neuron, either "SIGMOID" or "STEP".
     * @param layterType The type of layer the neuron resides, either
     *              "Hidden", "Input", or "Output".
     * @param activate The activation responce for this neuron.
     * @param layer The layer this neuron resides in, as a number.
     *              For example, 1 would indicate the input layer,
     *              2 would indicate the first hidden layer, 3 would
     *              indicate the second, and maybe 4 would incidate
     *              the output layer.
     */
    public NeuronGene(int id, String type, String layerType, double activate, int layer)
    {
        neuronID = id;
        neuronType = type;
        neuronLayerType = layerType;
        neuronLayer = layer;
        neuronActivationResponse = activate;
    }
    
    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the ID number for this
     * {@code NeuronGene}.
     * @return The ID for this neuron.
     */
    public int getID()
    {
        return this.neuronID;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the ID number for this
     * {@code NeuronGene}.
     * @param id The new ID number for this neuron.
     */
    public void setID(int id)
    {
        neuronID = id;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the type of id for this
     * {@code NeuronGene}.
     * @return The string value of this neuron, either "SIGMOID"
     *              or "STEP".
     * @see com.riskybusiness.genetic.NeuronGene#NeuronGene(int, String, String, double, int)
     */
    public String getNeuronType()
    {

        return this.neuronType;
    }

    /**
     * p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the type of neuron.
     * @param type The new type of neuron, either "SIGMOID",
     *              or "STEP".
     */
    public void setNeuronType(String type)
    {
        this.neuronType = type;
    }    
    
    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the actvation
     * response for this neuron.</p>
     * @return The activation response.
     */
    public double getActivationResponse()
    {
        return this.neuronActivationResponse;
    }
    
    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspSets the activation
     * response for this neuron.</p>
     * @param activate The new activation response.
     */
    public void setActivationResponse(double activate)
    {
        this.neuronActivationResponse = activate;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the layer
     * for this neuron.</p>
     * @return The layer of this neuron.
     */
    public int getNeuronLayer()
    {
        return this.neuronLayer;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspSet the layer
     * of this neuron</p>
     * @params layer The new layer for this neuron.
     */
    public void setNeuronLayer(int layer)
    {
        this.neuronLayer = layer;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the
     * bias weight of this neuron.</p>
     * @return The current bias wieght.</p>
     */
    public double getBiasWeight()
    {
        return this.biasWeight;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the bias
     * weight for this neuron.</p>
     * @param weight The new weight.
     */
    public void setBiasWeight(double weight)
    {
        this.biasWeight = weight;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the layer type.</p>
     * @return The laer type of this neuron.
     */
    public String getLayerType()
    {
        return this.neuronLayerType;
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the layer type.
     * This is either "Input", "Hidden", or "Output".</p>
     * @param layerType The new layer type.
     */
    public void setLayerType(String layerType)
    {
        neuronLayerType = layerType;
    }
    
    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the neuron's layer
     * by incrementing it by one.</p>
     */
    public void pushLayer()
    {
        this.neuronLayer += 1;
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
        toReturn += "This neuron is a " + neuronType + " neuron. The neuron exists in the " + neuronLayerType + " layer.\n";

        toReturn += "The neuron has an activation response of " + neuronActivationResponse + " and is in layer " + neuronLayer + "\n";

        //Return the string
        return toReturn;
    }
}
