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
import java.lang.Math;

/**
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A {@code Neuron} that uses the
 * sigmoid function as its activation function.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.neural.Neuron
 */
public class SigmoidNeuron extends Neuron
{
	
	private static final long serialVersionUID = 6203772663984140533L;
	
	private float summationDivisor;
	
    /**
     * <p>Constructs a {@code SigmoidNeuron} with the given
     * summation divisor to use in the activation function, as
     * well as the amount of inputs for the {@code Neuron}.</p>
     * @param summationDivisor The number to divide the sum by
     *              as it is sent into the activation function.
     * @param inputNum The amount of inputs to this
     *              {@code SigmoidNeuron}.
     * @see com.riskybusiness.neural.Neuron#Neuron(int)
     */
	public SigmoidNeuron(float summationDivisor, int inputNum)
	{
		super(inputNum);
		this.setSummationDivisor(summationDivisor);
	}
	
    /**
     * <p>Constructs a {@code SigmoidNeuron} with the given
     * amount of inputs for the {@code SigmoidNeuron}. The
     * summation divisor is set to 1.</p>
     * @param inputNum The amount of inputs to this
     *              {@code SigmoidNeuron}.
     * @see com.riskybusiness.neural.SigmoidNeuron#SigmoidNeuron(float, int)
     */
	public SigmoidNeuron(int inputNum)
	{
		this(1.0F, inputNum);
	}
	
    /**
     * <p>Constructs a copy of the given {@code SigmoidNeuron}.</p>
     * @param neuron {@inheritDoc}
     * @see com.riskybusiness.neural.Neuron#Neuron(Neuron)
     */
	public SigmoidNeuron(SigmoidNeuron neuron)
	{
		super(neuron);
		this.summationDivisor = neuron.summationDivisor;
	}
	
    /**
     * <p>Changes the number that is used to divide the sum by.</p>
     * @param summationDivisor The number to divide the sum by
     *              as it is sent into the activation function.
     */
	public void setSummationDivisor(float summationDivisor)
	{
		this.summationDivisor = summationDivisor;
	}
	
    /**
     * <p>Describes the sigmoid activation function. More can
     * be seen at
     *  <a href="https://en.wikipedia.org/wiki/Sigmoid_function">Wikipedia</a></p>
     * @param summation {@inheritDoc}
     * @return A float between 0 and 1 found on the sigmoid
     *              curve.
     */
	@Override
	public float activate(float summation)
	{
		return 1.0F / (1F + (float)Math.exp(-summation / summationDivisor));
	}
	
	/**
	 * <p>Describes the derivative of the sigmoid activation
	 * function. This can be described in terms of the sigmoid
	 * function; in this case, <b>s(x)</b>. The derivative of
	 * <b>s(x)</b> (<b>s'(x)</b>) is <b>s(x)(1 - s(x))</b>.</p>
	 * @param num {@inheritDoc}
	 * @return {@inheritDoc}
	 * @see com.riskybusiness.neural.SigmoidNeuron#activate(float)
	 */
	@Override
	public float activateDerivative(float num)
	{
		return this.activate(num) * (1 - this.activate(num));
	}
}
