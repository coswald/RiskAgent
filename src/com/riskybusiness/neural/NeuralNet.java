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

package com.riskybusiness.neural;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Object;

public class NeuralNet extends Object implements Serializable
{
	
	private static final long serialVersionUID = 7382374626520742474L;
	
	protected Synapse[] synapses;
	protected Neuron[] neurons;
	
	public NeuralNet(Neuron[] neurons, Synapse[] synapses)
	{
		this.neurons = neurons;
		this.synapses = synapses;
	}
	
	public NeuralNet(int inputLayerRows, int outputLayerRows, int... hiddenLayerRows)
	{
		int sum = inputLayerRows + outputLayerRows;
		int[] connections = new int[1 + hiddenLayerRows.length];
		connections[0] = inputLayerRows;
		int synapseSum = inputLayerRows * hiddenLayerRows[0];
		
		for(int i = 0; i < hiddenLayerRows.length - 1; i++)
		{
			sum += hiddenLayerRows[i];
			connections[i + 1] = hiddenLayerRows[i];
			synapseSum += hiddenLayerRows[i] * hiddenLayerRows[i + 1];
		}
		
		sum += hiddenLayerRows[hiddenLayerRows.length - 1];
		connections[connections.length - 1] = hiddenLayerRows[hiddenLayerRows.length - 1];
		synapseSum += hiddenLayerRows[hiddenLayerRows.length - 1] * outputLayerRows;
		
		this.neurons = new Neuron[sum];
		this.synapses = new Synapse[synapseSum];
		
		sum = inputLayerRows - 1;
		for(int i = 0, j = -1; i < neurons.length; i++)
		{
			neurons[i] = new SigmoidNeuron((j == -1) ? 1 : connections[j]);
			if(i >= sum)
				sum += (++j < hiddenLayerRows.length) ? hiddenLayerRows[j] : outputLayerRows;
		}
		
		int levelIndex = 0;
		synapseSum = inputLayerRows - 1;
		for(int i = 0, j = 0, k = inputLayerRows, l = -1; i < synapses.length; i++, j++)
		{
			synapses[i] = new Synapse(j - levelIndex, neurons[j], neurons[k]);
			if(j >= synapseSum)
			{
				if(k + 1 >= (synapseSum + ((l + 1 < hiddenLayerRows.length) ? hiddenLayerRows[l + 1] : outputLayerRows)))
				{
					levelIndex = synapseSum + 1;
					synapseSum += (++l < hiddenLayerRows.length) ? hiddenLayerRows[l] : outputLayerRows;
				}
				j = levelIndex;
				k++;
			} 
		}
	}
	
	public Neuron[] getNeurons()
	{
		return this.neurons;
	}
	
	public Synapse[] getSynapses()
	{
		return this.synapses;
	}
	
	public float[] fire(float[][] inputs) throws InvalidNeuronInputException
	{
		if(inputs.length > neurons.length)
			throw new InvalidNeuronInputException("The amount of inputs given are greater than the amount of neurons!");
		
		//Go through the inputs and fire them. If suddenly a Neuron can fire, that means all
		//of the Synapses for the input layer have been exhausted, meaning the amount of
		//inputs exceeds that of the amount given in the input layer.
		for(int i = 0; i < inputs.length; i++)
		{
			if(this.neurons[i].canFire())
			{
				for(Neuron n : this.neurons)
					n.clearInputs();
				throw new InvalidNeuronInputException("The amount of inputs given are greater than that of the input layer!");
			}
			else
				this.synapses[i].feedForward(inputs[i]);
		}
		
		//Fires the rest of the synapses.
		for(int i = inputs.length; i < this.synapses.length; i++)
			this.synapses[i].feedForward();
		
		//Finds the first output neuron and saves it's position
		//in the size variable.
		float[] output;
		int size = 0;
		for(int i = 0; i < this.neurons.length; i++)
		{
			if(this.neurons[i].canFire())
			{
				size = i;
				break;
			}
		}
		
		//Computes how many output neurons there are and
		//then fires them all, saving those values to 
		//the output variable.
		output = new float[this.neurons.length - size + 1];
		for(int i = size; i < this.neurons.length; i++)
			output[i - size] = this.neurons[i].fire();
		
		return output;
	}
	
	public void train(float[] desired, float[][] inputs)
	{
		float[] error = this.fire(inputs);
	}
}
