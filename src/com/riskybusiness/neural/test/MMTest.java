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

package com.riskybusiness.neural.test;

import com.riskybusiness.neural.NeuralNet;
import com.riskybusiness.neural.Neuron;
import com.riskybusiness.util.Debug;

import java.lang.Object;
import java.lang.String;

public class MMTest extends Object
{
	public static void main(String... args)
	{
		Debug.DEBUGGING = true;
		Debug.giveClose();
		
		NeuralNet test = new NeuralNet(2, 2, 2);
		Neuron[] neurons = test.getNeurons();
		float[][] newWeights = new float[][] {{1F, 1F}, {1F, 1F}, {.15F, .2F, 1F}, {.25F, .3F, 1F}, {.4F, .45F, 1F}, {.5F, .55F, 1F}};
		for(int i = 0; i < newWeights.length; i++)
		{
			neurons[i].setWeights(newWeights[i]);
			neurons[i].setLearningRate(.5F);
		}
		test = new NeuralNet(neurons, test.getSynapses());
		
		float[][] inputs = new float[][] {{.05F}, {.10F}};
		float[] desired = new float[] {.01F, .99F};
		
		float[] fired;
		int round = 0;
		while(true)
		{
			fired = test.fire(inputs);
			if(round >= 1 || fired[0] == desired[0] && fired[1] == desired[1])
				break;
			Debug.println("Round " + ++round);
			Debug.println("Guess: " + fired[0] + ", " + fired[1], 1);
			test.train(desired, inputs);
			
		}
	}
}