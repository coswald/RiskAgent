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
import com.riskybusiness.neural.Trainer;

import java.lang.Exception;
import static java.lang.Math.random;
import java.lang.Object;
import java.util.Scanner;


public class XORTest extends Object
{
	public static void main(String... args) throws Exception
	{
		NeuralNet xor = new NeuralNet(2, 1, 2);
		Trainer[] trainers = new Trainer[4000];
		
		float in1, in2;
		for(int i = 0 ; i < trainers.length; i++)
		{
			in1 = (Math.random() < .5) ? 0F : 1F;
			in2 = (Math.random() < .5) ? 0F : 1F;
			trainers[i] = new Trainer(((int)in1) ^ ((int)in2), in1, in2);
		}
		
		int round = 0;
		boolean trained = false;
		while(!trained)
		{
			System.out.println("\rRound: " + ++round);
			if(round % 1000 == 0)
				xor = new NeuralNet(2, 1, 2);
			//System.out.println(xor);
			for(int i = 0; i < trainers.length; i++)
			{
				xor.train(new float[] {trainers[i].getAnswer()}, new float[][] {new float[] {trainers[i].getInputs()[0]}, new float[] {trainers[i].getInputs()[1]}});
				//Thread.sleep(1000);
				//System.out.println("Training: " + i);
			}
			
			float got = 0F;
			int amountWrong = 0;
			for(float i = 0; i < 2; i++)
			{
				for(float j = 0; j < 2; j++)
				{
					got = xor.fire(new float[][] {new float[] {i}, new float[] {j}})[0];
					if((float)(Math.round(got * 100.0F) / 100.0F) != ((float)(((int)i) ^ ((int)j))))
						amountWrong++;
					System.out.printf("I: " + i + "; J: " + j + "; Got: %.2f%n", (Math.round(got * 100.0F) / 100.0F));
				}
			}
			System.out.println("Amount Wrong: " + amountWrong);
			System.out.println();
			if(amountWrong == 0)
				trained = true;
		}
		
		System.out.println("\nThe network is trained to the XOR function!");
		System.out.println("Ready to test it?");
		System.out.println("Enter a negative number to exit; otherwise, follow the instructions!\n");
		Scanner z = new Scanner(System.in);
		
		int a = 0, b = 0;
		while(a >= 0 && b >= 0)
		{
			System.out.print("Give me a valid integer, either 0 and 1: ");
			a = z.nextInt();
			if(a >= 0)
				b = z.nextInt();
			
			if(a >= 0 && b >= 0)
				System.out.println("The network says: " + xor.fire(new float[][] {new float[] {a}, new float[] {b}})[0]);
			else
				System.out.println("Have a great day!");
			xor.clearNetwork();
		}
	}
}
