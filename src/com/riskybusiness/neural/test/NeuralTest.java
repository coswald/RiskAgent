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
import com.riskybusiness.util.Debug;

import java.lang.Object;

public class NeuralTest extends Object
{
	public static void main(String[] args)
	{
		Debug.DEBUGGING = true;
		//Debug.SHOWMESSAGE = false;
		NeuralNet t = new NeuralNet(2, 1, 2);
		float[][] inputs = new float[2][1];
		inputs[0][0] = 1F;
		inputs[1][0] = 1F;
		float[] out = t.fire(inputs);
		Debug.println("The network T has 3 layers, with 2 in input, 1 in output, and 2 in the hidden layer.");
		Debug.println("The output for 1, 1 is " + out[0] + ".\n", 1);
		
		NeuralNet u = new NeuralNet(3, 2, 2);
		inputs = new float[3][1];
		inputs[0][0] = 1F;
		inputs[1][0] = 1F;
		inputs[2][0] = 1F;
		out = u.fire(inputs);
		Debug.println("The network U has 3 layers, with 3 in input, 2 in output, and 2 in the hidden layer"); //, and 4 in the second hidden layer.");
		Debug.println("The output for all ones are " + out[0] + " : " + out[1] + ".\n", 1);
		
		NeuralNet s = new NeuralNet(10, 3, 15, 4);
		inputs = new float[10][1];
		inputs[0][0] = 1F;
		inputs[1][0] = 1F;
		inputs[2][0] = 1F;
		inputs[3][0] = 1F;
		inputs[4][0] = 1F;
		inputs[5][0] = 1F;
		inputs[6][0] = 1F;
		inputs[7][0] = 1F;
		inputs[8][0] = 1F;
		inputs[9][0] = 1F;
		out = s.fire(inputs);
		Debug.println("The network S has 4 layers, with 10 in input, 3 in output, 15 in the first hidden layer and 4 in the second hidden layer.");
		Debug.println("The output for all ones are " + out[0] + " : " + out[1] + " : " + out[2], 1);
		
		Debug.giveClose();
	}
}
