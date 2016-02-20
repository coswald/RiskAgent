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

import com.riskybusiness.neural.InvalidNeuronInputException;
import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.SigmoidNeuron;
import com.riskybusiness.neural.Synapse;

import java.io.Serializable;
import java.lang.Object;

/**
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A {@code NeuralNet} is a set of
 * {@code Neuron}s interconnected by {@code Synapse}s to compute a
 * set of outputs. This is done by feeding the {@code NeuralNet} a
 * set of inputs into a set of {@code Neuron}s, and calling the
 * interconnected {@code Synapse}s
 * {@link com.riskybusiness.neural.Synapse#feedForward()} methods.
 * Once all of the {@code Synapse}s have fed the outputs forward,
 * the output layer can be caluculated. Unlike many other
 * implementations of a neural network, {@code NeuralNet} has no
 * way of knowing which {@code Neuron} is part of the input, hidden
 * or output layer. The only way a {@code NeuralNet} has to
 * determine this when firing is to determine if a {@code Neuron}
 * can fire or not as well as how many {@code Synapse}s have
 * already fired. More can be seen in
 * {@link com.riskybusiness.neural.NeuralNet#fire(float[][])}.
 * Also note that this network allows the input layer to have mutliple
 * inputs to them.</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;As of version 1.0, there is no
 * way of training the network. RiskyBusiness hopes to do this via
 * backpropagation, but this may prove difficult given our current
 * implementation of the layers. Also, the network as a whole has no
 * way of making a reccurent {@code Neuron}; that is to say, there is
 * no way of having a {@code Neuron} who is connected to itself.
 * Future implementations may send in a parameter to the {@code Neuron}
 * class that will tell its {@code Neuron}s fire method to not return
 * false if it is not full but has one space left that happens to be
 * itself. The fire method will have to be dealt with as well, as well
 * as the {@code Synapse} feedForward method.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.neural.Neuron
 * @see com.riskybusiness.neural.Synapse
 */
public class NeuralNet extends Object implements Serializable
{
	
	private static final long serialVersionUID = 7382374626520742474L;
	
    /**
     * <p>Describes all of the {@code Synapse}s in the network.</p>
     */
	protected Synapse[] synapses;
	
    /**
     * <p>Describes all of the {@code Neuron}s in the network.</p>
     */
    protected Neuron[] neurons;
	
    /**
     * <p>Makes a {@code NeuralNet} whose {@code Neuron}s and
     * {@code Synapse}s have already been previously made. Make
     * sure that the neuron list is sorted by layer prevolence.
     * This is described as the input layer first (in the order
     * of relevant inputs), then the first hidden layer, then
     * the third...nth layer, and finally the output layer. It
     * is imperative that this order be met in order for the
     * methods of this class to work properly.</p>
     * @param neurons The {@code Neuron}s that make up this
     *              network.
     * @param synapses The {@code Synapse}s that make up this
     *              network.
     */
	public NeuralNet(Neuron[] neurons, Synapse[] synapses)
	{
		this.neurons = neurons;
		this.synapses = synapses;
	}
	
    /**
     * <p>Constructs a {@code NeuralNet} with the given amount of
     * {@code Neuron}s for the input layer, output layer, and
     * consequent hiddenLayers. All of these layers are perfectly
     * interconnected. This means that each input {@code Neuron}
     * sends its output to exactly every hidden {@code Neuron} in the
     * next layer, and so on for every layer. The default
     * {@code Neuron} that is used is the {@code SigmoidNeuron}.</p>
     * @param inputLayerRows The amount of {@code Neuron}s in the
     *              input layer.
     * @param outputLayerRows The amount of {@code Neuron}s in the
     *              output layer.
     * @param hiddenLayerRows Each index corresponds to another hidden
     *              layer, with the amount of {@code Neuron}s in the
     *              hidden layer corresponding to the value at that
     *              index.
     */
	public NeuralNet(int inputLayerRows, int outputLayerRows, int... hiddenLayerRows)
	{
        //Just go through and see what happens. I have a hard time explaining what all this does.
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
		int synapseIndex = 0;
		synapseSum = inputLayerRows - 1;
		boolean setLevel = false;
		for(int i = 0, j = 0, k = inputLayerRows, l = -1; i < synapses.length; i++, j++, synapseIndex++)
		{
			if(setLevel)
			{
				j = levelIndex;
				synapseIndex = 0;
				k++;
				setLevel = false;
			}
			//System.out.println("I: " + i + " : " + synapseIndex + " : " + j + " : " + k);
			synapses[i] = new Synapse(synapseIndex, neurons[j], neurons[k]);
			
			if(j >= synapseSum)
			{
				if(k + 1 > (synapseSum + ((l + 1 < hiddenLayerRows.length) ? hiddenLayerRows[l + 1] : outputLayerRows)))
				{
					levelIndex = synapseSum + 1;
					synapseSum += (++l < hiddenLayerRows.length) ? hiddenLayerRows[l] : outputLayerRows;
				}
				setLevel = true;
			}
		}
	}
	
    /**
     * <p>Returns the {@code Neuron}s that make up this
     * network.</p>
     * @return The {@code Neuron}s in this network.
     */
	public Neuron[] getNeurons()
	{
		return this.neurons;
	}
	
    /**
     * <p>Returns the {@code Synapse}s that make up this
     * network.</p>
     * @return The {@code Synapse}s in this network.
     */
	public Synapse[] getSynapses()
	{
		return this.synapses;
	}
	
    /**
     * <p>Populates the {@code NeuralNet} with the inputs and
     * calculates their corresponding output.</p>
     * @param inputs The inputs to the {@code NeuralNet}. These
     *              values are determined in that each list
     *              accessed in from the inputs list corresponds
     *              to an input {@code Neuron} in the
     *              {@code NeuralNet}.
     * @return The results from the output layer, in order.
     * @throws InvalidNeuronInputException When the amount of
     *              inputs given is greater than that of the
     *              amount of {@code Neuron}s in the
     *              {@code NeuralNet}, or when the amount of
     *              inputs given is greater than that of the
     *              input layer.
     * @see com.riskybusiness.neural.Neuron#fire()
     * @see com.riskybusiness.neural.Synapse#feedForward()
     */
	public float[] fire(float[][] inputs) throws InvalidNeuronInputException
	{
		if(inputs.length > neurons.length)
			throw new InvalidNeuronInputException("The amount of inputs given are greater than the amount of neurons!");
		
		//Go through the inputs and fire them. If suddenly a Neuron can fire, that means all
		//of the Synapses for the input layer have been exhausted, meaning the amount of
		//inputs exceeds that of the amount given in the input layer.
		int synapseIndex = 0;
		for(int i = 0; !(this.synapses[synapseIndex].getSender().canFire()); i++, synapseIndex++)
		{
			if(this.neurons[i].canFire())
			{
				for(Neuron n : this.neurons)
					n.clearInputs();
				throw new InvalidNeuronInputException("The amount of inputs given are greater than that of the input layer!");
			}
			else
			{
				this.synapses[synapseIndex].feedForward(inputs[i]);
				
				if(i == inputs.length - 1)
					i = -1;
			}
		}
		
		//Fires the rest of the synapses.
		for(int i = synapseIndex; i < this.synapses.length; i++)
			this.synapses[i].feedForward();
		
		//Finds the first output neuron and saves its position
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
	
    /**
     * <p>Trains the {@code NeuralNet} in the traditional
     * sense, using backpropagation.</p>
     * @param desired The desired outputs from the 
     *              {@code NeuralNet}.
     * @param inputs The inputs to send to the
     *              {@code NeuralNet}.
     * @see com.riskybusiness.neural.NeuralNet#fire(float[][])
     */
	public void train(float[] desired, float[][] inputs)
	{
		float[] error = this.fire(inputs); //I have no idea what to do from here.
	}
}
