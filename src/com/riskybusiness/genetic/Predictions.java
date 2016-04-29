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

package com.riskybusiness.genetic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.lang.InterruptedException;
import java.lang.Object;

import com.riskybusiness.genetic.Epoch;
import com.riskybusiness.genetic.Genome;

public final class Predictions
{
	private Epoch e;
	private String fileName;
	private int inputNum;
	private int outputNum;
	
	public Predictions(Epoch e, String fileName, int inputNum, int outputNum)
	{
		this.e = e;
		this.fileName = fileName;
		this.inputNum = inputNum;
		this.outputNum = outputNum;
	}
	
	public void runPredictions()
	{
		Genome theChosenOne = e.getTheChosenOne();
		//Create the phenotype
        theChosenOne.createPhenotype();

        // This will reference one line at a time
        String line = null;

        //Represents the inputs on the line of the file
        String[] fileLine; // = new String[inputNum + outputNum];

        //Represents the input to the network
        float[][] inputs = new float[inputNum][1];

        //Represents the expected output from the network
        float expectedOutput[] = new float[outputNum];

        //Initialize the genome fitness to 0
        double variance = 0;
        double deviation = 0;
        double totalDeviation = 0;
		
		int lines = 0;
        try
		{
			System.out.println("Loading " + fileName);
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Loop through each line of the file, get the inputs, fire them and then evaluate the results
            while((line = bufferedReader.readLine()) != null)
			{
				lines++;
                fileLine = line.split("\\t");
                //Loop through each input on the line and add it to the input array or expected output
                for (int x = 0; x < fileLine.length; x++)
                {
                    //If we are at the last input then store it into the expected results
                    if (x >= inputNum)
                        expectedOutput[x - inputNum] = Float.parseFloat(fileLine[x]);
                    else
                        inputs[x] = new float[]{Float.parseFloat(fileLine[x])};
				}
                //Calculate the variance and add it to the fitness
                double result = theChosenOne.getNetwork().fire(inputs)[0] * 365;
                //System.out.println(result);
                //Thread.sleep(100);
                //if(result < 10.0)
                //{ 
                	//result = 0.0;
                //}
				
				//This will change with multiple expectedOutputs.
                deviation = Math.abs(result - expectedOutput[0]);
                totalDeviation += deviation;
                variance += Math.pow(deviation, 2);
            }   

            System.out.println("Average Deviation of the best fit individual: " + totalDeviation / lines);
            System.out.println("Total Variance of the best fit individual: " + variance);

            // Always close files.
            bufferedReader.close();         
        }
		catch(RuntimeException re)
		{
			System.err.println("Error: File not correct format!");
		}
        catch(FileNotFoundException ex)
		{
            System.err.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex)
		{
            System.err.println("Error reading file '" + fileName + "'");                  
        }
    }
}
