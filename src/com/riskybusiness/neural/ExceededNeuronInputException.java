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

import java.lang.RuntimeException;

/**
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;A basic {@code Exception} that tells us that
 * the amount of inputs sent to a {@code Neuron} was greater than the
 * amount it could hold.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see java.lang.Exception
 */
public class ExceededNeuronInputException extends RuntimeException
{
	private static final long serialVersionUID = 6796323911306049985L;
	
	/**
	 * <p>Creates an {@code ExceededNeuronInputException} that
	 * is like the {@link java.lang.Exception#Exception()}.</p>
	 * @see java.lang.Exception#Exception()
	 */
	public ExceededNeuronInputException()
	{
		super();
	}
	
	/**
	 * <p>Constructs a new {@code ExceededNeuronInputException}
	 * with the specified detail message.</p>
	 * @param message The detailed message about the exception.
	 * @see java.lang.Exception#Exception(String)
	 */ 
	public ExceededNeuronInputException(String message)
	{
		super(message);
	}
}
