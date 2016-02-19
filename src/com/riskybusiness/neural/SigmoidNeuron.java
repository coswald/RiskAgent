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

import java.lang.Math;

public class SigmoidNeuron extends Neuron
{
	
	private static final long serialVersionUID = 6203772663984140533L;
	
	private float summationDivisor;
	
	public SigmoidNeuron(float summationDivisor, int inputNum)
	{
		super(inputNum);
		this.setSummationDivisor(summationDivisor);
	}
	
	public SigmoidNeuron(int inputNum)
	{
		this(1.0F, inputNum);
	}
	
	public void setSummationDivisor(float summationDivisor)
	{
		this.summationDivisor = summationDivisor;
	}
	
	@Override
	public float activate(float summation)
	{
		return 1.0F / (1F + (float)Math.exp(-summation / summationDivisor));
	}
}
