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
import java.lang.Object;

public class Synapse extends Object implements Serializable
{
	
	private static final long serialVersionUID = 3646014859697821258L;
	
	private int neuronIndex;
	private Neuron sender;
	private Neuron receiver;
	
	public Synapse(int neuronIndex, Neuron sender, Neuron receiver)
	{
		this.neuronIndex = neuronIndex;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public void feedForward(float... inputs) throws ExceededNeuronInputException, InvalidNeuronInputException
	{
		receiver.addToInput(neuronIndex, sender.fire(inputs));
	}
	
	public void feedForward() throws ExceededNeuronInputException, InvalidNeuronInputException, NeuronCannotFireException
	{
		if(!sender.canFire())
			throw new NeuronCannotFireException(sender.toString() + " cannot fire!");
		receiver.addToInput(neuronIndex, sender.fire());
	}
	
	@Override
	public String toString()
	{
		return "A Synapse between a " + sender.toString() + " and a " + receiver.toString();
	}
}
