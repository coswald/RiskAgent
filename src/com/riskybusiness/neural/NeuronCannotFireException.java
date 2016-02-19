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
 * <p>&nbsp&nbsp&nbsp&nbspA basic {@code Exception} that tells us that
 * a {@code Neuron} could not fire. This could be due to programmer
 * restrictions, implementation issues, but most likely the fact that
 * the {@code Neuron} did not have enough inputs to fire.</p>
 * @author Coved W Oswald
 * @auhtor Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see java.lang.Exception
 */
public class NeuronCannotFireException extends RuntimeException
{
						     
	private static final long serialVersionUID = 8175957964354386068L;
	
	/**
	 * <p>Creates an {@code NeuronCannotFireException} that
	 * is like the {@link java.lang.Exception#Exception()}.</p>
	 * @see java.lang.Exception#Exception()
	 */
	public NeuronCannotFireException()
	{
		super();
	}
	
	/**
	 * <p>Constructs a new {@code NeuronCannotFireException}
	 * with the specified detail message.</p>
	 * @param message The detailed message about the exception.
	 * @see java.lang.Exception#Exception(String)
	 */ 
	public NeuronCannotFireException(String message)
	{
		super(message);
	}
}
