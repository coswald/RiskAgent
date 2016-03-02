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

/**
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A {@code Neuron} that uses a step
 * function as its activation function. This step is determined by
 * a {@link com.riskybusiness.neural.StepNeuron#threshold} and can be
 * set by the programmer.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.neural.Neuron
 */
public class StepNeuron extends Neuron
{
	
	private static final long serialVersionUID = 607098269654808303L;
	
    /**
     * <p>The value that will determine whether or not the
     * {@code StepNeuron} will fire. It will only fire if the sum
     * found is greater than or equal to the threshold.</p>
     */
	protected float threshold;
	
    /**
     * <p>Constructs a {@code StepNeuron} with the given threshold
     * to use in the activation function, as well as the amount of
     * inputs for the {@code Neuron}.</p>
     * @param threshold The number to determine whether to fire or not.
     * @param inputNum The amount of inputs to this
     *              {@code StepNeuron}.
     * @see com.riskybusiness.neural.Neuron#Neuron(int)
     */
	public StepNeuron(float threshold, int inputNum)
	{
		super(inputNum);
		this.setThreshold(threshold);
	}

    /**
     * <p>Constructs a copy of this {@code StepNeuron}.</p>
     * @param neuron {@inheritDoc}
     * @see com.riskybusiness.neural.Neuron#Neuron(Neuron)
     */
	public StepNeuron(StepNeuron neuron)
	{
		super(neuron);
		this.threshold = neuron.threshold;
	}
		
    /**
     * <p>Constructs a {@code StepNeuron} with the given amount
     * of inputs for the {@code SigmoidNeuron}. The threshold
     * is set to a half.</p>
     * @param inputNum The amount of inputs to this
     *              {@code step Neuron}.
     * @see com.riskybusiness.neural.StepNeuron#StepNeuron(float, int)
     */
    public StepNeuron(int inputNum)
    {
        this(.5F, inputNum);
    }
    
    /**
     * <p>Changes the threshold of this {@code StepNeuron} to the
     * given value.</p>
     * @param threshold The new threshold.
     */
	public void setThreshold(float threshold)
	{
		this.threshold = threshold;
	}
	
    /**
     * <p>Will fire if the summation variable is greater than the
     * threshold, or it won't.
     * @param summation {@inheritDoc}
     * @return 1 if the sum was greater or equal to the threshold,
     *              0 otherwise.
     */
	@Override
	public float activate(float summation)
	{
		return (summation < threshold) ? 0F : 1F;
	}
}
