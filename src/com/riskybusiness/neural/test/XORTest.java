package com.riskybusiness.neural.test;

import com.riskybusiness.neural.NeuralNet;
import com.riskybusiness.neural.Trainer;

import java.lang.Exception;
import static java.lang.Math.random;
import java.lang.Object;

public class XORTest extends Object
{
	public static void main(String... args) throws Exception
	{
		NeuralNet xor = new NeuralNet(2, 1, 2);
		Trainer[] trainers = new Trainer[2000];
		float in1, in2;
		for(int i = 0 ; i < trainers.length; i++)
		{
			in1 = (Math.random() < .5) ? 0F : 1F;
			in2 = (Math.random() < .5) ? 0F : 1F;
			trainers[i] = new Trainer(((int)in1) ^ ((int)in2), in1, in2);
		}
		
		boolean trained = false;
		while(!trained)
		{
			for(int i = 0; i < trainers.length; i++)
			{
				xor.train(new float[] {trainers[i].getAnswer()}, new float[][] {new float[] {trainers[i].getInputs()[0]}, new float[] {trainers[i].getInputs()[1]}});
				//Thread.sleep(1000);
				//System.out.println("Training: " + i);
				xor.clearNetwork();
			}
			
			float got = 0F;
			int amountWrong = 0;
			outer:
			for(float i = 0; i < 2; i++)
			{
				for(float j = 0; j < 2; j++)
				{
					got = xor.fire(new float[][] {new float[] {i}, new float[] {j}})[0];
					xor.clearNetwork();
					if(xor.fire(new float[][] {new float[] {i}, new float[] {j}})[0] != ((float)(((int)i) ^ ((int)j))))
						amountWrong++;
					xor.clearNetwork();
					System.out.println("I: " + i + "; J: " + j + "; Got: " + got);
				}
			}
			if(amountWrong == 0)
				trained = true;
		}
		
		System.out.println("The network is trained!");
	}
}
