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

import com.riskybusiness.neural.ExceededNeuronInputException;
import com.riskybusiness.neural.InvalidNeuronInputException;

import java.io.Serializable;
import java.lang.Comparable;
import java.lang.Float;
import java.lang.Math;
import java.lang.Object;

//http://natureofcode.com/book/chapter-10-neural-networks/
//*TO-DO Implement momentum: http://stats.stackexchange.com/questions/70101/neural-networks-weight-change-momentum-and-weight-decay

/**
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;This class represents a single artificial
 * cell in a network of said cells that recieves input, processes
 * those inputs, and generates an output. This model was described by
 * Warren S. McCulloch and Walter Pitts in 1943, and is now prevalent
 * in the form of a Perceptron. It is this model that is implemented
 * here, meant for the purpose of playing a RISK game.</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;However, this class could be used for other
 * purposes. The implementation for training is called
 * backpropogation, and is used by many neural networks. This is not
 * standard to NEAT, the algorithm implemented to play RISK. This
 * implementation was left in as a tribute to allow other types of
 * non-NEAT neural networks to play RISK as well. In NEAT, however,
 * a {@code Neuron} is described by a {@code NeuronGene}, and is
 * formed in the genome by the {@code NeuronGene}.</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;Of course, a perceptron is described by
 * certain characteristics that allows it to be a perceptron. These
 * characteristics include weights for each synapse comming into the
 * {@code Neuron}. This is described in the
 * {@link com.riskybusiness.neural.Neuron#weights} object. For
 * traditional training, the {@code Neuron} implements a
 * {@link com.riskybusiness.neural.Neuron#learningRate} float that
 * will allow the {@code Neuron} to be trained at different speeds.
 * This class is abstract because there are many different
 * activation functions possible for the {@code Neuron}. This
 * functionality was left out for future classes to implement.</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;Each {@code Neuron} allows for two methods
 * of input and output. The first allows for the {@code Neuron} to 
 * send input statically; that is to say, input is sent into the
 * {@code Neuron} all at once. This is implemented in the method
 * {@link com.riskybusiness.neural.Neuron#fire(float[])}. The second
 * allows for sending input dynamically. The input is saved to the
 * {@code Neuron} until the {@code Neuron} can fire. These two
 * methods allow the {@code Neuron} to act as a stand alone entity
 * as well as a functional part of a neural network.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @see java.io.Serializable
 * @see java.lang.Comparable
 * @since 1.6
 */
public abstract class Neuron extends Object implements Serializable, Comparable<Neuron>
{
	
	private static final long serialVersionUID = 37641900214726419L;
	
	/**
	 * <p>The inputs that are saved for the {@code Neuron} until
	 * it can fire. All inputs in this implementation must be
     * positive.</p>
	 */
	protected float[] inputs;
	
	/**
	 * <p>The weights for each input of the {@code Neuron}.
	 * These weights line up according to the order in which
	 * the inputs were received.</p>
	 */
	protected float[] weights;
	
	/**
 	 * <p>The rate at which the weights are adjusted while
	 * in the training phase.</p>
	 */
	protected float learningRate;
	
	/**
	 * <p>Describes the last total error of the {@code Neuron}.
	 * This can be used in training and can be helpful for
	 * accessing, but is not directly used in any of the methods
	 * described in this class except it's accessor and mutator.</p>
	 */
	private float lastTotalError = 0F;
	
	/**
	 * <p>Determines whether this {@code Neron} has fired
	 * since it's inputs have been filled.</p>
	 */
	private boolean haveFired = false;
	
	/**
	 * <p>Constructs a {@code Neuron} which uses the
	 * learning rate and weights given as starting points.</p>
	 * @param learningRate The learning rate to adjust the
	 * 				weights by.
	 * @param weights The initial weights for each input.
	 */
	public Neuron(float learningRate, float... weights)
	{
		this.weights = weights;
		this.inputs = new float[weights.length - 1];
		this.clearInputs();
		this.learningRate = learningRate;
	}
	
	/**
	 * <p>Constructs a {@code Neuron} which uses the
	 * weights given and a learning rate of .1.</p>
	 * @param weights The initial weights for each input.
	 * @see com.riskybusiness.neural.Neuron#Neuron(float[])
	 */
	public Neuron(float... weights)
	{
		this(0.1F, weights);
	}
	
	/**
	 * <p>Constructs a {@code Neuron} which uses the
	 * learning rate given and starts the {@code Neuron}
	 * with the given amount of inputs, not including
	 * the bias weights. The weights will start off with
	 * a random value between 0 and 1 per input.</p>
	 * @param learningRate The learning rate to adjust the
	 * 				weights by.
	 * @param inputNum The amount of inputs this neuron
	 *				will recieve.
	 */
	public Neuron(float learningRate, int inputNum)
	{
		this.learningRate = learningRate;
		this.weights = new float[inputNum + 1];
		this.inputs = new float[inputNum];
		this.clearInputs();
		Neuron.fillList(true, this.weights);
	}
	
	/**
	 * <p>Makes a {@code Neuron} with the given number
	 * of inputs and a learning rate of 0.1. Also
	 * randomizes weights; see link below.</p>
	 * @param inputNum The amount of inputs this neuron
	 *				will recieve.
	 * @see com.riskybusiness.neural.Neuron#Neuron(float, int)
	 */
	public Neuron(int inputNum)
	{
		this(0.1F, inputNum);
	}
	
	/**
	 * <p>Makes a copy of the given {@code Neuron}. This
	 * does not use the clone method, nor does this class
	 * implement {@link java.lang.Cloneable}, but this is
	 * just a copy constructor.</p>
	 * @param neuron The {@code Neuron} to copy.
	 */
	public Neuron(Neuron neuron)
	{
		this.inputs = neuron.inputs;
		this.weights = neuron.weights;
		this.learningRate = neuron.learningRate;
		this.haveFired = neuron.haveFired;
	}
	
	/**
	 * <p>Fills a list with values, either 1, or a
	 * random float between -0.5 and 0.5.</p>
	 * @param random Determines which values to fill
	 *				the list with: either random
	 *				values, or not.
	 * @param list The list to fill.
	 */
	private static void fillList(boolean random, float[] list)
	{
		for(int i = 0; i < list.length; i++)
		{
			if(random)
				list[i] = (float)Math.random() - .5F;
			else
				list[i] = 1;
		}
	}
	
	/**
	 * <p>Clears the inputs that are stored in this
	 * {@code Neuron}. This is accomplished by
	 * making all of the values in the
	 * {@link com.riskybusiness.neural.Neuron#inputs}
	 * list the lowest value that can be stored
	 * in a {@code Float}. This is the negative
	 * version of {@link java.lang.Float#MAX_VALUE}.</p>
	 * @see com.riskybusiness.neural.Neuron#inputs
	 */
	public void clearInputs()
	{
		for(int i = 0; i < this.inputs.length; i++)
			this.inputs[i] = -Float.MAX_VALUE;
		haveFired = false;
	}
	
	/**
	 * <p>Determines whether or not this {@code Neuron}
	 * has the necessary inputs to make an accurate
	 * calculation. In other words, this method looks
	 * and determines whether or not the
	 * {@link com.riskybusiness.neural.Neuron#inputs}
	 * list is full.</p>
	 * @return {@code true} if the input list is full,
	 *				{@code false} otherwise.
	 * @see com.riskybusiness.neural.Neuron#inputs
	 */
	public boolean canFire()
	{
		for(float x : this.inputs)
			if(x == -Float.MAX_VALUE)
				return false;
		return true;
	}
	
	/**
	 * <p>Adds a given output (presumably from another
	 * {@code Neuron}) to the
	 * {@link com.riskybusiness.neural.Neuron#inputs}
	 * list.</p>
	 * @param index The spot to add the output to.
	 * @param output The float to add to the input list.
     * @throws ExceededNeuronInputException When the amount
     *              of inputs has already been met.
     * @throws InvalidNeuronInputException When the index
     *              has already been populated.
	 * @see com.riskybusiness.neural.Neuron#inputs
	 */
	public void addToInput(int index, float output) throws ExceededNeuronInputException, InvalidNeuronInputException
	{
		if(this.canFire())
			throw new ExceededNeuronInputException("The amount of given inputs is already at its maximum!");
		
		if(this.inputs[index] != -Float.MAX_VALUE)
			throw new InvalidNeuronInputException("The input " + index + " on neuron " + this.toString() + " cannot be overriden!");
		
		this.inputs[index] = output;
	}
	
	/**
	 * <p>Returns the input given at the index provided</p>
	 * @param index The index to retrieve the input from.
	 * @return The input value for this {@code Neuron} at
	 * 				the specified index.
	 * @throws InvalidNeuronInputException When the index
	 * 				is greater than the size of the array
	 * 				holding the inputs or less than zero.
	 * @see com.riskybusiness.neural.Neuron#inputs
	 */
	public float getInputAt(int index) throws InvalidNeuronInputException
	{
		if(index >= (this.inputs.length + 1) || index < 0)
			throw new InvalidNeuronInputException("Cannot access an invalid index!");
		return (index >= this.inputs.length) ? 1 : this.inputs[index];
	}
	
	/**
	 * <p>Compared the output of this {@code Neuron}
	 * to that of another. What is returned is the
	 * difference between their outputs. If the
	 * output of this {@code Neuron} was greater,
	 * this method will return a positive number,
	 * while the inverse is true if the output is
	 * lower. NOTE: this only compares their
	 * outputs, nothing about the amount of inputs
	 * or weight values are checked.</p>
	 * @param other The other object to compare to.
	 * @return A positive number if the output
	 * 		of this {@code Neuron} was greater, 0
	 * 		if they were equal, and -1 if the other
	 * 		output was greater.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	 @Override
	public int compareTo(Neuron other)
	{
		float[] sample = new float[this.weights.length - 1];
		float[] sampleTwo = new float[other.weights.length - 1];
		Neuron.fillList(false, sample);
		Neuron.fillList(false, sampleTwo);
		
		return Float.floatToIntBits(this.fire(sample)) - Float.floatToIntBits(other.fire(sampleTwo));
	}
	
	/**
	 * <p>Sums the list of inputs given.</p>
	 * @param inputs The list of inputs to be summed.
	 * @return The sum of the inputs.
	 * @throws InvalidNeuronInputException When the
	 *				amount of inputs given is not
	 * 				equal to the amount of weights
	 *				minus one (for the bias weight),
	 *				this {@code Exception} is thrown.
	 * @see com.riskybusiness.neural.InvalidNeuronInputException
	 */
	protected float sum(float... inputs) throws InvalidNeuronInputException
	{
	    if(inputs.length + 1 != weights.length)
			throw new InvalidNeuronInputException("The amount of inputs is not the same as weights.");
			
		float sum = 0F;
		for(int i = 0; i < inputs.length; i++)
			sum += inputs[i] * weights[i];
		sum += 1F * weights[weights.length - 1]; //bias
		return sum;
	}
	
	/**
	 * <p>Describes how the {@code Neuron} should
	 * activate. This could be a step function, or
	 * a sigmoid function, or any other function
	 * conceivably effective.</p>
	 * @param summation The sum of the inputs of
	 * 				the {@code Neuron}, which tells
	 *				the {@code Neuron} whether to
	 * 				fire or not.
	 * @return A float that can be anything one
	 * 				could imagine, as long as the
	 * 				{@code Neuron} can make sense
	 * 				of it in the end.
	 */
	public abstract float activate(float summation);
	
	/**
	 * <p>Describes the derivative of the 
	 * {@link com.riskybusiness.neural.Neuron#activate(float)}
	 * function. This is used and can be used for training
	 * a network or a {@code Neuron}.</p>
	 * @param num The number to feed into the derivative
	 * 				of the activation function.
	 * @return The result of the derivative of the activation
	 * 				function.
	 */
	 public abstract float activateDerivative(float num);
	
	/**
	 * <p>Actually fires a value based on the
	 * inputs. This is done by calling the classes
	 * {@link com.riskybusiness.neural.Neuron#sum(float[])}
	 * method on the inputs given, and determining
	 * whether or not to fire based on this classes
	 * {@link com.riskybusiness.neural.Neuron#activate(float)}
	 * method. This method will also save the inputs
	 * given to the {@code Neuron} in the parameter
	 * to the stored input list.</p>
	 * @param inputs The inputs that determine whether or
	 * 				not to fire.
	 * @return A float value found by the activation function
	 * 				on the inputs given.
	 * @see com.riskybusiness.neural.Neuron#sum(float[])
	 * @see com.riskybusiness.neural.Neuron#activate(float)
	 */
	public float fire(float... inputs)
	{
		if(inputs.length == this.inputs.length)
		{
			//this.inputs = inputs;
			haveFired = true;
		}
		return this.activate(this.sum(inputs));
	}
	
	/**
	 * <p>Actually fires a value based on the
	 * inputs saved in the {@code Neuron}. This
	 * is accomplished in the same fashion as if
	 * the inputs were sent in staticlly.</p>
	 * @return A float value found by the activation function
	 * 				on the inputs that were already dynamically
	 *				entered.
	 * @see com.riskybusiness.neural.Neuron#fire(float[])
	 */
	public float fire()
	{
		return this.fire(this.inputs);
	}
	
	/**
	 * <p>Returns the weights associated with
	 * this {@code Neuron}.</p>
	 * @return The list containing all of the
	 * 				weights.
	 * @see com.riskybusiness.neural.Neuron#weights
	 */
	public float[] getWeights()
	{
		return this.weights;
	}
	
	/**
	 * <p>Returns whether or not the {@code Neuron}
	 * has already fired with the inputs currently
	 * stored within it. This value is set to true
	 * when the
	 * {@link com.riskybusiness.neural.Neuron#fire()}
	 * methods are called, and is set to false when the
	 * {@link com.riskybusiness.neural.Neuron#clearInputs()}
	 * function is called. This also only accounts
	 * for when both of these methods terminate
	 * successfully.</p>
	 * @return true if the {@code Neuron} has fired
	 * 			with the stored weights
	 *			currently stored, false
	 * 			otherwise.
	 */
	public boolean hasFired()
	{
		return this.haveFired;
	}
	
	/**
	 * <p>Sets the weights associated with
	 * this {@code Neuron}.</p>
	 * @param weights The list containing all of the
	 * 				weights.
	 * @see com.riskybusiness.neural.Neuron#weights
	 * @throws InvalidNeuronInputException If the
	 * 			weight length is not the same as the
	 * 			original weight length.
	 */
	public void setWeights(float... weights) throws InvalidNeuronInputException
	{
		if(weights.length != this.weights.length)
		{
			throw new InvalidNeuronInputException("The amount of weights given is invalid! Make sure to include the bias weight!");
		}
		this.weights = weights;
	}
	
	/**
	 * <p>Sets the weight given for the given index
	 * supplied.</p>
	 * @param weightIndex The position for the weight.
	 * @param weight The new weight for that index.
	 * @see com.riskybusiness.neural.Neuron#weights
	 * @throws InvalidNeuronInputException If the index
	 * 			is greater than or equal to
	 * 			the length of the weight array.
	 * @since COFED 1.3
	 */
	public void setWeight(int weightIndex, float weight) throws InvalidNeuronInputException
	{
		if(weightIndex >= this.weights.length || weightIndex < 0)
		{
			throw new InvalidNeuronInputException("The amount of weights given is invalid! Make sure to include the bias weight!");
		}
		this.weights[weightIndex] = weight;
	}
	
	/**
	 * <p>Adjusts the weights given the learning
	 * rate and weights of this {@code Neuron} as
	 * well as the adjustments needed for every
	 * weight. This error can be calculated as
	 * <br>
	 * <b>ERROR = DESIRED - NEURONOUTPUT</b>
	 * <br>
	 * and is multiplied by the inputs that gave
	 * the <b>NEURONOUTPUT</b>. This is done in
	 * the {com.riskybusiness.neural.Neuron#train(float, float[])}
	 * method, consiquentally calling this method
	 * after doing this calculation.</p>
	 * @param adjustments The list of adjustments
	 *				to be made, where every
	 *				position on the list has a
	 * 				corresponding value in the
	 * 				weights list.
	 * @throws InvalidNeuronInputException If the amount of
	 *				adjustments to be made is
	 * 				not equal to the amount of
	 *				weights found in this
	 * 				{@code Neuron}, including
	 *				the bias weight.
	 * @see com.riskybusiness.neural.Neuron#train(float, float[])
	 */
	public void adjustWeights(float... adjustments) throws InvalidNeuronInputException
	{
		if(adjustments.length != weights.length)
			throw new InvalidNeuronInputException("The amount of inputs is not the same as weights.");
		
		for(int i = 0; i < adjustments.length; i++)
			this.adjustWeight(i, adjustments[i]);
	}
	
	/**
	 * <p>Sets the last known error as described 
	 * by the individual programmer. This is not
	 * used directly by any calculation, but can
	 * be defined to help in later implementations.
	 * See {@link com.riskybusiness.neural.Neuron#lastTotalError}
	 * for more details.</p>
	 * @param lastKnownError The last known error.
	 */
	public void setLastTotalError(float lastTotalError)
	{
		this.lastTotalError = lastTotalError;
	}
	
	/**
	 * <p>Gets the last known error as described 
	 * by the individual programmer. This is not
	 * used directly by any calculation, but can
	 * be defined to help in later implementations.
	 * See {@link com.riskybusiness.neural.Neuron#lastTotalError}
	 * for more details.</p>
	 * @return The last known total error.
	 */
	public float getLastTotalError()
	{
		return this.lastTotalError;
	}
	
	/**
	 * <p>Adjust the weight given the learning
	 * rate and the weight index of this
	 * {@code Neuron}.
	 * @param index The weight index to adjust
	 * @param adjustment The adjustment to add
	 *				onto the weight.
	 * @see com.riskybusiness.neural.Neuron#adjustWeights(float[])
	 * @throws InvlalidNeuronInputException If the index is greater
	 * 				than the length of weights given.
	 */
	public void adjustWeight(int index, float adjustment) throws InvalidNeuronInputException
	{
		if(index < 0 || index >= this.weights.length)
			throw new InvalidNeuronInputException("The index given is greater than the amount of weights!");
		
		this.weights[index] += (adjustment * this.learningRate);
	}
	
	/**
	 * <p>Adjusts all of the weights to random
	 * values.</p>
	 */
	public void randomizeWeights()
	{
		Neuron.fillList(true, this.weights);
	}
	
	/**
	 * <p>Sets the learning rate for this
	 * {@code Neuron}.</p>
	 * @param learningRate The new learning
	 * 				rate.
	 */
	public void setLearningRate(float learningRate)
	{
		this.learningRate = learningRate;
	}
	
	/**
	 * <p>Gets the learning rate for this
	 * {@code Neuron}.</p>
	 * @return The learning rate.
	 */
	public float getLearningRate()
	{
		return this.learningRate;
	}
	
	/**
	 * <p>Trains the {@code Neuron} in the
	 * traditional sense. See
	 * {@link com.riskybusiness.neural.Neuron#adjustWeights(float[])}
	 * for more details.
	 * @param desired The desired output for
	 * 				the {@code Neuron}.
     * @param inputs The inputs to the
     *              {@code Neuron}.
	 * @throws com.riskybusiness.neural.InvalidNeuronInputException See
     *              {@link com.riskybusiness.neural.Neuron#adjustWeights(float[])}
	 */
	public void train(float desired, float... inputs) throws InvalidNeuronInputException
	{
		float error = desired - this.fire(inputs);
		
		float[] adjustments = new float[inputs.length + 1];
		for(int i = 0; i < inputs.length; i++)
			adjustments[i] = inputs[i] * error;
		adjustments[adjustments.length - 1] = 1F * error;
		
		this.adjustWeights(adjustments);
	}
	
	/**
	 * <p>Trains the {@code Neuron} in the
	 * traditional sense using the dynamic
	 * method of input.</p>
	 * @param desired The desired output for
	 * 				the {@code Neuron}.
	 * @see com.riskybusiness.neural.Neuron#train(float, float[])
	 */
	public void train(float desired)
	{
		this.train(desired, this.inputs);
	}
	
	/**
	 * <p>Returns a string that represents
	 * this class. This tells us the learning
	 * rate of the class, the class name, and
	 * the amount of weights this {@code Neuron}
	 * has.</p>
	 * @return A string representation of 
	 * 				this class.
	 */
	@Override
	public String toString()
	{
		String weightString = "";
		for(float w : weights)
			weightString += w + ", ";
		weightString = weightString.substring(0, weightString.length() - 2); 
		return this.getClass().getSimpleName() + " with a learning rate of " + this.learningRate + " and " + this.weights.length + " weights containing " + weightString + " as values.";
	}
}
