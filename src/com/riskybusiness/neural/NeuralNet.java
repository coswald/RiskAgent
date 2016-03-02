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
import com.riskybusiness.neural.InvalidNeuronOutputException;
import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.SigmoidNeuron;
import com.riskybusiness.neural.StepNeuron;
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
     * {@code Neuron} that is used is the {@code SigmoidNeuron}.
     * The exception to this rule is the output layer, which is made
     * up of {@code Stepneuron}s.</p>
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
		
		//Add to the number of neurons inside the neural network
		int sum = inputLayerRows + outputLayerRows;
		//An array that represent the number of inputs for the given layer + 1
		//I.E. connections[0] gives the number of inputs for a neuron in layer (0 + 1) or simply layer 1
		int[] connections = new int[1 + hiddenLayerRows.length];
		//Add to the connections array 
		connections[0] = inputLayerRows;
		//Calculate the number of synapses between the input layer and the first hidden layer
		int synapseSum = inputLayerRows * hiddenLayerRows[0];
		
		//Loops through the hidden layers if there is more than 1 hidden layer
		for(int i = 0; i < hiddenLayerRows.length - 1; i++)
		{
			//Add the number of neurons in the current hidden layer to the sum
			sum += hiddenLayerRows[i];
			//Add to the connections array
			connections[i + 1] = hiddenLayerRows[i];
			//Calculate the number of synapses between the current hidden layer and the next and add it to the sum
			synapseSum += hiddenLayerRows[i] * hiddenLayerRows[i + 1];
		}
		
		//Calculate the number of neurons in the last hidden layer and add it to the sum 
		sum += hiddenLayerRows[hiddenLayerRows.length - 1];
		//Add to the connections array
		connections[connections.length - 1] = hiddenLayerRows[hiddenLayerRows.length - 1];
		//Calculate the number of synapses between the last hidden layer and the output layer and add it to the sum
		synapseSum += hiddenLayerRows[hiddenLayerRows.length - 1] * outputLayerRows;
		
		//Instantiate the arrays for the nuerons and synapses using the values calculated above
		this.neurons = new Neuron[sum];
		this.synapses = new Synapse[synapseSum];
		
		//
		sum = inputLayerRows - 1;
		//Loop through the neurons and create neurons until there are no more neurons
		for(int i = 0, j = -1; i < neurons.length; i++)
		{
			//If the current neuron isn't in the output layer create a sigmoid neuron
			//else create a step neuron 
			if(neurons.length - i <= outputLayerRows)
				neurons[i] = new StepNeuron(connections[j]);
			else
				//If there is no hidden layer then the number of connections is 1
				neurons[i] = new SigmoidNeuron((j == -1) ? 1 : connections[j]);
			//If i is greater than the sum then we have exceeded the number of neurons in the active layer
			//and need to move on to the next layer. 
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
     * <p>Clears the networks {@code Neuron}s of input
     * values.</p>
     * @see com.riskybusiness.neural.Neuron#clearInputs()
     */
	public void clearNetwork()
	{
		for(Neuron n : this.neurons)
			n.clearInputs();
	}
	
    /**
     * <p>Populates the {@code NeuralNet} with the inputs and
     * calculates their corresponding output. This will leave
     * the {@code NeuralNet} with the {@code Neuron}s filled
     * with the input values that they would received while
     * firing.</p>
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
		//System.out.println("Inputs: " + inputs[0][0] + " " + inputs [1][0]);
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
			if(!this.neurons[i].hasFired())
			{
				size = i;
				break;
			}
		}
		
		//Computes how many output neurons there are and
		//then fires them all, saving those values to 
		//the output variable.
		output = new float[this.neurons.length - size];
		for(int i = size; i < this.neurons.length; i++)
			output[i - size] = this.neurons[i].fire();
	
		return output;
	}
	
    /**
     * <p>Trains the {@code NeuralNet} in the traditional
     * sense, using backpropagation. This method requires
     * that the type of activation function used by each
     * {@code Neuron} is continuous and differentiable.
     * This means that a {@code StepNeuron} can only be
     * used for the output layer if you are using this
     * method.</p>
     * @param desired The desired outputs from the 
     *              {@code NeuralNet}.
     * @param inputs The inputs to send to the
     *              {@code NeuralNet}.
     * @throws InvalidNeuronOutputException If the given size of outputs
     * 	            is not equal to that of the neural network's output
     *              layer.
     * @see com.riskybusiness.neural.NeuralNet#fire(float[][])
     */
	public void train(float[] desired, float[][] inputs) throws InvalidNeuronOutputException
	{
		while(true)
		{
			//http://www.nnwj.de/backpropagation.html
			float[] prediction = this.fire(inputs); //I have no idea what to do from here.
			if(prediction.length != desired.length)
				throw new InvalidNeuronOutputException("The amount of outputs given is not equal to that of the outputs in the output layer!");
			float[] error = new float[prediction.length];
			for(int i = 0; i < error.length; i++)
			{
				error[i] = desired[i] - prediction[i];
				//System.out.println("Desired: " + desired[i] + "; Predicted: " + prediction[i] + "; Error: " + error[i]);
			}
			
			//NOTE: Not sure if correct. I need a way to compute the total error, but I don't think that this is correct. -C
			//Website listed above gives record of ways to compute total error with one output neuron. I know this value,
			//but I don't know for multiple outputs. Wes, I'd appreciate a way to compute all of the output layers values.
			float totalError = 0;
			for(int i = 0; i < error.length; i++)
				totalError += error[i];
			if(totalError == 0)
				break;
			//End confusion.
			
			float adjustment;
			for(int i = this.synapses.length - 1; i >= 0; i--)
			{
				adjustment = totalError * this.synapses[i].getSender().fire() * this.synapses[i].getReciever().fire() * (1 - this.synapses[i].getReciever().fire());
				this.synapses[i].getReciever().adjustWeight(this.synapses[i].getNeuronIndex(), adjustment);
			}
			
			//for Bias Weight adjustment
			for(int i = this.neurons.length - 1; i >= 0; i--)
			{
				adjustment = totalError * 1 * this.neurons[i].fire() * (1 - this.neurons[i].fire());
				this.neurons[i].adjustWeight(this.neurons[i].getWeights().length - 1, adjustment);
			}
			this.clearNetwork();
		}
	}
}
