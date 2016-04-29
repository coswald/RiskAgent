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
import com.riskybusiness.genetic.GenomeHelper;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Tester
{
	public static void main(String... arg) throws Exception
	{
		/* Tester Params */
		
		//Tester variable
		boolean printGenome 		= false;
		//Tester variable
		boolean testFire 			= false;
		//Tester variable
		boolean testAddNeuron 		= false;
		//Tester variable
		boolean testAddLink			= false;
		//Tester variable
		boolean testMutators 		= false;	


		/* Parameters */

		//Represents the size of the population
		int 	populationSize 			= 1;
		//Represents the number of input neurons
		int 	numInputNeurons			= 3;
		//Represents the number of output neurons
		int 	numOutputNeurons		= 3;
		//Represents the number of initial hidden layers
		int 	numHiddenLayers			= 3;
		//Represents the number of initial neurons in each hidden layer
		int[]	hiddenLayers 			= {2, 2, 2};
		//Represents the number of neurons in the genome up to the given index
		int[] 	summationNeuronsInLayer	= new int[numHiddenLayers + 3];
		//Represents the current neuron ID 
		int 	curNeuronID 			= 0;
		//Represents the current link ID
		int 	curLinkID 				= 0;
		//Represents the current genome ID
		int 	curGenomeID 			= 0;


		/* Helper Items */

		//Represents the item used to create pseudo-random numbers
		Random 	random 	= new Random();
		//Represents the scanner to get user input
		Scanner input 	= new Scanner(System.in);
		//Represents users input
		String 	userInput;


		/* Population Items */

		//Represents the array of created neurons
		ArrayList<NeuronGene> 	neuronGenes	= new ArrayList<NeuronGene>();
		//Represents the array of created links
   		ArrayList<LinkGene>   	linkGenes  	= new ArrayList<LinkGene>();
   		//Represents the genomes of the population
   		ArrayList<Genome>		population 	= new ArrayList<Genome>();
		//Represents the number of links created for the active neuron
		int 					linksCreated = 0;


   		/* Historical Data */	

   		//Represents the historical changes of all the previous populations
   		InnovationDB 			innovations	= new InnovationDB();
   		//Represents the current generation
   		int 					generation 	= 1;		

   		System.out.println("Hit [Enter] to start");
   		userInput = input.nextLine();

		//Initialize number of neurons
		summationNeuronsInLayer[0] = 0;
		summationNeuronsInLayer[1] = numInputNeurons;

		for (int i = 0; i < numHiddenLayers; i++)
		{
			summationNeuronsInLayer[i + 2] = summationNeuronsInLayer[i + 1] + hiddenLayers[i];
		}
		summationNeuronsInLayer[numHiddenLayers + 2] = numOutputNeurons + summationNeuronsInLayer[numHiddenLayers + 1];


		//Create the initial genomes
		for (int lcv = 0; lcv < populationSize; lcv++)
		{
			//Reset the genes for the new population
			neuronGenes.clear();
			linkGenes.clear();
			curNeuronID = 0;
			curLinkID = 0;

			//The next three loops will create the neurons
			//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
			for (int i = 0; i < numInputNeurons; i++)
			{
				double dweight = random.nextDouble();
				neuronGenes.add(new NeuronGene(++curNeuronID, "Sigmoid", "Input", dweight, 1)); 
			}
			for (int i = 0; i < numHiddenLayers; i++)
			{
				for (int j = summationNeuronsInLayer[i + 1]; j < summationNeuronsInLayer[i + 2]; j++)
				{
					double dweight = random.nextDouble();
					neuronGenes.add(new NeuronGene(++curNeuronID, "Sigmoid", "Hidden", dweight, (i + 2)));
				}
			}
			for (int i = 0; i < numOutputNeurons; i++)
			{
				double dweight = random.nextDouble();
				neuronGenes.add(new NeuronGene(++curNeuronID, "Sigmoid", "Output", dweight, (numHiddenLayers + 2)));
			}

			//Create the link genes
			//This behemoth of a triple nested loop inside a loop simply goes creates the links between all the neurons
			//The first loop increments the active layer
			for (int i = 0; i < numHiddenLayers + 1; i++)
			{
				//This loop goes through each neuron in the active layer
				for (int j = summationNeuronsInLayer[i]; j < summationNeuronsInLayer[(i + 1)]; j++)
				{
					linksCreated = 0;
					//This loop goes through each neuron in the layer that comes after the active layer
					for (int k = summationNeuronsInLayer[(i + 1)]; k < summationNeuronsInLayer[(i + 2)]; k++)
					{
						linksCreated++;
						//Create random weight
						double dweight = random.nextDouble();
						//Add the link to the link gene array
		 				linkGenes.add(new LinkGene(linkGenes.size() + 1, neuronGenes.get(j).getID(), neuronGenes.get(k).getID(), ++curLinkID, dweight, true));
					}
				}
			}

			population.add(new Genome(++curGenomeID, neuronGenes, linkGenes, numInputNeurons, numOutputNeurons));

		}

		if (printGenome)
		{
			for (int i = 0; i <  population.size(); i++)
			{
    			System.out.println(population.get(i));
			}
		}

		for (int i = 0; i <  population.size(); i++)
		{
			population.get(i).createPhenotype();
		}

		if (testFire)
		{
			System.out.println("Testing Fire");
			for (int i = 0; i <  population.size(); i++)
			{
				for(float x = 0; x < 2; x++)
				{
					for(float y = 0; y < 2; y++)
					{
						System.out.println(population.get(i).getNetwork().fire(new float[][] {new float[] {x}, new float[] {y}})[0]);
					}
				}
			}
		}

		if (testAddNeuron)
		{
			for (int i = 0; i < population.size(); i++)
			{
				for(float x = 0; x < 2; x++)
				{
					for(float y = 0; y < 2; y++)
					{
						System.out.println("Output 1: " + population.get(i).getNetwork().fire(new float[][] {new float[] {x}, new float[] {y}})[0]);
					}
				}
				System.out.println("Hit [Enter] to see the old genome");
				userInput = input.nextLine();
				System.out.println(population.get(i));
			}

			System.out.println("Attempting to add some neurons.");
			for (int i = 0; i < population.size(); i++)
			{
				for (int j = 0; j < 5; j++)
				{
					population.get(i).addNeuron(1.0, innovations, 10);
					population.get(i).createPhenotype();
				}
			}

			for (int i = 0; i <  population.size(); i++)
			{
				for(float x = 0; x < 2; x++)
				{
					for(float y = 0; y < 2; y++)
					{
						System.out.println("Output 2: " + population.get(i).getNetwork().fire(new float[][] {new float[] {x}, new float[] {y}})[0]);
					}
				}
				System.out.println("Hit [Enter] to see the new genome");
				userInput = input.nextLine();
				System.out.println(population.get(i));
			}
		}

		if (testAddLink)
		{
			for (int i = 0; i < population.size(); i++)
			{
				for(float x = 0; x < 2; x++)
				{
					for(float y = 0; y < 2; y++)
					{
						System.out.println("Output 1: " + population.get(i).getNetwork().fire(new float[][] {new float[] {x}, new float[] {y}})[0]);
					}
				}
				System.out.println("Hit [Enter] to see the old genome");
				userInput = input.nextLine();
				System.out.println(population.get(i));
			}
			for (int i = 0; i < 100; i++)
			{
				population.get(0).addLink(1.0, 0.0, innovations, 10, 10);
			}			
			for (int i = 0; i <  population.size(); i++)
			{
				for(float x = 0; x < 2; x++)
				{
					for(float y = 0; y < 2; y++)
					{
						System.out.println("Output 2: " + population.get(i).getNetwork().fire(new float[][] {new float[] {x}, new float[] {y}})[0]);
					}
				}
				System.out.println("Hit [Enter] to see the new genome");
				userInput = input.nextLine();
				System.out.println(population.get(i));
			}
		}

		if (testMutators)
		{
			System.out.println("Hit [Enter] to see the old genome");
			userInput = input.nextLine();
			System.out.println(population.get(0));
			for (int i = 0; i < population.size(); i++)
			{
				System.out.println("Testing mutateInputLink");
				population.get(i).mutateInputLink(1.0);
				System.out.println("Success");
				System.out.println("Testing mutateInputNeuron");
				population.get(i).mutateInputNeuron(1.0);
				System.out.println("Success");
				System.out.println("Testing mutateLinkWeights");
				population.get(i).mutateLinkWeights();
				System.out.println("Success");
				System.out.println("Testing mutateNeuronWeights");
				population.get(i).mutateNeuronWeights();
				System.out.println("Success");
				population.get(i).createPhenotype();
			}
			System.out.println("Hit [Enter] to see the new genome");
			userInput = input.nextLine();
			System.out.println(population.get(0));

		}

	}
}