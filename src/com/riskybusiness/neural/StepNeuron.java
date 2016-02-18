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

public class StepNeuron extends Neuron
{
	
	private static final long serialVersionUID = 607098269654808303L;
	
	protected float threshold;
	
	public StepNeuron(float threshold, int inputNum)
	{
		super(inputNum);
		this.setThreshold(threshold);
	}
	
	public void setThreshold(float threshold)
	{
		this.threshold = threshold;
	}
	
	@Override
	public float activate(float summation)
	{
		return (summation < threshold) ? 0F : 1F;
	}
}
