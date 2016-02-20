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
import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.NeuronCannotFireException;

import java.io.Serializable;
import java.lang.Object;

/**
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Describes a connection between two
 * different {@link com.riskybusiness.neural.Neuron Neurons}. This
 * connection is not weighted; it is up for the {@code Neuron} to decide
 * the weights for every input. The {@code Synapse} does keep track of
 * the receiving and sending {@code Neuron}s, and the {@code Synapse}s
 * method should be called every time a network is to feed a value from
 * one layer to the next.</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Once a sending and receiving
 * {@code Neuron} is decided, the index of which the sending
 * {@code Neuron} must be determined. This value makes sure that the 
 * weight value given to an input on the {@code Neuron} is consistent as
 * well as easy to understand. The {@code Synapse} will
 * {@link com.riskybusiness.neural.Synapse#feedForward()} method will
 * call the receivers
 * {@link com.riskybusiness.neural.Neuron#addToInput(int, float)} method
 * using the index given.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.neural.Neuron
 */
public class Synapse extends Object implements Serializable
{
	
	private static final long serialVersionUID = 3646014859697821258L;
	
    /**
     * <p>The index for the input of the sender {@code Neuron} to the
     * receiver {@code Neuron}.</p>
     * @see com.riskybusiness.neural.Synapse#sender
     * @see com.riskybusiness.neural.Synapse#receiver
     */
	private int neuronIndex;
	
    /**
     * <p>The {@code Neuron} that sends the information.</p>
     */
    private Neuron sender;
	
    /**
     * <p>The {@code Neuron} that recieves the information.</p>
     */
    private Neuron receiver;
	
    /**
     * <p>Constructs a {@code Synapse} with the given input index
     * and the sender and receiver.</p>
     * @param neuronIndex The index of the input to send from the
     *              sender to the receiver.
     * @param sender The sending {@code Neuron}.
     * @param receiver The receiving {@code Neuron}.
     * @see com.riskybusiness.neural.Neuron
     */
	public Synapse(int neuronIndex, Neuron sender, Neuron receiver)
	{
		this.neuronIndex = neuronIndex;
		this.sender = sender;
		this.receiver = receiver;
	}
	
    /**
     * <p>This method should be called when the sending
     * {@code Neuron} has no synapses to it, and send the inputs
     * to the sending {@code Neuron} to fire.</p>
     * @param inputs The inputs to send to the sending
     *              {@code Neuron}.
     * @throws ExceededNeuronInputException See the "See also"
     *              section.
     * @throws InvalidNeuronInputException See the "See also"
     *              section.
     * @see com.riskybusiness.neural.Neuron#addToInput(int, float)
     */
	public void feedForward(float... inputs) throws ExceededNeuronInputException, InvalidNeuronInputException
	{
		receiver.addToInput(neuronIndex, sender.fire(inputs));
	}
	
    /**
     * <p>Feeds the output from the sending {@code Neuron} to
     * the input of the receiving {@code Neuron}.</p>
     * @throws ExceededNeuronInputException See the "See also"
     *              section.
     * @throws InvalidNeuronInputException See the "See also"
     *              section.
     * @throws NeuronCannotFireException When the {@code Neuron}
     *              cannot fire, as in it doesn't have
     *              enough inputs to fire yet.
     * @see com.riskybusiness.neural.Neuron#addToInput(int, float)
     */
	public void feedForward() throws ExceededNeuronInputException, InvalidNeuronInputException, NeuronCannotFireException
	{
		if(!sender.canFire())
			throw new NeuronCannotFireException(sender.toString() + " cannot fire!");
		receiver.addToInput(neuronIndex, sender.fire());
        //sender.clearInputs();
	}
	
	/**
     * <p>Returns the neuron index of the receiving
	 * {@code Neuron}.</p>
     * @return The neuron index.
     */
	public int getNeuronIndex()
	{
		return this.neuronIndex;
	}
	
    /**
     * <p>Returns the sending {@code Neuron}.</p>
     * @return The sending {@code Neuron}.
     */
	public Neuron getSender()
	{
		return this.sender;
	}
    
    /**
     * <p>Returns the receiving {@code Neuron}.</p>
     * @return The receiving {@code Neuron}.
     */
	public Neuron getReciever()
	{
		return this.receiver;
	}
	
    /**
     * <p>Returns a {@code String} representation of this
     * {@code Synapse}. This is done by returning the class
     * name followed by the neurons that this {@code Synapse}
     * holds.</p>
     * @return A string representaiton of this {@code Synapse}
     */
	@Override
	public String toString()
	{
		return "A " + this.getClass().getSimpleName() + " between a " + sender.toString() + " and a " + receiver.toString();
	}
}
