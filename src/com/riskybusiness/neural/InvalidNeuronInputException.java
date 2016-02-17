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

import java.lang.ArrayIndexOutOfBoundsException;

/**
 * <p>&nbsp&nbsp&nbsp&nbspA type of {@code Exception} that states that
 * a given {@code Neuron} cannot take the given type of input. This
 * could also mean that the {@code Neuron} cannot receive the amount
 * of inputs given or that the input has already been filled.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @see com.riskybusiness.neural.Neuron
 * @see java.lang.ArrayIndexOutOfBoundsException
 * @since 1.6
 */
public class InvalidNeuronInputException extends ArrayIndexOutOfBoundsException
{
	private static final long serialVersionUID = 7703107280930494895L;
	
	/**
	 * <p>Creates a {@code InvalidNeuronInputException} with
	 * no detail message.</p>
	 * @see java.lang.ArrayIndexOutOfBoundsException#ArrayIndexOutOfBoundsException()
	 */
	public InvalidNeuronInputException()
	{
		super();
	}
	
	/**
	 * <p>Constructs a {@code InvalidNeuronInputException}
	 * with the given detail message.</p>
	 * @param message The detailed message about the {@code Exception}.
	 * @see java.lang.ArrayIndexOutOfBoundsException#ArrayIndexOutOfBoundsException(String)
	 */
	public InvalidNeuronInputException(String message)
	{
		super(message);
	}
}
