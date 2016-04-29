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

package com.riskybusiness.genetic.test;

//Used for loading genomes
import java.lang.InterruptedException;
import java.lang.Object;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.io.*;
import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;
import com.riskybusiness.neural.NeuralNet;
import com.riskybusiness.neural.Trainer;
import com.riskybusiness.genetic.Genome;
import com.riskybusiness.genetic.Innovation;
import com.riskybusiness.genetic.InnovationDB;
import com.riskybusiness.genetic.InnovationType;
import com.riskybusiness.genetic.LinkGene;
import com.riskybusiness.genetic.NeuronGene;
import com.riskybusiness.genetic.Species;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Predictions
{
	public static void main(String... arg) throws Exception
	{

		int genomeID = 0;
		Genome theChosenOne = new Genome();
		InnovationDB innovations = new InnovationDB();
		int speciesID = 0;
		double bestFitness = 0.0;

		//Initialize the readers
		ObjectInputStream populationReader = null;
		ObjectInputStream speciesReader = null;
		ObjectInputStream parametersReader = null;
		//Try reading the data from the files
		try
		{
			//Initialize the readers and their files as well
			System.out.println("\tAttempting to load the chosen one");
			parametersReader = new ObjectInputStream(new FileInputStream("parameters.txt"));

			genomeID 		= (int) parametersReader.readObject();
			theChosenOne 	= (Genome) parametersReader.readObject();
			innovations 	= (InnovationDB) parametersReader.readObject();
			speciesID 		= (int) parametersReader.readObject();
			bestFitness 	= (double) parametersReader.readObject();

			//Close the readers
			if(populationReader != null)
				populationReader.close();
			if(speciesReader != null)
				speciesReader.close();
			if(parametersReader != null)
				parametersReader.close();
		}
		catch(Exception e)
		{
			System.out.println("\tError!");
			e.printStackTrace();
			System.err.println(e.toString());
			System.exit(1);
		}
		finally
		{
			System.out.println("\tInput Successful!");
		}

		//Create the phenotype
        theChosenOne.createPhenotype();

        // The name of the file to open.
        String fileName = "NSDUHPredictions.txt";

        // This will reference one line at a time
        String line = null;

        //Represents the inputs on the line of the file
        String[] fileLine = new String[13 + 1];

        //Represents the input to the network
        float[][] inputs = new float[13][1];

        //Represents the expected output from the network
        float expectedOutput = 0.0f;

        //Initialize the genome fitness to 0
        double variance = 0;
        double deviation = 0;
        double totalDeviation = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Loop through each line of the file, get the inputs, fire them and then evaluate the results
            while((line = bufferedReader.readLine()) != null) {
                fileLine = line.split("\\t");
                //Loop through each input on the line and add it to the input array or expected output
                for (int x = 0; x < fileLine.length; x++)
                {
                    //If we are at the last input then store it into the expected results
                    if (x == 13)
                    {
                        expectedOutput = (float)Integer.parseInt(fileLine[x]);
                    }
                    else
                    {
                        inputs[x] = new float[]{(float)Integer.parseInt(fileLine[x])};
                    }
                }
                //Calculate the variance and add it to the fitness
                double result = theChosenOne.getNetwork().fire(inputs)[0] * 365;
                //System.out.println(result);
                //Thread.sleep(100);
                if(result < 10.0)
                { 
                	result = 0.0;
                }
                deviation = Math.abs(result - expectedOutput);
                totalDeviation += deviation;
                variance += Math.pow(deviation, 2);
            }   

            System.out.println(totalDeviation / 55275);
            System.out.println(variance);

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
        }
    }
}
