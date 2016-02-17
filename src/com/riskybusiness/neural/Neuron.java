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

import java.io.Serializable;
import java.lang.Comparable;
import java.lang.Math;
import java.lang.Object;

//http://natureofcode.com/book/chapter-10-neural-networks/
//*TO-DO Implement momentum: http://stats.stackexchange.com/questions/70101/neural-networks-weight-change-momentum-and-weight-decay

/**
 * <p>&nbsp&nbsp&nbsp&nbspThis class represents a single artificial
 * cell in a network of said cells that recieves input, processes
 * those inputs, and generates an output. This model was described by
 * Warren S. McCulloch and Walter Pitts in 1943, and is now prevalent
 * in the form of a Perceptron. It is this model that is implemented
 * here, meant for the purpose of playing a RISK game.</p>
 * <p>&nbsp&nbsp&nbsp&nbspHowever, this class could be used for other
 * purposes. The implementation for training is called
 * backpropogation, and is used by many neural networks. This is not
 * standard to NEAT, the algorithm implemented to play RISK. This
 * implementation was left in as a tribute to allow other types of
 * non-NEAT neural networks to play RISK as well. In NEAT, however,
 * a {@code Neuron} is described by a {@code NeuronGene}, and is
 * formed in the genome by the {@code NeuronGene}.</p>
 * <p>&nbsp&nbsp&nbsp&nbspOf course, a perceptron is described by
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
 * <p>&nbsp&nbsp&nbsp&nbspEach {@code Neuron} allows for two methods
 * of input and output. The first allows for the {@code Neuron} to 
 * send input statically; that is to say, input is sent into the
 * {@code Neuron} all at once. This is implemented in the method
 * {@link com.riskybusiness.neural.Neuron#fire()}. The second allows
 * for sending input dynamically. The input is saved to the
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
	 * it can fire.</p>
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
	 * <p>Constructs a {@code Neuron} which uses the
	 * learning rate and weights given as starting points.</p>
	 * @param learningRate The learning rate to adjust the
	 * 		       weights by.
	 * @param weights The initial weights for each input.
	 */
	public Neuron(float learningRate, float... weights)
	{
		this.weights = weights;
		this.inputs = new float[weights.length - 1];
		this.clearInputs();
		this.learningRate = learningRate;
	}
	
	public Neuron(float... weights)
	{
		this(0.1F, weights);
	}
	
	public Neuron(float learningRate, int inputNum)
	{
		this.learningRate = learningRate;
		this.weights = new float[inputNum + 1];
		this.inputs = new float[inputNum];
		this.clearInputs();
		Neuron.fillList(true, this.weights);
	}
	
	public Neuron(int inputNum)
	{
		this(0.1F, inputNum);
	}
	
	private static void fillList(boolean random, float[] list)
	{
		for(int i = 0; i < list.length; i++)
		{
			if(random)
				list[i] = (float)Math.random();
			else
				list[i] = 1;
		}
	}
	
	public void clearInputs()
	{
		for(int i = 0; i < this.inputs.length; i++)
			this.inputs[i] = -1;
	}
	
	public boolean canFire()
	{
		for(float x : this.inputs)
			if(x < 0)
				return false;
		return true;
	}
	
	public void addToInput(int index, float output) throws ExceededNeuronInputException, InvalidNeuronInputException
	{
		if(this.canFire())
			throw new ExceededNeuronInputException("The amount of given inputs is already at its maximum!");
		
		if(this.inputs[index] >= 0)
			throw new InvalidNeuronInputException("The input " + index + " on neuron " + this.toString() + "cannot be overriden!");
		
		this.inputs[index] = output;
	}
	
	public int compareTo(Neuron other)
	{
		float[] sample = new float[this.weights.length];
		float[] sampleTwo = new float[other.weights.length];
		Neuron.fillList(false, sample);
		Neuron.fillList(false, sampleTwo);
		
		return (int)Math.round(this.fire(sample) - other.fire(sampleTwo));
	}
	
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
	
	public abstract float activate(float summation);
	
	public float fire(float... inputs)
	{
		return this.activate(this.sum(inputs));
	}
	
	public float fire()
	{
		return this.fire(this.inputs);
	}
	
	public float[] getWeights()
	{
		return this.weights;
	}
	
	public void adjustWeights(float... adjustments) throws InvalidNeuronInputException
	{
		if(adjustments.length != weights.length)
			throw new InvalidNeuronInputException("The amount of inputs is not the same as weights.");
		
		for(int i = 0; i < adjustments.length; i++)
			this.weights[i] += (adjustments[i] * this.learningRate);
	}
	
	public void setLearningRate(float learningRate)
	{
		this.learningRate = learningRate;
	}
	
	public void train(float desired, float... inputs) throws InvalidNeuronInputException
	{
		float error = desired - this.fire(inputs);
		
		float[] adjustments = new float[inputs.length + 1];
		for(int i = 0; i < inputs.length; i++)
			adjustments[i] = inputs[i] * error;
		adjustments[adjustments.length - 1] = 1F * error;
		
		this.adjustWeights(adjustments);
	}
	
	public void train(float desired)
	{
		this.train(desired, this.inputs);
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " with a learning rate of " + this.learningRate + " and " + this.weights.length + " weights";
	}
}
