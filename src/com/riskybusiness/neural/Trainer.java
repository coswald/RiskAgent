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

import java.lang.Object;

public class Trainer extends Object
{
	private float[] inputs;
	private float desiredAnswer;
	
	public Trainer(float desiredAnswer, float... inputs)
	{
		this.inputs = new float[inputs.length];
		for(int i = 0; i < inputs.length; i++)
			this.inputs[i] = inputs[i];
		//this.inputs[inputs.length] = 1; //bias input //*Removed after bias support changed*//
		this.desiredAnswer = desiredAnswer;
	}
	
	public float[] getInputs()
	{
		return this.inputs;
	}
	
	public float getAnswer()
	{
		return this.desiredAnswer;
	}
}
